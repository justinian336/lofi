package lof

import org.apache.commons.math3.special.Erf.erf

import scala.concurrent.{ExecutionContext, Future}
import scala.math.{BigDecimal, max, sqrt}

/**
  * Type to be extended by the primary data unit class
  * @tparam T The type of the concrete class implementing this trait
  */
trait LOFDataPoint[T<:LOFDataPoint[T]] extends Distance[T] with Identifiable{self: T=>

  /**
    * Get the local reachability distance of this point
    * @return
    */
  def getLRD(k: Int)(implicit dataSource: LOFDataSource[T], ec: ExecutionContext): Future[BigDecimal] = {

    //  First, get the kNNs of `p`:
    val kNN = dataSource.getKNN(self, k)

    kNN.flatMap{case (_, neighborhood, _)=>
      //  Then, get the support points:
      Future.sequence(
        neighborhood.map{case (p, _)=> dataSource.getKNN(p, k)}
      ).map { n =>
        1 / n.map { case (neighbor: T, knn, _) =>
          //    Get the k-distance for each point:
          val kDist = knn.maxBy { case (_, d) => d }._2

          //    Use it to get the reachability distance:
          kDist.max(distance(neighbor))
        }.foldLeft(BigDecimal.valueOf(0))(_ + _) / neighborhood.size
      }
    }
  }

  /**
    * Obtain the Local Outlier Factor of this point
    * @return
    */
  def getLOF(k: Int)(implicit dataSource: LOFDataSource[T], ec: ExecutionContext): Future[BigDecimal] = {
    for{
      (_, neighborhood, _) <- dataSource.getKNN(self, k)
      lrdP <- getLRD(k)
      lrdQ <- Future.sequence(
        neighborhood.map{case (q, _)=>
          q.getLRD(k)
        }
      )
    } yield {
      lrdQ.map(_ / lrdP).sum/neighborhood.size
    }

  }

  /**
    * Gets the standard distance of this point with respect to its neighborhood
    * @return
    */
  def sigmaDist(k: Int)(implicit dataSource: LOFDataSource[T], ec: ExecutionContext): Future[BigDecimal] = {
    dataSource.getKNN(self, k).map{case (p, neighborhood, k)=>
      neighborhood.foldLeft(BigDecimal.valueOf(0)){case (acc, v)=>
        acc + v._2.pow(2)
      }/neighborhood.size
    }
  }

  /**
    * Gets the probability-distance of this point
    * @param k neighborhood size
    * @param λ the confidence level as a Normal Distribution z value
    * @return
    */
  def pDist(k: Int, λ: BigDecimal)(implicit dataSource: LOFDataSource[T], ec: ExecutionContext): Future[BigDecimal] = {
    sigmaDist(k).map(λ * _)
  }

  /**
    * Get the Probability Local Outlier Factor (PLOF) of this point
    * @param k neighborhood size
    * @param λ the confidence level as a Normal Distribution z value
    * @return
    */
  def getPLOF(k: Int, λ: BigDecimal)(implicit dataSource: LOFDataSource[T], ec: ExecutionContext): Future[BigDecimal] = {
    for{
      (_, neighborhood, _) <- dataSource.getKNN(self, k)
      pDistP <- pDist(k, λ)
      pDistQ <- Future.sequence(
        neighborhood.map{case (q, _)=>
          q.pDist(k, λ)
        }
      ).map {pdistsQ=>
        pdistsQ.sum / pdistsQ.size
      }
    } yield {
      (pDistP/pDistQ) - 1
    }
  }

  /**
    * Gets the Local Outlier Probability for this point
    * @param k neighborhood size
    * @param λ the confidence level as a Normal Distribution z value
    * @return
    */
  def getLoOP(k: Int, λ: BigDecimal)(implicit dataSource: LOFDataSource[T], ec: ExecutionContext): Future[BigDecimal] = {
    getPLOF(k, λ).flatMap{plof=>
      dataSource.getNPLOF(k, self, λ).map{nPlof=>
        max(0, erf((plof/(nPlof*sqrt(BigDecimal.valueOf(2).toDouble))).toDouble))
      }
    }
  }

}
