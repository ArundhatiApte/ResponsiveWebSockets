package rws.server.impl;

import java.net.InetSocketAddress;

import rws.common.responsiveWebSocketConnection.impl.ResponsiveWsConnectionImpl;
import rws.common.webSocketConnection.WebSocketConnection;

import rws.server.api.ResponsiveWsServerConnection;
import rws.server.impl.ServerWebSocketConnection;

final class ResponsiveWsServerConnectionImpl extends ResponsiveWsConnectionImpl implements ResponsiveWsServerConnection {
  ResponsiveWsServerConnectionImpl(ServerWebSocketConnection webSocket) {
    super(webSocket);
    webSocket.<ResponsiveWsConnectionImpl>setAttachment(this);
  }

  @Override
  public InetSocketAddress getRemoteSocketAddress() {
    ServerWebSocketConnection webSocket = (ServerWebSocketConnection) this._webSocketConnection;
    return webSocket.getRemoteSocketAddress();
  }
}
