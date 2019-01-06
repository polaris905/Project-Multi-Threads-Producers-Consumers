package splitter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;

/**
 * Class to help get the information we need.
 */
public class InfoGetter {

  private long[] beginIndexs;
  private long[] endIndexs;
  private String splitRegex;
  private long startTime;
  private String fileName;
  private char separator;
  private int threadCount;

  /**
   * Constructor for InfoGetter.
   *
   * @param fileName input file
   * @param threadCount how many threads to process the file
   * @param splitRegex the separator for elements in the same line
   * @param separator separate the lines
   * @throws IOException the IO exception
   */
  public InfoGetter(String fileName, int threadCount, String splitRegex, char separator)
      throws IOException {
    this.threadCount = threadCount;
    this.fileName = fileName;
    this.beginIndexs = new long[threadCount];
    this.endIndexs = new long[threadCount];
    this.splitRegex = splitRegex;
    this.separator = separator;
    getAll();
  }

  /**
   * Get all the info.
   *
   * @throws IOException the IO exception
   */
  private void getAll() throws IOException {
    this.startTimeStampGetter();
    this.getIndex();
  }

  /**
   * Get the start time - first element in csv file.
   *
   * @return the start time
   */
  public long getStartTime() {
    return startTime;
  }

  /**
   * Begin indexs for different threads.
   *
   * @return the array
   */
  public long[] getBeginIndexs() {
    long[] begins = this.beginIndexs;
    return begins;
  }

  /**
   * End indexs for different threads.
   *
   * @return the array
   */
  public long[] getEndIndexs() {
    long[] ends = this.endIndexs;
    return ends;
  }

  /**
   * Get the start time from the first line of the file.
   *
   * @throws FileNotFoundException the file not found exception
   * @throws IOException the IO exception
   */
  private void startTimeStampGetter() throws FileNotFoundException, IOException {
    BufferedReader reader = new BufferedReader(
        new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
    String line;
    if ((line = reader.readLine()) != null) {
      startTime = Long.parseLong(line.split(splitRegex)[0].replaceAll("\"", ""));
    }
    reader.close();
  }

  /**
   * Separate the file into different parts.
   *
   * @throws IOException the IO exception
   */
  private void getIndex() throws IOException {
    RandomAccessFile randomAccessFile = new RandomAccessFile(fileName, "r");
    long fileTotalLength = randomAccessFile.length();
    long gap = fileTotalLength / threadCount;
    long checkIndex = 0;

    for (int n = 0; n < threadCount; n++) {
      beginIndexs[n] = checkIndex;
      if (n == threadCount - 1) {
        endIndexs[n] = fileTotalLength;
        break;
      }
      checkIndex += gap;
      long lengthToEnd = getAWholeLine(checkIndex, randomAccessFile);
      checkIndex += lengthToEnd;
      endIndexs[n] = checkIndex;
    }
  }

  /**
   * Helper for getIndex() function to get the whole line.
   *
   * @param beginIndex where to start
   * @param randomAccessFile the file we are dealing with
   * @return the distance between the start position and the end of this line
   * @throws IOException the IO exception
   */
  private long getAWholeLine(long beginIndex, RandomAccessFile randomAccessFile)
      throws IOException {
    randomAccessFile.seek(beginIndex);
    long count = 0;
    while (randomAccessFile.read() != separator) {
      count++;
    }
    count++;
    return count;
  }
}