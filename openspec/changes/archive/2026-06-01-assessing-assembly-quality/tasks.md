## 1. Error ADT — ContigCollectionError

- [x] 1.1 RED: write `ContigCollectionErrorSpec` asserting the sealed trait exposes `EmptyContigCollection` (case object), `TooManyContigs(count, max)`, `EmptyContig(index)`, `ExceedsTotalLength(total, max)`; confirm it fails to compile (not found: type/value).
- [x] 1.2 GREEN: create `bio.domain.assembly.ContigCollectionError` sealed trait with the four cases; confirm the spec passes.
- [x] 1.3 REFACTOR: review for convention compliance; rerun the spec.

## 2. Validated bundle — ContigCollection

- [x] 2.1 RED: write `ContigCollectionSpec` covering accept-sample, empty, too-many, empty-contig, exceeds-total-length, first-failure-wins, and `assertDoesNotCompile` for public `apply`/`copy`; confirm RED.
- [x] 2.2 GREEN: create `bio.domain.assembly.ContigCollection` as a `sealed abstract case class` wrapping `Vector[DnaString]` with `MaxContigs = 1000`, `MaxTotalLength = 50000`, smart constructor `from` validating empty → too-many → first empty contig → exceeds-total-length (first-failure-wins), constructed via anonymous subclass; confirm the spec passes.
- [x] 2.3 REFACTOR: review for convention compliance; rerun the spec.

## 3. Result type — AssemblyQuality

- [x] 3.1 RED: write `AssemblyQualitySpec` asserting it carries `n50`/`n75` and `format` renders `"7 6"`; confirm RED.
- [x] 3.2 GREEN: create `bio.domain.assembly.AssemblyQuality(n50: Int, n75: Int)` with `def format: String = s"$n50 $n75"`; confirm the spec passes.
- [x] 3.3 REFACTOR: review for convention compliance; rerun the spec.

## 4. Algorithm — AssemblyStatistics

- [x] 4.1 RED: write `AssemblyStatisticsSpec` covering `nStatistic` N50=7 / N75=6 on the sample, single-contig, all-equal-length, and `assess` returning `AssemblyQuality(7, 6)`; confirm RED.
- [x] 4.2 GREEN: implement `bio.algorithms.assembly.AssemblyStatistics` with `nStatistic(contigs, percentile)` (descending-length cumulative scan, exact `cumulative * 100 >= percentile * total` comparison) and `assess(contigs) = AssemblyQuality(nStatistic(_, 50), nStatistic(_, 75))`; confirm the spec passes.
- [x] 4.3 REFACTOR: ensure purity/totality (no `var`/mutable/imperative loops); rerun the spec.

## 5. IO runner + Main wiring — ASMQProb

- [x] 5.1 Create `bio.problems.ASMQProb` reading `asmq_data.txt` (newline-separated contigs), validating into a `ContigCollection`, assessing, and printing the formatted result via `IO`, with a printed error on invalid input.
- [x] 5.2 Wire `ASMQProb.solve()` as the active runner in `Main.scala` and verify it prints `7 6` for the dataset.

## 6. Verification

- [x] 6.1 Run the full `sbt test` suite and confirm all specs pass with no regressions.
