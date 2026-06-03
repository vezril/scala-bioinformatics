# Inferring Peptide from Full Spectrum

## Purpose

Reconstruct a length-n peptide from a list `L` of `2n+3` masses — the parent mass plus the b-ion and y-ion masses — by walking the prefix-ion series. Because consecutive prefix masses differ by exactly one residue mass, the peptide can be recovered residue by residue (Rosalind FULL).

## Requirements

### Requirement: Full-spectrum problem validation

The system SHALL provide a validated `FullSpectrumProblem` domain type wrapping the mass list `L` as a `Vector[Double]`, whose size must equal `2n+3` for some integer `n ≥ 1` (i.e. odd and at least 5) and whose values must all be positive. It MUST be constructed only through a smart constructor `from(masses)` returning `Either[FullSpectrumProblemError, FullSpectrumProblem]`, applying first-failure-wins validation, and MUST NOT expose a public `apply` or `copy` that could bypass validation.

#### Scenario: Accepts a valid mass list

- **WHEN** `FullSpectrumProblem.from` is called with the 13-value canonical FULL sample
- **THEN** it returns a `Right` whose `masses` match the input

#### Scenario: Rejects a list whose size is not 2n+3

- **WHEN** `FullSpectrumProblem.from` is called with a list of size 4
- **THEN** it returns `Left(InvalidSize(4))`

#### Scenario: Rejects a list smaller than the minimum

- **WHEN** `FullSpectrumProblem.from` is called with a list of size 3
- **THEN** it returns `Left(InvalidSize(3))`

#### Scenario: Rejects a non-positive mass

- **WHEN** `FullSpectrumProblem.from` is called with the list `[5.0, -1.0, 2.0, 3.0, 4.0]`
- **THEN** it returns `Left(NonPositiveMass(1, -1.0))`

#### Scenario: Cannot be constructed via a public apply or copy

- **WHEN** code attempts `FullSpectrumProblem(...)` or `.copy(...)` on a constructed instance
- **THEN** the code fails to compile

### Requirement: Inferred peptide result rendering

The system SHALL provide an `InferredPeptide` result type holding the reconstructed peptide string and exposing a `format: String` that returns it verbatim.

#### Scenario: Exposes the peptide

- **WHEN** an `InferredPeptide` result is constructed with `KEKEP`
- **THEN** its `peptide` field returns `KEKEP`

#### Scenario: Formats the peptide verbatim

- **WHEN** `format` is called on a result holding `KEKEP`
- **THEN** it returns `"KEKEP"`

### Requirement: Peptide inference from a full spectrum

The system SHALL provide an algorithm that, given a `FullSpectrumProblem`, returns a peptide of length `n`. After discarding the parent mass and sorting the `2n+2` ions, the algorithm walks the prefix series: starting from the smallest ion, it repeatedly takes the first larger ion whose gap from the current prefix equals an amino-acid residue mass (within tolerance), emitting that residue, for `n` steps.

#### Scenario: Reconstructs the canonical Rosalind FULL sample

- **WHEN** the algorithm is run on the canonical FULL sample (parent `1988.21104821` and its 12 b-ion/y-ion masses)
- **THEN** it returns the peptide `KEKEP`

#### Scenario: Reconstructs a single-residue peptide

- **WHEN** the algorithm is run on the list `[90.0, 1.0, 72.03711, 1.0, 72.03711]`
- **THEN** it returns the peptide `A`
