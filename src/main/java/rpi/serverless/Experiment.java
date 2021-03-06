package rpi.serverless;

import at.uibk.dps.ee.deploy.FileStringConverter;
import at.uibk.dps.ee.deploy.client.ApolloClient;
import at.uibk.dps.ee.deploy.server.ApolloServer;
import ch.qos.logback.classic.util.ContextInitializer;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.VertxTracerFactory;
import io.vertx.core.tracing.TracingOptions;

import java.util.ArrayList;

/**
 * Main class to start the experiment.
 *
 * @author Lukas Dötlinger
 */
public class Experiment {

  private final static String logbackConfig = "./src/main/resources/logback.xml";
  private final static String config =        "./src/main/resources/sentiment-analysis/config.xml";
  private final static String afcl =          "./src/main/resources/sentiment-analysis/workflow.yaml";
  private final static String mappings =      "./src/main/resources/sentiment-analysis/mappings.json";
  private final static String input =         "./src/main/resources/sentiment-analysis/input/input-200-tweets.json";

  private final Vertx vertx;

  public Experiment() {
    var vertxOptions = new VertxOptions();
    vertxOptions.setTracingOptions(
      new TracingOptions().setFactory(VertxTracerFactory.NOOP));

    this.vertx = Vertx.vertx(vertxOptions);
  }

  private void startServer() {
    ApolloServer apolloServer = new ApolloServer(this.vertx);
    apolloServer.start();
  }

  private void run() {
    double runs = 10;

    String specString   = FileStringConverter.readSpecString(afcl, mappings);
    String configString = FileStringConverter.readModuleConfiguration(config);
    String inputString  = FileStringConverter.readInputFile(input);

    ApolloClient apolloClient = new ApolloClient(this.vertx, "127.0.0.1");
    apolloClient.configureServer(specString, configString);

    System.out.println("Experiment started.");

    var runtimes = new ArrayList<Long>();

    for (int i = 1; i <= runs; i++) {
      String result = apolloClient.runInput(inputString);
      JsonObject json = new JsonObject(result);

      long runtime = Long.parseLong(json.getString("runtime"));

      runtimes.add(runtime);
      System.out.println("Experiment run " + i + " took " + runtime / 1000f + " seconds.");

      try {
        Thread.sleep(5000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    var mean = runtimes.stream().mapToLong(Long::longValue).sum() / runs;
    var variance =
      runtimes.stream()
        .map(r -> r - mean).mapToDouble(Double::doubleValue)
        .map(d -> Math.pow(d, 2))
        .sum() / runs;
    var std_deviation = Math.sqrt(variance);

    System.out.println("Experiment took on average " + mean / 1000f + " seconds for " +
      runs + " runs.");
    System.out.println(runtimes);
    System.out.println("Mean: " + mean + ", Variance: " + variance +
      ", Standard Deviation: " + std_deviation);

    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private void close() {
    vertx.deploymentIDs().forEach(vertx::undeploy);
    vertx.close();
  }

  public static void main(String[] args) {
    System.setProperty(ContextInitializer.CONFIG_FILE_PROPERTY, logbackConfig);

    Experiment experiment = new Experiment();
    experiment.startServer();
    experiment.run();
    experiment.close();
  }
}
