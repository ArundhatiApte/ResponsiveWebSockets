package rws.common.responsiveWebSocketConnection.impl;

import java.nio.ByteBuffer;

import rws.common.webSocketConnection.WebSocketConnection;

import rws.common.responsiveWebSocketConnection.impl.ResponsiveWsConnectionImpl;
import rws.common.responsiveWebSocketConnection.impl.EmittingEventByIncomingBinaryMessageFn;

final class ListenerOfEventsFromWebSocketConnection implements WebSocketConnection.EventsListener {
  public static final ListenerOfEventsFromWebSocketConnection instance = new ListenerOfEventsFromWebSocketConnection();

  private ListenerOfEventsFromWebSocketConnection() {}

  // ResponsiveWebSocketConnectionImpl use VoidEventsListener by default and
  // restricts to set null _eventsListener

  @Override
  public void onClose(WebSocketConnection webSocketConnection, int code, String reason, boolean isRemote) {
    ResponsiveWsConnectionImpl rwsc = webSocketConnection.<ResponsiveWsConnectionImpl>getAttachment();
    rwsc._eventsListener.onClose(rwsc, code, reason, isRemote);
  }

  @Override
  public void onError(WebSocketConnection webSocketConnection, Throwable error) {
    ResponsiveWsConnectionImpl rwsc = webSocketConnection.<ResponsiveWsConnectionImpl>getAttachment();
    rwsc._eventsListener.onError(rwsc, error);
  }

  @Override
  public void onBinaryMessage(WebSocketConnection webSocketConnection, ByteBuffer message) {
    EmittingEventByIncomingBinaryMessageFn.instance.apply(webSocketConnection, message);
  }

  @Override
  public void onTextMessage(WebSocketConnection webSocketConnection, String message) {
    ResponsiveWsConnectionImpl rwsc = webSocketConnection.<ResponsiveWsConnectionImpl>getAttachment();
    rwsc._eventsListener.onTextMessage(rwsc, message);
  }
}
