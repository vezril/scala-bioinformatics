## 1. DnaNucleotide ADT (TDD)

- [x] 1.1 Write failing tests for `DnaNucleotide`: all four case objects exist, `validChars` contains exactly {A,C,G,T} and excludes U, `fromChar` returns `Some` for each valid base and `None` for invalid/lowercase
- [x] 1.2 Run `sbt test` — confirm tests fail (Red)
- [x] 1.3 Implement `DnaNucleotide` sealed trait with `A`, `C`, `G`, `T` case objects and companion with `validChars` and `fromChar`
- [x] 1.4 Run `sbt test` — confirm `DnaNucleotide` tests pass (Green)

## 2. RnaNucleotide ADT (TDD)

- [x] 2.1 Write failing tests for `RnaNucleotide`: all four case objects exist, `validChars` contains exactly {A,C,G,U} and excludes T, `fromChar` returns `Some` for each valid base and `None` for T/lowercase
- [x] 2.2 Run `sbt test` — confirm tests fail (Red)
- [x] 2.3 Implement `RnaNucleotide` sealed trait with `A`, `C`, `G`, `U` case objects and companion with `validChars` and `fromChar`
- [x] 2.4 Run `sbt test` — confirm `RnaNucleotide` tests pass (Green)

## 3. SequenceError (rename DnaError)

- [x] 3.1 Create `SequenceError.scala` with `sealed trait SequenceError` and `InvalidCharacter(ch: Char)` / `ExceedsMaxLength(length: Int)` cases
- [x] 3.2 Delete `DnaError.scala`
- [x] 3.3 Update `DnaString.scala` to return `Either[SequenceError, DnaString]`
- [x] 3.4 Update `DnaStringSpec` to reference `SequenceError` instead of `DnaError`
- [x] 3.5 Run `sbt test` — confirm all tests still pass

## 4. Update DnaString to derive alphabet from DnaNucleotide

- [x] 4.1 Replace hardcoded `ValidChars = Set('A','C','G','T')` in `DnaString` with `DnaNucleotide.validChars`
- [x] 4.2 Add test: `DnaString.from("ACGU")` returns `Left(SequenceError.InvalidCharacter('U'))` — verifies U is rejected
- [x] 4.3 Run `sbt test` — confirm all tests pass

## 5. Rename NucleotideCounts → DnaNucleotideCounts (TDD)

- [x] 5.1 Write failing tests for `DnaNucleotideCounts`: same scenarios as existing `NucleotideCountsSpec` but with the new name
- [x] 5.2 Run `sbt test` — confirm new tests fail (Red)
- [x] 5.3 Create `DnaNucleotideCounts.scala` (copy and rename from `NucleotideCounts`)
- [x] 5.4 Delete `NucleotideCounts.scala`
- [x] 5.5 Update `DnaNucleotides.count` return type to `DnaNucleotideCounts`
- [x] 5.6 Update `DnaNucleotidesSpec` and `NucleotideCountsSpec` to reference `DnaNucleotideCounts`; rename `NucleotideCountsSpec` → `DnaNucleotideCountsSpec`
- [x] 5.7 Run `sbt test` — confirm all tests pass (Green)

## 6. Update DnaNucleotides.count to dispatch on DnaNucleotide (TDD)

- [x] 6.1 Rewrite `DnaNucleotides.count` to lift each `Char` via `DnaNucleotide.fromChar` and pattern-match on the ADT value instead of raw `Char` literals
- [x] 6.2 Run `sbt test` — confirm all existing `DnaNucleotidesSpec` tests pass (no behaviour change)

## 7. RnaString (TDD)

- [x] 7.1 Write failing tests for `RnaString.from`: valid RNA string accepted, T rejected, empty string accepted, invalid character rejected, >1000 chars rejected, exactly 1000 chars accepted
- [x] 7.2 Run `sbt test` — confirm tests fail (Red)
- [x] 7.3 Implement `RnaString` mirroring `DnaString`, deriving `ValidChars` from `RnaNucleotide.validChars`
- [x] 7.4 Run `sbt test` — confirm `RnaString` tests pass (Green)

## 8. RnaNucleotideCounts (TDD)

- [x] 8.1 Write failing tests for `RnaNucleotideCounts`: fields `a`, `c`, `g`, `u` exist, `format` returns `"a c g u"` space-separated, all-zero formats as `"0 0 0 0"`
- [x] 8.2 Run `sbt test` — confirm tests fail (Red)
- [x] 8.3 Implement `RnaNucleotideCounts(a, c, g, u)` with `format: String`
- [x] 8.4 Run `sbt test` — confirm `RnaNucleotideCounts` tests pass (Green)

## 9. Final Verification

- [x] 9.1 Run `sbt compile` — zero errors, zero warnings
- [x] 9.2 Run `sbt test` — all tests pass (count should be higher than the original 15)
- [x] 9.3 Review all source files: no `DnaError` references remain, no `NucleotideCounts` references remain, no raw `Char` literals in `DnaNucleotides.count`
