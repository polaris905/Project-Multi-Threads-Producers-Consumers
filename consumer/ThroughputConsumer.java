package consumer;

import bucket.ThroughputBucket;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * The type Throughput Request Consumer that can handle requests from a queue, then encapsulate the
 * request into a bucket. If the target bucket exists in the map, then update it, otherwise, add a
 * new bucket element into the map.
 */
public class ThroughputConsumer extends SpecialConsumer {

  private static long startTimestamp;

  /**
   * Instantiates a new Throughput consumer.
   *
   * @param queue the queue
   * @param map the map
   */
  public ThroughputConsumer(BlockingQueue queue, ConcurrentSkipListMap map) {
    super(queue, map);
  }

  /**
   * Sets start timestamp.
   *
   * @param startTimestamp the start timestamp
   */
  public static void setStartTimestamp(long startTimestamp) {
    ThroughputConsumer.startTimestamp = startTimestamp;
  }

  /**
   * Add request that taken from a queue to the Buckets .
   *
   * @param info the list of parsed information from a String line
   */
  @Override
  public void addRequest(List<String> info) {
    long timestamp = Long.parseLong(info.get(0));
    int latency = Integer.parseInt(info.get(2));
    long second =
        timestamp % UNIT < startTimestamp % UNIT ? timestamp / UNIT - 1 : timestamp / UNIT;
    getMap().putIfAbsent(second, new ThroughputBucket(second));
    getMap().get(second).addNew(latency);
  }

  @Override
  public String toString() {
    return "Throughput ReqConsumer: " + Thread.currentThread().getName();
  }
}
