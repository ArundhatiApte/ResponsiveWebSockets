package rws.common.responsiveWebSocketConnection.impl.modules.linkedMapWithUint16Key;

final class Entry<V> {

  protected static final <V> Entry<V> createWithoutNext(char uint16, V value) {
    return new Entry<V>(uint16, value, null);
  }

  private Entry(char uint16, V value, Entry<V> nextEntry) {
    this._uint16Key = uint16;
    this._value = value;
    this._nextEntry = nextEntry;
  }

  protected char _uint16Key;
  protected V _value;
  protected Entry<V> _nextEntry;
}
