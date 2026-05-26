## ADDED Requirements

### Requirement: HammingError is a sealed ADT of Hamming-distance failures
The system SHALL provide a `sealed trait HammingError` with case `final case class LengthMismatch(lengthA: Int, lengthB: Int)`. The type SHALL reside in the `bio.domain.analysis` package.

#### Scenario: LengthMismatch carries both offending lengths
- **WHEN** `HammingError.LengthMismatch(5, 7)` is constructed
- **THEN** the value's `lengthA` is `5` and `lengthB` is `7`

### Requirement: HammingDistance.between counts differing positions between two equal-length DnaStrings
The system SHALL provide `HammingDistance.between(a: DnaString, b: DnaString): Either[HammingError, Int]` returning the number of positions at which `a` and `b` differ when their lengths are equal. The algorithm SHALL reside in the `bio.algorithms.analysis` package. The result SHALL be non-negative and at most `a.value.length`.

#### Scenario: Rosalind sample produces 7
- **WHEN** `HammingDistance.between(a = <DnaString of "GAGCCTACTAACGGGAT">, b = <DnaString of "CATCGTAATGACGGCCT">)` is called
- **THEN** the result is `Right(7)`

#### Scenario: Identical strings have distance 0
- **WHEN** `HammingDistance.between(<DnaString of "ACGT">, <DnaString of "ACGT">)` is called
- **THEN** the result is `Right(0)`

#### Scenario: Two empty DnaStrings have distance 0
- **WHEN** `HammingDistance.between(<empty DnaString>, <empty DnaString>)` is called
- **THEN** the result is `Right(0)`

#### Scenario: All-different equal-length sequences have distance equal to length
- **WHEN** `HammingDistance.between(<DnaString of "AAAA">, <DnaString of "TTTT">)` is called
- **THEN** the result is `Right(4)`

#### Scenario: A single differing position yields distance 1
- **WHEN** `HammingDistance.between(<DnaString of "ACGT">, <DnaString of "ACGA">)` is called
- **THEN** the result is `Right(1)`

#### Scenario: Hamming distance is symmetric
- **WHEN** `HammingDistance.between(a, b)` and `HammingDistance.between(b, a)` are both computed for any pair of equal-length DnaStrings
- **THEN** both calls return the same `Right(Int)` value

### Requirement: HammingDistance.between rejects length-mismatched inputs
The system SHALL return `Left(HammingError.LengthMismatch(lengthA, lengthB))` whenever `a.value.length != b.value.length`. The error SHALL carry the lengths of the two inputs in the order they were passed.

#### Scenario: Different non-empty lengths are rejected
- **WHEN** `HammingDistance.between(<DnaString of "ACGT">, <DnaString of "ACGTAA">)` is called
- **THEN** the result is `Left(HammingError.LengthMismatch(4, 6))`

#### Scenario: Empty + non-empty is rejected
- **WHEN** `HammingDistance.between(<empty DnaString>, <DnaString of "AC">)` is called
- **THEN** the result is `Left(HammingError.LengthMismatch(0, 2))`

#### Scenario: Non-empty + empty is rejected (lengths preserved in order)
- **WHEN** `HammingDistance.between(<DnaString of "AC">, <empty DnaString>)` is called
- **THEN** the result is `Left(HammingError.LengthMismatch(2, 0))`
