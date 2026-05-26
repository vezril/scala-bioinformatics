package bio.algorithms.recurrence

import bio.domain.recurrence.MortalRabbitProblem

/** Mortal Fibonacci rabbits: every pair produces one offspring pair per month from its
  * second month onward, and dies after exactly `problem.lifespan` months.
  *
  * Maintains a `Vector[BigInt]` of length `lifespan`, indexed by age (0 = newborn,
  * `lifespan - 1` = oldest still alive). Each month transition:
  *
  *   - newborns = sum of pairs at ages `1..lifespan-1` (all adults reproduce one pair)
  *   - everyone ages up one bin; the pair at age `lifespan - 1` dies (is not carried forward)
  *
  * Runs in `O(n × m)` time and `O(m)` space. Uses `BigInt` end-to-end to avoid
  * overflow for large `n`.
  */
object MortalFibonacciRabbits {

  def population(problem: MortalRabbitProblem): BigInt = {
    val n = problem.months
    val m = problem.lifespan
    val initial: Vector[BigInt] = BigInt(1) +: Vector.fill(m - 1)(BigInt(0))
    val finalState: Vector[BigInt] = (2 to n).foldLeft(initial) { (v, _) =>
      val newborns = v.drop(1).sum
      newborns +: v.dropRight(1)
    }
    finalState.sum
  }
}
