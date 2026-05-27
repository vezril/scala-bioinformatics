## Purpose
Define the disease-carrier-frequencies capability: the `DiseaseCarriers.frequencies(homozygousRecessive: Vector[Probability]): Vector[Probability]` algorithm computing, under Hardy-Weinberg equilibrium, the per-factor probability that a randomly selected diploid individual carries at least one copy of the recessive allele (`B = 2√A − A` where `A = q²` is the homozygous-recessive proportion). Serves the Rosalind "Counting Disease Carriers" (AFRQ) problem and is the fifth algorithm in the `genetics` subdomain alongside `MendelianInheritance`, `ExpectedOffspring`, `IndependentAlleles`, and `IndependentSegregation`. Adds no new domain types — reuses the existing `Probability` value type from `bio.domain.stats` for both input elements and output elements.

## Requirements

### Requirement: DiseaseCarriers.frequencies computes per-factor carrier probability from homozygous-recessive proportions under Hardy-Weinberg equilibrium
The system SHALL provide `DiseaseCarriers.frequencies(homozygousRecessive: Vector[bio.domain.stats.Probability]): Vector[bio.domain.stats.Probability]` returning a vector of the same length as the input where each output element is computed as `2 * sqrt(p.value) - p.value` for the corresponding input element `p`. The algorithm SHALL reside in the `bio.algorithms.genetics` package. The function SHALL be total — every input vector produces a defined output vector of `Probability` values (the math `2√a − a` maps `[0, 1] → [0, 1]`).

#### Scenario: Rosalind sample produces the canonical three values within 0.001 absolute error
- **WHEN** `DiseaseCarriers.frequencies(input)` is called with `input = Vector(0.1, 0.25, 0.5).map(Probability.from(_).toOption.get)`
- **THEN** the result has length `3` and each element's `.value` is within `0.001` of the corresponding expected value in `Vector(0.532, 0.75, 0.914)`

#### Scenario: An input of 0.0 produces a carrier probability of exactly 0.0
- **WHEN** `DiseaseCarriers.frequencies(input)` is called with `input = Vector(Probability.from(0.0).toOption.get)`
- **THEN** the result has length `1` and `result(0).value == 0.0` (no recessive alleles in the population → no carriers)

#### Scenario: An input of 1.0 produces a carrier probability of exactly 1.0
- **WHEN** `DiseaseCarriers.frequencies(input)` is called with `input = Vector(Probability.from(1.0).toOption.get)`
- **THEN** the result has length `1` and `result(0).value == 1.0` (every individual is homozygous recessive → every individual is a carrier)

#### Scenario: An input of 0.25 (the q=1/2 case) produces exactly 0.75
- **WHEN** `DiseaseCarriers.frequencies(input)` is called with `input = Vector(Probability.from(0.25).toOption.get)`
- **THEN** the result has length `1` and `result(0).value` is within `1e-12` of `0.75` (exact: `q = 0.5`, `B = 2·0.5 − 0.25 = 0.75`)

#### Scenario: An empty input produces an empty output
- **WHEN** `DiseaseCarriers.frequencies(Vector.empty)` is called
- **THEN** the result is `Vector.empty`

#### Scenario: Result length always equals input length
- **WHEN** `DiseaseCarriers.frequencies(input)` is called with an input vector of length 7
- **THEN** the result has length `7`

#### Scenario: Output is monotonically non-decreasing for a sorted input — higher homozygous-recessive proportion implies a non-lower carrier probability
- **WHEN** `DiseaseCarriers.frequencies(input)` is called with `input = Vector(0.1, 0.25, 0.5, 0.9).map(Probability.from(_).toOption.get)` (a sorted ascending input)
- **THEN** for every consecutive pair `(result(i), result(i+1))` with `i in 0 until 3`, `result(i).value <= result(i+1).value`

#### Scenario: Every output element is a valid Probability (in [0, 1])
- **WHEN** `DiseaseCarriers.frequencies(input)` is called with any valid `Vector[Probability]` input
- **THEN** every output element's `.value` satisfies `0.0 <= value <= 1.0` (structurally guaranteed by the math `2√a − a` mapping `[0, 1] → [0, 1]`)
