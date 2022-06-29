package rws.tests.measureSpeed;

import java.io.PrintStream;

final object logger {
  final object labelsOfDirection {
    val fromServerToClient = "server -> client ";
    val fromClientToServer = "client -> server ";
  }

  def writeHeader(stream: PrintStream, countOfRequests: Int): Unit = {
    stream.print(countOfRequests);
    stream.println(" binary requests");
    stream.println("direction        time (ms)");
  }

  def writeRowWithResult(stream: PrintStream, labelOfDirection: String, timeMs: Int) {
    stream.print(labelOfDirection);
    stream.println(timeMs);
  }
}
