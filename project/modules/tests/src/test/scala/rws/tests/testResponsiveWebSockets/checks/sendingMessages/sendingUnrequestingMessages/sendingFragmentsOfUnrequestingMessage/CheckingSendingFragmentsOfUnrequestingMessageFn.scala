package rws.tests.testResponsiveWebSockets.checks.sendingMessages;

import java.util.concurrent.CompletableFuture;

import rws.common.responsiveWebSocketConnection.api.{ResponsiveWsConnection => Rwsc};

import rws.tests.utils.timeouts;
import rws.tests.utils.timeouts.Timeout;
import rws.tests.utils.createTimeoutForPromise;

abstract class CheckingSendingFragmentsOfUnrequestingMessageFn[Content] extends Function2[
  Rwsc,
  Rwsc,
  CompletableFuture[Void]
] {
  override def apply(
    sender: Rwsc,
    receiver: Rwsc
  ): CompletableFuture[Void] = {
    val checking = new CompletableFuture[Void]();
    val timeoutForCheck = createTimeoutForPromise(checking);

    val fragmentsOfMessage = _getFragmentsOfMessage();
    val fullMessage = _getFullMessage();
    receiver.setEventsListener(_createEventsListener(fullMessage, timeoutForCheck, checking));
    _sendFragmentsOfUnrequestingMessage(sender, fragmentsOfMessage);

    checking;
  }

  protected def _getFragmentsOfMessage(): Array[Content];
  protected def _getFullMessage(): Content;

  protected def _createEventsListener(
    fullMessage: Content,
    timeoutForCheck: Timeout,
    checking: CompletableFuture[Void]
  ): Rwsc.EventsListener;

  protected def _sendFragmentsOfUnrequestingMessage(sender: Rwsc, fragmentsOfMessage: Array[Content]): Unit;
}
