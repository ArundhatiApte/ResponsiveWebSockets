package rws.examples;

import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

import org.java_websocket.handshake.ClientHandshake;

import rws.common.responsiveWebSocketConnection.api.ResponsiveWsConnection;
import rws.common.responsiveWebSocketConnection.api.ResponseSender;

import rws.server.api.ResponsiveWsServer;
import rws.server.api.HandshakeAction;
import rws.server.api.ResponsiveWsServerConnection;
import rws.server.impl.ResponsiveWsServerImpl;

import rws.client.api.ResponsiveWsClient;
import rws.client.impl.ResponsiveWsClientImpl;

import rws.examples.utils.VoidConnectionEventsListener;
import rws.examples.utils.VoidServerEventsListener;
import static rws.examples.utils.AwaitingFn.await;
import static rws.examples.utils.PrintingByteBufferFn.printlnByteBuffer;

public final class SendingBinaryRequests {
  public static void main(String[] args) throws Throwable {
    int port = 2345;
    ResponsiveWsServer server = new ResponsiveWsServerImpl(new InetSocketAddress(port));
    await(server.start());

    CompletableFuture<ResponsiveWsServerConnection> creatingServerConnection = new CompletableFuture<>();
    server.setEventsListener(new ResponsiveWsServer.EventsListener() {
      @Override
      public void onUpgrade(ClientHandshake request, HandshakeAction handshakeAction) {
        handshakeAction.acceptConnection();
      }

      @Override
      public void onConnection(ResponsiveWsServerConnection serverConnection) {
        creatingServerConnection.complete(serverConnection);
      }
    });

    ResponsiveWsClient client = new ResponsiveWsClientImpl(new URI("ws://127.0.0.1:" + port));
    await(client.connect());
    ResponsiveWsServerConnection connectionToClient = await(creatingServerConnection);

    PrintStream out = System.out;

    ResponsiveWsConnection.EventsListener eventsListener = new VoidConnectionEventsListener() {
      @Override
      public void onBinaryRequest(
        ResponsiveWsConnection connection,
        ByteBuffer messageWithHeader,
        int startIndex,
        ResponseSender responseSender
      ) {
        out.print("binary request: ");
        printlnByteBuffer(out, messageWithHeader, startIndex);
        ByteBuffer response = ByteBuffer.wrap(new byte[] {12, 34, 56, 78});
        responseSender.sendBinaryResponse(response);
      }
    };

    {
      out.println("\nsending binary request from server");
      client.setEventsListener(eventsListener);
      ByteBuffer response = await(connectionToClient.sendBinaryRequest(ByteBuffer.wrap(new byte[] {0, 12, 34, 5})));
      out.print("binary response from client: ");
      printlnByteBuffer(out, response, connectionToClient.getStartIndexOfBodyInBinaryResponse());
    }

    {
      out.println("sending binary request from client");
      connectionToClient.setEventsListener(eventsListener);
      ByteBuffer response = await(client.sendBinaryRequest(ByteBuffer.wrap(new byte[] {91, 92, 93, 94, 95, 96})));
      out.print("binary response from server: ");
      printlnByteBuffer(out, response, client.getStartIndexOfBodyInBinaryResponse());
    }

    connectionToClient.terminate();
    client.terminate();
    server.close();
    System.exit(0);
  }
}
