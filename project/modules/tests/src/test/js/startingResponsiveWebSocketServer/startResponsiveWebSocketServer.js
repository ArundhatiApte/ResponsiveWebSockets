"use strict";

import uWebSockets from "uWebSockets.js";
import ResponsiveWebSocketServer from "ResponsiveWebSockets/Server";

import extractOptionsFromArgsOfShellOrExit from "./extractOptionsFromArgsOfShellOrExit.js";

ResponsiveWebSocketServer.setUWebSockets(uWebSockets);

const createSettingListenersOfMessagesFn = (function() {
  const setListeners = function(multiplerForBinaryResponse, responsiveWebSocketConnection) {
    responsiveWebSocketConnection.setBinaryRequestListener(
      sendMultipliedInt16OnBinaryRequest.bind(null, multiplerForBinaryResponse)
    );
    responsiveWebSocketConnection.setUnrequestingBinaryMessageListener(sendEchoOnUnrequestingBinaryMessage);
  };

  const sendMultipliedInt16OnBinaryRequest = function(
    multiplerForBinaryResponse,
    messageWithHeader,
    startIndex,
    senderOfResponse
  ) {
    const dataView = new DataView(messageWithHeader);
    const receivedInt = dataView.getInt16(startIndex);
    const intForResponse = receivedInt * multiplerForBinaryResponse;
    dataView.setInt16(startIndex, intForResponse);
    const response = new Uint8Array(messageWithHeader, startIndex);
    senderOfResponse.sendBinaryResponse(response);
  };

  const sendEchoOnUnrequestingBinaryMessage = function(messageWithHeader, startIndex) {
    return this.sendUnrequestingBinaryMessage(new Uint8Array(messageWithHeader, startIndex));
  };

  return function(multiplerForBinaryResponse) {
    return setListeners.bind(null, multiplerForBinaryResponse);
  };
})();

(async function() {
  const { port, multiplerForBinaryResponse } = extractOptionsFromArgsOfShellOrExit(process.argv);
  const server = new ResponsiveWebSocketServer({ server: new uWebSockets.App({}) });

  server.setConnectionListener(createSettingListenersOfMessagesFn(multiplerForBinaryResponse));
  await server.listen(port);
})();
