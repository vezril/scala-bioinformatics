package bio.algorithms.nucleic

import bio.domain.nucleic.DnaString

/** Removes intron substrings from a DNA sequence to produce the spliced exon DNA.
  *
  * For each intron in `introns`, in input order, removes every disjoint left-to-right
  * occurrence of the intron's character sequence from the running source string. The
  * result is the original `source` with all intron occurrences excised.
  *
  * **Semantics:** the inner loop uses `String.replace(CharSequence, CharSequence)`,
  * which is the JDK's literal (non-regex) replace-all. It iterates left-to-right and
  * replaces every disjoint occurrence in a single pass. The Rosalind precondition
  * — "introns are substrings of `s` and only one solution exists" — guarantees this
  * produces the unique correct exon sequence.
  *
  * **`unsafeFrom` justification:** the result string is built from `source.value` with
  * substrings deleted. Every character is still from the validated alphabet
  * `{A, C, G, T}`, and the length is non-increasing (`≤ source.value.length ≤ 1000`).
  * Both `DnaString` invariants are preserved structurally.
  *
  * **Note on empty introns:** an empty `DnaString` passed as an intron is a no-op
  * under JDK 11+ semantics (`"abc".replace("", "")` returns `"abc"`). Callers using
  * the composite [[bio.algorithms.protein.RnaSplicing]] are protected by
  * [[bio.domain.protein.RnaSplicingProblem]] which rejects empty introns at the
  * bundle boundary; bare-params callers here should ensure introns are non-empty if
  * they want portable behavior across JDK versions.
  */
object IntronSplicing {

  def splice(source: DnaString, introns: Vector[DnaString]): DnaString = {
    val spliced = introns.foldLeft(source.value) { (acc, intron) =>
      acc.replace(intron.value, "")
    }
    DnaString.unsafeFrom(spliced)
  }
}
