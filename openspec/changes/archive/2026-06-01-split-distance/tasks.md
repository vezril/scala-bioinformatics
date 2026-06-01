## 1. Error ADT

- [x] 1.1 Create `bio.domain.graph.SplitDistanceProblemError` (sealed trait + `EmptyTaxa`, `DuplicateTaxon(name)`, `TreeTaxaMismatch(treeIndex, missing, extra)`)

## 2. Split ADT (TDD)

- [x] 2.1 Write `SplitSpec` covering: side-order invariance (equality), canonical `side` excludes index `0`, distinct bipartitions are unequal; `assertDoesNotCompile` for `apply`/`copy` (RED)
- [x] 2.2 Implement `bio.domain.graph.Split` as a `sealed abstract case class` (`side: BitSet`) with a total canonicalising `Split.of(sideA, sideB)` (GREEN)
- [x] 2.3 Run `sbt test` and confirm the Split spec passes

## 3. Validated input bundle (TDD)

- [x] 3.1 Write `SplitDistanceProblemSpec` covering: accepts the canonical sample; rejects empty taxa → `EmptyTaxa`, duplicate taxon → `DuplicateTaxon`, tree1 leaf mismatch → `TreeTaxaMismatch(1, missing, extra)`, tree2-only mismatch → `treeIndex == 2`; first-failure-wins ordering; `assertDoesNotCompile` for `apply`/`copy` (RED)
- [x] 3.2 Implement `bio.domain.graph.SplitDistanceProblem` as a `sealed abstract case class` with first-failure-wins `from(taxa, tree1, tree2)` using a leaf-label helper (GREEN)
- [x] 3.3 Run `sbt test` and confirm the input-bundle spec passes

## 4. Algorithm (TDD)

- [x] 4.1 Write `SplitDistanceSpec` covering: canonical sample → `2`; identical trees → `0`; disjoint-split trees → `2*(n-3)`; symmetry in tree order (RED)
- [x] 4.2 Implement `bio.algorithms.graph.SplitDistance.compute` (per-tree functional traversal collecting nontrivial splits as `Set[Split]`, intersect, apply `2*(n-3) - 2*s`) (GREEN)
- [x] 4.3 Run `sbt test` and confirm the algorithm spec passes

## 5. Runner and wiring

- [x] 5.1 Create `bio.problems.SPTDProb` (`solve(): IO[Unit]`) reading `src/main/scala/resources/sptd_data.txt` (taxa line + two Newick lines), parsing both trees via `NewickParser`, building the problem, and printing the distance
- [x] 5.2 Wire `SPTDProb.solve()` into `bio.Main.run`

## 6. Verification

- [x] 6.1 Run the full `sbt test` suite and confirm zero regressions
