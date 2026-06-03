## Why

Rosalind problem LEXV ("Ordering Strings of Varying Length Lexicographically") gives an ordered alphabet 𝒜 of at most 12 symbols and a positive integer `n ≤ 4`, and asks for **all** strings of length 1 … `n` over 𝒜, listed in the varying-length lexicographic order (where a string precedes any longer string that extends it — `APPLE < APPLET`). It is the varying-length sibling of LEXF (which enumerates fixed-length k-mers): the ordering is exactly a pre-order depth-first traversal of the alphabet tree to depth `n`.

## What Changes

- Introduce a validated `LexOrderProblem` domain type pairing an ordered `alphabet` (≤ 12 distinct symbols) with a maximum length `n` (1 … 4).
- Introduce a `LexOrderProblemError` ADT for the invariants (empty alphabet; too many symbols; duplicate symbol; non-positive length; length over the cap).
- Introduce a `LexOrdering` result type holding the ordered strings, with a `format` (one per line).
- Introduce a `VaryingLengthLexOrder` algorithm enumerating every string of length 1 … `n` via a pre-order DFS over the alphabet tree (the given alphabet order defines the symbol order).
- Add a `LEXVProb` runner reading the alphabet and `n` from `lexv_data.txt` and printing the strings through `IO`.

## Capabilities

### New Capabilities
- `varying-length-lex-order`: Enumerate all strings of length at most `n` over an ordered alphabet, in varying-length lexicographic order (Rosalind LEXV).

### Modified Capabilities
<!-- None. LEXV adds a new capability without changing any existing requirement. -->

## Impact

- **New domain types** (`bio.domain.combinatorics`): `LexOrderProblem`, `LexOrderProblemError`, `LexOrdering` (result).
- **New algorithm** (`bio.algorithms.combinatorics.VaryingLengthLexOrder`) — a pure pre-order DFS to depth `n`, mirroring `EnumerateKmers` (LEXF) generalised to all lengths up to `n`.
- **New runner** (`bio.problems.LEXVProb`) reading `src/main/scala/resources/lexv_data.txt`.
- **Tests**: new specs under `bio.domain.combinatorics` and `bio.algorithms.combinatorics`. No existing tests change.
