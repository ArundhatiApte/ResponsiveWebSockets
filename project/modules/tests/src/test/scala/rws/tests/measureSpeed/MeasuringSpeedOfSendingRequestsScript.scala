package rws.tests.measureSpeed;

import java.net.InetSocketAddress;
import java.net.URI;

import rws.server.api.ResponsiveWsServer;
import rws.server.impl.ResponsiveWsServerImpl;

import rws.client.api.ResponsiveWsClient;
import rws.client.impl.ResponsiveWsClientImpl;

import rws.tests.utils.await;
import rws.tests.utils.createServerConnectionToClient;

import rws.tests.measureSpeed.measureSpeedOfSendingRequestsAndLogResultsBlocking;

object MeasuringSpeedOfSendingRequestsScript extends App {
  val port = rws.tests.ports.forMeasuringSpeed;
  val server: ResponsiveWsServer = new ResponsiveWsServerImpl(new InetSocketAddress(port));
  await(server.start());

  val clientConnection = new ResponsiveWsClientImpl(new URI("ws://127.0.0.1:" + port));
  val serverConnection = await(createServerConnectionToClient(
    server,
    clientConnection
  ));

  val maxTimeMsForWaitingResponse = 8000;
  clientConnection.setMaxTimeMsToWaitResponse(maxTimeMsForWaitingResponse);
  serverConnection.setMaxTimeMsToWaitResponse(maxTimeMsForWaitingResponse);

  val countOfRequests = 1_000_000;
  measureSpeedOfSendingRequestsAndLogResultsBlocking(
    serverConnection,
    clientConnection,
    countOfRequests,
    System.out
  );

  serverConnection.terminate();
  clientConnection.terminate();
  server.close();
  System.exit(0);
}
