package bio.algorithms.combinatorics

import bio.domain.combinatorics.{LexOrdering, LexOrderProblem}

/** Enumerates every string of length 1 to `n` over an ordered alphabet, in
  * varying-length lexicographic order (Rosalind LEXV — "Ordering Strings of
  * Varying Length Lexicographically").
  *
  * The order is the one in which a string precedes any longer string that
  * extends it (`APPLE < APPLET`) — equivalently a **pre-order** depth-first
  * traversal of the alphabet tree truncated at depth `n`: emit the current
  * string, then recurse into each child in the given alphabet order. Because the
  * alphabet is iterated in its given order and each node is emitted before its
  * descendants, the output is already in the required order with no explicit
  * sort. The unlike-LEXF detail is that *every* prefix is emitted, not just the
  * depth-`n` leaves.
  *
  * Pure functional recursion over immutable `Vector`s; the public [[enumerate]]
  * signature is total.
  */
object VaryingLengthLexOrder {

  def enumerate(problem: LexOrderProblem): LexOrdering = {
    val alphabet = problem.alphabet
    val n        = problem.maxLength

    def go(prefix: String): Vector[String] =
      alphabet.flatMap { symbol =>
        val s = prefix + symbol
        if (s.length < n) s +: go(s) else Vector(s)
      }

    LexOrdering(go(""))
  }
}
