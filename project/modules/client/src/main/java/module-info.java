module rws.client {
  requires transitive Java.WebSocket;
  requires rws.common;

  exports rws.client.api;
  exports rws.client.impl;
}
