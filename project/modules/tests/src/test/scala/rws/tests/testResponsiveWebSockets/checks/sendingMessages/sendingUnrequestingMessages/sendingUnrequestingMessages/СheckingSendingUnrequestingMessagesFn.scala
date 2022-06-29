package rws.tests.testResponsiveWebSockets.checks.sendingMessages;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

import scala.collection.mutable.ArrayBuffer;

import rws.common.responsiveWebSocketConnection.api.ResponsiveWsConnection;

import rws.tests.utils.HolderOfValue;

import rws.tests.testResponsiveWebSockets.checks.utils.timeouts;
import rws.tests.testResponsiveWebSockets.checks.utils.timeouts.Timeout;
import rws.tests.testResponsiveWebSockets.checks.utils.createTimeoutForPromise;

abstract class СheckingSendingUnrequestingMessagesFn[Content <: Comparable[Content]] extends Function2[
  ResponsiveWsConnection,
  ResponsiveWsConnection,
  CompletableFuture[Void]
] {
  protected type RWSC = ResponsiveWsConnection;

  override final def apply(sender: RWSC, receiver: RWSC): CompletableFuture[Void] = {
    val checking = new CompletableFuture[Void]();
    val timeoutForCheck = createTimeoutForPromise(checking);

    val sendedMessages = _createSendedMessages();
    val countOfMessages = sendedMessages.length;
    val countOfMessagesHolder = new HolderOfValue[Int](countOfMessages);
    val receivedMessages = new ArrayBuffer[Content](countOfMessages);
    val waitingToSendAllMessages = new CountDownLatch(1);

    receiver.setEventsListener(_createEventsListener(
      countOfMessagesHolder,
      receivedMessages,
      timeoutForCheck,
      sendedMessages,
      waitingToSendAllMessages,
      checking
    ));

    for (message <- sendedMessages) {
      _sendUnrequestingMessage(sender, message);
    }
    waitingToSendAllMessages.countDown();
    return checking;
  }

  protected def _createSendedMessages(): Array[Content];

  protected def _createEventsListener(
    countOfMessagesHolder: HolderOfValue[Int],
    receivedMessages: ArrayBuffer[Content],
    timeoutForCheck: Timeout,
    sendedMessages: Array[Content],
    waitingToSendAllMessages: CountDownLatch,
    checking: CompletableFuture[Void]
  ): ResponsiveWsConnection.EventsListener;

  protected final def _addMessageThenCompareIfAll(
    countOfMessagesHolder: HolderOfValue[Int],
    receivedMessage: Content,
    receivedMessages: ArrayBuffer[Content],
    timeoutForCheck: Timeout,
    sendedMessages: Array[Content],
    waitingToSendAllMessages: CountDownLatch,
    checking: CompletableFuture[Void]
  ): Unit = {
    val countOfMessagesToReceive = countOfMessagesHolder.get() - 1;
    countOfMessagesHolder.set(countOfMessagesToReceive);
    receivedMessages.addOne(receivedMessage);

    if (countOfMessagesToReceive == 0) {
      timeouts.clearTimeout(timeoutForCheck);
      waitingToSendAllMessages.await();
      receivedMessages.sortInPlace();
      if (_areArraysOfMessagesEqual(sendedMessages.sorted, receivedMessages)) {
        checking.complete(null);
      } else {
        checking.completeExceptionally(new RuntimeException("Different messages."));
      }
    }
  }

  protected def _sendUnrequestingMessage(sender: ResponsiveWsConnection, message: Content): Unit;

  private def _areArraysOfMessagesEqual(a: Array[Content], b: ArrayBuffer[Content]): Boolean = {
    val aLength = a.length;
    if (aLength != b.length) {
      return false;
    }
    for (i <- 0 to (aLength - 1)) {
      if (a(i).compareTo(b(i)) != 0) {
        return false;
      }
    }
    return true;
  }
}
