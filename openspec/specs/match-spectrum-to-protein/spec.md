# Matching a Spectrum to a Protein

## Purpose

Given a set of candidate proteins and a target complete spectrum `R`, this capability finds the candidate maximising the multiplicity of `R⊖S[s_k]` — where `S[s]` is the multiset of all prefix and suffix weights of a protein `s` — and returns that maximum multiplicity together with the achieving protein (Rosalind PRSM).

## Requirements

### Requirement: Spectrum-match problem validation

The system SHALL provide a validated `SpectrumMatchProblem` domain type wrapping the candidate proteins (`Vector[ProteinString]`) and the target spectrum `R` (`Vector[Double]` of positive numbers). It MUST be constructed only through a smart constructor `from(proteins, spectrum)` returning `Either[SpectrumMatchProblemError, SpectrumMatchProblem]`, applying first-failure-wins validation, and MUST NOT expose a public `apply` or `copy` that could bypass validation.

#### Scenario: Accepts a valid set of candidates and spectrum

- **WHEN** `SpectrumMatchProblem.from` is called with the proteins `GSDMQS`, `VWICN`, `IASWMQS`, `PVSMGAD` and a spectrum of six positive numbers
- **THEN** it returns a `Right` whose `proteins` and `spectrum` match the inputs

#### Scenario: Rejects an empty candidate list

- **WHEN** `SpectrumMatchProblem.from` is called with no proteins and a non-empty spectrum
- **THEN** it returns `Left(EmptyProteinList)`

#### Scenario: Rejects an empty spectrum

- **WHEN** `SpectrumMatchProblem.from` is called with at least one protein and an empty spectrum
- **THEN** it returns `Left(EmptySpectrum)`

#### Scenario: Rejects a non-positive spectrum value

- **WHEN** `SpectrumMatchProblem.from` is called with a spectrum whose value at index 1 is `-2.0`
- **THEN** it returns `Left(NonPositiveMass(1, -2.0))`

#### Scenario: Cannot be constructed via a public apply or copy

- **WHEN** code attempts `SpectrumMatchProblem(...)` or `.copy(...)` on a constructed instance
- **THEN** the code fails to compile

### Requirement: Spectrum-match result rendering

The system SHALL provide a `SpectrumMatch` result type holding the winning multiplicity (`Int`) and protein string, and exposing a `format: String` rendering the multiplicity on the first line and the protein on the second.

#### Scenario: Exposes the multiplicity and protein

- **WHEN** a `SpectrumMatch` result is constructed with multiplicity `3` and protein `IASWMQS`
- **THEN** its `multiplicity` is `3` and its `protein` is `IASWMQS`

#### Scenario: Formats multiplicity then protein

- **WHEN** `format` is called on a result with multiplicity `3` and protein `IASWMQS`
- **THEN** it returns `"3\nIASWMQS"`

### Requirement: Best spectrum-match computation

The system SHALL provide an algorithm that, given a `SpectrumMatchProblem`, returns the candidate protein maximising the multiplicity of `R⊖S[s_k]` together with that multiplicity, where `S[s_k]` is the multiset of all prefix and suffix weights of `s_k` and the multiplicity is the count of the most frequent difference `r − x` (`r ∈ R`, `x ∈ S[s_k]`). Ties resolve to the first such candidate.

#### Scenario: Computes the canonical Rosalind PRSM sample

- **WHEN** the algorithm is run on proteins `GSDMQS`, `VWICN`, `IASWMQS`, `PVSMGAD` with the spectrum `445.17838 115.02694 186.07931 314.13789 317.1198 215.09061`
- **THEN** it returns multiplicity `3` and a protein achieving that multiplicity (both `GSDMQS` and `IASWMQS` do; any maximiser is acceptable)

#### Scenario: Matches a single candidate against its own residue weight

- **WHEN** the algorithm is run on the single protein `A` with the spectrum `71.03711`
- **THEN** it returns multiplicity `2` and protein `A`

#### Scenario: Selects the candidate with the greater multiplicity

- **WHEN** the algorithm is run on proteins `A` and `AA` with the spectrum `71.03711 142.07422`
- **THEN** it returns multiplicity `4` and protein `AA`
