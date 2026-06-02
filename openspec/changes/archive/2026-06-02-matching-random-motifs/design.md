## Context

The project already models random-string matching for Rosalind PROB ("Introduction to Random Strings"):
- `bio.domain.stats.Probability` — a validated value class wrapping a finite `Double` in `[0,1]`, with `from(d): Either[ProbabilityError, Probability]` and `.value`.
- `bio.domain.analysis.RandomMatchProblem` + `bio.algorithms.analysis.RandomMatch.logProbabilities` — computes the `log10` probability that a single random string matches a DNA string, across a *vector* of GC-contents.
- `bio.domain.nucleic.DnaString` (validated, `.value`) and `DnaNucleotide` (sealed ADT with `fromChar`/`toChar`/`complement`, `validChars`).

RSTR is closely related but distinct: a single GC-content, and a closed-form "at least one of N matches" answer rather than a per-string log-probability.

## Goals / Non-Goals

**Goals:**
- Validated `RandomMotifProblem` (motif `DnaString` ≤ 10 bp; trials `N` with `1 ≤ N ≤ 100000`; GC-content as a reused `Probability`) via a smart constructor returning `Either`, using `sealed abstract case class` to block `apply`/`copy` leakage.
- Pure, total `MatchingRandomMotifs.probability(problem): RandomMotifMatch`.
- Result type with `format: String` rounding to three decimals (Rosalind sample shows `0.689`).
- Reuse `Probability` for the GC-content fraction; dispatch per-symbol probability on the `DnaNucleotide` ADT (no raw `Char` literals).
- `RSTRProb` runner reading `N`, `x`, and the motif from `rstr_data.txt`, printing via `IO`.

**Non-Goals:**
- Generating actual random strings (the answer is closed-form).
- Supporting multiple GC-contents or multiple motifs per run (PROB already covers the vector case).
- Reusing `RandomMatch.logProbabilities` — see Decision 2.

## Decisions

**1. Closed-form probability.**
For a motif `s` and GC-content `x`, the probability a single length-|s| random string equals `s` is `p = ∏_i P(s_i | x)` where `P(G)=P(C)=x/2` and `P(A)=P(T)=(1-x)/2`. The probability that at least one of `N` independent strings equals `s` is `1 - (1 - p)^N` (complement of "none match"). Computed as `1.0 - math.pow(1.0 - p, trials)`.

**2. New self-contained algorithm rather than reusing `RandomMatch.logProbabilities`.**
`RandomMatch` returns `log10` probabilities over a *vector* of GC-contents and matches on raw `Char`s. RSTR needs the linear-scale single-match probability `p` and a single GC-content. Exponentiating `10^logProb` to recover `p`, then wrapping in the `1-(1-p)^N` formula, would couple RSTR to an unrelated vector API and an extra round-trip through log space. A direct product dispatched on the `DnaNucleotide` ADT is clearer, total, and follows the framework's "dispatch on ADT values, not raw `Char`" convention. The relationship to PROB is documented in scaladoc. This is a non-alignment problem, so pure FP applies.

**3. Reuse `Probability` for the GC-content fraction.**
The GC-content `x` is a number in `[0,1]` — exactly `Probability`'s invariant. `RandomMotifProblem.from` takes a pre-validated `Probability`, mirroring how `RandomMatchProblem` takes `Vector[Probability]`. The runner validates the parsed double via `Probability.from` before constructing the problem.

**4. Validation order (first-failure-wins).**
`RandomMotifProblem.from(motif, trials, gcContent)` checks, in order: motif length > 10 → `MotifTooLong(length, 10)`; trials < 1 → `NonPositiveTrials(trials)`; trials > 100000 → `TooManyTrials(trials, 100000)`. The GC-content arrives already validated as a `Probability`, so it needs no further check. An empty motif is accepted (empty product `p = 1`, so the answer is `1`) as a boundary case.

**5. Placement in the `analysis` packages.**
`RandomMotifProblem`, `RandomMotifProblemError`, and `RandomMotifMatch` live in `bio.domain.analysis`; the algorithm in `bio.algorithms.analysis` — alongside the PROB / `RandomMatch` family. Result and algorithm names differ (`RandomMotifMatch` vs `MatchingRandomMotifs`), so no `=> Result` alias is needed.

**6. Three-decimal formatting.**
`RandomMotifMatch.format` renders via `f"${probability}%.3f"` (Rosalind allows 0.001 absolute error; the sample answer is `0.689`).

## Risks / Trade-offs

- **[Floating-point precision of `(1-p)^N` for large N]** → `math.pow` on a `Double` is more than adequate at `N ≤ 100000` and motif length ≤ 10 (`p` is small but well above underflow); the canonical sample (`90000, 0.6, ATAGCCGA → 0.689`) is an explicit end-to-end scenario.
- **[Empty motif yields probability 1]** → Mathematically correct (every random empty string trivially "equals" the empty motif); covered by an explicit boundary scenario. Rosalind inputs are non-empty, so this is a defensive boundary only.
- **[Confusing GC fraction (`[0,1]`) with the GcContent percentage type (`[0,100]`)]** → Deliberately reuse `Probability` (fraction), not `bio.domain.analysis.GcContent` (percentage), matching PROB's convention; documented in scaladoc.
