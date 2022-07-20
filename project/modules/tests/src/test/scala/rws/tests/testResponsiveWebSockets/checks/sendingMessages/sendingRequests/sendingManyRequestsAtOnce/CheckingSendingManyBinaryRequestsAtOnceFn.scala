package rws.tests.testResponsiveWebSockets.checks.sendingMessages;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import rws.common.responsiveWebSocketConnection.api.{ResponsiveWsConnection => Rwsc};

import rws.tests.utils.execOrReject;
import rws.tests.utils.timeouts;
import rws.tests.utils.timeouts.Timeout;
import rws.tests.utils.createTimeoutForPromise;

abstract class CheckingSendingManyBinaryRequestsAtOnceFn[Content] extends Function2[
  Rwsc,
  Rwsc,
  CompletableFuture[Void]
] {
  override final def apply(sender: Rwsc, receiver: Rwsc): CompletableFuture[Void] = {
    val checking = new CompletableFuture[Void]();
    val maxTimeMsForCheck = 8000;
    val timeoutForCheck = createTimeoutForPromise(checking, maxTimeMsForCheck);

    val maxTimeMsForWaitingResponse = 7000;
    sender.setMaxTimeMsToWaitResponse(maxTimeMsForWaitingResponse);

    val maxCountOfRequestAtOnce = Math.pow(2, 16).toInt;
    val sendingRequestsAndReceivingResponses = new Array[CompletableFuture[_]](maxCountOfRequestAtOnce - 1);
    val expectedResponses = new Array[Int](maxCountOfRequestAtOnce);
    val receivedResponses = new Array[Int](maxCountOfRequestAtOnce);
    val pool = new ScheduledThreadPoolExecutor(1);
    val startIndexOfBodyInResponse = _getStartIndexOfBodyInResponse(sender);

    pool.execute(() => {
      receiver.setEventsListener(_createSendingResponseEventsListener());

      for (i <- 0 to (maxCountOfRequestAtOnce - 2)) {
        val sendedMessage = _createSendedMessage();
        expectedResponses(i) = _createExpectedResponse(sendedMessage);
        sendingRequestsAndReceivingResponses(i) = _sendRequestAndAddReceivedResponse(
          pool,
          sender,
          sendedMessage,
          startIndexOfBodyInResponse,
          receivedResponses,
          i
        );
      }

      CompletableFuture.allOf(sendingRequestsAndReceivingResponses: _*).handleAsync((void, error) => {
        if (error != null) {
          timeouts.clearTimeout(timeoutForCheck);
          checking.completeExceptionally(error);
        } else {
          _sendOneRequestThenCompareResponses(
            pool,
            sender,
            _createSendedMessage(),
            maxCountOfRequestAtOnce - 1,
            startIndexOfBodyInResponse,
            receivedResponses,
            expectedResponses,
            timeoutForCheck,
            checking
          );
        }
      }, pool);
    });

    checking;
  }

  protected def _createSendingResponseEventsListener(): Rwsc.EventsListener;

  private def _createSendedMessage(): Int = {
    (Math.random() * 100_000).toInt;
  }

  private val _multipler = 4;

  protected final def _createExpectedResponse(n: Int): Int = {
    n * _multipler;
  }

  protected def _getStartIndexOfBodyInResponse(sender: Rwsc): Int;

  private def _sendRequestAndAddReceivedResponse(
    pool: Executor,
    sender: Rwsc,
    message: Int,
    startIndexOfBodyInResponse: Int,
    receivedResponses: Array[Int],
    index: Int
  ): CompletableFuture[_] = {
    val future = new CompletableFuture[Object]();

    _sendRequest(sender, message).handle((response, error) => {
      if (error != null) {
        future.completeExceptionally(error);
      } else {
        receivedResponses(index) = _extractMessageFromResponse(response, startIndexOfBodyInResponse);
        future.complete(null);
      }
    });
    future;
  }

  protected def _sendRequest(sender: Rwsc, message: Int): CompletableFuture[Content];
  protected def _extractMessageFromResponse(response: Content, startIndexOfBodyInResponse: Int): Int;

  private def _sendOneRequestThenCompareResponses(
    pool: Executor,
    sender: Rwsc,
    sendedMessage: Int,
    indexInResponses: Int,
    startIndexOfBodyInResponse: Int,
    receivedResponses: Array[Int],
    expectedResponses: Array[Int],
    timeoutForCheck: Timeout,
    checking: CompletableFuture[Void]
  ): Unit = {
    expectedResponses(indexInResponses) = _createExpectedResponse(sendedMessage);
    _sendRequestAndAddReceivedResponse(
      pool,
      sender,
      sendedMessage,
      startIndexOfBodyInResponse,
      receivedResponses,
      indexInResponses
    ).handleAsync((void, error) => {
      timeouts.clearTimeout(timeoutForCheck);
      if (error != null) {
        checking.completeExceptionally(error);
      } else {
        execOrReject(() => {
          expectedResponses.sortInPlace();
          receivedResponses.sortInPlace();

          if (Arrays.equals(expectedResponses, receivedResponses)) {
            checking.complete(null);
          } else {
            checking.completeExceptionally(new RuntimeException("Different responses."));
          }
        }, checking);
      }
    }, pool);
  }
}
