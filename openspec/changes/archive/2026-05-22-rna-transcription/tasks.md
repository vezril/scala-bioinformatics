## 1. RnaString internal constructor

- [x] 1.1 Add `private[domain] def unsafeFrom(s: String): RnaString` to `RnaString` companion — for use by trusted internal callers that guarantee valid content

## 2. RnaTranscription algorithm (TDD)

- [x] 2.1 Write failing tests for `RnaTranscription.transcribe`: Rosalind sample, all-T string, all non-T string (ACG), empty string, mixed bases, single T, single non-T base
- [x] 2.2 Run `sbt test` — confirm tests fail (Red)
- [x] 2.3 Implement `RnaTranscription.transcribe(dna: DnaString): RnaString` using `map` over `dna.value`, dispatching via `DnaNucleotide.fromChar`, mapping each base to its `RnaNucleotide` counterpart (T→U, others preserved), constructing result via `RnaString.unsafeFrom`
- [x] 2.4 Run `sbt test` — confirm all transcription tests pass (Green)
- [x] 2.5 Refactor: verify no raw `Char` literals, no mutable state, idiomatic functional style
- [x] 2.6 Run `sbt test` — confirm all tests still pass after refactor

## 3. Final Verification

- [x] 3.1 Run `sbt compile` — zero errors, zero warnings
- [x] 3.2 Run `sbt test` — all tests pass (count higher than 45)
- [x] 3.3 Verify `RnaTranscription.transcribe` returns `RnaString` directly (not `Either`), and no raw `'T'` or `'U'` char literals appear in the implementation
