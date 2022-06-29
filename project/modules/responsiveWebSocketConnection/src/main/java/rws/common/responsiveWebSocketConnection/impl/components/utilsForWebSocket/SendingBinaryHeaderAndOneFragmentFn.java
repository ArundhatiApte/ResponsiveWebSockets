package rws.common.responsiveWebSocketConnection.impl;

import java.nio.ByteBuffer;
import rws.common.webSocketConnection.WebSocketConnection;

final class SendingBinaryHeaderAndOneFragmentFn {
  public static SendingBinaryHeaderAndOneFragmentFn instance = new SendingBinaryHeaderAndOneFragmentFn();

  private SendingBinaryHeaderAndOneFragmentFn() {}

  public void apply(WebSocketConnection webSocketConnection, byte[] header, ByteBuffer fragment) {
    webSocketConnection.sendBinaryFragment(ByteBuffer.wrap(header), false);
    webSocketConnection.sendBinaryFragment(fragment, true);
  }
}
