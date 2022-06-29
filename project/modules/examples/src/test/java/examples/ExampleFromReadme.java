package rws.examples;

import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import org.java_websocket.handshake.ClientHandshake;

import rws.common.responsiveWebSocketConnection.api.ResponsiveWsConnection;
import static rws.common.responsiveWebSocketConnection.api.ResponsiveWsConnection.EventsListener;
import rws.common.responsiveWebSocketConnection.api.ResponseSender;

import rws.server.api.ResponsiveWsServer;
import rws.server.api.HandshakeAction;
import rws.server.api.ResponsiveWsServerConnection;
import rws.server.impl.ResponsiveWsServerImpl;

import rws.client.api.ResponsiveWsClient;
import rws.client.impl.ResponsiveWsClientImpl;

import static rws.examples.utils.AwaitingFn.await;
import static rws.examples.utils.PrintingByteBufferFn.printlnByteBuffer;

public final class ExampleFromReadme {
  public static void main(String[] args) throws Throwable {
    int port = 1234;
    ResponsiveWsServer server = new ResponsiveWsServerImpl(new InetSocketAddress(port));
    await(server.start());

    class VoidEventsListener implements EventsListener {
      @Override
      public void onClose(ResponsiveWsConnection c, int code, String reason) {}

      @Override
      public void onError(ResponsiveWsConnection c, Throwable error) {}

      @Override
      public void onBinaryRequest(
        ResponsiveWsConnection c,
        ByteBuffer messageWithHeader,
        int startIndex,
        ResponseSender rs
      ) {}

      @Override
      public void onMalformedBinaryMessage(ResponsiveWsConnection c, ByteBuffer message) {}

      @Override
      public void onTextMessage(ResponsiveWsConnection c, String message) {}

      @Override
      public void onUnrequestingBinaryMessage(
        ResponsiveWsConnection c,
        ByteBuffer messageWithHeader,
        int startIndex
      ) {}
    }

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
    ResponsiveWsServerConnection serverConnection = await(creatingServerConnection);

    {
      final EventsListener echoOnBinaryRequestListener = new VoidEventsListener() {
        @Override
        public void onBinaryRequest(
          ResponsiveWsConnection c,
          ByteBuffer messageWithHeader,
          int startIndex,
          ResponseSender rs
        ) {
          messageWithHeader.position(startIndex);
          rs.sendBinaryResponse(messageWithHeader);
        }
      };

      {
        client.setEventsListener(echoOnBinaryRequestListener);
        final byte[] message = new byte[] {1, 2, 3, 4};
        serverConnection.sendBinaryRequest(ByteBuffer.wrap(message)).whenCompleteAsync((binaryResponse, error) -> {
          // ...
        });
      }

      {
        serverConnection.setEventsListener(echoOnBinaryRequestListener);
        final byte[] message = new byte[] {5, 6, 7, 8};
        client.sendBinaryRequest(ByteBuffer.wrap(message)).whenCompleteAsync((binaryResponse, error) -> {
          // ...
        });
      }
    }

    {
      final EventsListener doingSomethingOnUnreqBinaryMessageListener = new VoidEventsListener() {
        // override onUnrequestingBinaryMessage
      };

      client.setEventsListener(doingSomethingOnUnreqBinaryMessageListener);

      serverConnection.sendUnrequestingBinaryMessage(ByteBuffer.wrap(
        new byte[] {10, 20, 30, 40}
      ));
    }

    Thread.sleep(100);
    serverConnection.close();
    client.close();
    server.close();
    System.out.println("end");
  }
}
