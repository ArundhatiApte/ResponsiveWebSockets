package rws.tests.checkСompatibilityWithVersionInJs.checks;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

import org.scalatest.Assertions.assert;

import rws.common.responsiveWebSocketConnection.api.{ResponsiveWsConnection => Rwsc};

import rws.tests.utils.timeouts;
import rws.tests.utils.timeouts.Timeout;
import rws.tests.utils.createTimeoutForPromise;
import rws.tests.utils.VoidEventsListener;

final object checkSendingUnrequestingBinaryMessages extends Function1[Rwsc, CompletableFuture[Void]] {
  override def apply(connection: Rwsc): CompletableFuture[Void] = {
    val checking = new CompletableFuture[Void]();
    val timeout = createTimeoutForPromise(checking);
    val sendedInt = 6789;
    val message = ByteBuffer.allocate(4).putInt(0, sendedInt);

    connection.setEventsListener(new VoidEventsListener() {
      override def onUnrequestingBinaryMessage(c: Rwsc, messageWithHeader: ByteBuffer, startIndex: Int): Unit = {
        timeouts.clearTimeout(timeout);
        message.rewind();
        messageWithHeader.position(startIndex);
        if (message.equals(messageWithHeader)) {
          checking.complete(null);
        } else {
          checking.completeExceptionally(new RuntimeException("Different messages."));
        }
      }
    });
    connection.sendUnrequestingBinaryMessage(message);
    checking;
  }
}
