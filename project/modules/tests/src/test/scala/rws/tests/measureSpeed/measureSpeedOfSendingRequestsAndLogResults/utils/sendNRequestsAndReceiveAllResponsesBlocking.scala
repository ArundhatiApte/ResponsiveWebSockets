package rws.tests.measureSpeed;

import java.util.concurrent.CompletableFuture;

import rws.common.responsiveWebSocketConnection.api.{ResponsiveWsConnection => Rwsc};

import rws.tests.utils.await;

final object sendNRequestsAndReceiveAllResponsesBlocking {
  def apply[Content](
    sender: Rwsc,
    receiver: Rwsc,
    totalCountOfRequestsToSend: Int,
    createMessageByNumberOfRequest: (Int) => Content,
    sendRequestAndReceiveResponse: (Rwsc, Content) => CompletableFuture[_]
  ): Unit = {
    val maxCountOfRequestsAtOnce = _maxCountOfRequestsAtOnce;
    var countOfRequestsToSend = totalCountOfRequestsToSend;

    if (countOfRequestsToSend > 0) {
      if (countOfRequestsToSend > maxCountOfRequestsAtOnce) {
        var countOfRequestsToSendNow = maxCountOfRequestsAtOnce;
        await(_sendRequestsAndReceiveAllResponses[Content](
          sender,
          receiver,
          countOfRequestsToSendNow,
          createMessageByNumberOfRequest,
          sendRequestAndReceiveResponse
        ));
        countOfRequestsToSend -= countOfRequestsToSendNow;
      } else {
        await(_sendRequestsAndReceiveAllResponses[Content](
          sender,
          receiver,
          countOfRequestsToSend,
          createMessageByNumberOfRequest,
          sendRequestAndReceiveResponse
        ));
        return;
      }
    }
  }

  private val _maxCountOfRequestsAtOnce = { (Math.pow(2, 16) - 1).toInt; }

  private def _sendRequestsAndReceiveAllResponses[Content](
    sender: Rwsc,
    receiver: Rwsc,
    countOfRequestsToSend: Int,
    createMessageByNumberOfRequest: (Int) => Content,
    sendRequestAndReceiveResponse: (Rwsc, Content) => CompletableFuture[_],
  ): CompletableFuture[_] = {
    val sendingRequests = new Array[CompletableFuture[_]](countOfRequestsToSend);

    for (i <- 0 to (countOfRequestsToSend - 1)) {
      sendingRequests(i) = sendRequestAndReceiveResponse(sender, createMessageByNumberOfRequest(i));
    }
    CompletableFuture.allOf(sendingRequests: _*);
  }
}
