## Context

The 20 residues are a sealed `AminoAcid` ADT (`bio.domain.protein.AminoAcid`) whose
companion is the single source of truth for alphabet facts. As of the SPEC capability it
carries `monoisotopicMass` per residue and a `closestByMass` lookup. Proteins are
`ProteinString`s built via smart constructors that validate characters (but impose no
length cap). Established precedent: a validated domain bundle, a result type with
`format`, a pure/total algorithm, and an `IO` runner that prints a descriptive error
instead of throwing (see `SPECProb`, `SMGBProb`).

Rosalind PRTM ("Calculating Protein Mass") gives a protein `P` of length `≤ 1000` aa and
asks for its total weight: the sum of its residues' monoisotopic masses, reported to
three decimals.

Canonical sample: `P = SKADYEK` → `821.392`
(`87.03203 + 128.09496 + 71.03711 + 115.02694 + 163.06333 + 129.04259 + 128.09496 =
821.39192`, which renders as `821.392`).

## Goals / Non-Goals

**Goals:**
- Add `AminoAcid.fromChar` (single source of truth) so a validated protein character can
  be lifted to its residue.
- A validated `ProteinMassProblem` bundle (`ProteinString` of length `≤ 1000`),
  constructed only via a smart constructor; empty proteins accepted.
- A pure, total `ProteinMass.calculate` summing residue monoisotopic masses.
- A `ProteinMass` result type whose `format` renders the mass to three decimals.
- A `PRTMProb` IO runner reading one protein string, printing the mass or a descriptive
  error.

**Non-Goals:**
- No average masses; monoisotopic only.
- No spectral problems (covered by SPEC); no new alignment work.
- No change to translation, alignment, or any existing algorithm beyond the additive
  `AminoAcid.fromChar`.

## Decisions

### Decision: `AminoAcid.fromChar` on the companion

Add a companion lookup derived from `all` (never hardcoded):

```scala
private val byCode: Map[Char, AminoAcid] = all.map(aa => aa.code -> aa).toMap
def fromChar(c: Char): Option[AminoAcid] = byCode.get(c)
```

The 20 codes are distinct (only the *masses* of `I`/`L` collide, not their letters), so
`byCode` has 20 entries and `fromChar` is unambiguous. This mirrors the nucleotide
companions' `fromChar` and lets the algorithm dispatch on ADT values rather than raw
chars.

**Alternative considered**: building a `Char -> Double` mass map directly in the
algorithm. Rejected — lifting to the ADT first is the project convention, and `fromChar`
is a fundamental alphabet operation worth having on the companion.

### Decision: `ProteinMassProblem` bundle with a length cap

```scala
sealed abstract case class ProteinMassProblem(protein: ProteinString)
```

`from(protein)` enforces a single rule: `protein.value.length <= 1000`, else
`ProteinTooLong(length, 1000)`. Characters are already validated by `ProteinString`, so
no character check is needed here. Empty proteins are accepted (mass `0.000`). Constructed
via an anonymous subclass `new ProteinMassProblem(protein) {}` so the synthesized
`apply`/`copy` cannot bypass validation.

`ProteinMassProblemError`: `sealed trait` with
`final case class ProteinTooLong(length: Int, max: Int)`.

### Decision: Pure functional summation (no imperative kernel)

Not an alignment-family algorithm, so ordinary FP — no `var`/`while`. The result type and
the algorithm object share the name `ProteinMass` in different packages; the algorithm
aliases the domain result as `Result` (mirroring `OverlapAlignment`/`SemiglobalAlignment`):

```scala
import bio.domain.protein.{ProteinMass => Result, ProteinMassProblem, AminoAcid}

def calculate(problem: ProteinMassProblem): Result = {
  val total = problem.protein.value.iterator
    .map(c => AminoAcid.fromChar(c).fold(0.0)(_.monoisotopicMass))
    .sum
  Result(total)
}
```

The function is total: `ProteinString` guarantees every character is a valid code, so
`fromChar` always yields `Some` (the `0.0` default is defensive and unreachable). The
empty protein sums to `0.0`. No I/O, no mutable state.

### Decision: Result type renders to three decimals

```scala
final case class ProteinMass(mass: Double) {
  def format: String = f"$mass%.3f"
}
```

`f"%.3f"` rounds half-up, so `821.39192` renders as `821.392`, matching Rosalind.

### Decision: Runner reads one protein string

`PRTMProb.solve(): IO[Unit]` reads `prtm_data.txt`, takes the first non-blank line,
builds a `ProteinString` (character errors become a descriptive `Left`), validates into a
`ProteinMassProblem` (length error → `Left`), runs `calculate`, and prints
`result.format`. Any error prints a message rather than throwing — mirroring `SPECProb`.

## Risks / Trade-offs

- **[Floating-point summation]** → summing ≤ 1000 masses each with ~5 decimal places
  keeps error far below the third decimal; `f"%.3f"` rounding matches Rosalind's expected
  output. The canonical sample is asserted exactly (`821.392`).
- **[Modifying `AminoAcid`]** → additive only (one new companion lookup). Existing
  `code`/`all`/`validCodes`/`monoisotopicMass`/`closestByMass` and every translation path
  are untouched; the full suite guards against regressions.
