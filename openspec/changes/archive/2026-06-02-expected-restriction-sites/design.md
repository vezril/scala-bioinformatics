## Context

The project already models random-string analysis under the `analysis` packages:
- `bio.domain.stats.Probability` — validated value class wrapping a finite `Double` in `[0,1]`, with `from(d): Either[ProbabilityError, Probability]` and `.value`.
- `bio.domain.analysis.RandomMatchProblem` + `bio.algorithms.analysis.RandomMatch` (Rosalind PROB) — `log10` single-position match probability across a *vector* of GC-contents.
- `bio.algorithms.analysis.MatchingRandomMotifs` (Rosalind RSTR) — the linear-scale `1 - (1 - p)^N` probability for a *single* GC-content, with per-symbol probabilities dispatched on the `DnaNucleotide` ADT.

EVAL ("Expected Number of Restriction Sites") sits between them: a *vector* of GC-contents like PROB, but a linear-scale *expected count* derived from the same single-position match probability `p`.

## Goals / Non-Goals

**Goals:**
- Validated `ExpectedRestrictionSitesProblem` (motif `DnaString` of even length ≤ 10; length `n` with `1 ≤ n ≤ 1,000,000`; GC-contents as a reused `Vector[Probability]`, size ≤ 20) via a smart constructor returning `Either`, using `sealed abstract case class` to block `apply`/`copy` leakage.
- Pure, total `ExpectedRestrictionSites.expectedCounts(problem): ExpectedRestrictionSites` (result).
- Result type with `format: String` rendering each expected count to three decimals, space-separated (Rosalind sample: `0.422 0.563 0.422`).
- Reuse `Probability` for the GC-content fractions; dispatch per-symbol probability on the `DnaNucleotide` ADT (no raw `Char` literals).
- `EVALProb` runner reading `n`, `s`, and `A` from `eval_data.txt`, printing via `IO`.

**Non-Goals:**
- Generating actual random strings (the answer is closed-form via linearity of expectation).
- Reusing `RandomMatch.logProbabilities` — see Decision 2.
- Verifying that `s` is a reverse palindrome (the Rosalind "restriction sites" framing is motivational; the math only requires the even-length input constraint).

## Decisions

**1. Linearity-of-expectation formula.**
A motif `s` of length `L` can begin at any of `n - L + 1` positions in a length-`n` string. By linearity of expectation, the expected number of occurrences is `(n - L + 1) · p`, where `p = ∏ⱼ P(sⱼ | x)` is the probability `s` matches at a single position under GC-content `x` (`P(G)=P(C)=x/2`, `P(A)=P(T)=(1-x)/2`). The position count is clamped to `max(0, n - L + 1)` so a motif longer than the string yields `0`.

**2. New self-contained algorithm rather than reusing `RandomMatch.logProbabilities`.**
`RandomMatch` returns `log10 p` and matches on raw `Char`s. Recovering `p = 10^(log10 p)` to scale by `(n - L + 1)` would couple EVAL to an unrelated log-space API. A direct product dispatched on the `DnaNucleotide` ADT is clearer, total, and follows the framework's "dispatch on ADT values, not raw `Char`" convention — consistent with the immediately-preceding RSTR algorithm. The relationship to PROB/RSTR is documented in scaladoc. This is a non-alignment problem, so pure FP applies.

**3. Reuse `Probability` for the GC-content fractions.**
The GC-contents in `A` are numbers in `[0,1]` — exactly `Probability`'s invariant. `ExpectedRestrictionSitesProblem` holds `Vector[Probability]`, mirroring `RandomMatchProblem`. The runner validates each parsed double via `Probability.from` before constructing the problem.

**4. Validation order (first-failure-wins).**
`ExpectedRestrictionSitesProblem.from(motif, length, gcContents)` checks, in order: motif length > 10 → `MotifTooLong(length, 10)`; motif length odd → `OddMotifLength(length)`; `length < 1` → `NonPositiveLength(length)`; `length > 1_000_000` → `LengthTooLarge(length, 1_000_000)`; `gcContents.size > 20` → `TooManyGcContents(size, 20)`. GC-content values arrive pre-validated as `Probability`. An empty motif (length 0, even) and an empty `gcContents` vector are both accepted boundaries (see Risks).

**5. Placement in the `analysis` packages.**
`ExpectedRestrictionSitesProblem`, `ExpectedRestrictionSitesProblemError`, and the `ExpectedRestrictionSites` result live in `bio.domain.analysis`; the algorithm in `bio.algorithms.analysis` — alongside PROB and RSTR. The algorithm object and the result type share the name `ExpectedRestrictionSites` in different packages, so the algorithm aliases the domain result via `import bio.domain.analysis.{ExpectedRestrictionSites => Result}` (the CONV/REVP/ORF pattern).

**6. Three-decimal, space-separated formatting.**
`ExpectedRestrictionSites.format` renders `expectations.map(e => f"$e%.3f").mkString(" ")` (Rosalind allows 0.001 absolute error; the sample answer is `0.422 0.563 0.422`). The empty result renders as the empty string.

## Risks / Trade-offs

- **[A motif longer than the string]** → `max(0, n - L + 1)` clamps the position count to 0, yielding expected `0`; covered by an explicit scenario (`n` smaller than `|s|`).
- **[Empty motif yields `n + 1` per GC-content]** → Mathematically correct (a length-0 substring occurs at every one of the `n + 1` gaps, with `p = 1`); accepted as a documented boundary. Rosalind inputs are non-empty even-length motifs, so this is defensive only.
- **[Empty GC-content array yields an empty result]** → Accepted (mirrors `RandomMatchProblem`); `format` renders `""`.
- **[GC-content of 0 or 1 makes a motif symbol impossible]** → The corresponding per-symbol probability is 0, so `p = 0` and the expected count is `0`; covered by an explicit scenario.
- **[Floating-point precision]** → `Double` products over ≤ 10 symbols and multiplication by `n ≤ 1,000,000` are well within precision; the canonical sample (`10 / AG / 0.25 0.5 0.75 → 0.422 0.563 0.422`) is an explicit end-to-end scenario.
