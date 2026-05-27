## 1. Migration of overlap-graphs to bio.{domain,algorithms}.graph

- [x] 1.1 Create new directories `src/main/scala/bio/domain/graph/`, `src/main/scala/bio/algorithms/graph/`, `src/test/scala/bio/domain/graph/`, `src/test/scala/bio/algorithms/graph/`
- [x] 1.2 Move `src/main/scala/bio/domain/analysis/OverlapEdge.scala` → `src/main/scala/bio/domain/graph/OverlapEdge.scala`; update its `package` declaration to `bio.domain.graph`
- [x] 1.3 Move `src/main/scala/bio/domain/analysis/OverlapLength.scala` → `src/main/scala/bio/domain/graph/OverlapLength.scala`; update its `package` declaration to `bio.domain.graph`
- [x] 1.4 Move `src/main/scala/bio/domain/analysis/OverlapLengthError.scala` → `src/main/scala/bio/domain/graph/OverlapLengthError.scala`; update its `package` declaration to `bio.domain.graph`
- [x] 1.5 Move `src/main/scala/bio/algorithms/analysis/OverlapGraph.scala` → `src/main/scala/bio/algorithms/graph/OverlapGraph.scala`; update `package` to `bio.algorithms.graph` and `import bio.domain.analysis.{OverlapEdge, OverlapLength}` → `import bio.domain.graph.{OverlapEdge, OverlapLength}`
- [x] 1.6 Move `src/test/scala/bio/domain/analysis/OverlapEdgeSpec.scala` → `src/test/scala/bio/domain/graph/OverlapEdgeSpec.scala`; update `package` to `bio.domain.graph`
- [x] 1.7 Move `src/test/scala/bio/domain/analysis/OverlapLengthSpec.scala` → `src/test/scala/bio/domain/graph/OverlapLengthSpec.scala`; update `package` to `bio.domain.graph` and update the two `assertDoesNotCompile` strings from `bio.domain.analysis.OverlapLength` → `bio.domain.graph.OverlapLength`
- [x] 1.8 Move `src/test/scala/bio/domain/analysis/OverlapLengthErrorSpec.scala` → `src/test/scala/bio/domain/graph/OverlapLengthErrorSpec.scala`; update `package` to `bio.domain.graph`
- [x] 1.9 Move `src/test/scala/bio/algorithms/analysis/OverlapGraphSpec.scala` → `src/test/scala/bio/algorithms/graph/OverlapGraphSpec.scala`; update `package` to `bio.algorithms.graph` and `import bio.domain.analysis.{OverlapEdge, OverlapLength}` → `import bio.domain.graph.{OverlapEdge, OverlapLength}`
- [x] 1.10 Update `src/main/scala/bio/problems/GRPHProb.scala`: change `import bio.algorithms.analysis.OverlapGraph` → `import bio.algorithms.graph.OverlapGraph`, and `import bio.domain.analysis.OverlapLength` → `import bio.domain.graph.OverlapLength`
- [x] 1.11 Update scaladoc reference in `src/main/scala/bio/domain/matrix/ConsensusProfileResult.scala`: `[[bio.domain.analysis.OverlapEdge]]` → `[[bio.domain.graph.OverlapEdge]]`
- [x] 1.12 Run `sbt compile` and `sbt test` to confirm the migration is clean — same test count, all green, no warnings about unused imports

## 2. UndirectedEdge + UndirectedEdgeError (TDD)

- [x] 2.1 Write `UndirectedEdgeErrorSpec` (package `bio.domain.graph`) covering field-exposure for all three error cases (`SelfLoop(5)`, `NonPositiveU(0)`, `NonPositiveV(-1)`); run, observe red
- [x] 2.2 Write `UndirectedEdgeSpec` covering all 7 scenarios: accept (1,2); reject u=0 as NonPositiveU; reject v=-3 as NonPositiveV; reject self-loop (7,7); validation order u-before-v (0,-1 → NonPositiveU(0)); validation order v-before-selfloop (7,0 → NonPositiveV(0)); `assertDoesNotCompile` for direct `UndirectedEdge(1, 2)`; run, observe red
- [x] 2.3 Implement `bio.domain.graph.UndirectedEdgeError` sealed trait with `SelfLoop(node: Int)`, `NonPositiveU(value: Int)`, `NonPositiveV(value: Int)` cases
- [x] 2.4 Implement `bio.domain.graph.UndirectedEdge` as `sealed abstract case class UndirectedEdge(u: Int, v: Int)` with `from` smart constructor enforcing `u >= 1`, `v >= 1`, `u != v` in that order; run all tests green
- [x] 2.5 Refactor pass: scaladoc describing each rule, the validation order, and the `sealed abstract case class` pattern

## 3. TreeCompletionProblem + TreeCompletionProblemError (TDD)

- [x] 3.1 Write `TreeCompletionProblemErrorSpec` covering field-exposure for `NonPositiveN(0)`, `NExceedsMaximum(1001, 1000)`, `EdgeEndpointOutOfRange(edge, 10)`; run, observe red
- [x] 3.2 Write `TreeCompletionProblemSpec` covering all 11 scenarios: accept Rosalind sample (n=10, 6 edges); accept (n=1, no edges); accept upper-bound n=1000; reject n=0 as NonPositiveN; reject n=1001 as NExceedsMaximum; reject u-out-of-range edge as EdgeEndpointOutOfRange; reject v-out-of-range edge as EdgeEndpointOutOfRange; first offending edge in input order wins; validation order n-lower before n-upper; validation order n-upper before edge scan; `assertDoesNotCompile` for direct `TreeCompletionProblem(10, Vector.empty)`; run, observe red
- [x] 3.3 Implement `bio.domain.graph.TreeCompletionProblemError` sealed trait with `NonPositiveN(value: Int)`, `NExceedsMaximum(value: Int, max: Int)`, `EdgeEndpointOutOfRange(edge: UndirectedEdge, n: Int)` cases
- [x] 3.4 Implement `bio.domain.graph.TreeCompletionProblem` as `sealed abstract case class` with `from` smart constructor enforcing `1 <= n <= 1000` then per-edge `u <= n && v <= n` (first offending edge wins); run all tests green
- [x] 3.5 Refactor pass: scaladoc describing each rule, the validation order, the "trust acyclic precondition" decision

## 4. TreeCompletion.edgesToAdd algorithm (TDD)

- [x] 4.1 Write `TreeCompletionSpec` covering all 5 scenarios: Rosalind sample (n=10, 6 edges) → 3; single node (n=1, no edges) → 0; already-a-tree (n=5, 4 edges) → 0; fully disconnected (n=10, no edges) → 9; two disjoint trees (n=6, 4 edges across two paths) → 1; run, observe red
- [x] 4.2 Implement `bio.algorithms.graph.TreeCompletion.edgesToAdd(problem: TreeCompletionProblem): Int` as `problem.n - problem.edges.size - 1`; run all tests green
- [x] 4.3 Refactor pass: scaladoc explaining the forest-component math (`n − m − 1 = c − 1`), the acyclic-input precondition, and the no-cycle-detection decision

## 5. Whole-suite verification

- [x] 5.1 Run `sbt test` and confirm all existing suites plus new tests pass; verify the OverlapGraph migration preserved exactly 16 prior overlap-graphs tests (8 OverlapGraph + 6 OverlapLength/Error + 1 OverlapEdge + 1 compile-block test)
- [x] 5.2 Verify the old `bio.{domain,algorithms}.analysis` directories no longer contain `Overlap*` files (`find src -name 'Overlap*.scala' -path '*analysis*'` should return empty)
- [x] 5.3 Verify `git status` shows: new files under `bio/{domain,algorithms}/graph/` (sources + tests) plus rename markers for the four migrated overlap files, plus the import update in `GRPHProb.scala` and the scaladoc update in `ConsensusProfileResult.scala`. No other existing files modified by this change.
