package rws.common.responsiveWebSocketConnection.impl.modules.messaging;

import java.nio.ByteBuffer;
import java.util.function.BiConsumer;

import org.scalatest.Assertions.assert;
import org.scalatest.funsuite.AnyFunSuite;

import rws.common.responsiveWebSocketConnection.impl.modules.messaging.TypeOfIncomingMessage;
import rws.common.responsiveWebSocketConnection.impl.modules.messaging.BinaryMessager;

final class BinaryMessagerTest extends AnyFunSuite {
  private val _binaryMessager = BinaryMessager.instance;

  test("extracting type of incoming message") {
    val message = new Array[Byte](_binaryMessager.sizeOfHeaderForRequestOrResponse + 2);
    val bufferOfMessage = ByteBuffer.wrap(message);

    _binaryMessager.fillHeaderAsRequest(12.toChar, message);
    checkExtractingTypeOfMessage(TypeOfIncomingMessage.request, bufferOfMessage);

    _binaryMessager.fillHeaderAsResponse(123.toChar, message);
    checkExtractingTypeOfMessage(TypeOfIncomingMessage.response, bufferOfMessage);

    _binaryMessager.fillHeaderAsUnrequestingMessage(message);
    checkExtractingTypeOfMessage(TypeOfIncomingMessage.unrequestingMessage, bufferOfMessage);
  }

  test("extracting id of request") {
    checkExtractingIdOfMessage(_binaryMessager.fillHeaderAsRequest);
  }

  test("extracting id of response") {
    checkExtractingIdOfMessage(_binaryMessager.fillHeaderAsResponse);
  }
}

final object checkExtractingTypeOfMessage {
  def apply(typeOfMessage: TypeOfIncomingMessage, bufferOfMessage: ByteBuffer) {
    assert(typeOfMessage == BinaryMessager.instance.extractTypeOfMessage(bufferOfMessage));
  }
}

final object checkExtractingIdOfMessage {
  def apply(fillHeaderAsRequestOrResponse: (Char, Array[Byte]) => Unit) {
    var extractedId: Char = 0;
    for (idOfMessage: Int <- 0 to _maxValueOfUint16) {
      fillHeaderAsRequestOrResponse(idOfMessage.toChar, _message);
      extractedId = _binaryMessager.extractIdOfMessage(_bufferOfMessage);
      assert(idOfMessage == extractedId);
    }
  }

  private val _maxValueOfUint16: Int = Math.pow(2, 16).toInt - 1;
  private val _binaryMessager = BinaryMessager.instance;
  private val _message = new Array[Byte](_binaryMessager.sizeOfHeaderForRequestOrResponse + 12);
  private val _bufferOfMessage = ByteBuffer.wrap(_message);
}
