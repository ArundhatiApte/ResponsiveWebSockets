package rws.tests.testResponsiveWebSockets.checks.sendingMessages;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import rws.common.responsiveWebSocketConnection.api.ResponsiveWsConnection;
import rws.common.responsiveWebSocketConnection.api.ResponseSender;

import rws.tests.utils.execOrReject;
import rws.tests.utils.VoidEventsListener;

import rws.tests.testResponsiveWebSockets.checks.sendingMessages.CheckingSendingManyBinaryRequestsAtOnceFn;

final object checkSendingManyBinaryRequestsAtOnce extends CheckingSendingManyBinaryRequestsAtOnceFn[ByteBuffer] {
  override def _createSendingResponseEventsListener(): ResponsiveWsConnection.EventsListener = {
    new VoidEventsListener() {
      override def onBinaryRequest(
        c: RWSC,
        messageWithHeader: ByteBuffer,
        startIndex: Int,
        responseSender: ResponseSender
      ): Unit = {
        val int = utilsForByteBuffer.getInt32FromByteBuffer(messageWithHeader, startIndex);
        responseSender.sendBinaryResponse(utilsForByteBuffer.createByteBufferWithInt32(_createExpectedResponse(int)));
      }
    };
  }

  override def _getStartIndexOfBodyInResponse(sender: RWSC): Int = {
    sender.getStartIndexOfBodyInBinaryResponse();
  }

  override def _sendRequest(sender: RWSC, message: Int): CompletableFuture[ByteBuffer] = {
    sender.sendBinaryRequest(utilsForByteBuffer.createByteBufferWithInt32(message));
  }

  override def _extractMessageFromResponse(response: ByteBuffer, startIndexOfBodyInResponse:  Int): Int = {
    utilsForByteBuffer.getInt32FromByteBuffer(response, startIndexOfBodyInResponse);
  }

  private final object utilsForByteBuffer {
    def getInt32FromByteBuffer(byteBuffer: ByteBuffer, startIndex: Int): Int = {
      byteBuffer.getInt(startIndex);
    }

    def createByteBufferWithInt32(int: Int): ByteBuffer = {
      val sizeOfInt32 = 4;
      val out = ByteBuffer.allocate(sizeOfInt32);
      out.putInt(0, int);
    }
  }
}
