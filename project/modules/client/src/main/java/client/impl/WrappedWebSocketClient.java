package rws.client.impl;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.Map;

import org.java_websocket.drafts.Draft;
import org.java_websocket.exceptions.InvalidDataException;

import rws.common.responsiveWebSocketConnection.api.ResponsiveWsConnection;
import rws.common.responsiveWebSocketConnection.impl.ResponsiveWsConnectionImpl;
import static rws.common.webSocketConnection.WebSocketConnection.EventsListener;

import rws.client.api.ResponsiveWsClient;
import rws.client.impl.ResponsiveWsClientImpl;
import rws.client.impl.WrapperOfWebSocketClient;

final class WrappedWebSocketClient extends org.java_websocket.client.WebSocketClient {
  WrappedWebSocketClient(WrapperOfWebSocketClient wrapper, URI uri) {
    super(uri);
    this._wrapper = wrapper;
  }

  WrappedWebSocketClient(WrapperOfWebSocketClient wrapper, URI uri, Draft protocolDraft) {
    super(uri, protocolDraft);
    this._wrapper = wrapper;
  }

  private final WrapperOfWebSocketClient _wrapper;
  protected CompletableFuture<Void> _futureOfConnecting;

   @Override
  public void onOpen(org.java_websocket.handshake.ServerHandshake serverHandshake) {
    WrapperOfWebSocketClient wrapper = this._wrapper;
    CompletableFuture<Void> futureOfConnecting = this._futureOfConnecting;
    if (futureOfConnecting != null) {
      this._futureOfConnecting.complete(null);
      this._futureOfConnecting = null;
    }
  }

  @Override
  public void onMessage(ByteBuffer message) {
    WrapperOfWebSocketClient wrapper = this._wrapper;
    wrapper._eventsListener.onBinaryMessage(wrapper, message);
  }

  @Override
  public void onMessage(String message) {
    WrapperOfWebSocketClient wrapper = this._wrapper;
    wrapper._eventsListener.onTextMessage(wrapper, message);
  }

  @Override
  public void onError(Exception error) {
    _emitError(this, error);
  }

  private static final void _emitError(WrappedWebSocketClient that, Exception error) {
    CompletableFuture<Void> cf = that._futureOfConnecting;
    if (cf != null) {
      cf.completeExceptionally(error);
      that._futureOfConnecting = null;
    }
    WrapperOfWebSocketClient wrapper = that._wrapper;
    EventsListener eventsListener = wrapper._eventsListener;
    if (eventsListener != null) {
      eventsListener.onError(wrapper, error);
    }
  }

  @Override
  public void onClose(int code, String reason, boolean isRemote) {
    if (isRemote == false && code == 1002) {
      _emitError(this, new InvalidDataException(code, reason));
    }

    WrapperOfWebSocketClient wrapper = this._wrapper;
    wrapper._eventsListener.onClose(wrapper, code, reason, isRemote);
  }
}
