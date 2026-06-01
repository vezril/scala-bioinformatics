## 1. Error ADT — GenomeAssemblyReadsProblemError

- [x] 1.1 RED: write `GenomeAssemblyReadsProblemErrorSpec` asserting the sealed trait exposes `EmptyReadCollection` (case object), `ReadTooShort(index, length, min)`, `ReadTooLong(index, length, max)`, `InconsistentLength(index, length, expected)`; confirm it fails to compile (not found: type/value).
- [x] 1.2 GREEN: create `bio.domain.graph.GenomeAssemblyReadsProblemError` sealed trait with the four cases; confirm the spec passes.
- [x] 1.3 REFACTOR: review for convention compliance; rerun the spec.

## 2. Validated bundle — GenomeAssemblyReadsProblem

- [x] 2.1 RED: write `GenomeAssemblyReadsProblemSpec` covering accept-sample, empty, too-short, too-long, inconsistent-length, first-failure-wins, and `assertDoesNotCompile` for public `apply`/`copy`; confirm RED.
- [x] 2.2 GREEN: create `bio.domain.graph.GenomeAssemblyReadsProblem` as a `sealed abstract case class` wrapping `Vector[DnaString]` with `MinLength = 2`, `MaxLength = 50`, smart constructor `from` validating empty → too-short → too-long → inconsistent (first-failure-wins), constructed via anonymous subclass; confirm the spec passes.
- [x] 2.3 REFACTOR: review for convention compliance; rerun the spec.

## 3. Algorithm — GenomeAssemblyReads.assemble

- [x] 3.1 RED: write `GenomeAssemblyReadsSpec` covering canonical `AATCTGT`, rotation/strand-equivalence to `GATTACA`, duplicate-read invariance, and both-strands-supplied invariance; confirm RED.
- [x] 3.2 GREEN: implement `bio.algorithms.graph.GenomeAssemblyReads.assemble` — augment reads with reverse complements, search descending k-mer sizes for the de Bruijn graph that is exactly two reverse-complement directed cycles, emit the lexicographically smallest minimal rotation across the two cycles as a `CyclicSuperstring`; confirm the spec passes.
- [x] 3.3 REFACTOR: ensure purity/totality (no `var`/mutable/imperative loops); rerun the spec.

## 4. IO runner + Main wiring — GASMProb

- [x] 4.1 Create `bio.problems.GASMProb` reading `gasm_data.txt` (newline-separated reads), validating into a `GenomeAssemblyReadsProblem`, assembling, and printing the formatted result via `IO`, with a printed error on invalid input.
- [x] 4.2 Wire `GASMProb.solve()` as the active runner in `Main.scala` and verify it prints the assembled chromosome for the dataset.

## 5. Verification

- [x] 5.1 Run the full `sbt test` suite and confirm all specs pass with no regressions.
