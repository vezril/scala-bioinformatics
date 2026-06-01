## 1. Error ADT (`InconsistentCharacterSetProblemError`)

- [x] 1.1 RED: write `InconsistentCharacterSetProblemErrorSpec` asserting the sealed ADT
  exposes `EmptyTable`, `RaggedTable(rowIndex, expected, actual)`,
  `ExceedsMaximumTaxa(count, max)`, and `InvalidCharacter(rowIndex, ch)`; confirm it fails
  to compile
- [x] 1.2 GREEN: create `bio.domain.graph.InconsistentCharacterSetProblemError` sealed
  trait with the four cases (`EmptyTable` as a case object); confirm `sbt test` green
- [x] 1.3 REFACTOR: review naming/fields against `CharacterBasedPhylogenyProblemError`;
  rerun tests

## 2. Validated bundle (`InconsistentCharacterSetProblem`)

- [x] 2.1 RED: write `InconsistentCharacterSetProblemSpec` covering valid construction (the
  sample), empty table, a ragged row, width 101 (too many taxa), a non-`0/1` symbol,
  first-failure ordering, and `assertDoesNotCompile` for public `apply`/`copy`; confirm RED
- [x] 2.2 GREEN: create `bio.domain.graph.InconsistentCharacterSetProblem` as a
  `sealed abstract case class(rows)` with `from(rows)` returning
  `Either[InconsistentCharacterSetProblemError, _]`; validation order empty → ragged →
  exceeds-max (100) → invalid character → `Right(new ... {})`; confirm `sbt test` green
- [x] 2.3 REFACTOR: extract a private per-row shape/char helper; ensure pure functional
  style; rerun tests

## 3. Result type (`ConsistentCharacterTable`)

- [x] 3.1 RED: write `ConsistentCharacterTableSpec` asserting `format` renders the rows one
  per line in order; confirm RED
- [x] 3.2 GREEN: create `bio.domain.graph.ConsistentCharacterTable(rows)` with
  `format: String = rows.mkString("\n")`; confirm `sbt test` green
- [x] 3.3 REFACTOR: tidy Scaladoc; rerun tests

## 4. Algorithm (`FixInconsistentCharacterSet.fix`)

- [x] 4.1 RED: write `FixInconsistentCharacterSetSpec` with the canonical sample (assert
  `Some`, `m−1` rows all drawn from the input, and the result is conflict-free), the
  shared-conflict fixable case, and the two-independent-conflicts `None` case; confirm RED
- [x] 4.2 GREEN: implement `bio.algorithms.graph.FixInconsistentCharacterSet.fix(problem):
  Option[ConsistentCharacterTable]` — enumerate conflicting pairs via the
  four-cross-intersection rule; if none, drop the first row; else the deleted row must be
  in the first conflicting pair, so test its two endpoints (smallest index first) for
  covering every conflict, dropping the first that does, else `None`; confirm `sbt test`
  green
- [x] 4.3 REFACTOR: ensure pure functional style (no `var`/mutable/imperative loops);
  factor the conflict test; add Scaladoc explaining the first-pair-cover argument; rerun
  tests

## 5. IO runner and wiring

- [x] 5.1 Create `bio.problems.CSETProb` reading `resources/cset_data.txt` (one character
  row per non-empty line), building the problem, chaining the Either, and printing
  `table.format` (or a no-solution message when `fix` returns `None`)
- [x] 5.2 Wire `CSETProb.solve()` into `Main.scala` (comment out the prior runner)
- [x] 5.3 Run `Main` against the sample `cset_data.txt` and confirm the output is the input
  with one row deleted and is consistent

## 6. Verification

- [x] 6.1 Run full `sbt test`; confirm all suites green and the new tests included
