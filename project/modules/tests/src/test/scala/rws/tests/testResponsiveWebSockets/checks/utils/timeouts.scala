package rws.tests.testResponsiveWebSockets.checks.utils;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

final object timeouts {
  type Timeout = ScheduledFuture[_];

  def setTimeout(fn: Runnable, milliseconds: Int) = {
    _executor.schedule(fn, milliseconds, TimeUnit.MILLISECONDS);
  }

  def clearTimeout(timeout: Timeout): Unit = { timeout.cancel(false); }

  private val _executor: ScheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
  _executor.setRemoveOnCancelPolicy(true);
}
