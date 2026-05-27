package bio.algorithms.protein

import bio.algorithms.nucleic.{IntronSplicing, RnaTranscription}
import bio.domain.protein.{ProteinString, RnaSplicingProblem, TranslationError}

/** The full RNA-splicing pipeline (Rosalind SPLC): remove introns from the source
  * DNA, transcribe the resulting exon DNA to RNA, and translate the RNA to a
  * protein.
  *
  * Three-step composition:
  *   1. [[IntronSplicing.splice]] (`DnaString` → `DnaString`) removes each intron's
  *      character sequence from the source.
  *   2. [[RnaTranscription.transcribe]] (`DnaString` → `RnaString`) converts the
  *      spliced DNA to RNA.
  *   3. [[RnaTranslation.translate]] (`RnaString` → `Either[TranslationError, ProteinString]`)
  *      translates the RNA codon-by-codon, halting at the first stop codon.
  *
  * **Cross-subdomain dependency:** this algorithm in `bio.algorithms.protein` imports
  * `IntronSplicing` and `RnaTranscription` from `bio.algorithms.nucleic`. That's the
  * established framework pattern — composition flows from nucleic vocabulary toward
  * the protein vocabulary, not the other way around.
  *
  * **Error policy:** the splice and transcribe steps are total. The only failure
  * mode is the inner `RnaTranslation.translate`. Its `TranslationError` is propagated
  * unchanged — no wrapping, no additional error type for the composite.
  */
object RnaSplicing {

  def transcribeAndTranslate(
      problem: RnaSplicingProblem
  ): Either[TranslationError, ProteinString] = {
    val spliced = IntronSplicing.splice(problem.source, problem.introns)
    val rna     = RnaTranscription.transcribe(spliced)
    RnaTranslation.translate(rna)
  }
}
