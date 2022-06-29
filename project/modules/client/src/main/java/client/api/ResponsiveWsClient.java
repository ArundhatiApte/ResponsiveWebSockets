package rws.client.api;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

import rws.common.responsiveWebSocketConnection.api.ResponsiveWsConnection;

public interface ResponsiveWsClient extends ResponsiveWsConnection {
  public CompletableFuture<Void> connect();
  public void connectBlocking() throws InterruptedException;
}
