package rws.tests.utils;

import java.util.concurrent.CompletableFuture;

final object await {
  def apply[T](promise: CompletableFuture[T]): T = { promise.join(); }
}
