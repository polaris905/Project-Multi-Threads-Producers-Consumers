package writer;

import bucket.ThroughputBucket;
import grapher.ThroughputGrapher;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * The type Peak Phase writer that generates peak phase analysis.
 */
public class PeakPhaseWriter extends CsvFileWriter {

  private static final int LOWTH = 5;
  private static final int HIGHTH = 99;
  private ConcurrentSkipListMap<Long, ThroughputBucket> map;
  private int threshold;
  private String fileName;

  /**
   * Constructor for peak phase writer.
   *
   * @param fileName target file
   * @param map data map
   * @param threshold the threshold to help determine the peak
   */
  public PeakPhaseWriter(String fileName, ConcurrentSkipListMap<Long, ThroughputBucket> map,
      int threshold) {
    super(fileName.replace("raw.csv", "-peak.csv"));
    this.map = map;
    this.fileName = fileName;
    this.threshold = threshold;
  }

  /**
   * Analyze the data and write the file.
   */
  @Override
  public void writeFile() {
    try (BufferedWriter output = new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream(getFileName(), false), "UTF-8"))) {
      LinkedList<ThroughputBucket> peakPhase = new LinkedList<>(map.values());
      while (peakPhase.getFirst().getCount() < threshold) {
        peakPhase.removeFirst();
      }
      while (peakPhase.getLast().getCount() < threshold) {
        peakPhase.removeLast();
      }
      new ThroughputGrapher("Peak Plot", peakPhase, fileName.replace("raw.csv", "-peak-plot"));
      long totalThroughput = 0;
      long highest = Long.MIN_VALUE;
      long totalResponseTime = 0;
      output.write(String.format("\"Second\",\"Count\",\"Latency\"%n"));
      for (ThroughputBucket bucket : peakPhase) {
        totalThroughput += bucket.getCount();
        totalResponseTime += bucket.getMeanLatency();
        highest = Math.max(bucket.getCount(), highest);
        output.write(String.format("\"%d\",\"%d\",\"%d\"%n", bucket.getSecond(), bucket.getCount(),
            bucket.getMeanLatency()));
      }
      output.write("The duration of the peak phase in seconds: " + (
          peakPhase.get(peakPhase.size() - 1).getSecond() - peakPhase.get(0).getSecond()) + "\n");
      output.write("The mean throughput:" + totalThroughput / peakPhase.size() + "\n");
      output.write("The highest interval requests/sec: " + highest + "\n");
      Collections.sort(peakPhase, new Comparator<ThroughputBucket>() {
        @Override
        public int compare(ThroughputBucket bucket1, ThroughputBucket bucket2) {
          return (int) (bucket1.getCount() - bucket2.getCount());
        }
      });
      int peakPhasePercentile = peakPhase.size() * LOWTH / 100;
      output.write(LOWTH + "th percentile value for the peak phase throughput: " + peakPhase
          .get(peakPhasePercentile).getCount() + "\n");
      output.write("The mean response time: " + totalResponseTime / peakPhase.size() + "\n");
      Collections.sort(peakPhase, new Comparator<ThroughputBucket>() {
        @Override
        public int compare(ThroughputBucket bucket1, ThroughputBucket bucket2) {
          return (int) (bucket1.getMeanLatency() - bucket2.getMeanLatency());
        }
      });
      int responseTimePercentile = peakPhase.size() * HIGHTH / 100;
      output.write(HIGHTH + "th percentile response time: " + peakPhase.get(responseTimePercentile)
          .getMeanLatency() + "\n");
      output.close();
      System.out.println("Generate: " + getFileName());
    } catch (IOException ex) {
      System.out.println("ERROR: something went wrong: " + ex.getMessage());
    }
  }

  @Override
  public String toString() {
    return "Peak Phase Writer: " + Thread.currentThread().getName();
  }
}
