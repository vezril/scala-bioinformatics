## Context

The first six capabilities handled single-sequence transformations (count, transcribe, reverse-complement) or pure-math problems (Mendelian, Fibonacci). Spec 7 changes both halves of the equation: input arrives as a *file format* (FASTA, multiple records, multi-line sequences) and output requires picking the maximum from a *list*. This is also the first capability where the question "what package does this belong in?" is non-obvious — parsing is neither a domain ADT nor a biological algorithm.

Current state: no FASTA support, no GC-content type, no collection-oriented algorithms.

## The math

GC content of a DNA string is the percentage of bases that are `G` or `C`:

```
gc_content(s) = 100 * (count(s, 'G') + count(s, 'C')) / length(s)
```

For the Rosalind sample sequence (`Rosalind_0808`, length 87):
- G + C count = 53
- 100 * 53 / 87 ≈ 60.919540% ✓

Empty string is the only edge case requiring a convention — `0/0` is undefined. We define `gc_content("") = 0.0` (no G or C bases present).

Rosalind allows absolute error of 0.001 in decimal output; tests use a tolerance of `±1e-3` on the percentage value (e.g., expected `60.919540`, accept `60.918540..60.920540`).

## FASTA format

```
>Rosalind_6404
CCTGCGGAAGATCGGCACTAGAATAGCCAGAACCGTTTCTCTGAGGCTT
CCGGCCTTCCCTCCCACTAATAATTCTGAGG
>Rosalind_5959
CCATCGGTAGCGCATCCTTAGTCCAATTAAGTCCCTATCCAGGCGCTCCGCCGAAGGTCTAT
...
```

Parsing rules:
- A line starting with `>` begins a new record; the rest of that line is the ID (trimmed)
- Subsequent lines, until the next `>` line, concatenate (after trimming each line) to form the DNA sequence
- Empty lines are ignored
- An empty `>` line (header with no id) is an `EmptyId` error
- Sequence content before any header line is a `MissingHeader` error
- A DNA sequence that fails `DnaString.from` validation is an `InvalidDna(id, cause)` error — `cause` carries the underlying `SequenceError`
- Trailing whitespace within IDs is trimmed (the sample shows `>Rosalind_0808 ` with a trailing space)

## Goals / Non-Goals

**Goals:**
- Model FASTA records with a simple case class (`FastaRecord(id, dna)`) — no smart constructor needed; the parser is the validation point
- Provide a pure parser returning `Either[FastaParseError, List[FastaRecord]]`
- Model GC content as a validated `[0, 100]` percentage with smart constructors
- Provide a total `GcContent.of(dna)` factory — empty DNA returns `GcContent(0.0)`
- Provide `HighestGc.find` returning `Option[(FastaRecord, GcContent)]` — `None` for empty input, first record wins ties

**Non-Goals:**
- File I/O — `FastaParser.parse` takes a `String`. File reading lives at a future IO boundary (Cats Effect wrapping)
- FASTA *output* (writing records back to a string) — read-only for now
- Quality scores, FASTQ format, or other sequence formats — separate concerns
- Protein FASTA — DNA only for now; the parser is hardcoded to `DnaString` because that's what the framework currently supports
- Streaming parser for huge files — `String` input only; entire file in memory. Adequate for Rosalind's "at most 10 strings of at most 1 kbp each"

## Decisions

### New package `bio.parsing`

**Decision**: FASTA parsing lives in `bio.parsing.FastaParser`, not `bio.algorithms` or `bio.domain`.

**Rationale**: Parsing is structural (string format → typed records), not biological. Putting it in `bio.algorithms` would muddy the package's semantic meaning ("bioinformatics algorithms"). Adding a new top-level package is cheap and improves discoverability — future format parsers (FASTQ, GenBank, etc.) have a natural home.

**Alternative considered**: Static `parse` method on `FastaRecord` companion. Rejected — the parser returns a *list* of records, so the method's relationship to the singular type is awkward.

### `FastaRecord` is a public case class, no smart constructor

**Decision**: `final case class FastaRecord(id: String, dna: DnaString)` — public constructor.

**Rationale**: `dna` is already a validated `DnaString`. `id` is a label without strong invariants — the FASTA spec allows any text after `>`. The parser is the validation point: if an `id` is empty, the parser returns `EmptyId` before constructing a `FastaRecord`. Adding a smart constructor would force a separate `FastaRecord.from` that simply re-validates `id`, which provides no additional safety beyond what the parser already gives.

**Trade-off**: A caller could construct `FastaRecord("", validDna)` directly, bypassing the parser. This is acceptable — the value is structurally valid; the "empty id" semantic is enforced by the parser, not the type. Same precedent as `DnaNucleotideCounts`, which has a public constructor.

### `GcContent` uses `sealed abstract case class` per project convention

**Decision**: `sealed abstract case class GcContent(value: Double)` with `GcContent.from(value: Double): Either[GcContentError, GcContent]` smart constructor (validates `[0, 100]`, no NaN/infinity) and `private[bio] def unsafeFrom(value: Double): GcContent` for trusted internal use.

**Rationale**: Same pattern established by `Probability`, `Population`, and `RabbitProblem` after the leaky-`apply` fix. `GcContent` has a meaningful invariant; the canonical idiom locks it down. The `unsafeFrom` is for the algorithm itself — math guarantees the result is in range, no need to revalidate.

### `GcContent.of(dna)` is total — empty DNA returns `GcContent(0.0)`

**Decision**: `GcContent.of(dna: DnaString): GcContent` (no `Option` or `Either`). Empty `DnaString` returns `GcContent(0.0)`.

**Rationale**: Mathematically, GC content of empty is `0/0`, undefined. Practically, "what percentage of the (empty) sequence is GC?" is meaningfully zero — there are no G or C bases. This convention keeps the function total and avoids forcing every caller to handle a `None` case that arises only from a degenerate input. Document the convention clearly.

**Alternative considered**: `Option[GcContent]`. Rejected — pushes empty-string handling onto every caller for marginal semantic benefit.

### Stored as percentage `[0, 100]`, not fraction `[0, 1]`

**Decision**: `GcContent.value` is the percentage (e.g., `60.919540`), not the fraction (e.g., `0.60919540`).

**Rationale**: The Rosalind output IS percentage. The domain term "GC content" colloquially refers to the percentage. Storing as fraction would require a `*100` translation at every output point. Storing as percentage matches the user's mental model and the I/O format.

**Trade-off**: Diverges from `Probability` which stores `[0, 1]`. Acceptable — these are different concepts. Probability has a unit-circle math meaning; GC content has a presentation-layer percentage meaning.

### `HighestGc.find` returns `Option[(FastaRecord, GcContent)]`

**Decision**: `def find(records: List[FastaRecord]): Option[(FastaRecord, GcContent)]`.

**Rationale**:
- `Option` for the empty-list case — there is no maximum of zero items
- Tuple instead of separate methods so the caller doesn't recompute GC after finding the winning record
- Tie-breaking: first occurrence wins (deterministic). Implemented via `foldLeft` with strictly-greater comparison.

**Alternative considered**: Returning just `FastaRecord` and forcing the caller to call `GcContent.of(winner.dna)` separately. Rejected — wastes the GC computation already performed during the scan.

**Alternative considered**: Throwing on empty list. Rejected — that's not how the framework handles empty/degenerate input (cf. `Population.from` which uses `Either`, `HighestGc` uses `Option`).

### Parser implementation: line-based fold

**Decision**: `FastaParser.parse` splits the input into lines (via `linesIterator`), trims each, filters empty lines, then folds into an accumulator state machine: `(currentId: Option[String], currentSeq: StringBuilder, completed: List[FastaRecord], error: Option[FastaParseError])`.

**Rationale**: `foldLeft` over lines is the most idiomatic Scala for this kind of grouped sequential parsing. No mutable iteration. The state machine handles "in-progress record" naturally — when a new `>` line appears, the previous record (if any) gets finalized and appended.

**Why `StringBuilder` inside the accumulator**: GC content is computed on the *final* string; accumulating sequence chunks via repeated `String` concatenation would be O(n²). `StringBuilder` keeps it O(n). The builder is local to the fold's accumulator — it doesn't leak as mutable state visible outside. Pragmatically the right choice; if the framework's "no mutable collections" rule is interpreted strictly we use `Vector[String]` and `mkString` at the end (slower but pure-immutable).

I'll lean toward `Vector[String]` + `mkString` for strict purity unless performance becomes a concern at the framework's scale (Rosalind's 10 records × 1 kbp).

## Risks / Trade-offs

- [`FastaRecord` allows empty `id` via direct construction] The parser rejects empty ids, but a direct `FastaRecord("", dna)` is legal. → Acceptable; the parser is the documented entry point. If callers start using direct construction, revisit with a smart constructor.
- [`GcContent.of(empty)` returning `0.0` is a convention] Mathematically arguable. → Document clearly in scaladoc and the spec scenarios.
- [Parser is not streaming] Loads the entire input into memory. → Adequate for Rosalind; if multi-gigabyte FASTA becomes a use case, introduce a streaming parser as a separate capability.
- [Stored as percentage diverges from `Probability` pattern] Slight inconsistency in how "bounded-real-domain types" are stored. → Acceptable; the divergence is justified by output format alignment and document it in the scaladoc.
- [Tolerance for tests] Rosalind allows 0.001 absolute error. We test with `±1e-3` for percentage and `±1e-9` for analytic edge cases (0.0, 100.0).
