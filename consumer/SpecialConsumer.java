package consumer;

import bucket.BucketAddable;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * The type Special consumer that only handle throughput or latency bucket.
 */
public abstract class SpecialConsumer extends ReqConsumer {

  private ConcurrentSkipListMap<Number, BucketAddable> map;

  /**
   * Instantiates a new Request consumer.
   *
   * @param queue the queue
   * @param map the map
   */
  public SpecialConsumer(BlockingQueue queue, ConcurrentSkipListMap map) {
    super(queue);
    this.map = map;
  }

  /**
   * Gets map.
   *
   * @return the map
   */
  public ConcurrentSkipListMap<Number, BucketAddable> getMap() {
    return map;
  }
}
