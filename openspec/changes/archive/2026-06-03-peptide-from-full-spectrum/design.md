## Context

FULL gives a list `L` of `2n+3` positive masses: `L(0)` is the parent mass, and the other `2n+2` are the b-ions and y-ions of a peptide `P` (in no order, complements guaranteed present). The b-ions are the prefix masses (`w(prefix) + w₁` for a constant offset `w₁`) and the y-ions the suffix masses (`+ w₂`). Consecutive prefix masses differ by exactly one residue mass — the offset cancels — so the peptide is recoverable from the prefix series.

The protein mass-spectrometry capabilities live in `bio.{domain,algorithms}.protein`; `bio.domain.protein.AminoAcid` exposes `all: Vector[AminoAcid]` and `monoisotopicMass`. FULL reuses `AminoAcid` and adds the reconstruction.

## Goals / Non-Goals

**Goals:**
- Validated `FullSpectrumProblem(masses)` (size `= 2n+3`, `n ≥ 1`; all positive) via a smart constructor returning `Either`, `sealed abstract case class` to block `apply`/`copy`.
- Pure, total `InferPeptide.infer(problem): InferredPeptide`.
- Result type with `format: String` (the peptide).
- Reuse `AminoAcid`; functional implementation (sorting + tail-recursive walk), no `var`/`while`/mutable collections.

**Non-Goals:**
- Computing the offsets `w₁`, `w₂` — only the peptide `t` is required, and prefix-mass differences cancel the offset.
- Handling malformed spectra beyond size/positivity — Rosalind guarantees well-formed input (complements present, clean residue gaps).

## Decisions

**1. Greedy prefix-series walk.**
Drop `L(0)` (parent) and sort the remaining `2n+2` ions ascending. The smallest ion is the first prefix ion `b₀`. Repeatedly, from the current prefix mass, take the **first ion (in ascending order) greater than it whose gap matches an amino-acid residue mass** within tolerance; that gap is the next residue and that ion is the next prefix ion. After `n` steps the residues spell the peptide. Verified on the sample (`KEKEP`): the ascending-first rule correctly selects the next b-ion and skips the interleaved y-ions (even when a y-ion happens to sit a residue-distance away, the true next b-ion is smaller and is chosen first).

**2. Residue matching by tolerance.**
A gap `g` matches residue `a` when `|g − a.monoisotopicMass| < 1e-4`. This cleanly distinguishes near-equal residues (e.g. K = 128.09496 vs Q = 128.05858, 0.036 apart) while absorbing floating-point noise (prefix gaps equal residue masses to ~1e-5). Isobaric residues (I/L, both 113.08406) tie; the first in `AminoAcid.all` is chosen — any solution is acceptable per Rosalind.

**3. Pure functional reconstruction.**
The walk is a `@tailrec` build over the sorted ion vector: each step uses `iterator.filter(_ > current).flatMap(ion => residueOf(ion − current).map((ion, _))).nextOption()` to find the next (ion, residue). No `var`/`while`/mutable state; sorting and iterator combinators only.

**4. Validation rules and order (first-failure-wins).**
`FullSpectrumProblem.from(masses)` checks the size is `2n+3` for some `n ≥ 1` — i.e. `size` is odd and `≥ 5` — else `InvalidSize(size)`; then every mass is positive, else `NonPositiveMass(index, value)` (the first offending).

**5. Naming and placement.**
`FullSpectrumProblem`, `FullSpectrumProblemError`, and the `InferredPeptide` result live in `bio.domain.protein`; the algorithm `InferPeptide.infer` in `bio.algorithms.protein`. Result (`InferredPeptide`) and algorithm (`InferPeptide`) names are distinct, so no alias is needed.

## Risks / Trade-offs

- **[Greedy correctness]** → relies on the true next prefix ion being the smallest residue-distance match; holds for Rosalind's well-formed spectra (clean residue gaps, complements present), verified on the sample. Documented assumption.
- **[Tolerance choice]** → `1e-4` separates K/Q-type near-equals yet tolerates float noise; isobaric I/L are interchangeable (any accepted).
- **[Invalid / degenerate input]** → non-`2n+3` sizes and non-positive masses are rejected; a spectrum that cannot be extended `n` times returns the residues found so far (defensive; Rosalind inputs are always solvable).
- **[Output among multiple solutions]** → Rosalind accepts any valid peptide; the greedy yields a deterministic one (matching the sample `KEKEP`).
