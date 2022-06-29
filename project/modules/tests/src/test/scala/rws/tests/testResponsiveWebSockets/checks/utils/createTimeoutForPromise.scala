package rws.tests.testResponsiveWebSockets.checks.utils;

import java.util.concurrent.CompletableFuture;

import rws.tests.testResponsiveWebSockets.checks.utils.timeouts;
import rws.tests.testResponsiveWebSockets.checks.utils.timeouts.Timeout;

final object createTimeoutForPromise {
  def apply(promise: CompletableFuture[Void], timeMs: Int = 2000): Timeout = {
    timeouts.setTimeout(new Runnable() {
      override def run() {
        promise.completeExceptionally(new RuntimeException("Timeout for test."));
      }
    }, timeMs);
  }
}
