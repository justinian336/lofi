package lof

/**
  * A utility type that represents objects for which a distance can be calculated
  * @tparam T the concrete class extending this class
  */
trait Distance[T<:LOFDataPoint[T]] {

  def distance(other: T): BigDecimal

}
