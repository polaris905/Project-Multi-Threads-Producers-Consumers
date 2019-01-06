package handler;

import argparser.Option;
import bucket.LatencyBucket;
import bucket.ThroughputBucket;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Abstract class for args handler.
 */
public abstract class TaskHandler {

  protected TaskHandler nextHandler;
  protected static final ConcurrentSkipListMap<Long, ThroughputBucket> THROUGHPUT_POST_MAP
      = new ConcurrentSkipListMap<>();
  protected static final ConcurrentSkipListMap<Long, ThroughputBucket> THROUGHPUT_GET_MAP
      = new ConcurrentSkipListMap<>();
  protected static final ConcurrentSkipListMap<Integer, LatencyBucket> LATENCY_POST_MAP
      = new ConcurrentSkipListMap<>();
  protected static final ConcurrentSkipListMap<Integer, LatencyBucket> LATENCY_GET_MAP
      = new ConcurrentSkipListMap<>();

  /**
   * Set next handler.
   *
   * @param nextHandler the next handler.
   */
  public void setNextHandler(TaskHandler nextHandler) {
    this.nextHandler = nextHandler;
  }

  /**
   * Handle request.
   *
   * @param options parsed args
   * @throws IOException when I/O exception occurs
   */
  public abstract void handleRequest(Map<String, Option> options) throws IOException;
}
