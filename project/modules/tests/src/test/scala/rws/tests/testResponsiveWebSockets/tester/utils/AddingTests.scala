package rws.tests.testResponsiveWebSockets.tester;

import java.util.concurrent.CompletableFuture;

protected trait AddingTests {
  def addAsyncTest(name: String, fnForTest: () => CompletableFuture[Void]): Unit;
}
