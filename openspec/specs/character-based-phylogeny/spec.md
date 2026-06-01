## Purpose

Validates a consistent `0`/`1` character table over a taxa set and reconstructs an
unrooted binary tree, rendered in Newick, that models it per the Rosalind
"Character-Based Phylogeny" (CHBP) problem. Provides the validated
`CharacterBasedPhylogenyProblem` input bundle with its
`CharacterBasedPhylogenyProblemError` ADT and the pure construction that turns the
consistent character splits into an unrooted binary tree.

## Requirements

### Requirement: Character-phylogeny input errors are represented as a dedicated ADT

The system SHALL represent the ways a character-based-phylogeny input can be
invalid as a sealed `CharacterBasedPhylogenyProblemError` ADT with the cases
`EmptyTaxa`, `DuplicateTaxon(name)`, `ExceedsMaximumTaxa(count, max)`,
`RowLengthMismatch(rowIndex, expected, actual)`,
`InvalidCharacter(rowIndex, ch)`, and
`ConflictingCharacters(rowIndexA, rowIndexB)`, where `rowIndex` values are
zero-based positions in the supplied character rows.

#### Scenario: Error ADT enumerates the invalid-input cases

- **WHEN** the `CharacterBasedPhylogenyProblemError` ADT is inspected
- **THEN** it is a sealed type whose cases are `EmptyTaxa`, `DuplicateTaxon`
  carrying the repeated taxon name, `ExceedsMaximumTaxa` carrying the taxa count
  and the maximum, `RowLengthMismatch` carrying the offending row index with the
  expected and actual lengths, `InvalidCharacter` carrying the offending row
  index and character, and `ConflictingCharacters` carrying the two offending
  row indices

#### Scenario: Conflict error identifies the offending rows

- **WHEN** a `ConflictingCharacters` is produced for character rows at indices 0
  and 2
- **THEN** it carries the two zero-based row indices that conflict

### Requirement: Character-phylogeny problem is a validated, invariant-bearing bundle

The system SHALL provide a `CharacterBasedPhylogenyProblem` bundling an ordered
list of taxa and the validated `0`/`1` character rows describing splits over
those taxa, constructed only through a smart constructor `from(taxa, rows)` that
returns `Either[CharacterBasedPhylogenyProblemError, CharacterBasedPhylogenyProblem]`.
The constructor SHALL reject an empty taxa list, a duplicated taxon, more than 80
taxa, any character row whose length differs from the taxa count, any character
containing a symbol other than `0` or `1`, and any pair of characters whose
splits conflict (an inconsistent table); it SHALL report the first failure
encountered. The type SHALL NOT expose a public `apply` or `copy` that bypasses
validation.

#### Scenario: Valid consistent table yields a problem

- **WHEN** `from` is given taxa `[cat, dog, elephant, mouse, rabbit, rat]` and
  rows `[011101, 001101, 001100]`
- **THEN** it returns a `Right` containing the `CharacterBasedPhylogenyProblem`

#### Scenario: Empty taxa list is rejected

- **WHEN** `from` is given an empty taxa list
- **THEN** it returns `Left(EmptyTaxa)`

#### Scenario: A duplicated taxon is rejected

- **WHEN** `from` is given taxa containing the same name twice
- **THEN** it returns `Left(DuplicateTaxon(name))` for that name

#### Scenario: Too many taxa are rejected

- **WHEN** `from` is given 81 distinct taxa
- **THEN** it returns `Left(ExceedsMaximumTaxa(81, 80))`

#### Scenario: A mis-sized character row is rejected

- **WHEN** `from` is given 6 taxa and a character row of length 5
- **THEN** it returns `Left(RowLengthMismatch(rowIndex, 6, 5))` for that row

#### Scenario: A non-binary character symbol is rejected

- **WHEN** `from` is given a character row containing a character other than `0`
  or `1`
- **THEN** it returns `Left(InvalidCharacter(rowIndex, ch))` for that row and
  character

#### Scenario: Conflicting characters are rejected

- **WHEN** `from` is given taxa `[a, b, c, d]` and the conflicting rows `1100`
  (`{a,b}|{c,d}`) and `1010` (`{a,c}|{b,d}`)
- **THEN** it returns `Left(ConflictingCharacters(0, 1))`

#### Scenario: First failure wins

- **WHEN** an input is invalid in more than one way
- **THEN** the error reflects the earliest failure in the validation order
  (empty taxa → duplicate taxon → exceeds-maximum → row length → invalid
  character → conflicting characters)

#### Scenario: Construction cannot bypass validation

- **WHEN** code attempts to call a public `apply` or `copy` on
  `CharacterBasedPhylogenyProblem`
- **THEN** the code does not compile

### Requirement: An unrooted binary tree is reconstructed from the character table

The system SHALL build, from a `CharacterBasedPhylogenyProblem`, an unrooted
binary tree (a `NewickTree`) that models the character table — meaning the set of
non-trivial splits induced by the tree's internal edges equals the set of
non-trivial splits described by the characters. The construction SHALL be a pure,
total function over validated input. The tree SHALL be renderable as a Newick
string in which each taxon appears exactly once and internal nodes are
unlabelled, terminated by a semicolon.

#### Scenario: Canonical sample is modelled

- **WHEN** building the tree for taxa `[cat, dog, elephant, mouse, rabbit, rat]`
  with rows `[011101, 001101, 001100]`
- **THEN** the resulting tree's non-trivial splits are exactly `{cat,rabbit} |
  {dog,elephant,mouse,rat}`, `{elephant,mouse,rat} | {cat,dog,rabbit}`, and
  `{elephant,mouse} | {cat,dog,rabbit,rat}` — the same unrooted tree as the
  Rosalind sample output `(dog,(cat,rabbit),(rat,(elephant,mouse)));`

#### Scenario: Every taxon appears exactly once

- **WHEN** the reconstructed tree is rendered as Newick
- **THEN** each of the input taxa labels appears exactly once as a leaf and no
  other labels appear

#### Scenario: An empty character table yields a star tree

- **WHEN** building the tree from taxa with no non-trivial characters (e.g. only
  all-equal rows, or no rows at all)
- **THEN** the tree is a single internal node whose children are exactly the
  taxa leaves (no internal splits)

#### Scenario: Rendering is canonical Newick

- **WHEN** a reconstructed tree is rendered
- **THEN** leaves render as their label, internal nodes render as their
  comma-separated children wrapped in parentheses, and the whole tree is
  terminated by a single `;`
