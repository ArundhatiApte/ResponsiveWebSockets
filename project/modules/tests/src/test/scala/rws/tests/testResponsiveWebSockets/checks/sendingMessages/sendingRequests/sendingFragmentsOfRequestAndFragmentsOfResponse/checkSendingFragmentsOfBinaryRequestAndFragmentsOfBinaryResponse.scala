package rws.tests.testResponsiveWebSockets.checks.sendingMessages;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

import scala.collection.mutable.Map;

import rws.common.responsiveWebSocketConnection.api.{ResponsiveWsConnection => Rwsc};
import rws.common.responsiveWebSocketConnection.api.ResponseSender;

import rws.tests.utils.execOrReject;
import rws.tests.utils.VoidEventsListener;
import rws.tests.utils.timeouts;
import rws.tests.utils.timeouts.Timeout;

import rws.tests.testResponsiveWebSockets.checks.sendingMessages.utils.createByteBufferFromUint8s;
import rws.tests.testResponsiveWebSockets.checks.sendingMessages.CheckingSendingFragmentsOfRequestAndFragmentsOfResponseFn;

final object checkSendingFragmentsOfBinaryRequestAndFragmentsOfBinaryResponse extends
  CheckingSendingFragmentsOfRequestAndFragmentsOfResponseFn[ByteBuffer]
{
  override def _getFullRequest(): ByteBuffer = {
    createByteBufferFromUint8s(
      1, 2, 3, 4,
      99, 88, 77, 66, 55, 44,
    )
  }

  override def _getFragmentsOfRequest(): Array[ByteBuffer] = {
    Array(
      createByteBufferFromUint8s(1, 2, 3, 4),
      createByteBufferFromUint8s(99, 88, 77, 66, 55, 44)
    );
  }

  override def _getFullResponse(): ByteBuffer = {
    createByteBufferFromUint8s(127, 126, 125, 124, 123, 122);
  }

  override def _getFragmentsOfResponse(): Array[ByteBuffer] = {
    Array(
      createByteBufferFromUint8s(127, 126),
      createByteBufferFromUint8s(125, 124, 123, 122)
    );
  }

  override def _getStartIndexOfBodyInResponse(sender: Rwsc): Int = {
    sender.getStartIndexOfBodyInBinaryResponse();
  }

  override def _createEventsListener(
    fullRequest: ByteBuffer,
    timeoutForCheck: Timeout,
    checking: CompletableFuture[Void],
    fragmentsOfResponse: Array[ByteBuffer]
  ): Rwsc.EventsListener = {
    new VoidEventsListener() {
      override def onBinaryRequest(
        c: Rwsc,
        messageWithHeader: ByteBuffer,
        startIndex: Int,
        responseSender: ResponseSender
      ): Unit = {
        execOrReject(() => {
          messageWithHeader.position(startIndex);
          if (_areMessagesEqual(fullRequest, 0, messageWithHeader, startIndex)) {
            responseSender.sendFragmentsOfBinaryResponse(fragmentsOfResponse: _*);
          } else {
            timeouts.clearTimeout(timeoutForCheck);
            checking.completeExceptionally(new RuntimeException("Different requests."));
          }
        }, checking);
      };
    };
  }

  override def _sendFragmentsOfRequest(sender: Rwsc, fragmentsOfRequest: Array[ByteBuffer]): CompletableFuture[ByteBuffer] = {
    sender.sendFragmentsOfBinaryRequest(fragmentsOfRequest: _*);
  }

  override def _areMessagesEqual(a: ByteBuffer, startIndexInA: Int, b: ByteBuffer, startIndexInB: Int): Boolean = {
    // ok, a and b are never be used in other place
    a.position(startIndexInA);
    b.position(startIndexInB);
    a.equals(b);
  }
}
