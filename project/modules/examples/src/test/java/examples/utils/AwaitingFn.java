package rws.examples.utils;

import java.util.concurrent.CompletableFuture;

public final class AwaitingFn {
  public static <T> T await(CompletableFuture<T> cf) {
    return cf.join();
  }
}
