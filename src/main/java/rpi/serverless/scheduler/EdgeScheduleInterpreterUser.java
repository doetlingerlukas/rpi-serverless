package rpi.serverless.scheduler;

import at.uibk.dps.ee.core.function.EnactmentFunction;
import at.uibk.dps.ee.enactables.local.container.FunctionFactoryLocal;
import at.uibk.dps.ee.enactables.local.demo.FunctionFactoryDemo;
import at.uibk.dps.ee.enactables.serverless.FunctionFactoryServerless;
import at.uibk.dps.ee.model.properties.PropertyServiceMapping;
import at.uibk.dps.ee.model.properties.PropertyServiceMapping.EnactmentMode;
import at.uibk.dps.ee.model.properties.PropertyServiceResource;
import at.uibk.dps.sc.core.interpreter.ScheduleInterpreterUser;
import edge.discovery.graph.PsMappingLocalRes;
import com.google.inject.Inject;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A schedule interpreter that expects to find multiple resources.
 *
 * @author Lukas Dötlinger
 */
public class EdgeScheduleInterpreterUser  extends ScheduleInterpreterUser {

  /**
   * Default constructor.
   *
   * @param functionFactoryLocal the factory for the creation of
   *                             {@link EnactmentFunction}s performing local calculation
   * @param functionFactorySl    the factory for the creation of serverless functions
   * @param functionFactoryDemo  the factory for the creation of demo functions
   *                             implemented natively
   */
  @Inject
  public EdgeScheduleInterpreterUser(FunctionFactoryLocal functionFactoryLocal,
      FunctionFactoryServerless functionFactorySl, FunctionFactoryDemo functionFactoryDemo) {
    super(functionFactoryLocal, functionFactorySl, functionFactoryDemo);
  }

  @Override
  protected EnactmentFunction interpretScheduleUser(Task task, Set<Mapping<Task, Resource>> scheduleModel) {
    var serverlessMappings = scheduleModel.stream()
      .filter(m -> PropertyServiceMapping.getEnactmentMode(m).equals(EnactmentMode.Serverless))
      .collect(Collectors.toSet());

    var bestEdgeMapping = serverlessMappings.stream()
      .filter(m -> PsMappingLocalRes.isLocResMapping(m))
      .filter(m -> PropertyServiceResource.getUsingTaskIds(m.getTarget()).size() < 4)
      .findFirst();

    var bestServerlessMapping = serverlessMappings.stream()
      .filter(m -> !PsMappingLocalRes.isLocResMapping(m))
      .min(Comparator.comparing((m) -> PropertyServiceResource.getUsingTaskIds(m.getTarget()).size()));

    var anyLocalMapping = scheduleModel.stream()
      .filter(m -> PropertyServiceMapping.getEnactmentMode(m).equals(EnactmentMode.Local))
      .findAny()
      .orElse(scheduleModel.iterator().next());

    var selectedMapping = bestEdgeMapping.orElse(bestServerlessMapping.orElse(anyLocalMapping));

    return getFunctionForMapping(task, selectedMapping);
  }
}
