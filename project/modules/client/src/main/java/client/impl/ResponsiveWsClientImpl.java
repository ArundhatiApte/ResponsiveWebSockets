package rws.client.impl;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;

import rws.common.responsiveWebSocketConnection.impl.ResponsiveWsConnectionImpl;
import rws.common.webSocketConnection.WebSocketConnection;

import rws.client.api.ResponsiveWsClient;
import rws.client.impl.WrapperOfWebSocketClient;
import rws.client.impl.WrappedWebSocketClient;

public final class ResponsiveWsClientImpl extends ResponsiveWsConnectionImpl implements ResponsiveWsClient {
  public ResponsiveWsClientImpl(URI uri) {
    this(new WrapperOfWebSocketClient(uri));
  }

  public ResponsiveWsClientImpl(URI uri, Draft protocolDraft) {
    this(new WrapperOfWebSocketClient(uri, protocolDraft));
  }

  private ResponsiveWsClientImpl(WrapperOfWebSocketClient wrapper) {
    super((WebSocketConnection) wrapper);
    wrapper._responsiveWsClient = this;
    this._wrapperOfWebSocketClient = wrapper;
    wrapper.<ResponsiveWsConnectionImpl>setAttachment(this);
  }

  private final WrapperOfWebSocketClient _wrapperOfWebSocketClient;

  public WebSocketClient asWebSocketClient() {
    return (WebSocketClient) ((WrapperOfWebSocketClient) this._webSocketConnection)._wrappedWebSocketClient;
  }

  @Override
  public CompletableFuture<Void> connect() {
    CompletableFuture<Void> connecting = new CompletableFuture<>();
    WrappedWebSocketClient client = this._wrapperOfWebSocketClient._wrappedWebSocketClient;
    client._futureOfConnecting = connecting;
    client.connect();
    return connecting;
  }

  @Override
  public void connectBlocking() throws InterruptedException {
    CompletableFuture<Void> connecting = new CompletableFuture<>();
    WrappedWebSocketClient client = this._wrapperOfWebSocketClient._wrappedWebSocketClient;
    client._futureOfConnecting = connecting;
    client.connectBlocking();
    connecting.join();
  }
}
