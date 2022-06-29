package rws.tests.testResponsiveWebSockets.checks.sendingMessages;

import java.util.concurrent.CompletableFuture;

import scala.collection.mutable.ArrayBuffer;
import scala.collection.mutable.Map;

import rws.common.responsiveWebSocketConnection.api.ResponsiveWsConnection;

import rws.tests.utils.execOrReject;

import rws.tests.testResponsiveWebSockets.checks.utils.timeouts;
import rws.tests.testResponsiveWebSockets.checks.utils.timeouts.Timeout;
import rws.tests.testResponsiveWebSockets.checks.utils.createTimeoutForPromise;

abstract class CheckingSendingFragmentsOfRequestAndFragmentsOfResponseFn[Content] extends Function2[
  ResponsiveWsConnection,
  ResponsiveWsConnection,
  CompletableFuture[Void]
] {
  protected type RWSC = ResponsiveWsConnection;

  override def apply(sender: RWSC, receiver: RWSC): CompletableFuture[Void] = {
    val checking = new CompletableFuture[Void]();
    val timeoutForCheck = createTimeoutForPromise(checking, 2000);

    val fullRequest = _getFullRequest();
    val fragmentsOfRequest = _getFragmentsOfRequest();
    val fullResponse = _getFullResponse();
    val fragmentsOfResponse = _getFragmentsOfResponse();
    val startIndexOfBodyInResponse = _getStartIndexOfBodyInResponse(sender);

    receiver.setEventsListener(_createEventsListener(fullRequest, timeoutForCheck, checking, fragmentsOfResponse));
    _sendRequestThenCheckResponse(sender, fragmentsOfRequest, fullResponse, startIndexOfBodyInResponse, timeoutForCheck, checking);
    checking;
  }

  protected def _getFullRequest(): Content;
  protected def _getFragmentsOfRequest(): Array[Content];
  protected def _getFullResponse(): Content;
  protected def _getFragmentsOfResponse(): Array[Content];
  protected def _getStartIndexOfBodyInResponse(sender: RWSC): Int;

  protected def _createEventsListener(
    fullRequest: Content,
    timeoutForCheck: Timeout,
    checking: CompletableFuture[Void],
    fragmentsOfResponse: Array[Content]
  ): ResponsiveWsConnection.EventsListener;

  private def _sendRequestThenCheckResponse(
    sender: RWSC,
    fragmentsOfRequest: Array[Content],
    fullResponse: Content,
    startIndexOfBodyInResponse: Int,
    timeoutForCheck: Timeout,
    checking: CompletableFuture[Void]
  ): Unit = {
    _sendFragmentsOfRequest(sender, fragmentsOfRequest).handleAsync((response, error) => {
      timeouts.clearTimeout(timeoutForCheck);
      if (error != null) {
        checking.completeExceptionally(error);
      } else {
        if (_areMessagesEqual(fullResponse, 0, response, startIndexOfBodyInResponse)) {
          checking.complete(null);
        } else {
          checking.completeExceptionally(new RuntimeException("Different responses."));
        }
      }
    });
  }

  protected def _sendFragmentsOfRequest(sender: RWSC, fragmentsOfRequest: Array[Content]): CompletableFuture[Content];
  protected def _areMessagesEqual(a: Content, startIndexInA: Int, b: Content, startIndexInB: Int): Boolean;
}
