package rws.server.impl;

import org.java_websocket.WebSocket;

import rws.server.api.HandshakeAction;
import rws.server.impl.ServerWebSocketConnection;
import rws.server.impl.StateOfHandshakeActionImpl;

final class HandshakeActionImpl implements HandshakeAction {
  HandshakeActionImpl(WebSocket webSocket) {
    this._webSocket = webSocket;
    this._state = StateOfHandshakeActionImpl.pending;
  }

  private WebSocket _webSocket;
  StateOfHandshakeActionImpl _state;

  public void acceptConnection() {
    this._state = StateOfHandshakeActionImpl.accepted;
  }

  public <T> void acceptConnection(T userData) {
    this._webSocket.<T>setAttachment(userData);
    this._state = StateOfHandshakeActionImpl.accepted;
  }

  public void rejectConnection() {
    this._state = StateOfHandshakeActionImpl.rejected;
  }
}
