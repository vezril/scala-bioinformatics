## Context

Rosalind problem 31 (KMP) asks for the *failure array* of a DNA string `s`, defined (in Rosalind's 1-indexed notation) as:

> `P[k] = length of the longest substring s[j..k] (j ≠ 1) that equals some prefix s[1..k−j+1]`

Equivalently — and more commonly stated — `P[k]` is the length of the longest *proper* prefix of `s[1..k]` that is also a suffix of `s[1..k]`. This is the Knuth-Morris-Pratt failure function, computable in `O(n)` time and `O(n)` space.

Input is FASTA-wrapped DNA (≤ 100 kbp); the *algorithm* layer just sees a `DnaString`. File ingestion is layered above via the existing `FastaFileReader` (used by `bio.problems.KMPProb` later, mirroring the SPLC/GRPH pattern).

The framework already hosts a `bio.algorithms.analysis` subdomain (home of `MotifLocations` and `RandomMatch`) — the failure array slots in naturally there: it's a string-processing primitive that *speeds up* motif finding (per the problem's name).

## Goals / Non-Goals

**Goals:**
- Provide a validated `FailureArrayProblem` ADT wrapping a `DnaString` (smart constructor returning `Either`, `sealed abstract case class`), enforcing a non-empty input string.
- Provide `bio.algorithms.analysis.FailureArray.compute` returning a `Vector[Int]` of length `n`, using the classic linear-time KMP table-build recurrence.
- TDD coverage at both layers — canonical Rosalind sample (21-character DNA), single-character, all-same, no-overlap, periodic, and two-character edge cases.

**Non-Goals:**
- The full KMP search algorithm. This change builds the *table* only; a future change could add `KmpSearch` on top of it.
- FASTA-aware file ingestion. The algorithm consumes a `DnaString`; layering above (the `bio.problems.KMPProb` runner) is incidental and can be added by the user when wiring the problem.
- 1-indexed return type or a `String` representation. The output is a 0-indexed `Vector[Int]` of length `n`; the caller (or the `KMPProb` runner) formats it for stdout. Mirrors the bare-`Vector` outputs of `RandomMatch.logProbabilities` and `IndependentSegregation.logProbs`.

## Decisions

**1. KMP table-build recurrence with two pointers.**

The classic O(n) algorithm. Walk `i` from 1 to `n−1`. Maintain `j` as the current candidate prefix length (the value of the previous `P` entry, effectively). At each step, while `j > 0 && s(i) != s(j)`, fall back to `j = P(j−1)`. If `s(i) == s(j)`, increment `j`. Set `P(i) = j`. Termination: each character is examined at most twice (once on the way up, once via fallback) → linear time. **Alternative considered:** Z-array → failure array conversion (rejected: introduces an intermediate array for no benefit when we already need only the failure array).

**2. Return type: bare `Vector[Int]`, not wrapped.**

The failure array is a structured *result*, not a validated domain value. Each entry is `0 ≤ P(i) < n` by construction (a proper-prefix length on a string of length `n`). The framework's pattern for "computed array output" — see `RandomMatch.logProbabilities`, `IndependentSegregation.logProbs`, `WrightFisher.expectedFrequencies` — is to return the bare collection type. **Alternative considered:** `Vector[NonNegativeInt]` (rejected: the framework has no such wrapper, and the values' non-negativity is a structural property of the algorithm, not a precondition that needs runtime checking).

**3. 0-indexed Vector matching the input's 0-indexed characters.**

Rosalind's notation is 1-indexed (`P[1] = 0` for the first position). The framework's convention everywhere else (`DnaString.value` is a Scala `String`, indexed from 0) is 0-indexed. We return a 0-indexed `Vector[Int]` of length `n` where `result(i)` corresponds to Rosalind's `P[i+1]`. The KMPProb runner can emit it space-separated for the Rosalind grader. **Alternative considered:** prepending a sentinel or shifting (rejected: introduces an off-by-one trap and a "what's index 0?" ambiguity for callers).

**4. `FailureArrayProblem` validation: non-empty.**

The failure array of the empty string is the empty `Vector[Int]`, which is technically well-defined but operationally useless (and the algorithm's `i` loop would be a no-op). The Rosalind input always has at least one character. The smart constructor enforces this so downstream code can rely on a non-empty input; the error case is `FailureArrayProblemError.EmptySequence`. **Alternative considered:** returning `Vector.empty` for empty input (rejected: silently accepting degenerate input contradicts the framework's "validate at the boundary" pattern).

**5. Place under `bio.{algorithms,domain}.analysis`.**

This is where `MotifLocations` (spec 9 — SUBS) and `RandomMatch` (spec 19 — PROB) already live. KMP is *literally* a motif-finding speedup. **Alternative considered:** a new `bio.algorithms.strings` subdomain (rejected: only one algorithm so far would live there; consolidating with the existing string-processing analysis siblings is cleaner).

## Risks / Trade-offs

- **[Risk]** Off-by-one between Rosalind's 1-indexed notation and our 0-indexed Vector return type. → **Mitigation:** Scaladoc on `FailureArray.compute` explicitly maps `result(i) == Rosalind's P[i+1]`; the canonical test uses a Vector of length 21 matching Rosalind's published 21 values position-for-position.
- **[Risk]** The KMP recurrence is famously easy to get wrong (especially the `while` fallback). → **Mitigation:** the canonical Rosalind sample doubles as a property test — `0 0 0 1 2 0 0 0 0 0 0 1 2 1 2 3 4 5 3 0 0` exercises both the rising-streak path and the fallback path; in addition, `ACACACAC → 0 0 1 2 3 4 5 6` exercises pure rising and `AAAAA → 0 1 2 3 4` exercises maximal periodicity.
- **[Trade-off]** Computing the failure array but not the full KMP search means `bio.algorithms.analysis.MotifLocations` doesn't *yet* benefit from O(n) search. That's a future change; scoping tightly to spec 31's "Speeding Up Motif Finding" deliverable (just the failure array) keeps the change focused.
