## 1. TrieEdge type

- [x] 1.1 RED: write `TrieEdgeSpec` asserting `TrieEdge(1, 2, DnaNucleotide.A)` exposes `parent`, `child`, and `symbol`; confirm it fails to compile (`not found`).
- [x] 1.2 GREEN: create `bio.domain.graph.TrieEdge` — `final case class TrieEdge(parent: Int, child: Int, symbol: DnaNucleotide)`; run the spec green.

## 2. Error type

- [x] 2.1 RED: write `PatternTrieProblemErrorSpec` asserting `TooManyPatterns(size, max)`, `PatternTooLong(index, length, max)`, and `PrefixConflict(prefixIndex, ofIndex)` construct and expose their fields; confirm RED.
- [x] 2.2 GREEN: create `bio.domain.graph.PatternTrieProblemError` — `sealed trait` + `TooManyPatterns(size: Int, max: Int)`, `PatternTooLong(index: Int, length: Int, max: Int)`, `PrefixConflict(prefixIndex: Int, ofIndex: Int)`; run green.

## 3. PatternTrieProblem domain type

- [x] 3.1 RED: write `PatternTrieProblemSpec` — accepts (`ATAGA`,`ATC`,`GAT`) (Right, patterns preserved), accepts empty collection, rejects 101 patterns (`TooManyPatterns(101,100)`), rejects a 101 bp pattern at index 0 (`PatternTooLong(0,101,100)`), rejects proper prefix (`AT`,`ATC`) → `PrefixConflict(0,1)`, rejects duplicates (`AT`,`AT`) → `PrefixConflict(0,1)`, and `assertDoesNotCompile` for public `apply` and `copy`. Confirm RED.
- [x] 3.2 GREEN: create `bio.domain.graph.PatternTrieProblem` as `sealed abstract case class PatternTrieProblem(patterns: Vector[DnaString])` with `MaxPatterns = 100`, `MaxPatternLength = 100`, and `from(patterns)` applying first-failure-wins (size > 100 → `TooManyPatterns`; first pattern with length > 100 → `PatternTooLong`; first `(i,j)` with `i != j` and `patterns(j).startsWith(patterns(i))` → `PrefixConflict(i, j)`), building via `Right(new PatternTrieProblem(patterns) {})`. Run green.

## 4. PatternTrie result type

- [x] 4.1 RED: write `PatternTrieResultSpec` — `edges` field exposure, `format` of the canonical sample's edges → the 9-line string, empty → `""`. Confirm RED.
- [x] 4.2 GREEN: create `bio.domain.graph.PatternTrie` result — `final case class PatternTrie(edges: Vector[TrieEdge])` with `format: String = edges.map(e => s"${e.parent} ${e.child} ${DnaNucleotide.toChar(e.symbol)}").mkString("\n")`. Run green.

## 5. TrieConstruction algorithm

- [x] 5.1 RED: write `TrieConstructionSpec` with scenarios — canonical (`ATAGA`,`ATC`,`GAT`) → the exact ordered 9 edges; single `AT` → `(1,2,A),(2,3,T)`; shared prefix (`AT`,`AG`) → `(1,2,A),(2,3,T),(2,4,G)`; empty collection → no edges. Build inputs with `DnaString.from`, `PatternTrieProblem.from`. Confirm RED.
- [x] 5.2 GREEN: create `bio.algorithms.graph.TrieConstruction` with `construct(problem: PatternTrieProblem): PatternTrie`. Root id `1`, next id starts at `2`. Thread immutable state `(nextId, children: Map[(Int, DnaNucleotide), Int], edges: Vector[TrieEdge])` via `foldLeft` over patterns, and within each pattern over its nucleotides (`DnaNucleotide.fromChar`): reuse `children((current, symbol))` if present, else create a new node/edge with `nextId`. Return `PatternTrie(edges)`. Run green.
- [x] 5.3 REFACTOR: review for `var`/`while`/mutable collections (none — pure FP) and raw `Char` literals (none — ADT dispatch); extract a small private `insert` helper and a `State` case class. Run full `sbt test` green.

## 6. Runner

- [x] 6.1 Create `bio.problems.TRIEProb` reading `trie_data.txt` (one DNA pattern per non-empty line). Parse each line via `DnaString.from` (fold into `Either`), build `PatternTrieProblem.from`, run `TrieConstruction.construct`, print `format` via `IO.println`; all errors printed (never thrown).
- [x] 6.2 Wire `TRIEProb.solve()` as the active runner in `bio.Main` (comment the prior active line), and run the full `sbt test` suite to confirm everything is green.
