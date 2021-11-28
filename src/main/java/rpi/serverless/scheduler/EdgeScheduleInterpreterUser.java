package rpi.serverless.scheduler;

import at.uibk.dps.ee.core.function.EnactmentFunction;
import at.uibk.dps.ee.enactables.local.container.FunctionFactoryLocal;
import at.uibk.dps.ee.enactables.local.demo.FunctionFactoryDemo;
import at.uibk.dps.ee.enactables.serverless.FunctionFactoryServerless;
import at.uibk.dps.ee.model.properties.PropertyServiceMapping;
import at.uibk.dps.ee.model.properties.PropertyServiceMapping.EnactmentMode;
import at.uibk.dps.sc.core.interpreter.ScheduleInterpreterUser;
import com.google.inject.Inject;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

import java.util.Set;

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
    var selectedMapping = scheduleModel.stream()
      .filter(m -> PropertyServiceMapping.getEnactmentMode(m).equals(EnactmentMode.Serverless))
      .findFirst();

    return getFunctionForMapping(task, selectedMapping.orElse(scheduleModel.iterator().next()));
  }
}
