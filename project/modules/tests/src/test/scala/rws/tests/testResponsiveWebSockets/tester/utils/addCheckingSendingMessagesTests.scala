package rws.tests.testResponsiveWebSockets.tester;

import java.util.concurrent.CompletableFuture;

import rws.common.responsiveWebSocketConnection.api.ResponsiveWsConnection;

import rws.tests.testResponsiveWebSockets.tester.AddingTests;

import rws.tests.testResponsiveWebSockets.checks.sendingMessages.checkSendingBinaryRequests;
import rws.tests.testResponsiveWebSockets.checks.sendingMessages.checkSendingUnrequestingBinaryMessages;
import rws.tests.testResponsiveWebSockets.checks.sendingMessages.checkTimeoutForReceivingResponse;
// ok
import rws.common.responsiveWebSocketConnection.impl.checkSendingMalformedBinaryMessages;
import rws.common.responsiveWebSocketConnection.impl.checkSendingTextMessage;

import rws.tests.testResponsiveWebSockets.checks.sendingMessages.checkSendingManyBinaryRequestsAtOnce;
import rws.tests.testResponsiveWebSockets.checks.sendingMessages.
  checkSendingFragmentsOfBinaryRequestAndFragmentsOfBinaryResponse;
import rws.tests.testResponsiveWebSockets.checks.sendingMessages.checkSendingFragmentsOfUnrequestingBinaryMessage;

final object addCheckingSendingMessagesTests {
  def apply(
    addingTests: AddingTests,
    createFnToTestFromServerToClient: (
      (ResponsiveWsConnection, ResponsiveWsConnection) => CompletableFuture[Void]
    ) => () => CompletableFuture[Void],
    createFnToTestFromClientToServer: (
      (ResponsiveWsConnection, ResponsiveWsConnection) => CompletableFuture[Void]
    ) => () => CompletableFuture[Void]
  ): Unit = {
    _add2SidesTests(
      addingTests,
      createFnToTestFromServerToClient,
      createFnToTestFromClientToServer,
      _createCheckingFnToNameOfTestTable()
    );
  }

  private def _add2SidesTests(
    addingTests: AddingTests,
    createFnToTestFromServerToClient: (
      (ResponsiveWsConnection, ResponsiveWsConnection) => CompletableFuture[Void]
    ) => () => CompletableFuture[Void],
    createFnToTestFromClientToServer: (
      (ResponsiveWsConnection, ResponsiveWsConnection) => CompletableFuture[Void]
    ) => () => CompletableFuture[Void],
    checkingFnToNameOfTest: Array[DescriptorOfTest]
  ): Unit = {
    for (descriptor <- checkingFnToNameOfTest) {
      if (descriptor != null) {
        _addTestFromServerAndFromClient(
          addingTests,
          createFnToTestFromServerToClient,
          createFnToTestFromClientToServer,
          descriptor
        );
      }
    }
  }

  private def _createCheckingFnToNameOfTestTable(): Array[DescriptorOfTest] = {
    return Array(
      new DescriptorOfTest(checkSendingBinaryRequests, "sending binary requests"),
      new DescriptorOfTest(checkSendingUnrequestingBinaryMessages, "sending unrequesting binary messages"),
      new DescriptorOfTest(checkTimeoutForReceivingResponse, "timeout for receiving response"),
      new DescriptorOfTest(checkSendingMalformedBinaryMessages, "sending malformed binary messages"),
      new DescriptorOfTest(checkSendingTextMessage, "sending text message"),
      new DescriptorOfTest(checkSendingManyBinaryRequestsAtOnce, "sending many binary requests at once"),
      new DescriptorOfTest(
        checkSendingFragmentsOfUnrequestingBinaryMessage,
        "sending fragmens of binary request and fragments of binary response"
      ),
      new DescriptorOfTest(
        checkSendingFragmentsOfUnrequestingBinaryMessage,
        "sending fragments of unrequesting binary message"
      )
    );
  }

  private def _addTestFromServerAndFromClient(
    addingTests: AddingTests,
    createFnToTestFromServerToClient: (
      (ResponsiveWsConnection, ResponsiveWsConnection) => CompletableFuture[Void]
    ) => () =>  CompletableFuture[Void],
    createFnToTestFromClientToServer: (
      (ResponsiveWsConnection, ResponsiveWsConnection) => CompletableFuture[Void]
    ) => () => CompletableFuture[Void],
    descriptor: DescriptorOfTest
  ): Unit = {
    val nameOfTest = descriptor.nameOfTest;
    val checkingFn = descriptor.checkingFn;
    addingTests.addAsyncTest(nameOfTest + " from server", createFnToTestFromServerToClient(checkingFn));
    addingTests.addAsyncTest(nameOfTest + " from client", createFnToTestFromClientToServer(checkingFn));
  }

  private final class DescriptorOfTest(
    checkingFnI: (ResponsiveWsConnection, ResponsiveWsConnection) => CompletableFuture[Void],
    nameOfTestI: String
  ) {
    val checkingFn = checkingFnI;
    val nameOfTest = nameOfTestI;
  }
}
