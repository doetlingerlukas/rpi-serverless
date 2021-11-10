package rpi.serverless.scheduler;

import at.uibk.dps.sc.core.interpreter.ScheduleInterpreterUser;
import at.uibk.dps.sc.core.interpreter.ScheduleInterpreterUserSingle;
import at.uibk.dps.sc.core.modules.SchedulerModule;
import at.uibk.dps.sc.core.scheduler.Scheduler;

/**
 * Configures binding of schedulers related to the experiment.
 *
 * @author Lukas Dötlinger
 */
public class EdgeSchedulerModule extends SchedulerModule {

  @Override
  protected void config() {
    bind(ScheduleInterpreterUser.class).to(EdgeScheduleInterpreterUser.class);
    if (schedulingMode.equals(SchedulingMode.Random)) {
      bind(Scheduler.class).to(EdgeScheduler.class);
    }
  }
}
