## ADDED Requirements

### Requirement: Validated SplicedMotifProblem input bundle

The system SHALL provide a validated domain type `bio.domain.analysis.SplicedMotifProblem` constructed only through a smart constructor `SplicedMotifProblem.from(source: DnaString, target: DnaString): Either[SplicedMotifProblemError, SplicedMotifProblem]`. The smart constructor MUST enforce, in this order, first-failure-wins:

1. `source.value.length <= 1000`, else `SourceTooLong(length, max)`.
2. `target.value.length <= 1000`, else `TargetTooLong(length, max)`.

Empty `source` and/or empty `target` MUST be accepted. The constructed value MUST expose `source: DnaString` and `target: DnaString`. The case class MUST be `sealed abstract` so the synthesised `apply` and `copy` cannot leak around the smart constructor.

#### Scenario: Accepts the canonical Rosalind SSEQ sample
- **WHEN** `SplicedMotifProblem.from` is called with source `ACGTACGTGACG` and target `GTA`
- **THEN** it returns `Right(problem)` where `problem.source.value == "ACGTACGTGACG"` and `problem.target.value == "GTA"`

#### Scenario: Accepts an empty source and empty target
- **WHEN** `SplicedMotifProblem.from` is called with two empty `DnaString`s
- **THEN** it returns `Right(problem)` where both fields are empty

#### Scenario: Accepts a non-empty source with empty target
- **WHEN** `SplicedMotifProblem.from` is called with source `ACGT` and empty target
- **THEN** it returns `Right(problem)`

#### Scenario: Accepts both strings at the 1000-character upper bound
- **WHEN** `SplicedMotifProblem.from` is called with `"A" * 1000` and `"A" * 1000`
- **THEN** it returns `Right(problem)`

#### Scenario: Rejects a 1001-character source as SourceTooLong(1001, 1000)
- **WHEN** `SplicedMotifProblem.from` is called with a 1001-char source and a short target
- **THEN** it returns `Left(SplicedMotifProblemError.SourceTooLong(1001, 1000))`

#### Scenario: Rejects a 1001-character target as TargetTooLong(1001, 1000)
- **WHEN** `SplicedMotifProblem.from` is called with a short source and a 1001-char target
- **THEN** it returns `Left(SplicedMotifProblemError.TargetTooLong(1001, 1000))`

#### Scenario: Companion apply does not leak
- **WHEN** code attempts to construct via `SplicedMotifProblem(s, t)`
- **THEN** compilation fails (the synthesised apply is hidden behind the smart constructor)

#### Scenario: copy does not leak
- **WHEN** code attempts to mutate via `problem.copy(target = other)`
- **THEN** compilation fails (the synthesised copy is hidden behind the smart constructor)

### Requirement: SplicedMotif.find returns the leftmost-greedy 1-indexed subsequence positions

The system SHALL provide an algorithm object `bio.algorithms.analysis.SplicedMotif` with a method `find(problem: SplicedMotifProblem): Option[Vector[Int]]` that returns the leftmost-greedy 1-indexed positions of `problem.target`'s symbols inside `problem.source` such that the symbols appear in order (a *subsequence* match — not necessarily contiguous), or `None` when no such subsequence exists.

The implementation MUST use a single-pass two-pointer walk: maintain `i` over `source` and `j` over `target`; on `source(i) == target(j)`, record `i + 1` and advance both; otherwise advance only `i`. After the loop, return `Some(indices)` iff `j == target.length`; otherwise `None`. When `target` is empty the result MUST be `Some(Vector.empty)`. The output positions MUST be strictly increasing.

#### Scenario: Canonical Rosalind SSEQ sample (greedy answer)
- **WHEN** `SplicedMotif.find` is called with source `ACGTACGTGACG` and target `GTA`
- **THEN** it returns `Some(Vector(3, 4, 5))` (the leftmost-greedy match; the published Rosalind answer `3 8 10` is also valid but our deterministic convention is leftmost)

#### Scenario: Empty target returns Some(Vector.empty)
- **WHEN** `SplicedMotif.find` is called with source `ACGT` and empty target
- **THEN** it returns `Some(Vector.empty)`

#### Scenario: Source equals target returns sequential 1-indexed positions
- **WHEN** `SplicedMotif.find` is called with source `ACGT` and target `ACGT`
- **THEN** it returns `Some(Vector(1, 2, 3, 4))`

#### Scenario: Target not a subsequence of source returns None
- **WHEN** `SplicedMotif.find` is called with source `AAA` and target `AAAA`
- **THEN** it returns `None`

#### Scenario: Target requires the last character of source
- **WHEN** `SplicedMotif.find` is called with source `ACGT` and target `T`
- **THEN** it returns `Some(Vector(4))`

#### Scenario: Empty source with empty target returns Some(Vector.empty)
- **WHEN** `SplicedMotif.find` is called with empty source and empty target
- **THEN** it returns `Some(Vector.empty)`

#### Scenario: Empty source with non-empty target returns None
- **WHEN** `SplicedMotif.find` is called with empty source and target `A`
- **THEN** it returns `None`

#### Scenario: Repeated target character finds successive occurrences
- **WHEN** `SplicedMotif.find` is called with source `AACGAACG` and target `AAAA`
- **THEN** it returns `Some(Vector(1, 2, 5, 6))`
