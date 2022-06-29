module rws.server {
  requires rws.common;
  requires transitive Java.WebSocket;

  exports rws.server.api;
  exports rws.server.impl;
}
