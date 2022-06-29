package rws.common.responsiveWebSocketConnection.impl.modules;

import org.scalatest.Assertions.assert;
import org.scalatest.funsuite.AnyFunSuite;

import rws.common.responsiveWebSocketConnection.impl.modules.timeouts.Timeout;
import rws.common.responsiveWebSocketConnection.impl.modules.timeouts.Timeouts;

final class TimeoutsTest extends AnyFunSuite {
  test("setting timeout") {
    val before = System.currentTimeMillis();
    val delayInMilliseconds: Int = 200;
    val millisecondsInTimeout = new LongHolder(0);

    Timeouts.setTimeout(new Runnable() {
      override def run() {
        millisecondsInTimeout.set(System.currentTimeMillis());
      }
    }, delayInMilliseconds);
    utils.wait(delayInMilliseconds + 100);

    val after = System.currentTimeMillis();
    assert(after != 0);
    val diffInMilliseconds = after - before;
    assert((diffInMilliseconds >= delayInMilliseconds) == true);
  }

  test("clearing timeout") {
    val delayInMilliseconds: Int = 200;
    val num = new LongHolder(0);

    val timeout = Timeouts.setTimeout(new Runnable() {
      override def run() {
        num.set(99);
      }
    }, delayInMilliseconds);

    utils.wait(delayInMilliseconds / 2);
    Timeouts.clearTimeout(timeout);
    assert(num.get() == 0);
    utils.wait(delayInMilliseconds / 4);
    assert(num.get() == 0);
    utils.wait(delayInMilliseconds / 2);
    assert(num.get() == 0);
  }
}

class LongHolder(num: Long) {
  private var _num: Long = num;

  def get() = _num;

  def set(num: Long) {
    this._num = num;
  }
}

object utils {
  def wait(milliseconds: Int) {
    try {
      Thread.sleep(milliseconds);
    } catch {
      case error: InterruptedException => {}
    }
  }
}
