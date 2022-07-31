package rws.client.impl;

import java.net.URI;
import java.nio.ByteBuffer;

import org.java_websocket.drafts.Draft;
import org.java_websocket.enums.Opcode;

import rws.common.webSocketConnection.WebSocketConnection;

import rws.client.impl.WrappedWebSocketClient;

final class WrapperOfWebSocketClient implements WebSocketConnection {
  WrapperOfWebSocketClient(URI uri) {
    this._wrappedWebSocketClient = new WrappedWebSocketClient(this, uri);
    this._init();
  }

  WrapperOfWebSocketClient(URI uri, Draft protocolDraft) {
    this._wrappedWebSocketClient = new WrappedWebSocketClient(this, uri, protocolDraft);
    this._init();
  }

  private void _init() {
    this._eventsListener = null;
    this._responsiveWsClient = null;
  }

  protected WebSocketConnection.EventsListener _eventsListener;
  protected ResponsiveWsClientImpl _responsiveWsClient;
  protected final WrappedWebSocketClient _wrappedWebSocketClient;

  @Override
  public String getUrl() {
    return this._wrappedWebSocketClient.getURI().toString();
  }

  @Override
  public void close() {
    this._wrappedWebSocketClient.close();
  }

  @Override
  public void close(int code) {
    this._wrappedWebSocketClient.close(code);
  }

  @Override
  public void close(int code, String reason) {
    this._wrappedWebSocketClient.close(code, reason);
  }

  @Override
  public void terminate() {
    int noMatterCode = 1100;
    this._wrappedWebSocketClient.closeConnection(noMatterCode, null);
  }

  @Override
  public <T> T getAttachment() {
    return this._wrappedWebSocketClient.<T>getAttachment();
  }

  @Override
  public <T> void setAttachment(T attachment) {
    this._wrappedWebSocketClient.<T>setAttachment(attachment);
  }

  @Override
  public void sendBinaryFragment(ByteBuffer fragment, boolean isLast) {
    this._wrappedWebSocketClient.sendFragmentedFrame(Opcode.BINARY, fragment, isLast);
  }

  @Override
  public void sendTextMessage(String message) {
    this._wrappedWebSocketClient.send(message);
  }

  @Override
  public void setEventsListener(WebSocketConnection.EventsListener eventsListener) {
    this._eventsListener = eventsListener;
  }
}
