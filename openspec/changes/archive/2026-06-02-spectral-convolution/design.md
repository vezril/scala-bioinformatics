## Context

CONV is a pure mass-spectrometry combinatorics problem, not an alignment problem.
The framework already models peptide masses (`AminoAcid.monoisotopicMass`,
`PrefixSpectrum`) in `bio.domain.protein`, so the new spectral types belong there.
Inputs are two whitespace-separated lines of positive reals (≤ 200 each) in
`conv_data.txt`; the canonical sample yields multiplicity `3` at shift `85.03163`.

## Goals / Non-Goals

**Goals:**
- Model the two inputs as validated multisets and a bundling problem type.
- Compute the spectral convolution and report `(largest multiplicity, |shift|)`
  exactly matching the Rosalind sample.
- Keep the algorithm pure FP (no `var`/`while`/`Array`) — CONV is not in the
  alignment family that justifies imperative DP.

**Non-Goals:**
- No reconstruction of peptides, no Minkowski sum, no spectrum alignment.
- No streaming/large-input optimization beyond the ≤ 200 × ≤ 200 case.

## Decisions

- **`MassMultiset` as a `sealed abstract case class`** wrapping `Vector[Double]`,
  built only via `from(Vector[Double]): Either[MassMultisetError, MassMultiset]`.
  Validation is first-failure-wins in this order: empty → too many (> 200) →
  non-positive element (`zipWithIndex.collectFirst`). The abstract+sealed pattern
  blocks synthesized `apply`/`copy`, proven with `assertDoesNotCompile`.
  Alternative considered: a plain wrapper — rejected because it would leak an
  unvalidated constructor, violating the framework's smart-constructor rule.
- **`MassMultisetError`** ADT: `EmptyMultiset`, `TooManyMasses(size, max)`,
  `NonPositiveMass(index, value)`. Mirrors `PrefixSpectrumError`.
- **`SpectralConvolutionProblem`** is a plain `final case class(s1, s2)` of two
  `MassMultiset`s. It carries no invariant beyond the already-validated multisets,
  so (like the `ProteinMass` result) it needs no smart constructor.
- **`SpectralConvolution` result** is a plain `final case class(multiplicity: Int,
  shift: Double)` with `format` = `s"$multiplicity\n${|shift| to 5 decimals}"`.
  The stored `shift` is the representative difference; `format` emits its absolute
  value via `f"%.5f"`, matching `85.03163`.
- **Rounding for bucketing.** Differences of two 5-decimal reals are exact in
  decimal but not in binary, so equal shifts can differ by ~1e-11. Bucket each
  difference by `round(d * 1e5) / 1e5` (five decimals) before counting. This is
  the standard CONV tolerance and reproduces the sample exactly.
- **Deterministic tie-break.** Rosalind accepts any maximizing `x`, but tests need
  determinism: among buckets, pick the one maximizing `(count, then smallest
  |value|)` via `sortBy { case (v, c) => (-c, math.abs(v)) }.head`. The sample has
  a unique max, so this only matters for ties.
- **`CONVProb` runner** reads `conv_data.txt`, splits into non-empty lines, parses
  the first two lines as whitespace-separated doubles (`toDoubleOption`), validates
  each into a `MassMultiset`, builds the problem, convolves, and prints `format`.
  All errors are printed via `IO.println`, never thrown (consistent with
  SPEC/PRTM runners).

## Risks / Trade-offs

- [Floating-point equality] → Fixed by five-decimal rounding before bucketing;
  matches input precision and the canonical answer.
- [Tie ambiguity in the maximizing shift] → Rosalind accepts any; we still pin a
  deterministic choice so tests are stable.
- [Performance: 200 × 200 = 40,000 differences] → Trivial; a single `for`
  comprehension and `groupBy` suffice, no optimization needed.
