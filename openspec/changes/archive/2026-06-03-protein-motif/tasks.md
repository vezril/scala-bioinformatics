## 1. ProteinMotifError ADT

- [x] 1.1 RED: write `bio.domain.protein.ProteinMotifErrorSpec` asserting `UnexpectedCharacter('}', 3)` carries `char == '}'` / `index == 3`; confirm it fails to compile ("not found").
- [x] 1.2 GREEN: create `bio.domain.protein.ProteinMotifError` as a sealed trait with `EmptyMotif` (case object), `UnterminatedGroup(start: Int)`, `UnexpectedCharacter(char: Char, index: Int)`. Confirm the test passes.

## 2. MotifElement ADT

- [x] 2.1 RED: write `bio.domain.protein.MotifElementSpec` asserting `OneOf(Set('S','T')).matches('S')` is true / `matches('A')` is false, and `NoneOf(Set('P')).matches('P')` is false / `matches('A')` is true. Confirm RED.
- [x] 2.2 GREEN: create `bio.domain.protein.MotifElement` sealed trait with `def matches(c: Char): Boolean`, plus `OneOf(residues: Set[Char])` (`residues.contains(c)`) and `NoneOf(residues: Set[Char])` (`!residues.contains(c)`). Confirm GREEN.

## 3. ProteinMotif type and parser

- [x] 3.1 RED: write `bio.domain.protein.ProteinMotifSpec` covering: `parse("N{P}[ST]{P}")` → elements `OneOf({N}), NoneOf({P}), OneOf({S,T}), NoneOf({P})`; `parse("")` → `EmptyMotif`; `parse("[ST")` → `UnterminatedGroup(0)`; `parse("A]B")` → `UnexpectedCharacter(']', 1)`; `assertDoesNotCompile` for public `apply`. Confirm RED.
- [x] 3.2 GREEN: create `bio.domain.protein.ProteinMotif` as `sealed abstract case class ProteinMotif(elements: Vector[MotifElement])` with `length` and a companion `parse(shorthand): Either[ProteinMotifError, ProteinMotif]` implementing the recursive-descent scan (literal → `OneOf` singleton, `[…]` → `OneOf`, `{…}` → `NoneOf`, unterminated/unexpected/empty errors). Confirm GREEN.

## 4. MotifLocations result type

- [x] 4.1 RED: write `bio.domain.protein.MotifLocationsSpec` asserting `MotifLocations("B5ZC00", Vector(85,118,142)).format == "B5ZC00\n85 118 142"`. Confirm RED.
- [x] 4.2 GREEN: create `bio.domain.protein.MotifLocations(id: String, positions: Vector[Int])` with `def format: String = s"$id\n${positions.mkString(" ")}"`. Confirm GREEN.

## 5. MotifSearch algorithm

- [x] 5.1 RED: write `bio.algorithms.protein.MotifSearchSpec` covering: motif `N{P}[ST]{P}` on `NQSANQTA` → `Vector(1,5)`; on `AAAA` → empty; on `NPSA` → empty; on `NQ` (shorter than motif) → empty. Parse the motif via `ProteinMotif.parse(...).toOption.get`. Confirm RED.
- [x] 5.2 GREEN: create `bio.algorithms.protein.MotifSearch.findLocations(motif: ProteinMotif, protein: String): Vector[Int]` scanning every start `0..L-m`, keeping those where all elements match, returning 1-based positions. Confirm GREEN.
- [x] 5.3 REFACTOR: tidy the scan (guard `L < m`), keep `findLocations` pure/total; rerun the spec to confirm still GREEN.

## 6. MPRTProb runner

- [x] 6.1 Add `bio.problems.MPRTProb` reading `src/main/scala/resources/mprt_data.txt` (one UniProt ID per line), and for each fetching `https://rest.uniprot.org/uniprotkb/{accession}.fasta` (strip any `_…` suffix to the accession) via `IO.blocking` + JDK `HttpURLConnection` (follow redirects), concatenating the FASTA sequence lines, running `MotifSearch.findLocations` with the `N{P}[ST]{P}` motif, and printing each matching protein's `MotifLocations.format`; network/parse errors print a message rather than throw.
- [x] 6.2 Point `Main.scala` at `MPRTProb.solve()` and run `sbt run` (best-effort; output depends on live UniProt) to confirm it wires up and prints ID/position blocks.

## 7. Full suite

- [x] 7.1 Run `sbt test` and confirm the whole suite is green.
