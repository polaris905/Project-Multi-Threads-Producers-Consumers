package bucket;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The type ThroughputBucket that encapsulates all requests into one second .
 */
public class ThroughputBucket implements BucketAddable {

  private long second;
  private AtomicLong count;
  private AtomicLong totalLatency;

  /**
   * Instantiates a new ThroughputBucket object.
   *
   * @param second the second
   */
  public ThroughputBucket(long second) {
    this.second = second;
    this.count = new AtomicLong(0);
    this.totalLatency = new AtomicLong(0);
  }

  /**
   * Add new a request and update the count and latency.
   *
   * @param latency the new latency
   */
  @Override
  public void addNew(int latency) {
    totalLatency.getAndAdd(latency);
    count.getAndIncrement();
  }

  /**
   * Gets the second of the bucket.
   *
   * @return the second
   */
  public long getSecond() {
    return second;
  }

  /**
   * Gets the count of requests combined in the bucket.
   *
   * @return the count
   */
  public long getCount() {
    return count.get();
  }

  /**
   * Gets the mean latency of the bucket.
   *
   * @return the mean latency
   */
  public long getMeanLatency() {
    return count.get() == 0 ? 0 : totalLatency.get() / count.get();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    ThroughputBucket bucket = (ThroughputBucket) obj;
    return second == bucket.second;
  }

  @Override
  public int hashCode() {
    return Objects.hash(second);
  }

  @Override
  public String toString() {
    return "ThroughputBucket{" + "second=" + second + ", " + "count=" + count + ", totalLatency="
        + totalLatency + '}';
  }
}
