package rws.examples.utils;

import java.io.PrintStream;
import java.nio.ByteBuffer;

public final class PrintingByteBufferFn {
  public static void printlnByteBuffer(PrintStream out, ByteBuffer bb, int startIndex) {
    byte[] bytes = bb.array();
    out.print("{ int8s: ");

    for (int i = startIndex; i < bytes.length; i += 1) {
      out.print(bytes[i]);
      out.print(", ");
    }
    out.println("}");
  }
}

