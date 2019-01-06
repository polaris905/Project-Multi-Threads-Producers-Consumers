package producer;

import java.nio.channels.FileChannel;
import java.util.concurrent.BlockingQueue;

/**
 * The type Two Queues Producer that read one line from CSV file and send to two queues.
 */
public class DoubleQueuesProducer extends ReqProducer {


  private BlockingQueue<String> throughputQueue;
  private BlockingQueue<String> latencyQueue;

  /**
   * Instantiates a new Csv parser.
   *
   * @param throughputQueue the throughput queue
   * @param latencyQueue the latency queue
   * @param channel the channel
   * @param begin the begin
   * @param end the end
   */
  public DoubleQueuesProducer(BlockingQueue<String> throughputQueue,
      BlockingQueue<String> latencyQueue, FileChannel channel, long begin, long end) {
    super(channel, begin, end);
    this.throughputQueue = throughputQueue;
    this.latencyQueue = latencyQueue;
  }

  /**
   * Helper function that implemented by subclass to determine whether use one queue or two queue.
   *
   * @throws InterruptedException if the thread is interrupted
   */
  @Override
  public void putToQueue(String line) throws InterruptedException {
    throughputQueue.put(line);
    latencyQueue.put(line);
  }

  @Override
  public String toString() {
    return "Double Queues ReqProducer: " + Thread.currentThread().getName();
  }
}
