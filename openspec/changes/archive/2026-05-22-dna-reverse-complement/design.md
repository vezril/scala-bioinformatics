## Context

This is the framework's fourth Rosalind capability. The first three (`project-init`, `nucleotide-adts`, `rna-transcription`) established the patterns: validated sequence types, sealed alphabet ADTs with `validChars`/`fromChar`/`toChar` companions, pure algorithms in `bio.algorithms` that dispatch on ADT values, `unsafeFrom` for trusted internal construction.

Reverse complement is conceptually similar to RNA transcription: read each base, apply a per-base transformation, produce a new sequence. The differences:
- Input and output are both `DnaString` (no inter-molecule conversion)
- Sequence is **reversed** before/after complementing
- The per-base mapping is `complement` (A↔T, C↔G), a chemistry property of DNA pairing

Current state of relevant code:
- `DnaNucleotide` has `validChars` and `fromChar` only — no `toChar` (it was added to `RnaNucleotide` during `rna-transcription` but DNA's wasn't needed at the time)
- `DnaString` has `from` but no `unsafeFrom` (only `RnaString` has it)

## Goals / Non-Goals

**Goals:**
- Implement `DnaReverseComplement.reverseComplement(dna: DnaString): DnaString` as a pure, total function
- Add `complement` to `DnaNucleotide` companion as a domain-level property of the DNA alphabet
- Add `toChar` to `DnaNucleotide` companion to bring it to parity with `RnaNucleotide`
- Add `unsafeFrom` to `DnaString` (implementation detail, not spec-level)

**Non-Goals:**
- RNA reverse complement — different complement rules (A↔U, C↔G); separate future change
- Generic `ReverseComplement[N]` abstraction — premature; introduce when a second molecule needs it
- Refactoring `RnaTranscription` to use the new `DnaNucleotide.toChar` — out of scope; can be a follow-on cleanup

## Decisions

### `complement` lives on the alphabet companion, not in the algorithm

**Decision**: Define `def complement(n: DnaNucleotide): DnaNucleotide` on the `DnaNucleotide` companion.

**Rationale**: Base complementarity is a property of DNA chemistry, not a property of the reverse-complement algorithm. Putting it on the companion makes it reusable (e.g., a future "forward complement" or "complement at position i" algorithm) and groups it with the other alphabet-level utilities (`validChars`, `fromChar`, `toChar`). This is the same reasoning that put `fromChar`/`toChar` on the companion in the first place.

**Alternative considered**: Keep `complement` as a private helper inside `DnaReverseComplement`. Rejected — it conflates algorithm logic with domain semantics.

### Add `toChar` now even though `complement` could return chars directly

**Decision**: Add `def toChar(n: DnaNucleotide): Char` to the `DnaNucleotide` companion.

**Rationale**: Symmetry with `RnaNucleotide.toChar`. The framework skill captures the companion pattern as "validChars + fromChar + toChar"; DNA was incomplete. The algorithm uses it to lower the complemented ADT back to a char for the output `String`. If we instead made `complement` return a `Char`, we'd be hiding an ADT round-trip inside the algorithm and creating asymmetry between molecules.

### Reverse the chars, not the ADT sequence

**Decision**: Reverse the underlying `String` of `DnaString`, then complement each char.

**Rationale**: `dna.value` is a `String`. Reversing a `String` is `O(n)` and natively supported. Reversing a `Seq[DnaNucleotide]` would require lifting every char through `fromChar` first — equivalent work, more allocation. Since complement and reverse commute, the order is interchangeable; doing reverse on the String first keeps the algorithm closer to standard string-processing idioms.

**Order chosen**: complement first, then reverse (or equivalently, reverse first, then complement — both produce identical output). Specifically: `dna.value.iterator.flatMap(c => fromChar(c).map(complement).map(toChar)).mkString.reverse`.

### Return `DnaString` directly via `unsafeFrom`

**Decision**: The algorithm constructs the output `DnaString` via `DnaString.unsafeFrom`, not `DnaString.from(...).getOrElse(...)`.

**Rationale**: The output is guaranteed valid by construction — every char of the input was a valid `DnaNucleotide`, and every complement is also a valid `DnaNucleotide` (the complement operation is total on the DNA alphabet). Re-validating wastes cycles and obscures the invariant. This mirrors the decision made in `rna-transcription` for `RnaString.unsafeFrom`. Add the same affordance to `DnaString` for consistency.

## Risks / Trade-offs

- [`complement` placement risks coupling] If a future operation needs a *different* notion of complement (e.g., methylation-aware), this single method becomes ambiguous. → Acceptable for now; rename or extend if it ever becomes ambiguous.
- [`DnaString.unsafeFrom` exposure] Same risk as `RnaString.unsafeFrom`: misuse produces an invalid sequence. → Same mitigation: `private[bio]` scope, doc comment marking it internal-only.
- [Test of palindromic self-complement] `GGCC.reverseComplement == GGCC`. Worth including — it proves the reverse and complement compose correctly rather than passing trivially.
