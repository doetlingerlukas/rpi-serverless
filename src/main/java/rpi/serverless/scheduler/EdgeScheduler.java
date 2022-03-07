package rpi.serverless.scheduler;

import at.uibk.dps.ee.guice.starter.VertxProvider;
import at.uibk.dps.ee.model.graph.SpecificationProvider;
import at.uibk.dps.ee.model.properties.*;
import at.uibk.dps.sc.core.capacity.CapacityCalculator;
import at.uibk.dps.sc.core.scheduler.SchedulerAbstract;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import edge.discovery.graph.PsMappingLocalRes;
import io.vertx.core.Vertx;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;
import org.opt4j.core.common.random.Rand;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Scheduler respecting edge resources.
 *
 * @author Lukas Dötlinger
 */
@Singleton
public class EdgeScheduler extends SchedulerAbstract {
  
  protected final Rand random;

  @Inject
  public EdgeScheduler(final SpecificationProvider specProvider, final CapacityCalculator capacityCalculator,
      final VertxProvider vProv, final Rand random) {
    super(specProvider, capacityCalculator, vProv);
    this.random = random;
  }

  @Override
  protected Set<Mapping<Task, Resource>> chooseMappingSubset(Task task, Set<Mapping<Task, Resource>> mappingOptions) {

      final List<Mapping<Task, Resource>> mappingList = mappingOptions.stream()
        .filter(mapping ->
          !PropertyServiceMapping.getEnactmentMode(mapping).equals(PropertyServiceMapping.EnactmentMode.Local))
        .filter(this::isCapacityMapping)
        .collect(Collectors.toList());

      if (false) {
        final int idx = random.nextInt(mappingList.size());
        final Set<Mapping<Task, Resource>> result = new HashSet<>();
        result.add(mappingList.get(idx));
        return result;
      } else {
        return mappingOptions;
      }
  }

  /**
   * Returns true iff the given mapping is affected by resource capacity.
   *
   * @param mapping the given mapping
   * @return true iff the given mapping is affected by resource capacity
   */
  protected boolean isCapacityMapping(final Mapping<Task, Resource> mapping) {
    final Task task = mapping.getSource();
    final Resource res = mapping.getTarget();
    return !PropertyServiceFunction.hasNegligibleWorkload(task)
      && PropertyServiceResource.hasLimitedCapacity(res);
  }
}
