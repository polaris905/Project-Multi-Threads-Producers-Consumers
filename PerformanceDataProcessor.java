import argparser.ArgParser;
import argparser.CmdLineExceptions;
import argparser.Option;
import argparser.Option.OptionBuilder;
import argparser.Options;
import handler.FileHandler;
import handler.ThreadHandler;
import handler.ThresholdHandler;
import pool.ThreadPool;
import timer.Timer;

import java.io.IOException;
import java.util.Map;

/**
 * The type Performance data processor that serves as an entry class of assignment 7.
 */
public class PerformanceDataProcessor {

  private static Options options;
  private static final String CSV_REGEX = "^(\\\\|\\/)?([\\w-]+(\\\\|\\/))*[\\w-]+\\.csv$";
  private static final String NUM_REGEX = "^[0-9]*$";

  static {
    options = new Options();
    options.addOption(
        new OptionBuilder("--POST").setRequired().hasSubOption().setSubOptionRegex(CSV_REGEX)
            .setDesc("Required argument that gives a CSV file contains POST requests.").build());
    options.addOption(
        new OptionBuilder("--GET").setRequired().hasSubOption().setSubOptionRegex(CSV_REGEX)
            .setDesc("Required argument that gives a CSV file contains GET requests.").build());
    options.addOption(new OptionBuilder("--threshold").hasSubOption().setSubOptionRegex(NUM_REGEX)
        .setDesc(
            "Optional argument that gives an integer value identifies the PEAK timer load period.")
        .build());
    options.addOption(
        new OptionBuilder("--multireader").setExclusive(new String[]{"--split-multireader"})
            .hasSubOption().setSubOptionRegex(NUM_REGEX)
            .setDesc("Optional argument that opens multiple producer threads.").build());
    options.addOption(
        new OptionBuilder("--split-multireader").setExclusive(new String[]{"--multireader"})
            .hasSubOption().setSubOptionRegex(NUM_REGEX).setDesc(
            "Optional argument that splits the original file "
                + "into several segments and opens multiple producer threads.").build());
    options.addOption(new OptionBuilder("--combo-consumer").setDesc(
        "Optional argument that use one queue and combo consumers to generate throughput "
            + "and latency buckets").build());
    options.addExample("--post post_test.csv --get get_test.csv");
    options.addExample("--post post_test.csv --get get_test.csv --threshold 5000");
    options.addExample("--post post_test.csv --get get_test.csv --multireader 4");
    options.addExample("--post post_test.csv --get get_test.csv --multireader 4 --combo-consumer");
    options.addExample(
        "--post post_test.csv --get get_test.csv --threshold 5000 --split-multireader 4");
    options.generateUsage();
  }

  /**
   * The entry point of application.
   *
   * @param args the input arguments
   */
  public static void main(String[] args) {
    ThreadPool.poolReset();
    Timer.reset();
    ArgParser parser = new ArgParser(args, options);
    ThreadHandler threadHandler = new ThreadHandler();
    FileHandler fileHandler = new FileHandler();
    ThresholdHandler thresholdHandler = new ThresholdHandler();
    try {
      Map<String, Option> validOptions = parser.parse();
      threadHandler.setNextHandler(fileHandler);
      fileHandler.setNextHandler(thresholdHandler);
      threadHandler.handleRequest(validOptions);
    } catch (CmdLineExceptions ex) {
      System.out.println(ex.getMessage());
      System.out.println(options.getUsage());
    } catch (IOException ex) {
      System.out.println(ex.getMessage());
    }
    Timer.stop();
  }
}
