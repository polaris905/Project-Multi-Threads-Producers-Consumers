package writer;

import pool.ThreadPool;

/**
 * The type Csv file writer.
 */
public abstract class CsvFileWriter implements Runnable {

  private String fileName;

  /**
   * Instantiates a new Csv file writer.
   *
   * @param fileName the file name
   */
  public CsvFileWriter(String fileName) {
    this.fileName = fileName;
  }

  /**
   * Gets file name.
   *
   * @return the file name
   */
  public String getFileName() {
    return fileName;
  }

  /**
   * Write file.
   */
  abstract void writeFile();

  /**
   * The run method implements the Runnable interface.
   */
  @Override
  public void run() {
    try {
      System.out.println(this + " wait");
      ThreadPool.getConsumerLatch().await();
      System.out.println(this + " is writing file");
      writeFile();
      System.out.println(this + " end");
    } catch (InterruptedException ex) {
      System.out.println(ex.getMessage());
    }
  }
}
