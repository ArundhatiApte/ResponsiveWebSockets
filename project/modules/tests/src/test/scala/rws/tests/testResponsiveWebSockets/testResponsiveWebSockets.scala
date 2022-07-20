package rws.tests.testResponsiveWebSockets;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.concurrent.CompletableFuture;

import rws.client.api.ResponsiveWsClient;
import rws.client.impl.ResponsiveWsClientImpl;

import rws.server.api.ResponsiveWsServerConnection;
import rws.server.impl.ResponsiveWsServerImpl;

import rws.tests.testResponsiveWebSockets.tester.AbstractTesterOfResponsiveWebSockets;

final class TesterOfResponsiveWebSockets extends AbstractTesterOfResponsiveWebSockets (
  new ResponsiveWsServerImpl(new InetSocketAddress(TesterOfResponsiveWebSockets._port)),
  new URI("ws://127.0.0.1:" + TesterOfResponsiveWebSockets._port)
) {
  override def _createResponsiveWsClient(uri: URI): ResponsiveWsClient = {
    new ResponsiveWsClientImpl(uri);
  }
}

final object TesterOfResponsiveWebSockets {
  protected val _port = rws.tests.ports.forTestingResponsiveWebSockets;
}
