# character-table Specification

## Purpose

Computes the character table of an unrooted binary tree given in Newick
format (Rosalind problem 32 — CSTR, "Creating a Character Table"). One row
per nontrivial edge-split; columns indexed by the lexicographically-sorted
taxa. Provides a validated `CharacterTableProblem` input bundle and the
`CharacterTable.compute` algorithm.

## Requirements

### Requirement: Validated CharacterTableProblem input bundle

The system SHALL provide a validated domain type `bio.domain.graph.CharacterTableProblem` constructed only through a smart constructor `CharacterTableProblem.from(tree: NewickTree): Either[CharacterTableProblemError, CharacterTableProblem]`. The smart constructor MUST count the labelled leaves of the tree (nodes with `children.isEmpty && label.isDefined`) and reject inputs with more than 200 unique leaf labels as `CharacterTableProblemError.TooManyTaxa(count, max)`. The constructed value MUST expose a public `leafLabels: Vector[String]` field containing the unique labelled leaves sorted in ascending lexicographic order. The case class MUST be `sealed abstract` so the synthesised `apply` and `copy` cannot leak around the smart constructor.

#### Scenario: Accepts the canonical Rosalind sample tree
- **WHEN** `CharacterTableProblem.from` is called with the parsed `(dog,((elephant,mouse),robot),cat);` tree
- **THEN** it returns `Right(problem)` where `problem.leafLabels == Vector("cat", "dog", "elephant", "mouse", "robot")`

#### Scenario: Accepts a single-leaf tree
- **WHEN** `CharacterTableProblem.from` is called with the parsed `a;` tree
- **THEN** it returns `Right(problem)` where `problem.leafLabels == Vector("a")`

#### Scenario: Accepts a 200-leaf tree at the upper boundary
- **WHEN** `CharacterTableProblem.from` is called with a tree whose 200 leaf labels are `t001..t200`
- **THEN** it returns `Right(problem)` where `problem.leafLabels.size == 200`

#### Scenario: Rejects a 201-leaf tree as TooManyTaxa
- **WHEN** `CharacterTableProblem.from` is called with a tree whose 201 leaf labels are `t001..t201`
- **THEN** it returns `Left(CharacterTableProblemError.TooManyTaxa(201, 200))`

#### Scenario: Companion apply does not leak
- **WHEN** code attempts to construct via `CharacterTableProblem(tree, Vector.empty)`
- **THEN** compilation fails (the synthesised apply is hidden behind the smart constructor)

#### Scenario: copy does not leak
- **WHEN** code attempts to mutate via `problem.copy(leafLabels = Vector.empty)`
- **THEN** compilation fails (the synthesised copy is hidden behind the smart constructor)

### Requirement: CharacterTable.compute returns the nontrivial character table rows

The system SHALL provide an algorithm object `bio.algorithms.graph.CharacterTable` with a method `compute(problem: CharacterTableProblem): Vector[String]` that returns the nontrivial-split rows of the character table.

The implementation MUST:
- Enumerate every internal-edge split by walking each non-root node of `problem.tree` and computing `subtreeLeaves(node) | (allLeaves − subtreeLeaves(node))`.
- Filter out trivial splits (those where either side has fewer than 2 labelled leaves).
- For each retained split, emit a binary row of length `problem.leafLabels.size`: position `i` is `'1'` iff `problem.leafLabels(i)` is in the side that does **not** contain `problem.leafLabels.head` (the lexicographically-first taxon); otherwise `'0'`.
- Deduplicate the resulting rows (different edges of a tree never share a split, but the same split may be produced from both endpoints during the walk).
- Return the rows sorted in ascending lexicographic order.

When `problem.leafLabels.size < 4` no nontrivial split is possible and the result MUST be `Vector.empty`. When the tree has only trivial splits (e.g. a flat star), the result MUST be `Vector.empty`.

#### Scenario: Canonical Rosalind CSTR sample
- **WHEN** `CharacterTable.compute` is called with the problem wrapping the parsed `(dog,((elephant,mouse),robot),cat);` tree
- **THEN** it returns `Vector("00110", "00111")`

#### Scenario: Single-leaf tree yields an empty table
- **WHEN** `CharacterTable.compute` is called with the problem wrapping the parsed `a;` tree
- **THEN** it returns `Vector.empty`

#### Scenario: Flat tree (no internal edges) yields an empty table
- **WHEN** `CharacterTable.compute` is called with the problem wrapping the parsed `(a,b,c,d);` tree
- **THEN** it returns `Vector.empty`

#### Scenario: Balanced quartet produces one nontrivial split
- **WHEN** `CharacterTable.compute` is called with the problem wrapping the parsed `((a,b),(c,d));` tree
- **THEN** it returns `Vector("0011")`

#### Scenario: Deeper nesting produces three nontrivial splits
- **WHEN** `CharacterTable.compute` is called with the problem wrapping the parsed `((a,b),((c,d),(e,f)));` tree
- **THEN** it returns `Vector("000011", "001100", "001111")`

#### Scenario: Output row length always equals leaf count
- **WHEN** `CharacterTable.compute` is called with any non-empty result
- **THEN** every row has length equal to `problem.leafLabels.size`
