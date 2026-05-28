## ADDED Requirements

### Requirement: Validated FailureArrayProblem input bundle

The system SHALL provide a validated domain type `bio.domain.analysis.FailureArrayProblem` constructed only through a smart constructor `FailureArrayProblem.from(dna: DnaString): Either[FailureArrayProblemError, FailureArrayProblem]`. The smart constructor MUST reject an empty DNA string with `FailureArrayProblemError.EmptySequence`. The case class MUST be `sealed abstract` so the synthesised `apply` and `copy` cannot leak around the smart constructor.

#### Scenario: Accepts a non-empty DNA string
- **WHEN** `FailureArrayProblem.from` is called with `DnaString.from("CAGCATGGTATCACAGCAGAG").toOption.get`
- **THEN** it returns `Right(problem)` where `problem.dna.value == "CAGCATGGTATCACAGCAGAG"`

#### Scenario: Accepts a single-character DNA string
- **WHEN** `FailureArrayProblem.from` is called with `DnaString.from("A").toOption.get`
- **THEN** it returns `Right(problem)` where `problem.dna.value == "A"`

#### Scenario: Rejects an empty DNA string as EmptySequence
- **WHEN** `FailureArrayProblem.from` is called with `DnaString.from("").toOption.get`
- **THEN** it returns `Left(FailureArrayProblemError.EmptySequence)`

### Requirement: FailureArray.compute returns the KMP failure array

The system SHALL provide an algorithm object `bio.algorithms.analysis.FailureArray` with a method `compute(problem: FailureArrayProblem): Vector[Int]` that returns the Knuth-Morris-Pratt failure array of `problem.dna.value`. The returned `Vector` MUST have length equal to `problem.dna.value.length`, MUST be 0-indexed (so `result(i)` corresponds to Rosalind's 1-indexed `P[i+1]`), and MUST satisfy `result(0) == 0` by convention. For every other index `i`, `result(i)` MUST equal the length of the longest *proper* prefix of `s[0..i]` (inclusive) that is also a suffix of `s[0..i]`. The implementation MUST run in `O(n)` time using the classic two-pointer KMP table-build recurrence.

#### Scenario: Canonical Rosalind KMP sample
- **WHEN** `FailureArray.compute` is called with a problem wrapping `DnaString.from("CAGCATGGTATCACAGCAGAG").toOption.get`
- **THEN** it returns `Vector(0, 0, 0, 1, 2, 0, 0, 0, 0, 0, 0, 1, 2, 1, 2, 3, 4, 5, 3, 0, 0)`

#### Scenario: Single-character input returns a length-1 zero vector
- **WHEN** `FailureArray.compute` is called with a problem wrapping `DnaString.from("A").toOption.get`
- **THEN** it returns `Vector(0)`

#### Scenario: All-same-character input produces a strictly increasing array
- **WHEN** `FailureArray.compute` is called with a problem wrapping `DnaString.from("AAAAA").toOption.get`
- **THEN** it returns `Vector(0, 1, 2, 3, 4)`

#### Scenario: No-self-overlap input produces an all-zero array
- **WHEN** `FailureArray.compute` is called with a problem wrapping `DnaString.from("ACGT").toOption.get`
- **THEN** it returns `Vector(0, 0, 0, 0)`

#### Scenario: Periodic ACAC input produces a rising staircase
- **WHEN** `FailureArray.compute` is called with a problem wrapping `DnaString.from("ACACACAC").toOption.get`
- **THEN** it returns `Vector(0, 0, 1, 2, 3, 4, 5, 6)`

#### Scenario: Two-character matching pair AA
- **WHEN** `FailureArray.compute` is called with a problem wrapping `DnaString.from("AA").toOption.get`
- **THEN** it returns `Vector(0, 1)`

#### Scenario: Two-character non-matching pair AT
- **WHEN** `FailureArray.compute` is called with a problem wrapping `DnaString.from("AT").toOption.get`
- **THEN** it returns `Vector(0, 0)`
