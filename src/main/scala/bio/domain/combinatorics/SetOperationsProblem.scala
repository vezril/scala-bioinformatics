package bio.domain.combinatorics

/** Validated input bundle for the Rosalind SETO ("Introduction to Set Operations")
  * problem — see [[bio.algorithms.combinatorics.SetOperations.compute]].
  *
  * Bundles a universe size `n` with two subsets `a` and `b` of `{1, …, n}`. The smart
  * constructor validates, first-failure-wins, in the order:
  *   1. `n >= 1` (the universe `{1, …, n}` is defined for positive integers);
  *   2. `n <= 20000` (Rosalind upper bound);
  *   3. every element of `a` lies in `{1, …, n}`;
  *   4. every element of `b` lies in `{1, …, n}`.
  *
  * When a subset has out-of-range elements, the smallest such element is reported, so
  * the error is deterministic regardless of set iteration order.
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply`/`copy` cannot
  * leak around the smart constructor — construct via [[SetOperationsProblem.from]].
  */
sealed abstract case class SetOperationsProblem(n: Int, a: Set[Int], b: Set[Int])

object SetOperationsProblem {

  private val MaxN: Int = 20000

  def from(
      n: Int,
      a: Set[Int],
      b: Set[Int]
  ): Either[SetOperationsProblemError, SetOperationsProblem] =
    if (n < 1) Left(SetOperationsProblemError.NonPositiveUniverse(n))
    else if (n > MaxN) Left(SetOperationsProblemError.ExceedsMaximum(n, MaxN))
    else
      outOfRange("A", a, n)
        .orElse(outOfRange("B", b, n))
        .toLeft(new SetOperationsProblem(n, a, b) {})

  /** An [[SetOperationsProblemError.ElementOutOfRange]] for the smallest element of
    * `set` outside `{1, …, n}`, otherwise `None`.
    */
  private def outOfRange(
      setLabel: String,
      set: Set[Int],
      n: Int
  ): Option[SetOperationsProblemError] =
    set.filter(e => e < 1 || e > n).minOption
      .map(SetOperationsProblemError.ElementOutOfRange(setLabel, _, n))
}
