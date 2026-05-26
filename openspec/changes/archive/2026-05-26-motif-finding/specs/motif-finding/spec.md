## ADDED Requirements

### Requirement: MotifLocations.find returns 1-based positions of motif occurrences in text
The system SHALL provide `MotifLocations.find(text: DnaString, motif: DnaString): Vector[Int]` returning every 1-based starting position where `motif` occurs as a contiguous substring of `text`. Results SHALL be in ascending order. Overlapping matches SHALL be included. The function SHALL be total — every pair of `DnaString`s produces a defined `Vector[Int]` (no exceptions, no `Either`). The algorithm SHALL reside in the `bio.algorithms.analysis` package.

#### Scenario: Rosalind sample produces 2 4 10
- **WHEN** `MotifLocations.find(text = <DnaString of "GATATATGCATATACTT">, motif = <DnaString of "ATAT">)` is called
- **THEN** the result is `Vector(2, 4, 10)`

#### Scenario: A motif that matches at the start of text is included at position 1
- **WHEN** `MotifLocations.find(text = <DnaString of "ACGTACGT">, motif = <DnaString of "AC">)` is called
- **THEN** the result begins with `1` (the first occurrence is at position 1)

#### Scenario: A motif that matches at the end of text is included at the last possible position
- **WHEN** `MotifLocations.find(text = <DnaString of "ACGT">, motif = <DnaString of "GT">)` is called
- **THEN** the result is `Vector(3)`

#### Scenario: All occurrences are returned including overlapping ones
- **WHEN** `MotifLocations.find(text = <DnaString of "AAAA">, motif = <DnaString of "AA">)` is called
- **THEN** the result is `Vector(1, 2, 3)` — three overlapping occurrences

#### Scenario: text equal to motif returns a single match at position 1
- **WHEN** `MotifLocations.find(text = <DnaString of "ACGT">, motif = <DnaString of "ACGT">)` is called
- **THEN** the result is `Vector(1)`

### Requirement: MotifLocations.find returns Vector.empty when no matches exist
The system SHALL return `Vector.empty` whenever `motif` does not occur in `text` (including the degenerate cases described below).

#### Scenario: A motif with no occurrences returns Vector.empty
- **WHEN** `MotifLocations.find(text = <DnaString of "AAAA">, motif = <DnaString of "GG">)` is called
- **THEN** the result is `Vector.empty`

#### Scenario: A motif longer than text returns Vector.empty
- **WHEN** `MotifLocations.find(text = <DnaString of "AC">, motif = <DnaString of "ACGT">)` is called
- **THEN** the result is `Vector.empty`

#### Scenario: Empty text returns Vector.empty
- **WHEN** `MotifLocations.find(text = <empty DnaString>, motif = <DnaString of "AC">)` is called
- **THEN** the result is `Vector.empty`

### Requirement: MotifLocations.find returns Vector.empty for an empty motif by convention
The system SHALL return `Vector.empty` when `motif` is an empty `DnaString`. This is a convention call — every position in `text` is trivially a starting point of the empty string, but the biological question "where does this motif occur?" has no meaningful answer for an empty motif. The function chooses the empty result.

#### Scenario: Empty motif returns Vector.empty
- **WHEN** `MotifLocations.find(text = <DnaString of "ACGT">, motif = <empty DnaString>)` is called
- **THEN** the result is `Vector.empty`

#### Scenario: Empty motif and empty text returns Vector.empty
- **WHEN** `MotifLocations.find(text = <empty DnaString>, motif = <empty DnaString>)` is called
- **THEN** the result is `Vector.empty`
