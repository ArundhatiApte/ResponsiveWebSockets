package rws.common.responsiveWebSocketConnection.impl;

import java.nio.ByteBuffer;

import rws.common.webSocketConnection.WebSocketConnection;

import static rws.common.responsiveWebSocketConnection.api.ResponsiveWsConnection.EventsListener;

import rws.common.responsiveWebSocketConnection.impl.ResponsiveWsConnectionImpl;
import rws.common.responsiveWebSocketConnection.impl.EntryAboutPromiseOfBinaryRequest;
import rws.common.responsiveWebSocketConnection.impl.ResponseSenderImpl;

import rws.common.responsiveWebSocketConnection.impl.modules.messaging.BinaryMessager;
import rws.common.responsiveWebSocketConnection.impl.modules.messaging.TypeOfIncomingMessage;
import rws.common.responsiveWebSocketConnection.impl.modules.messaging.ExceptionAtParsing;

import rws.common.responsiveWebSocketConnection.impl.modules.linkedMapWithUint16Key.LinkedMapWithUint16Key;
import rws.common.responsiveWebSocketConnection.impl.modules.timeouts.Timeout;
import rws.common.responsiveWebSocketConnection.impl.modules.timeouts.Timeouts;

final class EmittingEventByIncomingBinaryMessageFn {
  public static final EmittingEventByIncomingBinaryMessageFn instance = new EmittingEventByIncomingBinaryMessageFn();

  private EmittingEventByIncomingBinaryMessageFn() {}

  public void apply(WebSocketConnection webSocketConnection, ByteBuffer message) {
    BinaryMessager binaryMessager = BinaryMessager.instance;
    TypeOfIncomingMessage typeOfMessage;
    ResponsiveWsConnectionImpl rwsc = webSocketConnection.<ResponsiveWsConnectionImpl>getAttachment();

    try {
      typeOfMessage = binaryMessager.extractTypeOfMessage(message);
    } catch(ExceptionAtParsing error) {
      _emitMalformedMessageEvent(rwsc, message);
      return;
    }

    if (typeOfMessage == TypeOfIncomingMessage.response) {
      _resolveAwaitingResponseMessagePromise(rwsc, message);
      return;
    }
    if (typeOfMessage == TypeOfIncomingMessage.unrequestingMessage) {
      _emitUnrequestingMessageEvent(
        rwsc,
        message,
        binaryMessager.sizeOfHeaderForUnrequestingMessage
      );
      return;
    }
    if (typeOfMessage == TypeOfIncomingMessage.request) {
      _emitAwaitingResponseMessageEvent(
        rwsc,
        message,
        binaryMessager.sizeOfHeaderForRequestOrResponse
      );
      return;
    }
  }

  private static void _emitMalformedMessageEvent(ResponsiveWsConnectionImpl rwsc, ByteBuffer message) {
    rwsc._eventsListener.onMalformedBinaryMessage(rwsc, message);
  }

  private static void _resolveAwaitingResponseMessagePromise(
    ResponsiveWsConnectionImpl responsiveWebSocketConnection,
    ByteBuffer message
  ) {
    char numOfMessage;
    try {
      numOfMessage = BinaryMessager.instance.extractIdOfMessage(message);
    } catch(Throwable error) {
      _emitMalformedMessageEvent(responsiveWebSocketConnection, message);
      return;
    }

    LinkedMapWithUint16Key<EntryAboutPromiseOfBinaryRequest> idOfRequestToPromise = responsiveWebSocketConnection._idOfRequestToPromise;
    EntryAboutPromiseOfBinaryRequest awaitingPromise = idOfRequestToPromise.getAndRemoveIfHas(numOfMessage);

    if (awaitingPromise != null) {
      Timeouts.clearTimeout(awaitingPromise.timeoutToReceiveResponse);
      awaitingPromise.gettingResponse.complete(message);
    }
  }

  private static void _emitUnrequestingMessageEvent(
    ResponsiveWsConnectionImpl responsiveWebSocketConnection,
    ByteBuffer message,
    int startIndexOfBodyInUnrequestingMessage
  ) {
    responsiveWebSocketConnection._eventsListener.onUnrequestingBinaryMessage(
      responsiveWebSocketConnection,
      message,
      startIndexOfBodyInUnrequestingMessage
    );
  }

  private static void _emitAwaitingResponseMessageEvent(
    ResponsiveWsConnectionImpl responsiveWebSocketConnection,
    ByteBuffer message,
    int startIndexOfBodyInBinaryRequest
  ) {
    char numOfMessage;
    try {
      numOfMessage = BinaryMessager.instance.extractIdOfMessage(message);
    } catch(Throwable error) {
      _emitMalformedMessageEvent(responsiveWebSocketConnection, message);
      return;
    }
    responsiveWebSocketConnection._eventsListener.onBinaryRequest(
      responsiveWebSocketConnection,
      message,
      startIndexOfBodyInBinaryRequest,
      new ResponseSenderImpl(responsiveWebSocketConnection, numOfMessage)
    );
  }
}
