package bio.algorithms.protein

import bio.domain.protein.{
  AminoAcid,
  ProteinMass => Result,
  ProteinMassProblem
}

/** Computes the total monoisotopic mass of a protein — Rosalind PRTM
  * ("Calculating Protein Mass").
  *
  * The weight of a weighted string is the sum of its symbols' weights; for a protein
  * each symbol's weight is its residue's monoisotopic mass. The algorithm lifts each
  * character to its [[AminoAcid]] via [[AminoAcid.fromChar]] and sums the masses.
  *
  * Pure and total: `ProteinString` guarantees every character is a valid code, so
  * `fromChar` always yields `Some` (the `0.0` default is defensive and unreachable);
  * the empty protein sums to `0.0`. No I/O, no mutable state.
  */
object ProteinMass {

  def calculate(problem: ProteinMassProblem): Result = {
    val total = problem.protein.value.iterator
      .map(c => AminoAcid.fromChar(c).fold(0.0)(_.monoisotopicMass))
      .sum
    Result(total)
  }
}
