package writer;

import bucket.LatencyBucket;
import grapher.LatencyGrapher;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * The type Latency writer that generates the POST and GET latency results.
 */
public class LatencyWriter extends CsvFileWriter {

  private ConcurrentSkipListMap<Integer, LatencyBucket> map;
  private String fileName;

  /**
   * Instantiates a new Latency writer.
   *
   * @param fileName the file name
   * @param map the map
   */
  public LatencyWriter(String fileName, ConcurrentSkipListMap<Integer, LatencyBucket> map) {
    super(fileName.replace("raw.csv", "-latency.csv"));
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
      output.write(String.format("\"Latency\",\"Count\"%n"));
      for (Entry<Integer, LatencyBucket> entry : map.entrySet()) {
        output.write(String.format("\"%d\",\"%d\"%n", entry.getKey(), entry.getValue().getCount()));
      }
      output.close();
      new LatencyGrapher("Latency Plot", map.values(),
          fileName.replace("raw.csv", "-latency-plot"));
      System.out.println("Generate: " + getFileName());
    } catch (IOException ex) {
      System.out.println("ERROR: something went wrong: " + ex.getMessage());
    }
  }

  @Override
  public String toString() {
    return "Latency Writer: " + Thread.currentThread().getName();
  }
}
