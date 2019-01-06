package consumer;

import bucket.LatencyBucket;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * The type Latency Request Consumer that can handle requests from a queue, then encapsulate the
 * request into a bucket. If the target bucket exists in the map, then update it, otherwise, add a
 * new bucket element into the map.
 */
public class LatencyConsumer extends SpecialConsumer {

  /**
   * Instantiates a new Latency consumer.
   *
   * @param queue the queue
   * @param map the map
   */
  public LatencyConsumer(BlockingQueue queue, ConcurrentSkipListMap map) {
    super(queue, map);
  }

  /**
   * Add request that taken from a queue to the Buckets .
   *
   * @param info the list of parsed information from a String line
   */
  @Override
  public void addRequest(List<String> info) {
    int latency = Integer.parseInt(info.get(2));
    getMap().putIfAbsent(latency, new LatencyBucket(latency));
    getMap().get(latency).addNew(latency);
  }

  @Override
  public String toString() {
    return "Latency ReqConsumer: " + Thread.currentThread().getName();
  }
}
