# API Отзывчивых WebSockets

#### Содержание

- [module: rws.common](#module-rwscommon)
    - [package: rws.common.responsiveWebSocketConnection.api](#package-rwscommonresponsivewebsocketconnectionapi)
        - [interface: ResponsiveWebSocketConnection](#interface-responsivewebsocketconnection)
            * void close([int code[, String reason]])
            * `<T>` T getAttachment()
            * int getStartIndexOfBodyInBinaryResponse()
            * String getURL()
            * `CompletableFuture<ByteBuffer>` sendBinaryRequest(ByteBuffer message[, int maxTimeMsToWaitResponse])
            * `CompletableFuture<ByteBuffer>` sendFragmentsOfBinaryRequest(ByteBuffer... fragments)
            * void sendFragmentsOfUnrequestingBinaryMessage(ByteBuffer... fragments)
            * void sendUnrequestingBinaryMessage(ByteBuffer message)
            * `<T>` void setAttachment(T attachment)
            * void setMaxTimeMsToWaitResponse(int timeMs)
            * void setEventsListener(ResponsiveWebSocketConnection.EventsListener listener)
            * void terminate()
        - [interface: ResponsiveWebSocketConnection.EventsListener](#interface-responsivewebsocketconnection.eventslistener)
            * void onBinaryRequest(
              ResponsiveWebSocketConnection c,
              ByteBuffer messageWithHeader,
              int startIndex,
              ResponseSender rs
            )
            * void onClose(ResponsiveWebSocketConnection c, int code, String reason)
            * void onError(ResponsiveWebSocketConnection c, Throwable error)
            * void onMalformedBinaryMessage(ResponsiveWebSocketConnection c, ByteBuffer message)
            * void onTextMessage(ResponsiveWebSocketConnection c, String message)
            * void onUnrequestingBinaryMessage(ResponsiveWebSocketConnection c, ByteBuffer messageWithHeader, int startIndex)
        - [interface: ResponseSender](#interface-responsesender)
            * void sendBinaryResponse(ByteBuffer message)
            * void sendFragmentsOfBinaryResponse(ByteBuffer... fragments)
        - [class: TimeoutToReceiveResponseException](#[class-timeouttoreceiveresponseexception)
    - [package: rws.common.responsiveWebSocketConnection.impl](package-rwscommonresponsivewebsocketconnectionimpl)
        - [class: ResponsiveWebSocketConnectionImpl](class-responsivewebsocketconnectionimpl)
             * new ResponsiveWebSocketConnectionImpl(WebSocketConnection webSocketConnection)
    - [package: rws.common.webSocketConnection](package-rwscommonwebsocketconnection)
        - [interface: WebSocketConnection](interface-websocketconnection)
            * String getURL()
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
            * void onClose(WebSocketConnection webSocketConnection, int code, String reason)
            * void onError(WebSocketConnection webSocketConnection, Throwable error)
            * void onBinaryMessage(WebSocketConnection webSocketConnection, ByteBuffer message)
            * void onTextMessage(WebSocketConnection webSocketConnection, String message)
- [module: rws.client](module-rwsclient)
    - [package: rws.client.api](#package-rwsclientapi)
        - [interface: ResponsiveWebSocketClient](#interface-responsivewebsocketclient)
            * `CompletableFuture<Void>` connect()
            * void connectBlocking()
    - [package: rws.client.impl](#package-rwsclientimpl)
        - [class: ResponsiveWebSocketClientImpl](#class-responsivewebsocketclientimpl)
            * new ResponsiveWebSocketClientImpl(URI uri[, Draft protocolDraft])
            * WebSocketClient asWebSocketClient()
- [module: rws.server](module-rwsserver)
    - [package: rws.server.api](#package-rwsserverapi)
        - [interface: HandshakeAction](#[interface-handshakeaction)
            * void acceptConnection()
            * `<T>` void acceptConnection(T userData)
            * void rejectConnection()
        - [interface: ResponsiveWebSocketServer](#interface-responsivewebsocketserver)
            * void close() throws InterruptedException
            * `CompletableFuture<Void>` start()
            * void setEventsListener(ResponsiveWebSocketServer.EventsListener listener)
        - [interface: ResponsiveWebSocketServer.EventsListener](#interface-responsivewebsocketserver.eventslistener)
            * void onUpgrade(ClientHandshake request, HandshakeAction handshakeAction)
            * void onConnection(ResponsiveWebSocketServerConnection serverConnection)
        - [interface: ResponsiveWebSocketServerConnection](#interface-responsivewebsocketserverconnection)
            * InetSocketAddress getRemoteSocketAddress()
    - [package: rws.server.impl](#package-rwsserverimpl)
        - [class: ResponsiveWebSocketServerImpl](#class-responsivewebsocketserverimpl)
            * new ResponsiveWsServerImpl(InetSocketAddress address[, List<Draft> protocolsDrafts])
            * WebSocketServer asWebSocketServer()

# module rws.common 

Модуль основы отзывчивого WebSocket соединения.

## package: rws.responsiveWebSocketConnection.api

API отзывчивого WebSocket соединения.

### interface: ResponsiveWebSocketConnection

Базовый интерфейс для серверного соединения и клиента.

#### public void close([int code[, String reason]])

Начинает процедуру закрытия соединения.

#### public `<T>` T getAttachment()

Возвращает прикреплённые данные.

#### public int getStartIndexOfBodyInBinaryResponse()

Возвращает первый индекс байта в двоичном ответе, с которого начинается тело сообщения.

#### public String getURL()

Возвращает URL.

#### public `CompletableFuture<ByteBuffer>` sendBinaryRequest(ByteBuffer message[, int maxTimeMsToWaitResponse])

* message - Сообщение
* maxTimeMsToWaitResponse - Максимальное время ожидания ответа

Отправляет двоичное сообщение, ожидающее ответ.
Получатель имеет возможность отправить ответ переопределив метод `onBinaryRequest`
интерфейса ``ResponsiveWebSocketConnection.EventsListener`.
По умолчанию максимальное время ожидания ответа `maxTimeMsToWaitResponse` равно значению,
установленному методом `setMaxTimeMsToWaitResponse`.
Если ответ не придет в течение `maxTimeMsToWaitResponse` миллисекунд,
`CompletableFuture` завершится исключением `TimeoutToReceiveResponseException`.

#### public `CompletableFuture<ByteBuffer>` sendFragmentsOfBinaryRequest(ByteBuffer... fragments)

* fragments - Части сообщения

Отправляет двоичный запрос, также как `sendBinaryRequest`.
Метод посылает данные фрагментами, без соединения частей в одно тело, избегая выделения памяти для всего запроса.

#### public void sendFragmentsOfUnrequestingBinaryMessage(ByteBuffer... fragments)

* fragments - Части сообщения

Отправляет двоичное сообщение, без ожидания ответа, также как `sendUnrequestingBinaryMessage`.
Метод посылает данные фрагментами, без соединения частей в одно тело, избегая выделения памяти для всего сообщения.

#### public void sendUnrequestingBinaryMessage(ByteBuffer message)

* message - Сообщение

Отправляет двоичное сообщение без ожидания ответа.
Получатель имеет возможность увидеть данные,
переопределив метод `onUnrequestingBinaryMessage` интерфейса `ResponsiveWebSocketConnection.EventsListener`.

#### public `<T>` void setAttachment(T attachment)

Прикрепляет данные.

#### public void setMaxTimeMsToWaitResponse(int timeMs)

* timeMs - Кол-во миллисекунд

Задает максимальное время в миллисекундах ожидания ответа по умолчанию для отправленных сообщений
с помощью метода `sendBinaryRequest`.
Можно переопределить во 2-ом параметре метода для отправки ожидающего ответа сообщения.
По умолчанию 2000.

#### public void setEventsListener(ResponsiveWebSocketConnection.EventsListener listener)

Устанавливает обработчик событий отзывчивого WebSocket. Параметр не может быть null.

#### public void terminate()

Разрывает соединение без процедуры закрытия.

### interface: ResponsiveWebSocketConnection.EventsListener

#### public void onBinaryRequest(ResponsiveWebSocketConnection c, ByteBuffer messageWithHeader, int startIndex, ResponseSender rs)

* c - Соединение, получившее запрос
* messageWithHeader - Сообщение, содержащее заголовок и переданное отправителем тело
* startIndex - Индекс первого байта тела сообщения
* rs - Объект для отправки ответа

Событие, возникающее при получении двоичного сообщения, отправитель которого ожидает ответ.

### public void onClose(ResponsiveWebSocketConnection c, int code, String reason)

* c - Закрываемое соединение

Событие, возникающее при закрытии WebSocket соединения.

#### public void onError(ResponsiveWebSocketConnection c, Throwable error)

* c - Соединение, во время работы которого произошла ошибка
* error - Ошибка

Событие, происходящее при возникновении ошибки WebSocket соединения.

#### public void onMalformedBinaryMessage(ResponsiveWebSocketConnection c, ByteBuffer message)

* c - Соединение, получившее сообщение
* message - Двоичное сообщение

Событие, возникающее при получении двоичного сообщения без верного заголовка.

Заметки:

* если первый байт сообщения равен 1 (как беззнаковое целое) и сообщение длиннее двух байт,
то сообщение расценивается как запрос
* если первый байт сообщения равен 2 (как беззнаковое целое) и сообщение длиннее двух байт,
то сообщение расценивается как ответ
* если первый байт сообщения равен 3 (как беззнаковое целое),
то сообщение расценивается как сообщение без ожидания ответа

#### public void onTextMessage(ResponsiveWebSocketConnection c, String message)

* c - Соединение, получившее сообщение
* message - Текстовое сообщение

Событие, возникающее при получении текстового сообщения.

#### public void onUnrequestingBinaryMessage(ResponsiveWebSocketConnection c, ByteBuffer messageWithHeader, int startIndex)

* c - Соединение, получившее сообщение
* messageWithHeader - Сообщение, содержащее заголовок и переданное отправителем тело
* startIndex - Индекс первого байта тела сообщения

Событие, возникающее при получении двоичного сообщения без ожидания ответа отправителем.

### interface: ResponseSender

Объект, отправляющий ответ на запрос. Метод для отправки ответа вызывается только 1 раз.

#### public void sendBinaryResponse(ByteBuffer message)

* message - Ответ

Отправляет двоичный ответ.

#### public void sendFragmentsOfBinaryResponse(ByteBuffer... fragments)

* fragments - Части ответа

Отправляет двоичный ответ, также как `sendBinaryResponse`.
Метод посылает данные фрагментами, без соединения частей в одно тело, избегая выделения памяти для всего ответа.

### class: TimeoutToReceiveResponseException

* наследует RuntimeException

Исключение, возникающее при вызове метода `sendBinaryRequest`, завершающее аварийно `CompletableFuture`,
когда ответ на запрос не пришёл за отведённое максимальное время ожидания.

## package: rws.common.responsiveWebSocketConnection.impl

### class: ResponsiveWebSocketConnectionImpl

* реализует ResponsiveWebSocketConnection

Класс, реализующий интерфейс отзывчивого WebSocket соединения, обёртывающий стандартный WebSocket.

#### new ResponsiveWebSocketConnectionImpl(WebSocketConnection webSocketConnection)

* webSocketConnection - Обёртываемое WebSocket соединение

Создаёт объект, основанный на WebSocket соединении.

## package: rws.common.webSocketConnection

### interface: WebSocketConnection

Интерфейс обёртываемого WebSocket соединения.

# module: rws.client

Модуль предоставляет интерфейс клиента и его реализацию,
внутренне основанную на [Java WebSocket][Java-WebSocket].

## package: rws.client.api

### interface: ResponsiveWebSocketClient

* наследует ResponsiveWebSocketConnection

#### public `CompletableFuture<Void>` connect()

Подключается к WebSocket серверу асинхронно.

#### public void connectBlocking()

Подключается к WebSocket серверу.

## package: rws.client.impl

### class: ResponsiveWebSocketClientImpl

* реализует ResponsiveWebSocketConnection, ResponsiveWebSocketClient
* наследует ResponsiveWebSocketConnectionImpl

#### new ResponsiveWebSocketClientImpl(URI uri[, Draft protocolDraft])

* uri - Адрес WebSocket сервера
* protocolDraft - Схема протокола из пакета `org.java_websocket.drafts` модуля [Java WebSocket][Java-WebSocket].

Создает объекта класса `ResponsiveWebSocketClientImpl`.

#### WebSocketClient asWebSocketClient()

Возвращает обёрнутого WebSocket клиента из модуля [Java WebSocket][Java-WebSocket].

# module: rws.server

Модуль предоставляет интерфейс отзывчивого WebSocket сервера и соединения с клиентом, реализацию.

## package: rws.server.api

### interface: HandshakeAction

Объект, принимающий или отклоняющий запрос на создание WebSocket соединения.

#### public void acceptConnection()

Принимает запрос на создание WebSocket соединения.

#### public `<T>` void acceptConnection(T userData)

* userData - Данные, прикрепляемые к объекту серверного соединения с клиентом

Принимает запрос на создание WebSocket соединения.

#### public void rejectConnection()

Отклоняет запрос на создание WebSocket соединения.

### interface: ResponsiveWebSocketServer

#### public void close() throws InterruptedException

Закрывает сервер.

#### public `CompletableFuture<Void>` start()

Запускает сервер.

#### public void setEventsListener(ResponsiveWebSocketServer.EventsListener listener)

Устанавливает обработчик событий отзывчивого WebSocket сервера.

### interface: ResponsiveWebSocketServer.EventsListener

#### public void onUpgrade(ClientHandshake request, HandshakeAction handshakeAction)

* ClientHandshake request - Клиентский запрос из пакета `org.java_websocket.handshake`
модуля [Java WebSocket][Java-WebSocket]

Событие, возникающее при получении запроса на создание WebSocket соединения.

#### public void onConnection(ResponsiveWebSocketServerConnection serverConnection)

* serverConnection -  Соединение с клиентом

Событие, возникающее при подключении WebSocket клиента к серверу.

### interface: ResponsiveWebSocketServerConnection

* наследует ResponsiveWebSocketConnection

Серверное соединение с клиентом.

#### public InetSocketAddress getRemoteSocketAddress()

Возвращает IP адрес удалённой стороны.

## package: rws.server.impl

### class: ResponsiveWebSocketServerImpl

* реализует ResponsiveWebSocketServer

Отзывчивый WebSocket сервер, основанный на [Java WebSocket][Java-WebSocket].

#### new ResponsiveWebSocketServerImpl(InetSocketAddress address[, List protocolsDrafts])

* address - Локальный адрес, на котором будет запущен сервер
* protocolsDrafts - Список схем протоколов WebSocket из пакета `org.java_websocket.drafts`
модуля [Java WebSocket][Java-WebSocket]

#### public WebSocketServer asWebSocketServer()

Возвращает обёрнутый WebSocket сервер из пакета `org.java_websocket.server` модуля [Java WebSocket][Java-WebSocket].

[Java-WebSocket]: https://github.com/TooTallNate/Java-WebSocket
