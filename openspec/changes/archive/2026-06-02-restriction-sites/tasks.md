## 1. RestrictionSiteProblemError ADT

- [x] 1.1 RED: write `RestrictionSiteProblemErrorSpec` asserting `SequenceTooLong(length, max)` exists as a `RestrictionSiteProblemError` and carries its fields; confirm it fails to compile (type not found)
- [x] 1.2 GREEN: create `bio/domain/nucleic/RestrictionSiteProblemError.scala` (sealed trait + `SequenceTooLong`) until the spec compiles and passes
- [x] 1.3 REFACTOR: tidy and confirm the new spec is green

## 2. RestrictionSiteProblem domain type

- [x] 2.1 RED: write `RestrictionSiteProblemSpec` — accepts the sample `TCAATGCATGCGGGTCTATATGCAT` (preserving `dna`), accepts an empty `DnaString`, accepts a 1000 bp sequence, rejects a 1001 bp sequence → `SequenceTooLong(1001, 1000)`, and `assertDoesNotCompile` for `apply`/`copy`; confirm RED
- [x] 2.2 GREEN: create `bio/domain/nucleic/RestrictionSiteProblem.scala` as a `sealed abstract case class` wrapping `DnaString` with `from(dna)` (reject length > 1000); make the spec pass
- [x] 2.3 REFACTOR: ensure MaxLength is a single named constant; rerun green

## 3. RestrictionSite + RestrictionSites result types

- [x] 3.1 RED: write `RestrictionSitesResultSpec` (domain) asserting `RestrictionSite(position, length)` exposes its fields, the sample 8-site result `format` is the eight `"pos len"` lines joined by `\n` (first `4 6`, last `21 4`), and an empty result `format` is `""`; confirm RED
- [x] 3.2 GREEN: create `bio/domain/nucleic/RestrictionSite.scala` (`position: Int`, `length: Int`) and `bio/domain/nucleic/RestrictionSites.scala` (`sites: Vector[RestrictionSite]`, `format` joining `"$position $length"` lines with `\n`); make the spec pass
- [x] 3.3 REFACTOR: rerun green

## 4. RestrictionSites algorithm

- [x] 4.1 RED: write `RestrictionSitesAlgoSpec` (in `bio.algorithms.nucleic`) — sample → exactly the eight ordered sites; `AAAAAAAA` → empty; `GTAC` → exactly `(1,4)`; `GC` → empty; and every returned length ∈ `{4,6,8,10,12}` for the sample; confirm RED
- [x] 4.2 GREEN: create `bio/algorithms/nucleic/RestrictionSites.scala` (alias the domain result as `Result`) with a `for` comprehension over position (ascending) then even length 4–12, guarded by `position + length <= n`, emitting a site when `DnaReverseComplement.reverseComplement(sub).value == sub.value` (lift the slice via `DnaString.unsafeFrom`); pure FP, no `var`/`while`/`Array`; make the spec pass
- [x] 4.3 REFACTOR: confirm purity and a single even-length range constant; rerun green

## 5. REVPProb runner + wiring

- [x] 5.1 GREEN: create `bio/problems/REVPProb.scala` reading `revp_data.txt` via `FastaFileReader`, taking the first record's `DnaString`, validating into a `RestrictionSiteProblem`, locating sites, and printing `format`; missing-record / FASTA / validation errors printed via `IO.println`, never thrown
- [x] 5.2 GREEN: point `bio/Main.scala` at `REVPProb.solve()`; run `sbt run` and confirm it prints the eight pairs starting `4 6`
- [x] 5.3 REFACTOR: run the full `sbt test` suite and confirm all green
