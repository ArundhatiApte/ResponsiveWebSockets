package rws.tests.checkСompatibilityWithVersionInJs.checks;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

import org.scalatest.Assertions.assert;

import rws.common.responsiveWebSocketConnection.api.{ResponsiveWsConnection => Rwsc};

final object checkSendingBinaryRequest extends Function2[Rwsc, Short, CompletableFuture[Void]] {
  override def apply(connection: Rwsc, multipler: Short): CompletableFuture[Void] = {
    val sendedShort: Short = 11;
    val expectedShortInResponse = sendedShort * multipler;
    val message = ByteBuffer.allocate(2).putShort(0, sendedShort);
    connection.sendBinaryRequest(message).thenAccept((response) => {
      val shortInResponse = response.getShort(connection.getStartIndexOfBodyInBinaryResponse());
      assert(expectedShortInResponse == shortInResponse);
    });
  }
}
