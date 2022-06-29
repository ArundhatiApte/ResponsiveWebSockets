package rws.tests.testResponsiveWebSockets.tester;

import java.net.URI;

import rws.client.api.ResponsiveWsClient;
import rws.server.api.ResponsiveWsServer;

import rws.tests.testResponsiveWebSockets.tester.AddingTests;

import rws.tests.testResponsiveWebSockets.checks.upgradingConnection.checkAcceptingRequestOnUpgrade;
import rws.tests.testResponsiveWebSockets.checks.upgradingConnection.checkRejectingRequestOnUpgrade;

protected final object addCheckingUpgradingConnetionTests {
  def apply(
    addingTests: AddingTests,
    responsiveWebSocketServer: ResponsiveWsServer,
    uriOfServer: URI,
    createResponsiveWsClient: (URI) => ResponsiveWsClient
  ): Unit = {
    addingTests.addAsyncTest("accepting request on upgrade", () => {
      checkAcceptingRequestOnUpgrade(responsiveWebSocketServer, uriOfServer, createResponsiveWsClient);
    });
    addingTests.addAsyncTest("rejecting request on upgrade", () => {
      checkRejectingRequestOnUpgrade(responsiveWebSocketServer, uriOfServer, createResponsiveWsClient);
    });
  }
}
