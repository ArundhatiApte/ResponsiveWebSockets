package rws.tests.checkСompatibilityWithVersionInJs;

import org.scalatest.Assertions.assert;
import org.scalatest.funsuite.AnyFunSuite;

import rws.tests.checkСompatibilityWithVersionInJs.СheckingСompatibilityWithVersionInJsTester;

final object СheckingСompatibilityWithVersionInJsScript extends App {
  val port = rws.tests.ports.forCheckingСompatibilityWithVersionInJs;
  val pathToStartingServerInNodeJsScript =
    "./modules/tests/src/test/js/startingResponsiveWebSocketServer/startResponsiveWebSocketServer";

  val tester = new СheckingСompatibilityWithVersionInJsTester(
    port,
    pathToStartingServerInNodeJsScript
  );
  org.scalatest.run(tester);
  System.exit(0);
}
