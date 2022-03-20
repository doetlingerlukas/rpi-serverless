package rpi.serverless.scheduler;

import at.uibk.dps.ee.core.function.EnactmentFunction;
import at.uibk.dps.ee.enactables.FunctionFactoryUser;
import at.uibk.dps.ee.model.properties.PropertyServiceMapping;
import at.uibk.dps.ee.model.properties.PropertyServiceMapping.EnactmentMode;
import at.uibk.dps.ee.model.properties.PropertyServiceResource;
import at.uibk.dps.sc.core.interpreter.ScheduleInterpreterUser;
import edge.discovery.graph.PsMappingLocalRes;
import com.google.inject.Inject;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;
import org.opt4j.core.common.random.Rand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A schedule interpreter that expects to find multiple resources.
 *
 * @author Lukas Dötlinger
 */
public class EdgeScheduleInterpreterUser extends ScheduleInterpreterUser {

  protected final Rand random;

  private final Logger logger = LoggerFactory.getLogger(EdgeScheduleInterpreterUser.class);

  @Inject
  public EdgeScheduleInterpreterUser(Set<FunctionFactoryUser> userFactories, final Rand random) {
    super(userFactories);
    this.random = random;
  }

  @Override
  protected EnactmentFunction interpretScheduleUser(Task task, Set<Mapping<Task, Resource>> scheduleModel) {
    var nonLocalMappings = scheduleModel.stream()
        .filter(m -> !PropertyServiceMapping.getEnactmentMode(m).equals(PropertyServiceMapping.EnactmentMode.Local))
        .collect(Collectors.toList());

    var edgeMappings = nonLocalMappings.stream()
      .filter(PsMappingLocalRes::isLocResMapping)
      .filter(m -> PropertyServiceResource.getUsingTaskIds(m.getTarget()).size() < 4).findFirst();

    var serverlessMappings = scheduleModel.stream()
      .filter(m -> PropertyServiceMapping.getEnactmentMode(m).equals(EnactmentMode.Serverless))
      .collect(Collectors.toSet());

    var selectedMapping = nonLocalMappings.isEmpty() ?
      scheduleModel.iterator().next() :
      nonLocalMappings.get(random.nextInt(nonLocalMappings.size()));

    return getFunctionForMapping(task, selectedMapping);
  }
}
