package rws.common.responsiveWebSocketConnection.impl.modules;

public final class SequenceGeneratorOfUint16 {
  public SequenceGeneratorOfUint16() {
    this._integer = 0;
  }

  private char _integer;

  public char getNextInt() {
    return this._integer++;
  }
}
