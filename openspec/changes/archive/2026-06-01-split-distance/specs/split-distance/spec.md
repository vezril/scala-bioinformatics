## ADDED Requirements

### Requirement: SplitDistanceProblemError is a sealed ADT of SplitDistanceProblem construction failures

The system SHALL provide a `sealed trait SplitDistanceProblemError` in the
`bio.domain.graph` package with cases:
- `EmptyTaxa` — no taxon names were supplied;
- `DuplicateTaxon(name: String)` — a taxon name occurred more than once;
- `TreeTaxaMismatch(treeIndex: Int, missing: Set[String], extra: Set[String])` —
  a tree's set of leaf labels differed from the taxa set, where `treeIndex` is
  the 1-based tree number, `missing` are taxa absent from the tree's leaves, and
  `extra` are leaf labels not present in the taxa.

#### Scenario: DuplicateTaxon carries the offending name

- **WHEN** `SplitDistanceProblemError.DuplicateTaxon("rat")` is constructed
- **THEN** its `name` field equals `"rat"`

#### Scenario: TreeTaxaMismatch carries the tree index and the differing labels

- **WHEN** `SplitDistanceProblemError.TreeTaxaMismatch(2, Set("cat"), Set("lion"))` is constructed
- **THEN** its `treeIndex` equals `2`, `missing` equals `Set("cat")`, and `extra` equals `Set("lion")`

### Requirement: SplitDistanceProblem is a validated two-tree comparison bundle

The system SHALL provide a
`sealed abstract case class SplitDistanceProblem(taxa: Vector[String], tree1: NewickTree, tree2: NewickTree)`
in the `bio.domain.graph` package, constructable only through
`SplitDistanceProblem.from(taxa: Vector[String], tree1: NewickTree, tree2: NewickTree): Either[SplitDistanceProblemError, SplitDistanceProblem]`.
Validation SHALL apply first-failure-wins in the order: non-empty taxa, distinct
taxa, `tree1` leaf labels equal to the taxa set, `tree2` leaf labels equal to the
taxa set. The synthesized `apply` and `copy` SHALL NOT be public — direct
construction MUST be a compile error.

#### Scenario: Accepts the canonical Rosalind sample input

- **WHEN** `SplitDistanceProblem.from` is called with taxa `[dog, rat, elephant, mouse, cat, rabbit]` and the two parsed sample trees `(rat,(dog,cat),(rabbit,(elephant,mouse)));` and `(rat,(cat,dog),(elephant,(mouse,rabbit)));`
- **THEN** the result is `Right` of a problem carrying those taxa and trees

#### Scenario: Rejects an empty taxa vector

- **WHEN** `SplitDistanceProblem.from` is called with empty taxa and two valid trees
- **THEN** the result is `Left(SplitDistanceProblemError.EmptyTaxa)`

#### Scenario: Rejects duplicate taxon names

- **WHEN** `SplitDistanceProblem.from` is called with taxa `[dog, rat, dog]` and trees on those labels
- **THEN** the result is `Left(SplitDistanceProblemError.DuplicateTaxon("dog"))`

#### Scenario: Rejects a first tree whose leaf labels differ from the taxa

- **WHEN** `SplitDistanceProblem.from` is called with taxa `[a, b, c, d]` and a `tree1` whose leaves are `[a, b, c, e]` (missing `d`, extra `e`)
- **THEN** the result is `Left(SplitDistanceProblemError.TreeTaxaMismatch(1, Set("d"), Set("e")))`

#### Scenario: Reports the second tree when only it mismatches

- **WHEN** `SplitDistanceProblem.from` is called with taxa `[a, b, c, d]`, a valid `tree1`, and a `tree2` missing leaf `d`
- **THEN** the result is `Left` of a `TreeTaxaMismatch` whose `treeIndex` equals `2`

#### Scenario: Direct apply does not compile

- **WHEN** source code `bio.domain.graph.SplitDistanceProblem(Vector("a"), t1, t2)` is compiled
- **THEN** the compiler rejects the expression

### Requirement: Split is a canonicalised bipartition of the taxa

The system SHALL provide a `Split` type in the `bio.domain.graph` package
representing a partition of the taxon indices into two sides, constructed via a
total smart constructor `Split.of(sideA: BitSet, sideB: BitSet): Split` that
canonicalises the value so that two splits describing the same bipartition
compare equal regardless of which argument is which side. Canonicalisation SHALL
store the side that does not contain the reference index `0`. The synthesized
`apply` and `copy` SHALL NOT be public.

#### Scenario: Side order does not affect equality

- **WHEN** `Split.of(BitSet(0, 4), BitSet(1, 2, 3, 5))` and `Split.of(BitSet(1, 2, 3, 5), BitSet(0, 4))` are constructed
- **THEN** the two splits are equal

#### Scenario: The canonical side excludes the reference index

- **WHEN** `Split.of(BitSet(0, 4), BitSet(1, 2, 3, 5))` is constructed
- **THEN** its canonical `side` equals `BitSet(1, 2, 3, 5)` (the side without index `0`)

#### Scenario: Different bipartitions are not equal

- **WHEN** `Split.of(BitSet(2, 3), BitSet(0, 1, 4, 5))` and `Split.of(BitSet(3, 5), BitSet(0, 1, 2, 4))` are constructed
- **THEN** the two splits are not equal

### Requirement: SplitDistance.compute returns the split distance between the two trees

The system SHALL provide
`SplitDistance.compute(problem: SplitDistanceProblem): Int` in the
`bio.algorithms.graph` package returning `2 * (n − 3) − 2 * s`, where `n` is the
number of taxa and `s` is the number of nontrivial splits shared by both trees. A
nontrivial split is one induced by an internal edge — both sides contain at least
two taxa. Splits SHALL be compared by their canonical [[Split]] form so that a
split appearing in both trees is counted once toward `s`.

#### Scenario: Reproduces the canonical sample distance

- **WHEN** `SplitDistance.compute` is called on the sample problem (taxa `[dog, rat, elephant, mouse, cat, rabbit]`, trees `(rat,(dog,cat),(rabbit,(elephant,mouse)));` and `(rat,(cat,dog),(elephant,(mouse,rabbit)));`)
- **THEN** the result is `2`

#### Scenario: Two identical trees have split distance zero

- **WHEN** `SplitDistance.compute` is called with the same tree for both `tree1` and `tree2`
- **THEN** the result is `0`

#### Scenario: Trees sharing no nontrivial split achieve the maximum distance

- **WHEN** `SplitDistance.compute` is called on two trees over the same taxa whose nontrivial splits are entirely disjoint
- **THEN** the result is `2 * (n − 3)`

#### Scenario: Distance is symmetric in the two trees

- **WHEN** `SplitDistance.compute` is called on `(taxa, T1, T2)` and again on `(taxa, T2, T1)`
- **THEN** both calls return the same value
