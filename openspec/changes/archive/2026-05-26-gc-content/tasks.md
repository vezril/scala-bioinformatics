## 1. FastaParseError ADT

- [x] 1.1 Create `FastaParseError.scala` with `sealed trait FastaParseError` and cases `MissingHeader`, `EmptyId`, `InvalidDna(id: String, cause: SequenceError)`
- [x] 1.2 Run `sbt compile` — confirm clean compile

## 2. FastaRecord domain type

- [x] 2.1 Write failing tests for `FastaRecord`: id and dna fields accessible after construction
- [x] 2.2 Run `sbt test` — confirm tests fail (Red)
- [x] 2.3 Implement `final case class FastaRecord(id: String, dna: DnaString)` in `bio.domain`
- [x] 2.4 Run `sbt test` — confirm FastaRecord tests pass (Green)

## 3. FastaParser (TDD)

- [x] 3.1 Write failing tests for `FastaParser.parse` covering: single record, multi-line sequence concatenation, multiple records preserving order (full Rosalind sample), trailing whitespace on header trimmed, empty input → empty list, whitespace-only input → empty list, sequence before header → `MissingHeader`, bare `>` header → `EmptyId`, `>` followed by whitespace only → `EmptyId`, invalid DNA char → `InvalidDna(id, cause)`, header with no sequence → record with empty `DnaString`
- [x] 3.2 Run `sbt test` — confirm tests fail (Red)
- [x] 3.3 Create new package directory `bio.parsing`. Implement `FastaParser.parse(input: String): Either[FastaParseError, List[FastaRecord]]` using a `foldLeft` over trimmed non-empty lines, accumulating a state machine `(currentId: Option[String], currentSeqChunks: Vector[String], completed: List[FastaRecord])`. Finalize the last record after the fold. Use `Vector[String]` accumulation + `mkString` (no mutable collections).
- [x] 3.4 Run `sbt test` — confirm all FastaParser tests pass (Green)
- [x] 3.5 Refactor: verify no `var`, no mutable collections, idiomatic functional style; ensure parser short-circuits on first error rather than collecting multiple
- [x] 3.6 Run `sbt test` — confirm all tests still pass after refactor

## 4. GcContentError ADT

- [x] 4.1 Create `GcContentError.scala` with `sealed trait GcContentError` and cases `OutOfRange(value: Double)`, `NotFinite`
- [x] 4.2 Run `sbt compile` — confirm clean compile

## 5. GcContent value type (TDD)

- [x] 5.1 Write failing tests for `GcContent.from`: 50.0 accepted, 0.0 accepted, 100.0 accepted, 100.1 rejected as `OutOfRange`, -0.1 rejected as `OutOfRange`, NaN rejected as `NotFinite`, `Double.PositiveInfinity` rejected as `NotFinite`. Plus invariant tests: `assertDoesNotCompile("""bio.domain.GcContent(50.0)""")` and `assertDoesNotCompile("""bio.domain.GcContent.from(0).toOption.get.copy(value = 99.0)""")`
- [x] 5.2 Run `sbt test` — confirm tests fail (Red)
- [x] 5.3 Implement `GcContent` as `sealed abstract case class GcContent(value: Double)` with companion `from` and `private[bio] unsafeFrom`. Construction uses anonymous-subclass pattern: `Right(new GcContent(v) {})`.
- [x] 5.4 Run `sbt test` — confirm GcContent.from tests and invariant tests pass (Green)

## 6. GcContent.of (TDD)

- [x] 6.1 Write failing tests for `GcContent.of(dna: DnaString)`: documented example "AGCTATAG" → 37.5%, all-GC "GCGC" → 100%, all-AT "ATAT" → 0%, empty string → 0.0% (by convention), single G → 100%, single A → 0%
- [x] 6.2 Run `sbt test` — confirm tests fail (Red)
- [x] 6.3 Implement `GcContent.of(dna: DnaString): GcContent` on the companion. Use `foldLeft` over `dna.value` counting G and C via `DnaNucleotide.fromChar` pattern match (no raw char literals). Empty input returns `GcContent.unsafeFrom(0.0)`; otherwise `unsafeFrom(100.0 * (g + c) / total)`.
- [x] 6.4 Run `sbt test` — confirm `GcContent.of` tests pass (Green)

## 7. HighestGc algorithm (TDD)

- [x] 7.1 Write failing tests for `HighestGc.find`: Rosalind sample → `Rosalind_0808` with `60.919540%` (tolerance `±1e-3`), empty list → `None`, single record → returned with its GC content, tie resolution → first record wins
- [x] 7.2 Run `sbt test` — confirm tests fail (Red)
- [x] 7.3 Implement `HighestGc.find(records: List[FastaRecord]): Option[(FastaRecord, GcContent)]` using `foldLeft` with strictly-greater comparison (so ties don't replace the incumbent). Pre-compute each record's GcContent once to avoid recomputation.
- [x] 7.4 Run `sbt test` — confirm HighestGc tests pass (Green)
- [x] 7.5 Refactor: verify no `var`, idiomatic functional style; algorithm computes GC content for each record exactly once
- [x] 7.6 Run `sbt test` — confirm all tests still pass after refactor

## 8. Final Verification

- [x] 8.1 Run `sbt compile` — zero errors, zero warnings
- [x] 8.2 Run `sbt test` — all tests pass (count higher than 113)
- [x] 8.3 Review all new source files: no raw char literals in algorithms (all DNA dispatch through `DnaNucleotide.fromChar`), no `var`/mutable collections, no `Either` on total functions (`GcContent.of` returns `GcContent` directly), `GcContent` invariant locked via `sealed abstract case class`
