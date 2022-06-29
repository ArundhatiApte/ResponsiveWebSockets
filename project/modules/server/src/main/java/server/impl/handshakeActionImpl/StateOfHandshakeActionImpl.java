package rws.server.impl;

final class StateOfHandshakeActionImpl {
  private StateOfHandshakeActionImpl() {}

  public static final StateOfHandshakeActionImpl pending = new StateOfHandshakeActionImpl();
  public static final StateOfHandshakeActionImpl accepted = new StateOfHandshakeActionImpl();
  public static final StateOfHandshakeActionImpl rejected = new StateOfHandshakeActionImpl();
}
