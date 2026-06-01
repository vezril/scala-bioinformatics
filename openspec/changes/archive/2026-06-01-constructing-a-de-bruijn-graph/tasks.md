## 1. Error ADT (`DeBruijnGraphProblemError`)

- [x] 1.1 RED: write `DeBruijnGraphProblemErrorSpec` asserting the sealed ADT exposes
  `EmptyKmerCollection`, `TooManyKmers(count, max)`, `KmerTooShort(index, length, min)`,
  `KmerTooLong(index, length, max)`, and `InconsistentLength(index, length, expected)`;
  confirm it fails to compile
- [x] 1.2 GREEN: create `bio.domain.graph.DeBruijnGraphProblemError` sealed trait with the
  five cases (`EmptyKmerCollection` as a case object); confirm `sbt test` green
- [x] 1.3 REFACTOR: review naming/fields against `GenomeAssemblyProblemError`; rerun tests

## 2. Validated bundle (`DeBruijnGraphProblem`)

- [x] 2.1 RED: write `DeBruijnGraphProblemSpec` covering valid construction (the six
  length-4 sample k-mers, order preserved), empty collection, 1001 k-mers (too many), a
  length-1 k-mer (too short), a length-51 k-mer (too long), unequal lengths (inconsistent),
  first-failure ordering, and `assertDoesNotCompile` for public `apply`/`copy`; confirm RED
- [x] 2.2 GREEN: create `bio.domain.graph.DeBruijnGraphProblem` as a
  `sealed abstract case class(kmers: Vector[DnaString])` with `from(kmers)` returning
  `Either[DeBruijnGraphProblemError, _]`; validation order empty → too-many (1000) →
  (per-k-mer, index order) too-short (min 2) → too-long (max 50) → inconsistent-length
  (vs. first k-mer) → `Right(new ... {})`; confirm `sbt test` green
- [x] 2.3 REFACTOR: extract the per-k-mer scan helper; ensure pure functional style; rerun tests

## 3. Edge and result types (`DeBruijnEdge`, `DeBruijnGraph`)

- [x] 3.1 RED: write `DeBruijnGraphSpec` asserting `format` renders each edge as
  `(from, to)` one per line in order; confirm RED
- [x] 3.2 GREEN: create `bio.domain.graph.DeBruijnEdge(from: String, to: String)`
  (mirroring `OverlapEdge`) and `bio.domain.graph.DeBruijnGraph(edges: Vector[DeBruijnEdge])`
  with `format: String = edges.map(e => s"(${e.from}, ${e.to})").mkString("\n")`; confirm
  `sbt test` green
- [x] 3.3 REFACTOR: tidy Scaladoc; rerun tests

## 4. Algorithm (`DeBruijnGraphConstruction.construct`)

- [x] 4.1 RED: write `DeBruijnGraphConstructionSpec` with the canonical sample (assert the
  9 ordered edges), a reverse-complement case (`AAAA` → `(AAA, AAA), (TTT, TTT)`), a
  palindrome case (`ATAT` → single `(ATA, TAT)`), and a duplicate-input case
  (`GGGG, GGGG` → `(CCC, CCC), (GGG, GGG)`); confirm RED
- [x] 4.2 GREEN: implement `bio.algorithms.graph.DeBruijnGraphConstruction.construct(problem):
  DeBruijnGraph` — collect the k-mer values into a `Set[String]`, union with their reverse
  complements (reuse `DnaReverseComplement.reverseComplement`), map each (k+1)-mer `r` to
  `DeBruijnEdge(r.dropRight(1), r.tail)`, then sort lexicographically by `(from, to)`;
  confirm `sbt test` green
- [x] 4.3 REFACTOR: ensure pure functional style (no `var`/mutable/imperative loops); add
  Scaladoc explaining the set-based de-duplication and deterministic sort; rerun tests

## 5. IO runner and wiring

- [x] 5.1 Create `bio.problems.DBRUProb` reading `resources/dbru_data.txt` (one k-mer per
  line), building each line into a `DnaString`, building the problem, chaining the Either,
  and printing `result.format`
- [x] 5.2 Wire `DBRUProb.solve()` into `Main.scala` (comment out the prior runner)
- [x] 5.3 Run `Main` against the sample `dbru_data.txt` and confirm the output is the 9
  ordered edges matching the Rosalind sample

## 6. Verification

- [x] 6.1 Run full `sbt test`; confirm all suites green and the new tests included
