package rws.tests.testResponsiveWebSockets.checks;

import java.util.concurrent.CompletableFuture;

import rws.common.responsiveWebSocketConnection.api.{ResponsiveWsConnection => Rwsc};

import rws.tests.utils.execOrReject;
import rws.tests.utils.VoidEventsListener;
import rws.tests.utils.timeouts.Timeout;
import rws.tests.utils.createTimeoutForPromise;

final object checkClosingConnection {
  def apply(
    closingConnectinSide: Rwsc,
    acceptingEventSide: Rwsc
  ): CompletableFuture[Void] = {
    val checking = new CompletableFuture[Void]();
    val timeoutForCheck = createTimeoutForPromise(checking);
    val code = 4000;
    val reason = "message"

    acceptingEventSide.setEventsListener(new VoidEventsListener() {
      override def onClose(c: Rwsc, eCode: Int, eReason: String): Unit = {
        execOrReject(() => {
          if (code == eCode && reason == eReason) {
            checking.complete(null);
          } else {
            checking.completeExceptionally(new RuntimeException(
              "(" + code + ", " + reason + ") != (" + eCode + ", " + eReason + ")"
            ));
          }
        }, checking);
      }
    });

    closingConnectinSide.close(code, reason);
    checking;
  }
}
