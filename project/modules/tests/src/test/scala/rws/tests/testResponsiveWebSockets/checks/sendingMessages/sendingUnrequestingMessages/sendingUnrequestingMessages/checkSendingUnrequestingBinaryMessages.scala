package rws.tests.testResponsiveWebSockets.checks.sendingMessages;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

import scala.collection.mutable.ArrayBuffer;

import rws.common.responsiveWebSocketConnection.api.{ResponsiveWsConnection => Rwsc};

import rws.tests.utils.execOrReject;
import rws.tests.utils.HolderOfValue;
import rws.tests.utils.VoidEventsListener;
import rws.tests.utils.timeouts;
import rws.tests.utils.timeouts.Timeout;

import rws.tests.testResponsiveWebSockets.checks.sendingMessages.utils.createByteBufferFromUint8s;
import rws.tests.testResponsiveWebSockets.checks.sendingMessages.СheckingSendingUnrequestingMessagesFn;

final object checkSendingUnrequestingBinaryMessages extends СheckingSendingUnrequestingMessagesFn[ByteBuffer] {
  override def _createSendedMessages(): Array[ByteBuffer] = {
    Array(
      createByteBufferFromUint8s(1),
      createByteBufferFromUint8s(1, 2),
      createByteBufferFromUint8s(1, 2, 3, 4, 5, 6, 7, 8),
      createByteBufferFromUint8s(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)
    );
  }

  override def _createEventsListener(
    countOfMessagesHolder: HolderOfValue[Int],
    receivedMessages: ArrayBuffer[ByteBuffer],
    timeoutForCheck: Timeout,
    sendedMessages: Array[ByteBuffer],
    waitingToSendAllMessages: CountDownLatch,
    checking: CompletableFuture[Void]
  ): Rwsc.EventsListener = {
    new VoidEventsListener() {
      override def onUnrequestingBinaryMessage(c: Rwsc, messageWithHeader: ByteBuffer, startIndex: Int): Unit = {
        execOrReject(() => {
          messageWithHeader.position(startIndex);
          _addMessageThenCompareIfAll(
            countOfMessagesHolder,
            messageWithHeader,
            receivedMessages,
            timeoutForCheck,
            sendedMessages,
            waitingToSendAllMessages,
            checking
          );
        }, checking);
      }
    }
  }

  override def _sendUnrequestingMessage(sender: Rwsc, message: ByteBuffer): Unit = {
    sender.sendUnrequestingBinaryMessage(message);
    message.rewind();
  }
}
