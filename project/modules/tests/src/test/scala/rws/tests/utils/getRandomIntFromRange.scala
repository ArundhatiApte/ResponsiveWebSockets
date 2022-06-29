package rws.tests.utils;

final object getRandomIntFromRange {
  def apply(min: Int, max: Int): Int = {
    (Math.random() * (min - max + 1)).toInt + min;
  }
}
