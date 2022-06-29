package rws.common.responsiveWebSocketConnection.impl;

import java.nio.ByteBuffer;

import rws.common.responsiveWebSocketConnection.api.ResponsiveWsConnection;
import rws.common.responsiveWebSocketConnection.api.ResponseSender;

final class VoidEventsListener implements ResponsiveWsConnection.EventsListener {
  public static final VoidEventsListener instance = new VoidEventsListener();

  private VoidEventsListener() {}

  public void onClose(ResponsiveWsConnection connection, int code, String reason) {}
  public void onError(ResponsiveWsConnection connection, Throwable error) {}

  public void onMalformedBinaryMessage(ResponsiveWsConnection connection, ByteBuffer message) {}
  public void onTextMessage(ResponsiveWsConnection connection, String message) {}

  public void onBinaryRequest(
    ResponsiveWsConnection connection,
    ByteBuffer messageWithHeader,
    int startIndex,
    ResponseSender responseSender
  ) {}

  public void onUnrequestingBinaryMessage(ResponsiveWsConnection connection, ByteBuffer messageWithHeader, int startIndex) {}
}
