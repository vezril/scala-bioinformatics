## 1. NewickTree ADT (Red phase first)

- [x] 1.1 Write `src/test/scala/bio/domain/graph/NewickTreeSpec.scala` covering: leaf (label-only) construction; labelled internal node; unlabelled internal node; `labels` helper returns the full label set on a 3-level tree; `labels` on an unlabelled-root tree excludes the `None`. Run `sbt Test/compile` and confirm Red.
- [x] 1.2 Create `src/main/scala/bio/domain/graph/NewickTree.scala` as `final case class NewickTree(label: Option[String], children: Vector[NewickTree])` with `def labels: Set[String]` that recursively collects all `Some(_)` labels via `children.foldLeft(label.toSet)(_ ++ _.labels)`.
- [x] 1.3 Run `sbt testOnly bio.domain.graph.NewickTreeSpec` and confirm Green.

## 2. NewickParser (Red phase first)

- [x] 2.1 Write `src/test/scala/bio/parsing/NewickParserSpec.scala` covering: canonical `(cat)dog;` and `(dog,cat);`; deeply nested `((a,b)c,(d,e)f)g;`; single-leaf `a;`; surrounding whitespace `  (dog,cat);  `; rejection cases — empty input, whitespace-only input, missing terminator, unmatched open paren, unmatched close paren, trailing content. Run `sbt Test/compile` and confirm Red.
- [x] 2.2 Create `src/main/scala/bio/parsing/NewickParseError.scala` as a `sealed trait` with cases `EmptyInput`, `MissingTerminator`, `UnmatchedOpenParen(position: Int)`, `UnmatchedCloseParen(position: Int)`, `UnexpectedCharacter(char: Char, position: Int)`, `TrailingContent(remaining: String)`.
- [x] 2.3 Create `src/main/scala/bio/parsing/NewickParser.scala` exposing `parse(input: String): Either[NewickParseError, NewickTree]`. Internal recursive descent: trim outer whitespace; reject empty; parse a single node (optional `(...,...)` group followed by an optional bare-identifier label, where identifiers are runs of non-`(),:;` characters); require terminating `;`; reject any non-whitespace trailing content.
- [x] 2.4 Run `sbt testOnly bio.parsing.NewickParserSpec` and confirm Green.

## 3. NewickDistanceProblem (Red phase first)

- [x] 3.1 Write `src/test/scala/bio/domain/graph/NewickDistanceProblemSpec.scala` covering: accepts a valid query against the `(cat)dog;` tree; rejects unknown source as `UnknownLabel("fish")`; rejects unknown target as `UnknownLabel("fish")`; companion `apply` does not leak (assertDoesNotCompile); `copy` does not leak (assertDoesNotCompile). Run `sbt Test/compile` and confirm Red.
- [x] 3.2 Create `src/main/scala/bio/domain/graph/NewickDistanceProblemError.scala` as a `sealed trait` with case `UnknownLabel(label: String)`.
- [x] 3.3 Create `src/main/scala/bio/domain/graph/NewickDistanceProblem.scala` as `sealed abstract case class NewickDistanceProblem(tree: NewickTree, x: String, y: String)` with companion `from(tree, x, y): Either[NewickDistanceProblemError, NewickDistanceProblem]` checking `x ∈ tree.labels` first, then `y ∈ tree.labels`.
- [x] 3.4 Run `sbt testOnly bio.domain.graph.NewickDistanceProblemSpec` and confirm Green.

## 4. NewickDistance algorithm (Red phase first)

- [x] 4.1 Write `src/test/scala/bio/algorithms/graph/NewickDistanceSpec.scala` covering: canonical `(cat)dog;` query `dog cat → 1`; canonical `(dog,cat);` query `dog cat → 2`; same-node `(dog,cat);` query `dog dog → 0`; multi-level `((a,b)c,(d,e)f)g;` query `a e → 4`; sibling query `a b → 2`; leaf-to-internal `a g → 2`. Use a private `fixture(newick, x, y)` helper that parses + builds the `NewickDistanceProblem` and `getOrElse(sys.error(...))`. Run `sbt Test/compile` and confirm Red.
- [x] 4.2 Create `src/main/scala/bio/algorithms/graph/NewickDistance.scala` exposing `between(problem: NewickDistanceProblem): Int`. Algorithm: walk the tree assigning each node a synthetic `Int` ID; record an undirected adjacency map keyed by ID and a label→ID map (first occurrence wins); BFS from `idOf(x)` to `idOf(y)`; short-circuit `x == y` to return `0`.
- [x] 4.3 Run `sbt testOnly bio.algorithms.graph.NewickDistanceSpec` and confirm Green.

## 5. Refactor & Final Verification

- [x] 5.1 Re-read all six new source files and four new test specs; ensure Scaladoc references Rosalind problem code (NWCK), explains the synthetic-ID rationale, and the validation order matches the rest of the framework. No stray imports.
- [x] 5.2 Run `sbt test` and confirm the full suite passes with zero regressions.
