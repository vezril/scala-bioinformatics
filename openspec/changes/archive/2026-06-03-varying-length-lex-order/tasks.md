## 1. LexOrderProblemError ADT

- [x] 1.1 RED: write `bio.domain.combinatorics.LexOrderProblemErrorSpec` asserting `TooManySymbols(15, 12)` carries `count == 15` / `max == 12`; confirm it fails to compile ("not found").
- [x] 1.2 GREEN: create `bio.domain.combinatorics.LexOrderProblemError` as a sealed trait with `EmptyAlphabet` (case object), `TooManySymbols(count, max)`, `DuplicateSymbol(symbol)`, `NonPositiveLength(length)`, `LengthExceedsMaximum(length, max)`. Confirm the test passes.

## 2. LexOrderProblem domain type

- [x] 2.1 RED: write `bio.domain.combinatorics.LexOrderProblemSpec` covering: accepts `Vector('D','N','A')`/3, rejects empty (`EmptyAlphabet`), rejects 13 symbols (`TooManySymbols(13,12)`), rejects duplicate (`DuplicateSymbol('A')`), rejects length 0 (`NonPositiveLength(0)`), rejects length 5 (`LengthExceedsMaximum(5,4)`), `assertDoesNotCompile` for public `apply` and for `copy`. Confirm RED.
- [x] 2.2 GREEN: create `bio.domain.combinatorics.LexOrderProblem` as `sealed abstract case class LexOrderProblem(alphabet: Vector[Char], maxLength: Int)` with `from(...)` enforcing (first-failure-wins) non-empty, MaxSymbols=12, no duplicate, length 1..4 (MaxLength=4). Confirm GREEN.

## 3. LexOrdering result type

- [x] 3.1 RED: write `bio.domain.combinatorics.LexOrderingSpec` asserting `LexOrdering(Vector("D","DD","DN")).format == "D\nDD\nDN"`. Confirm RED.
- [x] 3.2 GREEN: create `bio.domain.combinatorics.LexOrdering(strings: Vector[String])` with `def format: String = strings.mkString("\n")`. Confirm GREEN.

## 4. VaryingLengthLexOrder algorithm

- [x] 4.1 RED: write `bio.algorithms.combinatorics.VaryingLengthLexOrderSpec` covering: canonical sample (`D N A`, 3) → the full 39-string ordered vector; maxLength 1 → `Vector("D","N","A")`; single-symbol `X`, 3 → `Vector("X","XX","XXX")`. Confirm RED.
- [x] 4.2 GREEN: create `bio.algorithms.combinatorics.VaryingLengthLexOrder.enumerate(problem): LexOrdering` via a pure pre-order DFS: `go(prefix)` maps each alphabet symbol `c` to `s = prefix + c`, emitting `s` and prepending it before `go(s)` when `s.length < maxLength`; start from `go("")`. Confirm GREEN.
- [x] 4.3 REFACTOR: tidy the recursion, keep `enumerate` pure/total; rerun the spec to confirm still GREEN.

## 5. LEXVProb runner

- [x] 5.1 Add `bio.problems.LEXVProb` reading `src/main/scala/resources/lexv_data.txt` (line 1 = space-separated symbols, line 2 = n), building the `LexOrderProblem`, running `VaryingLengthLexOrder.enumerate`, and printing `result.format` through `IO`; errors print a message rather than throw.
- [x] 5.2 Point `Main.scala` at `LEXVProb.solve()` and confirm `sbt run` prints the canonical ordering.

## 6. Full suite

- [x] 6.1 Run `sbt test` and confirm the whole suite is green.
