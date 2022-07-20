package rws.tests;

final object ports {
  val forTestingResponsiveWebSockets = _getRandomIntFromRange(40_000, 40_100);
  val forMeasuringSpeed = _getRandomIntFromRange(40_100, 40_200);
  val forCheckingСompatibilityWithVersionInJs = _getRandomIntFromRange(40_200, 40_300);

  private def _getRandomIntFromRange(minIncl: Int, maxExcl: Int): Int = {
    Math.floor(Math.random() * (maxExcl - minIncl + 1)).toInt + minIncl;
  }
}
