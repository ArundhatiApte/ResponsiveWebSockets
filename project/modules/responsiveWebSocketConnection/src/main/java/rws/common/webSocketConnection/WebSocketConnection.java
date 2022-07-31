package rws.common.webSocketConnection;

import java.nio.ByteBuffer;

public interface WebSocketConnection {
  public String getUrl();

  public void close();
  public void close(int code);
  public void close(int code, String reason);

  public void terminate();

  public <T> T getAttachment();
  public <T> void setAttachment(T attachment);

  public void sendBinaryFragment(ByteBuffer fragment, boolean isLast);
  public void sendTextMessage(String message);
  public void setEventsListener(WebSocketConnection.EventsListener eventsListener);

  public static interface EventsListener {
    public void onClose(WebSocketConnection webSocketConnection, int code, String reason, boolean isRemote);
    public void onError(WebSocketConnection webSocketConnection, Throwable error);
    public void onBinaryMessage(WebSocketConnection webSocketConnection, ByteBuffer message);
    public void onTextMessage(WebSocketConnection webSocketConnection, String message);
  }
}
