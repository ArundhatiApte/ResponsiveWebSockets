package rws.examples.utils;

import java.nio.ByteBuffer;

import rws.common.responsiveWebSocketConnection.api.ResponsiveWsConnection;
import rws.common.responsiveWebSocketConnection.api.ResponseSender;

public class VoidConnectionEventsListener implements ResponsiveWsConnection.EventsListener {
  @Override
  public void onClose(ResponsiveWsConnection c, int code, String reason) {}

  @Override
  public void onError(ResponsiveWsConnection c, Throwable error) {}

  @Override
  public void onMalformedBinaryMessage(ResponsiveWsConnection c, ByteBuffer message) {}

  @Override
  public void onTextMessage(ResponsiveWsConnection c, String message) {}

  @Override
  public void onBinaryRequest(
    ResponsiveWsConnection c,
    ByteBuffer messageWithHeader,
    int startIndex,
    ResponseSender responseSender
  ) {}

  @Override
  public void onUnrequestingBinaryMessage(
    ResponsiveWsConnection c,
    ByteBuffer messageWithHeader,
    int startIndex
  ) {}
}
