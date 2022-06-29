package rws.common.responsiveWebSocketConnection.impl;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

import rws.common.responsiveWebSocketConnection.impl.modules.messaging.BinaryMessager;
import rws.common.responsiveWebSocketConnection.impl.ResponsiveWsConnectionImpl;
import rws.common.responsiveWebSocketConnection.impl.PreparingToSendBinaryRequestFn;
import rws.common.responsiveWebSocketConnection.impl.SendingBinaryHeaderAndFragmentsFn;

final class SendingFragmentsOfBinaryRequestMethod {
  public static final SendingFragmentsOfBinaryRequestMethod instance = new SendingFragmentsOfBinaryRequestMethod();

  private SendingFragmentsOfBinaryRequestMethod() {}

  public CompletableFuture<ByteBuffer> apply(
    ResponsiveWsConnectionImpl responsiveWebSocketConnection,
    ByteBuffer[] fragments,
    int maxTimeMsToWaitResponse
  ) {
    synchronized(responsiveWebSocketConnection) {
      CompletableFuture<ByteBuffer> gettingResponse = new CompletableFuture<>();
      char idOfRequest = PreparingToSendBinaryRequestFn.instance.apply(
        responsiveWebSocketConnection,
        gettingResponse,
        maxTimeMsToWaitResponse
      );
      _sendBinaryFragmentsOfRequest(responsiveWebSocketConnection, idOfRequest, fragments);
      return gettingResponse;
    }
  }

  private static void _sendBinaryFragmentsOfRequest(
    ResponsiveWsConnectionImpl responsiveWebSocketConnection,
    char idOfRequest,
    ByteBuffer[] fragments
  ) {
    byte[] header = responsiveWebSocketConnection._headerForBinaryRequestOrResponse;
    BinaryMessager.instance.fillHeaderAsRequest(idOfRequest, header);
    SendingBinaryHeaderAndFragmentsFn.instance.apply(
      responsiveWebSocketConnection._webSocketConnection,
      header,
      fragments
    );
  }
}
