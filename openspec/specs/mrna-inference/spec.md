## Purpose
Define the framework's first reverse-translation count: `InferMRna.count(protein: ProteinString): Int` returning the number of distinct mRNA strings that could have produced `protein` under the standard RNA genetic code, modulo 1,000,000. Codon degeneracies and the stop-codon count are *derived* from `GeneticCode.translate`, keeping the spec-8 genetic code as the single source of truth for both forward (translation) and reverse (counting) directions. Serves the Rosalind "Inferring mRNA from Protein" problem.

## Requirements

### Requirement: InferMRna.count returns the number of mRNA candidates modulo 1,000,000
The system SHALL provide `InferMRna.count(protein: ProteinString): Int` returning the number of distinct mRNA strings that could have translated to `protein` under the standard RNA genetic code, modulo `1_000_000`. The factor for each amino acid SHALL be its codon degeneracy as defined by `GeneticCode.translate`; the final factor SHALL be the number of stop codons (3 in the standard code, also derived from `GeneticCode.translate`). The algorithm SHALL reside in the `bio.algorithms.protein` package. The function SHALL be total — every `ProteinString` produces a defined `Int` in `[0, 999_999]`.

#### Scenario: Rosalind sample "MA" produces 12
- **WHEN** `InferMRna.count(<ProteinString of "MA">)` is called
- **THEN** the result is `12`

#### Scenario: Empty protein produces 3 (only the stop-codon factor contributes)
- **WHEN** `InferMRna.count(<empty ProteinString>)` is called
- **THEN** the result is `3`

#### Scenario: Single Methionine produces 3 (1 codon for M × 3 stop codons)
- **WHEN** `InferMRna.count(<ProteinString of "M">)` is called
- **THEN** the result is `3`

#### Scenario: Single Tryptophan produces 3 (1 codon for W × 3 stop codons)
- **WHEN** `InferMRna.count(<ProteinString of "W">)` is called
- **THEN** the result is `3`

#### Scenario: Single Leucine produces 18 (6 codons for L × 3 stop codons)
- **WHEN** `InferMRna.count(<ProteinString of "L">)` is called
- **THEN** the result is `18`

#### Scenario: Single Arginine produces 18 (6 codons for R × 3 stop codons)
- **WHEN** `InferMRna.count(<ProteinString of "R">)` is called
- **THEN** the result is `18`

#### Scenario: Two consecutive Leucines produce 108 (6 × 6 × 3)
- **WHEN** `InferMRna.count(<ProteinString of "LL">)` is called
- **THEN** the result is `108`

#### Scenario: Spec-8 sample protein "MAMAPRTEINSTRING" produces 102976 (= 191102976 mod 1000000)
- **WHEN** `InferMRna.count(<ProteinString of "MAMAPRTEINSTRING">)` is called
- **THEN** the result is `102976`

#### Scenario: Modulo wraps for an 8-Leucine protein (6^8 × 3 = 5038848 mod 1000000 = 38848)
- **WHEN** `InferMRna.count(<ProteinString of "LLLLLLLL">)` is called
- **THEN** the result is `38848`

#### Scenario: All-Methionine protein remains at 3 regardless of length (since 1^n × 3 = 3)
- **WHEN** `InferMRna.count(<ProteinString of "MMMMMMMMMM">)` is called (length 10)
- **THEN** the result is `3`
