package rws.tests.testResponsiveWebSockets.checks.upgradingConnection;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

import org.java_websocket.handshake.ClientHandshake;

import rws.client.api.ResponsiveWsClient;
import rws.server.api.ResponsiveWsServer;
import rws.server.api.ResponsiveWsServerConnection;
import rws.server.api.HandshakeAction;

import rws.tests.utils.execOrReject;
import rws.tests.utils.timeouts.Timeout;
import rws.tests.utils.createTimeoutForPromise;

final object checkAcceptingRequestOnUpgrade {
  def apply(
    server: ResponsiveWsServer,
    uriOfServer: URI,
    createResponsiveWsClient: (URI) => ResponsiveWsClient
  ): CompletableFuture[Void] = {
    val checking = new CompletableFuture[Void]();
    val timeout = createTimeoutForPromise(checking);
    val userData = "foo";

    server.setEventsListener(new ResponsiveWsServer.EventsListener() {
      override def onUpgrade(request: ClientHandshake, handshakeAction: HandshakeAction): Unit = {
        execOrReject(() => handshakeAction.acceptConnection(userData), checking);
      }
      override def onConnection(serverConnection: ResponsiveWsServerConnection): Unit = {
        execOrReject(() => {
          if (serverConnection.getAttachment[String]() == userData) {
            checking.complete(null);
          } else {
            checking.completeExceptionally(new RuntimeException("Different user data."));
          }
        }, checking);
      }
    });

    val client = createResponsiveWsClient(uriOfServer);
    client.connect().handleAsync((void, error) => {
      if (error != null) {
        checking.completeExceptionally(error);
      }
      client.terminate();
    });
    checking;
  }
}
