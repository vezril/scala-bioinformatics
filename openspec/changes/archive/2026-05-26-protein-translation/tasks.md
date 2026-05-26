## 1. AminoAcid ADT (TDD)

- [x] 1.1 Write failing tests for `AminoAcid`: each of the 20 case objects exists and exposes the correct `code: Char`; the sealed trait enumerates to exactly 20 distinct codes
- [x] 1.2 Run `sbt test` — confirm tests fail (Red)
- [x] 1.3 Implement `AminoAcid.scala` in `bio.domain`: `sealed trait AminoAcid { def code: Char }` plus 20 case objects (`F, L, I, V, S, P, T, A, Y, H, N, D, Q, K, E, C, R, G, W, M`) each overriding `code`
- [x] 1.4 Run `sbt test` — confirm AminoAcid tests pass (Green)

## 2. TranslationError and ProteinError ADTs

- [x] 2.1 Create `TranslationError.scala` with `sealed trait TranslationError` and case `LengthNotMultipleOfThree(length: Int)`
- [x] 2.2 Create `ProteinError.scala` with `sealed trait ProteinError` and case `InvalidCharacter(ch: Char)`
- [x] 2.3 Run `sbt compile` — confirm clean compile

## 3. Codon and CodonOutcome (TDD)

- [x] 3.1 Write failing tests for `Codon`: public construction; field access; `Codon.fromChars` returning `Some` for valid RNA chars and `None` otherwise; `Codon.parseAll` for empty RnaString → `Right(Vector.empty)`, length-3 → singleton vector, length-6 → two codons in order, length-5 → `Left(LengthNotMultipleOfThree(5))`
- [x] 3.2 Write failing tests for `CodonOutcome`: `AminoAcidProduct(aa).aa` exposes the amino acid; `Stop` is a subtype of `CodonOutcome`
- [x] 3.3 Run `sbt test` — confirm tests fail (Red)
- [x] 3.4 Implement `Codon.scala` in `bio.domain`: `final case class Codon(first, second, third: RnaNucleotide)` with companion `fromChars(a, b, c: Char): Option[Codon]` (uses `RnaNucleotide.fromChar`) and `parseAll(rna: RnaString): Either[TranslationError, Vector[Codon]]` (groups by 3 via `grouped(3)`, validates length divisibility first)
- [x] 3.5 Implement `CodonOutcome.scala` in `bio.domain`: `sealed trait CodonOutcome`, `final case class AminoAcidProduct(aa: AminoAcid)`, `case object Stop`
- [x] 3.6 Run `sbt test` — confirm Codon and CodonOutcome tests pass (Green)

## 4. GeneticCode (TDD)

- [x] 4.1 Write failing tests for `GeneticCode.translate`: sample codons (`AUG → M`, `UUU → F`, `UGG → W`); all three Stop codons (`UAA`, `UAG`, `UGA`); plus an exhaustive sanity check that iterates all 64 codons via a comprehension over the 4 `RnaNucleotide`s and asserts that each returns a non-null `CodonOutcome` (i.e., the table is total)
- [x] 4.2 Run `sbt test` — confirm tests fail (Red)
- [x] 4.3 Implement `GeneticCode.scala` in `bio.domain`: `object GeneticCode { def translate(c: Codon): CodonOutcome = c match { /* 64 entries */ } }`. Use the standard RNA codon table from the spec brief. Pattern match must be exhaustive (compiler-enforced)
- [x] 4.4 Run `sbt test` — confirm GeneticCode tests pass (Green) with no exhaustivity warnings

## 5. ProteinString (TDD)

- [x] 5.1 Write failing tests for `ProteinString.from`: `"MAMA"` accepted; `""` accepted; `"MAB"` rejected as `InvalidCharacter('B')`; `"mama"` rejected as `InvalidCharacter('m')`. Plus invariant tests: `assertDoesNotCompile("""bio.domain.ProteinString("MAMA")""")` and `assertDoesNotCompile("""bio.domain.ProteinString.from("M").toOption.get.copy(value = "X")""")`
- [x] 5.2 Write failing tests for `ProteinString.fromAminoAcids`: `Vector(M, A, M, A)` → value `"MAMA"`; `Vector.empty` → value `""`
- [x] 5.3 Run `sbt test` — confirm tests fail (Red)
- [x] 5.4 Implement `ProteinString.scala` in `bio.domain`: `sealed abstract case class ProteinString(value: String)` with companion `from(s: String)` (validates each char against an alphabet set derived from `AminoAcid` members' `code`), `fromAminoAcids(aas: Seq[AminoAcid])` (total — assembles via `aas.map(_.code).mkString` then `unsafeFrom`), and `private[bio] unsafeFrom(s: String)`. Construction uses anonymous-subclass pattern `new ProteinString(s) {}`
- [x] 5.5 Run `sbt test` — confirm ProteinString tests pass (Green)

## 6. RnaString MaxLength bump (TDD)

- [x] 6.1 Add failing tests to `RnaStringSpec`: a 5000-character valid RNA is accepted (previously over the 1000 cap); a 10000-character valid RNA is accepted; a 10001-character RNA is rejected as `ExceedsMaxLength(10001)`
- [x] 6.2 Run `sbt test` — confirm new tests fail (Red), existing tests still pass
- [x] 6.3 Update `RnaString.scala`: change `private val MaxLength = 1000` to `10000`
- [x] 6.4 Update the existing "exactly 1000 characters is accepted" RnaString test (and any other test pinned to 1000) to reflect the new boundary — the 1000-char test should still pass (it's now well under the cap), but boundary tests for the old cap must move to 10000
- [x] 6.5 Run `sbt test` — confirm RnaString tests pass (Green)

## 7. RnaTranslation algorithm (TDD)

- [x] 7.1 Write failing tests for `RnaTranslation.translate`: empty RnaString → `Right(empty ProteinString)`; Rosalind sample `"AUGGCCAUGGCGCCCAGAACUGAGAUCAAUAGUACCCGUAUUAACGGGUGA"` → `Right(ProteinString("MAMAPRTEINSTRING"))`; leading stop `"UAA"` → `Right(empty ProteinString)`; mid-sequence stop `"AUGUAAGCC"` → `Right(ProteinString("M"))`; no-stop input `"AUGGCC"` → `Right(ProteinString("MA"))`; length-5 input `"AUGGC"` → `Left(LengthNotMultipleOfThree(5))`
- [x] 7.2 Run `sbt test` — confirm tests fail (Red)
- [x] 7.3 Implement `RnaTranslation.scala` in `bio.algorithms`: `def translate(rna: RnaString): Either[TranslationError, ProteinString] = Codon.parseAll(rna).map { codons => val aas = codons.iterator.map(GeneticCode.translate).takeWhile { case Stop => false; case _ => true }.collect { case AminoAcidProduct(aa) => aa }.toVector; ProteinString.fromAminoAcids(aas) }`. No `var`, no mutable collections
- [x] 7.4 Run `sbt test` — confirm RnaTranslation tests pass (Green)
- [x] 7.5 Refactor: confirm exhaustive pattern matches, no `var`, no raw char literals outside `AminoAcid.code` and existing nucleotide dispatch; algorithm short-circuits at Stop via `takeWhile`
- [x] 7.6 Run `sbt test` — confirm all tests still pass after refactor

## 8. Final Verification

- [x] 8.1 Run `sbt clean compile` — zero errors, zero warnings (including exhaustiveness)
- [x] 8.2 Run `sbt test` — all tests pass (count higher than 145)
- [x] 8.3 Review all new source files: `GeneticCode.translate` is an exhaustive 64-entry match (no `Map`); `Codon` construction is public; `ProteinString` invariant locked via `sealed abstract case class`; `RnaTranslation.translate` is the only public translation entry; no `var`/mutable collections; no `Either` on total operations
