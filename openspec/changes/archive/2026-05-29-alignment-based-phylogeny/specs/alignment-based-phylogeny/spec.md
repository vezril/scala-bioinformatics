## ADDED Requirements

### Requirement: NamedSequence record

The system SHALL provide a domain type `bio.domain.analysis.NamedSequence` carrying:

- `label: String` — the FASTA-style species identifier.
- `sequence: String` — the (possibly gapped) sequence over the alphabet `{A, C, G, T, -}`.

`NamedSequence` MUST be a plain `final case class` (free `apply`, `copy`, equality, and pattern-matching — no smart constructor). It is used for both input alignment rows and output internal-node assignments. Alphabet and length constraints are enforced by the *containing* `AlignmentBasedPhylogenyProblem`, not by `NamedSequence` itself.

#### Scenario: Constructs with named fields
- **WHEN** code calls `NamedSequence(label = "rat", sequence = "AC")`
- **THEN** the resulting value has those two fields exposed and is value-equal to another instance with the same fields

### Requirement: Validated AlignmentBasedPhylogenyProblem input bundle

The system SHALL provide a validated domain type `bio.domain.analysis.AlignmentBasedPhylogenyProblem` constructed only through a smart constructor `AlignmentBasedPhylogenyProblem.from(tree: NewickTree, alignment: Vector[NamedSequence]): Either[AlignmentBasedPhylogenyProblemError, AlignmentBasedPhylogenyProblem]`. The smart constructor MUST enforce, in this order, first-failure-wins:

1. `alignment.nonEmpty`, else `EmptyAlignment`.
2. All rows of `alignment` have the same length as the first row, else `LengthMismatch(rowIndex, length, expectedLength)` for the first offending row.
3. Every row's sequence length `<= 300`, else `SequenceTooLong(rowIndex, length, 300)` for the first offending row.
4. Every character of every row is in `{A, C, G, T, -}`, else `InvalidCharacter(rowIndex, position, ch)` for the first offending position (row-major scan).
5. The tree has `<= 500` leaves, else `TooManyLeaves(actual, 500)`.
6. Every internal node has a non-empty `label`, else `InternalNodeMissingLabel`.
7. Every internal node has exactly 2 children, else `NonBinaryInternalNode(label, degree)` for the first offending node (root-first traversal).
8. The *set* of leaf labels in the tree equals the *set* of alignment-row labels, else `LeafLabelMismatch(treeOnly, alignmentOnly)` where each side is the symmetric-difference set.

The constructed value MUST expose `tree: NewickTree` and `alignment: Vector[NamedSequence]`. The case class MUST be `sealed abstract` so the synthesised `apply` and `copy` cannot leak around the smart constructor.

#### Scenario: Accepts the canonical Rosalind ALPH sample
- **WHEN** `AlignmentBasedPhylogenyProblem.from` is called with the Rosalind sample tree `(((ostrich,cat)rat,(duck,fly)mouse)dog,(elephant,pikachu)hamster)robot;` and the six-row alignment `{ostrich=AC, cat=CA, duck=T-, fly=GC, elephant=-T, pikachu=AA}`
- **THEN** it returns `Right(problem)` where the tree and the alignment round-trip into the wrapper

#### Scenario: Rejects an empty alignment
- **WHEN** `AlignmentBasedPhylogenyProblem.from` is called with any tree and `Vector.empty`
- **THEN** it returns `Left(AlignmentBasedPhylogenyProblemError.EmptyAlignment)`

#### Scenario: Rejects an alignment with row-length mismatch
- **WHEN** `AlignmentBasedPhylogenyProblem.from` is called with rows `{a=AC, b=ACC}` (row 1 has length 3, row 0 has length 2)
- **THEN** it returns `Left(AlignmentBasedPhylogenyProblemError.LengthMismatch(1, 3, 2))`

#### Scenario: Rejects an over-length sequence
- **WHEN** `AlignmentBasedPhylogenyProblem.from` is called with a row whose sequence is 301 characters
- **THEN** it returns `Left(AlignmentBasedPhylogenyProblemError.SequenceTooLong(0, 301, 300))`

#### Scenario: Rejects an invalid character
- **WHEN** `AlignmentBasedPhylogenyProblem.from` is called with a row containing `X`
- **THEN** it returns `Left(AlignmentBasedPhylogenyProblemError.InvalidCharacter(...))` with the offending row index, position, and character

#### Scenario: Rejects a tree with an unlabeled internal node
- **WHEN** `AlignmentBasedPhylogenyProblem.from` is called with a tree where the root is unlabeled (e.g. `(a,b);`)
- **THEN** it returns `Left(AlignmentBasedPhylogenyProblemError.InternalNodeMissingLabel)`

#### Scenario: Rejects a tree with a non-binary internal node
- **WHEN** `AlignmentBasedPhylogenyProblem.from` is called with a tree where an internal node has 3 children
- **THEN** it returns `Left(AlignmentBasedPhylogenyProblemError.NonBinaryInternalNode(...))` for the first offending node

#### Scenario: Rejects when leaf labels do not match alignment labels
- **WHEN** `AlignmentBasedPhylogenyProblem.from` is called with a tree whose leaves are `{a, b, c}` and an alignment with labels `{a, b, d}`
- **THEN** it returns `Left(AlignmentBasedPhylogenyProblemError.LeafLabelMismatch(...))` reporting `{c}` and `{d}` as the offending sets

#### Scenario: Companion apply does not leak
- **WHEN** code attempts to construct via `AlignmentBasedPhylogenyProblem(tree, alignment)`
- **THEN** compilation fails (the synthesised apply is hidden behind the smart constructor)

#### Scenario: copy does not leak
- **WHEN** code attempts to mutate via `problem.copy(alignment = other)`
- **THEN** compilation fails (the synthesised copy is hidden behind the smart constructor)

### Requirement: AlignmentBasedPhylogeny output ADT

The system SHALL provide a domain type `bio.domain.analysis.AlignmentBasedPhylogeny` carrying:

- `totalDistance: Int` — the minimum value of `d_H(T) = Σ_{edges (u, v)} d_H(s_u, s_v)` over all internal-node assignments.
- `internalAssignments: Vector[NamedSequence]` — one `NamedSequence` per internal node of the tree, with `label` matching the tree's internal-node label and `sequence` being the assigned DNA-with-gaps string. Ordered by deterministic pre-order traversal (root-first, left-child before right-child).

`AlignmentBasedPhylogeny` MUST be a plain `final case class`.

#### Scenario: Constructs with named fields
- **WHEN** code calls `AlignmentBasedPhylogeny(totalDistance = 8, internalAssignments = Vector(NamedSequence("robot", "AC"), NamedSequence("dog", "AC"), NamedSequence("rat", "AC"), NamedSequence("mouse", "TC"), NamedSequence("hamster", "AT")))`
- **THEN** the resulting value has those fields exposed and is value-equal to another instance with the same fields

### Requirement: AlignmentBasedPhylogeny.solve returns the minimum-parsimony assignment

The system SHALL provide an algorithm object `bio.algorithms.analysis.AlignmentBasedPhylogeny` with a method `solve(problem: AlignmentBasedPhylogenyProblem): AlignmentBasedPhylogeny` that returns the minimum-parsimony internal-node assignment via the classical *Sankoff* small-parsimony dynamic program.

The implementation MUST process each column of the alignment independently, applying the Sankoff DP over the 5-symbol alphabet `{A, C, G, T, -}`:

1. **Bottom-up.** At each leaf, `cost(leaf, c)` is `0` if `c` equals the leaf's column character, else `+∞`. At each internal node `u` with children `L` and `R`, `cost(u, c) = min_{c_L} (cost(L, c_L) + δ(c, c_L)) + min_{c_R} (cost(R, c_R) + δ(c, c_R))`, where `δ(a, b) = 0` if `a == b` else `1`.
2. **Top-down.** At the root, pick `c` minimising `cost(root, c)`. At each internal node, the chosen symbol for each child is the symbol that realised the inner-min for the parent's chosen symbol. Recurse to leaves (which are forced).
3. **Total distance.** `totalDistance` is the sum across all columns of `min_c cost(root, c)`.
4. **Output.** Concatenate the per-column chosen symbols at each internal node to form that node's full DNA string. Return `internalAssignments` in deterministic pre-order traversal.

The returned `AlignmentBasedPhylogeny.internalAssignments` MUST satisfy:

1. Exactly one entry per internal node.
2. Every `sequence` has the same length as the input alignment rows.
3. Pre-order traversal ordering (root first, then recursively left subtree, then right subtree).
4. For every leaf, the leaf's input row is unchanged.
5. The reported `totalDistance` equals the actual sum-of-Hamming-over-edges of the chosen labels — including for both directions of every undirected tree edge.

#### Scenario: Canonical Rosalind ALPH sample (distance 8)
- **WHEN** `AlignmentBasedPhylogeny.solve` is called with the Rosalind sample tree and alignment
- **THEN** the result has `totalDistance == 8` and produces an `internalAssignments` vector of length 5 with one entry per internal node label `{rat, mouse, dog, hamster, robot}`, ordered by pre-order traversal, where the assignment when combined with the leaf rows produces a tree whose edge-sum Hamming distance equals 8

#### Scenario: All leaves identical produces zero distance and matching internal labels
- **WHEN** `AlignmentBasedPhylogeny.solve` is called with the tree `((a,b)c,(d,e)f)g;` and an alignment where all five leaves have sequence `AC`
- **THEN** `totalDistance == 0` and every internal node (`c`, `f`, `g`) is assigned `"AC"`

#### Scenario: Two-leaf tree gives the Hamming distance of the inputs
- **WHEN** `AlignmentBasedPhylogeny.solve` is called with the tree `(a,b)r;` and alignment `{a=AC, b=AT}`
- **THEN** `totalDistance == 1` and the internal-assignment vector has length 1 with `label == "r"`, and the assigned sequence is one of `"AC"` or `"AT"` (any DP optimum)

#### Scenario: Single-column gap-vs-non-gap input
- **WHEN** `AlignmentBasedPhylogeny.solve` is called with the tree `(a,b)r;` and alignment `{a=A, b=-}`
- **THEN** `totalDistance == 1` (one edge with mismatch) and the root assignment is either `"A"` or `"-"` (any DP optimum)

#### Scenario: The reported totalDistance matches the sum-of-Hamming-over-edges of the returned assignments
- **WHEN** `AlignmentBasedPhylogeny.solve` is called with any valid input
- **THEN** summing per-column Hamming distance across every edge of the tree (using the reported assignments at internal nodes and the input rows at leaves) yields a value equal to the reported `totalDistance`
