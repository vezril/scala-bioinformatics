## 1. WeightedNewickTree domain type

- [x] 1.1 RED: write `WeightedNewickTreeSpec` — a leaf (`label = Some("dog")`, empty children); an internal node with a `WeightedChild(leaf, 42.0)` exposing `weight` and `subtree`. Confirm RED.
- [x] 1.2 GREEN: create `bio.domain.graph.WeightedNewickTree` — `final case class WeightedNewickTree(label: Option[String], children: Vector[WeightedChild])` and `final case class WeightedChild(subtree: WeightedNewickTree, weight: Double)` (in the same file or `WeightedChild.scala`). Add `def labels: Set[String]` (union of this label and descendants') for downstream validation. Run green.

## 2. WeightedNewickParser

- [x] 2.1 RED: write `WeightedNewickParserSpec` — parses `(dog:42,cat:33);` (root with `dog`@42.0, `cat`@33.0), parses `((dog:4,cat:3):74,robot:98,elephant:58);` (an internal child @74.0 with dog/cat), `Left` for `(dog:42,cat:33)` (no terminator) and `(dog:xx,cat:33);` (bad weight). Confirm RED.
- [x] 2.2 GREEN: create `bio.parsing.WeightedNewickParseError` (`sealed trait` + `EmptyInput`, `MissingTerminator`, `UnmatchedOpenParen(pos)`, `UnmatchedCloseParen(pos)`, `UnexpectedCharacter(char, pos)`, `TrailingContent(rest)`, `InvalidWeight(text, pos)`) and `bio.parsing.WeightedNewickParser.parse(input): Either[WeightedNewickParseError, WeightedNewickTree]` — recursive descent: `node := ("(" child ("," child)* ")")? label?`, `child := node (":" weight)?`; label = run of chars not in `(),;:`; weight = run of `[0-9.eE+-]` parsed via `toDoubleOption` (else `InvalidWeight`); default weight `0.0` when absent; validate paren balance + terminating `;`. Run green.

## 3. WeightedTreeDistanceProblem domain type

- [x] 3.1 RED: write `WeightedTreeDistanceProblemErrorSpec` (`NodeNotFound(label)`) and `WeightedTreeDistanceProblemSpec` — accepts `(dog:42,cat:33);` + `cat`/`dog`, rejects `fish`/`dog` (`NodeNotFound("fish")`), rejects `dog`/`fish` (`NodeNotFound("fish")`), and `assertDoesNotCompile` for public `apply`/`copy`. Confirm RED.
- [x] 3.2 GREEN: create `bio.domain.graph.WeightedTreeDistanceProblemError` (`sealed trait` + `NodeNotFound(label: String)`) and `bio.domain.graph.WeightedTreeDistanceProblem` as `sealed abstract case class WeightedTreeDistanceProblem(tree: WeightedNewickTree, x: String, y: String)` with `from(tree, x, y)` checking `tree.labels.contains(x)` then `contains(y)` (first-failure-wins → `NodeNotFound`), building via `Right(new ... {})`. Run green.

## 4. WeightedNewickDistance algorithm

- [x] 4.1 RED: write `WeightedNewickDistanceSpec` — `(dog:42,cat:33);` `cat dog` → `75.0`; `((dog:4,cat:3):74,robot:98,elephant:58);` `dog elephant` → `136.0`; `(dog:42,cat:33);` `dog dog` → `0.0`. Build via `WeightedNewickParser.parse` + `WeightedTreeDistanceProblem.from`. Confirm RED.
- [x] 4.2 GREEN: create `bio.algorithms.graph.WeightedNewickDistance` with `between(problem): Double`. Index the tree by a pure DFS threading a counter → `(labelToId: Map[String, Int], edges: Vector[(Int, Int, Double)])`; build undirected adjacency `Map[Int, List[(Int, Double)]]`; DFS from `labelToId(x)` to `labelToId(y)` accumulating weights (tree ⇒ unique path), returning the sum (`0.0` if `x == y`). Run green.
- [x] 4.3 REFACTOR: review for `var`/`while`/mutable collections (none — recursion + immutable maps); extract `index`/`adjacency`/`pathWeight` helpers. Run full `sbt test` green.

## 5. Runner

- [x] 5.1 Create `bio.problems.NKEWProb` reading `nkew_data.txt` as blank-line-separated blocks (each: tree line + `x y` line, mirroring `NWCKProb`'s `splitBlocks`); per block parse via `WeightedNewickParser.parse`, build `WeightedTreeDistanceProblem.from`, run `WeightedNewickDistance.between`; render each distance (whole numbers without a trailing `.0`) and print space-joined via `IO.println`; all errors printed (never thrown).
- [x] 5.2 Wire `NKEWProb.solve()` as the active runner in `bio.Main` (comment the prior active line), and run the full `sbt test` suite to confirm everything is green.
