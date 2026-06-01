## Purpose

Computes the quartet distance dq(T1,T2) = 2·(C(n,4) − shared) between two unrooted
binary trees on the same taxa per the Rosalind "Quartet Distance" (QRTD) problem.
Provides the validated `QuartetDistanceProblem` two-tree input bundle with its
`QuartetDistanceProblemError` ADT and the algorithm that counts the 4-taxon subsets
whose induced quartet topology differs between the two trees.

## Requirements

### Requirement: Quartet-distance input errors are represented as a dedicated ADT

The system SHALL represent the ways a quartet-distance input can be invalid as a sealed
`QuartetDistanceProblemError` ADT with the cases `EmptyTaxa`, `DuplicateTaxon(name)`, and
`TreeTaxaMismatch(treeIndex, missing, extra)`, where `treeIndex` identifies which of the
two trees disagrees with the declared taxa and `missing` / `extra` are the offending
taxon sets.

#### Scenario: Error ADT enumerates the invalid-input cases

- **WHEN** the `QuartetDistanceProblemError` ADT is inspected
- **THEN** it is a sealed type whose cases are `EmptyTaxa`, `DuplicateTaxon` carrying the
  duplicated taxon name, and `TreeTaxaMismatch` carrying the offending tree index and the
  `missing` and `extra` taxon sets

#### Scenario: Mismatch error distinguishes the two trees

- **WHEN** a `TreeTaxaMismatch` is produced for the second tree
- **THEN** its `treeIndex` identifies the second tree and its `missing`/`extra` sets
  describe how that tree's leaf labels differ from the declared taxa

### Requirement: Quartet-distance problem is a validated, invariant-bearing bundle

The system SHALL provide a `QuartetDistanceProblem` bundling a taxon list and two unrooted
binary trees, constructed only through a smart constructor `from(taxa, tree1, tree2)` that
returns `Either[QuartetDistanceProblemError, QuartetDistanceProblem]`. The constructor
SHALL reject empty taxa, duplicate taxa, and any tree whose leaf labels do not exactly
match the declared taxa, reporting the first failure encountered. The type SHALL NOT
expose a public `apply` or `copy` that bypasses validation.

#### Scenario: Valid input yields a problem

- **WHEN** `from` is given a non-empty list of distinct taxa and two trees whose leaf
  labels each equal the taxon set
- **THEN** it returns a `Right` containing the `QuartetDistanceProblem`

#### Scenario: Empty taxa are rejected

- **WHEN** `from` is given an empty taxon list
- **THEN** it returns `Left(EmptyTaxa)`

#### Scenario: Duplicate taxa are rejected

- **WHEN** `from` is given a taxon list containing a repeated name
- **THEN** it returns `Left(DuplicateTaxon(name))` for the first repeated name

#### Scenario: First tree mismatch is reported

- **WHEN** the first tree's leaf labels do not equal the declared taxa
- **THEN** it returns `Left(TreeTaxaMismatch(...))` identifying the first tree with the
  missing and extra taxon sets

#### Scenario: Second tree mismatch is reported

- **WHEN** the first tree matches the taxa but the second tree's leaf labels do not
- **THEN** it returns `Left(TreeTaxaMismatch(...))` identifying the second tree

#### Scenario: First failure wins

- **WHEN** an input is invalid in more than one way (e.g. duplicate taxa and a tree
  mismatch)
- **THEN** the error reflects the earliest failure in the validation order
  (empty → duplicate → tree-1 mismatch → tree-2 mismatch)

#### Scenario: Construction cannot bypass validation

- **WHEN** code attempts to call a public `apply` or `copy` on `QuartetDistanceProblem`
- **THEN** the code does not compile

### Requirement: Quartet distance counts differing quartet topologies

The system SHALL compute the quartet distance `dq(T1, T2) = 2·(C(n, 4) − shared)`, where
`shared` is the number of 4-taxon subsets whose induced quartet topology is identical in
both trees, and SHALL return the result as a `Long`. The computation SHALL scale to the
full Rosalind input ceiling (`n ≤ 2000`) without enumerating every 4-taxon subset.

#### Scenario: Canonical sample

- **WHEN** computing the quartet distance for taxa `A B C D E`, `T1 = (A,C,((B,D),E));`
  and `T2 = (C,(B,D),(A,E));`
- **THEN** the result is `4`

#### Scenario: Identical trees have zero distance

- **WHEN** both trees are the same topology over the same taxa
- **THEN** the quartet distance is `0`

#### Scenario: Smallest differing case

- **WHEN** `n = 4` and the two trees induce different quartet topologies on the single
  4-subset
- **THEN** the quartet distance is `2`

#### Scenario: Distance is symmetric

- **WHEN** the quartet distance is computed as `compute(T1, T2)` and as `compute(T2, T1)`
- **THEN** both results are equal
