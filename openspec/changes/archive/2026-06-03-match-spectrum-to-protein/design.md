## Context

PRSM gives `n` candidate protein strings and a multiset `R` (the complete spectrum of some unknown protein) and asks for the candidate `s_k` maximising the multiplicity of `R⊖S[s_k]`, plus that multiplicity. The *complete spectrum* `S[s]` is the multiset of the weights of every prefix and every suffix of `s`.

The project already has the pieces: `bio.domain.protein.AminoAcid` (monoisotopic residue masses via `monoisotopicMass`), `ProteinString` (validated 20-letter protein), and the spectral-convolution idea from CONV (max multiplicity of pairwise differences, bucketed to 5 decimals to absorb floating-point error). PRSM lives in `bio.{domain,algorithms}.protein` alongside PRTM/CONV/SPEC.

## Goals / Non-Goals

**Goals:**
- Validated `SpectrumMatchProblem(proteins, spectrum)` via a smart constructor returning `Either`, `sealed abstract case class` to block `apply`/`copy`.
- Pure, total `MatchSpectrum.bestMatch(problem): SpectrumMatch`.
- Result type with `format: String` (`multiplicity\nprotein`).
- Reuse `AminoAcid.monoisotopicMass` and `ProteinString`; functional implementation, no `var`/`while`/mutable collections.

**Non-Goals:**
- Reusing CONV's `MassMultiset`/`SpectralConvolution` directly — `MassMultiset` caps size at 200, which `R` or `S[s_k]` may approach; PRSM computes the convolution multiplicity inline with the same 5-decimal bucketing.
- Reconstructing the unknown protein — only the best candidate and its match multiplicity are required.

## Decisions

**1. Complete spectrum `S[s]` = cumulative prefix and suffix weights.**
For a protein `s` with residue masses `m₁…m_L`, the prefix weights are the cumulative sums `m₁, m₁+m₂, …` (length `L`) and the suffix weights are the cumulative sums from the right (length `L`), giving the `2L`-element multiset `S[s]`. Computed functionally with `scanLeft(0.0)(_ + _).tail` on the masses and on their reverse. Residue masses come from `AminoAcid.fromChar(c).monoisotopicMass` (every char of a `ProteinString` is a valid code, so the lookup always succeeds).

**2. Match score = maximum multiplicity of `R⊖S[s_k]`.**
`R⊖S[s_k]` is the multiset of all differences `r − x` (`r ∈ R`, `x ∈ S[s_k]`); its maximum multiplicity is the count of the most frequent difference — i.e. how many `(r,x)` pairs share a single shift. Each difference is rounded to 5 decimals (`round(d·1e5)/1e5`) before grouping, to absorb binary rounding error (the CONV idiom). The candidate with the greatest maximum multiplicity wins; ties resolve to the first such candidate (`maxBy` returns the first maximal element) — any maximiser is acceptable per Rosalind.

**3. Validation rules and order (first-failure-wins).**
`SpectrumMatchProblem.from(proteins, spectrum)` checks: `proteins.nonEmpty`, else `EmptyProteinList`; `spectrum.nonEmpty`, else `EmptySpectrum`; then each spectrum value `> 0`, else `NonPositiveMass(index, value)` (the first offending). DNA/protein-character validity is owned upstream by `ProteinString`.

**4. Naming and placement.**
`SpectrumMatchProblem`, `SpectrumMatchProblemError`, and the `SpectrumMatch` result live in `bio.domain.protein`; the algorithm `MatchSpectrum.bestMatch` in `bio.algorithms.protein`. Result (`SpectrumMatch`) and algorithm (`MatchSpectrum`) names are distinct, so no alias is needed.

## Risks / Trade-offs

- **[Floating-point grouping]** → differences are bucketed to 5 decimals before counting (the verified CONV approach), so near-equal shifts coincide. The canonical sample (`4` proteins, `6`-value `R`) is an explicit end-to-end scenario yielding `3 / IASWMQS`.
- **[Not reusing `MassMultiset`]** → its 200-element cap could reject realistic `R`/`S`; computing the multiplicity inline keeps the same bucketing without the cap. Minor duplication of ~3 lines, documented.
- **[Tie-breaking]** → `maxBy` returns the first maximal candidate; Rosalind accepts any maximiser. Covered by a selection scenario.
- **[Empty inputs]** → no candidates / empty spectrum / non-positive mass are rejected by validation; covered by scenarios.
