package rpi.serverless.scheduler;

import at.uibk.dps.ee.guice.starter.VertxProvider;
import at.uibk.dps.ee.model.graph.SpecificationProvider;
import at.uibk.dps.sc.core.capacity.CapacityCalculator;
import at.uibk.dps.sc.core.scheduler.SchedulerAbstract;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Scheduler respecting edge resources.
 *
 * @author Lukas Dötlinger
 */
@Singleton
public class EdgeScheduler extends SchedulerAbstract {

  @Inject
  public EdgeScheduler(final SpecificationProvider specProvider, final CapacityCalculator capacityCalculator,
                       final VertxProvider vProv) {
    super(specProvider, capacityCalculator, vProv);
  }

  @Override
  protected Set<Mapping<Task, Resource>> chooseMappingSubset(Task task, Set<Mapping<Task, Resource>> mappingOptions) {
    final List<Mapping<Task, Resource>> mappingList = new ArrayList<>(mappingOptions);
    Random random = new Random();
    return IntStream.generate(() -> random.nextInt(mappingOptions.size())).limit(10)
      .boxed().map(mappingList::get).collect(Collectors.toSet());
  }
}
