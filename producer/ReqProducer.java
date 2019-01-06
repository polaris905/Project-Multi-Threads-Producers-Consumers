package producer;

import pool.ThreadPool;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Request Producer to read the data via file channel.
 */
public abstract class ReqProducer implements Runnable {

  private static final char SEPARATOR = '\n';
  private long begin;
  private long end;
  private FileChannel channel;

  /**
   * Instantiates a new Csv parser.
   *
   * @param channel the channel
   * @param begin the begin
   * @param end the end
   */
  public ReqProducer(FileChannel channel, long begin, long end) {
    this.channel = channel;
    this.begin = begin;
    this.end = end;
  }

  /**
   * Parse the data via file channel.
   *
   * @throws InterruptedException if the thread is interrupted
   * @throws IOException if the file is not found
   */
  public void parseData() throws InterruptedException, IOException {
    StringBuilder builder = new StringBuilder();
    ByteBuffer buf = ByteBuffer.allocate(1024);
    channel.position(begin);
    boolean isEnd = false;
    long current = begin;
    while (channel.read(buf) != -1 && !isEnd) {
      buf.flip();
      while (buf.hasRemaining()) {
        char read = (char) buf.get();
        current++;
        if (read == SEPARATOR) {
          putToQueue(builder.toString());
          builder = new StringBuilder();
        } else {
          builder.append(read);
        }
        if (current >= end) {
          isEnd = true;
          break;
        }
      }
      buf.clear();
    }
    channel.close();
  }

  /**
   * Helper function that implemented by subclass to determine whether use one queue or two queue.
   *
   * @param line the information line
   * @throws InterruptedException if the thread is interrupted
   */
  abstract void putToQueue(String line) throws InterruptedException;

  /**
   * The run method implements the Runnable interface.
   */
  @Override
  public void run() {
    System.out.println(this + " start");
    try {
      this.parseData();
      ThreadPool.getProducerLatch().countDown();
    } catch (InterruptedException ex) {
      System.out.println(ex.getMessage());
    } catch (IOException ex) {
      System.out.println(ex.getMessage());
    }
    System.out.println(this + " end");
  }
}

