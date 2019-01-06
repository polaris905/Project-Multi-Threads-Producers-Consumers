package bucket;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The type LatencyBucket that encapsulates all requests into one second .
 */
public class LatencyBucket implements BucketAddable {

  private int latency;
  private AtomicLong count;

  /**
   * Instantiates a new LatencyBucket object.
   *
   * @param latency the latency
   */
  public LatencyBucket(int latency) {
    this.latency = latency;
    this.count = new AtomicLong(0);
  }

  /**
   * Add new a request and update the count.
   */
  @Override
  public void addNew(int latency) {
    count.getAndIncrement();
  }

  /**
   * Gets the latency of the bucket.
   *
   * @return the latency
   */
  public long getLatency() {
    return latency;
  }

  /**
   * Gets the count of requests combined in the bucket.
   *
   * @return the count
   */
  public long getCount() {
    return count.get();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    LatencyBucket bucket = (LatencyBucket) obj;
    return latency == bucket.latency;
  }

  @Override
  public int hashCode() {
    return Objects.hash(latency);
  }

  @Override
  public String toString() {
    return "LatencyBucket{" + "latency=" + latency + ", count=" + count + '}';
  }
}
