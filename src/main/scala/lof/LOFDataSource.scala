package lof

import generic.GenericUtils.{absDiffMap, reducerPoly}
import shapeless.ops.hlist.{LeftFolder, Mapper, Zip}
import shapeless.{::, Generic, HList, HNil}

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
  def getKNN[H<: HList, K<:HList, L<: HList](p: T, k: Int)
                                            (implicit dataSource: LOFDataSource[T],
                                              ec: ExecutionContext,
                                              gen: Generic.Aux[T, H],
                                              zipper: Zip.Aux[H::H::HNil, L],
                                              diffMapper: Mapper.Aux[absDiffMap.type, L, H],
                                              folder: LeftFolder.Aux[H, BigDecimal, reducerPoly.type, BigDecimal]): Future[(LOFDataPoint[T], List[(T, BigDecimal)], Int)]

  /**
    * Returns the mean Probability Local Outlier Factor. Necessary for LoOP calculation
    * @param k the size of the neighborhood
    * @param p the data point
    * @param λ the confidence level as a Normal Distribution z value
    * @return
    */
  def getNPLOF[H<: HList, K<:HList, L<: HList]
  (k: Int, p: T, λ: BigDecimal)(implicit dataSource: LOFDataSource[T],
                                ec: ExecutionContext,
                                gen: Generic.Aux[T, H],
                                zipper: Zip.Aux[H::H::HNil, L],
                                diffMapper: Mapper.Aux[absDiffMap.type, L, H],
                                folder: LeftFolder.Aux[H, BigDecimal, reducerPoly.type, BigDecimal]): Future[BigDecimal]

}
