## Why

Spec 13 of the project brief — "Inferring mRNA from Protein" — is the framework's first *reverse* translation problem and its first explicit use of *modular arithmetic*. Given a protein, count how many distinct mRNA strings could have produced it (under the standard genetic code), modulo 1,000,000. This reuses the codon table introduced in spec 8 but applied in the opposite direction: instead of `Codon → AminoAcid`, we ask "how many codons map to each amino acid?", multiply those degeneracies along the protein, then multiply by 3 for the stop-codon choices.

Beyond solving the Rosalind problem, this change validates that the genetic-code table set up in spec 8 is the single source of truth for both directions — forward (translation) and reverse (counting). No new codon data is added; everything is *derived* from `GeneticCode.translate`.

## What Changes

- **NEW** `InferMRna` algorithm object in `bio.algorithms.protein`
- **NEW** `InferMRna.count(protein: ProteinString): Int` — total function returning the number of distinct mRNA strings that could have produced `protein`, modulo `1_000_000`. Counts the codon multiplicities for each amino acid via `GeneticCode.translate` (no hard-coded reverse table), multiplies them, then multiplies by the number of stop codons (also derived). Takes modulo after each multiplication to prevent overflow.
- **NO** new domain types — bare `Int` return matches `MotifLocations` / `HammingDistance` precedent
- **NO** new error types — the function is total over all `ProteinString`s
- **NO** modifications to `GeneticCode`, `AminoAcid`, or `ProteinString`; the inverse codon counts are derived in `InferMRna` on first use

## Capabilities

### New Capabilities

- `mrna-inference`: The `InferMRna.count(protein: ProteinString): Int` algorithm computing, modulo 1,000,000, the number of distinct mRNA candidates that could have translated to the given protein under the standard RNA genetic code. The codon degeneracies and the stop-codon count are derived from `GeneticCode.translate` rather than hard-coded, so the table remains a single source of truth.

### Modified Capabilities

None.

## Impact

- New file: `src/main/scala/bio/algorithms/protein/InferMRna.scala`
- New test file: `src/test/scala/bio/algorithms/protein/InferMRnaSpec.scala`
- No changes to existing files
- No new external dependencies
- All existing 244 tests continue passing
