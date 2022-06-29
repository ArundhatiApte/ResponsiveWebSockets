package rws.common.responsiveWebSocketConnection.impl;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

import rws.common.responsiveWebSocketConnection.impl.modules.timeouts.Timeout;
import rws.common.responsiveWebSocketConnection.impl.modules.timeouts.Timeouts;
import rws.common.responsiveWebSocketConnection.impl.modules.linkedMapWithUint16Key.LinkedMapWithUint16Key;

import rws.common.responsiveWebSocketConnection.impl.ResponsiveWsConnectionImpl;
import rws.common.responsiveWebSocketConnection.impl.EntryAboutPromiseOfBinaryRequest;
import rws.common.responsiveWebSocketConnection.impl.RejectingFutureOnTimeoutFn;

final class PreparingToSendBinaryRequestFn {
  public static final PreparingToSendBinaryRequestFn instance = new PreparingToSendBinaryRequestFn();

  private PreparingToSendBinaryRequestFn() {}

  public char apply(
    ResponsiveWsConnectionImpl responsiveWebSocketConnection,
    CompletableFuture<ByteBuffer> gettingResponse,
    int maxTimeMsToWaitResponse
  ) {
    char idOfRequest = responsiveWebSocketConnection._generatorOfUint16RequestId.getNextInt();

    LinkedMapWithUint16Key<EntryAboutPromiseOfBinaryRequest> idOfRequestToPromise =
      responsiveWebSocketConnection._idOfRequestToPromise;

    Timeout timeout = Timeouts.setTimeout(new RejectingFutureOnTimeoutFn(
      gettingResponse,
      idOfRequestToPromise,
      idOfRequest
    ), maxTimeMsToWaitResponse);

    EntryAboutPromiseOfBinaryRequest entryAboutPromise = new EntryAboutPromiseOfBinaryRequest(
      gettingResponse,
      timeout
    );
    idOfRequestToPromise.put(idOfRequest, entryAboutPromise);
    return idOfRequest;
  }
}
