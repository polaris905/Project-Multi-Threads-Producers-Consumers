package splitter;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Helper to split file into different segments.
 */
public class FileSplitter {

  /**
   * Main function to split the file as indexes indicate.
   * @param file target file
   * @param beginIndexes start index for each segment
   * @param endIndexes end index for each segment
   */
  public static void splitFile(String file, long[] beginIndexes, long[] endIndexes) {
    for (int i = 0; i < beginIndexes.length; i++) {
      writeHelper(file, beginIndexes[i], endIndexes[i], i, '\n');
    }
  }

  /**
   * Helper to write the corresponding part.
   * @param file target file
   * @param begin start index in target file
   * @param end end index in target file
   * @param segValue segment number
   * @param separator separator for line
   */
  private static void writeHelper(String file, long begin, long end, int segValue, char separator) {
    try (BufferedWriter output = new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream(file.replace(".csv", segValue + ".csv")),
            "UTF-8"))) {
      //read the file.
      RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
      FileChannel channel = randomAccessFile.getChannel();
      StringBuilder builder = new StringBuilder();
      ByteBuffer buf = ByteBuffer.allocate(1024);
      channel.position(begin);
      boolean isEnd = false;
      long current = begin;
      while (channel.read(buf) != -1 && !isEnd) {
        buf.flip();
        while (buf.hasRemaining()) {
          char read = (char) buf.get();
          current++;
          if (read == separator) {
            builder.append(read);
            output.write(builder.toString());
            builder = new StringBuilder();
          } else {
            builder.append(read);
          }
          if (current >= end) {
            isEnd = true;
            break;
          }
        }
        buf.clear();
      }
      channel.close();
      output.close();
      System.out.println("Split original file " + file + segValue);
    } catch (FileNotFoundException fnfe) {
      System.out.println("Input file not found! : " + fnfe.getMessage());
    } catch (IOException ioe) {
      System.out.println("Something went wrong! : " + ioe.getMessage());
    }
  }
}
