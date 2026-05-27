## Context

Spec 21 takes a DNA string `s` (≤ 1 kbp) and a collection of intron substrings, and returns the protein resulting from removing the introns and then transcribing + translating the resulting exon DNA. The framework already supplies:

- `DnaString` (validated alphabet `{A,C,G,T}`, length cap 1000) in `bio.domain.nucleic`
- `RnaTranscription.transcribe(dna: DnaString): RnaString` in `bio.algorithms.nucleic`
- `RnaTranslation.translate(rna: RnaString): Either[TranslationError, ProteinString]` in `bio.algorithms.protein`
- `ProteinString`, `TranslationError` in `bio.domain.protein`

The fresh step is **intron splicing** — removing every occurrence of each intron substring from the source DNA. This change adds that primitive plus a composite that wires the three steps together.

## Goals / Non-Goals

**Goals:**
- `IntronSplicing.splice(source: DnaString, introns: Vector[DnaString]): DnaString` — a pure, total primitive in `bio.algorithms.nucleic` that removes intron substrings.
- `RnaSplicingProblem` validated parameter bundle in `bio.domain.protein` — wraps the two inputs of the composite algorithm, rejects empty introns.
- `RnaSplicing.transcribeAndTranslate(problem): Either[TranslationError, ProteinString]` in `bio.algorithms.protein` — the full pipeline.
- Faithful to the Rosalind sample: input `(source, [intron1, intron2])` produces `"MVYIADKQHVASREAYGHMFKVCA"`.

**Non-Goals:**
- A new FASTA file reader. Callers use the existing `FastaFileReader.read` and destructure `records.head` → source, `records.tail.map(_.dna)` → introns themselves.
- Verifying that each intron actually appears in the source. Per Rosalind, introns are guaranteed substrings; if a caller supplies a non-matching intron the algorithm silently no-ops on it (matches the natural semantics of "remove every occurrence" — there are none).
- Handling overlapping introns or order-of-removal ambiguity. The Rosalind problem says "Only one solution will exist for the dataset provided"; we trust that precondition and remove introns in input order with iterative `String.replace` semantics.
- A `LogProbability`-style wrapper for the output. Output is the existing `ProteinString` type — no new domain types are needed at the output level.

## Decisions

### Decision 1: `IntronSplicing.splice` takes bare params (not the bundle)

```scala
object IntronSplicing {
  def splice(source: DnaString, introns: Vector[DnaString]): DnaString = {
    val spliced = introns.foldLeft(source.value) { (acc, intron) =>
      acc.replace(intron.value, "")
    }
    DnaString.unsafeFrom(spliced)
  }
}
```

The primitive is in `bio.algorithms.nucleic` (operates on DNA → DNA). It does *not* take `RnaSplicingProblem` because that bundle lives in `bio.domain.protein` — taking it as a parameter would invert the dependency direction (the more-general primitive would depend on the more-specific composite's input type). Bare params keep the primitive callable by anyone who has a `DnaString` and a `Vector[DnaString]`, including future algorithms that aren't about protein at all.

`unsafeFrom` is justified: the result string is `source.value` with substrings removed — every character is still from the validated alphabet, and the length is ≤ `source.value.length ≤ 1000`. Both invariants are preserved by construction.

**Implementation choice — `String.replace` vs hand-rolled loop:**
- Java's `String.replace(CharSequence, CharSequence)` is the *literal* (non-regex) replace-all. It iterates left-to-right replacing every disjoint occurrence in a single pass. This is exactly what Rosalind wants.
- `.foldLeft` over `introns` means introns are removed in input order. The Rosalind precondition guarantees this produces the unique correct answer.
- Empty intron behavior: `"abc".replace("", "")` returns `"abc"` in JDK 11+ (no-op). Older JDKs spread the empty match — but our `RnaSplicingProblem.from` already rejects empty introns at the composite boundary, and bare-params callers are warned in scaladoc.

### Decision 2: `RnaSplicingProblem` is a validated bundle with one error case

```scala
sealed abstract case class RnaSplicingProblem(source: DnaString, introns: Vector[DnaString])

object RnaSplicingProblem {
  def from(source: DnaString, introns: Vector[DnaString]): Either[RnaSplicingProblemError, RnaSplicingProblem] =
    introns.zipWithIndex.find { case (intron, _) => intron.value.isEmpty } match {
      case Some((_, idx)) => Left(RnaSplicingProblemError.EmptyIntron(idx))
      case None           => Right(new RnaSplicingProblem(source, introns) {})
    }
}
```

`DnaString` already enforces the per-input invariants we care about (alphabet, length). The only meaningful cross-input check is that no intron is empty — a degenerate input that, depending on JDK version, could cause `String.replace` to do unexpected things. Rejecting empty introns up front makes the composite algorithm robust regardless of JDK and gives the bundle a real reason to exist (vs. being a no-op wrapper).

**Why a bundle at all when there's only one error case?** Two reasons: (a) it's a reusable validated value that can be passed around or constructed once and reused across calls; (b) it matches the framework's established pattern for "validated input to a non-trivial algorithm" (`PartialPermutationProblem`, `RandomMatchProblem`, `TreeCompletionProblem`, etc.) — a downstream reader who sees `RnaSplicingProblem` immediately knows where to find the validation contract.

**No upper bound on `introns.size`:** Rosalind doesn't specify one, and any cap we picked would be arbitrary. The algorithm is `O(|source| × |introns|)` so even a few hundred introns at `|source| = 1000` is trivial.

### Decision 3: Placement — primitive in `nucleic`, composite + bundle in `protein`

The framework's placement convention is "subject of the operation":
- `IntronSplicing` operates on `DnaString → DnaString` → `bio.algorithms.nucleic`
- `RnaSplicing.transcribeAndTranslate` produces a `ProteinString` → `bio.algorithms.protein`
- `RnaSplicingProblem` is the input *to the composite*, so it lives next to the composite → `bio.domain.protein`

This means the composite *imports* the primitive across subdomain boundaries (`bio.algorithms.protein` imports `bio.algorithms.nucleic.IntronSplicing`). That's fine — cross-subdomain imports already happen (e.g., `RnaTranslation` in `protein` consumes `RnaString` from `nucleic`).

**Alternative considered:** put the primitive in `protein` too, since it's only used by the composite. Rejected because the primitive's signature is `DnaString → DnaString` with no protein-related concept; a future caller doing intron removal for a non-translation purpose (e.g., GC analysis of exons) would expect it in `nucleic`.

### Decision 4: Composite pipeline

```scala
object RnaSplicing {
  def transcribeAndTranslate(
      problem: RnaSplicingProblem
  ): Either[TranslationError, ProteinString] = {
    val spliced = IntronSplicing.splice(problem.source, problem.introns)
    val rna     = RnaTranscription.transcribe(spliced)
    RnaTranslation.translate(rna)
  }
}
```

Three lines. No additional error wrapping — `TranslationError` is bubbled up directly. Splicing and transcription are total, so the only failure mode is translation (e.g., the spliced RNA's first codon happening to be invalid — though the existing `RnaTranslation` already handles that case).

### Decision 5: No new error type for `RnaSplicing` itself

The only failure in the composite comes from `RnaTranslation.translate`, so the composite's return type matches: `Either[TranslationError, ProteinString]`. Wrapping `TranslationError` in a new `RnaSplicingError` ADT would add a layer of indirection without information. If a future variant needed splicing-specific failures (e.g., "intron not found, must be present"), we'd introduce the ADT then.

## Risks / Trade-offs

- **Risk:** Using `String.replace` couples the algorithm to JDK string semantics; if the JDK changed behavior on empty patterns we could see surprises. → **Mitigation:** the bundle rejects empty introns; bare-params callers are documented; the framework already targets JDK 11+ where the behavior is stable.
- **Risk:** Intron order matters in pathological cases (intron A is a substring of intron B's match-region, etc.). → **Mitigation:** Rosalind guarantees a unique solution. Documented in scaladoc; if a future spec relaxes the guarantee we'd add a "remove all overlapping introns" variant.
- **Trade-off:** No FASTA file reader specific to RNA splicing. → **Mitigation:** the existing `FastaFileReader.read` is one function call; the destructuring is one line at the call site. A wrapper would obscure that the input format is the same FASTA.
- **Trade-off:** `unsafeFrom` is used twice in this change (once in `IntronSplicing` for the spliced DnaString, and indirectly via the existing `RnaTranscription`/`RnaTranslation` chain). → **Mitigation:** both call sites are structurally justified — characters are preserved from validated inputs, lengths are non-increasing — and `unsafeFrom` remains `private[bio]`.
- **Trade-off:** No upper bound on `introns.size`. → **Mitigation:** algorithmic cost is bounded by `|source| × |introns| ≤ 1000 × ~few-hundred = O(10^5)`. Negligible. If a future Rosalind variant raises the bounds dramatically we can add validation then.
