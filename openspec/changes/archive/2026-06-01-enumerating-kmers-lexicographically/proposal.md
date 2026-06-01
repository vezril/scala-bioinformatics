## Why

Rosalind problem 63 ("Enumerating k-mers Lexicographically", LEXF) asks us to
enumerate every length-`n` string over an ordered alphabet, listed in lexicographic
order. This adds a foundational combinatorial-enumeration capability to the
framework — the ordered Cartesian product of an alphabet — that later
motif/character problems can build on.

## What Changes

- Add a `KmerEnumerationProblemError` ADT enumerating the structural ways the input
  can be invalid (empty alphabet, too many symbols, a duplicate symbol, a
  non-positive length, a length over the cap).
- Add a validated `KmerEnumerationProblem` bundle (`sealed abstract case class`)
  pairing the ordered alphabet with the word length, constructed only via a smart
  constructor `from(alphabet, length)` that enforces the Rosalind caps (1–10
  distinct symbols, 1 ≤ n ≤ 10) with first-failure-wins ordering.
- Add a `KmerEnumeration` result type that renders the k-mers one per line via
  `format`.
- Add an `EnumerateKmers.enumerate(problem): KmerEnumeration` algorithm that returns
  every length-`n` string in lexicographic order under the given alphabet order.
- Add a `LEXFProb` IO runner that reads `lexf_data.txt` (alphabet line + length
  line), builds the problem, runs the enumeration, and prints the k-mers; wire it
  into `Main`.

## Capabilities

### New Capabilities
- `enumerating-kmers-lexicographically`: validating an ordered alphabet and word
  length, then enumerating all length-`n` strings in lexicographic order.

### Modified Capabilities
<!-- none — this is a purely additive capability -->

## Impact

- New domain types under `bio.domain.combinatorics`: `KmerEnumerationProblemError`,
  `KmerEnumerationProblem`, `KmerEnumeration`.
- New algorithm under `bio.algorithms.combinatorics`: `EnumerateKmers`.
- New runner `bio.problems.LEXFProb`; one line changed in `bio.Main`.
- No existing behavior changes.
