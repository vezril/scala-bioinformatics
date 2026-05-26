## Context

The framework's domain layer currently encodes nucleotide alphabets implicitly: a `Set[Char]` in `DnaString` and raw `Char` literal arms in `DnaNucleotides.count`. There is no type that *is* a nucleotide. RNA is unrepresented entirely. This change introduces explicit sealed ADTs for both alphabets, making the type system enforce what previously relied on runtime validation and developer discipline.

Current state:
- `DnaString(value: String)` — validated, but alphabet defined by a hardcoded `Set('A','C','G','T')`
- `DnaError` — validation errors, DNA-only name despite generic structure
- `NucleotideCounts(a,c,g,t)` — DNA-specific name not reflected in its identifier
- `DnaNucleotides.count` — pattern-matches on `Char` literals; no compile-time alphabet exhaustiveness

## Goals / Non-Goals

**Goals:**
- Introduce `DnaNucleotide` and `RnaNucleotide` as first-class sealed ADTs
- Make `DnaString` derive its valid alphabet from `DnaNucleotide.validChars` — one source of truth
- Add `RnaString` as a fully validated sequence type using `RnaNucleotide.validChars`
- Rename `DnaError` → `SequenceError` as a shared validation error type
- Rename `NucleotideCounts` → `DnaNucleotideCounts`; add `RnaNucleotideCounts`
- Update `DnaNucleotides.count` to dispatch on `DnaNucleotide` values, not raw `Char` literals

**Non-Goals:**
- Transcription algorithms (DNA→RNA, RNA→DNA) — follow-on change
- RNA nucleotide counting algorithm — follow-on change
- Ambiguity codes (`N`, `R`, `Y`, etc.) — out of scope for freshman level
- Generic `Sequence[N]` abstraction — premature; add when a third molecule type appears

## Decisions

### Separate sealed hierarchies, not a shared parent

**Decision**: `DnaNucleotide` and `RnaNucleotide` are independent sealed traits. There is no shared `Nucleotide` supertype.

**Rationale**: A shared supertype that includes both `T` and `U` would allow values of the wrong molecule type to appear in functions expecting a specific alphabet. The compile-time safety goal requires that `DnaNucleotide` and `RnaNucleotide` are entirely distinct types — a function accepting `DnaNucleotide` cannot receive an `RnaNucleotide.U` without a type error.

**Alternative considered**: Single `Nucleotide` ADT containing all five bases. Rejected because it admits `U` in a DNA context at the type level, defeating the purpose.

### `SequenceError` as a shared validation error type

**Decision**: Rename `DnaError` → `SequenceError`. Both `DnaString.from` and `RnaString.from` return `Either[SequenceError, _]`.

**Rationale**: The two error cases (`InvalidCharacter`, `ExceedsMaxLength`) carry no molecule-specific information — the same structure applies to both. A shared type reduces duplication without losing information. If future error cases need molecule-specific semantics, `SequenceError` can be split at that point.

**Alternative considered**: Separate `DnaError` / `RnaError` mirroring each other. Rejected as identical-structure duplication with no safety benefit at this stage.

### Nucleotide companion provides `validChars` and `fromChar`

**Decision**: Each companion object exposes:
- `val validChars: Set[Char]` — used by sequence smart constructors for validation
- `def fromChar(c: Char): Option[N]` — used by algorithms to lift a `Char` into the ADT

**Rationale**: `DnaString` currently hardcodes `Set('A','C','G','T')`. Deriving it from `DnaNucleotide.validChars` means the alphabet is defined once. Adding a new nucleotide automatically extends validation. The `fromChar` companion method is the bridge between the `String`-backed sequence and the ADT vocabulary used in algorithms.

### Keep `String`-backed sequences; nucleotide ADTs are the algorithm vocabulary

**Decision**: `DnaString` and `RnaString` remain backed by a `String`, not `IndexedSeq[DnaNucleotide]`.

**Rationale**: Genomic sequences are arbitrarily large. An `IndexedSeq[DnaNucleotide]` for even a modest bacterial genome would be orders of magnitude larger and slower than a `String`. The nucleotide ADTs serve as the *vocabulary* of algorithms — what you pattern-match against — not the storage format.

### `DnaNucleotides.count` dispatches via `DnaNucleotide.fromChar`

**Decision**: The `foldLeft` in `DnaNucleotides.count` lifts each `Char` to `Option[DnaNucleotide]` via `fromChar`, then pattern-matches on the ADT. The wildcard arm is unreachable for valid `DnaString` input.

**Rationale**: This removes the raw `Char` literals (`'A'`, `'C'`, `'G'`, `'T'`) from the algorithm. If `DnaNucleotide` gains a new case object, the pattern match becomes non-exhaustive and the compiler warns immediately.

## Risks / Trade-offs

- [Breaking rename: `DnaError` → `SequenceError`] All existing call sites must be updated. → Contained: only `DnaString`, its spec, and `DnaStringSpec` reference this type today. Full list is in the task sequence.
- [Breaking rename: `NucleotideCounts` → `DnaNucleotideCounts`] Same scope impact. → Contained: `DnaNucleotides.count`, `DnaNucleotidesSpec`, `NucleotideCountsSpec`.
- [`fromChar` returns `Option`] The wildcard arm in `count` is dead code for valid `DnaString` input, but must still be handled. → Accept: the `Option` fold with a no-op on `None` is idiomatic and makes the invariant explicit rather than hidden.

## Migration Plan

All changes are internal to the framework. No external API exists yet. The migration is a mechanical rename-and-update within `src/`:

1. Create new types (`DnaNucleotide`, `RnaNucleotide`, `SequenceError`, `RnaString`, `RnaNucleotideCounts`)
2. Update `DnaString` to use `DnaNucleotide.validChars`
3. Rename `NucleotideCounts` → `DnaNucleotideCounts` and update all references
4. Rename `DnaError` → `SequenceError` and update all references
5. Update `DnaNucleotides.count` to dispatch on `DnaNucleotide`
6. Run full test suite — all 15 existing tests must continue to pass
