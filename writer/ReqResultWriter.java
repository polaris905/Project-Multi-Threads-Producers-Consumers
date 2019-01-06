package writer;

import bucket.ThroughputBucket;
import grapher.ThroughputGrapher;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * The type Request result writer that generates the POST or GET csv file.
 */
public class ReqResultWriter extends CsvFileWriter {

  private ConcurrentSkipListMap<Long, ThroughputBucket> map;
  private static final int NTH = 99;
  private String fileName;

  /**
   * Instantiates a new Req result writer.
   *
   * @param fileName the file name
   * @param map the map
   */
  public ReqResultWriter(String fileName, ConcurrentSkipListMap<Long, ThroughputBucket> map) {
    super(fileName.replace("raw.csv", "-result.csv"));
    this.fileName = fileName;
    this.map = map;
  }

  /**
   * Write a single file.
   */
  @Override
  public void writeFile() {
    try (BufferedWriter output = new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream(getFileName(), false), "UTF-8"));) {
      long totalLatency = 0;
      long requestCount = 0;
      int bucketCount = 0;
      output.write(String.format("\"Second\",\"Count\",\"Latency\"%n"));
      for (Entry<Long, ThroughputBucket> entry : map.entrySet()) {
        requestCount += entry.getValue().getCount();
        totalLatency += entry.getValue().getMeanLatency();
        bucketCount++;
        output.write(String
            .format("\"%d\",\"%d\",\"%d\"%n", entry.getKey(), entry.getValue().getCount(),
                entry.getValue().getMeanLatency()));
      }
      output.write("Test length: " + (map.lastKey() - map.firstKey()) + "\n");
      output.write("Mean latency: " + totalLatency / bucketCount + "\n");
      List<ThroughputBucket> list = new ArrayList<>(map.values());
      Collections.sort(list, new Comparator<ThroughputBucket>() {
        @Override
        public int compare(ThroughputBucket bucket1, ThroughputBucket bucket2) {
          return (int) (bucket1.getMeanLatency() - bucket2.getMeanLatency());
        }
      });
      int percentile = map.size() * NTH / 100;
      output.write(NTH + "th percentile latency: " + list.get(percentile).getMeanLatency() + "\n");
      output.write("Total throughput: " + requestCount / (map.lastKey() - map.firstKey()) + "\n");
      output.close();
      //for the POST-results data only.
      if (fileName.contains("POST")) {
        new ThroughputGrapher("Throughput Plot", map.values(),
            fileName.replace("raw.csv", "-throughput-plot"));
      }
      System.out.println("Generate: " + getFileName());
    } catch (IOException ex) {
      System.out.println("ERROR: something went wrong: " + ex.getMessage());
    }
  }

  @Override
  public String toString() {
    return "Request Result Writer: " + Thread.currentThread().getName();
  }
}
