package rws.tests.utils;

import java.nio.ByteBuffer;

import rws.common.responsiveWebSocketConnection.api.{ResponsiveWsConnection => Rwsc};
import rws.common.responsiveWebSocketConnection.api.ResponsiveWsConnection.EventsListener;
import rws.common.responsiveWebSocketConnection.api.ResponseSender;

class VoidEventsListener extends EventsListener {
  override def onClose(c: Rwsc, code: Int, reason: String): Unit = {}
  override def onError(c: Rwsc, error: Throwable): Unit = {}

  override def onMalformedBinaryMessage(c: Rwsc, message: ByteBuffer): Unit = {}
  override def onTextMessage(c: Rwsc, message: String): Unit = {}

  override def onBinaryRequest(
    c: Rwsc,
    messageWithHeader: ByteBuffer,
    startIndex: Int,
    responseSender: ResponseSender
  ): Unit = {}

  override def onUnrequestingBinaryMessage(c: Rwsc, messageWithHeader: ByteBuffer, startIndex: Int): Unit = {}
}
