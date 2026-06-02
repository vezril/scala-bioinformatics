## 1. Error type

- [x] 1.1 RED: write `MaximalRepeatProblemErrorSpec` asserting `SequenceTooLong(length, max)` and `NonPositiveMinLength(minLength)` construct and expose their fields; confirm it fails to compile (`not found`).
- [x] 1.2 GREEN: create `bio.domain.graph.MaximalRepeatProblemError` — `sealed trait` + `SequenceTooLong(length: Int, max: Int)`, `NonPositiveMinLength(minLength: Int)`; run the spec green.

## 2. MaximalRepeatProblem domain type

- [x] 2.1 RED: write `MaximalRepeatProblemSpec` — accepts a 25 bp DnaString + minLength 20 (Right, fields preserved), rejects a 1001 bp DnaString (`SequenceTooLong(1001, 1000)`), rejects minLength 0 (`NonPositiveMinLength(0)`), and `assertDoesNotCompile` for public `apply` and `copy`. Confirm RED.
- [x] 2.2 GREEN: create `bio.domain.graph.MaximalRepeatProblem` as `sealed abstract case class MaximalRepeatProblem(dna: DnaString, minLength: Int)` with `MaxLength = 1000` and `from(dna, minLength)` applying first-failure-wins (`dna.value.length > 1000` → `SequenceTooLong`; `minLength < 1` → `NonPositiveMinLength`), building via `Right(new MaximalRepeatProblem(dna, minLength) {})`. Run green.

## 3. MaximalRepeats result type

- [x] 3.1 RED: write `MaximalRepeatsResultSpec` — `repeats` field exposure, `format` of `[AG, TAG]` → `"AG\nTAG"`, empty → `""`. Confirm RED.
- [x] 3.2 GREEN: create `bio.domain.graph.MaximalRepeats` result — `final case class MaximalRepeats(repeats: Vector[String])` with `format: String = repeats.mkString("\n")`. Run green.

## 4. IdentifyMaximalRepeats algorithm

- [x] 4.1 RED: write `IdentifyMaximalRepeatsSpec` — canonical sample (minLength 20) → exactly the 2 expected repeats (`theSameElementsAs`); `TAGTTAGCGAGA` minLength 2 → contains `AG` and `TAG`; `ACGT` minLength 1 → empty; `TAGTTAGCGAGA` minLength 20 → empty. Build inputs via `DnaString.from` + `MaximalRepeatProblem.from`. Confirm RED.
- [x] 4.2 GREEN: create `bio.algorithms.graph.IdentifyMaximalRepeats` with `find(problem): MaximalRepeats`. Build `text = dna.value + "$"`. Construct a structured suffix tree (the SUFF naive-insertion builder, immutable `Map[Int, Map[Char, Edge]]`, root 0, edges `(start,end,child)`), additionally recording `leafStart: Map[Int, Int]` (each suffix insertion creates one leaf → its start position). Derive `parentEdge`; compute per-node `stringDepth` via `@tailrec` BFS top-down; compute per-node `leftChars: Set[Option[Char]]` and `repLeafStart` via `foldLeft` over nodes in decreasing edge-depth (leaf seeds `Set(if start==0 None else Some(text(start-1)))` / its own start; each node unions/propagates into its parent). A node is a maximal repeat iff `children(node).size >= 2` and `leftChars(node).size >= 2`; its label is `text.substring(repLeafStart(node), repLeafStart(node) + stringDepth(node))`; keep those with length ≥ `minLength`, distinct, sorted. Wrap in `MaximalRepeats`. Run green.
- [x] 4.3 REFACTOR: review for `var`/`while`/mutable collections (none — immutable maps, tailrec BFS, folds) and confirm constant-stack construction; extract private helpers (`buildTree`, `stringDepths`, `leftCharsAndReps`). Run full `sbt test` green.

## 5. Runner

- [x] 5.1 Create `bio.problems.MREPProb` reading the DNA string from `mrep_data.txt` (single non-empty line), build `DnaString.from` and `MaximalRepeatProblem.from(dna, 20)`, run `IdentifyMaximalRepeats.find`, print `format` via `IO.println`; all errors printed (never thrown).
- [x] 5.2 Wire `MREPProb.solve()` as the active runner in `bio.Main` (comment the prior active line), and run the full `sbt test` suite to confirm everything is green.
