## ADDED Requirements

### Requirement: Validated ReversingSubstitutionsProblem input bundle

The system SHALL provide a validated domain type `bio.domain.analysis.ReversingSubstitutionsProblem` constructed only through a smart constructor `ReversingSubstitutionsProblem.from(tree: NewickTree, alignment: Vector[NamedSequence]): Either[ReversingSubstitutionsProblemError, ReversingSubstitutionsProblem]`. The smart constructor MUST enforce, in this order, first-failure-wins:

1. `alignment.nonEmpty`, else `EmptyAlignment`.
2. All rows of `alignment` have the same length as the first row, else `LengthMismatch(rowIndex, length, expectedLength)` for the first offending row.
3. Every row's sequence length `<= 400`, else `SequenceTooLong(rowIndex, length, 400)` for the first offending row.
4. Every character of every row is in `{A, C, G, T}`, else `InvalidCharacter(rowIndex, position, ch)` for the first offending position (row-major scan).
5. `alignment.size <= 100`, else `TooManyStrings(actual, 100)`.
6. Every internal node has a non-empty `label`, else `InternalNodeMissingLabel`.
7. Every leaf has a non-empty `label`, else `LeafMissingLabel`.
8. Every internal node has exactly 2 children, else `NonBinaryInternalNode(label, degree)` for the first offending node (root-first traversal).
9. The set of *all* node labels in the tree (internal + leaf) equals the set of alignment-row labels, else `NodeLabelMismatch(treeOnly, alignmentOnly)`.

The constructed value MUST expose `tree: NewickTree` and `alignment: Vector[NamedSequence]`. The case class MUST be `sealed abstract` so the synthesised `apply` and `copy` cannot leak around the smart constructor.

#### Scenario: Accepts the canonical Rosalind RSUB sample
- **WHEN** `ReversingSubstitutionsProblem.from` is called with tree `(((ostrich,cat)rat,mouse)dog,elephant)robot;` and seven labelled sequences `{robot=AATTG, dog=GGGCA, mouse=AAGAC, rat=GTTGT, cat=GAGGC, ostrich=GTGTC, elephant=AATTC}`
- **THEN** it returns `Right(problem)` round-tripping the tree and the alignment

#### Scenario: Rejects an empty alignment
- **WHEN** `ReversingSubstitutionsProblem.from` is called with `Vector.empty`
- **THEN** it returns `Left(ReversingSubstitutionsProblemError.EmptyAlignment)`

#### Scenario: Rejects rows of differing lengths
- **WHEN** `ReversingSubstitutionsProblem.from` is called with rows of differing lengths
- **THEN** it returns `Left(ReversingSubstitutionsProblemError.LengthMismatch(...))` with the offending row index

#### Scenario: Rejects a sequence longer than 400 bp
- **WHEN** any row has a 401-character sequence
- **THEN** it returns `Left(ReversingSubstitutionsProblemError.SequenceTooLong(_, 401, 400))`

#### Scenario: Rejects an invalid (non-DNA) character (no gap allowed)
- **WHEN** any row contains `-` or any character outside `{A, C, G, T}`
- **THEN** it returns `Left(ReversingSubstitutionsProblemError.InvalidCharacter(...))` with the offending row index, position, and character

#### Scenario: Rejects more than 100 alignment rows
- **WHEN** the alignment vector has 101 rows
- **THEN** it returns `Left(ReversingSubstitutionsProblemError.TooManyStrings(101, 100))`

#### Scenario: Rejects an unlabeled internal node
- **WHEN** the tree has an internal node with no label (e.g., the root of `(a,b);`)
- **THEN** it returns `Left(ReversingSubstitutionsProblemError.InternalNodeMissingLabel)`

#### Scenario: Rejects an unlabeled leaf
- **WHEN** the tree has a leaf with no label (e.g., a leaf parsed as an empty Newick token)
- **THEN** it returns `Left(ReversingSubstitutionsProblemError.LeafMissingLabel)`

#### Scenario: Rejects a non-binary internal node
- **WHEN** the tree has an internal node with 3 children
- **THEN** it returns `Left(ReversingSubstitutionsProblemError.NonBinaryInternalNode(label, 3))` for the first offending node

#### Scenario: Rejects when node labels and alignment labels differ
- **WHEN** the tree has node labels `{a, b, c}` and the alignment has labels `{a, b, d}`
- **THEN** it returns `Left(ReversingSubstitutionsProblemError.NodeLabelMismatch(Set("c"), Set("d")))`

#### Scenario: Companion apply does not leak
- **WHEN** code attempts to construct via `ReversingSubstitutionsProblem(tree, alignment)`
- **THEN** compilation fails

#### Scenario: copy does not leak
- **WHEN** code attempts to mutate via `problem.copy(...)`
- **THEN** compilation fails

### Requirement: ReversingSubstitution output record

The system SHALL provide a domain type `bio.domain.analysis.ReversingSubstitution` carrying:

- `firstChangeSpecies: String` — the label of node `t` (the child of the first-substitution edge `(s, t)`).
- `reversionSpecies: String` — the label of node `w` (the child of the reversion edge `(v, w)`).
- `position: Int` — 1-indexed position in the alignment at which the reversion occurs.
- `originalSymbol: Char` — `s[i]`.
- `substitutedSymbol: Char` — `t[i]`.
- `revertedSymbol: Char` — `w[i]` (always equal to `originalSymbol` by construction, but kept explicit so the output formatter can render `originalSymbol->substitutedSymbol->revertedSymbol` directly).

`ReversingSubstitution` MUST be a plain `final case class`.

#### Scenario: Constructs with named fields
- **WHEN** code calls `ReversingSubstitution(firstChangeSpecies = "dog", reversionSpecies = "mouse", position = 1, originalSymbol = 'A', substitutedSymbol = 'G', revertedSymbol = 'A')`
- **THEN** the result exposes those six fields and is value-equal to another instance with the same fields

### Requirement: ReversingSubstitutions.findAll enumerates every reversion

The system SHALL provide an algorithm object `bio.algorithms.analysis.ReversingSubstitutions` with a method `findAll(problem: ReversingSubstitutionsProblem): Vector[ReversingSubstitution]` that returns every reversing substitution in the tree.

The algorithm MUST, for each alignment position `i ∈ [0, L)` and each directed parent-child edge `(s, t)` of the tree where `s[i] != t[i]`:

1. Let `X = s[i]` (original) and `Y = t[i]` (substituted).
2. DFS from `t` into its descendants. At each node `u` known to satisfy `u[i] == Y`, examine every child `c`:
   - If `c[i] == Y`: continue DFS into `c`.
   - If `c[i] == X`: emit `ReversingSubstitution(t.label, c.label, i + 1, X, Y, X)`. Do NOT recurse into `c`.
   - Otherwise (`c[i]` is neither `X` nor `Y`): stop this branch.

The returned `Vector` MUST contain every reversing substitution exactly once — no duplicates, no omissions. Output ordering is implementation-defined (Rosalind accepts any order); a deterministic order is acceptable.

#### Scenario: Canonical Rosalind RSUB sample (5 reversions)
- **WHEN** `ReversingSubstitutions.findAll` is called with tree `(((ostrich,cat)rat,mouse)dog,elephant)robot;` and the seven sample sequences
- **THEN** the returned set equals exactly `{
    ReversingSubstitution("dog", "mouse", 1, 'A', 'G', 'A'),
    ReversingSubstitution("dog", "mouse", 2, 'A', 'G', 'A'),
    ReversingSubstitution("rat", "ostrich", 3, 'G', 'T', 'G'),
    ReversingSubstitution("rat", "cat", 3, 'G', 'T', 'G'),
    ReversingSubstitution("dog", "rat", 3, 'T', 'G', 'T')
  }`

#### Scenario: Two-node tree never has a reversion
- **WHEN** `ReversingSubstitutions.findAll` is called with a tree `(a)r;` and matching sequences (only a single edge `r → a`)
- **THEN** the result is `Vector.empty` because a single edge cannot contain a reversion

#### Scenario: Identical strings throughout produce no reversions
- **WHEN** `ReversingSubstitutions.findAll` is called with a tree where every node has the same DNA string
- **THEN** the result is `Vector.empty`

#### Scenario: A single position with a clean `A → G → A` chain reports one reversion
- **WHEN** the tree is `(b)a;(c)b;` parsed as a chain `a → b → c` with `a = "A"`, `b = "G"`, `c = "A"` (sequence length 1)
- **THEN** the result contains exactly `ReversingSubstitution("b", "c", 1, 'A', 'G', 'A')`

#### Scenario: An intermediate substitution to a third symbol breaks the reversion chain
- **WHEN** the tree is a four-node chain `a → b → c → d` with `a = "A"`, `b = "G"`, `c = "T"`, `d = "A"` (substituted value `G` does not persist along the path)
- **THEN** the result does NOT contain a reversion at position 1 with `firstChangeSpecies == "b"` (because `c[0] = T ≠ G`, breaking condition 3)
