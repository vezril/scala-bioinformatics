## 1. SuffixTreeEdge type

- [x] 1.1 RED: write `SuffixTreeEdgeSpec` asserting `SuffixTreeEdge("node1", "node2", 1, 1)` exposes `parent`, `child`, `start`, and `length`; confirm it fails to compile (`not found`).
- [x] 1.2 GREEN: create `bio.domain.graph.SuffixTreeEdge` — `final case class SuffixTreeEdge(parent: String, child: String, start: Int, length: Int)`; run the spec green.

## 2. Error type

- [x] 2.1 RED: write `LongestRepeatProblemErrorSpec` asserting `NonPositiveK(k)`, `TextTooLong(length, max)`, and `EdgeOutOfBounds(index, start, length, textLength)` construct and expose their fields; confirm RED.
- [x] 2.2 GREEN: create `bio.domain.graph.LongestRepeatProblemError` — `sealed trait` + `NonPositiveK(k: Int)`, `TextTooLong(length: Int, max: Int)`, `EdgeOutOfBounds(index: Int, start: Int, length: Int, textLength: Int)`; run green.

## 3. LongestRepeatProblem domain type

- [x] 3.1 RED: write `LongestRepeatProblemSpec` — accepts (`CATACATAC$`, 2, within-bounds edges) (Right, fields preserved), accepts empty edge list, rejects k=0 (`NonPositiveK(0)`), rejects text length 20002 (`TextTooLong(20002,20001)`), rejects out-of-bounds edge for text `AC$` start 5 length 1 (`EdgeOutOfBounds(0,5,1,3)`), and `assertDoesNotCompile` for public `apply` and `copy`. Confirm RED.
- [x] 3.2 GREEN: create `bio.domain.graph.LongestRepeatProblem` as `sealed abstract case class LongestRepeatProblem(text: String, k: Int, edges: Vector[SuffixTreeEdge])` with `MaxTextLength = 20001` and `from(text, k, edges)` applying first-failure-wins (`k < 1` → `NonPositiveK`; `text.length > 20001` → `TextTooLong`; first edge with `start < 1` or `start - 1 + length > text.length` (index order) → `EdgeOutOfBounds`), building via `Right(new LongestRepeatProblem(...) {})`. Run green.

## 4. LongestRepeat result type

- [x] 4.1 RED: write `LongestRepeatResultSpec` — `substring` field exposure, `format` of `CATAC` → `"CATAC"`, empty → `""`. Confirm RED.
- [x] 4.2 GREEN: create `bio.domain.graph.LongestRepeat` result — `final case class LongestRepeat(substring: String)` with `format: String = substring`. Run green.

## 5. LongestMultipleRepeat algorithm

- [x] 5.1 RED: write `LongestMultipleRepeatSpec` with the canonical sample edge list (`CATACATAC$`) — k=2 → `CATAC`; k=4 → `A`; k=5 → `""` (empty). Build the problem via `LongestRepeatProblem.from`. Confirm RED.
- [x] 5.2 GREEN: create `bio.algorithms.graph.LongestMultipleRepeat` with `find(problem: LongestRepeatProblem): LongestRepeat`. Build `childrenOf: Map[String, Vector[SuffixTreeEdge]]`, `parentEdge: Map[String, SuffixTreeEdge]`, and `root` (node never a child). Compute string-depth and edge-depth via a `@tailrec` BFS from the root; compute leaf-counts via a `foldLeft` over nodes in decreasing edge-depth (add each node's count to its parent; leaves init 1, internal init 0). Pick the internal node (has children) with `leafCount >= k` maximising string-depth; reconstruct its path-string via a `@tailrec` walk up `parentEdge` collecting labels then `mkString` (empty if none qualifies). Wrap in `LongestRepeat`. Run green.
- [x] 5.3 REFACTOR: review for `var`/`while`/mutable collections (none — tailrec BFS + folds + immutable `Map`/`Set`) and confirm constant-stack traversal; extract small private helpers (`childrenOf`, `stringDepths`, `leafCounts`, `pathString`). Run full `sbt test` green.

## 6. Runner

- [x] 6.1 Create `bio.problems.LREPProb` reading `lrep_data.txt` (line 1: text `s$`; line 2: `k`; remaining lines: `parent child start length` edges). Parse `k` via `toIntOption`; parse each edge line via `split("\\s+")` into `SuffixTreeEdge` (start/length via `toIntOption`), folding into `Either`; build `LongestRepeatProblem.from`; run `LongestMultipleRepeat.find`; print `format` via `IO.println`; all errors printed (never thrown).
- [x] 6.2 Wire `LREPProb.solve()` as the active runner in `bio.Main` (comment the prior active line), and run the full `sbt test` suite to confirm everything is green.
