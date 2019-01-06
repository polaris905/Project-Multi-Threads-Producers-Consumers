package writer;

import bucket.ThroughputBucket;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * The type Combined writer that combine the POST and GET results.
 */
public class CombinedWriter extends CsvFileWriter {

  private ConcurrentSkipListMap<Long, ThroughputBucket> postMap;
  private ConcurrentSkipListMap<Long, ThroughputBucket> getMap;

  /**
   * Instantiates a new Combined writer.
   *
   * @param fileName the file name
   * @param postMap the post map
   * @param getMap the get map
   */
  public CombinedWriter(String fileName, ConcurrentSkipListMap<Long, ThroughputBucket> postMap,
      ConcurrentSkipListMap<Long, ThroughputBucket> getMap) {
    super(fileName.replace("POSTraw.csv", "combined.csv"));
    this.postMap = postMap;
    this.getMap = getMap;
  }

  /**
   * Merge files takes in 2 concurrent maps, and merges the columns together.
   */
  @Override
  public void writeFile() {
    try {
      BufferedWriter output = new BufferedWriter(
          new OutputStreamWriter(new FileOutputStream(getFileName(), false), "UTF-8"));
      long totalReq = 0;
      long count = 0;
      long maxThroughput = 0;
      output.write(String.format("\"POST\",\"\",\"\",\"GET\",\"\",\"\",\"Total\"%n"));
      output.write(String.format(
          "\"Second\",\"Count\",\"Latency\"" + ",\"Second\",\"Count\",\"Latency\",\"Count\"%n"));
      for (Entry<Long, ThroughputBucket> pair : postMap.entrySet()) {
        long key = pair.getKey();
        ThroughputBucket bucket1 = pair.getValue();
        ThroughputBucket bucket2 = getMap.get(key);
        count++;
        if (bucket2 != null) {
          output.write(String
              .format("\"%d\",\"%d\",\"%d\",\"%d\",\"%d\",\"%d\",\"%d\"%n", bucket1.getSecond(),
                  bucket1.getCount(), bucket1.getMeanLatency(), bucket2.getSecond(),
                  bucket2.getCount(), bucket2.getMeanLatency(),
                  bucket1.getCount() + bucket2.getCount()));
          totalReq += (bucket1.getCount() + bucket2.getCount());
          maxThroughput = Math.max(maxThroughput, bucket1.getCount() + bucket2.getCount());
        } else {
          output.write(String
              .format("\"%d\",\"%d\",\"%d\",\"%s\",\"%s\",\"%s\",\"%d\"%n", bucket1.getSecond(),
                  bucket1.getCount(), bucket1.getMeanLatency(), "", "", "", bucket1.getCount()));
          totalReq += bucket1.getCount();
          maxThroughput = Math.max(maxThroughput, bucket1.getCount());
        }
      }
      output.write("Overall throughput: " + totalReq / count + "\n");
      output.write("Peak throughput: " + maxThroughput + "\n");
      output.close();
      System.out.println("Generate: " + getFileName());
    } catch (IOException ex) {
      System.out.println("ERROR: something went wrong: " + ex.getMessage());
    }
  }

  @Override
  public String toString() {
    return "Combined Writer: " + Thread.currentThread().getName();
  }
}