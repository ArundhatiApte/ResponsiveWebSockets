package rws.common.responsiveWebSocketConnection.impl;

import java.nio.ByteBuffer;
import rws.common.webSocketConnection.WebSocketConnection;

final class SendingBinaryHeaderAndFragmentsFn {
  public static final SendingBinaryHeaderAndFragmentsFn instance = new SendingBinaryHeaderAndFragmentsFn();

  private SendingBinaryHeaderAndFragmentsFn() {}

  public void apply(WebSocketConnection webSocketConnection, byte[] binaryHeader, ByteBuffer[] fragments) {
    webSocketConnection.sendBinaryFragment(ByteBuffer.wrap(binaryHeader), false);

    int lastIndex = fragments.length - 1;
    for (int i = 0; i < lastIndex; i += 1) {
      webSocketConnection.sendBinaryFragment(fragments[i], false);
    }
    webSocketConnection.sendBinaryFragment(fragments[lastIndex], true);
  }
}
