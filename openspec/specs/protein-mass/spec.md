# Protein Mass

## Purpose

Compute the total monoisotopic mass of a protein (Rosalind PRTM): an `AminoAcid.fromChar` lookup lifts single-letter codes to residues, a validated `ProteinMassProblem` wraps the protein string, a pure `ProteinMass.calculate` function sums residue masses, `ProteinMass.format` renders the result, and the `PRTMProb` runner reads, solves, and prints the dataset answer.

## Requirements

### Requirement: Lift a character to its amino acid

The system SHALL provide a companion `AminoAcid.fromChar(c: Char): Option[AminoAcid]`
lookup that returns the residue for a valid single-letter code and `None` otherwise. The
lookup SHALL be derived from the canonical residue list, and this extension SHALL be
additive and SHALL NOT change any existing amino-acid behaviour.

#### Scenario: Lifts a valid single-letter code

- **WHEN** `AminoAcid.fromChar` is called with `'W'`
- **THEN** it returns `Some(AminoAcid.W)`

#### Scenario: Rejects a non-amino-acid character

- **WHEN** `AminoAcid.fromChar` is called with `'B'`
- **THEN** it returns `None`

### Requirement: Validated protein-mass problem

The system SHALL provide a `ProteinMassProblem` domain type that wraps a `ProteinString`
and is constructible only through a smart constructor returning
`Either[ProteinMassProblemError, ProteinMassProblem]`. Validation SHALL reject a protein
longer than 1000 aa. Empty proteins SHALL be accepted. The type SHALL NOT expose a public
`apply` or `copy` that bypasses validation.

#### Scenario: Accepts the canonical sample protein

- **WHEN** `ProteinMassProblem.from` is called with the protein `SKADYEK`
- **THEN** it returns a `Right` whose `protein` preserves the input

#### Scenario: Accepts an empty protein

- **WHEN** `ProteinMassProblem.from` is called with an empty protein
- **THEN** it returns a `Right`

#### Scenario: Accepts a protein at the 1000-aa upper bound

- **WHEN** `ProteinMassProblem.from` is called with a 1000-aa protein
- **THEN** it returns a `Right`

#### Scenario: Rejects a protein longer than 1000 aa

- **WHEN** `ProteinMassProblem.from` is called with a 1001-aa protein
- **THEN** it returns `Left(ProteinTooLong(1001, 1000))`

#### Scenario: Does not expose a public apply or copy

- **WHEN** code attempts to call `ProteinMassProblem(...)` or `.copy(...)` directly
- **THEN** the code fails to compile

### Requirement: Calculate the total monoisotopic mass

The system SHALL provide a pure, total `ProteinMass.calculate` function that takes a
`ProteinMassProblem` and returns a `ProteinMass` carrying the sum of the monoisotopic
masses of the protein's residues. The empty protein SHALL have total mass `0`. The
function SHALL perform no I/O.

#### Scenario: Computes the canonical sample mass

- **WHEN** `calculate` is called with the protein `SKADYEK`
- **THEN** the returned `mass` is within `1e-3` of `821.39192`

#### Scenario: A single residue has its own monoisotopic mass

- **WHEN** `calculate` is called with the protein `W`
- **THEN** the returned `mass` is within `1e-5` of `186.07931`

#### Scenario: The empty protein has zero mass

- **WHEN** `calculate` is called with an empty protein
- **THEN** the returned `mass` is `0`

### Requirement: Render the protein mass

The system SHALL provide a `ProteinMass.format` method that renders the total mass to
three decimal places.

#### Scenario: Formats the mass to three decimals

- **WHEN** `format` is called on a `ProteinMass` whose mass is `821.39192`
- **THEN** it returns exactly `821.392`

### Requirement: Read and solve the PRTM dataset

The system SHALL provide a `PRTMProb` runner that reads a protein string from
`prtm_data.txt`, validates it into a `ProteinMassProblem`, computes the total monoisotopic
mass, and prints the formatted result through the `IO` monad. Invalid input SHALL produce
a printed error message rather than an exception.

#### Scenario: Prints the mass for the dataset

- **WHEN** `PRTMProb.solve()` runs against the canonical dataset
- **THEN** it prints `821.392`

#### Scenario: Prints an error for invalid input

- **WHEN** `PRTMProb.solve()` runs against a dataset whose protein contains an invalid character
- **THEN** it prints a descriptive error message and does not throw
