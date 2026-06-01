## 1. Error ADT (`PerfectCoverageProblemError`)

- [x] 1.1 RED: write `PerfectCoverageProblemErrorSpec` asserting the sealed ADT exposes
  `EmptyKmerCollection`, `KmerTooShort(index, length, min)`, `KmerTooLong(index, length,
  max)`, and `InconsistentLength(index, length, expected)`; confirm it fails to compile
- [x] 1.2 GREEN: create `bio.domain.graph.PerfectCoverageProblemError` sealed trait with the
  four cases (`EmptyKmerCollection` as a case object); confirm `sbt test` green
- [x] 1.3 REFACTOR: review naming/fields against `DeBruijnGraphProblemError`; rerun tests

## 2. Validated bundle (`PerfectCoverageProblem`)

- [x] 2.1 RED: write `PerfectCoverageProblemSpec` covering valid construction (the seven
  length-5 sample reads, order preserved), empty collection, a length-1 k-mer (too short),
  a length-51 k-mer (too long), unequal lengths (inconsistent), first-failure ordering, and
  `assertDoesNotCompile` for public `apply`/`copy`; confirm RED
- [x] 2.2 GREEN: create `bio.domain.graph.PerfectCoverageProblem` as a
  `sealed abstract case class(kmers: Vector[DnaString])` with `from(kmers)` returning
  `Either[PerfectCoverageProblemError, _]`; validation order empty → (per-k-mer, index
  order) too-short (min 2) → too-long (max 50) → inconsistent-length (vs. first k-mer) →
  `Right(new ... {})`; confirm `sbt test` green
- [x] 2.3 REFACTOR: extract the per-k-mer scan helper; ensure pure functional style; rerun tests

## 3. Result type (`CyclicSuperstring`)

- [x] 3.1 RED: write `CyclicSuperstringSpec` asserting `format` renders the bare symbol
  sequence (no surrounding punctuation); confirm RED
- [x] 3.2 GREEN: create `bio.domain.graph.CyclicSuperstring(value: String)` with
  `format: String = value`; confirm `sbt test` green
- [x] 3.3 REFACTOR: tidy Scaladoc; rerun tests

## 4. Algorithm (`PerfectCoverageAssembly.assemble`)

- [x] 4.1 RED: write `PerfectCoverageAssemblySpec` with the canonical sample (assert
  `ACAGATT`, and assert it is a rotation of `GATTACA`), a self-loop case (`AA` → `A`), a
  two-read cycle (`AT, TA` → `AT`), and a duplicate-read case (sample with one read repeated
  → `ACAGATT`); confirm RED
- [x] 4.2 GREEN: implement `bio.algorithms.graph.PerfectCoverageAssembly.assemble(problem):
  CyclicSuperstring` — collect read values into a `Set[String]`, build a `Map[String,String]`
  from each read's length-`(L-1)` prefix (`dropRight(1)`) to its suffix (`tail`), then walk
  the cycle from the lexicographically smallest node for `n` (= node count) steps, emitting
  each visited node's head symbol; confirm `sbt test` green
- [x] 4.3 REFACTOR: ensure pure functional style (no `var`/mutable/imperative loops — use a
  recursive/`Iterator.iterate` walk); add Scaladoc explaining the cycle walk and
  deterministic start; rerun tests

## 5. IO runner and wiring

- [x] 5.1 Create `bio.problems.PCOVProb` reading `resources/pcov_data.txt` (one k-mer per
  line), building each line into a `DnaString`, building the problem, chaining the Either,
  and printing `result.format`
- [x] 5.2 Wire `PCOVProb.solve()` into `Main.scala` (comment out the prior runner)
- [x] 5.3 Run `Main` against the sample `pcov_data.txt` and confirm the output is `ACAGATT`
  (a rotation of the Rosalind sample `GATTACA`)

## 6. Verification

- [x] 6.1 Run full `sbt test`; confirm all suites green and the new tests included
