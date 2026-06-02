package bio.algorithms.protein

import bio.domain.protein.{AminoAcid, InferredProtein, PrefixSpectrum, ProteinString}

/** Reconstructs a protein from its **prefix spectrum** — Rosalind SPEC
  * ("Inferring Protein from Spectrum").
  *
  * The prefix spectrum `L` is the cumulative weight of every prefix of a weighted
  * protein, so each consecutive difference `L(i+1) − L(i)` is exactly one residue's
  * monoisotopic mass. Mapping each difference to the nearest amino-acid mass
  * recovers a protein of length `n − 1` (for `n` weights). Rosalind accepts any
  * protein whose prefix spectrum equals `L`; the isobaric `I`/`L` ambiguity is
  * resolved deterministically by [[AminoAcid.closestByMass]].
  *
  * Pure and total: every difference has a closest residue, and assembling a
  * `ProteinString` from already-typed amino acids cannot fail. No I/O, no mutable
  * state.
  */
object InferProteinFromSpectrum {

  def infer(problem: PrefixSpectrum): InferredProtein = {
    val residues = problem.weights
      .sliding(2)
      .collect { case Vector(a, b) => AminoAcid.closestByMass(b - a) }
      .toVector
    InferredProtein(ProteinString.fromAminoAcids(residues))
  }
}
