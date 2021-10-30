package rpi.serverless;

import at.uibk.dps.ee.deploy.FileStringConverter;
import at.uibk.dps.ee.deploy.client.ApolloClient;
import at.uibk.dps.ee.deploy.server.ApolloServer;
import ch.qos.logback.classic.util.ContextInitializer;
import io.vertx.core.Vertx;

/**
 * Main class to start the experiment.
 *
 * @author Lukas Dötlinger
 */
public class Experiment {

  private final static String logbackConfig = "./src/main/resources/logback.xml";
  private final static String config =        "./src/main/resources/config.xml";
  private final static String afcl =          "./src/main/resources/afcl.yaml";
  private final static String mappings =      "./src/main/resources/mappings.json";
  private final static String input =         "./src/main/resources/input.json";

  private final Vertx vertx;

  public Experiment() {
    this.vertx = Vertx.vertx();
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
    apolloClient.runInput(inputString);
  }

  public static void main(String[] args) {
    System.setProperty(ContextInitializer.CONFIG_FILE_PROPERTY, logbackConfig);

    Experiment experiment = new Experiment();
    experiment.startServer();
    experiment.run();
  }
}
