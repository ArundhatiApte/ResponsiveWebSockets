package rws.tests.testResponsiveWebSockets.tester;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

import rws.common.responsiveWebSocketConnection.api.ResponsiveWsConnection;

import rws.tests.testResponsiveWebSockets.tester.AddingTests;
import rws.tests.testResponsiveWebSockets.tester.Connections;
import rws.tests.testResponsiveWebSockets.checks.checkClosingConnection;

protected final object addCheckingClosingConnectionTests {
  def apply(
    addingTests: AddingTests,
    createServerConnectionAndClient: () => CompletableFuture[Connections]
  ): Unit = {
    val getServerConnection = (connections: Connections) => connections.serverConnection;
    val getClientConnection = (connections: Connections) => connections.clientConnection;

    addingTests.addAsyncTest(
      "closing connection by server",
      _createFnToCheckClosingConnectionAndCloseIfFail(
        createServerConnectionAndClient,
        getServerConnection,
        getClientConnection
      )
    );
    addingTests.addAsyncTest(
      "closing connection by client",
      _createFnToCheckClosingConnectionAndCloseIfFail(
        createServerConnectionAndClient,
        getClientConnection,
        getServerConnection
      )
    );
  }

  private def _createFnToCheckClosingConnectionAndCloseIfFail(
    createServerConnectionAndClient: () => CompletableFuture[Connections],
    getClosingConnectinSide: (Connections) => ResponsiveWsConnection,
    getAcceptingEventSide: (Connections) => ResponsiveWsConnection
  ): () => CompletableFuture[Void] = {
    () => _executeTestAndCloseConnectionsIfFail(
      createServerConnectionAndClient,
      getClosingConnectinSide,
      getAcceptingEventSide
    );
  }

  private def _executeTestAndCloseConnectionsIfFail(
    createServerConnectionAndClient: () => CompletableFuture[Connections],
    getClosingConnectinSide: (Connections) => ResponsiveWsConnection,
    getAcceptingEventSide: (Connections) => ResponsiveWsConnection
  ): CompletableFuture[Void] = {
    val connections = createServerConnectionAndClient().get();
    val closingConnectionSide = getClosingConnectinSide(connections);
    val acceptingEventSide = getAcceptingEventSide(connections);

    checkClosingConnection(closingConnectionSide, acceptingEventSide).exceptionally((error) => {
      closingConnectionSide.terminate();
      acceptingEventSide.terminate();
      throw error;
    });
  }
}
