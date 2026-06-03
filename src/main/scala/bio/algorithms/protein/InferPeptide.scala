package bio.algorithms.protein

import bio.domain.protein.{AminoAcid, FullSpectrumProblem, InferredPeptide}

import scala.annotation.tailrec

/** Reconstructs a peptide from its full b-ion/y-ion spectrum — Rosalind FULL
  * ("Inferring Peptide from Full Spectrum").
  *
  * The parent mass is discarded and the remaining `2n+2` ions are sorted. Consecutive
  * prefix (b-ion) masses differ by exactly one residue mass (the prefix offset cancels),
  * so the peptide is recovered by walking the prefix series: from the smallest ion,
  * repeatedly take the first larger ion whose gap from the current prefix matches an
  * amino-acid residue mass, emitting that residue, for `n` steps. Interleaved y-ions are
  * skipped because the true next b-ion is the smallest residue-distance match.
  *
  * Pure and total: sorting plus a `@tailrec` walk over the immutable ion vector using
  * iterator combinators — no `var`, `while`, or mutable collection.
  */
object InferPeptide {

  /** A residue gap matches a residue mass within this tolerance (separates near-equal
    * residues such as K/Q while absorbing floating-point noise).
    */
  private val Tolerance: Double = 1e-4

  def infer(problem: FullSpectrumProblem): InferredPeptide = {
    val n    = (problem.masses.size - 3) / 2
    val ions = problem.masses.tail.sorted

    @tailrec
    def walk(current: Double, residues: Vector[Char]): Vector[Char] =
      if (residues.length == n) residues
      else
        nextResidue(ions, current) match {
          case Some((ion, code)) => walk(ion, residues :+ code)
          case None              => residues
        }

    InferredPeptide(walk(ions.head, Vector.empty).mkString)
  }

  /** The first ion greater than `current` whose gap is a residue mass, with that residue's code. */
  private def nextResidue(ions: Vector[Double], current: Double): Option[(Double, Char)] =
    ions.iterator
      .filter(_ > current)
      .flatMap(ion => residueOf(ion - current).map(a => (ion, a.code)))
      .nextOption()

  /** The amino acid whose residue mass equals `gap` within tolerance, if any. */
  private def residueOf(gap: Double): Option[AminoAcid] =
    AminoAcid.all.find(a => math.abs(a.monoisotopicMass - gap) < Tolerance)
}
