## Why

Spec 11 of the project brief — "Calculating Expected Offspring" — extends the framework's genetics subdomain with a *population-level expectation*. Where spec 5 (`MendelianInheritance`) computed the probability that a single offspring expresses a dominant phenotype given a population of *individuals*, spec 11 computes the expected *number* of dominant offspring across a population of *couples* grouped by their genotype pairing. It introduces a new domain type (a population indexed by the six possible diploid pairings) and the framework's first algorithm that returns an expected count rather than a probability.

This is also the first algorithm where the input has a fixed-arity multi-field shape (6 counts in a defined order) — a useful precedent for future problems that take "k counts, one per category" as input.

## What Changes

- **NEW** `CouplePopulation` domain type in `bio.domain.genetics` — `sealed abstract case class CouplePopulation(homDomHomDom, homDomHet, homDomHomRec, hetHet, hetHomRec, homRecHomRec: Int)` representing a population of couples grouped by genotype pairing, with `from(c1, c2, c3, c4, c5, c6: Int): Either[CouplePopulationError, CouplePopulation]` smart constructor enforcing `0 <= each <= 20000`
- **NEW** `CouplePopulationError` sealed ADT in `bio.domain.genetics` — cases `NegativeCount(index: Int, value: Int)` and `ExceedsMaxCount(index: Int, value: Int)`; `index` is 1-based and matches the Rosalind input order
- **NEW** `ExpectedOffspring.dominantPhenotype(pop: CouplePopulation): Double` algorithm in `bio.algorithms.genetics` — total function returning the expected number of dominant-phenotype offspring across the population, assuming every couple produces exactly two offspring
- **NO** modified capabilities; `Population`/`PopulationError` (from spec 5) are untouched

## Capabilities

### New Capabilities

- `expected-offspring`: The `CouplePopulation` validated domain type (with smart constructor enforcing per-count bounds `[0, 20000]`), the `CouplePopulationError` ADT, and the `ExpectedOffspring.dominantPhenotype(pop): Double` algorithm computing the expected dominant-offspring count for a population of couples under standard Mendelian assumptions.

### Modified Capabilities

None.

## Impact

- New files in `bio.domain.genetics`: `CouplePopulation.scala`, `CouplePopulationError.scala`
- New file in `bio.algorithms.genetics`: `ExpectedOffspring.scala`
- New test files mirroring each new source file
- No changes to existing files
- No new external dependencies
- All existing 209 tests continue passing
