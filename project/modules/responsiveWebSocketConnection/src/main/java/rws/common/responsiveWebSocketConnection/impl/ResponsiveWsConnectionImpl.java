package rws.common.responsiveWebSocketConnection.impl;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

import rws.common.webSocketConnection.WebSocketConnection;

import rws.common.responsiveWebSocketConnection.api.ResponsiveWsConnection;
import static rws.common.responsiveWebSocketConnection.api.ResponsiveWsConnection.EventsListener;
import rws.common.responsiveWebSocketConnection.api.TimeoutToReceiveResponseException;

import rws.common.responsiveWebSocketConnection.impl.modules.SequenceGeneratorOfUint16;
import rws.common.responsiveWebSocketConnection.impl.modules.messaging.BinaryMessager;
import rws.common.responsiveWebSocketConnection.impl.modules.linkedMapWithUint16Key.LinkedMapWithUint16Key;

import rws.common.responsiveWebSocketConnection.impl.EntryAboutPromiseOfBinaryRequest;
import rws.common.responsiveWebSocketConnection.impl.ListenerOfEventsFromWebSocketConnection;

import rws.common.responsiveWebSocketConnection.impl.SendingBinaryRequestMethod;
import rws.common.responsiveWebSocketConnection.impl.SendingFragmentsOfBinaryRequestMethod;

import rws.common.responsiveWebSocketConnection.impl.SendingUnrequestingBinaryMessageMethod;
import rws.common.responsiveWebSocketConnection.impl.SendingFragmentsOfUnrequestingBinaryMessageMethod;

import rws.common.responsiveWebSocketConnection.impl.VoidEventsListener;

public class ResponsiveWsConnectionImpl implements ResponsiveWsConnection {
  public ResponsiveWsConnectionImpl(WebSocketConnection webSocketConnection) {
    this._webSocketConnection = webSocketConnection;
    // subclass set attachment to this
    // webSocketConnection.<ResponsiveWsConnectionImpl>setAttachment(this);

    this._attachment = null;
    this._maxTimeMsToWaitResponse = _defaultMaxTimeMsToWaitResponse;

    this._headerForBinaryRequestOrResponse = new byte[_binaryMessager.sizeOfHeaderForRequestOrResponse];

    this._generatorOfUint16RequestId = new SequenceGeneratorOfUint16();
    this._idOfRequestToPromise = new LinkedMapWithUint16Key<EntryAboutPromiseOfBinaryRequest>();
    this._eventsListener = VoidEventsListener.instance;

    _setupListenerOfIncomingMessages(webSocketConnection);
  }

  protected WebSocketConnection _webSocketConnection;
  protected Object _attachment;
  protected int _maxTimeMsToWaitResponse;
  protected final byte[] _headerForBinaryRequestOrResponse;
  protected SequenceGeneratorOfUint16 _generatorOfUint16RequestId;
  protected LinkedMapWithUint16Key<EntryAboutPromiseOfBinaryRequest> _idOfRequestToPromise;
  protected EventsListener _eventsListener;

  private static final int _defaultMaxTimeMsToWaitResponse = 2000;
  protected static final BinaryMessager _binaryMessager = BinaryMessager.instance;
  protected static final byte[] _headerOfUnrequestingMessage;

  static {
    _headerOfUnrequestingMessage = new byte[_binaryMessager.sizeOfHeaderForUnrequestingMessage];
    _binaryMessager.fillHeaderAsUnrequestingMessage(_headerOfUnrequestingMessage);
  }

  @Override
  public final void close() {
    this._webSocketConnection.close();
  }

  @Override
  public final void close(int code) {
    this._webSocketConnection.close(code);
  }

  @Override
  public final void close(int code, String reason) {
    this._webSocketConnection.close(code, reason);
  }

  @Override
  public final void terminate() {
    this._webSocketConnection.terminate();
  }

  @Override
  public final <T> T getAttachment() {
    return (T) this._attachment;
  }

  @Override
  public final <T> void setAttachment(T attachment) {
    this._attachment = attachment;
  }

  @Override
  public final int getStartIndexOfBodyInBinaryResponse() {
    return _binaryMessager.sizeOfHeaderForRequestOrResponse;
  }

  @Override
  public final String getUrl() {
    return this._webSocketConnection.getUrl();
  }

  @Override
  public final CompletableFuture<ByteBuffer> sendBinaryRequest(ByteBuffer message) {
    return SendingBinaryRequestMethod.instance.apply(this, message, this._maxTimeMsToWaitResponse);
  }

  @Override
  public final CompletableFuture<ByteBuffer> sendBinaryRequest(ByteBuffer message, int maxTimeMsToWaitResponse) {
    return SendingBinaryRequestMethod.instance.apply(this, message, maxTimeMsToWaitResponse);
  }

  @Override
  public final CompletableFuture<ByteBuffer> sendFragmentsOfBinaryRequest(ByteBuffer... fragments) {
    return SendingFragmentsOfBinaryRequestMethod.instance.apply(this, fragments, this._maxTimeMsToWaitResponse);
  }

  @Override
  public final void sendUnrequestingBinaryMessage(ByteBuffer message) {
    SendingUnrequestingBinaryMessageMethod.instance.apply(this, message);
  }

  @Override
  public final void sendFragmentsOfUnrequestingBinaryMessage(ByteBuffer... fragments) {
    SendingFragmentsOfUnrequestingBinaryMessageMethod.instance.apply(this, fragments);
  }

  @Override
  public final void setMaxTimeMsToWaitResponse(int ms) {
    this._maxTimeMsToWaitResponse = ms;
  }

  @Override
  public final void setEventsListener(EventsListener listener) {
    if (listener == null) {
      throw new NullPointerException("listener is null.");
    }
    this._eventsListener = listener;
  }

  private static final void _setupListenerOfIncomingMessages(WebSocketConnection webSocketConnection) {
    webSocketConnection.setEventsListener(ListenerOfEventsFromWebSocketConnection.instance);
  }
}
