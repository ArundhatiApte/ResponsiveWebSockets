package rws.examples.utils;

import org.java_websocket.handshake.ClientHandshake;

import static rws.server.api.ResponsiveWsServer.EventsListener;
import rws.server.api.ResponsiveWsServerConnection;
import rws.server.api.HandshakeAction;

public class VoidServerEventsListener implements EventsListener {
  public void onUpgrade(ClientHandshake request, HandshakeAction handshakeAction) {}
  public void onConnection(ResponsiveWsServerConnection serverConnection) {}
}
