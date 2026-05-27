## 1. IntronSplicing primitive (TDD)

- [x] 1.1 Write `IntronSplicingSpec` (package `bio.algorithms.nucleic`) covering all 6 scenarios: Rosalind sample exon extraction; empty introns → source unchanged; intron not present → source unchanged; intron equals entire source → empty result; multiple disjoint occurrences all removed; input order respected (`["AT", "CG"]` against `"ATCG"` → `""`); run, observe red
- [x] 1.2 Implement `bio.algorithms.nucleic.IntronSplicing.splice(source: DnaString, introns: Vector[DnaString]): DnaString` per design.md: `foldLeft` over `introns` calling `.replace(intron.value, "")`, wrap with `DnaString.unsafeFrom`; run all tests green
- [x] 1.3 Refactor pass: scaladoc describing the input-order semantics, the `unsafeFrom` justification (alphabet preserved, length non-increasing), and the "Rosalind precondition: introns guaranteed substrings" assumption

## 2. RnaSplicingProblemError ADT (TDD)

- [x] 2.1 Write `RnaSplicingProblemErrorSpec` (package `bio.domain.protein`) covering `EmptyIntron(2)` field-exposure; run, observe red
- [x] 2.2 Implement `bio.domain.protein.RnaSplicingProblemError` sealed trait with `EmptyIntron(index: Int)` case; run all tests green
- [x] 2.3 Refactor pass: scaladoc explaining the single error case and the 0-indexed convention

## 3. RnaSplicingProblem bundle (TDD)

- [x] 3.1 Write `RnaSplicingProblemSpec` covering all 6 scenarios: accept Rosalind sample inputs; accept empty introns vector; accept empty source; reject empty intron at index 0 (`EmptyIntron(0)`); reject empty intron at later position (`EmptyIntron(1)`); `assertDoesNotCompile` for direct `RnaSplicingProblem(...)`; run, observe red
- [x] 3.2 Implement `bio.domain.protein.RnaSplicingProblem` as `sealed abstract case class RnaSplicingProblem(source: DnaString, introns: Vector[DnaString])` with `from` smart constructor scanning `introns` for the first empty intron via `.zipWithIndex.find`; run all tests green
- [x] 3.3 Refactor pass: scaladoc describing the single validation rule, the first-failure-in-input-order behavior, and the `sealed abstract case class` pattern

## 4. RnaSplicing composite algorithm (TDD)

- [x] 4.1 Write `RnaSplicingSpec` (package `bio.algorithms.protein`) covering all 4 scenarios: Rosalind sample → `Right(ProteinString("MVYIADKQHVASREAYGHMFKVCA"))`; empty introns + spec-8 sample DNA → `Right(ProteinString("MAMAPRTEINSTRING"))`; intron equal to source → `Right(ProteinString(""))`; translation failure propagated (construct a problem whose spliced RNA fails to translate); run, observe red
- [x] 4.2 Implement `bio.algorithms.protein.RnaSplicing.transcribeAndTranslate(problem: RnaSplicingProblem): Either[TranslationError, ProteinString]` as the three-step pipeline: `IntronSplicing.splice` → `RnaTranscription.transcribe` → `RnaTranslation.translate`; run all tests green
- [x] 4.3 Refactor pass: scaladoc describing the three-step pipeline, the cross-subdomain dependency (`protein` algorithm importing `nucleic.IntronSplicing`), and the error-propagation policy (no wrapping)

## 5. Whole-suite verification

- [x] 5.1 Run `sbt test` and confirm all existing suites plus the new tests pass with no warnings
- [x] 5.2 Verify only additive changes (`git status` should show only new files under `bio/algorithms/nucleic/`, `bio/algorithms/protein/`, `bio/domain/protein/`, and matching test directories)
