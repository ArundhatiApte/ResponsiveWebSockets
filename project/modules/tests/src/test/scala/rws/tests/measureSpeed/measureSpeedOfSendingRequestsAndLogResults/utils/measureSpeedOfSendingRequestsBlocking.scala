package rws.tests.measureSpeed;

import java.util.concurrent.CompletableFuture;

import rws.common.responsiveWebSocketConnection.api.{ResponsiveWsConnection => Rwsc};

import rws.tests.utils.await;
import rws.tests.measureSpeed.sendNRequestsAndReceiveAllResponsesBlocking;

final object measureSpeedOfSendingRequestsBlocking {
  def apply[Content](
    sender: Rwsc,
    receiver: Rwsc,
    countOfRequests: Int,
    createMessageByNumberOfRequest: (Int) => Content,
    sendRequestAndReceiveResponse: (Rwsc, Content) => CompletableFuture[_]
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
