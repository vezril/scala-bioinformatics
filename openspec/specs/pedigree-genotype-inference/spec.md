# Inferring Genotype from a Pedigree

## Purpose

Compute the genotype probability distribution (AA, Aa, aa) for the root individual of a Newick pedigree tree whose leaves are known ancestor genotypes — Rosalind MEND — by propagating point-mass leaf distributions upward through bottom-up Mendelian crosses.

## Requirements

### Requirement: Pedigree problem construction

The system SHALL provide a `PedigreeProblem` domain type built from a parsed `NewickTree`, modelling a rooted binary pedigree as a `Pedigree` ADT (a `KnownAncestor` leaf carrying a `Genotype`, or an `Offspring` of two parent pedigrees). The smart constructor `from(tree)` MUST validate, first-failure-wins, that every leaf carries a valid genotype label (`AA`, `Aa`, or `aa`) and every internal node has exactly two children, returning `Either[PedigreeProblemError, PedigreeProblem]`. It MUST NOT expose a public `apply` or `copy` that could bypass validation.

#### Scenario: Builds a valid pedigree from a Newick tree

- **WHEN** `PedigreeProblem.from` is called with the parsed tree of `((Aa,aa),(Aa,Aa));`
- **THEN** it returns a `Right`

#### Scenario: Accepts a single known-genotype root

- **WHEN** `PedigreeProblem.from` is called with the parsed tree of `Aa;`
- **THEN** it returns a `Right`

#### Scenario: Rejects an unknown genotype label

- **WHEN** `PedigreeProblem.from` is called with a tree containing a leaf labelled `Bb`
- **THEN** it returns `Left(InvalidGenotype("Bb"))`

#### Scenario: Rejects a non-binary internal node

- **WHEN** `PedigreeProblem.from` is called with a tree whose internal node has three children
- **THEN** it returns `Left(NotBinary(3))`

#### Scenario: Cannot be constructed via a public apply or copy

- **WHEN** code attempts `PedigreeProblem(...)` or `.copy(...)` on a constructed instance
- **THEN** the code fails to compile

### Requirement: Genotype probabilities result rendering

The system SHALL provide a `GenotypeProbabilities` result type holding the root individual's probabilities of the `AA`, `Aa`, and `aa` genotypes, and exposing a `format: String` rendering the three values in that order, space-separated, each to three decimal places.

#### Scenario: Exposes the three probabilities

- **WHEN** a `GenotypeProbabilities` result is constructed with `0.156`, `0.5`, `0.344`
- **THEN** its `homozygousDominant`, `heterozygous`, and `homozygousRecessive` fields return `0.156`, `0.5`, and `0.344`

#### Scenario: Formats three space-separated three-decimal values

- **WHEN** `format` is called on a result holding `0.156`, `0.5`, `0.344`
- **THEN** it returns `"0.156 0.500 0.344"`

### Requirement: Genotype inference from a pedigree

The system SHALL provide an algorithm that, given a `PedigreeProblem`, returns the `GenotypeProbabilities` of the root individual. Each leaf is a point mass on its known genotype; each internal node's distribution is the Mendelian cross of its two children's distributions, where a parent transmits the `A` allele with probability `P(AA) + P(Aa)/2`, giving offspring `P(AA) = tA₁·tA₂`, `P(aa) = (1−tA₁)(1−tA₂)`, and `P(Aa)` the remainder.

#### Scenario: Computes the canonical Rosalind MEND sample

- **WHEN** the algorithm is run on the pedigree `((((Aa,aa),(Aa,Aa)),((aa,aa),(aa,AA))),Aa);`
- **THEN** the result probabilities are within `0.001` of `0.156`, `0.5`, and `0.344`

#### Scenario: Returns a point mass for a known-genotype root

- **WHEN** the algorithm is run on the pedigree `Aa;`
- **THEN** the result probabilities are `0.0`, `1.0`, and `0.0`

#### Scenario: Crosses two heterozygous parents

- **WHEN** the algorithm is run on the pedigree `(Aa,Aa);`
- **THEN** the result probabilities are within `0.001` of `0.25`, `0.5`, and `0.25`

#### Scenario: Crosses a homozygous dominant with a homozygous recessive

- **WHEN** the algorithm is run on the pedigree `(AA,aa);`
- **THEN** the result probabilities are `0.0`, `1.0`, and `0.0`
