package bio.algorithms.recurrence

import bio.domain.recurrence.RabbitProblem

object FibonacciRabbits {

  /** Returns the number of rabbit pairs alive after `problem.months` months under the
    * generalized Fibonacci recurrence:
    *   F(1) = F(2) = 1
    *   F(n) = F(n-1) + k * F(n-2)   for n >= 3
    * where `k = problem.litterSize`.
    *
    * Runs in O(n) time and O(1) space via a fold accumulating the pair (F(n-2), F(n-1)).
    */
  def population(problem: RabbitProblem): BigInt = {
    val n = problem.months
    val k = BigInt(problem.litterSize)
    if (n <= 2) BigInt(1)
    else {
      val (_, fN) = (3 to n).foldLeft((BigInt(1), BigInt(1))) {
        case ((prevPrev, prev), _) => (prev, prev + k * prevPrev)
      }
      fN
    }
  }
}
