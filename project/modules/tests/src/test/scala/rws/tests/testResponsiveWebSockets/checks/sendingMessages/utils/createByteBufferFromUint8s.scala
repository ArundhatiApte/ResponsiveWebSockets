package rws.tests.testResponsiveWebSockets.checks.sendingMessages.utils;

import java.nio.ByteBuffer;

final object createByteBufferFromUint8s {
  def apply(uint8s: Int*): ByteBuffer = {
    val countOfUints = uint8s.size;
    val bytes = new Array[Byte](countOfUints);

    for (i <- 0 to (countOfUints - 1)) {
      bytes(i) = uint8s(i).toByte;
    }
    ByteBuffer.wrap(bytes);
  }
}
