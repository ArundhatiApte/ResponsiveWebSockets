package rws.tests.testResponsiveWebSockets.checks.sendingMessages;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import scala.collection.mutable.Map;

import rws.common.responsiveWebSocketConnection.api.ResponsiveWsConnection;

import rws.tests.utils.execOrReject;

import rws.tests.testResponsiveWebSockets.checks.utils.timeouts;
import rws.tests.testResponsiveWebSockets.checks.utils.timeouts.Timeout;
import rws.tests.testResponsiveWebSockets.checks.utils.createTimeoutForPromise;

abstract class CheckingSendingRequestsFn[Content] extends Function2[
  ResponsiveWsConnection,
  ResponsiveWsConnection,
  CompletableFuture[Void]
] {
  protected type RWSC = ResponsiveWsConnection;

  override final def apply(sender: RWSC, receiver: RWSC): CompletableFuture[Void] = {
    val checking = new CompletableFuture[Void]();
    val timeoutForCheck = createTimeoutForPromise(checking);

    val startIndexOfBodyInResponse = _getStartIndexOfBodyInResponse(sender);
    val sendedMessageToExpectedResponse = _createSendedMessageToExpectedResponseTable();
    val sendedMessageToReceivedResponse = Map[Content, Content]();
    val pool = new ScheduledThreadPoolExecutor(1);

    pool.execute(() => {
      receiver.setEventsListener(_createSendingResponseEventsListener());

      val sendingAllRequests = _sendRequestsAndAddResponseToMap(
        sender,
        sendedMessageToExpectedResponse,
        startIndexOfBodyInResponse,
        sendedMessageToReceivedResponse
      );

      CompletableFuture.allOf(sendingAllRequests: _*).handleAsync((void, error) => {
        timeouts.clearTimeout(timeoutForCheck);
        if (error != null) {
          checking.completeExceptionally(error);
        } else {
          execOrReject(() => {
            if (sendedMessageToExpectedResponse.equals(sendedMessageToReceivedResponse)) {
              checking.complete(null);
            } else {
              checking.completeExceptionally(new RuntimeException("Different responses."));
            }
          }, checking);
        }
      }, pool);
    });

    checking;
  }

  protected def _getStartIndexOfBodyInResponse(sender: RWSC): Int;
  protected def _createSendedMessageToExpectedResponseTable(): Map[Content, Content];
  protected def _createSendingResponseEventsListener(): ResponsiveWsConnection.EventsListener;

  private def _sendRequestsAndAddResponseToMap(
    sender: RWSC,
    sendedMessageToExpectedResponse: Map[Content, Content],
    startIndexOfBodyInResponse: Int,
    sendedMessageToReceivedResponse: Map[Content, Content]
  ): Array[CompletableFuture[_]] = {
    val out = new Array[CompletableFuture[_]](sendedMessageToExpectedResponse.size);
    var i = 0;

    for (message <- sendedMessageToExpectedResponse.keys) {
      out(i) = _sendMessageToReceiverAndAddResponseToMap(
        sender,
        message,
        startIndexOfBodyInResponse,
        sendedMessageToReceivedResponse
      );
      i += 1;
    }
    out;
  }

  protected def _sendMessageToReceiverAndAddResponseToMap(
    sender: RWSC,
    message: Content,
    startIndexOfBodyInResponse: Int,
    sendedMessageToReceivedResponse: Map[Content, Content]
  ): CompletableFuture[Void];
}
