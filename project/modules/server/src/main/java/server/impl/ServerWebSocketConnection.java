package rws.server.impl;

import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.ByteBuffer;

import org.java_websocket.WebSocket;
import org.java_websocket.enums.Opcode;

import rws.common.webSocketConnection.WebSocketConnection;

final class ServerWebSocketConnection implements WebSocketConnection {
  ServerWebSocketConnection(WebSocket webSocket, String url) {
    this._wrappedWebSocket = webSocket;
    this._eventsListener = null;
    this._url = url;
    this._attachment = webSocket.getAttachment();
  }

  private final WebSocket _wrappedWebSocket;
  WebSocketConnection.EventsListener _eventsListener; // always setted by ResponsiveWebSocketConnectionImpl
  private String _url;
  private Object _attachment;

  public InetSocketAddress getRemoteSocketAddress() {
    return this._wrappedWebSocket.getRemoteSocketAddress();
  }

  @Override
  public String getUrl() {
    return this._url;
  }

  @Override
  public void close() {
    this._wrappedWebSocket.close();
  }

  @Override
  public void close(int code) {
    this._wrappedWebSocket.close(code);
  }

  @Override
  public void close(int code, String reason) {
    this._wrappedWebSocket.close(code, reason);
  }

  @Override
  public void terminate() {
    int noMatterCode = 1100;
    this._wrappedWebSocket.closeConnection(noMatterCode, null);
  }

  @Override
  public <T> T getAttachment() {
    return (T) this._attachment;
  }

  @Override
  public <T> void setAttachment(T attachment) {
    this._attachment = attachment;
  }

  @Override
  public void sendBinaryFragment(ByteBuffer fragment, boolean isLast) {
    this._wrappedWebSocket.sendFragmentedFrame(Opcode.BINARY, fragment, isLast);
  }

  @Override
  public void sendTextMessage(String message) {
    this._wrappedWebSocket.send(message);
  }

  @Override
  public void setEventsListener(WebSocketConnection.EventsListener eventsListener) {
    this._eventsListener = eventsListener;
  }
}
