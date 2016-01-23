package com.scanii.client.cli;


import com.scanii.client.ScaniiClient;
import com.scanii.client.ScaniiException;
import com.scanii.client.ScaniiResult;
import com.scanii.client.ScaniiTarget;
import com.scanii.client.batch.ScaniiBatchClient;
import com.scanii.client.batch.ScaniiResultHandler;
import com.scanii.client.misc.Endpoints;
import humanize.Humanize;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class ScaniiCLI {
  public static void main(String[] args) throws IOException, ArgumentParserException, InterruptedException {
    ArgumentParser parser = ArgumentParsers.newArgumentParser("scanii-cli")
      .defaultHelp(true)
      .description("Scanii.com command line interface");
    parser.addArgument("-c", "--credentials")
      .help("api credentials to use in the KEY:SECRET format")
      .required(true);

    parser.addArgument("-e", "--endpoint")
      .choices("us1", "eu1", "auto", "local")
      .help("api endpoint to be used, see: http://docs.scanii.com/articles/understanding-api-endpoints.html")
      .setDefault("auto");

    parser.addArgument("path")
      .nargs(1)
      .help("path to local content to be processed");

    Namespace ns = null;
    try {
      ns = parser.parseArgs(args);
    } catch (ArgumentParserException e) {
      parser.handleError(e);
      System.exit(1);
    }

    // parsing credentials:
    final String KEY = ns.getString("credentials").split(":")[0];
    final String SECRET = ns.getString("credentials").split(":")[1];

    // endpoint:
    ScaniiTarget target = ScaniiTarget.latest();

    if (ns.getString("endpoint").equals("us1")) {
      target = ScaniiTarget.v2_1_US1;
    } else if (ns.getString("endpoint").equals("eu1")) {
      target = ScaniiTarget.v2_1_EU1;
    } else if (ns.getString("endpoint").equals("local")) {
      target = ScaniiTarget.LOCAL;
    }

    // bootstrapping:
    System.out.println("scanii-cli starting....");
    System.out.println("using endpoint: " + Endpoints.resolve(target, ""));
    System.out.println("using key: " + KEY);
    System.out.print("verifying connectivity to scanii service...");

    final ScaniiClient client = new ScaniiClient(target, KEY, SECRET);
    try {
      client.ping();
    } catch (ScaniiException ex) {
      System.out.println("ERROR, please check your credentials...");
      ex.printStackTrace();
      System.exit(1);
    }
    System.out.println("OK");

    // building file list:
    final AtomicLong totalFiles = new AtomicLong();
    List<String> paths = ns.getList("path");
    System.out.print("processing file list for path:" + paths.toString() + "...");

    for (String dir : paths) {
      Files.walkFileTree(Paths.get(dir), new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
          if (!attrs.isDirectory()) {
            totalFiles.incrementAndGet();
          }
          return FileVisitResult.CONTINUE;
        }
      });
    }

    System.out.println("done");
    System.out.println("processing " + Humanize.formatDecimal(totalFiles.get()) + " files in batch mode");

    final ScaniiBatchClient bclient = new ScaniiBatchClient(client);
    final AtomicLong bytesProcessed = new AtomicLong();
    final long startTime = System.nanoTime();

    for (String dir : paths) {
      Files.walkFileTree(Paths.get(dir), new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult visitFile(final Path path, BasicFileAttributes attrs) throws IOException {
          if (!attrs.isDirectory()) {
            bclient.submit(path, new ScaniiResultHandler() {
              @Override
              public void handle(ScaniiResult result) {
                bytesProcessed.addAndGet(result.getContentLength());
                long completed = bclient.getCompletedCount();
                float total = totalFiles.get();
                String percentage = Humanize.formatPercent(completed / total);
                System.out.println(String.format("➝ %s | findings: %s completed: %s/%s (%s)", path.toAbsolutePath().toString(), result.getFindings(), Humanize.formatDecimal(completed), Humanize.formatDecimal(total), percentage));
              }
            });
          }
          return FileVisitResult.CONTINUE;
        }
      });
    }

    while (bclient.hasPending()) {
      Thread.sleep(1000);
    }

    long elapsed = System.nanoTime() - startTime;
    System.out.println(">>");
    System.out.println("run summary:");
    System.out.println(String.format(">> %d files processed in %s", bclient.getCompletedCount(), Humanize.nanoTime(elapsed)));
    System.out.println(String.format(">> throughput: %s/s", Humanize.binaryPrefix(bytesProcessed.get() / TimeUnit.NANOSECONDS.toSeconds(elapsed))));
    System.out.println(">>");
  }
}
