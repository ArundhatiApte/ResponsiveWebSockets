package rws.tests.testResponsiveWebSockets.checks.sendingMessages;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

import rws.common.responsiveWebSocketConnection.api.{ResponsiveWsConnection => Rwsc};
import rws.common.responsiveWebSocketConnection.api.ResponseSender;
import rws.common.responsiveWebSocketConnection.api.TimeoutToReceiveResponseException;

import rws.tests.utils.execOrReject;
import rws.tests.utils.HolderOfValue;
import rws.tests.utils.VoidEventsListener;
import rws.tests.utils.timeouts;
import rws.tests.utils.timeouts.Timeout;
import rws.tests.utils.createTimeoutForPromise;

import rws.tests.testResponsiveWebSockets.checks.sendingMessages.utils.createByteBufferFromUint8s;

final object checkTimeoutForReceivingResponse extends Function2[
  Rwsc,
  Rwsc,
  CompletableFuture[Void]
] {
  override def apply(
    sender: Rwsc,
    receiver: Rwsc
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

    val message = createByteBufferFromUint8s(5, 6, 7, 8);
    sender.sendBinaryRequest(message, msToWait).whenCompleteAsync((response, error) => {
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
  ): Rwsc.EventsListener = {
    new VoidEventsListener() {
      override def onBinaryRequest(
        c: Rwsc,
        message: ByteBuffer,
        startIndex: Int,
        senderOfResponse: ResponseSender
      ): Unit = {
        execOrReject(() => {
          holderOfTimeoutForSendingResponse.set(
            timeouts.setTimeout(
              () => senderOfResponse.sendBinaryResponse(createByteBufferFromUint8s(1, 2, 3, 4)),
              delayMsForSendingResponse
            )
          );
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
