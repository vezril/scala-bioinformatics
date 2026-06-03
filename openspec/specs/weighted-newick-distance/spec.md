# Newick Format with Edge Weights

## Purpose

This capability parses weighted Newick trees whose edges carry `label:weight` annotations and computes the distance between two queried nodes — the sum of edge weights along the unique path connecting them. It backs the Rosalind NKEW problem.

## Requirements

### Requirement: Weighted Newick tree representation

The system SHALL provide a `WeightedNewickTree` type — a node with an optional `label` and a `children: Vector[WeightedChild]`, where each `WeightedChild` pairs a child `WeightedNewickTree` with the `weight` of the edge to it.

#### Scenario: A leaf exposes its label and no children

- **WHEN** a `WeightedNewickTree` is constructed with label `dog` and no children
- **THEN** its `label` is `Some("dog")` and its `children` are empty

#### Scenario: An internal node exposes its weighted children

- **WHEN** a `WeightedNewickTree` is constructed with a single `WeightedChild` wrapping the leaf `dog` with weight `42.0`
- **THEN** its `children` has one element whose `weight` is `42.0` and whose subtree is the `dog` leaf

### Requirement: Weighted Newick parsing

The system SHALL provide a `WeightedNewickParser.parse(input)` returning `Either[WeightedNewickParseError, WeightedNewickTree]` that parses Newick trees with `label:weight` edges, reading each edge's weight as a number.

#### Scenario: Parses a simple weighted tree

- **WHEN** `WeightedNewickParser.parse` is called with `(dog:42,cat:33);`
- **THEN** it returns a `Right` whose root has children `dog` (edge weight `42.0`) and `cat` (edge weight `33.0`)

#### Scenario: Parses a nested weighted tree

- **WHEN** `WeightedNewickParser.parse` is called with `((dog:4,cat:3):74,robot:98,elephant:58);`
- **THEN** it returns a `Right` whose root has three children, one of which is an internal node (edge weight `74.0`) containing `dog` and `cat`

#### Scenario: Rejects input without a terminator

- **WHEN** `WeightedNewickParser.parse` is called with `(dog:42,cat:33)`
- **THEN** it returns a `Left`

#### Scenario: Rejects a non-numeric edge weight

- **WHEN** `WeightedNewickParser.parse` is called with `(dog:xx,cat:33);`
- **THEN** it returns a `Left`

### Requirement: Weighted tree distance problem validation

The system SHALL provide a validated `WeightedTreeDistanceProblem` wrapping a parsed `WeightedNewickTree` and two query node labels `x` and `y`, both of which must occur in the tree. It MUST be constructed only through a smart constructor `from(tree, x, y)` returning `Either[WeightedTreeDistanceProblemError, WeightedTreeDistanceProblem]`, first-failure-wins.

#### Scenario: Accepts two labels present in the tree

- **WHEN** `WeightedTreeDistanceProblem.from` is called with the parsed tree of `(dog:42,cat:33);` and labels `cat`, `dog`
- **THEN** it returns a `Right`

#### Scenario: Rejects a missing first label

- **WHEN** `WeightedTreeDistanceProblem.from` is called with the parsed tree of `(dog:42,cat:33);` and labels `fish`, `dog`
- **THEN** it returns `Left(NodeNotFound("fish"))`

#### Scenario: Rejects a missing second label

- **WHEN** `WeightedTreeDistanceProblem.from` is called with the parsed tree of `(dog:42,cat:33);` and labels `dog`, `fish`
- **THEN** it returns `Left(NodeNotFound("fish"))`

### Requirement: Weighted tree distance computation

The system SHALL provide an algorithm that, given a `WeightedTreeDistanceProblem`, returns the distance between `x` and `y` — the sum of edge weights along the unique path connecting them in the tree.

#### Scenario: Computes the first canonical NKEW distance

- **WHEN** the algorithm is run on the parsed tree of `(dog:42,cat:33);` with query `cat dog`
- **THEN** it returns `75.0`

#### Scenario: Computes a distance across an internal node

- **WHEN** the algorithm is run on the parsed tree of `((dog:4,cat:3):74,robot:98,elephant:58);` with query `dog elephant`
- **THEN** it returns `136.0`

#### Scenario: The distance from a node to itself is zero

- **WHEN** the algorithm is run on the parsed tree of `(dog:42,cat:33);` with query `dog dog`
- **THEN** it returns `0.0`
