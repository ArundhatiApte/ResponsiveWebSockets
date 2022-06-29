## ResponsiveWebSockets

Sending requests and unrequesting messages wrapper for WebSockets.
Module use lightweight message format.
Size of request header and response header is 3 byte, unrequesting message header is 1 byte.

### Overview

Standard web socket has method for sending binary and text data and event of incoming message.
When the server and the client interact, sometimes there is an idea of transmitting a message waiting for a response.
ResponsiveWebSockets is based on WebSockets and can send requests and unrequesting messages.

WebSocket message

```
client|           |server
      |  message  |
      |---------->|
      |           |
      |  message  |
      |<----------|
```

HTTP request/response

```
client|           |server
      |  request  |
      |---------->|
      |  response |
      |<----------|
```

request/response in ResponsiveWebSockets

```
client|             |server
      |  request A  |
      |------------>|
      |             |
      |  response A |
      |<------------|
```

ResponsiveWebSockets can send more than one request at once.

```
client|             |server
      |  request A  |
      |------------>|
      |             |
      |  request B  |
      |------------>|
      |             |
      |  response A |
      |<------------|
      |             |
      |  request C  |
      |------------>|
      |             |
      |  response C |
      |<------------|
      |             |
      |  response B |
      |<------------|
```

### Installation

To install, you will need Java version 1.9 or higher. ResponsiveWebSockets package consists of 3 jigsaw modules:

* responsive webSocket connection
* client
* server

The client and server need a common component in the form of a responsive WebSocket connection.
There are no artifacts in online repositories. There are jar files on the releases page.
To build, you need to download the source code, go to the project folder, execute 'mvn package` in the console.
Jar files of modules will appear in the project/modules/*/target/ directories:

* in project/modules/responsiveWebSocketConnection/target/ - responsive webSocket connection
* in project/modules/client/target/ - client
* in project/modules/server/target/ - server

#### Using via Maven

Install required dependencies to the local maven repository:

```bash
mvn install:install-file\
  -Dfile=<path-to-file>\
  -DgroupId=rws\
  -DartifactId=<artifact-id>\
  -Dversion=0.1.0\
  -Dpackaging=jar\
  -DgeneratePom=true
```

The following values of artifact identifiers were used during the assembly:

* responsive webSocket connection - responsive-web-socket-connection
* client - responsive-web-socket-client
* server - responsive-web-socket-server

Then add entries about dependencies to the pom project's file:

```xml
<properties>
  <!-- ... -->
  <responsive-web-sockets.version>0.1.0</responsive-web-sockets.version>
</properties>

<dependencies>
  <!-- ... -->

  <dependency>
    <groupId>rws</groupId>
    <artifactId>responsive-web-socket-client</artifactId>
    <version>${responsive-web-sockets.version}</version>
  </dependency>

  <dependency>
    <groupId>rws</groupId>
    <artifactId>responsive-web-socket-server</artifactId>
    <version>${responsive-web-sockets.version}</version>
  </dependency>

  <dependency>
    <groupId>rws</groupId>
    <artifactId>responsive-web-socket-connection</artifactId>
    <version>${responsive-web-sockets.version}</version>
  </dependency>

</dependencies>
```

Add module-info.java file to the folder with source code:

```java
module some.project {
  // ...
  requires rws.common; // responsive-web-socket-connection
  requires rws.client;
  requires rws.server;
}
```

ResponsiveWebSockets client and server wrap 
[WebSockets module](https://github.com/TooTallNate/Java-WebSocket) with the following maven dependency:

```xml
<dependency>
  <groupId>org.java-websocket</groupId>
  <artifactId>Java-WebSocket</artifactId>
  <version>1.5.3</version>
</dependency>
```

#### Using via shell

Create a folder for jigsaw modules, for example, mlib.
Put jar files of used jigsaw components to this directory.
When compiling via javac, add a option specifying the path to modules: `javac --module-path where/mlib ...`
Similarly, specify the folder by launching via java.

### Usage

#### Example

```java
class VoidEventsListener implements EventsListener {
  @Override
  public void onClose(ResponsiveWebSocketConnection c, int code, String reason) {}

  @Override
  public void onError(ResponsiveWebSocketConnection c, Throwable error) {}

  @Override
  public void onBinaryRequest(
    ResponsiveWebSocketConnection c,
    ByteBuffer messageWithHeader,
    int startIndex,
    ResponseSender rs
  ) {}

  @Override
  public void onMalformedBinaryMessage(ResponsiveWebSocketConnection c, ByteBuffer message) {}

  @Override
  public void onTextMessage(ResponsiveWebSocketConnection c, String message) {}

  @Override
  public void onUnrequestingBinaryMessage(
    ResponsiveWebSocketConnection c,
    ByteBuffer messageWithHeader,
    int startIndex
  ) {}
}

{
  final EventsListener echoOnBinaryRequestListener = new VoidEventsListener() {
    @Override
    public void onBinaryRequest(
      ResponsiveWebSocketConnection c,
      ByteBuffer messageWithHeader,
      int startIndex,
      ResponseSender rs
    ) {
      messageWithHeader.position(startIndex);
      rs.sendBinaryResponse(messageWithHeader);
    }
  };

  {
    client.setEventsListener(echoOnBinaryRequestListener);
    final byte[] message = new byte[] {1, 2, 3, 4};
    serverConnection.sendBinaryRequest(ByteBuffer.wrap(message)).whenCompleteAsync((binaryResponse, error) -> {
      // ...
    });
  }

  {
    serverConnection.setEventsListener(echoOnBinaryRequestListener);
    final byte[] message = new byte[] {5, 6, 7, 8};
    client.sendBinaryRequest(ByteBuffer.wrap(message)).whenCompleteAsync((binaryResponse, error) -> {
      // ...
    });
  }
}

{
  final EventsListener doingSomethingOnUnreqBinaryMessageListener = new VoidEventsListener() {
    // override onUnrequestingBinaryMessage
  };

  client.setEventsListener(doingSomethingOnUnreqBinaryMessageListener);

  serverConnection.sendUnrequestingBinaryMessage(ByteBuffer.wrap(
    new byte[] {10, 20, 30, 40}
  ));
}
```

#### Note about sending a large number of requests at a once

Internally, the responsive WebSocket connection uses a 16-bit number to recognize request and response messages.
When sending a large number of requests at the same time, 2 requests with the same identifier may be sent,
which will lead to the loss of one of the responses
(and recognition of the response to the first request as a response to the second,
if the response to the first comes earlier).
The described problem will appear if:

* The connection has sent N requests and is waiting for responses to them, after which more than
65536 - N requests were sent.
* The connection is not waiting for responses to requests and has sent more than 65536 requests.

### Format of ResponsiveWebSockets message headers

Responsive Web Sockets use 3 types of messages: requests, responses and unrequesting messages.
At the beginning of the message there is a header indicating the type, then there is the body.
The header of request and response also contains a 16 bit number of message.

The first byte of the binary request header is 1, the next 2 bytes contain the message number.
Example: `0b00000001_00101010_01100110` (message number - 10854).

The first byte of the binary response header is 2, the next 2 bytes contain the message number.
Example: `0b00000010_00101010_01100110` (message number - 10854).

The header of unrequesting message consists of 1 byte, which equal to 3.

Messages are sent via WebSocket binary frames.

### Links:

- [API documentation](/doc/translations/API.ru.md)
- [Examples of usage](/src/test/java/rws/examples)
- [ResponsiveWebSockets in JavaScript](https://github.com/ArundhatiApte/ResponsiveWebSockets.js)
