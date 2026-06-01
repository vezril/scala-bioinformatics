## 1. Error ADT (`GenomeAssemblyProblemError`)

- [x] 1.1 RED: write `GenomeAssemblyProblemErrorSpec` asserting the sealed ADT exposes
  `EmptyReadCollection`, `TooManyReads(count, max)`, and
  `ReadTooLong(index, length, max)`; confirm it fails to compile
- [x] 1.2 GREEN: create `bio.domain.graph.GenomeAssemblyProblemError` sealed trait with
  the three cases (`EmptyReadCollection` as a case object); confirm `sbt test` green
- [x] 1.3 REFACTOR: review naming/fields against `SharedMotifProblemError`; rerun tests

## 2. Validated bundle (`GenomeAssemblyProblem`)

- [x] 2.1 RED: write `GenomeAssemblyProblemSpec` covering valid construction (the sample
  reads), empty collection, 51 reads (too many), a read longer than 1000 bp,
  first-failure ordering, and `assertDoesNotCompile` for public `apply`/`copy`; confirm RED
- [x] 2.2 GREEN: create `bio.domain.graph.GenomeAssemblyProblem` as a
  `sealed abstract case class(reads: Vector[DnaString])` with `from(reads)` returning
  `Either[GenomeAssemblyProblemError, _]`; validation order empty → too-many (50) →
  read-too-long (1000) → `Right(new ... {})`; confirm `sbt test` green
- [x] 2.3 REFACTOR: extract the first-oversized-read helper; ensure pure functional style;
  rerun tests

## 3. Result type (`ShortestSuperstring`)

- [x] 3.1 RED: write `ShortestSuperstringSpec` asserting `format` returns the assembled
  sequence; confirm RED
- [x] 3.2 GREEN: create `bio.domain.graph.ShortestSuperstring(value: String)` with
  `format: String = value`; confirm `sbt test` green
- [x] 3.3 REFACTOR: tidy Scaladoc; rerun tests

## 4. Algorithm (`GenomeAssembly.assemble`)

- [x] 4.1 RED: write `GenomeAssemblySpec` with the canonical sample (assert
  `Some("ATTAGACCTGCCGGAATAC")`), a single-read case (assembles to itself), an
  every-read-is-a-substring property check, and an unassemblable `None` case; confirm RED
- [x] 4.2 GREEN: implement `bio.algorithms.graph.GenomeAssembly.assemble(problem):
  Option[ShortestSuperstring]` — compute each read's unique >half-length successor,
  find the unique start (no predecessor), walk the chain appending only the
  non-overlapping tail; return `None` if no unique chain covers every read; confirm
  `sbt test` green
- [x] 4.3 REFACTOR: ensure pure functional style (no `var`/mutable/imperative loops);
  factor the maximal-overlap helper; add Scaladoc explaining the uniqueness guarantee;
  rerun tests

## 5. IO runner and wiring

- [x] 5.1 Create `bio.problems.LONGProb` reading `resources/long_data.txt` via
  `FastaFileReader`, mapping records to `DnaString` reads, building the problem, chaining
  the Either, and printing `result.format` (or a no-solution message when `assemble`
  returns `None`)
- [x] 5.2 Wire `LONGProb.solve()` into `Main.scala` (comment out the prior runner)
- [x] 5.3 Run `Main` against the sample `long_data.txt` and confirm the output is the
  shortest superstring containing every read

## 6. Verification

- [x] 6.1 Run full `sbt test`; confirm all suites green and the new tests included
