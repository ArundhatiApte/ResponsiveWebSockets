package rws.server.api;

public interface HandshakeAction {
  public void acceptConnection();
  public <T> void acceptConnection(T userData);
  public void rejectConnection();
}
