package rws.common.responsiveWebSocketConnection.impl;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

import rws.common.responsiveWebSocketConnection.impl.modules.timeouts.Timeout;

final class EntryAboutPromiseOfBinaryRequest {
  public EntryAboutPromiseOfBinaryRequest(
    CompletableFuture<ByteBuffer> gettingResponse,
    Timeout timeoutToReceiveResponse
  ) {
    this.gettingResponse = gettingResponse;
    this.timeoutToReceiveResponse = timeoutToReceiveResponse;
  }

  public final CompletableFuture<ByteBuffer> gettingResponse;
  public final Timeout timeoutToReceiveResponse;
}
