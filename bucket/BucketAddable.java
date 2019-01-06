package bucket;

/**
 * The interface represents the addable Bucket.
 */
public interface BucketAddable {

  /**
   * Add new information to the bucket.
   *
   * @param latency the latency
   */
  void addNew(int latency);
}
