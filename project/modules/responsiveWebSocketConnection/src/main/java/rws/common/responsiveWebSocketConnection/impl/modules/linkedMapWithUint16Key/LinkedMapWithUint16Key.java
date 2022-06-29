package rws.common.responsiveWebSocketConnection.impl.modules.linkedMapWithUint16Key;

import rws.common.responsiveWebSocketConnection.impl.modules.linkedMapWithUint16Key.Entry;

public final class LinkedMapWithUint16Key<V> {
  public LinkedMapWithUint16Key() {
    this._firstEntry = null;
  }

  private Entry<V> _firstEntry;

  public void put(char uint16, V value) {
    Entry<V> entry = this._firstEntry;

    if (entry == null) {
      this._firstEntry = Entry.<V>createWithoutNext(uint16, value);
      return;
    }

    while (true) {
      if (entry._uint16Key == uint16) {
        entry._value = value;
        return;
      }
      if (entry._nextEntry == null) {
        entry._nextEntry = Entry.<V>createWithoutNext(uint16, value);
        return;
      }
      entry = entry._nextEntry;
    }
  }

  public V getAndRemoveIfHas(char uint16) {
    Entry<V> currentEntry = this._firstEntry;
    if (currentEntry == null) {
      return null;
    }
    if (currentEntry._uint16Key == uint16) {
      this._firstEntry = currentEntry._nextEntry;
      return currentEntry._value;
    }
    Entry<V> prevEntry = currentEntry;
    currentEntry = currentEntry._nextEntry;

    while (true) {
      if (currentEntry == null) {
        return null;
      }
      if (currentEntry._uint16Key == uint16) {
        prevEntry._nextEntry = currentEntry._nextEntry;
        return currentEntry._value;
      }
      prevEntry = currentEntry;
      currentEntry = currentEntry._nextEntry;
    }
  }
}
