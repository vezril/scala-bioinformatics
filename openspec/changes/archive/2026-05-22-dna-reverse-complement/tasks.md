## 1. DnaNucleotide.toChar (TDD)

- [x] 1.1 Write failing tests for `DnaNucleotide.toChar`: each of A/C/G/T returns the correct char; round-trip `fromChar` → `toChar` equals the original char for all four bases
- [x] 1.2 Run `sbt test` — confirm tests fail (Red)
- [x] 1.3 Implement `def toChar(n: DnaNucleotide): Char` on the `DnaNucleotide` companion as an exhaustive pattern match over the four case objects
- [x] 1.4 Run `sbt test` — confirm `toChar` tests pass (Green)

## 2. DnaNucleotide.complement (TDD)

- [x] 2.1 Write failing tests for `DnaNucleotide.complement`: A→T, T→A, C→G, G→C; involution (applying twice returns original) for all four bases
- [x] 2.2 Run `sbt test` — confirm tests fail (Red)
- [x] 2.3 Implement `def complement(n: DnaNucleotide): DnaNucleotide` on the `DnaNucleotide` companion as an exhaustive pattern match
- [x] 2.4 Run `sbt test` — confirm `complement` tests pass (Green)

## 3. DnaString.unsafeFrom

- [x] 3.1 Add `private[bio] def unsafeFrom(s: String): DnaString` to the `DnaString` companion with a doc comment marking it internal-only
- [x] 3.2 Run `sbt compile` — confirm clean compile

## 4. DnaReverseComplement algorithm (TDD)

- [x] 4.1 Write failing tests for `DnaReverseComplement.reverseComplement`: Rosalind sample (`AAAACCCGGT` → `ACCGGGTTTT`), empty string, each of A/C/G/T singletons, palindromic `GGCC` → `GGCC`, all-same-base `AAAA` → `TTTT`, double-application returns original
- [x] 4.2 Run `sbt test` — confirm tests fail (Red)
- [x] 4.3 Implement `DnaReverseComplement.reverseComplement(dna: DnaString): DnaString` using `dna.value.iterator.flatMap(c => DnaNucleotide.fromChar(c).map(DnaNucleotide.complement).map(DnaNucleotide.toChar)).mkString.reverse`, wrapping the result via `DnaString.unsafeFrom`
- [x] 4.4 Run `sbt test` — confirm all reverse-complement tests pass (Green)
- [x] 4.5 Refactor: verify no raw `Char` literals in the algorithm, no `var`, no mutable collections, return type is `DnaString` (not `Either`)
- [x] 4.6 Run `sbt test` — confirm all tests still pass after refactor

## 5. Final Verification

- [x] 5.1 Run `sbt compile` — zero errors, zero warnings
- [x] 5.2 Run `sbt test` — all tests pass (count higher than 52)
- [x] 5.3 Review `DnaReverseComplement.scala`: no `'A'`/`'C'`/`'G'`/`'T'` char literals (chars only appear in `DnaNucleotide.toChar` and `fromChar`), no `Either` in the return type
