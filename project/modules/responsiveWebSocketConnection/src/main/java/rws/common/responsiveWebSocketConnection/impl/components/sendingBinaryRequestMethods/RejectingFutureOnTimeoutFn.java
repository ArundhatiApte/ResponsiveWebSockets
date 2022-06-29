package rws.common.responsiveWebSocketConnection.impl;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

import rws.common.responsiveWebSocketConnection.api.TimeoutToReceiveResponseException;

import rws.common.responsiveWebSocketConnection.impl.modules.linkedMapWithUint16Key.LinkedMapWithUint16Key;
import rws.common.responsiveWebSocketConnection.impl.EntryAboutPromiseOfBinaryRequest;

final class RejectingFutureOnTimeoutFn implements Runnable {
  public RejectingFutureOnTimeoutFn(
    CompletableFuture<ByteBuffer> gettingResponseBuffer,
    LinkedMapWithUint16Key<EntryAboutPromiseOfBinaryRequest> idOfRequestToPromise,
    char idOfRequest
  ) {
    this._gettingResponseBuffer = gettingResponseBuffer;
    this._idOfRequestToPromise = idOfRequestToPromise;
    this._idOfRequest = idOfRequest;
  }

  private final CompletableFuture<ByteBuffer> _gettingResponseBuffer;
  private final LinkedMapWithUint16Key<EntryAboutPromiseOfBinaryRequest> _idOfRequestToPromise;
  private final char _idOfRequest;

  @Override
  public void run() {
    this._idOfRequestToPromise.getAndRemoveIfHas(this._idOfRequest);
    this._gettingResponseBuffer.completeExceptionally(new TimeoutToReceiveResponseException(
      "ResponsiveWebSocketConnection:: timeout for receiving response."
    ));
  }
}
