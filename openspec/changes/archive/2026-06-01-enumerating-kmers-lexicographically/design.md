## Context

Rosalind LEXF gives an ordered alphabet of ≤10 symbols and a positive integer
`n ≤ 10`, and asks for every length-`n` string over that alphabet, listed in
lexicographic order. The alphabet's *given order* defines the lexicographic order
(the sample alphabet `A C G T` is already in the desired order).

The output is the ordered Cartesian product `𝒜ⁿ`. With ≤10 symbols and n ≤ 10 the
result is at most 10¹⁰ in the worst case, but realistic Rosalind inputs are far
smaller (the sample is 4² = 16); the algorithm is a straightforward deterministic
build. This fits the framework's `bio.*.combinatorics` package beside the existing
`Permutations`, `PartialPermutations`, and `Subsets`.

## Goals / Non-Goals

**Goals:**
- A validated `KmerEnumerationProblem` (ordered alphabet + length) with
  first-failure-wins construction.
- A pure, total `EnumerateKmers.enumerate` returning the k-mers in lexicographic
  order under the given alphabet order.
- Match the canonical sample output (16 lines `AA … TT`).

**Non-Goals:**
- Re-sorting symbols into English-alphabet order: the input order *is* the order to
  use, so we preserve it rather than sorting.
- Streaming/lazy output for pathological 10¹⁰ inputs; the Rosalind caps keep the
  eager `Vector` build acceptable, consistent with `Permutations`.

## Decisions

**1. Preserve the given alphabet order; do not sort.**
The problem defines lexicographic order *by the supplied alphabet*. Sorting into
ASCII/English order would be wrong for a re-ordered alphabet and is unnecessary for
the sample. We store `alphabet: Vector[Char]` exactly as given and enumerate using
that order.

*Alternative considered:* sort the alphabet. Rejected — changes the problem's
defined order.

**2. Build the product with a left fold, prefix-outer / symbol-inner.**
`(1 to n).foldLeft(Vector(""))((acc, _) => for { p <- acc; c <- alphabet } yield p + c)`.
Iterating `acc` (already in order) in the outer position and `alphabet` (in given
order) in the inner position yields strings in lexicographic order with the first
position most significant — exactly the required ordering — with no explicit sort.

*Alternative considered:* generate then sort. Rejected — the fold is already
ordered and avoids an O(kⁿ log) sort.

**3. Result type `KmerEnumeration(kmers)` with `format`.**
Mirrors the result-type convention (`ShortestSuperstring`, `ConsistentCharacterTable`,
`SetOperationsResult`): a thin wrapper whose `format: String` joins the k-mers one
per line for the runner to print.

**4. `KmerEnumerationProblem` validation order: empty alphabet → too-many-symbols →
duplicate-symbol → non-positive-length → length-exceeds-maximum.**
`from(alphabet, length)` reports the earliest failure. `sealed abstract case class`
prevents `apply`/`copy` from bypassing validation, proven via
`assertDoesNotCompile`.

**5. Domain placement in `bio.*.combinatorics`.**
The k-mer product is a pure counting/enumeration construct alongside permutations
and subsets; no sequence/alphabet ADT (DNA/RNA) is involved since the alphabet is
arbitrary symbols.

## Risks / Trade-offs

- **Worst-case 10¹⁰ k-mers →** out of scope for realistic Rosalind inputs; the eager
  `Vector` build matches `Permutations`' approach and the caps keep it bounded for
  graded datasets. A lazy `Iterator` variant could replace the build behind the same
  signature later if needed.
- **Duplicate symbols in the alphabet would corrupt ordering/uniqueness →** rejected
  up front with `DuplicateSymbol`, so the enumeration always operates on a clean
  ordered alphabet.
