package rws.common.responsiveWebSocketConnection.impl;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

import rws.common.responsiveWebSocketConnection.impl.modules.messaging.BinaryMessager;
import rws.common.responsiveWebSocketConnection.impl.ResponsiveWsConnectionImpl;
import rws.common.responsiveWebSocketConnection.impl.PreparingToSendBinaryRequestFn;
import rws.common.responsiveWebSocketConnection.impl.SendingBinaryHeaderAndOneFragmentFn;

final class SendingBinaryRequestMethod {
  public static final SendingBinaryRequestMethod instance = new SendingBinaryRequestMethod();

  private SendingBinaryRequestMethod() {}

  public CompletableFuture<ByteBuffer> apply(
    ResponsiveWsConnectionImpl responsiveWebSocketConnection,
    ByteBuffer message,
    int maxTimeMsToWaitResponse
  ) {
    synchronized(responsiveWebSocketConnection) {
      CompletableFuture<ByteBuffer> gettingResponse = new CompletableFuture<>();
      char idOfRequest = PreparingToSendBinaryRequestFn.instance.apply(
        responsiveWebSocketConnection,
        gettingResponse,
        maxTimeMsToWaitResponse
      );
      _sendBinaryMessageOfRequest(responsiveWebSocketConnection, idOfRequest, message);
      return gettingResponse;
    }
  }

  private static void _sendBinaryMessageOfRequest(
    ResponsiveWsConnectionImpl responsiveWebSocketConnection,
    char idOfRequest,
    ByteBuffer message
  ) {
    byte[] header = responsiveWebSocketConnection._headerForBinaryRequestOrResponse;
    BinaryMessager.instance.fillHeaderAsRequest(idOfRequest, header);

    SendingBinaryHeaderAndOneFragmentFn.instance.apply(
      responsiveWebSocketConnection._webSocketConnection,
      header,
      message
    );
  }
}
