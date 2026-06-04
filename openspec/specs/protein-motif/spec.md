# protein-motif Specification

## Purpose

Finds every location at which a protein motif occurs within a protein sequence
(Rosalind spec — MPRT, "Finding a Protein Motif"). A motif is given in shorthand
notation where a bare residue `X` matches that residue, a group `[XY…]` matches
any listed residue, and a negation `{X…}` matches any residue not listed.
Provides a `MotifElement` ADT for a single motif position, a `ProteinMotif`
domain type parsed from shorthand through a smart constructor, a
`ProteinMotifError` ADT enumerating parse failures, a `MotifLocations` result
type, and the pure `MotifSearch.findLocations` algorithm that reports all
1-based start positions (including overlaps).

## Requirements

### Requirement: Motif element ADT

The system SHALL provide a `MotifElement` sealed ADT in `bio.domain.protein` modelling a single motif position, with a `matches(c: Char): Boolean` method. It SHALL include `OneOf(residues: Set[Char])` (matching any residue in the set) and `NoneOf(residues: Set[Char])` (matching any residue not in the set).

#### Scenario: OneOf matches a listed residue and rejects an unlisted one
- **WHEN** `OneOf(Set('S', 'T')).matches` is queried with `'S'` and with `'A'`
- **THEN** it returns `true` for `'S'` and `false` for `'A'`

#### Scenario: NoneOf rejects a listed residue and matches an unlisted one
- **WHEN** `NoneOf(Set('P')).matches` is queried with `'P'` and with `'A'`
- **THEN** it returns `false` for `'P'` and `true` for `'A'`

### Requirement: Protein motif type and parser

The system SHALL provide a `ProteinMotif` domain type in `bio.domain.protein` holding an ordered `Vector[MotifElement]`, constructed only through a smart constructor `parse(shorthand: String): Either[ProteinMotifError, ProteinMotif]`. The parser SHALL translate a bare residue `X` to `OneOf(Set('X'))`, a group `[XY…]` to `OneOf` of the listed residues, and a negation `{X…}` to `NoneOf` of the listed residues. It SHALL be a `sealed abstract case class` (no public `apply`/`copy`).

#### Scenario: Parses the N-glycosylation motif
- **WHEN** `ProteinMotif.parse("N{P}[ST]{P}")` is called
- **THEN** it returns a `Right` whose elements are `OneOf(Set('N'))`, `NoneOf(Set('P'))`, `OneOf(Set('S','T'))`, `NoneOf(Set('P'))`

#### Scenario: Rejects an empty motif
- **WHEN** `ProteinMotif.parse("")` is called
- **THEN** it returns a `Left` holding `ProteinMotifError.EmptyMotif`

#### Scenario: Rejects an unterminated group
- **WHEN** `ProteinMotif.parse("[ST")` is called
- **THEN** it returns a `Left` holding `ProteinMotifError.UnterminatedGroup(0)`

#### Scenario: Rejects an unexpected character
- **WHEN** `ProteinMotif.parse("A]B")` is called
- **THEN** it returns a `Left` holding `ProteinMotifError.UnexpectedCharacter(']', 1)`

#### Scenario: Cannot be constructed via a public companion apply
- **WHEN** `bio.domain.protein.ProteinMotif(Vector.empty)` is referenced in source
- **THEN** the code does not compile

### Requirement: Protein motif error ADT

The system SHALL provide a `ProteinMotifError` sealed ADT in `bio.domain.protein` enumerating the parse failures: `EmptyMotif`, `UnterminatedGroup(start: Int)`, and `UnexpectedCharacter(char: Char, index: Int)`.

#### Scenario: Reports the index of an unexpected character
- **WHEN** the character `}` at index 3 is rejected during parsing
- **THEN** the error is `ProteinMotifError.UnexpectedCharacter('}', 3)`

### Requirement: Motif locations result type

The system SHALL provide a `MotifLocations` result type in `bio.domain.protein` holding a protein `id` and the 1-based match `positions` (`Vector[Int]`), and exposing a `format: String` that renders the id on the first line and the positions space-separated on the second.

#### Scenario: Formats the id and positions
- **WHEN** a `MotifLocations("B5ZC00", Vector(85, 118, 142))` is formatted
- **THEN** `format` returns `"B5ZC00\n85 118 142"`

### Requirement: Motif search algorithm

The system SHALL provide a `MotifSearch` algorithm in `bio.algorithms.protein` with a pure, total method `findLocations(motif: ProteinMotif, protein: String): Vector[Int]`. It SHALL return, in ascending order, every 1-based start position at which the motif matches the protein sequence, including overlapping matches.

#### Scenario: Finds all motif occurrences including overlaps
- **WHEN** `findLocations` is run with the motif `N{P}[ST]{P}` on the protein `NQSANQTA`
- **THEN** the result is `Vector(1, 5)`

#### Scenario: Returns no positions when the motif is absent
- **WHEN** `findLocations` is run with the motif `N{P}[ST]{P}` on the protein `AAAA`
- **THEN** the result is empty

#### Scenario: Respects the negated and grouped positions
- **WHEN** `findLocations` is run with the motif `N{P}[ST]{P}` on the protein `NPSA`
- **THEN** the result is empty (the second position is `P`, excluded by `{P}`)

#### Scenario: Returns no positions when the protein is shorter than the motif
- **WHEN** `findLocations` is run with the motif `N{P}[ST]{P}` on the protein `NQ`
- **THEN** the result is empty
