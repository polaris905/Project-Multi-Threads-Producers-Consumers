package handler;

import argparser.Option;
import consumer.ComboConsumer;
import consumer.LatencyConsumer;
import consumer.ThroughputConsumer;
import pool.ThreadPool;
import producer.DoubleQueuesProducer;
import producer.SingleQueueProducer;
import splitter.FileSplitter;
import splitter.InfoGetter;
import timer.Timer;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Help to determine the way to handle multiple thread.
 */
public class ThreadHandler extends TaskHandler {

  private static final int CAPACITY = 50000;
  private ReaderType readerType;
  private ConsumerType consumerType = ConsumerType.SPECIAL_CONSUMER;

  public enum ReaderType {
    SPLIT_READER, MULTI_READER
  }

  public enum ConsumerType {
    SPECIAL_CONSUMER, COMBO_CONSUMER;
  }

  /**
   * Help to determine the number of producers and consumers based on user's input.
   *
   * @param options parsed args
   * @throws IOException when I/O exception occurs
   */
  @Override
  public void handleRequest(Map<String, Option> options) throws IOException {
    if (options.containsKey("--split-multireader")) {
      ThreadPool.setProducerThreads(
          Integer.parseInt(options.get("--split-multireader").getSubOptions().get(0)));
      readerType = ReaderType.SPLIT_READER;
    } else if (options.containsKey("--multireader")) {
      ThreadPool.setProducerThreads(
          Integer.parseInt(options.get("--multireader").getSubOptions().get(0)));
      readerType = ReaderType.MULTI_READER;
    }
    if (options.containsKey("--combo-consumer")) {
      consumerType = ConsumerType.COMBO_CONSUMER;
    }
    try {
      String postFile = options.get("--POST").getSubOptions().get(0);
      generateMap(THROUGHPUT_POST_MAP, LATENCY_POST_MAP, postFile);
      ThreadPool.getConsumerLatch().await();
      String getFile = options.get("--GET").getSubOptions().get(0);
      generateMap(THROUGHPUT_GET_MAP, LATENCY_GET_MAP, getFile);
      if (this.nextHandler != null) {
        this.nextHandler.handleRequest(options);
      }
    } catch (InterruptedException ex) {
      ex.printStackTrace();
    }
  }

  /**
   * The helper function that launchs several producer and consumer threads to bucket information
   * from the original CSV file.
   *
   * @param throughputMap data structure stores throughput buckets
   * @param latencyMap data structure stores latency buckets
   * @param fileName the path for target file.
   * @throws IOException if file does not exist or I/O exception occurs.
   */
  private void generateMap(ConcurrentSkipListMap throughputMap, ConcurrentSkipListMap latencyMap,
      String fileName) throws IOException {
    ThreadPool.latchReset();
    BlockingQueue<String> queue1 = new ArrayBlockingQueue<>(CAPACITY);
    BlockingQueue<String> queue2 = new ArrayBlockingQueue<>(CAPACITY);
    InfoGetter infoGetter = new InfoGetter(fileName, ThreadPool.getProducerThreads(),
        "(?<=\"),(?=\")", '\n');
    ThroughputConsumer.setStartTimestamp(infoGetter.getStartTime());
    if (ThreadPool.getProducerThreads() == 1) {
      RandomAccessFile randomAccessFile = new RandomAccessFile(fileName, "r");
      FileChannel channel = randomAccessFile.getChannel();
      if (consumerType == ConsumerType.SPECIAL_CONSUMER) {
        ThreadPool.addThread(
            new DoubleQueuesProducer(queue1, queue2, channel, 0, randomAccessFile.length()));
      } else {
        ThreadPool
            .addThread(new SingleQueueProducer(queue1, channel, 0, randomAccessFile.length()));
      }
    } else {
      if (readerType == ReaderType.SPLIT_READER) {
        Timer.pause();
        FileSplitter.splitFile(fileName, infoGetter.getBeginIndexs(), infoGetter.getEndIndexs());
        Timer.resume();
        for (int i = 0; i < ThreadPool.getProducerThreads(); i++) {
          String newFile = fileName.replace(".csv", i + ".csv");
          RandomAccessFile randomAccessFile = new RandomAccessFile(newFile, "r");
          FileChannel channel = randomAccessFile.getChannel();
          if (consumerType == ConsumerType.SPECIAL_CONSUMER) {
            ThreadPool.addThread(
                new DoubleQueuesProducer(queue1, queue2, channel, 0, randomAccessFile.length()));
          } else {
            ThreadPool
                .addThread(new SingleQueueProducer(queue1, channel, 0, randomAccessFile.length()));
          }
        }
      } else {
        long[] beginIndexs = infoGetter.getBeginIndexs();
        long[] endIndexs = infoGetter.getEndIndexs();
        for (int i = 0; i < ThreadPool.getProducerThreads(); i++) {
          long begin = beginIndexs[i];
          long end = endIndexs[i];
          RandomAccessFile randomAccessFile = new RandomAccessFile(fileName, "r");
          FileChannel channel = randomAccessFile.getChannel();
          if (consumerType == ConsumerType.SPECIAL_CONSUMER) {
            ThreadPool.addThread(new DoubleQueuesProducer(queue1, queue2, channel, begin, end));
          } else {
            ThreadPool.addThread(new SingleQueueProducer(queue1, channel, begin, end));

          }
        }
      }
    }
    if (consumerType == ConsumerType.SPECIAL_CONSUMER) {
      for (int i = 0; i < ThreadPool.getMaxThreads() / 2; i++) {
        ThreadPool.addThread(new ThroughputConsumer(queue1, throughputMap));
        ThreadPool.addThread(new LatencyConsumer(queue2, latencyMap));
      }
    } else {
      for (int i = 0; i < ThreadPool.getMaxThreads(); i++) {
        ThreadPool.addThread(new ComboConsumer(queue1, throughputMap, latencyMap));
      }
    }
  }
}
