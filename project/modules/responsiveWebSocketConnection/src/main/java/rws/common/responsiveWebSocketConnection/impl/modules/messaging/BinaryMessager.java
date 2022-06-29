package rws.common.responsiveWebSocketConnection.impl.modules.messaging;

import java.nio.ByteBuffer;

import rws.common.responsiveWebSocketConnection.impl.modules.messaging.TypeOfIncomingMessage;
import rws.common.responsiveWebSocketConnection.impl.modules.messaging.ExceptionAtParsing;

public final class BinaryMessager {
  private BinaryMessager() {}

  public static final BinaryMessager instance = new BinaryMessager();

  public final int sizeOfHeaderForRequestOrResponse = 3;
  public final int sizeOfHeaderForUnrequestingMessage = 3;

  private static final class _ByteHeader {
    public static final byte request = 1;
    public static final byte response = 2;
    public static final byte unrequestingMessage = 3;
  }

  public void fillHeaderAsRequest(char uint16IdOfMessage, byte[] bytes) {
    _fillHeaderAsRequestOrResponse(_ByteHeader.request, uint16IdOfMessage, bytes);
  }

  public void fillHeaderAsResponse(char uint16IdOfMessage, byte[] bytes) {
    _fillHeaderAsRequestOrResponse(_ByteHeader.response, uint16IdOfMessage, bytes);
  }

  private static void _fillHeaderAsRequestOrResponse(byte header, char uint16IdOfMessage, byte[] bytes) {
    bytes[0] = header;
    bytes[1] = (byte) (uint16IdOfMessage >>> 8);
    bytes[2] = (byte) uint16IdOfMessage;
  }

  public void fillHeaderAsUnrequestingMessage(byte[] bytes) {
    bytes[0] = _ByteHeader.unrequestingMessage;
  }

  public TypeOfIncomingMessage extractTypeOfMessage(ByteBuffer message) {
    byte header1stByte = message.get(0);

    switch (header1stByte) {
      case _ByteHeader.request:
        return TypeOfIncomingMessage.request;
      case _ByteHeader.response:
        return TypeOfIncomingMessage.response;
      case _ByteHeader.unrequestingMessage:
        return TypeOfIncomingMessage.unrequestingMessage;
    }
    throw new ExceptionAtParsing("Message has unrecognized header.");
  }

  public char extractIdOfMessage(ByteBuffer message) {
    return message.getChar(1);
  }
}
