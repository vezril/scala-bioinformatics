package bio.algorithms.matrix

import bio.domain.matrix.{ConsensusProfileProblem, ConsensusProfileResult, ProfileMatrix}
import bio.domain.nucleic.{DnaNucleotide, DnaString}

/** Computes the profile matrix and consensus string for a collection of equal-length
  * DNA records.
  *
  * Algorithm:
  *   1. For each column `j in [0, width)`, count how many records have each nucleotide
  *      at position `j`. This produces a `Map[DnaNucleotide, Int]` per column.
  *   2. Pivot column-major into row-major: a `Map[DnaNucleotide, Vector[Int]]` whose
  *      `Vector[Int]` for each nucleotide has length `width`. This becomes the
  *      [[ProfileMatrix]].
  *   3. For the consensus string, walk the columns again and pick the nucleotide with
  *      the maximum count, with ties broken alphabetically (A < C < G < T). The tie-
  *      break falls out of iterating `Vector(A, C, G, T).find(_.count == max)`.
  *
  * Complexity: `O(width × records × 4)` — linear in the input size with a constant
  * factor of 4 for the nucleotide alphabet. At Rosalind's bounds (records ≤ 10,
  * width ≤ 1000) this is trivially fast.
  *
  * `unsafeFrom` is used for both the [[ProfileMatrix]] (all four keys are populated
  * with `Vector[Int]`s of the same length `width` by construction) and the
  * [[DnaString]] (every character comes from `DnaNucleotide.toChar`, so the alphabet
  * invariant is guaranteed). Both invariants are honored structurally — no need to
  * re-validate.
  */
object ConsensusProfile {

  private val Nucleotides: Vector[DnaNucleotide] =
    Vector(DnaNucleotide.A, DnaNucleotide.C, DnaNucleotide.G, DnaNucleotide.T)

  def compute(problem: ConsensusProfileProblem): ConsensusProfileResult = {
    val records = problem.records
    val width   = records.head.dna.value.length

    // Step 1: column-major per-nucleotide counts.
    val columnCounts: Vector[Map[DnaNucleotide, Int]] =
      (0 until width).toVector.map { col =>
        Nucleotides.map { n =>
          val ch = DnaNucleotide.toChar(n)
          n -> records.count(_.dna.value.charAt(col) == ch)
        }.toMap
      }

    // Step 2: pivot to row-major Map[nuc, Vector[width counts]].
    val counts: Map[DnaNucleotide, Vector[Int]] =
      Nucleotides.map(n => n -> columnCounts.map(_(n))).toMap

    // Step 3: consensus string, alphabetical tie-break.
    val consensusChars: String = columnCounts.map { colCounts =>
      val maxCount = colCounts.values.max
      val winner   = Nucleotides.find(n => colCounts(n) == maxCount).get
      DnaNucleotide.toChar(winner)
    }.mkString

    ConsensusProfileResult(
      consensus = DnaString.unsafeFrom(consensusChars),
      profile = ProfileMatrix.unsafeFrom(counts)
    )
  }
}
