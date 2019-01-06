package handler;

import argparser.Option;
import pool.ThreadPool;
import writer.PeakPhaseWriter;

import java.io.IOException;
import java.util.Map;

/**
 * handler to help generate files as user requires.
 */
public class ThresholdHandler extends TaskHandler {

  /**
   * Handler to determine write peak phase file if needed.
   *
   * @param options parsed args
   * @throws IOException when I/O exception occurs
   */
  @Override
  public void handleRequest(Map<String, Option> options) throws IOException {
    if (options.containsKey("--threshold")) {
      String fileName = options.get("--POST").getSubOptions().get(0);
      int threshold = Integer.parseInt(options.get("--threshold").getSubOptions().get(0));
      ThreadPool.addThread(new PeakPhaseWriter(fileName, THROUGHPUT_POST_MAP, threshold));
    }
    ThreadPool.stop();
    ThreadPool.sleep();
  }
}