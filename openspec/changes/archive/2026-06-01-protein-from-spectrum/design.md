## Context

The project models the 20 standard residues as a sealed `AminoAcid` ADT
(`bio.domain.protein.AminoAcid`) whose companion is the single source of truth for
alphabet facts (`code`, `all`, `validCodes`). Proteins are `ProteinString`s built via
smart constructors. Established precedent: a validated domain bundle, a result type with
`format`, a pure/total algorithm, and an `IO` runner that prints a descriptive error
instead of throwing (see `SMGBProb`, `GREPProb`).

Rosalind SPEC ("Inferring Protein from Spectrum") gives a list `L` of `n ≤ 100` positive
reals — the **prefix spectrum**, i.e. the cumulative weights of every prefix of some
weighted protein. Because consecutive prefix weights differ by exactly one residue's
monoisotopic mass, the protein of length `n − 1` is recovered by differencing `L` and
matching each gap to the closest amino-acid mass. Output: any protein whose prefix
spectrum equals `L`.

Canonical sample: `L = [3524.8542, 3710.9335, 3841.974, 3970.0326, 4057.0646]`. The four
consecutive differences `186.0793, 131.0405, 128.0586, 87.032` map to `W, M, Q, S` →
`WMQS`.

## Goals / Non-Goals

**Goals:**
- Add monoisotopic masses to the `AminoAcid` alphabet (single source of truth) plus a
  deterministic `closestByMass` lookup.
- A validated `PrefixSpectrum` bundle (`n ≤ 100` positive reals), first-failure-wins,
  constructed only via a smart constructor.
- A pure, total `InferProteinFromSpectrum.infer` reconstructing the protein of length
  `n − 1`.
- An `InferredProtein` result type with `format`.
- A `SPECProb` IO runner reading one weight per line, printing the protein or a
  descriptive error.

**Non-Goals:**
- No spectral-convolution / full-spectrum problems (PRTM, FULL, CONV) — only the prefix
  spectrum.
- No average masses; monoisotopic only.
- No change to translation, alignment, or any existing algorithm beyond the additive
  `AminoAcid` extension.

## Decisions

### Decision: Monoisotopic masses live on the `AminoAcid` companion

Extend the trait with `def monoisotopicMass: Double` and give each of the 20 case
objects its standard Rosalind monoisotopic mass (e.g. `W = 186.07931`, `M = 131.04049`,
`Q = 128.05858`, `S = 87.03203`). Add a companion lookup:

```scala
def closestByMass(target: Double): AminoAcid =
  all.minBy(aa => math.abs(aa.monoisotopicMass - target))
```

`Vector.minBy` returns the **first** minimal element, so ties are broken deterministically
by `all` order. The isobaric pair `I`/`L` share mass `113.08406`; with `all` ordering
`(F, L, I, V, …)`, `L` precedes `I`, so an exact `113.08406` gap resolves to `L`.
Rosalind accepts any valid protein, so this deterministic choice is sound.

**Alternative considered**: a standalone `MonoisotopicMassTable` object keyed by `Char`.
Rejected — it would duplicate the alphabet and violate the project's "one source of truth
per alphabet" rule. Mass is an intrinsic property of a residue and belongs on the ADT
(the same reasoning that put `toChar` on the nucleotide companions).

### Decision: `PrefixSpectrum` bundle with first-failure-wins validation

```scala
sealed abstract case class PrefixSpectrum(weights: Vector[Double])
```

`from(weights)` validates, first-failure-wins:
1. non-empty, else `EmptySpectrum` (a length-`n − 1` protein needs `n ≥ 1`);
2. `weights.length <= 100`, else `TooManyWeights(count, 100)`;
3. the first non-positive weight, else `NonPositiveWeight(index, value)` (the problem
   guarantees positive reals).

Constructed via an anonymous subclass `new PrefixSpectrum(weights) {}` so the synthesized
`apply`/`copy` cannot bypass validation. `weights` is held as a `Vector[Double]`;
monotonicity is **not** enforced — the problem guarantees a genuine prefix spectrum, and
differencing handles whatever ordering is given.

`PrefixSpectrumError`: `sealed trait` with `case object EmptySpectrum`,
`final case class TooManyWeights(count: Int, max: Int)`, and
`final case class NonPositiveWeight(index: Int, value: Double)`.

### Decision: Pure functional inference (no imperative kernel)

This is not an alignment-family algorithm, so it uses ordinary FP — no `var`/`while`:

```scala
def infer(problem: PrefixSpectrum): InferredProtein = {
  val residues = problem.weights
    .sliding(2)
    .collect { case Vector(a, b) => AminoAcid.closestByMass(b - a) }
    .toVector
  InferredProtein(ProteinString.fromAminoAcids(residues))
}
```

For a single-weight spectrum (`n == 1`), `sliding(2)` yields no pair, giving the empty
protein — consistent with "length `n − 1`". The function is total: every difference has a
closest residue, and `ProteinString.fromAminoAcids` is total over typed amino acids.

### Decision: Result type mirrors the sibling result types

```scala
final case class InferredProtein(protein: ProteinString) {
  def format: String = protein.value
}
```

`format` renders the protein's single-letter codes (e.g. `WMQS`).

### Decision: Runner reads one weight per line

`SPECProb.solve(): IO[Unit]` reads `spec_data.txt`, splits on `\R`, trims, drops blank
lines, parses each via `toDoubleOption` (a parse failure becomes a descriptive `Left`),
`foldRight`s into `Either[String, List[Double]]`, validates into `PrefixSpectrum`, runs
`infer`, and prints `result.format`. Any parse/validation error prints a message rather
than throwing — mirroring `GREPProb`.

## Risks / Trade-offs

- **[Floating-point matching]** → masses differ by ≥ ~0.02 between distinct residues
  (except the exactly isobaric `I`/`L`), far larger than the ~1e-4 rounding in the
  datasets, so nearest-mass matching is robust. Tests assert the canonical `WMQS` plus an
  exact-mass edge case.
- **[`I`/`L` ambiguity]** → genuinely undecidable from mass alone; Rosalind accepts
  either. The deterministic `all`-order tie-break documents the choice (`L`).
- **[Modifying `AminoAcid`]** → additive only (new abstract member implemented by all 20
  objects, new companion helper). Existing `code`/`all`/`validCodes` and every translation
  path are untouched; the full suite guards against regressions.
