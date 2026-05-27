## Context

Spec 19 takes a `DnaString s` and an array `A` of probability values (each a GC fraction `x ∈ [0, 1]`). For each `A[k]`, it computes `B[k] = log10(P(random string matches s exactly))` where a random string of length `|s|` is sampled under `P(G) = P(C) = x/2`, `P(A) = P(T) = (1−x)/2`. The framework already has `DnaString` (validated, ≤ 1000 chars), `DnaNucleotide` (sealed 4-case ADT), and `Probability` (validated `[0, 1]` `Double` value class in `bio.domain.stats`).

This algorithm bridges two existing vocabularies — `DnaString` (sequence input) and `Probability` (the GC-content array) — and produces a `Vector[Double]` (raw log-probabilities, unbounded below). It is naturally a member of the `analysis` subdomain alongside `GcContent`, `HammingDistance`, `MotifLocations`, and `OverlapGraph`.

## Goals / Non-Goals

**Goals:**
- A validated `RandomMatchProblem(dna: DnaString, gcContents: Vector[Probability])` parameter bundle enforcing the Rosalind upper bounds (`|dna| ≤ 100`, `|gcContents| ≤ 20`).
- A `RandomMatchProblemError` ADT for the two bound-violation cases.
- `RandomMatch.logProbabilities(problem): Vector[Double]` — total, deterministic, single-pass per GC value.

**Non-Goals:**
- A new `LogProbability` wrapper type. Log-probabilities are unbounded below; the bare `Double` matches the Rosalind output format directly and avoids a wrapper with no downstream user.
- Lower-bound enforcement on `|gcContents|` — the empty vector is a valid input that produces an empty output. Same for `|dna|`: an empty `DnaString` produces `0.0` log-probabilities (empty product is 1, `log10(1) = 0`) for every GC value.
- Defending against `x = 0` or `x = 1`: those are valid `Probability` values and the algorithm produces `Double.NegativeInfinity` for any DNA string that contains a character whose probability is `0` (e.g. `x = 0` with a G in the sequence). That's mathematically correct and tested as an edge case.
- Numerical-stability gymnastics. `Double` precision is enough for `|s| ≤ 100`; we sum logs rather than logging a product to avoid underflow naturally.

## Decisions

### Decision 1: `RandomMatchProblem` as a validated bundle (not bare parameters)

```scala
sealed abstract case class RandomMatchProblem(dna: DnaString, gcContents: Vector[Probability])
object RandomMatchProblem {
  private val MaxDnaLength: Int = 100
  private val MaxGcContents: Int = 20

  def from(dna: DnaString, gcContents: Vector[Probability]): Either[RandomMatchProblemError, RandomMatchProblem] =
    if (dna.value.length > MaxDnaLength)
      Left(RandomMatchProblemError.DnaTooLong(dna.value.length, MaxDnaLength))
    else if (gcContents.size > MaxGcContents)
      Left(RandomMatchProblemError.TooManyGcContents(gcContents.size, MaxGcContents))
    else
      Right(new RandomMatchProblem(dna, gcContents) {})
}
```

Two cross-cutting upper-bound checks (DNA length and array size), so a bundle with `from` makes the algorithm total over its validated input. Mirrors the established pattern from `PartialPermutationProblem` and `ConsensusProfileProblem`. Validation order: DNA length first, then array size (first-failure-wins).

**Alternative considered:** Bare-parameter `RandomMatch.logProbabilities(dna: DnaString, gcContents: Vector[Probability])` mirroring `MotifLocations.find`. Rejected because Rosalind specifies hard upper bounds (≤ 100, ≤ 20) on top of what the per-element types already validate; the bundle makes those constraints type-system-visible.

### Decision 2: `RandomMatchProblemError` as a sealed ADT, both cases carrying value + max

```scala
sealed trait RandomMatchProblemError
object RandomMatchProblemError {
  final case class DnaTooLong(length: Int, max: Int) extends RandomMatchProblemError
  final case class TooManyGcContents(size: Int, max: Int) extends RandomMatchProblemError
}
```

Carrying `max` alongside the offending count makes the error self-describing — no callers need to know the internal constant. Mirrors `NExceedsMaximum` / `KExceedsMaximum` in `PartialPermutationProblemError`.

### Decision 3: Sum-of-logs algorithm (not log-of-product)

```scala
def logProbabilities(problem: RandomMatchProblem): Vector[Double] = {
  val chars = problem.dna.value
  problem.gcContents.map { gc =>
    val x      = gc.value
    val pGc    = x / 2.0          // P(G) == P(C)
    val pAt    = (1.0 - x) / 2.0  // P(A) == P(T)
    chars.foldLeft(0.0) { (acc, ch) =>
      val p = ch match {
        case 'G' | 'C' => pGc
        case 'A' | 'T' => pAt
        case _         => pAt  // unreachable: DnaString is alphabet-validated
      }
      acc + Math.log10(p)
    }
  }
}
```

**Why sum of logs (not `log10(product)`):** the product `Π P(c)` underflows to `0.0` for moderate `|s|` and small probabilities (e.g. `(0.05)^50 ≈ 8.9e−66`, still representable as `Double`, but `(0.001)^50 = 1e−150`, also representable but the safer regime is logs). Summing logs sidesteps the underflow regime entirely. For Rosalind bounds (`|s| ≤ 100`) the product *would* still be representable, but the log-sum form is the right idiom regardless — it's the same number of FLOPs and avoids a footgun at scale.

**Inner `match` is exhaustive in practice:** `DnaString` is alphabet-validated against `{A, C, G, T}` at construction. The `case _ => pAt` arm exists only to make the match total at the type system level; it's unreachable. (Alternative: use `DnaNucleotide.fromChar(ch).get` — also unreachable but a `.get` is uglier than a fallback arm.)

### Decision 4: Algorithm in `bio.algorithms.analysis`, types in `bio.domain.analysis`

The algorithm is structural analysis of a DNA string under a probability model. It joins:
- `GcContent` (analysis: structural property of one DNA string)
- `HammingDistance` (analysis: difference between two DNA strings)
- `MotifLocations` (analysis: substring search)
- `OverlapGraph` (analysis: overlap structure across many DNA strings)
- now `RandomMatch` (analysis: log-probability of a random DNA string matching the given sequence under a probability model)

The bundle and error live in `bio.domain.analysis` (same subdomain as `HammingError`, `GcContent`, `OverlapEdge`).

`bio.domain.stats.Probability` is *imported*, not re-derived. The framework convention is to compose validated types across subdomains, not duplicate them.

### Decision 5: Output type is `Vector[Double]`

The output is a vector of log-probabilities, indexed correspondingly with the input GC array. They are unbounded below (can be `Double.NegativeInfinity` when any character has probability 0 at the given GC value), bounded above by `0.0`. A custom `LogProbability` wrapper would be defensible but currently has no second consumer — keep the type vocabulary minimal until there's a need.

## Risks / Trade-offs

- **Risk:** `Double.NegativeInfinity` from `log10(0)` propagates silently. → **Mitigation:** documented in scaladoc and tested as an explicit edge case (`gc = 0` with a `G` in the sequence). The framework's other floating-point algorithms (`ExpectedOffspring`, `IndependentAlleles`) make similar choices.
- **Risk:** `MaxDnaLength = 100` is stricter than `DnaString`'s 1000-character cap. → **Mitigation:** the constraint comes from the Rosalind problem definition, not from the algorithm itself. We enforce it because the bundle is the authoritative validated input — if a future caller wanted unbounded behavior they could call into the algorithm via a different entry point. Until that need shows up, the cap matches the spec.
- **Trade-off:** Validation order (DNA length → array size). → **Mitigation:** first-failure-wins is the established framework convention (matches `PartialPermutationProblem` and `ConsensusProfileProblem`); tested explicitly so the order is observable.
- **Trade-off:** No `LogProbability` wrapper. → **Mitigation:** raw `Vector[Double]` matches the Rosalind output format. If a second algorithm later wants a `LogProbability` type, introducing it is purely additive and doesn't disturb this capability.
