## ResposiveWebSockets

Отправляющая запросы и незапрашивающие сообщения обертка для WebSockets.
Модуль использует легковесный формат сообщений.
Для запросов и ответов заголовок размером 3 байта, для незапрашивающих сообщений - 1 байт.

### Краткий обзор

Стандартный WebSocket имеет метод для отправки двоичных и текстовых данных и событие входящего сообщения.
При взаимодействии сервера и клиента иногда возникает мысль о передаче ожидающего ответа сообщения.
ResposiveWebSockets основан на WebSockets и может отправлять запросы и незапрашивающие сообщения.

сообщение WebSocket

```
client|           |server
      |  message  |
      |---------->|
      |           |
      |  message  |
      |<----------|
```

запрос/ответ HTTP

```
client|           |server
      |  request  |
      |---------->|
      |  response |
      |<----------|
```

запрос/ответ ResponsiveWebSockets

```
client|             |server
      |  request A  |
      |------------>|
      |             |
      |  response A |
      |<------------|
```

ResponsiveWebSockets также могут отправлять более 1-го запроса за раз.

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

### Установка

Для установки потребуется Java версии 1.9 или выше. Пакет ResponsiveWebSockets состоит из 3-ёх jigsaw модулей:

* отзывчивое webSocket соединение
* клиент
* сервер

Клиенту и серверу требуется общий компонент в виде отзывчивого webSocket соединения.
Можно добавить только клиента, только сервер или все модули.
Артефактов нет в интернет репозиториях, jar файлы находятся на странице выпусков.
Для сборки необходимо скачать исходный код, перейти в папку project/, выполнить в консоли `mvn package`.
В каталогах project/modules/*/target/ появятся jar файлы модулей:

* в project/modules/responsiveWebSocketConnection/target/ - отзывчивого webSocket соединения
* в project/modules/client/target/ - клиента
* в project/modules/server/target/ - сервера

#### Использование через Maven

Установить в локальный репозиторий требуемые зависимости:

```bash
mvn install:install-file\
  -Dfile=<path-to-file>\
  -DgroupId=rws\
  -DartifactId=<artifact-id>\
  -Dversion=0.1.0\
  -Dpackaging=jar\
  -DgeneratePom=true
```

Можете указать собственное название в поле artifactId.
При сборке были использованы следующие значения идентификаторов артефактов:

* отзывчивое webSocket соединение - responsive-web-socket-connection
* клиент - responsive-web-socket-client
* сервер - responsive-web-socket-server

После добавить записи о зависимостях в pom файл проекта:

```xml
<properties>
  <!-- ... -->
  <responsive-web-sockets.version>0.1.0</responsive-web-sockets.version>
</properties>

<dependencies>
  <!-- ... -->

  <dependency>
    <groupId>rws</groupId>
    <artifactId>responsive-web-socket-connection</artifactId>
    <version>${responsive-web-sockets.version}</version>
  </dependency>

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

Затем в папку исходного кода добавить файл module-info.java:

```java
module some.project {
  // другие зависимости
  requires rws.common; // отзывчивое webSocket соединение
  requires rws.client;
  requires rws.server;
}
```

Клиент и сервер ResponsiveWebSockets обёртывают
[WebSockets модуль](https://github.com/TooTallNate/Java-WebSocket) со следующей зависимостью maven:

```xml
<dependency>
  <groupId>org.java-websocket</groupId>
  <artifactId>Java-WebSocket</artifactId>
  <version>1.5.3</version>
</dependency>
```

#### Использование через консоль

Создать папку для jigsaw модулей, например mlib.
В каталог поместить используемый набор jar файлов jigsaw компонентов.
При компиляции через javac добавить параметр, указывающий путь к модулям: `javac --module-path where/mlib` ...
Аналогично указать папку, запуская через java.

### Использование

#### Пример

```java
class VoidEventsListener implements EventsListener {
  @Override
  public void onClose(ResponsiveWsConnection c, int code, String reason, boolean isRemote) {}

  @Override
  public void onError(ResponsiveWsConnection c, Throwable error) {}

  @Override
  public void onBinaryRequest(
    ResponsiveWsConnection c,
    ByteBuffer messageWithHeader,
    int startIndex,
    ResponseSender rs
  ) {}

  @Override
  public void onMalformedBinaryMessage(ResponsiveWsConnection c, ByteBuffer message) {}

  @Override
  public void onTextMessage(ResponsiveWsConnection c, String message) {}

  @Override
  public void onUnrequestingBinaryMessage(
    ResponsiveWsConnection c,
    ByteBuffer messageWithHeader,
    int startIndex
  ) {}
}

{
  final EventsListener echoOnBinaryRequestListener = new VoidEventsListener() {
    @Override
    public void onBinaryRequest(
      ResponsiveWsConnection c,
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

#### Заметка об отправке большого количества запросов за раз

Внутренне соединение отзывчивого WebSocket использует 16 битный номер для распознавания сообщений запроса и ответа.
При одномоментной отправке большого количества запросов может произойти отправка 2-ух запросов с одинаковым
идентификатором, что приведёт к потере одного из ответов
(и распознаванию ответа на первый запрос как ответа на второй, если ответ на первый придёт раньше).
Описания проблема появится если:

* Соединение отправило N запросов и ожидает получение ответов на них, после чего было отправлено еще более
65536 - N запросов.
* Соединение не ожидает ответов на запросы и отправило более 65536 запросов.

### Формат заголовков сообщений ResponsiveWebSockets

Отзывчивые WebSockets используют 3 типа сообщений: запросы, ответы и незапрашивающие сообщения.
В начале сообщения находится заголовок, указывающий вид, затем идёт тело.
Заголовок запроса и ответа также содержит 16 битный номер сообщения.

Первый байт заголовка двоичного запроса равен 1, следующие 2 байта содержат номер сообщения.
Пример: `0b00000001_00101010_01100110` (номер сообщения - 10854).

Первый байт заголовка двоичного ответа равен 2, следующие 2 байта содержат номер сообщения.
Пример: `0b00000010_00101010_01100110` (номер сообщения - 10854).

Заголовок незапрашивающего сообщения состоит из 1 байта равного 3.

Сообщения отправляются через двоичные фреймы WebSocket.

### Тестирование

Для запуска тестов требуется выполнить `mvn test` в папке project/. Также существует сценарий, проверяющий
совместимость с версий ResponsiveWebSockets на JavaScript. Для запуска указанного скрипта требуется иметь
зависимости для node.js, устанавливаемые командой `npm install` в каталоге project/modules/tests/. Выполнение
проверки совместимости с версией на JavaScript происходит при помощи команды оболочки, выполняемой из папки
project/:

```shell
mvn -pl modules/tests\
  test-compile\
  -e exec:java\
  -Dexec.mainClass='rws.tests.checkСompatibilityWithVersionInJs.СheckingСompatibilityWithVersionInJsScript'
```

### Ссылки:

- [Документация по API](/doc/translations/API.ru.md)
- [Примеры использования](/project/modules/examples/src/test/java/examples/)
- [ResponsiveWebSockets на JavaScript](https://github.com/ArundhatiApte/responsive-web-sockets-js)
