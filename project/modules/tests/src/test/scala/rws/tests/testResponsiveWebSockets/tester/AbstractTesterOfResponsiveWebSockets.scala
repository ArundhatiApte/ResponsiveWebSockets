package rws.tests.testResponsiveWebSockets.tester;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

import org.scalatest.BeforeAndAfterAll;
import org.scalatest.funsuite.AnyFunSuite;

import rws.common.responsiveWebSocketConnection.api.ResponsiveWsConnection;
import rws.client.api.ResponsiveWsClient;
import rws.server.api.ResponsiveWsServer;
import rws.server.api.ResponsiveWsServerConnection;

import rws.tests.utils.await;
import rws.tests.utils.createServerConnectionToClient;

import rws.tests.testResponsiveWebSockets.tester.AddingTests;
import rws.tests.testResponsiveWebSockets.tester.Connections;
import rws.tests.testResponsiveWebSockets.tester.addCheckingUpgradingConnetionTests;
import rws.tests.testResponsiveWebSockets.tester.addCheckingSendingMessagesTests;
import rws.tests.testResponsiveWebSockets.tester.addCheckingClosingConnectionTests;

abstract class AbstractTesterOfResponsiveWebSockets(
  server: ResponsiveWsServer,
  uriOfServer: URI
) extends AnyFunSuite with BeforeAndAfterAll {

  private val _responsiveWebSocketServer = server;
  private val _uriOfServer = uriOfServer;

  private var _serverConnection: ResponsiveWsServerConnection = null;
  private var _clientConnection: ResponsiveWsClient = null;

  private val _addingTests = new AddingTestsImpl();

  this._setup();

  override def beforeAll(): Unit = {
    val server = _responsiveWebSocketServer;
    await(server.start());
    val clientConnection = _createResponsiveWsClient(_uriOfServer);
    _serverConnection = await(createServerConnectionToClient(server, clientConnection));
    _clientConnection = clientConnection;
  }

  protected def _createResponsiveWsClient(uri: URI): ResponsiveWsClient;

  private def _createServerAndClientConnections(): CompletableFuture[Connections] = {
    val uriOfServer = _uriOfServer;
    val client = _createResponsiveWsClient(uriOfServer);

    createServerConnectionToClient(_responsiveWebSocketServer, client).thenApply((serverConnection) => {
      new Connections(serverConnection, client)
    });
  }

  private def _setup(): Unit = {
    addCheckingUpgradingConnetionTests(
      _addingTests,
      _responsiveWebSocketServer,
      _uriOfServer,
      _createResponsiveWsClient
    );
    addCheckingSendingMessagesTests(
      _addingTests,
      _createFnToTestFromServerToClient,
      _createFnToTestFromClientToServer
    );
    addCheckingClosingConnectionTests(_addingTests, _createServerAndClientConnections);
  }

  private def _createFnToTestFromServerToClient(
    check: (ResponsiveWsConnection, ResponsiveWsConnection) => CompletableFuture[Void]
  ): () => CompletableFuture[Void] = {
    () => check(_serverConnection, _clientConnection)
  }

  private def _createFnToTestFromClientToServer(
    check: (ResponsiveWsConnection, ResponsiveWsConnection) => CompletableFuture[Void]
  ): () => CompletableFuture[Void] = {
    () => check(_clientConnection, _serverConnection)
  }

  protected final class AddingTestsImpl extends AddingTests {
    override def addAsyncTest(nameOfTest: String, fnForTest: () => CompletableFuture[Void]): Unit = {
      test(nameOfTest) {
        await(fnForTest());
      }
    }
  }

  override def afterAll(): Unit = {
    _clientConnection.terminate();
    _serverConnection.terminate();
    _responsiveWebSocketServer.close();
  }
}
