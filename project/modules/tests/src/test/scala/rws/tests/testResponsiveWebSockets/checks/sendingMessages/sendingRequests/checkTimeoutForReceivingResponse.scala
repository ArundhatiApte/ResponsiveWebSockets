package rws.tests.testResponsiveWebSockets.checks.sendingMessages;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

import rws.common.responsiveWebSocketConnection.api.ResponsiveWsConnection;
import rws.common.responsiveWebSocketConnection.api.ResponseSender;
import rws.common.responsiveWebSocketConnection.api.TimeoutToReceiveResponseException;

import rws.tests.utils.execOrReject;
import rws.tests.utils.HolderOfValue;
import rws.tests.utils.VoidEventsListener;

import rws.tests.testResponsiveWebSockets.checks.utils.timeouts;
import rws.tests.testResponsiveWebSockets.checks.utils.timeouts.Timeout;
import rws.tests.testResponsiveWebSockets.checks.utils.createTimeoutForPromise;
import rws.tests.testResponsiveWebSockets.checks.sendingMessages.utils.createByteBufferFromUint8s;

final object checkTimeoutForReceivingResponse extends Function2[
  ResponsiveWsConnection,
  ResponsiveWsConnection,
  CompletableFuture[Void]
] {
  override def apply(
    sender: ResponsiveWsConnection,
    receiver: ResponsiveWsConnection
  ): CompletableFuture[Void] = {
    val checking = new CompletableFuture[Void]();
    val msToWait = 100;
    val timeoutForCheck = createTimeoutForPromise(checking, 2000);
    var holderOfTimeoutForSendingResponse = new HolderOfValue[Timeout]();

    receiver.setEventsListener(this._createEventsListener(
      holderOfTimeoutForSendingResponse,
      msToWait * 4,
      checking
    ));

    sender.sendBinaryRequest(createByteBufferFromUint8s(5, 6, 7, 8), msToWait).whenCompleteAsync((response, error) => {
      execOrReject(() => {
        _clearTimeoutsAndCheckError(holderOfTimeoutForSendingResponse.get(), timeoutForCheck, error, checking);
      }, checking);
    });

    checking;
  }

  private def _createEventsListener(
    holderOfTimeoutForSendingResponse: HolderOfValue[Timeout],
    delayMsForSendingResponse: Int,
    checking: CompletableFuture[Void]
  ): ResponsiveWsConnection.EventsListener = {
    new VoidEventsListener() {
      override def onBinaryRequest(
        rwsc: ResponsiveWsConnection,
        message: ByteBuffer,
        startIndex: Int,
        senderOfResponse: ResponseSender
      ): Unit = {
        execOrReject(() => {
          holderOfTimeoutForSendingResponse.set(timeouts.setTimeout(new Runnable() {
            override def run() {
              senderOfResponse.sendBinaryResponse(createByteBufferFromUint8s(1, 2, 3, 4));
            }
          }, delayMsForSendingResponse));
        }, checking);
      }
    };
  }

  private def _clearTimeoutsAndCheckError(
    timeoutForSendingResponse: Timeout,
    timeoutForCheck: Timeout,
    error: Throwable,
    checking:  CompletableFuture[Void]
  ): Unit = {
    if (timeoutForSendingResponse != null) {
      timeouts.clearTimeout(timeoutForSendingResponse);
    }
    timeouts.clearTimeout(timeoutForCheck);

    if (error == null) {
      checking.completeExceptionally(new RuntimeException("Response was received."));
    } else {
      if (error.isInstanceOf[TimeoutToReceiveResponseException]) {
        checking.complete(null);
      } else {
        checking.completeExceptionally(error);
      }
    }
  }
}
