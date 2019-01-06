package grapher;

import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;

import java.io.IOException;

/**
 * Main File Grapher.
 */
public abstract class FileGrapher extends ApplicationFrame {

  static final int CHARTWIDTH = 1000;
  static final int CHARTHEIGHT = 500;

  /**
   * Constructor for the class.
   * @param applicationTitle name of the application
   */
  public FileGrapher(String applicationTitle) {
    super(applicationTitle);
  }

  /**
   * Create a chart and save the image.
   * @param dataset data to plot
   * @param chartTitle title of the chart
   * @return a plotted chart
   * @throws NumberFormatException avoid parsing invalid string
   * @throws IOException if destination dose not exist
   */
  protected abstract JFreeChart createChart(XYDataset dataset, String chartTitle)
      throws NumberFormatException, IOException;
}
