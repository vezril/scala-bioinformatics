## ADDED Requirements

### Requirement: Spectrum-graph problem validation

The system SHALL provide a validated `SpectrumGraphProblem` domain type wrapping the mass list `L` as a `Vector[Double]` of size at most 100, all values positive. It MUST be constructed only through a smart constructor `from(masses)` returning `Either[SpectrumGraphProblemError, SpectrumGraphProblem]`, applying first-failure-wins validation, and MUST NOT expose a public `apply` or `copy` that could bypass validation.

#### Scenario: Accepts a valid mass list

- **WHEN** `SpectrumGraphProblem.from` is called with the 9-value canonical sample
- **THEN** it returns a `Right` whose `masses` match the input

#### Scenario: Rejects more than 100 masses

- **WHEN** `SpectrumGraphProblem.from` is called with 101 positive masses
- **THEN** it returns `Left(TooManyMasses(101, 100))`

#### Scenario: Rejects a non-positive mass

- **WHEN** `SpectrumGraphProblem.from` is called with the list `[10.0, -1.0]`
- **THEN** it returns `Left(NonPositiveMass(1, -1.0))`

#### Scenario: Cannot be constructed via a public apply or copy

- **WHEN** code attempts `SpectrumGraphProblem(...)` or `.copy(...)` on a constructed instance
- **THEN** the code fails to compile

### Requirement: Spectrum-graph peptide result rendering

The system SHALL provide a `SpectrumGraphPeptide` result type holding the longest matching protein and exposing a `format: String` that returns it verbatim.

#### Scenario: Exposes the peptide

- **WHEN** a `SpectrumGraphPeptide` result is constructed with `WMSPG`
- **THEN** its `peptide` field returns `WMSPG`

#### Scenario: Formats the peptide verbatim

- **WHEN** `format` is called on a result holding `WMSPG`
- **THEN** it returns `"WMSPG"`

### Requirement: Longest spectrum-graph peptide computation

The system SHALL provide an algorithm that, given a `SpectrumGraphProblem`, returns the longest protein matching the spectrum graph of `L` — the edge-label string of a longest path in the DAG whose nodes are the masses and whose edges `u→v` exist when `v − u` equals an amino-acid residue mass.

#### Scenario: Computes the canonical Rosalind SGRA sample

- **WHEN** the algorithm is run on `3524.8542 3623.5245 3710.9335 3841.974 3929.00603 3970.0326 4026.05879 4057.0646 4083.08025`
- **THEN** it returns the peptide `WMSPG`

#### Scenario: Infers a single residue from one edge

- **WHEN** the algorithm is run on the list `[10.0, 81.03711]` (a gap of one alanine residue)
- **THEN** it returns the peptide `A`

#### Scenario: Returns the empty protein when no gap is a residue mass

- **WHEN** the algorithm is run on the list `[10.0, 20.0]`
- **THEN** it returns the empty peptide
