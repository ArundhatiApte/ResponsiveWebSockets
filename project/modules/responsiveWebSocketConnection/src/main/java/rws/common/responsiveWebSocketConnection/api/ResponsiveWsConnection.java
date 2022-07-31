package rws.common.responsiveWebSocketConnection.api;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

import rws.common.responsiveWebSocketConnection.api.ResponseSender;

public interface ResponsiveWsConnection {
  public void close();
  public void close(int code);
  public void close(int code, String reason);
  public void terminate();

  public <T> T getAttachment();
  public <T> void setAttachment(T attachment);

  public int getStartIndexOfBodyInBinaryResponse();
  public String getUrl();

  public CompletableFuture<ByteBuffer> sendBinaryRequest(ByteBuffer message);
  public CompletableFuture<ByteBuffer> sendBinaryRequest(ByteBuffer message, int maxTimeMsToWaitResponse);
  public CompletableFuture<ByteBuffer> sendFragmentsOfBinaryRequest(ByteBuffer... fragments);

  public void sendUnrequestingBinaryMessage(ByteBuffer message);
  public void sendFragmentsOfUnrequestingBinaryMessage(ByteBuffer... fragments);

  public void setMaxTimeMsToWaitResponse(int timeMs);
  public void setEventsListener(ResponsiveWsConnection.EventsListener listener);

  public static interface EventsListener {
    public void onClose(ResponsiveWsConnection connection, int code, String reason, boolean isRemote);
    public void onError(ResponsiveWsConnection connection, Throwable error);

    public void onMalformedBinaryMessage(ResponsiveWsConnection connection, ByteBuffer message);
    public void onTextMessage(ResponsiveWsConnection connection, String message);

    public void onBinaryRequest(
      ResponsiveWsConnection connection,
      ByteBuffer messageWithHeader,
      int startIndex,
      ResponseSender responseSender
    );
    public void onUnrequestingBinaryMessage(
      ResponsiveWsConnection connection,
      ByteBuffer messageWithHeader,
      int startIndex
    );
  }
}

