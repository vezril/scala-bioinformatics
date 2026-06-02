package bio.algorithms.protein

import bio.algorithms.nucleic.{DnaReverseComplement, RnaTranscription}
import bio.domain.nucleic.DnaString
import bio.domain.protein.CodonOutcome.{AminoAcidProduct, Stop}
import bio.domain.protein.{
  AminoAcid,
  Codon,
  CodonOutcome,
  GeneticCode,
  OpenReadingFrameProblem,
  ProteinString,
  OpenReadingFrames => Result
}

/** Finds every distinct candidate protein translatable from an open reading frame
  * — Rosalind ORF ("Open Reading Frames").
  *
  * A DNA string implies six reading frames: three on the string itself and three
  * on its reverse complement. An open reading frame begins at a start codon `AUG`
  * and ends at the first in-frame stop codon; the candidate protein is the
  * translation of that frame up to (but not including) the stop. A start codon with
  * no downstream in-frame stop yields no candidate.
  *
  * Pure and total: it reuses the tested [[DnaReverseComplement]], [[RnaTranscription]],
  * and [[GeneticCode]] primitives, scans each frame's codon outcomes functionally
  * (no `var`/`while`), and returns the candidate proteins deduplicated and ordered
  * deterministically (by value) — Rosalind permits any order.
  */
object OpenReadingFrames {

  /** Frame offsets within a strand: the three reading frames. */
  private val Offsets: Range = 0 until 3

  def find(problem: OpenReadingFrameProblem): Result = {
    val forward = problem.dna
    val reverse = DnaReverseComplement.reverseComplement(forward)

    val proteins =
      (strandProteins(forward) ++ strandProteins(reverse)).distinct.sortBy(_.value)

    Result(proteins)
  }

  /** All candidate proteins found in the three reading frames of a single strand. */
  private def strandProteins(dna: DnaString): Vector[ProteinString] = {
    val rna = RnaTranscription.transcribe(dna).value
    Offsets.toVector.flatMap(offset => frameProteins(rna, offset))
  }

  /** Candidate proteins in one reading frame, identified by the RNA string and the
    * frame's starting offset. Trailing 1–2 nucleotides that cannot form a complete
    * codon are ignored.
    */
  private def frameProteins(rna: String, offset: Int): Vector[ProteinString] = {
    val outcomes = codonOutcomes(rna, offset)
    outcomes.indices.iterator
      .collect { case i if isStart(outcomes(i)) => orfFrom(outcomes, i) }
      .flatten
      .toVector
  }

  /** The translated outcome of every complete codon in this frame, in order. */
  private def codonOutcomes(rna: String, offset: Int): Vector[CodonOutcome] =
    Iterator
      .iterate(offset)(_ + 3)
      .takeWhile(_ + 3 <= rna.length)
      .flatMap(i => Codon.fromChars(rna(i), rna(i + 1), rna(i + 2)))
      .map(GeneticCode.translate)
      .toVector

  /** A start codon translates to methionine (`M`). */
  private def isStart(outcome: CodonOutcome): Boolean =
    outcome == AminoAcidProduct(AminoAcid.M)

  /** The protein read from `start` up to the first downstream stop. Returns `None`
    * if no in-frame stop follows (an incomplete ORF, which is not a candidate).
    */
  private def orfFrom(
      outcomes: Vector[CodonOutcome],
      start: Int
  ): Option[ProteinString] = {
    val rest = outcomes.drop(start)
    val (run, after) = rest.span {
      case AminoAcidProduct(_) => true
      case Stop                => false
    }
    if (after.nonEmpty)
      Some(ProteinString.fromAminoAcids(run.collect { case AminoAcidProduct(aa) => aa }))
    else
      None
  }
}
