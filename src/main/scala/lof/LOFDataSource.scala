package lof

import scala.concurrent.{ExecutionContext, Future}
import scala.math.BigDecimal

/**
  * Trait to be extended by your data source
  * @tparam T the type of the concrete class representing the primary data unit
  */
trait LOFDataSource[T<:LOFDataPoint[T]] {

  /**
    * A custom implementation of k-nearest neighbor search. The speed of the LOF and LoOP implementations greatly depends
    * on the efficiency of this method.
    * @param p a data point
    * @param k the size of the neighborhood
    * @return
    */
  def getKNN(p: T, k: Int): Future[(LOFDataPoint[T], List[(T, BigDecimal)], Int)]

  /**
    * Returns the mean Probability Local Outlier Factor. Necessary for LoOP calculation
    * @param k the size of the neighborhood
    * @param p the data point
    * @param λ the confidence level as a Normal Distribution z value
    * @return
    */
  def getNPLOF(k: Int, p: T, λ: BigDecimal)(implicit ec: ExecutionContext): Future[BigDecimal]

}
