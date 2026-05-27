package bio.utils

object Utils {
  def format(n: Double): String = {
    BigDecimal(n)
      .setScale(3, BigDecimal.RoundingMode.HALF_UP)
      .toDouble
      .toString
  }
}
