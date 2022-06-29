package rws.server.api;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

import org.java_websocket.handshake.ClientHandshake;

import rws.server.api.HandshakeAction;
import rws.server.api.ResponsiveWsServerConnection;

public interface ResponsiveWsServer {
  public void close() throws InterruptedException;
  public CompletableFuture<Void> start();
  public void setEventsListener(ResponsiveWsServer.EventsListener listener);

  public static interface EventsListener {
    public void onUpgrade(ClientHandshake request, HandshakeAction handshakeAction);
    public void onConnection(ResponsiveWsServerConnection serverConnection);
  }
}
