## Context

SGRA gives a list `L` of positive masses (≤ 100). The *spectrum graph* has a node per mass and a directed edge `u→v` when `v > u` and `v − u` equals an amino-acid residue mass (labelled with that residue). A protein matches `L` exactly when its residues spell the edge labels along an increasing path; the longest matching protein is therefore the **longest path** in this DAG.

The protein mass-spectrometry capabilities live in `bio.{domain,algorithms}.protein`; `AminoAcid` exposes `all` and `monoisotopicMass`. SGRA reuses `AminoAcid` and adds the graph longest-path search.

## Goals / Non-Goals

**Goals:**
- Validated `SpectrumGraphProblem(masses)` (size ≤ 100, all positive) via a smart constructor returning `Either`, `sealed abstract case class` to block `apply`/`copy`.
- Pure, total `SpectrumGraph.longestPeptide(problem): SpectrumGraphPeptide`.
- Result type with `format: String` (the protein).
- Reuse `AminoAcid`; functional implementation (sorting + memoised DP fold), no `var`/`while`/mutable collections.

**Non-Goals:**
- Materialising the graph as explicit node/edge objects — edges are computed on the fly from value gaps.
- Returning the matching mass path — only the protein (edge-label) string is required.

## Decisions

**1. Longest path in the DAG by a memoised DP.**
Sort the masses ascending (so every edge goes from a lower to a higher index — the graph is acyclic). For node `i`, `best(i)` is the longest edge-label path *starting* at `i`: the maximum over edges `i→j` (where `sorted(j) − sorted(i)` is a residue mass) of `(1 + best(j).length, residue :: best(j).labels)`, or `(0, Nil)` if `i` has no outgoing edge. Computing `best` from the highest index down to the lowest ensures every `best(j)` is ready before `best(i)`. The answer is the longest `best(i)` taken over start nodes in index order (deterministic; ties resolve to the lowest-index start). Verified on the sample: the path `3524.8542 → 3710.9335 → 3841.974 → 3929.00603 → 4026.05879 → 4083.08025` spells `WMSPG`.

**2. Residue matching by tolerance.**
A gap `g` matches residue `a` when `|g − a.monoisotopicMass| < 1e-4` — separating near-equal residues (K vs Q, 0.036 apart) while absorbing floating-point noise. Isobaric residues (I/L) tie; the first in `AminoAcid.all` is chosen — any solution is acceptable per Rosalind.

**3. Pure functional reconstruction.**
`best` is built by a `foldLeft` over indices `n-1 .. 0` into an immutable `Map[Int, (Int, List[Char])]`; each node's candidates are a comprehension over higher nodes with a matching gap, reduced by `maxByOption(_._1)`. The final answer is `(0 until n).iterator.map(best).maxByOption(_._1).map(_._2.mkString).getOrElse("")`. No `var`/`while`/mutable collection.

**4. Validation rules and order (first-failure-wins).**
`SpectrumGraphProblem.from(masses)` checks `masses.size <= 100`, else `TooManyMasses(size, 100)`; then every mass is positive, else `NonPositiveMass(index, value)`. The empty list is accepted (no nodes → empty protein).

**5. Naming and placement.**
`SpectrumGraphProblem`, `SpectrumGraphProblemError`, and the `SpectrumGraphPeptide` result live in `bio.domain.protein`; the algorithm `SpectrumGraph.longestPeptide` in `bio.algorithms.protein`. Result (`SpectrumGraphPeptide`) and algorithm (`SpectrumGraph`) names are distinct, so no alias is needed.

## Risks / Trade-offs

- **[O(n²) edges]** → `n ≤ 100`, so at most ~10⁴ gap checks; trivially fast.
- **[Tolerance]** → `1e-4` separates K/Q-type near-equals yet tolerates float noise; isobaric I/L are interchangeable (any accepted).
- **[Multiple longest paths]** → Rosalind accepts any; the DP returns a deterministic one (lowest-index start, first residue on ties), matching the sample `WMSPG`.
- **[Empty / no-edge inputs]** → an empty list or one with no residue-distance pair yields the empty protein; covered by scenarios.
