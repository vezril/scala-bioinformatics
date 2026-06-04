package bio.algorithms.protein

import bio.domain.protein.ProteinMotif

/** Finds every location of a [[ProteinMotif]] in a protein sequence (Rosalind
  * MPRT — "Finding a Protein Motif").
  *
  * Returns the 1-based start positions, in ascending order, at which the motif
  * matches the sequence — every position is tested, so overlapping matches are
  * all reported. The protein is a raw residue `String` (UniProt sequences may
  * contain non-standard residues such as `U`/`X`). `O(L · m)`; the public
  * [[findLocations]] signature is pure and total.
  */
object MotifSearch {

  def findLocations(motif: ProteinMotif, protein: String): Vector[Int] = {
    val m = motif.length
    val l = protein.length
    if (m == 0 || l < m) Vector.empty
    else
      (0 to l - m).toVector
        .filter { start =>
          motif.elements.indices.forall(k => motif.elements(k).matches(protein.charAt(start + k)))
        }
        .map(_ + 1) // 1-based positions
  }
}
