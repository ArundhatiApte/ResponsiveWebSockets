package rws.common.responsiveWebSocketConnection.impl.modules.messaging;

public final class TypeOfIncomingMessage {
  private TypeOfIncomingMessage() {}

  public static final TypeOfIncomingMessage request = new TypeOfIncomingMessage();
  public static final TypeOfIncomingMessage response = new TypeOfIncomingMessage();
  public static final TypeOfIncomingMessage unrequestingMessage = new TypeOfIncomingMessage();
}
