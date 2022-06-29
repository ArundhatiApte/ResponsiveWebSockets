package rws.tests.testResponsiveWebSockets.checks.sendingMessages;

import java.util.concurrent.CompletableFuture;

import rws.common.responsiveWebSocketConnection.api.ResponsiveWsConnection;

import rws.tests.testResponsiveWebSockets.checks.utils.timeouts;
import rws.tests.testResponsiveWebSockets.checks.utils.timeouts.Timeout;
import rws.tests.testResponsiveWebSockets.checks.utils.createTimeoutForPromise;

abstract class CheckingSendingFragmentsOfUnrequestingMessageFn[Content] extends Function2[
  ResponsiveWsConnection,
  ResponsiveWsConnection,
  CompletableFuture[Void]
] {
  protected type RWSC = ResponsiveWsConnection;

  override def apply(
    sender: ResponsiveWsConnection,
    receiver: ResponsiveWsConnection
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
  ): ResponsiveWsConnection.EventsListener;

  protected def _sendFragmentsOfUnrequestingMessage(sender: RWSC, fragmentsOfMessage: Array[Content]): Unit;
}
