## Context

SEXL ("Sex-Linked Inheritance") gives an array `A` where `A[k]` is the proportion of males exhibiting the `k`-th recessive X-linked gene, and asks for `B[k]`, the probability a random female is a carrier of that gene. Males are hemizygous (one X), so a male exhibits an X-linked recessive trait exactly when his X carries the recessive allele — hence `A[k]` equals the recessive allele frequency `q`. Under Hardy–Weinberg equilibrium with `q` recessive and `p = 1 − q` dominant, a female (XX) is a carrier when heterozygous, with probability `2pq = 2q(1 − q)`.

Each `A[k]` is a proportion in `[0,1]`, exactly the invariant of `bio.domain.stats.Probability` (reused across PROB/RSTR/EVAL). SEXL is a Mendelian-genetics computation, so it lives in `bio.{domain,algorithms}.genetics`.

## Goals / Non-Goals

**Goals:**
- A `SexLinkedProblem` wrapping `Vector[Probability]` (the male proportions). The `[0,1]` bound is owned by `Probability`, so the type carries no extra invariant and is a plain wrapper (precedent: `SpectralConvolutionProblem`).
- Pure, total `SexLinkedInheritance.carrierProbabilities(problem): CarrierProbabilities` computing `2q(1−q)` per gene.
- Result type with `format: String` rendering the values space-separated to three decimals each (sample `0.18 0.5 0.32`).
- Reuse `Probability`; functional `map`, no `var`/`while`/mutable collections.

**Non-Goals:**
- Modelling male/female genotype distributions explicitly — the closed form `2q(1−q)` suffices.
- A per-array length cap — Rosalind states no bound and the arrays are small; the empty array is accepted (empty output).

## Decisions

**1. Closed form `B[k] = 2·A[k]·(1 − A[k])`.**
With `q = A[k]` the recessive allele frequency, the female carrier (heterozygote) probability under HWE is `2q(1−q)`. Verified on the sample: `2·0.1·0.9 = 0.18`, `2·0.5·0.5 = 0.5`, `2·0.8·0.2 = 0.32`. The algorithm maps each `Probability`'s value through this formula, preserving input order.

**2. Reuse `Probability`; plain `SexLinkedProblem` wrapper.**
Each proportion is validated to `[0,1]` by `Probability` (the runner parses doubles via `Probability.from`). `SexLinkedProblem` therefore adds no invariant and is a plain `final case class SexLinkedProblem(maleProportions: Vector[Probability])` — no smart constructor or error type needed (mirrors `SpectralConvolutionProblem`).

**3. Total function; empty input.**
`carrierProbabilities` always succeeds; an empty `maleProportions` yields an empty `CarrierProbabilities`. Outputs are `Double` in `[0, 0.5]` (the maximum `2q(1−q) = 0.5` at `q = 0.5`).

**4. Result formatting.**
`CarrierProbabilities.format = values.map(v => f"$v%.3f").mkString(" ")` (Rosalind grades within 0.001; the sample's `0.18 0.5 0.32` renders as `0.180 0.500 0.320`, accepted).

**5. Naming and placement.**
`SexLinkedProblem` and the `CarrierProbabilities` result live in `bio.domain.genetics`; the algorithm `SexLinkedInheritance.carrierProbabilities` in `bio.algorithms.genetics`. Result and algorithm names are distinct, so no `=> Result` alias is needed.

## Risks / Trade-offs

- **[No problem-level validation]** → intentional: `Probability` already enforces `[0,1]`, so there is nothing else to validate; a plain wrapper is the honest model (precedent established).
- **[Formatting `0.5` vs `0.500`]** → `%.3f` yields `0.500`; accepted by Rosalind's 0.001 tolerance (consistent with EVAL/RSTR/MEND).
- **[Boundary `q = 0` / `q = 1`]** → both give carrier probability `0`; covered by a scenario. `q = 0.5` gives the maximum `0.5`.
- **[Empty input]** → empty output; covered by a scenario.
