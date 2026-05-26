## Context

Spec 8 established the standard RNA genetic code as a single exhaustive pattern match in `bio.domain.protein.GeneticCode.translate`. Spec 13 asks the *inverse* question: given a protein, how many mRNAs could have produced it? Each amino acid in the protein contributes a factor equal to its codon degeneracy (the number of codons that map to that amino acid). The whole protein is followed by a stop codon, which contributes a factor of 3 (UAA, UAG, UGA).

The framework now has three "protein-layer" pieces in place: `AminoAcid` (the alphabet), `GeneticCode` (forward table), and `ProteinString` (validated sequence). This change adds the reverse-count algorithm using only what's already there.

## The math

For protein `P = p_1 p_2 ... p_n`:
```
count = (∏ degeneracy(p_i)) × stop_codon_count
      = (∏ |{ c ∈ Codon | translate(c) = AminoAcidProduct(p_i) }|) × |{ c | translate(c) = Stop }|
```

Output is `count mod 1_000_000`.

Codon degeneracies (derived from the spec-8 table):

| AA | # codons | AA | # codons |
|----|----------|----|----------|
| F | 2 | Y | 2 |
| L | 6 | H | 2 |
| I | 3 | Q | 2 |
| M | 1 | N | 2 |
| V | 4 | K | 2 |
| S | 6 | D | 2 |
| P | 4 | E | 2 |
| T | 4 | C | 2 |
| A | 4 | W | 1 |
|   |   | R | 6 |
|   |   | G | 4 |

Sum across all amino acids = 61. Plus 3 stop codons = 64 total. ✓

### Sample trace

Protein `"MA"`: 1 (M) × 4 (A) × 3 (stop) = 12 ✓ (Rosalind sample)

Protein `"MAMAPRTEINSTRING"` (= the spec-8 sample output):
`1 × 4 × 1 × 4 × 4 × 6 × 4 × 2 × 3 × 2 × 6 × 4 × 6 × 3 × 2 × 4 = 63,700,992` × 3 = `191,102,976` mod `1,000,000` = `102,976`

## Goals / Non-Goals

**Goals:**
- Provide `InferMRna.count(protein: ProteinString): Int` returning the count modulo `1_000_000`
- Be total — every `ProteinString` yields a defined `Int` in `[0, 999_999]`
- Match the Rosalind sample: `"MA"` → `12`
- Derive codon degeneracies and stop-codon count from `GeneticCode.translate` — no parallel data table
- Take modulo after each multiplication to keep intermediate values bounded
- Empty protein returns `3` (only the stop-codon factor contributes)

**Non-Goals:**
- Configurable modulus. Rosalind fixes it at `1_000_000`; a future spec might want a different modulus, but YAGNI.
- Enumerating the actual mRNA candidates. The count is `> 10^779` at the upper bound (length 1000, all-L), which is unrepresentable as a list. Only the count modulo `1_000_000` is required.
- Reverse translation that produces a *specific* mRNA. That's a different problem (codon optimization), not this spec.
- Modifying `GeneticCode` to expose pre-computed reverse counts. The derivation is cheap (64 codons) and computing it inside `InferMRna` keeps `GeneticCode` minimal.

## Decisions

### Derive codon counts from `GeneticCode`, don't hard-code

**Decision**: Build `codonCount: Map[AminoAcid, Int]` and `stopCodonCount: Int` by iterating all 64 codons and grouping their `GeneticCode.translate` results. The computation is a `val` initialized once at object load.

**Rationale**:
- Single source of truth. If anyone ever changes a row in `GeneticCode`, the reverse counts update automatically.
- Avoids duplication of the 20-entry codon-degeneracy table.
- The derivation is O(64) — trivial.
- Compile-time exhaustiveness on the forward table means the derived counts are guaranteed correct.

**Alternative considered**: Hard-code the codon counts as a `Map[AminoAcid, Int]` literal. Rejected — duplicates information already in `GeneticCode`; any future edit to the genetic code would silently desync the two.

**Alternative considered**: Add `GeneticCode.codonCount(aa)` and `GeneticCode.stopCodonCount` to the domain. Rejected — keeps `GeneticCode` focused on its single responsibility (the forward table); the reverse view is an algorithm concern.

### Modulo after each multiplication

**Decision**: Accumulate the product in an `Int`, taking `% Modulus` after each multiplication.

**Rationale**:
- Max intermediate before mod: `(999_999) × 6 = 5_999_994` — well within `Int` range (~2.1B).
- After mod, the accumulator stays in `[0, 999_999]`; multiplication by a small factor (max 6) keeps it bounded.
- No need for `BigInt`; `Int` is sufficient and faster.
- Modular-arithmetic identity `(a × b) mod n = ((a mod n) × b) mod n` keeps the answer correct.

**Alternative considered**: `BigInt` accumulator with a single `% 1_000_000` at the end. Rejected — for length-1000 proteins of high-degeneracy amino acids (e.g., all L), the un-modded product has ~778 digits. `BigInt` would work but adds allocation overhead for no benefit; `Int` with continuous mod is both faster and simpler.

### Bare `Int` return, no wrapper

**Decision**: `def count(protein: ProteinString): Int`. The result is already bounded `[0, 999_999]`, so the type doesn't need a wrapper to enforce additional invariants.

**Rationale**: Matches the framework's precedent — `MotifLocations.find` returns `Vector[Int]`, `HammingDistance.between` returns `Either[_, Int]`, `ExpectedOffspring.dominantPhenotype` returns `Double`. Output values are bare scalars when no further invariant is meaningful.

### `Map[Char, AminoAcid]` lookup for protein characters

**Decision**: Build a `Map[Char, AminoAcid]` once (from `AminoAcid.all`), then look up each char in the protein. O(1) per character.

**Rationale**:
- `ProteinString` is validated — every char is guaranteed to be in the map, so the lookup cannot return `None` in practice.
- `AminoAcid.all.find(_.code == ch)` would be O(20) per char; building the map once is O(20) total.

**Trade-off**: A `.get` (or `getOrElse(sys.error)`) call after a `Map.get` is technically unsafe — if some future change loosens `ProteinString` validation, this could throw. Mitigated by:
- `ProteinString.from`'s alphabet check is the documented validation point
- An `Option.get` failure here would mean the `ProteinString` invariant has been broken — a bug worth surfacing

### Lives in `bio.algorithms.protein`

**Decision**: `InferMRna` joins `RnaTranslation` under `bio.algorithms.protein`.

**Rationale**: Both algorithms operate on the relationship between RNA and protein under the standard genetic code. They're siblings in the subdomain.

### Method name `count`, not `numCandidates` or `inferCount`

**Decision**: `InferMRna.count(protein: ProteinString): Int`.

**Rationale**: Short, accurate ("the count of mRNA candidates"), reads naturally at the call site (`InferMRna.count(protein)`). `numCandidates` is verbose; `inferCount` redundantly repeats the object name.

## Risks / Trade-offs

- [Derivation happens at object initialization time] If `GeneticCode` ever introduces a circular dependency or static init ordering issue, the derivation could fail at load time. → Mitigation: `InferMRna` only depends on `GeneticCode`, `AminoAcid`, `CodonOutcome`, and `Codon` — all leaf types in `bio.domain.protein`. No cycles possible.
- [Hard-coded `1_000_000` modulus] A future Rosalind problem might require a different modulus. → Refactor then; either add an overload `count(protein, modulus)` or extract a parameterized helper. One-line change.
- [`Map.get(ch).get` is technically unsafe] As above, mitigated by the `ProteinString` invariant. The framework already uses similar patterns (`RnaNucleotide.fromChar(c).get` in `Codon.parseAll` on validated `RnaString`).
- [Intermediate `Int` multiplication could overflow if degeneracies grew] Current max degeneracy is 6 (L, R, S). Even at hypothetical degeneracy 12 the multiplication `999_999 × 12 = 11_999_988` still fits. → Documented; if a future genetic-code variant has higher degeneracies, switch to `Long` (or take mod *before* the multiplication, which already works).
