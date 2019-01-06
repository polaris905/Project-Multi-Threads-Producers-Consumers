package handler;

import argparser.Option;
import pool.ThreadPool;
import writer.CombinedWriter;
import writer.LatencyWriter;
import writer.ReqResultWriter;

import java.io.IOException;
import java.util.Map;

/**
 * Handler to open all kinds of writer to write information into files.
 */
public class FileHandler extends TaskHandler {

  /**
   * Handle request.
   *
   * @param options parsed args
   * @throws IOException when I/O exception occurs
   */
  @Override
  public void handleRequest(Map<String, Option> options) throws IOException {
    String postFile = options.get("--POST").getSubOptions().get(0);
    String getFile = options.get("--GET").getSubOptions().get(0);
    ThreadPool.addThread(new ReqResultWriter(postFile, THROUGHPUT_POST_MAP));
    ThreadPool.addThread(new ReqResultWriter(getFile, THROUGHPUT_GET_MAP));
    ThreadPool.addThread(new CombinedWriter(postFile, THROUGHPUT_POST_MAP, THROUGHPUT_GET_MAP));
    ThreadPool.addThread(new LatencyWriter(postFile, LATENCY_POST_MAP));
    ThreadPool.addThread(new LatencyWriter(getFile, LATENCY_GET_MAP));
    if (this.nextHandler != null) {
      this.nextHandler.handleRequest(options);
    }
  }
}
