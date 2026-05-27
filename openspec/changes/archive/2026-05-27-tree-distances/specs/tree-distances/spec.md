## ADDED Requirements

### Requirement: Validated NewickDistanceProblem input bundle

The system SHALL provide a validated domain type `bio.domain.graph.NewickDistanceProblem` constructed only through a smart constructor `NewickDistanceProblem.from(tree: NewickTree, x: String, y: String): Either[NewickDistanceProblemError, NewickDistanceProblem]`. The smart constructor MUST verify that `x` appears in `tree.labels` and `y` appears in `tree.labels`. Validation order MUST be: source label first, then target label; first failure wins. The case class MUST be `sealed abstract` so the synthesised `apply` and `copy` cannot leak around the smart constructor.

#### Scenario: Accepts a valid query against a parsed tree
- **WHEN** `NewickDistanceProblem.from(tree, "dog", "cat")` is called against the parsed `(cat)dog;` tree
- **THEN** it returns `Right(NewickDistanceProblem(tree, "dog", "cat"))`

#### Scenario: Rejects an unknown source label
- **WHEN** `NewickDistanceProblem.from(tree, "fish", "cat")` is called against the parsed `(cat)dog;` tree
- **THEN** it returns `Left(NewickDistanceProblemError.UnknownLabel("fish"))`

#### Scenario: Rejects an unknown target label
- **WHEN** `NewickDistanceProblem.from(tree, "dog", "fish")` is called against the parsed `(cat)dog;` tree
- **THEN** it returns `Left(NewickDistanceProblemError.UnknownLabel("fish"))`

### Requirement: NewickDistance.between computes path-edge distance via BFS

The system SHALL provide an algorithm object `bio.algorithms.graph.NewickDistance` with a method `between(problem: NewickDistanceProblem): Int` that returns the number of edges on the unique path between `problem.x` and `problem.y` in `problem.tree`, treating the tree as an undirected graph. The implementation MUST use BFS over a node-ID adjacency map (because internal nodes may be unlabelled and label uniqueness is not guaranteed by the Newick grammar). When `problem.x == problem.y`, the result MUST be `0`.

#### Scenario: Canonical Rosalind sample — labelled root, single child
- **WHEN** `NewickDistance.between` is called with the parsed tree `(cat)dog;` and query `dog cat`
- **THEN** it returns `1`

#### Scenario: Canonical Rosalind sample — two leaves under an unlabelled root
- **WHEN** `NewickDistance.between` is called with the parsed tree `(dog,cat);` and query `dog cat`
- **THEN** it returns `2`

#### Scenario: Distance from a node to itself is zero
- **WHEN** `NewickDistance.between` is called with the parsed tree `(dog,cat);` and query `dog dog`
- **THEN** it returns `0`

#### Scenario: Multi-level path through internal nodes
- **WHEN** `NewickDistance.between` is called with the parsed tree `((a,b)c,(d,e)f)g;` and query `a e`
- **THEN** it returns `4`

#### Scenario: Distance across siblings under a labelled internal node
- **WHEN** `NewickDistance.between` is called with the parsed tree `((a,b)c,(d,e)f)g;` and query `a b`
- **THEN** it returns `2`

#### Scenario: Distance between a leaf and a labelled internal node
- **WHEN** `NewickDistance.between` is called with the parsed tree `((a,b)c,(d,e)f)g;` and query `a g`
- **THEN** it returns `2`
