## Context

Specs 2-7 covered single-strand transformations on DNA/RNA and pure-math problems. Spec 8 introduces a different shape of problem: a fixed reference *table* (the standard genetic code, 64 codons → 20 amino acids + Stop) plus a stateful read of an input sequence three letters at a time. This is also the framework's first encounter with the protein "layer" of the central dogma — every future problem touching open reading frames, splicing, or proteomics will reuse the types added here.

Current state: `RnaString`, `RnaNucleotide`, and `RnaTranscription` (DNA → RNA) exist. No notion of amino acids, codons, the genetic code, or proteins. `RnaString.MaxLength = 1000`, but Rosalind's spec 8 allows mRNA inputs "up to 10 kbp" — the existing cap blocks that.

## Goals / Non-Goals

**Goals:**
- Model the 20 standard amino acids as a closed sealed ADT, each with its single-letter code
- Model a `Codon` as a triple of validated `RnaNucleotide`s — a public case class (no smart constructor needed since the components are already validated)
- Provide the standard RNA-to-amino-acid genetic code as a single pure lookup function `GeneticCode.translate(c: Codon): CodonOutcome`
- Provide a validated `ProteinString` domain type with a smart constructor that locks the invariant (only the 20 valid letters)
- Provide `RnaTranslation.translate(rna: RnaString): Either[TranslationError, ProteinString]` — splits RNA into codons, halts on first Stop
- Raise `RnaString.MaxLength` from 1000 to 10000 to accept Rosalind's spec-8 inputs

**Non-Goals:**
- Alternate genetic codes (mitochondrial, ciliate nuclear, etc.) — only the standard table for now
- Reverse translation (protein → RNA), since it's ambiguous and not asked for
- Frame detection / ORF finding / 6-frame translation — spec 8 says "the protein string encoded by s", implying a single fixed reading frame starting at index 0
- Streaming translation for very large RNA — 10 kbp fits comfortably in memory
- Modeling rare amino acids (selenocysteine U, pyrrolysine O) — only the standard 20
- Promoting `ProteinString` to a max-length-bounded type beyond the natural 1/3 of `RnaString.MaxLength` — no separate length cap; the cap follows from RNA input

## Decisions

### `AminoAcid` is a sealed trait with 20 case objects, each carrying a `code: Char`

**Decision**: `sealed trait AminoAcid { def code: Char }` with 20 case objects (F, L, I, V, S, P, T, A, Y, H, N, D, Q, K, E, C, R, G, W, M).

**Rationale**: Same pattern as `DnaNucleotide` and `RnaNucleotide` — a closed alphabet is a sealed-trait + case-objects ADT. The `code` field bundles the canonical IUPAC single-letter representation onto each variant, so callers can produce the printable form without an external lookup table.

**Alternative considered**: An enum-like value class wrapping `Char`. Rejected — loses exhaustivity checking on pattern matches and conflates "valid char" with "amino acid".

### `Codon` is a `final case class Codon(first, second, third: RnaNucleotide)` — public constructor

**Decision**: Public case class taking three already-validated `RnaNucleotide`s.

**Rationale**: The components are themselves a sealed ADT; any triple of them is a structurally valid codon. There are no further invariants (every combination is a real codon — that's the whole 64-entry table). Adding a smart constructor would add no safety.

Helpers on the companion:
- `Codon.fromChars(a, b, c: Char): Option[Codon]` — useful for spec scenarios and tests
- `Codon.parseAll(rna: RnaString): Either[TranslationError, Vector[Codon]]` — splits a validated RNA string into successive codons. Returns `LengthNotMultipleOfThree` if the length isn't divisible by 3.

### `CodonOutcome` is a sealed ADT — `AminoAcidProduct(aa)` or `Stop`

**Decision**: `sealed trait CodonOutcome` with `final case class AminoAcidProduct(aa: AminoAcid) extends CodonOutcome` and `case object Stop extends CodonOutcome`.

**Rationale**: Each of the 64 codons maps to *either* an amino acid *or* a Stop signal. Using a single ADT makes the codon table total (no Option/Either at the table level) and lets the translation algorithm pattern-match exhaustively.

**Alternative considered**: `Either[Stop.type, AminoAcid]`. Rejected — `Either` carries an "error vs success" connotation, but `Stop` is not an error; it's a valid biological signal. A named ADT communicates intent better.

### `GeneticCode` is a single `object` with one public method, `translate`

**Decision**: `object GeneticCode { def translate(c: Codon): CodonOutcome = ... }`. The 64-entry mapping is implemented as one pattern match over the `Codon(first, second, third)` triple — exhaustive checking confirms all 64 cases are handled.

**Rationale**:
- Pure data + pure function — no I/O, no state
- A pattern match keeps each entry as a single line (`case Codon(U, U, U) => AminoAcidProduct(F)`) and the compiler enforces exhaustiveness; if a future change adds a nucleotide, the warning lights up exactly here
- No private `Map[Codon, CodonOutcome]` because the map would require boxing the keys and lose the exhaustiveness guarantee

**Alternative considered**: A `Map[Codon, CodonOutcome]` initialized at object load. Rejected — same answer at runtime, but loses compile-time guarantees and adds initialization order concerns.

**Alternative considered**: A method per amino acid. Rejected — 64-way dispatch is the natural fit, not 21-way.

### `ProteinString` uses `sealed abstract case class` per project convention

**Decision**: `sealed abstract case class ProteinString(value: String)` with companion `from(s: String): Either[ProteinError, ProteinString]` (validates every char is in the 20-letter alphabet) and `private[bio] unsafeFrom(s: String): ProteinString` for trusted internal use.

The companion also exposes a total `fromAminoAcids(aas: Seq[AminoAcid]): ProteinString` — assembling a protein from already-typed amino acids cannot fail.

**Rationale**: Same pattern as `Probability`, `Population`, `RabbitProblem`, `GcContent`. The `apply` and `copy` are not public, so direct `ProteinString("MAMA")` and `.copy(value = "X")` are compile errors.

**Trade-off**: Diverges from `RnaString`/`DnaString` which use `final class extends AnyVal`. Acceptable — these are different historical patterns in the codebase (older nucleotide types use AnyVal value classes, newer bounded-value types use sealed abstract case class). I follow the newer pattern; consistency with the newer family (`GcContent`, `Probability`, `Population`) outweighs consistency with the older family.

### Translation halts at the first Stop; no error if there is no Stop

**Decision**: `RnaTranslation.translate` walks codons left-to-right, accumulating amino acids until either (a) a Stop codon appears, or (b) the input is exhausted. Both terminations produce `Right(ProteinString(...))`.

**Rationale**:
- Spec 8 expects "the protein string encoded by s" — when the input ends with Stop (as in the Rosalind sample), Stop is excluded from output
- When the input lacks a Stop, falling off the end is also a valid termination — there's no biology rule that says "input without Stop is malformed"; truncated mRNA exists

**Alternative considered**: Treat absence-of-stop as `MissingStopCodon` error. Rejected — adds a failure mode that the Rosalind sample doesn't require and that has no good real-world interpretation.

### Length-not-multiple-of-3 is the only translation error

**Decision**: `sealed trait TranslationError { case class LengthNotMultipleOfThree(length: Int) extends TranslationError }`. Translation returns `Either[TranslationError, ProteinString]`. The error fires when `rna.value.length % 3 != 0`.

**Rationale**:
- Empty RNA (length 0) is valid → `Right(ProteinString(""))` (0 is divisible by 3)
- An RnaString is already validated for its alphabet — every chunk of 3 will be a valid codon, so we never have a "bad codon" runtime case
- Stop is not an error (see prior decision)

### `RnaString.MaxLength` bumped from 1000 to 10000

**Decision**: Single-line change `private val MaxLength = 10000` in `RnaString.scala`.

**Rationale**: Spec 8 explicitly allows 10 kbp. This is a backwards-compatible relaxation — every previously-valid RnaString is still valid; only the boundary case "1001..10000" newly becomes valid. The `SequenceError.ExceedsMaxLength` case still exists; it now fires at the new threshold.

**Alternative considered**: Parameterize `RnaString` by a `maxLength: Int` configuration. Rejected — over-engineering. A single global cap matched to the largest spec-defined input keeps the API simple.

**Alternative considered**: Bump `DnaString.MaxLength` too for symmetry. Rejected — no spec yet requires DNA > 1000. Keep the DNA cap as is; bump it when a problem actually needs it.

### `RnaTranslation` lives in `bio.algorithms`, not `bio.domain`

**Decision**: `RnaTranslation` is an object in `bio.algorithms` (joining `DnaNucleotides`, `RnaTranscription`, `DnaReverseComplement`, etc.). `GeneticCode` lives in `bio.domain`.

**Rationale**: The genetic code is *data* (a fixed table that biology gives us); `RnaTranslation` is an *algorithm* (sequencing through codons, accumulating output, stopping on a sentinel). Same line-of-separation as elsewhere: `bio.domain` for data and bounded value types, `bio.algorithms` for the procedural transformations.

## Risks / Trade-offs

- [Pattern-matching the 64-entry codon table is verbose] One `case` per codon = 64 lines. → Acceptable — explicit, exhaustive, and the compiler catches any missing entry. A future "compact" implementation could use a `Map` lookup but loses exhaustiveness checking.
- [Bumping `RnaString.MaxLength` to 10000 affects the rna-sequence capability spec] One scenario in the existing spec changes its boundary value. → Mitigation: this is a `MODIFIED Requirements` delta in the spec. No previous behavior breaks (the relaxation is monotone).
- [No-stop input silently produces a partial protein] A caller expecting a stop codon (typical of well-formed mRNA) won't see an error if Stop is missing. → Acceptable per the "translate as much as possible" interpretation. Callers needing strict validation can check `rna.value.length % 3` themselves before calling, or compare returned length.
- [`AminoAcid.code` overlap with `RnaNucleotide`/`DnaNucleotide` letters] Both `A` (amino acid Alanine) and `RnaNucleotide.A` exist. → Acceptable — they're in different sealed ADTs in the same package; pattern matches disambiguate via the type. Documentation makes the conflict explicit.
- [`ProteinString` has no max-length cap] Length is bounded only indirectly by `RnaString.MaxLength / 3`. → Acceptable for now; revisit if proteins enter the framework via a different path.
