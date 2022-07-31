package rws.common.responsiveWebSocketConnection.impl;

import java.nio.ByteBuffer;

import rws.common.responsiveWebSocketConnection.api.ResponsiveWsConnection;
import rws.common.responsiveWebSocketConnection.api.ResponseSender;

final class VoidEventsListener implements ResponsiveWsConnection.EventsListener {
  public static final VoidEventsListener instance = new VoidEventsListener();

  private VoidEventsListener() {}

  @Override
  public void onClose(ResponsiveWsConnection connection, int code, String reason, boolean isRemote) {}
  @Override
  public void onError(ResponsiveWsConnection connection, Throwable error) {}
  @Override
  public void onMalformedBinaryMessage(ResponsiveWsConnection connection, ByteBuffer message) {}
  @Override
  public void onTextMessage(ResponsiveWsConnection connection, String message) {}

  @Override
  public void onBinaryRequest(
    ResponsiveWsConnection connection,
    ByteBuffer messageWithHeader,
    int startIndex,
    ResponseSender responseSender
  ) {}

  @Override
  public void onUnrequestingBinaryMessage(
    ResponsiveWsConnection connection,
    ByteBuffer messageWithHeader,
    int startIndex
  ) {}
}
