package pool;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * The type Thread pool that manages the multiple thread process.
 */
public class ThreadPool {

  /**
   * The constant MAX_THREADS.
   */
  private static final int MAX_THREADS = Runtime.getRuntime().availableProcessors();
  /**
   * The constant producerThreads.
   */
  private static int producerThreads = 1;
  /**
   * The constant executor.
   */
  private static ExecutorService executor;

  private static CountDownLatch producerLatch;

  private static CountDownLatch consumerLatch;

  /**
   * Gets max threads.
   *
   * @return the max threads
   */
  public static int getMaxThreads() {
    return MAX_THREADS;
  }

  /**
   * Thread pool reset.
   */
  public static void poolReset() {
    executor = Executors.newCachedThreadPool();
  }

  /**
   * Gets producer threads.
   *
   * @return the producer threads
   */
  public static int getProducerThreads() {
    return producerThreads;
  }

  /**
   * CountDownLatch reset.
   */
  public static void latchReset() {
    producerLatch = new CountDownLatch(producerThreads);
    consumerLatch = new CountDownLatch(MAX_THREADS);
  }

  /**
   * Sets producer threads.
   *
   * @param producerThreads the producer threads
   */
  public static void setProducerThreads(int producerThreads) {
    ThreadPool.producerThreads = producerThreads;
  }

  /**
   * Gets producer latch.
   *
   * @return the producer latch
   */
  public static CountDownLatch getProducerLatch() {
    return producerLatch;
  }

  /**
   * Gets consumer latch.
   *
   * @return the consumer latch
   */
  public static CountDownLatch getConsumerLatch() {
    return consumerLatch;
  }

  /**
   * Add a new thread.
   *
   * @param runnable the runnable
   */
  public static void addThread(Runnable runnable) {
    executor.execute(runnable);
  }

  /**
   * Stop the executor.
   */
  public static void stop() {
    executor.shutdown();
  }

  /**
   * Sleep until all threads stop.
   */
  public static void sleep() {
    try {
      while (!executor.isTerminated()) {
        executor.awaitTermination(100, TimeUnit.MILLISECONDS);
      }
    } catch (InterruptedException ex) {
      System.out.println(ex.getMessage());
    }
  }
}