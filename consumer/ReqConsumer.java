package consumer;

import pool.ThreadPool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * The type Request Consumer that can handle requests from a queue, then encapsulate the request
 * into a bucket. If the target bucket exists in the map, then update it, otherwise, add a new
 * bucket element into the map.
 */
public abstract class ReqConsumer implements Runnable {

  /**
   * The constant UNIT presents second to million second.
   */
  public static final int UNIT = 1000;
  /**
   * The constant SPLIT_REGEX.
   */
  public static final String SPLIT_REGEX = "(?<=\"),(?=\")";
  private BlockingQueue<String> queue;

  /**
   * Instantiates a new Request consumer.
   *
   * @param queue the queue
   */
  public ReqConsumer(BlockingQueue queue) {
    this.queue = queue;
  }

  /**
   * Add request that taken from a queue to the Buckets .
   *
   * @param info the list of parsed information from a String line
   */
  public abstract void addRequest(List<String> info);

  /**
   * Parse the information of a line.
   *
   * @param line the line
   * @return the list
   */
  public List<String> parseLine(String line) {
    String[] fields = line.split(SPLIT_REGEX);
    List<String> list = new ArrayList<>(fields.length);
    for (int i = 0; i < fields.length; i++) {
      String field = fields[i].replaceAll("\"", "");
      list.add(field);
    }
    return list;
  }

  /**
   * The run method implements the Runnable interface.
   */
  @Override
  public void run() {
    System.out.println(this + " start");
    while (true) {
      if (queue.isEmpty() && ThreadPool.getProducerLatch().getCount() == 0) {
        break;
      } else {
        String line = queue.poll();
        if (line == null) {
          continue;
        }
        List<String> info = parseLine(line);
        addRequest(info);
      }
    }
    System.out.println(this + " stop");
    ThreadPool.getConsumerLatch().countDown();
  }
}
