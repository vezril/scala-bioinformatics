package bio.algorithms.protein

import bio.domain.protein.{AminoAcid, SpectrumGraphProblem, SpectrumGraphPeptide}

/** Infers the longest protein matching the spectrum graph of a mass list — Rosalind
  * SGRA ("Using the Spectrum Graph to Infer Peptides").
  *
  * The spectrum graph has a node per mass and a directed edge `u→v` whenever `v − u`
  * equals an amino-acid residue mass. A matching protein spells the edge labels along an
  * increasing path, so the longest matching protein is the longest path in this DAG.
  *
  * Sorting the masses makes every edge go from a lower to a higher index. `best(i)` — the
  * longest edge-label path starting at node `i` — is computed from the highest index down,
  * so each successor is ready before its predecessor. The answer is the longest `best(i)`
  * over start nodes in index order (deterministic). Residue gaps are matched within `1e-4`.
  *
  * Pure and total: sorting plus a `foldLeft` into an immutable `Map`; no `var`, `while`,
  * or mutable collection. Reuses `AminoAcid`.
  */
object SpectrumGraph {

  private val Tolerance: Double = 1e-4

  def longestPeptide(problem: SpectrumGraphProblem): SpectrumGraphPeptide = {
    val sorted = problem.masses.sorted
    val n      = sorted.length

    // best(i) = (path length in residues, residue labels) of the longest path starting at i.
    val best: Map[Int, (Int, List[Char])] =
      (n - 1 to 0 by -1).foldLeft(Map.empty[Int, (Int, List[Char])]) { (acc, i) =>
        val candidates =
          for {
            j    <- (i + 1) until n
            code <- residueOf(sorted(j) - sorted(i)).map(_.code)
          } yield (1 + acc(j)._1, code :: acc(j)._2)
        acc + (i -> candidates.maxByOption(_._1).getOrElse((0, Nil)))
      }

    val peptide =
      (0 until n).iterator
        .map(best)
        .maxByOption(_._1)
        .map(_._2.mkString)
        .getOrElse("")

    SpectrumGraphPeptide(peptide)
  }

  /** The amino acid whose residue mass equals `gap` within tolerance, if any. */
  private def residueOf(gap: Double): Option[AminoAcid] =
    AminoAcid.all.find(a => math.abs(a.monoisotopicMass - gap) < Tolerance)
}
