package rws.tests.utils;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

import org.java_websocket.handshake.ClientHandshake;

import rws.client.api.ResponsiveWsClient;

import rws.server.api.ResponsiveWsServer;
import rws.server.api.ResponsiveWsServerConnection;
import rws.server.api.HandshakeAction;

import rws.tests.utils.execOrReject;
import rws.tests.utils.HolderOfValue;
import rws.tests.utils.voidEventsListener;

final object createServerConnectionToClient {
  def apply(
    responsiveWebSocketServer: ResponsiveWsServer,
    responsiveWebSocketClient: ResponsiveWsClient
  ): CompletableFuture[ResponsiveWsServerConnection] = {
    val creatingServerConnection = new CompletableFuture[ResponsiveWsServerConnection]();
    val holderOfConnectingClient = new HolderOfValue[CompletableFuture[Void]]();

    responsiveWebSocketServer.setEventsListener(new ResponsiveWsServer.EventsListener() {
      override def onUpgrade(request: ClientHandshake, handshakeAction: HandshakeAction): Unit = {
        execOrReject(() => handshakeAction.acceptConnection(), creatingServerConnection);
      }

      override def onConnection(serverConnection: ResponsiveWsServerConnection) {
        execOrReject(() => {
          holderOfConnectingClient.get().handleAsync((void, error) => {
            if (error != null) {
              creatingServerConnection.completeExceptionally(error);
            } else {
              creatingServerConnection.complete(serverConnection);
            }
          });
        }, creatingServerConnection);
      }
    });

    val connectingClient = responsiveWebSocketClient.connect();
    holderOfConnectingClient.set(connectingClient);

    connectingClient.exceptionally((error) => {
      creatingServerConnection.completeExceptionally(error);
      null;
    });

    creatingServerConnection;
  }
}
