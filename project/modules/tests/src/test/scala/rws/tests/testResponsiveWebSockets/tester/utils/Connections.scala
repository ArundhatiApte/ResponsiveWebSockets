package rws.tests.testResponsiveWebSockets.tester;

import rws.client.api.ResponsiveWsClient;
import rws.server.api.ResponsiveWsServerConnection;

final class Connections(sc: ResponsiveWsServerConnection, cc: ResponsiveWsClient) {
  val serverConnection = sc;
  val clientConnection = cc;
}
