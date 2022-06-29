package rws.tests.utils;

import java.nio.ByteBuffer;

import rws.common.responsiveWebSocketConnection.api.ResponsiveWsConnection;
import rws.common.responsiveWebSocketConnection.api.ResponsiveWsConnection.EventsListener;
import rws.common.responsiveWebSocketConnection.api.ResponseSender;

class VoidEventsListener extends EventsListener {
  private type RWSC = ResponsiveWsConnection;

  override def onClose(c: RWSC, code: Int, reason: String): Unit = {}
  override def onError(c: RWSC, error: Throwable): Unit = {}

  override def onMalformedBinaryMessage(c: RWSC, message: ByteBuffer): Unit = {}
  override def onTextMessage(c: RWSC, message: String): Unit = {}

  override def onBinaryRequest(
    c: RWSC,
    messageWithHeader: ByteBuffer,
    startIndex: Int,
    responseSender: ResponseSender
  ): Unit = {}

  override def onUnrequestingBinaryMessage(c: RWSC, messageWithHeader: ByteBuffer, startIndex: Int): Unit = {}
}

final object voidEventsListener extends VoidEventsListener;
