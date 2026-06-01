## 1. Error ADT (`SetOperationsProblemError`)

- [x] 1.1 RED: write `SetOperationsProblemErrorSpec` asserting the sealed ADT exposes
  `NonPositiveUniverse(value)`, `ExceedsMaximum(value, max)`, and
  `ElementOutOfRange(setLabel, value, universe)`; confirm it fails to compile
- [x] 1.2 GREEN: create `bio.domain.combinatorics.SetOperationsProblemError` sealed trait
  with the three cases; confirm `sbt test` green
- [x] 1.3 REFACTOR: review naming/fields against `SubsetUniverseSizeError`; rerun tests

## 2. Validated bundle (`SetOperationsProblem`)

- [x] 2.1 RED: write `SetOperationsProblemSpec` covering valid construction (incl. empty
  subsets), `n = 0`, `n = 20001`, an out-of-range element, first-failure ordering, and
  `assertDoesNotCompile` for public `apply`/`copy`; confirm RED
- [x] 2.2 GREEN: create `bio.domain.combinatorics.SetOperationsProblem` as a
  `sealed abstract case class(n, a, b)` with `from(...)` returning
  `Either[SetOperationsProblemError, SetOperationsProblem]`; validation order
  non-positive → exceeds-max → `A` range → `B` range → `Right(new ... {})`; report the
  smallest offending element per set; confirm `sbt test` green
- [x] 2.3 REFACTOR: extract a shared out-of-range check for `A`/`B`; rerun tests

## 3. Result type (`SetOperationsResult`)

- [x] 3.1 RED: write `SetOperationsResultSpec` asserting `format` renders the six sets on
  six lines, ascending and brace-delimited, with empty set as `{}`; confirm RED
- [x] 3.2 GREEN: create `bio.domain.combinatorics.SetOperationsResult` (six
  `SortedSet[Int]` fields) with a `format: String` method; confirm `sbt test` green
- [x] 3.3 REFACTOR: factor the single-set rendering into a private helper; rerun tests

## 4. Algorithm (`SetOperations.compute`)

- [x] 4.1 RED: write `SetOperationsSpec` with the canonical sample (all six sets),
  disjoint subsets, and difference-asymmetry / union-symmetry checks; confirm RED
- [x] 4.2 GREEN: implement `bio.algorithms.combinatorics.SetOperations.compute(problem):
  SetOperationsResult` via set algebra over `universe = SortedSet(1 to n: _*)`; confirm
  `sbt test` green
- [x] 4.3 REFACTOR: ensure pure functional style (no `var`/mutable/imperative loops);
  add Scaladoc; rerun tests

## 5. IO runner and wiring

- [x] 5.1 Create `bio.problems.SETOProb` reading `resources/seto_data.txt` (an `n` line +
  two brace-delimited set lines), parsing sets, chaining Eithers, printing `result.format`
- [x] 5.2 Wire `SETOProb.solve()` into `Main.scala` (comment out the prior runner)
- [x] 5.3 Run `Main` against the sample `seto_data.txt` and confirm the six expected lines

## 6. Verification

- [x] 6.1 Run full `sbt test`; confirm all suites green and the new tests included
