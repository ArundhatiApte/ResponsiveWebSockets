package rws.common.responsiveWebSocketConnection.impl.modules.timeouts;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import rws.common.responsiveWebSocketConnection.impl.modules.timeouts.Timeout;

public final class Timeouts {
  public static Timeout setTimeout(Runnable fn, int milliseconds) {
    final ScheduledFuture task = _executor.schedule(fn, milliseconds, TimeUnit.MILLISECONDS);
    return new Timeout(task);
  }

  public static void clearTimeout(Timeout timeout) {
    final boolean mayInterruptIfRunning = false;
    timeout._task.cancel(mayInterruptIfRunning);
  }

  private static final ScheduledThreadPoolExecutor _executor = new ScheduledThreadPoolExecutor(1);
}
