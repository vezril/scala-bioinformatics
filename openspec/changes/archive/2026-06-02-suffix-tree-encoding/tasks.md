## 1. Error type

- [x] 1.1 RED: write `SuffixTreeProblemErrorSpec` asserting `SequenceTooLong(length, max)` constructs and exposes its fields; confirm it fails to compile (`not found`).
- [x] 1.2 GREEN: create `bio.domain.graph.SuffixTreeProblemError` — `sealed trait` + `final case class SequenceTooLong(length: Int, max: Int)`; run the spec green.

## 2. SuffixTreeProblem domain type

- [x] 2.1 RED: write `SuffixTreeProblemSpec` — accepts `ATAAATG` (Right, `dna` preserved), accepts empty DnaString, rejects a length-1001 DnaString (`SequenceTooLong(1001, 1000)`), and `assertDoesNotCompile` for public `apply` and `copy`. Confirm RED.
- [x] 2.2 GREEN: create `bio.domain.graph.SuffixTreeProblem` as `sealed abstract case class SuffixTreeProblem(dna: DnaString)` with `MaxLength = 1000` and `from(dna)` rejecting `length > 1000` → `SequenceTooLong`, building via `Right(new SuffixTreeProblem(dna) {})`. Run green.

## 3. SuffixTreeEncoding result type

- [x] 3.1 RED: write `SuffixTreeEncodingResultSpec` — `edges` field exposure, `format` of `["A$", "$"]` → `"A$\n$"`, empty → `""`. Confirm RED.
- [x] 3.2 GREEN: create `bio.domain.graph.SuffixTreeEncoding` result — `final case class SuffixTreeEncoding(edges: Vector[String])` with `format: String = edges.mkString("\n")`. Run green.

## 4. SuffixTreeConstruction algorithm

- [x] 4.1 RED: write `SuffixTreeConstructionSpec` with scenarios (comparing sorted edge labels) — `ATAAATG` → the 12 sample labels; `""` → `["$"]`; `A` → `["$", "A$"]`; `AAA` → `["$", "$", "$", "A", "A", "A$"]`. Build inputs via `DnaString.from` + `SuffixTreeProblem.from`. Confirm RED.
- [x] 4.2 GREEN: create `bio.algorithms.graph.SuffixTreeConstruction` with `encode(problem: SuffixTreeProblem): SuffixTreeEncoding`. Form `text = dna.value + "$"`; build the tree by folding `insert` over suffix starts `0 until text.length`, threading an immutable `Tree(children: Map[Int, Map[Char, Edge]], nextId: Int)` (root id 0). `insert` is a `@tailrec` descent: look up the child edge by the next char; if absent add a leaf edge labelled `[pos, n)`; if present, match via a `@tailrec` `commonPrefix` — full match descends, partial match splits the edge (new internal node, re-parent old child, add new leaf). Collect labels `text.substring(e.start, e.end)` over all edges into `SuffixTreeEncoding`. Run green.
- [x] 4.3 REFACTOR: review for `var`/`while`/mutable collections (none — immutable maps + tailrec) and confirm constant-stack descent; keep `Edge`/`Tree` as private case classes and factor `insert`/`commonPrefix` helpers. Run full `sbt test` green.

## 5. Runner

- [x] 5.1 Create `bio.problems.SUFFProb` reading the DNA string from `suff_data.txt` (single line; strip a single trailing `$` if present), build `DnaString.from` and `SuffixTreeProblem.from`, run `SuffixTreeConstruction.encode`, print `format` via `IO.println`; all errors printed (never thrown).
- [x] 5.2 Wire `SUFFProb.solve()` as the active runner in `bio.Main` (comment the prior active line), and run the full `sbt test` suite to confirm everything is green.
