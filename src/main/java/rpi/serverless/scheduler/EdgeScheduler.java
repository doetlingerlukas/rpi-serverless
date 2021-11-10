package rpi.serverless.scheduler;

import at.uibk.dps.ee.model.graph.SpecificationProvider;
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
  public EdgeScheduler(final SpecificationProvider specProvider) {
    super(specProvider);
  }

  @Override
  protected Set<Mapping<Task, Resource>> chooseMappingSubset(Task task, Set<Mapping<Task, Resource>> mappingOptions) {
    super.specification.getMappings().forEach(m -> {
      System.out.println(m);
    });

    final List<Mapping<Task, Resource>> mappingList = new ArrayList<>(mappingOptions);
    Random random = new Random();
    return IntStream.generate(() -> random.nextInt(mappingOptions.size())).limit(10)
      .boxed().map(mappingList::get).collect(Collectors.toSet());
  }
}
