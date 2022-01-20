package rpi.serverless;

import at.uibk.dps.ee.deploy.FileStringConverter;
import at.uibk.dps.ee.deploy.client.ApolloClient;
import at.uibk.dps.ee.deploy.server.ApolloServer;
import ch.qos.logback.classic.util.ContextInitializer;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.VertxTracerFactory;
import io.vertx.core.tracing.TracingOptions;

import java.time.Duration;
import java.time.Instant;

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
  private final static String input =         "./src/main/resources/sentiment-analysis/input/input-10000-tweets.json";

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
    String specString   = FileStringConverter.readSpecString(afcl, mappings);
    String configString = FileStringConverter.readModuleConfiguration(config);
    String inputString  = FileStringConverter.readInputFile(input);

    ApolloClient apolloClient = new ApolloClient(this.vertx, "127.0.0.1");
    apolloClient.configureServer(specString, configString);

    System.out.println("Experiment started.");
    var start = Instant.now();
    apolloClient.runInput(inputString);
    var end = Instant.now();
    System.out.println("Experiment took " + Duration.between(start, end).toMillis() / 1000f + " seconds.");
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
