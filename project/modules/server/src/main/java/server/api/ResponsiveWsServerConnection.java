package rws.server.api;

import java.net.InetSocketAddress;

import rws.common.responsiveWebSocketConnection.api.ResponsiveWsConnection;

public interface ResponsiveWsServerConnection extends ResponsiveWsConnection {
  public InetSocketAddress getRemoteSocketAddress();
}
