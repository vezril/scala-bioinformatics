## 1. Error ADT (`KmerCompositionProblemError`)

- [x] 1.1 RED: write `KmerCompositionProblemErrorSpec` asserting the sealed ADT exposes
  `NonPositiveK(k)` and `KExceedsMaximum(k, max)`; confirm it fails to compile
- [x] 1.2 GREEN: create `bio.domain.combinatorics.KmerCompositionProblemError` sealed
  trait with the two cases; confirm `sbt test` green
- [x] 1.3 REFACTOR: review naming/fields against `KmerEnumerationProblemError`; rerun tests

## 2. Validated bundle (`KmerCompositionProblem`)

- [x] 2.1 RED: write `KmerCompositionProblemSpec` covering valid construction (a sample
  `DnaString` + k=4, dna/k preserved), k=0 (non-positive), k=11 (exceeds max),
  first-failure ordering, and `assertDoesNotCompile` for public `apply`/`copy`; confirm RED
- [x] 2.2 GREEN: create `bio.domain.combinatorics.KmerCompositionProblem` as a
  `sealed abstract case class(dna: DnaString, k: Int)` with `from(dna, k)` returning
  `Either[KmerCompositionProblemError, _]`; validation order non-positive (k < 1) →
  exceeds-max (k > 10) → `Right(new ... {})`; confirm `sbt test` green
- [x] 2.3 REFACTOR: ensure pure functional style; rerun tests

## 3. Result type (`KmerComposition`)

- [x] 3.1 RED: write `KmerCompositionSpec` asserting `format` renders the counts
  space-separated in order; confirm RED
- [x] 3.2 GREEN: create `bio.domain.combinatorics.KmerComposition(counts: Vector[Int])`
  with `format: String = counts.mkString(" ")`; confirm `sbt test` green
- [x] 3.3 REFACTOR: tidy Scaladoc; rerun tests

## 4. Algorithm (`ComposeKmers.compose`)

- [x] 4.1 RED: write `ComposeKmersSpec` with the canonical Rosalind 4-mer sample
  (assert the 256 counts equal the expected output), the exact `4^k` count and
  `sum == max(0, n-k+1)`, a string-shorter-than-k case (all zeros), a length-1 case
  (counts of A,C,G,T), and an overlapping case (`AAAA`, k=2 → count 3 for `AA`);
  confirm RED
- [x] 4.2 GREEN: implement `bio.algorithms.combinatorics.ComposeKmers.compose(problem):
  KmerComposition` — build the ordered k-mers by reusing
  `EnumerateKmers.enumerate(KmerEnumerationProblem.from(Vector('A','C','G','T'), k))`,
  slide a length-k window (`sliding`) tallying into a `Map[String,Int]` via `foldLeft`,
  then map each ordered k-mer to its count (default 0); confirm `sbt test` green
- [x] 4.3 REFACTOR: ensure pure functional style (no `var`/mutable/imperative loops);
  add Scaladoc noting the inner enumeration is total under the validated k-cap; rerun tests

## 5. IO runner and wiring

- [x] 5.1 Create `bio.problems.KMERProb` reading `resources/kmer_data.txt` via
  `FastaFileReader`, taking the single record's `DnaString`, building the problem with
  k=4, chaining the Either, and printing `result.format`
- [x] 5.2 Wire `KMERProb.solve()` into `Main.scala` (comment out the prior runner)
- [x] 5.3 Run `Main` against the sample `kmer_data.txt` and confirm the output is the
  256 counts matching the Rosalind sample output

## 6. Verification

- [x] 6.1 Run full `sbt test`; confirm all suites green and the new tests included
