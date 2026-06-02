## ADDED Requirements

### Requirement: Validated mass multiset

The system SHALL provide a `MassMultiset` domain type that wraps a `Vector[Double]`
of masses and is constructible only through a smart constructor returning
`Either[MassMultisetError, MassMultiset]`. Validation SHALL reject an empty
multiset, a multiset of more than 200 masses, and any non-positive mass, reporting
the first failure in that order. The type SHALL NOT expose a public `apply` or
`copy` that bypasses validation.

#### Scenario: Accepts the canonical sample multiset

- **WHEN** `MassMultiset.from` is called with the nine sample S₁ masses
- **THEN** it returns a `Right` whose `masses` preserve the input order and repeats

#### Scenario: Preserves repeated masses

- **WHEN** `MassMultiset.from` is called with a vector containing `968.35544` twice
- **THEN** the resulting multiset retains both occurrences

#### Scenario: Rejects an empty multiset

- **WHEN** `MassMultiset.from` is called with an empty vector
- **THEN** it returns `Left(EmptyMultiset)`

#### Scenario: Rejects a multiset larger than 200 masses

- **WHEN** `MassMultiset.from` is called with 201 masses
- **THEN** it returns `Left(TooManyMasses(201, 200))`

#### Scenario: Rejects a non-positive mass

- **WHEN** `MassMultiset.from` is called with a vector whose third element is `0.0`
- **THEN** it returns `Left(NonPositiveMass(2, 0.0))`

#### Scenario: Reports the first failure only

- **WHEN** `MassMultiset.from` is called with 201 masses that also contain a negative value
- **THEN** it returns `Left(TooManyMasses(201, 200))` rather than a non-positive error

#### Scenario: Does not expose a public apply or copy

- **WHEN** code attempts to call `MassMultiset(...)` or `.copy(...)` directly
- **THEN** the code fails to compile

### Requirement: Spectral convolution problem

The system SHALL provide a `SpectralConvolutionProblem` domain type that bundles two
`MassMultiset` values — the minuend `s1` and the subtrahend `s2` — as the input to
the convolution algorithm.

#### Scenario: Bundles the two input multisets

- **WHEN** a `SpectralConvolutionProblem` is built from S₁ and S₂
- **THEN** its `s1` and `s2` expose the respective multisets unchanged

### Requirement: Compute the spectral convolution

The system SHALL provide a pure, total `SpectralConvolution.convolve` function that
takes a `SpectralConvolutionProblem` and returns a `SpectralConvolution` carrying the
largest multiplicity of the convolution `S₁⊖S₂` and a shift value that achieves it.
The convolution SHALL be the multiset of all differences `s₁ − s₂`; equal shifts
SHALL be matched after rounding to five decimal places. The function SHALL perform
no I/O.

#### Scenario: Computes the canonical sample multiplicity and shift

- **WHEN** `convolve` is called with the canonical sample S₁ and S₂
- **THEN** the returned `multiplicity` is `3` and the returned `shift` has absolute value within `1e-5` of `85.03163`

#### Scenario: Counts repeated differences across the full product

- **WHEN** `convolve` is called with `S₁ = [10.0, 20.0]` and `S₂ = [5.0]`
- **THEN** the returned `multiplicity` is `1`

#### Scenario: Finds the most common shift for a clear majority

- **WHEN** `convolve` is called with `S₁ = [10.0, 20.0, 30.0]` and `S₂ = [0.0001, 10.0001, 20.0001]` where every shift of `9.9999` recurs
- **THEN** the returned `multiplicity` is `3` and the returned `shift` has absolute value within `1e-5` of `9.9999`

#### Scenario: Tolerates floating-point error within five decimals

- **WHEN** two differences are equal to five decimals but differ by `1e-11`
- **THEN** they are counted in the same multiplicity bucket

### Requirement: Render the spectral convolution result

The system SHALL provide a `SpectralConvolution.format` method that renders the
largest multiplicity on the first line and the absolute value of the maximizing
shift, to five decimal places, on the second line.

#### Scenario: Formats the canonical sample result

- **WHEN** `format` is called on a `SpectralConvolution` with multiplicity `3` and shift `85.03163`
- **THEN** it returns exactly `3\n85.03163`

#### Scenario: Renders the absolute value of a negative shift

- **WHEN** `format` is called on a `SpectralConvolution` whose shift is `-85.03163`
- **THEN** the second line is `85.03163`

### Requirement: Read and solve the CONV dataset

The system SHALL provide a `CONVProb` runner that reads two whitespace-separated
lines of masses from `conv_data.txt`, validates them into two `MassMultiset`s,
computes the spectral convolution, and prints the formatted result through the `IO`
monad. Invalid or missing input SHALL produce a printed error message rather than an
exception.

#### Scenario: Prints the multiplicity and shift for the dataset

- **WHEN** `CONVProb.solve()` runs against the canonical dataset
- **THEN** it prints `3` then `85.03163`

#### Scenario: Prints an error for malformed input

- **WHEN** `CONVProb.solve()` runs against a dataset whose first line contains a non-numeric token
- **THEN** it prints a descriptive error message and does not throw
