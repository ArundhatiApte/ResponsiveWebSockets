package rws.common.responsiveWebSocketConnection.impl;

import java.nio.ByteBuffer;

import rws.common.webSocketConnection.WebSocketConnection;
import rws.common.responsiveWebSocketConnection.impl.ResponsiveWsConnectionImpl;
import rws.common.responsiveWebSocketConnection.impl.SendingBinaryHeaderAndFragmentsFn;

final class SendingFragmentsOfUnrequestingBinaryMessageMethod {
  public static final SendingFragmentsOfUnrequestingBinaryMessageMethod instance =
    new SendingFragmentsOfUnrequestingBinaryMessageMethod();

  private SendingFragmentsOfUnrequestingBinaryMessageMethod() {}

  public void apply(ResponsiveWsConnectionImpl responsiveWebSocketConnection, ByteBuffer... fragments) {
    synchronized(responsiveWebSocketConnection) {
      SendingBinaryHeaderAndFragmentsFn.instance.apply(
        responsiveWebSocketConnection._webSocketConnection,
        ResponsiveWsConnectionImpl._headerOfUnrequestingMessage,
        fragments
      );
    }
  }
}
