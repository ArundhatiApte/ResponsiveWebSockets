package rws.common.responsiveWebSocketConnection.api;

public class TimeoutToReceiveResponseException extends RuntimeException {
  public TimeoutToReceiveResponseException(String message) {
    super(message);
  }
}
