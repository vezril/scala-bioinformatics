## Purpose

Validates a list of prefix weights and reconstructs a protein string of length `n − 1` whose prefix spectrum matches the input, by mapping each consecutive weight difference to the closest amino acid in the monoisotopic mass table. Includes the monoisotopic-mass extension to the amino-acid alphabet and an IO runner over the SPEC dataset.

## Requirements

### Requirement: Monoisotopic masses on the amino-acid alphabet

The system SHALL expose a `monoisotopicMass` property on each of the 20 `AminoAcid`
residues and a companion `closestByMass(target: Double): AminoAcid` lookup that returns
the residue whose monoisotopic mass is nearest to `target`. Ties SHALL be broken
deterministically by the alphabet's canonical order. This extension SHALL be additive and
SHALL NOT change any existing amino-acid behaviour.

#### Scenario: Reports the canonical monoisotopic mass of a residue

- **WHEN** the `monoisotopicMass` of `AminoAcid.W` is read
- **THEN** it is `186.07931`

#### Scenario: Matches a difference to the nearest residue

- **WHEN** `AminoAcid.closestByMass` is called with `128.0586`
- **THEN** it returns `AminoAcid.Q`, not `AminoAcid.K`

#### Scenario: Breaks an isobaric tie deterministically

- **WHEN** `AminoAcid.closestByMass` is called with `113.08406` (the mass shared by `I` and `L`)
- **THEN** it returns `AminoAcid.L`, the residue appearing first in the canonical order

### Requirement: Validated prefix-spectrum problem

The system SHALL provide a `PrefixSpectrum` domain type that wraps a list of prefix
weights as a `Vector[Double]` and is constructible only through a smart constructor
returning `Either[PrefixSpectrumError, PrefixSpectrum]`. Validation SHALL be
first-failure-wins in this order: empty list, then more than 100 weights, then the first
non-positive weight. The type SHALL NOT expose a public `apply` or `copy` that bypasses
validation.

#### Scenario: Accepts the canonical sample weights

- **WHEN** `PrefixSpectrum.from` is called with `[3524.8542, 3710.9335, 3841.974, 3970.0326, 4057.0646]`
- **THEN** it returns a `Right` whose `weights` preserve the five input values in order

#### Scenario: Accepts a single-weight spectrum

- **WHEN** `PrefixSpectrum.from` is called with a single positive weight
- **THEN** it returns a `Right`

#### Scenario: Rejects an empty spectrum

- **WHEN** `PrefixSpectrum.from` is called with an empty list
- **THEN** it returns `Left(EmptySpectrum)`

#### Scenario: Rejects a spectrum with more than 100 weights

- **WHEN** `PrefixSpectrum.from` is called with 101 positive weights
- **THEN** it returns `Left(TooManyWeights(101, 100))`

#### Scenario: Rejects a non-positive weight

- **WHEN** `PrefixSpectrum.from` is called with `[10.0, 0.0, 20.0]`
- **THEN** it returns `Left(NonPositiveWeight(1, 0.0))`

#### Scenario: Reports the first failure when multiple rules fail

- **WHEN** `PrefixSpectrum.from` is called with an empty list while other rules would also fail
- **THEN** it returns `Left(EmptySpectrum)` ahead of the other checks

#### Scenario: Does not expose a public apply or copy

- **WHEN** code attempts to call `PrefixSpectrum(...)` or `.copy(...)` directly
- **THEN** the code fails to compile

### Requirement: Infer the protein from its prefix spectrum

The system SHALL provide a pure, total `InferProteinFromSpectrum.infer` function that
takes a `PrefixSpectrum` and returns an `InferredProtein` carrying a protein string of
length `n − 1`, where `n` is the number of weights. Each consecutive difference of the
prefix weights SHALL be mapped to the amino acid whose monoisotopic mass is nearest to
that difference. The function SHALL perform no I/O.

#### Scenario: Reconstructs the canonical sample protein

- **WHEN** `infer` is called with `[3524.8542, 3710.9335, 3841.974, 3970.0326, 4057.0646]`
- **THEN** the inferred protein is `WMQS`

#### Scenario: Produces a protein of length n minus one

- **WHEN** `infer` is called with a spectrum of `n` weights
- **THEN** the inferred protein has length `n − 1`

#### Scenario: A single-weight spectrum yields the empty protein

- **WHEN** `infer` is called with a single-weight spectrum
- **THEN** the inferred protein is the empty string

### Requirement: Render the inferred protein

The system SHALL provide an `InferredProtein.format` method that renders the inferred
protein as its single-letter amino-acid codes on one line.

#### Scenario: Formats the protein letters

- **WHEN** `format` is called on an `InferredProtein` whose protein is `WMQS`
- **THEN** it returns exactly `WMQS`

### Requirement: Read and solve the SPEC dataset

The system SHALL provide a `SPECProb` runner that reads newline-separated prefix weights
from `spec_data.txt`, validates them into a `PrefixSpectrum`, infers the protein, and
prints the result through the `IO` monad. Invalid or unparseable input SHALL produce a
printed error message rather than an exception.

#### Scenario: Prints the inferred protein for the dataset

- **WHEN** `SPECProb.solve()` runs against the canonical dataset
- **THEN** it prints `WMQS`

#### Scenario: Prints an error for unparseable input

- **WHEN** `SPECProb.solve()` runs against a dataset containing a line that is not a number
- **THEN** it prints a descriptive error message and does not throw
