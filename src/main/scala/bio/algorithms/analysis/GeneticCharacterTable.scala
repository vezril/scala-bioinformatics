package bio.algorithms.analysis

import bio.domain.analysis.GeneticCharacterTableProblem

/** Builds the character table of an aligned, characterizable DNA matrix
  * (Rosalind spec 33 — "Creating a Character Table from Genetic Strings").
  *
  * Each column of the matrix encodes one potential character — a binary
  * partition of the rows based on the symbol that appears there. A column is
  * **nontrivial** iff it has both possible symbols and neither side is a
  * singleton. Trivial columns (uniform or a 4-1-style split where one row
  * diverges) are excluded.
  *
  * **Row encoding.** For each nontrivial column `j`, the bit at row `i` is
  * `'1'` iff `sequence(i).charAt(j) == sequence(0).charAt(j)`, else `'0'`.
  * That is — row `i` gets a `'1'` exactly when it agrees with row `0` at the
  * column in question. Deterministic, matches the published Rosalind sample
  * row-for-row.
  *
  * **Row order.** Column-input order: column `0`'s row precedes column `1`'s,
  * etc. The spec explicitly permits any row order; we pick the left-to-right
  * reading because it preserves the structural signal of "which column did
  * this character come from?".
  *
  * **Sister spec.** [[bio.algorithms.graph.CharacterTable]] (spec 32 — CSTR)
  * produces the same output *shape* but from a Newick tree, with a different
  * canonical encoding rule (lex-first-taxon-side gets `0`s). The two
  * algorithms are intentionally independent — the operations they perform
  * (tree-edge split enumeration vs column-wise symbol partitioning) are
  * different, even though the output type matches.
  *
  * **Complexity:** `O(rows · cols)` character reads + `O(cols)` row builds.
  * At the Rosalind cap of 100×300, that's 30 000 reads — microseconds.
  */
object GeneticCharacterTable {

  def compute(problem: GeneticCharacterTableProblem): Vector[String] = {
    val rows   = problem.sequences
    val cols   = problem.length
    val nRows  = problem.size

    (0 until cols).iterator.flatMap { j =>
      val column    = rows.map(_.value.charAt(j))
      val reference = column.head
      val matches   = column.count(_ == reference)
      // Skip the column if either side of the partition has fewer than 2 rows.
      // This subsumes "uniform" (matches == nRows ⇒ other side has 0 rows) and
      // "4-1 split" (one side has 1 row) — both are trivial.
      if (matches < 2 || nRows - matches < 2) None
      else Some(column.iterator.map(c => if (c == reference) '1' else '0').mkString)
    }.toVector
  }
}
