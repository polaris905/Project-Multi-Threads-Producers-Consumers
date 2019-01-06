package consumer;

import bucket.BucketAddable;
import bucket.LatencyBucket;
import bucket.ThroughputBucket;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * The type Combo consumer that takes information from one queue and handles both throughput and
 * latency buckets.
 */
public class ComboConsumer extends ReqConsumer {

  private static long startTimestamp;
  private ConcurrentSkipListMap<Number, BucketAddable> throughputMap;
  private ConcurrentSkipListMap<Number, BucketAddable> latencyMap;

  /**
   * Instantiates a new Combo Request consumer.
   *
   * @param queue the queue
   * @param throughputMap the throughputMap
   * @param latencyMap the latencyMap
   */
  public ComboConsumer(BlockingQueue queue, ConcurrentSkipListMap throughputMap,
      ConcurrentSkipListMap latencyMap) {
    super(queue);
    this.throughputMap = throughputMap;
    this.latencyMap = latencyMap;
  }

  /**
   * Sets start timestamp.
   *
   * @param startTimestamp the start timestamp
   */
  public static void setStartTimestamp(long startTimestamp) {
    ComboConsumer.startTimestamp = startTimestamp;
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
    throughputMap.putIfAbsent(second, new ThroughputBucket(second));
    throughputMap.get(second).addNew(latency);
    latencyMap.putIfAbsent(latency, new LatencyBucket(latency));
    latencyMap.get(latency).addNew(latency);
  }

  @Override
  public String toString() {
    return "Combo ReqConsumer: " + Thread.currentThread().getName();
  }
}
