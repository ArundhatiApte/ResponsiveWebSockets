package rws.common.responsiveWebSocketConnection.impl.modules;

import org.scalatest.Assertions.assert;
import org.scalatest.funsuite.AnyFunSuite;

import rws.common.responsiveWebSocketConnection.impl.modules.SequenceGeneratorOfUint16;

final class SequenceGeneratorOfUint16Test extends AnyFunSuite {
  test("generating uint16s") {
    val maxNumber = Math.pow(2, 16).toInt - 1;
    val generator = new SequenceGeneratorOfUint16();

    var prevNum: Int = -1;
    var currentNum: Int = 0;

    for (i <- 0 to maxNumber) {
      currentNum = generator.getNextInt().toInt;
      assert((prevNum + 1) == currentNum);
      prevNum = currentNum;
    }
    currentNum = generator.getNextInt().toInt;
    assert(0 == currentNum);
  }
}
