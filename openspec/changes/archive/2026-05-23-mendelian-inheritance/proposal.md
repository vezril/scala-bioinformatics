## Why

Spec 5 of the project brief introduces the framework's first non-sequence problem: probability that two randomly mating organisms from a typed population produce offspring with a dominant phenotype. This expands the framework beyond DNA/RNA string manipulation into population genetics — a new domain that introduces three new concepts (genotype, population, probability) and the first pure-mathematical algorithm.

## What Changes

- **NEW** `Genotype` sealed ADT in `bio.domain`: `HomozygousDominant`, `Heterozygous`, `HomozygousRecessive`
- **NEW** `Population` case class in `bio.domain` with a smart constructor returning `Either[PopulationError, Population]` — counts must be non-negative, total must be ≥ 2
- **NEW** `Probability` value class in `bio.domain` with a smart constructor returning `Either[ProbabilityError, Probability]` — wrapped value must be in `[0.0, 1.0]`
- **NEW** `MendelianInheritance.probabilityOfDominantPhenotype(pop: Population): Probability` in `bio.algorithms` — pure, total, analytical (closed-form) solution
- **NEW** error ADTs `PopulationError` and `ProbabilityError` colocated with their owning types — separate from `SequenceError` since the domain is unrelated

The Rosalind problem also suggests Monte Carlo simulation as a sanity check. We will **not** implement simulation as a production deliverable, but the test suite will include the Rosalind sample plus several analytically-verifiable edge cases that confirm the closed-form solution.

## Capabilities

### New Capabilities

- `mendelian-inheritance`: Population genetics domain types (`Genotype`, `Population`, `Probability`, error ADTs) plus the analytical algorithm `MendelianInheritance.probabilityOfDominantPhenotype` that computes the probability of offspring showing a dominant phenotype from a typed parental population.

### Modified Capabilities

## Impact

- New files in `bio.domain`: `Genotype.scala`, `Population.scala`, `Probability.scala`, `PopulationError.scala`, `ProbabilityError.scala`
- New file in `bio.algorithms`: `MendelianInheritance.scala`
- New test files mirroring each
- No changes to existing code, no new dependencies, no `build.sbt` changes
- All existing 71 tests continue passing
