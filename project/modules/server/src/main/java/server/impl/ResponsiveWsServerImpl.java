package rws.server.impl;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.List;

import org.java_websocket.drafts.Draft;
import org.java_websocket.server.WebSocketServer;

import rws.server.api.ResponsiveWsServer;
import rws.server.impl.WrappedWebSocketServer;

public final class ResponsiveWsServerImpl implements ResponsiveWsServer {
  public ResponsiveWsServerImpl(InetSocketAddress address) {
    this._webSocketServer = new WrappedWebSocketServer(this, address);
    this._eventsListener = null;
  }

  public ResponsiveWsServerImpl(InetSocketAddress address, List<Draft> protocolsDrafts) {
    this._webSocketServer = new WrappedWebSocketServer(this, address, protocolsDrafts);;
    this._eventsListener = null;
  }

  private final WrappedWebSocketServer _webSocketServer;
  protected ResponsiveWsServer.EventsListener _eventsListener;

  @Override
  public CompletableFuture<Void> start() {
    CompletableFuture<Void> connecting = new CompletableFuture<>();
    WrappedWebSocketServer webSocketServer = this._webSocketServer;
    webSocketServer._futureOfConnecting = connecting;
    webSocketServer.start();
    return connecting;
  }

  @Override
  public void setEventsListener(ResponsiveWsServer.EventsListener eventsListener) {
    this._eventsListener = eventsListener;
  }

  @Override
  public void close() throws InterruptedException {
    this._webSocketServer.stop();
  }

  public WebSocketServer asWebSocketServer() {
    return this._webSocketServer;
  }
}
