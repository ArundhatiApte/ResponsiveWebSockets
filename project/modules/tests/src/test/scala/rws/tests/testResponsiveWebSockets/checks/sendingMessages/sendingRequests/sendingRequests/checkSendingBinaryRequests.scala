package rws.tests.testResponsiveWebSockets.checks.sendingMessages;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

import scala.collection.mutable.ArrayBuffer;
import scala.collection.mutable.Map;

import rws.common.responsiveWebSocketConnection.api.{ResponsiveWsConnection => Rwsc};
import rws.common.responsiveWebSocketConnection.api.ResponseSender;

import rws.tests.utils.VoidEventsListener;

import rws.tests.testResponsiveWebSockets.checks.sendingMessages.utils.createByteBufferFromUint8s;
import rws.tests.testResponsiveWebSockets.checks.sendingMessages.CheckingSendingRequestsFn;

final object checkSendingBinaryRequests extends CheckingSendingRequestsFn[ByteBuffer] {
  override def _getStartIndexOfBodyInResponse(sender: Rwsc): Int = {
    sender.getStartIndexOfBodyInBinaryResponse();
  }

  override def _createSendedMessageToExpectedResponseTable(): Map[ByteBuffer, ByteBuffer] = {
    Map(
      createByteBufferFromUint8s(1) -> createByteBufferFromUint8s(2),
      createByteBufferFromUint8s(1, 2) -> createByteBufferFromUint8s(2, 4),
      createByteBufferFromUint8s(1, 2, 3) -> createByteBufferFromUint8s(2, 4, 6),
      createByteBufferFromUint8s(1, 2, 3, 4) -> createByteBufferFromUint8s(2, 4, 6, 8)
    );
  }

  override def _createSendingResponseEventsListener(): Rwsc.EventsListener = {
    new VoidEventsListener() {
      override def onBinaryRequest(
        c: Rwsc,
        messageWithHeader: ByteBuffer,
        startIndex: Int,
        responseSender: ResponseSender
      ): Unit = {
        messageWithHeader.position(startIndex);
        _multipX2EveryByteInBuffer(messageWithHeader);
        responseSender.sendBinaryResponse(messageWithHeader);
      }
    };
  }

  private def _multipX2EveryByteInBuffer(buffer: ByteBuffer): Unit = {
    for (i <- 0 to (buffer.limit() - 1)) {
      buffer.put(i, (buffer.get(i) * 2).toByte);
    }
  }

  override def _sendMessageToReceiverAndAddResponseToMap(
    sender: Rwsc,
    message: ByteBuffer,
    startIndexOfBodyInResponse: Int,
    sendedMessageToReceivedResponse: Map[ByteBuffer, ByteBuffer]
  ): CompletableFuture[Void] = {
    sender.sendBinaryRequest(message).thenAccept((response) => {
      message.rewind();
      response.position(startIndexOfBodyInResponse);
      sendedMessageToReceivedResponse.put(message, response);
    });
  }
}
