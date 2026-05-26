package bio.parsing

import bio.domain.nucleic.DnaString

/** A labeled DNA sequence parsed from FASTA format.
  *
  * The case class has a public constructor — [[bio.parsing.FastaParser]] is the canonical
  * validation entry point for `id` (rejects empty ids). `dna` is already a validated
  * [[DnaString]], so no further invariants are needed at this level.
  */
final case class FastaRecord(id: String, dna: DnaString)
