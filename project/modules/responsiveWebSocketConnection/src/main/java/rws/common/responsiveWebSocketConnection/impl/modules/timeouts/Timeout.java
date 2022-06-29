package rws.common.responsiveWebSocketConnection.impl.modules.timeouts;

import java.util.concurrent.ScheduledFuture;

public final class Timeout {
  protected Timeout(ScheduledFuture task) {
    this._task = task;
  }

  protected final ScheduledFuture _task;
}
