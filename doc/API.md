# ResponsiveWebSockets API

#### Table of Contents


- [module: rws.common](#module-rwscommon)
    - [package: rws.common.responsiveWebSocketConnection.api](#package-rwscommonresponsivewebsocketconnectionapi)
        - [interface: ResponsiveWsConnection](#interface-responsivewsconnection)
            * void close([int code[, String reason]])
            * `<T>` T getAttachment()
            * int getStartIndexOfBodyInBinaryResponse()
            * String getUrl()
            * `CompletableFuture<ByteBuffer>` sendBinaryRequest(ByteBuffer message[, int maxTimeMsToWaitResponse])
            * `CompletableFuture<ByteBuffer>` sendFragmentsOfBinaryRequest(
              [int maxTimeMsToWaitResponse, ]
              ByteBuffer... fragments
            )
            * void sendFragmentsOfUnrequestingBinaryMessage(ByteBuffer... fragments)
            * void sendUnrequestingBinaryMessage(ByteBuffer message)
            * `<T>` void setAttachment(T attachment)
            * void setMaxTimeMsToWaitResponse(int timeMs)
            * void setEventsListener(ResponsiveWsConnection.EventsListener listener)
            * void terminate()
        - [interface: ResponsiveWsConnection.EventsListener](#interface-responsivewsconnection.eventslistener)
            * void onBinaryRequest(
              ResponsiveWsConnection c,
              ByteBuffer messageWithHeader,
              int startIndex,
              ResponseSender rs
            )
            * void onClose(ResponsiveWsConnection c, int code, String reason, boolean isRemote)
            * void onError(ResponsiveWsConnection c, Throwable error)
            * void onMalformedBinaryMessage(ResponsiveWsConnection c, ByteBuffer message)
            * void onTextMessage(ResponsiveWsConnection c, String message)
            * void onUnrequestingBinaryMessage(ResponsiveWsConnection c, ByteBuffer messageWithHeader, int startIndex)
        - [interface: ResponseSender](#interface-responsesender)
            * void sendBinaryResponse(ByteBuffer message)
            * void sendFragmentsOfBinaryResponse(ByteBuffer... fragments)
        - [class: TimeoutToReceiveResponseException](#[class-timeouttoreceiveresponseexception)
    - [package: rws.common.responsiveWebSocketConnection.impl](package-rwscommonresponsivewebsocketconnectionimpl)
        - [class: ResponsiveWsConnectionImpl](class-responsivewsconnectionimpl)
             * new ResponsiveWsConnectionImpl(WebSocketConnection webSocketConnection)
    - [package: rws.common.webSocketConnection](package-rwscommonwebsocketconnection)
        - [interface: WebSocketConnection](interface-websocketconnection)
            * String getUrl()
            * void close()
            * void close(int code)
            * void close(int code, String reason)
            * void terminate()
            * <T> T getAttachment()
            * <T> void setAttachment(T attachment)
            * void sendBinaryFragment(ByteBuffer fragment, boolean isLast)
            * void sendTextMessage(String message)
            * void setEventsListener(WebSocketConnection.EventsListener eventsListener)
        - [interface: WebSocketConnection.EventsListener](interface-websocketconnectioneventslistener)
            * void onClose(WebSocketConnection webSocketConnection, int code, String reason, boolean isRemote)
            * void onError(WebSocketConnection webSocketConnection, Throwable error)
            * void onBinaryMessage(WebSocketConnection webSocketConnection, ByteBuffer message)
            * void onTextMessage(WebSocketConnection webSocketConnection, String message)
- [module: rws.client](module-rwsclient)
    - [package: rws.client.api](#package-rwsclientapi)
        - [interface: ResponsiveWsClient](#interface-responsivewsclient)
            * `CompletableFuture<Void>` connect()
            * void connectBlocking()
    - [package: rws.client.impl](#package-rwsclientimpl)
        - [class: ResponsiveWsClientImpl](#class-responsivewsclientimpl)
            * new ResponsiveWsClientImpl(URI uri[, Draft protocolDraft])
            * WebSocketClient asWebSocketClient()
- [module: rws.server](module-rwsserver)
    - [package: rws.server.api](#package-rwsserverapi)
        - [interface: HandshakeAction](#[interface-handshakeaction)
            * void acceptConnection()
            * `<T>` void acceptConnection(T userData)
            * void rejectConnection()
        - [interface: ResponsiveWsServer](#interface-responsivewsserver)
            * void close() throws InterruptedException
            * `CompletableFuture<Void>` start()
            * void setEventsListener(ResponsiveWsServer.EventsListener listener)
        - [interface: ResponsiveWsServer.EventsListener](#interface-responsivewsservereventslistener)
            * void onUpgrade(ClientHandshake request, HandshakeAction handshakeAction)
            * void onConnection(ResponsiveWsServerConnection serverConnection)
        - [interface: ResponsiveWsServerConnection](#interface-responsivewsserverconnection)
            * InetSocketAddress getRemoteSocketAddress()
    - [package: rws.server.impl](#package-rwsserverimpl)
        - [class: ResponsiveWsServerImpl](#class-responsivewsserverimpl)
            * new ResponsiveWsServerImpl(InetSocketAddress address[, List<Draft> protocolsDrafts])
            * WebSocketServer asWebSocketServer()

# module rws.common

Base module for ResponsiveWsConnection.

## package: rws.connection.api

ResponsiveWsConnection API.

### interface: ResponsiveWsConnection

Base interface for server connection and client.

#### public void close([int code[, String reason]])

Initiates a closing handshake.

#### public `<T>` T getAttachment()

Returns user's data.

#### public int getStartIndexOfBodyInBinaryResponse()

Returns index of the first byte in binary response, from which the message body begins.

#### public String getUrl()

Returns URL.

#### public `CompletableFuture<ByteBuffer>` sendBinaryRequest(ByteBuffer message[, int maxTimeMsToWaitResponse])

* message - Binary message
* maxTimeMsToWaitResponse - Count of milliseconds for waiting the response

Sends awaiting response binary message.
The recepient has the ability to send a response by overriding `onBinaryRequest` method of
`ResponsiveWsConnection.EventsListener` interface.
By default `maxTimeMsToWaitResponse` is value setted by `setMaxTimeMsToWaitResponse` method.
If response will not arrive within `maxTimeMsToWaitResponse` time milliseconds,
the `CompletableFuture` will be rejected with `TimeoutToReceiveResponseException`.

#### public `CompletableFuture<ByteBuffer>` sendFragmentsOfBinaryRequest([int maxTimeMsToWaitResponse, ]ByteBuffer... fragments)

* maxTimeMsToWaitResponse - Count of milliseconds for waiting the response
* fragments - Binary fragments of the message

Sends binary request, similar as `sendBinaryRequest`.
The method sends data in fragments, without connecting the parts into one body,
avoiding allocating memory for the entire response.

#### public void sendFragmentsOfUnrequestingBinaryMessage(ByteBuffer... fragments)

* fragments - Binary fragments of the message

Sends binary unrequesting message, similar as `sendUnrequestingBinaryMessage`.
The method sends data in fragments, without connecting parts into one body,
avoiding memory allocation for the entire message.

#### public void sendUnrequestingBinaryMessage(ByteBuffer message)

* message - Binary message

Sends binary message without waiting response.
Recepient can handle data by overriding `onUnrequestingBinaryMessage` method of
`ResponsiveWsConnection.EventsListener` interface.

#### public `<T>` void setAttachment(T attachment)

Sets user's data.

#### public void setMaxTimeMsToWaitResponse(int timeMs)

* timeMs - Count of milliseconds

Sets default maximum time in milliseconds for waiting response on request.
This time can be redefined in second parametr in `sendBinaryRequest` method.
By default 2000.

#### public void setEventsListener(ResponsiveWsConnection.EventsListener listener)

Sets events listener for this connection. Handler can't be null.

#### public void terminate()

Forcibly close the connection.

### interface: ResponsiveWsConnection.EventsListener

#### public void onBinaryRequest(ResponsiveWsConnection c, ByteBuffer messageWithHeader, int startIndex, ResponseSender rs)

* c - The connection that received the request
* messageWithHeader - Message containing the header and the body transmitted by the sender
* startIndex - Index of the first byte of the message body
* rs - Object for sending response

Listener of event, that occurs when a binary message is received, the sender of which is waiting for a response.

#### public void onClose(ResponsiveWsConnection c, int code, String reason)

* c - The closed connection

Listener of event, that occurs when inner WebSocket connection is closed.

#### public void onError(ResponsiveWsConnection c, Throwable error)

* c - The connection, during which work the error had occured

Listener of error from inner WebSocket connection

#### public void onMalformedBinaryMessage(ResponsiveWsConnection c, ByteBuffer message)

* c - The connection that received the message
* message - Binary message

Listener of event, that occurs when a malformed binary message (message without valid header) is received.

Notes:

* if the first byte of the message is 1 (as an unsigned integer) and the message is longer than two bytes,
then the message is treated as a request
* if the first byte of the message is 2 (as an unsigned integer) and the message is longer than two bytes,
then the message is treated as a response
* if the first byte of the message is 3 (as an unsigned integer),
then the message is treated as a unrequesting message

#### public void onTextMessage(ResponsiveWsConnection c, String message)

* c - The connection that received the message
* message - Text message

Listener of event, that occurs when a text message is received.

#### public void onUnrequestingBinaryMessage(ResponsiveWsConnection c, ByteBuffer messageWithHeader, int startIndex)

* c - The connection that received the message
* messageWithHeader - Message containing the header and the body transmitted from the sender
* startIndex - Index of the first byte of the message body

Listener of event, that occurs when a binary message is received, the sender of which is not waiting for a response.

### interface: ResponseSender

Object that sends a response to a request. The method for sending the response is called only 1 time.

#### public void sendBinaryResponse(ByteBuffer message)

* message - Binary response

Sends binary response.

#### public void sendFragmentsOfBinaryResponse(ByteBuffer... fragments)

* fragments - Parts of binary response

Sends binary response, similas as `sendBinaryResponse`.
The method sends data in fragments, without connecting parts into one body,
avoiding memory allocation for the entire message.

### class: TimeoutToReceiveResponseException

* extends RuntimeException

Exception, that rejects `CompletableFuture` when the response to the request
did not arrive during the max time for waiting.

## package: rws.common.responsiveWebSocketConnection.impl

### class: ResponsiveWsConnectionImpl

* implements ResponsiveWsConnection

Class that wraps a standard WebSocket and implements ResponsiveWebSocketConnection interface.

#### new ResponsiveWsConnectionImpl(WebSocketConnection webSocketConnection)

* webSocketConnection - Wrapped WebSocket connection

Creates an implementation of `ResponsiveWsConnection` by internally setting an event handler
`WebSocketConnection.EventsListener` to the wrapped `webSocketConnection`. After the call, you need to set a link
to a new object for the WebSocket connection:

```java
ResponsiveWsConnectionImpl rwsc = new ResponsiveWsConnectionImpl(webSocketConnection);
webSocketConnection.<ResponsiveWsConnectionImpl>setAttachment(rwsc);
```

## package: rws.common.webSocketConnection

### interface: WebSocketConnection

Interface of wrapped WebSocekt connection.

# module: rws.client

The module provides the client interface and it's implementation,
internally based on [Java WebSocket][Java-WebSocket].

## package: rws.client.api

### interface: ResponsiveWsClient

* extends ResponsiveWsConnection

#### public `CompletableFuture<Void>` connect()

Connects to the WebSocket server asynchronously.

#### public void connectBlocking()

Connects to the WebSocket server.

## package: rws.client.impl

### class: ResponsiveWsClientImpl

* implements ResponsiveWsConnection, ResponsiveWsClient
* extends ResponsiveWsConnectionImpl

#### new ResponsiveWsClientImpl(URI uri[, Draft protocolDraft])

* uri - WebSocket server adress
* protocolDraft - WebSocket protocol draft from package `org.java_websocket.drafts`
of the [Java WebSocket][Java-WebSocket] module.

Creates instance of `ResponsiveWsClientImpl`.

#### WebSocketClient asWebSocketClient()

Returns wrapped WebSocket client from package `org.java_websocket.client`
of the [Java WebSocket][Java-WebSocket] module.

# module: rws.server

The module provides ResponsiveWebSocketServer API and it's implementation based
on the [Java WebSocket][Java-WebSocket].

## package: rws.server.api

### interface: HandshakeAction

Interface that accepts or rejects requests to create a WebSocket connection.

#### public void acceptConnection()

Accepts a request to create a WebSocket connection.

#### public `<T>` void acceptConnection(T userData)

* userData - Data attached to the server connection object

Accepts a request to create a WebSocket connection.

#### public void rejectConnection()

Rejects the request to create a WebSocket connection.

### interface: ResponsiveWsServer

#### public void close() throws InterruptedException

Closes server.

#### public `CompletableFuture<Void>` start()

Starts address listening.

#### public void setEventsListener(ResponsiveWsServer.EventsListener listener)

Sets listener of request on updgrade and connection events.

### interface: ResponsiveWsServer.EventsListener

#### public void onUpgrade(ClientHandshake request, HandshakeAction handshakeAction)

* ClientHandshake request - client request from package org.java_websocket.handshake
of the [Java WebSocket][Java-WebSocket] module

Listener of event that occurs when server receive request to create a WebSocket connection.

#### public void onConnection(ResponsiveWsServerConnection serverConnection)

* serverConnection - Connection to client

Listener of event that occurs when the WebSocket client connects to the server.

### interface: ResponsiveWsServerConnection

* extends ResponsiveWsConnection

Server connection to the client.

#### public InetSocketAddress getRemoteSocketAddress()

Returns the remote socket IP address.

## package: rws.server.impl

### class: ResponsiveWsServerImpl

* implements ResponsiveWsServer

ResponsiveWebSocketServer based on the [Java WebSocket][Java-WebSocket].

#### new ResponsiveWsServerImpl(InetSocketAddress address[, List protocolsDrafts])

* address - The local address on which server will be started
* protocolsDrafts - List of WebSocket protocols drafts from package `org.java_websocket.drafts`
of the [Java WebSocket][Java-WebSocket] module.

#### public WebSocketServer asWebSocketServer()

Returns wrapped WebSocket server from package `org.java_websocket.server`
of the [Java WebSocket][Java-WebSocket] module.

[Java-WebSocket]: https://github.com/TooTallNate/Java-WebSocket
