package rws.tests.checkСompatibilityWithVersionInJs;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

import org.scalatest.BeforeAndAfterAll;
import org.scalatest.funsuite.AnyFunSuite;

import rws.common.responsiveWebSocketConnection.api.ResponsiveWsConnection;
import rws.client.api.ResponsiveWsClient;
import rws.client.impl.ResponsiveWsClientImpl;

import rws.tests.utils.await;

import rws.tests.checkСompatibilityWithVersionInJs.checks.checkSendingBinaryRequest;
import rws.tests.checkСompatibilityWithVersionInJs.checks.checkSendingUnrequestingBinaryMessages;

final class СheckingСompatibilityWithVersionInJsTester(
  port: Int,
  pathToStartingServerInNodeJsScript: String
) extends AnyFunSuite with BeforeAndAfterAll {
  private val _pathToStartingServerInNodeJsScript = pathToStartingServerInNodeJsScript;
  private val _port = port;
  private var _processOfServerInNodeJs: Process = null;
  private var _connectionWithServer: ResponsiveWsConnection = null;
  private val _multiplerForBinaryResponse: Short = 4;

  this._addTests();

  override def beforeAll(): Unit = { _launchServerInNodeJsAndSetupConnection(); }

  def _launchServerInNodeJsAndSetupConnection(): Unit = {
    val port = this._port;
    this._processOfServerInNodeJs = _launchServerInNodeJs(
      this._pathToStartingServerInNodeJsScript,
      port,
      this._multiplerForBinaryResponse
    );
    val uri = URI.create("ws://127.0.0.1:" + port);
    this._connectionWithServer = _createConnectionWithServer(uri);
  }

  private def _addTests(): Unit = {
    test("sending binary request and receiving response") {
      await(checkSendingBinaryRequest(this._connectionWithServer, this._multiplerForBinaryResponse));
    }
    test("sending unrequesting binary messages") {
      await(checkSendingUnrequestingBinaryMessages(this._connectionWithServer));
    }
  }

  override def afterAll(): Unit = { _closeConnectionAndProcessOfServer(); }

  def _closeConnectionAndProcessOfServer(): Unit = {
    this._connectionWithServer.terminate();
    this._processOfServerInNodeJs.destroy();
  }
}

private final object _launchServerInNodeJs {
  def apply(pathToScriptForNodeJs: String, port: Int, multiplerForBinaryResponse: Short): Process = {
    val processBuilder = new ProcessBuilder(
      "node",
      pathToScriptForNodeJs,
      port.toString,
      multiplerForBinaryResponse.toString
    );
    processBuilder.inheritIO();
    val process = processBuilder.start();
    val timeMsForStartingServer = 2000;
    Thread.sleep(timeMsForStartingServer);
    process;
  }
}

private final object _createConnectionWithServer {
  def apply(uri: URI): ResponsiveWsClient = {
    val responsiveWsClient = new ResponsiveWsClientImpl(uri);
    responsiveWsClient.connectBlocking();
    responsiveWsClient;
  }
}
