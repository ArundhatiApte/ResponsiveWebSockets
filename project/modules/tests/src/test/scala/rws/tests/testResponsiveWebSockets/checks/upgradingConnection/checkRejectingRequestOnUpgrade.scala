package rws.tests.testResponsiveWebSockets.checks.upgradingConnection;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

import org.java_websocket.handshake.ClientHandshake;

import rws.client.api.ResponsiveWsClient;
import rws.server.api.ResponsiveWsServer;
import rws.server.api.ResponsiveWsServerConnection;
import rws.server.api.HandshakeAction;

import rws.tests.utils.execOrReject;
import rws.tests.utils.voidEventsListener;

import rws.tests.testResponsiveWebSockets.checks.utils.timeouts.Timeout;
import rws.tests.testResponsiveWebSockets.checks.utils.createTimeoutForPromise;

final object checkRejectingRequestOnUpgrade {
  def apply(
    server: ResponsiveWsServer,
    uriOfServer: URI,
    createResponsiveWsClient: (URI) => ResponsiveWsClient
  ): CompletableFuture[Void] = {
    val checking = new CompletableFuture[Void]();
    val timeoutForCheck = createTimeoutForPromise(checking);

    server.setEventsListener(new ResponsiveWsServer.EventsListener() {
      override def onUpgrade(request: ClientHandshake, handshakeAction: HandshakeAction): Unit = {
        execOrReject(() => handshakeAction.rejectConnection(), checking);
      }
      override def onConnection(serverConnection: ResponsiveWsServerConnection): Unit = {
        execOrReject(
          () => checking.completeExceptionally(new RuntimeException("Connection was created.")),
          checking
        );
      }
    });

    _assertFailureAtConnection(createResponsiveWsClient(uriOfServer), checking);
    checking;
  }

  private def _assertFailureAtConnection (client: ResponsiveWsClient, checking: CompletableFuture[Void]): Unit = {
    var connecting: CompletableFuture[Void] = null;
    try {
      connecting = client.connect();
    } catch {
      case error: Throwable => checking.completeExceptionally(error);
    }

    connecting.handleAsync((void, error) => {
      if (error != null) {
        checking.complete(null);
      } else {
        checking.completeExceptionally(new RuntimeException("Connection was created."));
      }
      client.terminate();
    });
  }
}
