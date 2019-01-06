package producer;

import java.nio.channels.FileChannel;
import java.util.concurrent.BlockingQueue;

/**
 * The type Single Queue Producer that read one line from CSV file and only send to one queue.
 */
public class SingleQueueProducer extends ReqProducer {

  private BlockingQueue<String> queue;

  /**
   * Instantiates a new Csv parser.
   *
   * @param queue the queue
   * @param channel the channel
   * @param begin the begin
   * @param end the end
   */
  public SingleQueueProducer(BlockingQueue<String> queue, FileChannel channel, long begin,
      long end) {
    super(channel, begin, end);
    this.queue = queue;
  }

  /**
   * Helper function that implemented by subclass to determine whether use one queue or two queue.
   *
   * @throws InterruptedException if the thread is interrupted
   */
  @Override
  public void putToQueue(String line) throws InterruptedException {
    queue.put(line);
  }

  @Override
  public String toString() {
    return "Single Queue ReqProducer: " + Thread.currentThread().getName();
  }
}
