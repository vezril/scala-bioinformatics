## Why

The reverse complement is the next Rosalind problem and the framework's first intra-DNA transformation. It also surfaces two long-deferred domain extensions: `DnaNucleotide.toChar` (mirror of the existing `RnaNucleotide.toChar`, needed whenever an algorithm produces DNA output) and `DnaNucleotide.complement` (the chemistry of base pairing — a domain-level property of the DNA alphabet). Both belong on the alphabet companion alongside `validChars` and `fromChar`.

## What Changes

- **NEW** `DnaReverseComplement` algorithm in `bio.algorithms` with `reverseComplement(dna: DnaString): DnaString` — pure, total, returns `DnaString` directly (no `Either` wrapper)
- **NEW** `def complement(n: DnaNucleotide): DnaNucleotide` on the `DnaNucleotide` companion (A↔T, C↔G)
- **NEW** `def toChar(n: DnaNucleotide): Char` on the `DnaNucleotide` companion (mirror of `RnaNucleotide.toChar`)
- `DnaString` gains a `private[bio] def unsafeFrom(s: String): DnaString` for trusted internal callers — mirrors the existing `RnaString.unsafeFrom`. This is implementation, not a spec-level change.

## Capabilities

### New Capabilities

- `dna-reverse-complement`: Pure algorithm producing the reverse complement of a `DnaString` by reversing the sequence and complementing each base via `DnaNucleotide.complement`

### Modified Capabilities

- `dna-nucleotide`: Add two companion methods — `toChar` (existing pattern, was missing on DNA side) and `complement` (new domain operation, A↔T, C↔G)

## Impact

- New files: `src/main/scala/bio/algorithms/DnaReverseComplement.scala`, `src/test/scala/bio/algorithms/DnaReverseComplementSpec.scala`
- Updated files: `src/main/scala/bio/domain/DnaNucleotide.scala`, `src/main/scala/bio/domain/DnaString.scala`, `src/test/scala/bio/domain/DnaNucleotideSpec.scala`
- No new dependencies, no `build.sbt` changes
- All existing 52 tests must continue passing
