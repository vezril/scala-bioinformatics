## ADDED Requirements

### Requirement: NewickTree ADT models a parsed Newick tree

The system SHALL provide a recursive case class `bio.domain.graph.NewickTree(label: Option[String], children: Vector[NewickTree])`. A leaf is a `NewickTree` with `children == Vector.empty` and a non-`None` `label`. An internal node MAY have `label == None` (unlabelled internal node) or `label == Some(_)` (labelled internal node). The class MUST expose a helper `labels: Set[String]` that returns the set of all non-empty labels appearing anywhere in the tree (used by distance-query validation).

#### Scenario: Leaf node carries a label and no children
- **WHEN** `NewickTree(Some("cat"), Vector.empty)` is constructed
- **THEN** `tree.label` is `Some("cat")` and `tree.children` is empty and `tree.labels` is `Set("cat")`

#### Scenario: Labelled internal node has both label and children
- **WHEN** `NewickTree(Some("dog"), Vector(NewickTree(Some("cat"), Vector.empty)))` is constructed
- **THEN** `tree.label` is `Some("dog")` and `tree.labels` is `Set("dog", "cat")`

#### Scenario: Unlabelled internal node has children but no label
- **WHEN** `NewickTree(None, Vector(NewickTree(Some("dog"), Vector.empty), NewickTree(Some("cat"), Vector.empty)))` is constructed
- **THEN** `tree.label` is `None` and `tree.labels` is `Set("dog", "cat")`

### Requirement: NewickParser.parse parses Newick-format strings

The system SHALL provide `bio.parsing.NewickParser.parse(input: String): Either[NewickParseError, NewickTree]`. The parser MUST accept the Newick grammar consisting of: a (possibly nested) parenthesised, comma-separated list of subtrees, an optional label on each node, and a terminating semicolon. Whitespace surrounding the entire input MUST be tolerated; the parser MUST NOT require whitespace handling inside the grammar.

#### Scenario: Parses a single-leaf-subtree labelled internal node
- **WHEN** `NewickParser.parse("(cat)dog;")` is called
- **THEN** it returns `Right(NewickTree(Some("dog"), Vector(NewickTree(Some("cat"), Vector.empty))))`

#### Scenario: Parses an unlabelled-root tree with two leaf children
- **WHEN** `NewickParser.parse("(dog,cat);")` is called
- **THEN** it returns `Right(NewickTree(None, Vector(NewickTree(Some("dog"), Vector.empty), NewickTree(Some("cat"), Vector.empty))))`

#### Scenario: Parses a deeply nested labelled tree
- **WHEN** `NewickParser.parse("((a,b)c,(d,e)f)g;")` is called
- **THEN** it returns `Right(tree)` where `tree.label == Some("g")`, `tree.children.size == 2`, and `tree.labels == Set("a", "b", "c", "d", "e", "f", "g")`

#### Scenario: Parses a single leaf with a terminating semicolon
- **WHEN** `NewickParser.parse("a;")` is called
- **THEN** it returns `Right(NewickTree(Some("a"), Vector.empty))`

#### Scenario: Tolerates surrounding whitespace
- **WHEN** `NewickParser.parse("  (dog,cat);  ")` is called
- **THEN** it returns `Right(NewickTree(None, Vector(NewickTree(Some("dog"), Vector.empty), NewickTree(Some("cat"), Vector.empty))))`

#### Scenario: Rejects empty input
- **WHEN** `NewickParser.parse("")` is called
- **THEN** it returns `Left(NewickParseError.EmptyInput)`

#### Scenario: Rejects whitespace-only input
- **WHEN** `NewickParser.parse("   ")` is called
- **THEN** it returns `Left(NewickParseError.EmptyInput)`

#### Scenario: Rejects input missing the terminating semicolon
- **WHEN** `NewickParser.parse("(dog,cat)")` is called
- **THEN** it returns `Left(NewickParseError.MissingTerminator)`

#### Scenario: Rejects input with an unmatched open paren
- **WHEN** `NewickParser.parse("(dog,cat;")` is called
- **THEN** it returns a `Left` whose error is `NewickParseError.UnmatchedOpenParen(_)` (position-bearing)

#### Scenario: Rejects input with an unmatched close paren
- **WHEN** `NewickParser.parse("dog,cat);")` is called
- **THEN** it returns a `Left` whose error is `NewickParseError.UnmatchedCloseParen(_)` (position-bearing)

#### Scenario: Rejects input with content after the semicolon
- **WHEN** `NewickParser.parse("(a,b);garbage")` is called
- **THEN** it returns `Left(NewickParseError.TrailingContent("garbage"))`
