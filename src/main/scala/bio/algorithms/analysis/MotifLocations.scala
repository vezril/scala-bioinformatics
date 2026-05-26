package bio.algorithms.analysis

import bio.domain.nucleic.DnaString

/** Locates every occurrence of a short DNA motif inside a longer DNA sequence.
  *
  * Positions are reported as 1-based indices (the leftmost character is at position 1,
  * matching the Rosalind convention) and overlapping matches are included.
  *
  * The function is total over all pairs of `DnaString`s:
  *   - An empty motif returns `Vector.empty` (an empty pattern has no meaningful
  *     starting position; matches the framework's "empty input → empty output" idiom)
  *   - A motif longer than the text returns `Vector.empty`
  *   - An empty text returns `Vector.empty`
  *   - No match returns `Vector.empty`
  *
  * The result is always in ascending order — a natural consequence of the left-to-right
  * scan.
  */
object MotifLocations {

  def find(text: DnaString, motif: DnaString): Vector[Int] = {
    val t = text.value
    val m = motif.value
    if (m.isEmpty) Vector.empty
    else
      (0 to (t.length - m.length)).iterator
        .filter(i => t.startsWith(m, i))
        .map(_ + 1)
        .toVector
  }
}
