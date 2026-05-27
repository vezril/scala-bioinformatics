## Purpose
Define the rna-splicing capability: the `IntronSplicing.splice` primitive (`DnaString` → `DnaString` substring removal), the `RnaSplicingProblem` validated parameter bundle and its error ADT (`RnaSplicingProblemError.EmptyIntron`), and the composite `RnaSplicing.transcribeAndTranslate(problem): Either[TranslationError, ProteinString]` algorithm running the full splice → transcribe → translate pipeline. Serves the Rosalind "RNA Splicing" (SPLC) problem and seeds the framework's first composing-three-primitives algorithm (reusing the existing `RnaTranscription` and `RnaTranslation` capabilities unchanged), modeling the cross-subdomain dependency pattern from `bio.algorithms.protein` to `bio.algorithms.nucleic`.

## Requirements

### Requirement: IntronSplicing.splice removes every occurrence of each intron substring from the source DNA
The system SHALL provide `IntronSplicing.splice(source: bio.domain.nucleic.DnaString, introns: Vector[bio.domain.nucleic.DnaString]): bio.domain.nucleic.DnaString` returning a `DnaString` formed by iteratively removing every occurrence of each intron's character sequence from `source.value` in input order. The algorithm SHALL reside in the `bio.algorithms.nucleic` package. The function SHALL be total — every input produces a defined `DnaString`.

#### Scenario: Rosalind sample exons are produced correctly
- **WHEN** `IntronSplicing.splice(source, introns)` is called with `source.value = "ATGGTCTACATAGCTGACAAACAGCACGTAGCAATCGGTCGAATCTCGAGAGGCATATGGTCACATGATCGGTCGAGCGTGTTTCAAAGTTTGCGCCTAG"` and `introns = Vector("ATCGGTCGAA", "ATCGGTCGAGCGTGT").map(DnaString.from(_).toOption.get)`
- **THEN** the result is a `DnaString` whose `.value` equals `"ATGGTCTACATAGCTGACAAACAGCACGTAGCATCTCGAGAGGCATATGGTCACATGTTCAAAGTTTGCGCCTAG"` (the source with both intron strings removed)

#### Scenario: Empty introns vector returns the source unchanged
- **WHEN** `IntronSplicing.splice(source, Vector.empty)` is called with `source.value = "ACGTACGT"`
- **THEN** the result has `.value == "ACGTACGT"`

#### Scenario: An intron that does not appear is a no-op
- **WHEN** `IntronSplicing.splice(source, introns)` is called with `source.value = "AAAA"` and `introns = Vector(DnaString.from("GGGG").toOption.get)`
- **THEN** the result has `.value == "AAAA"`

#### Scenario: An intron equal to the entire source removes everything
- **WHEN** `IntronSplicing.splice(source, introns)` is called with `source.value = "ACGT"` and `introns = Vector(DnaString.from("ACGT").toOption.get)`
- **THEN** the result has `.value == ""` (an empty `DnaString`)

#### Scenario: Multiple disjoint occurrences of the same intron are all removed
- **WHEN** `IntronSplicing.splice(source, introns)` is called with `source.value = "AAACGTAAACGTAAA"` and `introns = Vector(DnaString.from("CGT").toOption.get)`
- **THEN** the result has `.value == "AAAAAAAAA"` (three `"AAA"` blocks; both `"CGT"` occurrences removed)

#### Scenario: Introns are removed in input order
- **WHEN** `IntronSplicing.splice(source, introns)` is called with `source.value = "ATCG"` and `introns = Vector(DnaString.from("AT").toOption.get, DnaString.from("CG").toOption.get)`
- **THEN** the result has `.value == ""` (first `"AT"` removed → `"CG"`, then `"CG"` removed → `""`)

### Requirement: RnaSplicingProblemError is a sealed ADT of RnaSplicingProblem construction failures
The system SHALL provide a `sealed trait RnaSplicingProblemError` with case `final case class EmptyIntron(index: Int)` indicating that the intron at position `index` (0-indexed in the input `introns` vector) was an empty `DnaString`. The type SHALL reside in the `bio.domain.protein` package.

#### Scenario: EmptyIntron carries the index of the offending intron
- **WHEN** `RnaSplicingProblemError.EmptyIntron(2)` is constructed
- **THEN** the value's `index` field equals `2`

### Requirement: RnaSplicingProblem is a validated parameter bundle for the rna-splicing algorithm
The system SHALL provide a `sealed abstract case class RnaSplicingProblem(source: bio.domain.nucleic.DnaString, introns: Vector[bio.domain.nucleic.DnaString])`. Construction SHALL be possible only through `RnaSplicingProblem.from(source: DnaString, introns: Vector[DnaString]): Either[RnaSplicingProblemError, RnaSplicingProblem]` enforcing that no intron is empty. The function SHALL return `Left(RnaSplicingProblemError.EmptyIntron(index))` carrying the 0-indexed position of the *first* empty intron in input order. The synthesized `apply` and `copy` SHALL NOT be public — direct construction `RnaSplicingProblem(source, introns)` MUST be a compile error. The type SHALL reside in the `bio.domain.protein` package.

#### Scenario: Rosalind sample inputs are accepted
- **WHEN** `RnaSplicingProblem.from(source, introns)` is called with the Rosalind sample (source of length 99, two non-empty introns of length 10 and 15)
- **THEN** the result is `Right(<RnaSplicingProblem with the supplied source and introns>)`

#### Scenario: An empty introns vector is accepted
- **WHEN** `RnaSplicingProblem.from(DnaString.from("ACGT").toOption.get, Vector.empty)` is called
- **THEN** the result is `Right(<RnaSplicingProblem with the supplied source and no introns>)`

#### Scenario: An empty source is accepted
- **WHEN** `RnaSplicingProblem.from(DnaString.from("").toOption.get, Vector.empty)` is called
- **THEN** the result is `Right(<RnaSplicingProblem with an empty source>)`

#### Scenario: An empty intron at position 0 is rejected
- **WHEN** `RnaSplicingProblem.from(DnaString.from("ACGT").toOption.get, Vector(DnaString.from("").toOption.get))` is called
- **THEN** the result is `Left(RnaSplicingProblemError.EmptyIntron(0))`

#### Scenario: An empty intron at a later position is rejected, carrying that position
- **WHEN** `RnaSplicingProblem.from(DnaString.from("ACGT").toOption.get, Vector(DnaString.from("AC").toOption.get, DnaString.from("").toOption.get, DnaString.from("GT").toOption.get))` is called
- **THEN** the result is `Left(RnaSplicingProblemError.EmptyIntron(1))`

#### Scenario: Direct apply does not compile
- **WHEN** source code `bio.domain.protein.RnaSplicingProblem(bio.domain.nucleic.DnaString.from("A").toOption.get, Vector.empty)` is compiled
- **THEN** the compiler rejects the expression

### Requirement: RnaSplicing.transcribeAndTranslate runs the splice → transcribe → translate pipeline
The system SHALL provide `RnaSplicing.transcribeAndTranslate(problem: RnaSplicingProblem): Either[bio.domain.protein.TranslationError, bio.domain.protein.ProteinString]`. The function SHALL: (1) call `IntronSplicing.splice(problem.source, problem.introns)` to produce the spliced DNA; (2) call `RnaTranscription.transcribe` on the spliced DNA to produce an `RnaString`; (3) call `RnaTranslation.translate` on the `RnaString` and return its result. The algorithm SHALL reside in the `bio.algorithms.protein` package. Any `TranslationError` from the final step SHALL be propagated unchanged.

#### Scenario: Rosalind sample produces "MVYIADKQHVASREAYGHMFKVCA"
- **WHEN** `RnaSplicing.transcribeAndTranslate(problem)` is called with `problem = RnaSplicingProblem.from(source, introns).toOption.get` where `source.value = "ATGGTCTACATAGCTGACAAACAGCACGTAGCAATCGGTCGAATCTCGAGAGGCATATGGTCACATGATCGGTCGAGCGTGTTTCAAAGTTTGCGCCTAG"` and `introns = Vector("ATCGGTCGAA", "ATCGGTCGAGCGTGT").map(DnaString.from(_).toOption.get)`
- **THEN** the result is `Right(<ProteinString with value="MVYIADKQHVASREAYGHMFKVCA">)`

#### Scenario: Empty introns yields direct transcription + translation of the source
- **WHEN** `RnaSplicing.transcribeAndTranslate(problem)` is called with `problem.source.value = "AUGGCCAUGGCGCCCAGAACUGAGAUCAAUAGUACCCGUAUUAACGGGUGA".replace('U','T')` (the spec-8 sample DNA) and `problem.introns == Vector.empty`
- **THEN** the result is `Right(<ProteinString with value="MAMAPRTEINSTRING">)` (the same output as direct translation of the spec-8 sample)

#### Scenario: An intron equal to the entire source produces translation of the empty RNA
- **WHEN** `RnaSplicing.transcribeAndTranslate(problem)` is called with `problem.source.value = "ATGTAA"` and `problem.introns = Vector(DnaString.from("ATGTAA").toOption.get)`
- **THEN** the result is `Right(<ProteinString with value="">)` (empty source after splicing → empty RNA → empty protein)

#### Scenario: Translation failures from the inner RnaTranslation are propagated
- **WHEN** `RnaSplicing.transcribeAndTranslate(problem)` is called with an input whose spliced/transcribed RNA would cause `RnaTranslation.translate` to return `Left(translationError)`
- **THEN** the result is `Left(translationError)` — the same `TranslationError` is propagated unchanged
