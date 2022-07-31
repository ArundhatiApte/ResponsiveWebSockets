# API Отзывчивых WebSockets

#### Содержание

- [module: rws.common](#module-rwscommon)
    - [package: rws.common.responsiveWebSocketConnection.api](#package-rwscommonresponsivewebsocketconnectionapi)
        - [interface: ResponsiveWsConnection](#interface-responsivewsconnection)
            * void close([int code[, String reason]])
            * `<T>` T getAttachment()
            * int getStartIndexOfBodyInBinaryResponse()
            * String getUrl()
            * `CompletableFuture<ByteBuffer>` sendBinaryRequest(ByteBuffer message[, int maxTimeMsToWaitResponse])
            * `CompletableFuture<ByteBuffer>` sendFragmentsOfBinaryRequest(ByteBuffer... fragments)
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

Модуль основы отзывчивого WebSocket соединения.

## package: rws.responsiveWebSocketConnection.api

API отзывчивого WebSocket соединения.

### interface: ResponsiveWsConnection

Базовый интерфейс для серверного соединения и клиента.

#### public void close([int code[, String reason]])

Начинает процедуру закрытия соединения.

#### public `<T>` T getAttachment()

Возвращает прикреплённые данные.

#### public int getStartIndexOfBodyInBinaryResponse()

Возвращает первый индекс байта в двоичном ответе, с которого начинается тело сообщения.

#### public String getUrl()

Возвращает URL.

#### public `CompletableFuture<ByteBuffer>` sendBinaryRequest(ByteBuffer message[, int maxTimeMsToWaitResponse])

* message - Сообщение
* maxTimeMsToWaitResponse - Максимальное время ожидания ответа

Отправляет двоичное сообщение, ожидающее ответ.
Получатель имеет возможность отправить ответ переопределив метод `onBinaryRequest`
интерфейса `ResponsiveWsConnection.EventsListener`.
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

#### public void setEventsListener(ResponsiveWsConnection.EventsListener listener)

Устанавливает обработчик событий отзывчивого WebSocket. Параметр не может быть null.

#### public void terminate()

Разрывает соединение без процедуры закрытия.

### interface: ResponsiveWsConnection.EventsListener

#### public void onBinaryRequest(ResponsiveWsConnection c, ByteBuffer messageWithHeader, int startIndex, ResponseSender rs)

* c - Соединение, получившее запрос
* messageWithHeader - Сообщение, содержащее заголовок и переданное отправителем тело
* startIndex - Индекс первого байта тела сообщения
* rs - Объект для отправки ответа

Событие, возникающее при получении двоичного сообщения, отправитель которого ожидает ответ.

### public void onClose(ResponsiveWsConnection c, int code, String reason, boolean isRemote)

* c - Закрываемое соединение

Событие, возникающее при закрытии WebSocket соединения.

#### public void onError(ResponsiveWsConnection c, Throwable error)

* c - Соединение, во время работы которого произошла ошибка
* error - Ошибка

Событие, происходящее при возникновении ошибки WebSocket соединения.

#### public void onMalformedBinaryMessage(ResponsiveWsConnection c, ByteBuffer message)

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

#### public void onTextMessage(ResponsiveWsConnection c, String message)

* c - Соединение, получившее сообщение
* message - Текстовое сообщение

Событие, возникающее при получении текстового сообщения.

#### public void onUnrequestingBinaryMessage(ResponsiveWsConnection c, ByteBuffer messageWithHeader, int startIndex)

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

### class: ResponsiveWsConnectionImpl

* реализует ResponsiveWsConnection

Класс, реализующий интерфейс отзывчивого WebSocket соединения, обёртывающий стандартный WebSocket.

#### new ResponsiveWsConnectionImpl(WebSocketConnection webSocketConnection)

* webSocketConnection - Обёртываемое WebSocket соединение

Создаёт объект, основанный на WebSocket соединении.

## package: rws.common.webSocketConnection

### interface: WebSocketConnection

Интерфейс обёртываемого WebSocket соединения.

# module: rws.client

Модуль предоставляет интерфейс клиента и его реализацию,
внутренне основанную на [Java WebSocket][Java-WebSocket].

## package: rws.client.api

### interface: ResponsiveWsClient

* наследует ResponsiveWsConnection

#### public `CompletableFuture<Void>` connect()

Подключается к WebSocket серверу асинхронно.

#### public void connectBlocking()

Подключается к WebSocket серверу.

## package: rws.client.impl

### class: ResponsiveWsClientImpl

* реализует ResponsiveWsConnection, ResponsiveWsClient
* наследует ResponsiveWsConnectionImpl

#### new ResponsiveWsClientImpl(URI uri[, Draft protocolDraft])

* uri - Адрес WebSocket сервера
* protocolDraft - Схема протокола из пакета `org.java_websocket.drafts` модуля [Java WebSocket][Java-WebSocket].

Создает объекта класса `ResponsiveWsClientImpl`.

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

### interface: ResponsiveWsServer

#### public void close() throws InterruptedException

Закрывает сервер.

#### public `CompletableFuture<Void>` start()

Запускает сервер.

#### public void setEventsListener(ResponsiveWsServer.EventsListener listener)

Устанавливает обработчик событий отзывчивого WebSocket сервера.

### interface: ResponsiveWsServer.EventsListener

#### public void onUpgrade(ClientHandshake request, HandshakeAction handshakeAction)

* ClientHandshake request - Клиентский запрос из пакета `org.java_websocket.handshake`
модуля [Java WebSocket][Java-WebSocket]

Событие, возникающее при получении запроса на создание WebSocket соединения.

#### public void onConnection(ResponsiveWsServerConnection serverConnection)

* serverConnection - Соединение с клиентом

Событие, возникающее при подключении WebSocket клиента к серверу.

### interface: ResponsiveWsServerConnection

* наследует ResponsiveWsConnection

Серверное соединение с клиентом.

#### public InetSocketAddress getRemoteSocketAddress()

Возвращает IP адрес удалённой стороны.

## package: rws.server.impl

### class: ResponsiveWsServerImpl

* реализует ResponsiveWsServer

Отзывчивый WebSocket сервер, основанный на [Java WebSocket][Java-WebSocket].

#### new ResponsiveWsServerImpl(InetSocketAddress address[, List protocolsDrafts])

* address - Локальный адрес, на котором будет запущен сервер
* protocolsDrafts - Список схем протоколов WebSocket из пакета `org.java_websocket.drafts`
модуля [Java WebSocket][Java-WebSocket]

#### public WebSocketServer asWebSocketServer()

Возвращает обёрнутый WebSocket сервер из пакета `org.java_websocket.server` модуля [Java WebSocket][Java-WebSocket].

[Java-WebSocket]: https://github.com/TooTallNate/Java-WebSocket
