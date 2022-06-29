package rws.common.responsiveWebSocketConnection.impl.modules.uint16KeyMap;

import org.scalatest.Assertions.assert;
import org.scalatest.funsuite.AnyFunSuite;

import rws.common.responsiveWebSocketConnection.impl.modules.linkedMapWithUint16Key.LinkedMapWithUint16Key;

final class LinkedMapWithUint16KeyTest extends AnyFunSuite {
  test("setting, getting with removing") {
    val map = new LinkedMapWithUint16Key[Value]();

    val key1: Char = 1;
    val value1 = new Value();

    val key2: Char = 2;
    val value2 = new Value();

    val key3: Char = 3;
    val value3 = new Value();

    val key4: Char = 4;
    val value4 = new Value();

    val key5: Char = 5;
    val value5 = new Value();

    val key6: Char = 6;
    val value6 = new Value();

    val nonExistentKey1: Char = 10;
    val nonExistentKey2: Char = 11;

    utils.getByKeyAndAssertEquals[Value](null, map, nonExistentKey1);
    utils.getByKeyAndAssertEquals[Value](null, map, nonExistentKey2);

    map.put(key1, value1);
    utils.getByKeyAndAssertEqualsThenCheckNull[Value](value1, map, key1);

    map.put(key1, value1);
    map.put(key2, value2);
    map.put(key1, value1);
    map.put(key3, value3);
    map.put(key4, value4);
    map.put(key5, value5);
    map.put(key6, value6);
    map.put(key6, value6);
    map.put(key4, value4);
    map.put(key1, value1);

    utils.getByKeyAndAssertEqualsThenCheckNull[Value](value6, map, key6); // end
    utils.getByKeyAndAssertEqualsThenCheckNull[Value](value1, map, key1); // start
    utils.getByKeyAndAssertEqualsThenCheckNull[Value](value3, map, key3); // middle

    map.put(key6, value6);
    map.put(key1, value1);
    map.put(key3, value3);

    utils.getByKeyAndAssertEqualsThenCheckNull[Value](value4, map, key4);
    utils.getByKeyAndAssertEqualsThenCheckNull[Value](value2, map, key2);

    utils.getByKeyAndAssertEquals[Value](null, map, nonExistentKey2);
  }
}

final class Value {}

final object utils {
  def getByKeyAndAssertEquals[V](expectedValue: V, map: LinkedMapWithUint16Key[V], key: Char) {
    assert(expectedValue == map.getAndRemoveIfHas(key));
  }
  def getByKeyAndAssertEqualsThenCheckNull[V](expectedValue: V, map: LinkedMapWithUint16Key[V], key: Char) {
    getByKeyAndAssertEquals(expectedValue, map, key);
    assert(null == map.getAndRemoveIfHas(key));
  }
}
