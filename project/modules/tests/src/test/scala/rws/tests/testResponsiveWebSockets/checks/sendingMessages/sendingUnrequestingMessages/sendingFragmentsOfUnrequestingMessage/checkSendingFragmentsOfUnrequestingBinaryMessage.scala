package rws.tests.testResponsiveWebSockets.checks.sendingMessages;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

import rws.common.responsiveWebSocketConnection.api.{ResponsiveWsConnection => Rwsc};

import rws.tests.utils.VoidEventsListener;
import rws.tests.utils.timeouts;
import rws.tests.utils.timeouts.Timeout;

import rws.tests.testResponsiveWebSockets.checks.sendingMessages.utils.createByteBufferFromUint8s;
import rws.tests.testResponsiveWebSockets.checks.sendingMessages.CheckingSendingFragmentsOfUnrequestingMessageFn;

final object checkSendingFragmentsOfUnrequestingBinaryMessage extends CheckingSendingFragmentsOfUnrequestingMessageFn[
  ByteBuffer
] {
  override def _getFragmentsOfMessage(): Array[ByteBuffer] = {
    Array(
      createByteBufferFromUint8s(1, 2, 3, 4, 5, 6, 7, 8),
      createByteBufferFromUint8s(99, 88, 77, 66),
      createByteBufferFromUint8s(8, 9),
      createByteBufferFromUint8s(0, 0, 0, 0)
    );
  }

  override def _getFullMessage(): ByteBuffer = {
    createByteBufferFromUint8s(
      1, 2, 3, 4, 5, 6, 7, 8,
      99, 88, 77, 66,
      8, 9,
      0, 0, 0, 0
    );
  }

  override def _createEventsListener(
    fullMessage: ByteBuffer,
    timeoutForCheck: Timeout,
    checking: CompletableFuture[Void]
  ): Rwsc.EventsListener = {
    new VoidEventsListener() {
      override def onUnrequestingBinaryMessage(c: Rwsc, messageWithHeader: ByteBuffer, startIndex: Int): Unit = {
        timeouts.clearTimeout(timeoutForCheck);
        messageWithHeader.position(startIndex);
        if (fullMessage.equals(messageWithHeader)) {
          checking.complete(null);
        } else {
          checking.completeExceptionally(new RuntimeException("Different messages."));
        }
      }
    };
  }

  override def _sendFragmentsOfUnrequestingMessage(sender: Rwsc, fragmentsOfMessage: Array[ByteBuffer]): Unit = {
    sender.sendFragmentsOfUnrequestingBinaryMessage(fragmentsOfMessage: _*);
  }
}
