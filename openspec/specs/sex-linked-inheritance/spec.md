# Sex-Linked Inheritance

## Purpose

Given the proportion of males exhibiting each recessive X-linked gene, compute the probability that a randomly selected female is a carrier of that gene. Under Hardy–Weinberg equilibrium, the male trait proportion equals the recessive allele frequency `q`, so the female carrier (heterozygote) probability is `2q(1 − q)` (Rosalind SEXL).

## Requirements

### Requirement: Sex-linked problem representation

The system SHALL provide a `SexLinkedProblem` domain type wrapping the male trait proportions as a `Vector[Probability]`, where each element is the proportion of males exhibiting a recessive X-linked gene. The `[0,1]` bound on each proportion is owned by `Probability`, so the problem carries no additional invariant and exposes the wrapped proportions.

#### Scenario: Wraps and exposes the male proportions

- **WHEN** a `SexLinkedProblem` is constructed from the proportions `0.1`, `0.5`, `0.8`
- **THEN** its `maleProportions` field returns those three `Probability` values

#### Scenario: Accepts an empty array

- **WHEN** a `SexLinkedProblem` is constructed from no proportions
- **THEN** its `maleProportions` field is empty

### Requirement: Carrier probabilities result rendering

The system SHALL provide a `CarrierProbabilities` result type holding the per-gene female carrier probabilities as a `Vector[Double]` and exposing a `format: String` rendering the values in order, space-separated, each to three decimal places. The empty result MUST render as the empty string.

#### Scenario: Exposes the carrier probabilities

- **WHEN** a `CarrierProbabilities` result is constructed from a vector of values
- **THEN** its `values` field returns exactly that vector

#### Scenario: Formats values space-separated to three decimals

- **WHEN** `format` is called on a result holding `0.18`, `0.5`, `0.32`
- **THEN** it returns `"0.180 0.500 0.320"`

#### Scenario: Empty result renders as the empty string

- **WHEN** `format` is called on a result holding no values
- **THEN** it returns `""`

### Requirement: Sex-linked carrier probability computation

The system SHALL provide an algorithm that, given a `SexLinkedProblem`, returns the female carrier probability for each gene, in order. For a male trait proportion `q` (the recessive allele frequency), the carrier probability MUST be `2·q·(1 − q)` (the Hardy–Weinberg heterozygote frequency).

#### Scenario: Computes the canonical Rosalind SEXL sample

- **WHEN** the algorithm is run with proportions `0.1`, `0.5`, `0.8`
- **THEN** the carrier probabilities are within `0.001` of `0.18`, `0.5`, and `0.32`

#### Scenario: Yields zero carriers at the allele-frequency extremes

- **WHEN** the algorithm is run with proportions `0.0` and `1.0`
- **THEN** the carrier probabilities are `0.0` and `0.0`

#### Scenario: Yields the maximum carrier probability at frequency one half

- **WHEN** the algorithm is run with the proportion `0.5`
- **THEN** the carrier probability is within `0.001` of `0.5`

#### Scenario: Returns an empty result for an empty array

- **WHEN** the algorithm is run with no proportions
- **THEN** the result holds no values
