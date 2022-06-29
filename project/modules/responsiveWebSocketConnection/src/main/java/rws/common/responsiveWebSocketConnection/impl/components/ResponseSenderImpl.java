package rws.common.responsiveWebSocketConnection.impl;

import java.nio.ByteBuffer;

import rws.common.webSocketConnection.WebSocketConnection;

import rws.common.responsiveWebSocketConnection.api.ResponseSender;

import rws.common.responsiveWebSocketConnection.impl.ResponsiveWsConnectionImpl;
import rws.common.responsiveWebSocketConnection.impl.SendingBinaryHeaderAndFragmentsFn;
import rws.common.responsiveWebSocketConnection.impl.SendingBinaryHeaderAndOneFragmentFn;

import rws.common.responsiveWebSocketConnection.impl.modules.messaging.BinaryMessager;

final class ResponseSenderImpl implements ResponseSender {
  public ResponseSenderImpl(ResponsiveWsConnectionImpl responsiveWebSocketConnection, char idOfMessage) {
    this._responsiveWebSocketConnection = responsiveWebSocketConnection;
    this._idOfMessage = idOfMessage;
  }

  private final ResponsiveWsConnectionImpl _responsiveWebSocketConnection;
  private final char _idOfMessage;

  public void sendBinaryResponse(ByteBuffer message) {
    ResponsiveWsConnectionImpl responsiveWebSocketConnection = this._responsiveWebSocketConnection;
    synchronized(responsiveWebSocketConnection) {
      byte[] header = responsiveWebSocketConnection._headerForBinaryRequestOrResponse;
      BinaryMessager.instance.fillHeaderAsResponse(this._idOfMessage, header);

      WebSocketConnection webSocket = responsiveWebSocketConnection._webSocketConnection;
      SendingBinaryHeaderAndOneFragmentFn.instance.apply(webSocket, header, message);
    }
  }

  public void sendFragmentsOfBinaryResponse(ByteBuffer... fragments) {
    ResponsiveWsConnectionImpl responsiveWebSocketConnection = this._responsiveWebSocketConnection;
    synchronized(responsiveWebSocketConnection) {
      byte[] header = responsiveWebSocketConnection._headerForBinaryRequestOrResponse;
      BinaryMessager.instance.fillHeaderAsResponse(this._idOfMessage, header);

      WebSocketConnection webSocket = responsiveWebSocketConnection._webSocketConnection;
      SendingBinaryHeaderAndFragmentsFn.instance.apply(webSocket, header, fragments);
    }
  }
}
