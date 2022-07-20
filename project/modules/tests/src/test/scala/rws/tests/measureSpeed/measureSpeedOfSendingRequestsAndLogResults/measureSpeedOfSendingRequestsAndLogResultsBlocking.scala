package rws.tests.measureSpeed;

import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

import rws.common.responsiveWebSocketConnection.api.{ResponsiveWsConnection => Rwsc};
import rws.common.responsiveWebSocketConnection.api.ResponseSender;

import rws.tests.utils.VoidEventsListener;
import rws.tests.measureSpeed.logger;
import rws.tests.measureSpeed.sendNRequestsAndReceiveAllResponsesBlocking;
import rws.tests.measureSpeed.measureSpeedOfSendingRequestsBlocking;

final object measureSpeedOfSendingRequestsAndLogResultsBlocking {
  def apply(
    serverConnection: Rwsc,
    clientConnection: Rwsc,
    countOfRequests: Int,
    writableStream: PrintStream
  ): Unit = {
    logger.writeHeader(writableStream, countOfRequests);

    val createMessageByNumberOfRequest = (num: Int) => {
      ByteBuffer.wrap(Array[Byte](num.toByte, (num * 2).toByte, (num * 3).toByte, (num * 4).toByte));
    };
    val sendBinaryRequestAndReceiveResponse = (connection: Rwsc, message: ByteBuffer) => {
      connection.sendBinaryRequest(message);
    };

    val sendingBinaryResponseListener = new VoidEventsListener() {
      override def onBinaryRequest(c: Rwsc, m: ByteBuffer, startIndex: Int, r: ResponseSender): Unit = {
        val bytes = m.array();
        r.sendBinaryResponse(ByteBuffer.wrap(
          Array[Byte](bytes(startIndex + 3), bytes(startIndex + 2), bytes(startIndex + 1), bytes(startIndex))
        ));
      }
    };
    val setSendingResponseListenerOfRequest: (Rwsc) => Unit = (connection: Rwsc) => {
      connection.setEventsListener(sendingBinaryResponseListener);
    };

    val casesForBinaryRequests = Array(
      new _Case(logger.labelsOfDirection.fromServerToClient, serverConnection, clientConnection),
      new _Case(logger.labelsOfDirection.fromClientToServer, clientConnection, serverConnection)
    );
    _sendRequestsAndLogResultsBlocking[ByteBuffer](
      countOfRequests,
      casesForBinaryRequests,
      createMessageByNumberOfRequest,
      sendBinaryRequestAndReceiveResponse,
      setSendingResponseListenerOfRequest,
      writableStream
    );
  }

  private final class _Case(labelOfDirectionI: String, senderI: Rwsc, receiverI: Rwsc) {
    val labelOfDirection = labelOfDirectionI;
    val sender = senderI;
    val receiver = receiverI;
  }

  private def _sendRequestsAndLogResultsBlocking[Content](
    countOfRequests: Int,
    casesForRequests: Array[_Case],
    createMessageByNumberOfRequest: (Int) => Content,
    sendRequestAndReceiveResponse: (Rwsc, Content) => CompletableFuture[_],
    setSendingResponseListenerOfRequest: (Rwsc) => Unit,
    writableStream: PrintStream
  ): Unit = {
    for (config <- casesForRequests) {
      _measureSpeedOfSendingRequestsBlockingAndLogResult[Content](
        config.sender,
        config.receiver,
        countOfRequests,
        createMessageByNumberOfRequest,
        sendRequestAndReceiveResponse,
        setSendingResponseListenerOfRequest,
        writableStream,
        config.labelOfDirection
      );
    }
  }

  private def _measureSpeedOfSendingRequestsBlockingAndLogResult[Content](
    sender: Rwsc,
    receiver: Rwsc,
    countOfRequests: Int,
    createMessageByNumberOfRequest: (Int) => Content,
    sendRequestAndReceiveResponse: (Rwsc, Content) => CompletableFuture[_],
    setSendingResponseListenerOfRequest: (Rwsc) => Unit,
    writableStream: PrintStream,
    direction: String
  ): Unit = {
    setSendingResponseListenerOfRequest(receiver);
    val countOfCallsForMethodCompilationInJVM = 10_000;
    sendNRequestsAndReceiveAllResponsesBlocking[Content](
      sender,
      receiver,
      countOfCallsForMethodCompilationInJVM,
      createMessageByNumberOfRequest,
      sendRequestAndReceiveResponse
    );
    val timeMs = measureSpeedOfSendingRequestsBlocking[Content](
      sender,
      receiver,
      countOfRequests,
      createMessageByNumberOfRequest,
      sendRequestAndReceiveResponse
    );
    logger.writeRowWithResult(writableStream, direction, timeMs);
  }
}
