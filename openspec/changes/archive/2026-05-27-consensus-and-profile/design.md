## Context

Spec 18 takes a `Vector[FastaRecord]` (all DNA sequences of the same length `n`) and emits two things: a 4×n profile matrix of per-column nucleotide counts, and a consensus DNA string of length `n` formed by the column-wise argmax. The framework already has `DnaString` (validated), `DnaNucleotide` (sealed 4-case ADT with `validChars` / `fromChar` / `toChar`), and `FastaRecord(id, dna)`.

This is the first algorithm in the framework whose result is a *labeled tabular matrix* — neither a scalar, a list of scalars, a graph, nor a probability. Two new packages are introduced (`bio.domain.matrix`, `bio.algorithms.matrix`) so this and future matrix-shaped algorithms (distance matrices, scoring matrices, multiple-sequence-alignment matrices) have a home.

## Goals / Non-Goals

**Goals:**
- A validated `ConsensusProfileProblem(records: Vector[FastaRecord])` bundle that enforces "at least one record" and "all records the same length".
- A `ProfileMatrix` value type wrapping `Map[DnaNucleotide, Vector[Int]]` with public accessors `a`, `c`, `g`, `t` for the four per-column count vectors. Smart constructor enforces structural invariants (all four keys present, all vectors equal length).
- A `ConsensusProfileResult(consensus: DnaString, profile: ProfileMatrix)` pair.
- `ConsensusProfile.compute(problem): ConsensusProfileResult` — total over the validated bundle, deterministic (alphabetical tie-break on consensus), single-pass per column.

**Non-Goals:**
- Generic `Matrix[A]` / `DenseMatrix` abstractions. Out of scope until we have a second matrix-shaped result type.
- Reading/writing the Rosalind output format (`ATGCAACT\nA: 5 1 0 0 5 5 0 0\n...`). That's presentation; algorithm produces structured data, callers can format.
- Streaming / large-input handling. Rosalind caps at 10 records × 1 kbp; whole-vector approach is fine.
- All-tied-consensus enumeration. Spec permits a single consensus when ties exist; we pick the alphabetical winner.
- Validating that record ids are unique. The algorithm doesn't depend on ids; we just iterate sequences.

## Decisions

### Decision 1: `ConsensusProfileProblem` as a validated bundle (not an Either from the algorithm)

```scala
sealed abstract case class ConsensusProfileProblem(records: Vector[FastaRecord])
object ConsensusProfileProblem {
  def from(records: Vector[FastaRecord]): Either[ConsensusProfileProblemError, ConsensusProfileProblem] = {
    if (records.isEmpty) Left(ConsensusProfileProblemError.EmptyInput)
    else {
      val lengths = records.map(_.dna.value.length)
      if (lengths.distinct.size > 1) Left(ConsensusProfileProblemError.LengthMismatch(lengths))
      else Right(new ConsensusProfileProblem(records) {})
    }
  }
}
```

Two cross-constraints to enforce together (non-empty + equal-length) and a permanently meaningful "valid input" type once constructed — exactly the case for a bundle (mirrors `PartialPermutationProblem`). `ConsensusProfile.compute` then becomes total: `ConsensusProfileProblem ⇒ ConsensusProfileResult`.

**Alternative considered:** `compute(records): Either[Error, Result]` (mirrors `HammingDistance.between`). Rejected because the bundle gives a *reusable* validated value — callers can pass it to multiple algorithms later (e.g. a future "Most Likely Common Ancestor" algorithm could share the same input contract).

### Decision 2: `ProfileMatrix` wraps `Map[DnaNucleotide, Vector[Int]]`, not four named fields

```scala
sealed abstract case class ProfileMatrix(counts: Map[DnaNucleotide, Vector[Int]]) {
  def a: Vector[Int] = counts(DnaNucleotide.A)
  def c: Vector[Int] = counts(DnaNucleotide.C)
  def g: Vector[Int] = counts(DnaNucleotide.G)
  def t: Vector[Int] = counts(DnaNucleotide.T)
  def width: Int     = a.length
}
```

The map keeps the type system honest — every column-count lookup is *by nucleotide ADT*, not by string or int index. The accessor pattern (`.a`, `.c`, `.g`, `.t`) gives the row-by-row convenience the Rosalind output format wants. The map indirection costs ~4 small allocations, which is irrelevant at n ≤ 1000.

Smart constructor enforces:
1. The map contains exactly the four `DnaNucleotide` keys.
2. All four `Vector[Int]` values have the same length.
3. (Counts being non-negative is structurally guaranteed by construction since they come from `length`-style accumulation; we do not double-check.)

**Alternative considered:** `final case class ProfileMatrix(a, c, g, t)` with four explicit `Vector[Int]` fields. Simpler structurally but loses the "indexable by nucleotide ADT" property — a caller iterating `DnaNucleotide.values.toList` would need a `match` to dispatch back into the right field. The map version makes that loop trivial. Both versions were strong options; we go with the map for ADT-symmetric iteration.

### Decision 3: `ProfileMatrixError` as an ADT (not just an exception)

```scala
sealed trait ProfileMatrixError
object ProfileMatrixError {
  final case class MissingNucleotide(missing: DnaNucleotide) extends ProfileMatrixError
  final case class UnequalColumnLengths(lengths: Map[DnaNucleotide, Int]) extends ProfileMatrixError
}
```

`ProfileMatrix.from` is called internally by `ConsensusProfile.compute` (and by trusted construction via `unsafeFrom` in the algorithm). The error ADT exists so that a public `from` is testable in isolation — the framework's convention is to expose validated constructors for every domain type.

### Decision 4: `ConsensusProfile.compute` is column-major; single pass per column

```scala
def compute(problem: ConsensusProfileProblem): ConsensusProfileResult = {
  val records = problem.records
  val width   = records.head.dna.value.length
  val nucs    = Vector(DnaNucleotide.A, DnaNucleotide.C, DnaNucleotide.G, DnaNucleotide.T)

  val columns: Vector[Map[DnaNucleotide, Int]] = (0 until width).toVector.map { col =>
    nucs.map { n =>
      val ch = DnaNucleotide.toChar(n)
      n -> records.count(_.dna.value.charAt(col) == ch)
    }.toMap
  }

  // Pivot column-major counts into row-major Map[nuc, Vector[counts]]
  val counts: Map[DnaNucleotide, Vector[Int]] =
    nucs.map(n => n -> columns.map(_(n))).toMap

  val consensus: String = columns.map { colCounts =>
    val maxCount = colCounts.values.max
    val winner   = nucs.find(n => colCounts(n) == maxCount).get  // alphabetical tie-break
    DnaNucleotide.toChar(winner)
  }.mkString

  val profile = ProfileMatrix.unsafeFrom(counts)
  val dna     = DnaString.unsafeFrom(consensus)
  ConsensusProfileResult(dna, profile)
}
```

Complexity: `O(width × #records × 4)` per column, so `O(n × m)` overall with constant factor 4. Plenty fast at n ≤ 1000, m ≤ 10.

The **alphabetical tie-break** falls out of using `nucs.find(...)` where `nucs` is the fixed `Vector(A, C, G, T)`. Tested explicitly.

`unsafeFrom` is used on both `ProfileMatrix` (we just built a complete 4-key map with equal-length values from a known-equal `width`) and `DnaString` (we built it from `DnaNucleotide.toChar`, so every character is in `{A, C, G, T}`). Both invariants are honored by construction — `unsafeFrom` is the right tool. `DnaString.unsafeFrom` already exists in the framework (used by `RnaTranslation`); we add `ProfileMatrix.unsafeFrom` following the same `private[bio]` pattern.

### Decision 5: `ConsensusProfileResult` is a plain `final case class`

```scala
final case class ConsensusProfileResult(consensus: DnaString, profile: ProfileMatrix)
```

Both fields are already validated (`DnaString` and `ProfileMatrix` each enforce their own invariants). The pair holds no further invariant — it's just a tuple with names. Public constructor (mirrors `FastaRecord` and `OverlapEdge`).

### Decision 6: `bio.{domain,algorithms}.matrix` as a new subdomain

The first occupant. The framework currently has six subdomains organized by *biology*: `nucleic`, `protein`, `genetics`, `stats`, `recurrence`, `combinatorics`, `analysis`. Profile matrices don't fit `analysis` (which has scalar/list outputs like `GcContent`, `HammingDistance`, `MotifLocations`, `OverlapGraph`) — they're *matrices*, a different shape. A future `distance-matrix` capability (UPGMA, neighbor-joining) would also belong here.

If, by the time we have only one matrix algorithm in five specs, we want to fold it back into `analysis`, that's a single rename — cheap. The split costs us nothing now and pre-positions a clean home for what's coming.

## Risks / Trade-offs

- **Risk:** `ProfileMatrix` as a `Map[DnaNucleotide, Vector[Int]]` is heavier than four explicit fields. → **Mitigation:** allocation cost is negligible (one small `Map` plus four `Vector[Int]` at most-n=1000); the type-system payoff (uniform iteration by `DnaNucleotide`) is worth it.
- **Risk:** Two new packages with one occupant each. → **Mitigation:** the project already established the "subdomain by biology/shape" convention with seven existing subdomains; this just continues it. The cost of an empty-ish package is much smaller than the cost of growing `bio.algorithms.analysis` into a junk drawer.
- **Trade-off:** Alphabetical tie-break is one of many valid choices. → **Mitigation:** spec permits any choice; alphabetical is the simplest *deterministic* one, which matters for testability and reproducibility.
- **Trade-off:** `unsafeFrom` on `ProfileMatrix` is a new internal escape hatch. → **Mitigation:** scoped `private[bio]`, only used inside the algorithm whose construction logic structurally guarantees the invariants. Public `from` still exists for external callers.
