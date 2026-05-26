## Context

The `nucleotide-adts` change established `DnaString`, `RnaString`, `DnaNucleotide`, and `RnaNucleotide` as fully independent, compile-time-safe domain types. The transcription algorithm is the first operation that crosses the DNA/RNA boundary. It was explicitly deferred from `nucleotide-adts` and is now unblocked.

Current state:
- `DnaNucleotide.fromChar(c: Char): Option[DnaNucleotide]` — lifts a char to the DNA ADT
- `RnaNucleotide` has `A`, `C`, `G`, `U` case objects
- `RnaString` is validated and String-backed
- `DnaNucleotides.count` demonstrates the pattern: `foldLeft` over `dna.value`, dispatch via `fromChar`

## Goals / Non-Goals

**Goals:**
- Implement `RnaTranscription.transcribe(dna: DnaString): RnaString` as a pure function
- Dispatch on `DnaNucleotide` values (not raw `Char` literals) — consistent with the pattern established in `DnaNucleotides.count`
- Map T→U; preserve A, C, G

**Non-Goals:**
- Reverse transcription (RNA→DNA) — not in this change
- Complement or reverse complement — separate future algorithms
- IO wrapping — transcription is pure; the IO boundary stays at `Main`
- Validation of input — `DnaString` is already validated by construction

## Decisions

### Return type is `RnaString`, not `Either`

**Decision**: `transcribe` returns `RnaString` directly, not `Either[SequenceError, RnaString]`.

**Rationale**: The input is a `DnaString` — already validated to contain only `{A, C, G, T}`. Every DNA nucleotide has a defined RNA counterpart. The function is total; there are no failure cases. Wrapping in `Either` would add noise and force callers to handle an error that can never occur.

### Implementation via `map` over chars, not `foldLeft`

**Decision**: Build the transcribed string via `dna.value.map(transcribeChar).mkString`, where `transcribeChar` dispatches on `DnaNucleotide.fromChar`.

**Rationale**: Transcription is a character-by-character mapping — `map` expresses this more directly than `foldLeft`. The result is then wrapped in `RnaString` via its companion. Since `DnaString` guarantees valid input, `fromChar` will always return `Some`; the `None` arm is unreachable but handled explicitly for type safety.

**Alternative considered**: `foldLeft` accumulating a `StringBuilder`. Rejected — `map` + `mkString` is more idiomatic for a pure character transformation with no accumulator state.

### `RnaString` constructed directly, bypassing smart constructor

**Decision**: The output `RnaString` is constructed via `new RnaString(transcribed)` (accessing the private constructor through a package-private or companion factory), not `RnaString.from(...)`.

**Rationale**: The transcribed string is guaranteed valid by construction — it contains only `{A, C, G, U}` because every input char was a valid `DnaNucleotide` and was mapped to a valid `RnaNucleotide`. Running `RnaString.from` would re-validate an already-valid string unnecessarily. An `unsafeFrom` or direct companion constructor is the idiomatic pattern for this case.

**Implementation note**: Add `def unsafeFrom(s: String): RnaString` to `RnaString`'s companion for use by trusted internal callers.

## Risks / Trade-offs

- [`unsafeFrom` bypasses validation] If called with invalid input it produces an invalid `RnaString`. → Mitigate by keeping it package-private or documenting it as internal-only. Callers outside `bio.algorithms` should always use `RnaString.from`.
- [Empty input] `transcribe(DnaString(""))` should return `RnaString("")` — a valid empty RNA string. → Covered by test.
