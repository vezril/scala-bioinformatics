# phylogenetic-ancestors Specification

## Purpose

Counts the internal nodes of an unrooted binary tree with `n` leaves
(Rosalind problem 29 — INOD, "Counting Phylogenetic Ancestors").
Provides a validated `UnrootedBinaryTreeLeafCount` input bundle and the
`PhylogeneticAncestors.internalNodes` closed-form algorithm.

## Requirements

### Requirement: Validated UnrootedBinaryTreeLeafCount input bundle

The system SHALL provide a validated domain type `bio.domain.graph.UnrootedBinaryTreeLeafCount` constructed only through a smart constructor `UnrootedBinaryTreeLeafCount.from(n: Int): Either[UnrootedBinaryTreeLeafCountError, UnrootedBinaryTreeLeafCount]`. The smart constructor MUST enforce `3 <= n <= 10000` (the Rosalind INOD bounds). Validation order MUST be: lower-bound check first, then upper-bound check, with the first failure winning. The case class MUST be `sealed abstract` so the synthesised `apply` and `copy` cannot leak around the smart constructor.

#### Scenario: Accepts the canonical Rosalind sample value
- **WHEN** `UnrootedBinaryTreeLeafCount.from(4)` is called
- **THEN** it returns `Right(UnrootedBinaryTreeLeafCount(4))`

#### Scenario: Accepts the lower boundary
- **WHEN** `UnrootedBinaryTreeLeafCount.from(3)` is called
- **THEN** it returns `Right(UnrootedBinaryTreeLeafCount(3))`

#### Scenario: Accepts the upper boundary
- **WHEN** `UnrootedBinaryTreeLeafCount.from(10000)` is called
- **THEN** it returns `Right(UnrootedBinaryTreeLeafCount(10000))`

#### Scenario: Rejects values below the lower bound
- **WHEN** `UnrootedBinaryTreeLeafCount.from(2)` is called
- **THEN** it returns `Left(UnrootedBinaryTreeLeafCountError.BelowMinimum(2, 3))`

#### Scenario: Rejects zero
- **WHEN** `UnrootedBinaryTreeLeafCount.from(0)` is called
- **THEN** it returns `Left(UnrootedBinaryTreeLeafCountError.BelowMinimum(0, 3))`

#### Scenario: Rejects negative values
- **WHEN** `UnrootedBinaryTreeLeafCount.from(-5)` is called
- **THEN** it returns `Left(UnrootedBinaryTreeLeafCountError.BelowMinimum(-5, 3))`

#### Scenario: Rejects values above the upper bound
- **WHEN** `UnrootedBinaryTreeLeafCount.from(10001)` is called
- **THEN** it returns `Left(UnrootedBinaryTreeLeafCountError.ExceedsMaximum(10001, 10000))`

### Requirement: PhylogeneticAncestors.internalNodes counts unrooted-binary internal nodes

The system SHALL provide an algorithm object `bio.algorithms.graph.PhylogeneticAncestors` with a method `internalNodes(problem: UnrootedBinaryTreeLeafCount): Int` that returns the number of internal nodes of any unrooted binary tree whose number of leaves equals `problem.n`. The implementation MUST use the closed-form identity `internalNodes = n - 2` (valid for all `n >= 3` per the unrooted-binary degree-sum identity). The return type MUST be `Int`.

#### Scenario: Canonical Rosalind sample (n = 4)
- **WHEN** `PhylogeneticAncestors.internalNodes` is called with a `UnrootedBinaryTreeLeafCount` constructed from `n = 4`
- **THEN** it returns `2`

#### Scenario: Lower-boundary input (n = 3)
- **WHEN** `PhylogeneticAncestors.internalNodes` is called with a `UnrootedBinaryTreeLeafCount` constructed from `n = 3`
- **THEN** it returns `1`

#### Scenario: Upper-boundary input (n = 10000)
- **WHEN** `PhylogeneticAncestors.internalNodes` is called with a `UnrootedBinaryTreeLeafCount` constructed from `n = 10000`
- **THEN** it returns `9998`

#### Scenario: Mid-range input (n = 100)
- **WHEN** `PhylogeneticAncestors.internalNodes` is called with a `UnrootedBinaryTreeLeafCount` constructed from `n = 100`
- **THEN** it returns `98`
