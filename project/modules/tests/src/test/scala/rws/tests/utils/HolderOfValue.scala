package rws.tests.utils;

final class HolderOfValue[V](value: V = null) {
  private var _value: V = value;

  def get() = _value;

  def set(value: V): Unit = {
    this._value = value;
  }
}
