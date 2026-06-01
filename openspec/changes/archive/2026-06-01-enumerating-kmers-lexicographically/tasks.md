## 1. Error ADT (`KmerEnumerationProblemError`)

- [x] 1.1 RED: write `KmerEnumerationProblemErrorSpec` asserting the sealed ADT exposes
  `EmptyAlphabet`, `TooManySymbols(count, max)`, `DuplicateSymbol(symbol)`,
  `NonPositiveLength(length)`, and `LengthExceedsMaximum(length, max)`; confirm it fails
  to compile
- [x] 1.2 GREEN: create `bio.domain.combinatorics.KmerEnumerationProblemError` sealed
  trait with the five cases (`EmptyAlphabet` as a case object); confirm `sbt test` green
- [x] 1.3 REFACTOR: review naming/fields against `PermutationLengthError` and
  `SetOperationsProblemError`; rerun tests

## 2. Validated bundle (`KmerEnumerationProblem`)

- [x] 2.1 RED: write `KmerEnumerationProblemSpec` covering valid construction (the sample
  alphabet + length, order preserved), empty alphabet, 11 symbols (too many), a duplicate
  symbol, length 0 (non-positive), length 11 (exceeds max), first-failure ordering, and
  `assertDoesNotCompile` for public `apply`/`copy`; confirm RED
- [x] 2.2 GREEN: create `bio.domain.combinatorics.KmerEnumerationProblem` as a
  `sealed abstract case class(alphabet: Vector[Char], length: Int)` with
  `from(alphabet, length)` returning `Either[KmerEnumerationProblemError, _]`; validation
  order empty-alphabet → too-many (10) → duplicate → non-positive → exceeds-max (10) →
  `Right(new ... {})`; confirm `sbt test` green
- [x] 2.3 REFACTOR: extract the first-duplicate helper; ensure pure functional style;
  rerun tests

## 3. Result type (`KmerEnumeration`)

- [x] 3.1 RED: write `KmerEnumerationSpec` asserting `format` renders the k-mers one per
  line in order; confirm RED
- [x] 3.2 GREEN: create `bio.domain.combinatorics.KmerEnumeration(kmers: Vector[String])`
  with `format: String = kmers.mkString("\n")`; confirm `sbt test` green
- [x] 3.3 REFACTOR: tidy Scaladoc; rerun tests

## 4. Algorithm (`EnumerateKmers.enumerate`)

- [x] 4.1 RED: write `EnumerateKmersSpec` with the canonical sample (assert the 16
  ordered k-mers `AA … TT`), the exact `k^n` count and per-string width, a length-1 case
  (returns the alphabet), and a single-symbol-alphabet case (`[A]`, n=3 → `AAA`); confirm
  RED
- [x] 4.2 GREEN: implement `bio.algorithms.combinatorics.EnumerateKmers.enumerate(problem):
  KmerEnumeration` — build the ordered Cartesian product via a left fold
  (prefix-outer / alphabet-inner) so output is lexicographic with no explicit sort;
  confirm `sbt test` green
- [x] 4.3 REFACTOR: ensure pure functional style (no `var`/mutable/imperative loops); add
  Scaladoc explaining why the fold yields lexicographic order; rerun tests

## 5. IO runner and wiring

- [x] 5.1 Create `bio.problems.LEXFProb` reading `resources/lexf_data.txt` (alphabet line
  of whitespace-separated symbols, then the length line), building the problem, chaining
  the Either, and printing `result.format`
- [x] 5.2 Wire `LEXFProb.solve()` into `Main.scala` (comment out the prior runner)
- [x] 5.3 Run `Main` against the sample `lexf_data.txt` and confirm the output is the 16
  ordered k-mers `AA … TT`

## 6. Verification

- [x] 6.1 Run full `sbt test`; confirm all suites green and the new tests included
