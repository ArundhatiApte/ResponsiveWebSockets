package rws.common.responsiveWebSocketConnection.impl;

import java.nio.ByteBuffer;

import rws.common.webSocketConnection.WebSocketConnection;
import rws.common.responsiveWebSocketConnection.impl.ResponsiveWsConnectionImpl;
import rws.common.responsiveWebSocketConnection.impl.SendingBinaryHeaderAndOneFragmentFn;

final class SendingUnrequestingBinaryMessageMethod {
  public static final SendingUnrequestingBinaryMessageMethod instance = new SendingUnrequestingBinaryMessageMethod();

  private SendingUnrequestingBinaryMessageMethod() {}

  public void apply(ResponsiveWsConnectionImpl responsiveWebSocketConnection, ByteBuffer message) {
    synchronized(responsiveWebSocketConnection) {
      SendingBinaryHeaderAndOneFragmentFn.instance.apply(
        responsiveWebSocketConnection._webSocketConnection,
        ResponsiveWsConnectionImpl._headerOfUnrequestingMessage,
        message
      );
    }
  }
}
