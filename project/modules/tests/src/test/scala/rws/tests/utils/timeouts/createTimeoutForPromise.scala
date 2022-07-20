package rws.tests.utils;

import java.util.concurrent.CompletableFuture;

import rws.tests.utils.timeouts;
import rws.tests.utils.timeouts.Timeout;

final object createTimeoutForPromise {
  def apply(promise: CompletableFuture[Void], timeMs: Int = 2000): Timeout = {
    timeouts.setTimeout(new Runnable() {
      override def run() {
        promise.completeExceptionally(new RuntimeException("Timeout for test."));
      }
    }, timeMs);
  }
}
