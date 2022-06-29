package rws.common.responsiveWebSocketConnection.api;

import java.nio.ByteBuffer;

public interface ResponseSender {
  public void sendBinaryResponse(ByteBuffer message);
  public void sendFragmentsOfBinaryResponse(ByteBuffer... fragments);
}
