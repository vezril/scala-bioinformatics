## 1. InferMRna algorithm (TDD)

- [x] 1.1 Write failing tests in `src/test/scala/bio/algorithms/protein/InferMRnaSpec.scala` for `InferMRna.count` covering: Rosalind sample `"MA"` → `12`, empty protein → `3`, single `"M"` → `3`, single `"W"` → `3`, single `"L"` → `18`, single `"R"` → `18`, `"LL"` → `108`, spec-8 sample `"MAMAPRTEINSTRING"` → `102976`, modulo wrap `"LLLLLLLL"` (8 Ls) → `38848` (= 6^8 × 3 mod 1_000_000), all-M of length 10 → `3`. Use a `protein` helper that calls `ProteinString.from(s).getOrElse(sys.error(...))`
- [x] 1.2 Run `sbt test` — confirm tests fail (Red) because `InferMRna` does not yet exist
- [x] 1.3 Implement `src/main/scala/bio/algorithms/protein/InferMRna.scala` in `bio.algorithms.protein`. Algorithm:
  - Define `private val Modulus: Int = 1_000_000`
  - Derive codon counts: iterate all 64 codons via a `for`-comprehension over `RnaNucleotide.{A, C, G, U}`, group by `GeneticCode.translate(codon)`, build `codonCount: Map[AminoAcid, Int]` for `AminoAcidProduct` outcomes and `stopCodonCount: Int` from the `Stop` count
  - Build `aminoAcidByCode: Map[Char, AminoAcid]` from `AminoAcid.all`
  - `def count(protein: ProteinString): Int = protein.value.foldLeft(stopCodonCount) { (acc, ch) => (acc * codonCount(aminoAcidByCode(ch))) % Modulus }`
  - Imports: `bio.domain.protein.{AminoAcid, Codon, CodonOutcome, GeneticCode, ProteinString}`, `bio.domain.nucleic.RnaNucleotide`. No `var`, no mutable collections
- [x] 1.4 Run `sbt test` — confirm `InferMRnaSpec` passes (Green)
- [x] 1.5 Refactor: confirm no `var`, no mutable collections, derivation iterates exactly 64 codons once at object init, modulo applied after each multiplication
- [x] 1.6 Run `sbt test` — confirm all tests still pass after refactor

## 2. Final Verification

- [x] 2.1 Run `sbt clean compile` — zero errors (warnings unrelated to this change are acceptable)
- [x] 2.2 Run `sbt test` — all tests pass (count higher than 244)
- [x] 2.3 Verify the new file resides at `src/main/scala/bio/algorithms/protein/InferMRna.scala` with `package bio.algorithms.protein`
- [x] 2.4 Verify the codon counts and stop-codon count are derived from `GeneticCode.translate` (not hard-coded as a parallel table) — open the file and confirm the derivation iterates `RnaNucleotide.{A, C, G, U}`
