// ok
package rws.common.responsiveWebSocketConnection.impl;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

import rws.common.webSocketConnection.WebSocketConnection;
import rws.common.responsiveWebSocketConnection.api.ResponsiveWsConnection;
import rws.common.responsiveWebSocketConnection.impl.ResponsiveWsConnectionImpl;

import rws.tests.utils.execOrReject;
import rws.tests.utils.HolderOfValue;
import rws.tests.utils.VoidEventsListener;

import rws.tests.testResponsiveWebSockets.checks.utils.timeouts;
import rws.tests.testResponsiveWebSockets.checks.utils.timeouts.Timeout;
import rws.tests.testResponsiveWebSockets.checks.utils.createTimeoutForPromise;
import rws.tests.testResponsiveWebSockets.checks.sendingMessages.utils.createByteBufferFromUint8s;

final object checkSendingMalformedBinaryMessages extends Function2[
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

    val malformedMessages = _createMalformedMessages();
    val holderOfMalformedMessagesToReceiveCount = new HolderOfValue[Int](malformedMessages.length);

    receiver.setEventsListener(this._createEventsListener(
      malformedMessages,
      holderOfMalformedMessagesToReceiveCount,
      timeoutForCheck,
      checking
    ));

    val webSocketConnection = sender.asInstanceOf[ResponsiveWsConnectionImpl]._webSocketConnection;
    _sendMalformedMessages(webSocketConnection, malformedMessages);
    checking;
  }

  def _createMalformedMessages(): Array[ByteBuffer] = Array[ByteBuffer](
    createByteBufferFromUint8s(1),
    createByteBufferFromUint8s(1, 9),

    createByteBufferFromUint8s(2),
    createByteBufferFromUint8s(2, 9),

    createByteBufferFromUint8s(13, 2, 1, 0, 2, 3)
  )

  def _createEventsListener(
    malformedMessages: Array[ByteBuffer],
    holderOfMalformedMessagesToReceiveCount: HolderOfValue[Int],
    timeoutForCheck: Timeout,
    checking: CompletableFuture[Void]
  ): ResponsiveWsConnection.EventsListener = {
    return new VoidEventsListener() {
      override def onMalformedBinaryMessage(rwsc: ResponsiveWsConnection, message: ByteBuffer): Unit = {
        execOrReject(() => {
          var countOfMalformedMessagesToReceive = holderOfMalformedMessagesToReceiveCount.get();
          countOfMalformedMessagesToReceive -= 1;

          if (countOfMalformedMessagesToReceive == 0) {
            timeouts.clearTimeout(timeoutForCheck);
            checking.complete(null);
          } else {
            holderOfMalformedMessagesToReceiveCount.set(countOfMalformedMessagesToReceive);
          }
        }, checking);
      }
    };
  }

  def _sendMalformedMessages(webSocketConnection: WebSocketConnection, malformedMessages: Array[ByteBuffer]): Unit = {
    val isFin = true;
    for (message <- malformedMessages) {
      webSocketConnection.sendBinaryFragment(message, isFin);
    }
  }
}
