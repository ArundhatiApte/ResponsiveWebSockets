package rws.tests.measureSpeed;

import java.util.concurrent.CompletableFuture;

import rws.common.responsiveWebSocketConnection.api.ResponsiveWsConnection;

import rws.tests.utils.await;
import rws.tests.measureSpeed.sendNRequestsAndReceiveAllResponsesBlocking;

final object measureSpeedOfSendingRequestsBlocking {
  private type RWSC = ResponsiveWsConnection;

  def apply[Content](
    sender: RWSC,
    receiver: RWSC,
    countOfRequests: Int,
    createMessageByNumberOfRequest: (Int) => Content,
    sendRequestAndReceiveResponse: (RWSC, Content) => CompletableFuture[_]
  ): Int = {
    val timeOfStart = _now();

    sendNRequestsAndReceiveAllResponsesBlocking(
      sender,
      receiver,
      countOfRequests,
      createMessageByNumberOfRequest,
      sendRequestAndReceiveResponse
    );

    (_now() - timeOfStart).toInt;
  }

  private def _now(): Long = { System.currentTimeMillis(); }
}
