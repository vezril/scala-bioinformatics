## Why

Rosalind problem 19 ("Introduction to Random Strings") asks us to compute, for each GC content value `x` in an input array, the common log (`log10`) of the probability that a random DNA string of length `|s|` constructed under the symbol frequencies `P(G) = P(C) = x/2`, `P(A) = P(T) = (1−x)/2` matches a given DNA string `s` exactly. This is the framework's first algorithm that *combines* the DNA-sequence vocabulary (a `DnaString`) with the probability vocabulary (a `Vector[Probability]`), and the first that returns a numerically-derived `Vector[Double]` of log-probabilities. It seeds the `analysis` subdomain with its second numeric-output algorithm that depends on both a sequence and a probability model.

## What Changes

- Add a validated `RandomMatchProblem` parameter bundle in `bio.domain.analysis` enforcing the Rosalind upper bounds: `|dna| ≤ 100` (sub-range of `DnaString`'s 1000-character cap) and `|gcContents| ≤ 20`.
- Add a `RandomMatchProblemError` sealed ADT with cases `DnaTooLong(length: Int, max: Int)` and `TooManyGcContents(size: Int, max: Int)`.
- Add `RandomMatch.logProbabilities(problem): Vector[Double]` in `bio.algorithms.analysis`, returning a `Vector[Double]` of the same length as `problem.gcContents` where each entry is `sum_{c in dna} log10(symbolProbability(c, gc))`. Total over the validated input.
- No modifications to existing capabilities. `DnaString`, `Probability`, and `DnaNucleotide` are reused unchanged.

## Capabilities

### New Capabilities
- `random-string-matching`: The `RandomMatchProblem` validated parameter bundle, the `RandomMatchProblemError` ADT, and the `RandomMatch.logProbabilities` algorithm computing the per-GC-content log-probability that a random DNA string matches the given sequence exactly.

### Modified Capabilities
<!-- none — purely additive -->

## Impact

- New files: `bio.domain.analysis.RandomMatchProblem`, `bio.domain.analysis.RandomMatchProblemError`, `bio.algorithms.analysis.RandomMatch`.
- New test suites: `RandomMatchProblemSpec`, `RandomMatchProblemErrorSpec`, `RandomMatchSpec`.
- No new SBT dependencies.
- No breaking changes — purely additive. Existing `bio.domain.{stats,nucleic,analysis}` types reused as-is.
