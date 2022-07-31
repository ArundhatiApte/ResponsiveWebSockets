package rws.server.impl;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.List;

import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshakeBuilder;
import org.java_websocket.server.WebSocketServer;

import rws.common.webSocketConnection.WebSocketConnection;

import rws.server.api.ResponsiveWsServer;
import rws.server.api.ResponsiveWsServerConnection;

import rws.server.impl.ResponsiveWsServerImpl;
import rws.server.impl.ResponsiveWsServerConnectionImpl;
import rws.server.impl.ServerWebSocketConnection;
import rws.server.impl.HandshakeActionImpl;
import rws.server.impl.StateOfHandshakeActionImpl;

final class WrappedWebSocketServer extends WebSocketServer {
  WrappedWebSocketServer(
    ResponsiveWsServerImpl responsiveWsServer,
    InetSocketAddress address
  ) {
    super(address);
    this._responsiveWebSocketServer = responsiveWsServer;
  }

  WrappedWebSocketServer(
    ResponsiveWsServerImpl responsiveWsServer,
    InetSocketAddress address,
     List<Draft> protocolsDraft
  ) {
    super(address, protocolsDraft);
    this._responsiveWebSocketServer = responsiveWsServer;
  }

  private final ResponsiveWsServerImpl _responsiveWebSocketServer;
  protected CompletableFuture<Void> _futureOfConnecting;

  @Override
  public void onStart() {
    this._futureOfConnecting.complete(null);
    this._futureOfConnecting = null;
  }

  @Override
  public ServerHandshakeBuilder onWebsocketHandshakeReceivedAsServer(
    WebSocket webSocket,
    Draft draft,
    ClientHandshake request
  ) throws InvalidDataException {
    ResponsiveWsServer.EventsListener eventsListener = this._responsiveWebSocketServer._eventsListener;
    if (eventsListener == null) {
      return this._acceptRequestOnUpgradingConnection(webSocket, draft, request);
    }
    HandshakeActionImpl handshakeAction = new HandshakeActionImpl(webSocket);
    eventsListener.onUpgrade(request, handshakeAction);
    StateOfHandshakeActionImpl state = handshakeAction._state;

    if (state == StateOfHandshakeActionImpl.accepted) {
      return this._acceptRequestOnUpgradingConnection(webSocket, draft, request);
    }
    if (state == StateOfHandshakeActionImpl.rejected) {
      throw new InvalidDataException(CloseFrame.POLICY_VALIDATION, "Not accepted.");
    }
    throw new RuntimeException("Only sync listeners are allowed.");
  }

  private ServerHandshakeBuilder _acceptRequestOnUpgradingConnection(
    WebSocket webSocket,
    Draft draft,
    ClientHandshake request
  ) throws InvalidDataException {
    return super.onWebsocketHandshakeReceivedAsServer(webSocket, draft, request);
  }

  @Override
  public void onOpen(WebSocket webSocket, ClientHandshake handshake) {
    ResponsiveWsServer.EventsListener eventsListener = this._responsiveWebSocketServer._eventsListener;
    if (eventsListener == null) {
      return;
    }
    ServerWebSocketConnection wrapper = new ServerWebSocketConnection(webSocket, handshake.getResourceDescriptor());
    Object prevAttachment = webSocket.<Object>getAttachment();
    webSocket.<ServerWebSocketConnection>setAttachment(wrapper);

    ResponsiveWsServerConnection serverConnection = new ResponsiveWsServerConnectionImpl(wrapper);
    serverConnection.<Object>setAttachment(prevAttachment);
    eventsListener.onConnection(serverConnection);
  }

  @Override
  public void onMessage(WebSocket webSocket, ByteBuffer message) {
    ServerWebSocketConnection webSocketConnection = webSocket.<ServerWebSocketConnection>getAttachment();
    // always setted by ResponsiveWsConnectionImpl
    WebSocketConnection.EventsListener eventsListener = webSocketConnection._eventsListener;
    eventsListener.onBinaryMessage(webSocketConnection, message);
  }

  @Override
  public void onMessage(WebSocket webSocket, String message) {
    ServerWebSocketConnection webSocketConnection = webSocket.<ServerWebSocketConnection>getAttachment();
    // always setted by ResponsiveWsConnectionImpl
    WebSocketConnection.EventsListener eventsListener = webSocketConnection._eventsListener;
    eventsListener.onTextMessage(webSocketConnection, message);
  }

  @Override
  public void onError(WebSocket webSocket, Exception error) {
    if (webSocket == null) {
      _emitErrorFromServer(this, error);
    } else {
      _emitErrorFromWebSocket(webSocket, error);
    }
  }

  private static void  _emitErrorFromWebSocket(WebSocket webSocket, Exception error) {
    ServerWebSocketConnection webSocketConnection = webSocket.<ServerWebSocketConnection>getAttachment();
    // always setted by ResponsiveWsConnectionImpl
    WebSocketConnection.EventsListener eventsListener = webSocketConnection._eventsListener;
    eventsListener.onError(webSocketConnection, error);
  }

  private static void _emitErrorFromServer(WrappedWebSocketServer webSocketServer, Exception error) {
    CompletableFuture<Void> futureOfConnecting = webSocketServer._futureOfConnecting;
    if (futureOfConnecting != null) {
      futureOfConnecting.completeExceptionally(error);
      webSocketServer._futureOfConnecting = null;
    }
  }

  @Override
  public void	onClose(WebSocket webSocket, int code, String reason, boolean isRemote) {
    ServerWebSocketConnection webSocketConnection = webSocket.<ServerWebSocketConnection>getAttachment();
    // always setted by ResponsiveWsConnectionImpl
    WebSocketConnection.EventsListener eventsListener = webSocketConnection._eventsListener;
    eventsListener.onClose(webSocketConnection, code, reason, isRemote);
  }
}
