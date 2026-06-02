## 1. Error ADT

- [x] 1.1 RED: write `CompleteCycleProblemErrorSpec` in `src/test/scala/bio/domain/graph/` asserting `CompleteCycleProblemError` is a sealed trait with `EmptyKmerCollection` (case object), `TooManyReads(count, max)`, `KmerTooShort(index, length, min)`, `KmerTooLong(index, length, max)`, and `InconsistentLength(index, length, expected)` cases; confirm it fails to compile (`not found: type/value CompleteCycleProblemError`).
- [x] 1.2 GREEN: create `src/main/scala/bio/domain/graph/CompleteCycleProblemError.scala` — `sealed trait` with the five cases above; confirm Green with `sbt "testOnly bio.domain.graph.CompleteCycleProblemErrorSpec"`.

## 2. Validated read collection

- [x] 2.1 RED: write `CompleteCycleProblemSpec` covering: accepts the 17 canonical 3-mers (kmers preserved in order, repeats kept), rejects empty with `EmptyKmerCollection`, rejects 51 reads with `TooManyReads(51, 50)`, rejects a length-1 read at index 1 with `KmerTooShort(1, 1, 2)`, rejects a length-7 read at index 1 with `KmerTooLong(1, 7, 6)`, rejects an inconsistent-length read at index 1 with `InconsistentLength`, and `assertDoesNotCompile` for public `apply` and `copy`; confirm Red.
- [x] 2.2 GREEN: create `src/main/scala/bio/domain/graph/CompleteCycleProblem.scala` — `sealed abstract case class CompleteCycleProblem(kmers: Vector[DnaString])`; `MinLength = 2`, `MaxLength = 6`, `MaxReads = 50`; `from` first-failure-wins empty → too-many → `firstBadKmer` (collectFirst: too-short → too-long → inconsistent vs `kmers.head` length), returning `Right(new CompleteCycleProblem(kmers) {})`; confirm Green.
- [x] 2.3 REFACTOR: review naming/structure against the `PerfectCoverageProblem` precedent; rerun the spec to confirm still Green.

## 3. Result type

- [x] 3.1 RED: write `CompleteCycleAssembliesSpec` asserting `CompleteCycleAssemblies(Vector("ATG", "AGT")).format` returns `"ATG\nAGT"`; confirm Red.
- [x] 3.2 GREEN: create `src/main/scala/bio/domain/graph/CompleteCycleAssemblies.scala` — `final case class CompleteCycleAssemblies(strings: Vector[String])` with `def format: String = strings.mkString("\n")`; confirm Green.

## 4. Enumeration algorithm

- [x] 4.1 RED: write `CompleteCycleAssemblySpec` in `src/test/scala/bio/algorithms/graph/` covering: canonical sample returns exactly the six known strings (compare as a set via `theSameElementsAs`); every canonical string starts with `CAG`; simple cycle `AT,TG,GA` returns exactly `Vector("ATG")`; branching `CA,AT,TA,AG,GA,AC` returns the two strings `CATAGA` and `CAGATA` (set comparison); confirm Red.
- [x] 4.2 GREEN: create `src/main/scala/bio/algorithms/graph/CompleteCycleAssembly.scala` — `object CompleteCycleAssembly { def assemble(problem: CompleteCycleProblem): CompleteCycleAssemblies = ... }`. Build the edge multiset `Map[(from, to), Int]` from `read -> (read.dropRight(1), read.tail)` preserving multiplicity; fix the first edge to the first read (`start = e1.from`); enumerate complete cycles by pure functional backtracking (`extend(node, remaining)`: when remaining is empty return `List(Nil)` iff `node == start` else `Nil`; otherwise for each distinct available edge from `node`, recurse on `e.to` with the multiset decremented and prepend `e`); assemble each circuit as `firstRead ++ subsequentEdges.map(_.to.last)` taken to length = read count; `distinct` then `sorted`; wrap in `CompleteCycleAssemblies`. No `var`/`while`/mutable collections; confirm Green.
- [x] 4.3 REFACTOR: ensure the enumeration is pure/total and the de Bruijn edge idiom matches the codebase (`r.dropRight(1) -> r.tail`); document the multiplicity-preservation contrast with PCOV; rerun the spec to confirm still Green.

## 5. IO runner and Main wiring

- [x] 5.1 Create `src/main/scala/bio/problems/GREPProb.scala` — `solve(): IO[Unit]` reading `grep_data.txt` (PCOVProb-style `foldRight` into `Either[String, Vector[DnaString]]`), validating into `CompleteCycleProblem`, running `CompleteCycleAssembly.assemble`, and printing `result.format`; on read/validation error print a descriptive message via `IO.println` rather than throwing.
- [x] 5.2 Wire `GREPProb.solve()` into `bio.Main` (PCOV/ASMQ-style runner line) and confirm `sbt run` prints six lines, each beginning with `CAG`.

## 6. Verification

- [x] 6.1 Run the full suite `sbt test` and confirm all tests pass (count increased by the new specs); confirm no `var`/`while`/mutable collections were introduced (this is a graph-enumeration algorithm, not an alignment-family DP).
