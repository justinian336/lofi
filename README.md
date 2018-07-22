# LOF

Provides an interface for implementing Local Outlier Factor and Local Outlier Probability algorithms for anomaly detection.

Local Outlier Factor (LOF) is a local measure of *outlierness* which takes into account the clustered nature of the dataset.
It evaluates the *outlierness* of a point by comparing its distance to its k-neighbors to that of its neighbors. Points laying inside a cluster are assigned a LOF close to 1, while points located away from any cluster receive higher values.

One disadvantage of LOF is that its values are not bounded, are not comparable between datasets and are not easily interpretable. It is difficult to make sense of the magnitude of LOF.

Local Outlier Probability (LoOP) solves these issues by turning the *outlierness* of a point into a probability.

## Usage

The implementation requires the creation of two classes: one for the dataset and one for the primary data unit (or data point).
Your dataset must extend the `LOFDataSource` trait. In particular, this requires the implementation of k-nearest neighbors search. There are several algorithms, and the best implementation depends on your use case.

Your *data point* class must extend the `LOFDataPoint` trait, which contains all the necessary methods.

You can then obtain LOF and LoOP in the following way:

```scala
implicit val myDataSource: MyLOFDataSource = MyLOFDataSource()

val point: MyLOFDataPoint = MyLOFDataPoint(x, y)

// Obtain the LOF of a point by using a neighborhood size of 10
val lof: Future[BigDecimal] = point.getLOF(10)

// Obtain the LoOP of a point for a neighborhood size of 15 and a confidence level of roughly 95% (lambda = 2)
val loop: Future[BigDecimal] = point.getLoOP(10, BigDecimal(2))
```

## Sources

- Breunig M, Kriegel H, Ng R, and Sander J. LOF: identifying density-based local outliers. In: Proceedings of the 2000 ACM SIGMOD international conference on Management of data (SIGMOD '00). ACM, New York, NY, USA, 93-104.
- Kriegel H, Kröger P, Schubert E, et al. LoOP: local outlier probabilities. In: Proceedings of the 18th ACM Conference on Information and Knowledge Management. New York (NY): ACM; 2009.

