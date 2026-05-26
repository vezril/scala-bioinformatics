package bio.algorithms.analysis

import bio.domain.analysis.HammingError
import bio.domain.nucleic.DnaString

/** Hamming distance between two equal-length DNA sequences: the number of positions at
  * which the two sequences differ.
  *
  * Returns `Left(HammingError.LengthMismatch(lengthA, lengthB))` when the inputs are of
  * unequal length. Two empty `DnaString`s have distance `0`.
  *
  * Character inequality is checked directly on the underlying `Char` values; both inputs
  * are validated `DnaString`s (alphabet ⊆ `{A, C, G, T}`), so going through
  * `DnaNucleotide.fromChar` adds no semantic value.
  */
object HammingDistance {

  def between(a: DnaString, b: DnaString): Either[HammingError, Int] =
    if (a.value.length != b.value.length)
      Left(HammingError.LengthMismatch(a.value.length, b.value.length))
    else
      Right(a.value.lazyZip(b.value).count { case (x, y) => x != y })
}
