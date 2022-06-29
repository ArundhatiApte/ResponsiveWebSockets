package rws.tests.utils;

import java.util.concurrent.CompletableFuture;

final object execOrReject {
  def apply(fn: () => Any, promise: CompletableFuture[_]): Unit = {
    try {
      fn();
    } catch {
      case error: Throwable => promise.completeExceptionally(error);
    }
  }
}
