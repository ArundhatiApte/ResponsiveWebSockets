// ok
package rws.common.responsiveWebSocketConnection.impl;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

import rws.common.webSocketConnection.WebSocketConnection;
import rws.common.responsiveWebSocketConnection.api.ResponsiveWsConnection;
import rws.common.responsiveWebSocketConnection.impl.ResponsiveWsConnectionImpl;

import rws.tests.utils.execOrReject;
import rws.tests.utils.VoidEventsListener;

import rws.tests.testResponsiveWebSockets.checks.utils.timeouts;
import rws.tests.testResponsiveWebSockets.checks.utils.timeouts.Timeout;
import rws.tests.testResponsiveWebSockets.checks.utils.createTimeoutForPromise;

final object checkSendingTextMessage extends Function2[
  ResponsiveWsConnection,
  ResponsiveWsConnection,
  CompletableFuture[Void]
] {
  override def apply(
    sender: ResponsiveWsConnection,
    receiver: ResponsiveWsConnection
  ): CompletableFuture[Void] = {
    val checking = new CompletableFuture[Void]();
    val timeoutForCheck = createTimeoutForPromise(checking);
    val textMessage = "abcd";

    receiver.setEventsListener(new VoidEventsListener() {
      override def onTextMessage(rwsc: ResponsiveWsConnection, message: String): Unit = {
        timeouts.clearTimeout(timeoutForCheck);
        if (message.equals(textMessage)) {
          checking.complete(null);
        } else {
          checking.completeExceptionally(new RuntimeException("Different messages."));
        }
      }
    });

    sender.asInstanceOf[ResponsiveWsConnectionImpl]._webSocketConnection.sendTextMessage(textMessage);
    checking;
  }
}
