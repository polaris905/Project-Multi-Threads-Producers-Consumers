package grapher;

import bucket.ThroughputBucket;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * Grapher for throughput.
 */
public class ThroughputGrapher extends FileGrapher {

  private JFreeChart chart;
  final ChartPanel chartPanel;

  /**
   * Constructor for the grapher.
   *
   * @param applicationTitle name of the application
   * @param data data passed in
   * @param chartTitle title of the chart
   * @throws NumberFormatException avoid parsing invalid string
   * @throws IOException if destination dose not exist
   */
  public ThroughputGrapher(String applicationTitle, Collection<ThroughputBucket> data,
      String chartTitle) throws NumberFormatException, IOException {
    super(applicationTitle);
    XYSeriesCollection dataset = createDataset(data);
    this.chart = createChart(dataset, chartTitle);
    this.chartPanel = new ChartPanel(chart);
    this.chartPanel.setPreferredSize(new java.awt.Dimension(CHARTWIDTH, CHARTHEIGHT));
    this.add(chartPanel);
  }

  /**
   * Get the data from the map.
   *
   * @param data data source
   * @return collection of data needed for plot
   */
  private XYSeriesCollection createDataset(Collection<ThroughputBucket> data) {
    XYSeriesCollection dataset = new XYSeriesCollection();

    // Set up series
    final XYSeries seriesX = new XYSeries("X");
    double firstTime = data.iterator().next().getSecond();
    for (ThroughputBucket bucket : data) {
      seriesX.add(bucket.getSecond() - firstTime, bucket.getCount());
    }
    dataset.addSeries(seriesX);
    return dataset;
  }

  /**
   * Create a chart and save the image.
   *
   * @param dataset data to plot
   * @param chartTitle title of the chart
   * @return a plotted chart
   * @throws NumberFormatException avoid parsing invalid string
   * @throws IOException if destination dose not exist
   */
  @Override
  protected JFreeChart createChart(XYDataset dataset, String chartTitle)
      throws NumberFormatException, IOException {
    String[] paths = chartTitle.split("/");
    String title = paths[paths.length - 1];
    chart = ChartFactory.createXYLineChart(title, // chart title
        "Time", // domain axis label
        "Cumulative Throughput", // range axis label
        dataset, // data
        PlotOrientation.VERTICAL, // the plot orientation
        true, // legend
        true, // tooltips
        false); // urls
    File xyChart = new File(chartTitle + ".png");
    ChartUtilities.saveChartAsPNG(xyChart, chart, CHARTWIDTH, CHARTHEIGHT);
    return chart;
  }
}
