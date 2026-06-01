## 1. Error ADT (`CharacterBasedPhylogenyProblemError`)

- [x] 1.1 RED: write `CharacterBasedPhylogenyProblemErrorSpec` asserting the sealed ADT
  exposes `EmptyTaxa`, `DuplicateTaxon(name)`, `ExceedsMaximumTaxa(count, max)`,
  `RowLengthMismatch(rowIndex, expected, actual)`, `InvalidCharacter(rowIndex, ch)`, and
  `ConflictingCharacters(rowIndexA, rowIndexB)`; confirm it fails to compile
- [x] 1.2 GREEN: create `bio.domain.graph.CharacterBasedPhylogenyProblemError` sealed trait
  with the six cases (`EmptyTaxa` as a case object); confirm `sbt test` green
- [x] 1.3 REFACTOR: review naming/fields against `CharacterTableProblemError` and
  `QuartetDistanceProblemError`; rerun tests

## 2. Validated bundle (`CharacterBasedPhylogenyProblem`)

- [x] 2.1 RED: write `CharacterBasedPhylogenyProblemSpec` covering valid construction (the
  sample), empty taxa, a duplicated taxon, 81 taxa, a mis-sized row, a non-`0/1` symbol,
  a conflicting pair (`1100` vs `1010`), first-failure ordering, and
  `assertDoesNotCompile` for public `apply`/`copy`; confirm RED
- [x] 2.2 GREEN: create `bio.domain.graph.CharacterBasedPhylogenyProblem` as a
  `sealed abstract case class(taxa, characters)` with `from(taxa, rows)` returning
  `Either[CharacterBasedPhylogenyProblemError, _]`; validation order empty → duplicate →
  exceeds-max (80) → row length → invalid character → conflicting characters →
  `Right(new ... {})`; conflict check uses the four-non-empty-intersection rule over
  non-trivial splits; confirm `sbt test` green
- [x] 2.3 REFACTOR: extract a private split/conflict helper; ensure pure functional style;
  rerun tests

## 3. Newick rendering (`NewickTree.render`)

- [x] 3.1 RED: extend `NewickTreeSpec` (or add one) asserting `render` produces
  `cat` for a leaf, `(a,b)` for an internal node, and a `;`-terminated string for a whole
  tree such as `(dog,(cat,rabbit),(rat,(elephant,mouse)));`; confirm RED
- [x] 3.2 GREEN: add `render: String` to `bio.domain.graph.NewickTree` (leaf → label,
  internal → parenthesised comma-joined children, top level appends `;`); confirm
  `sbt test` green
- [x] 3.3 REFACTOR: factor the recursive node rendering from the top-level `;` wrapper;
  rerun tests

## 4. Algorithm (`CharacterBasedPhylogeny.build`)

- [x] 4.1 RED: write `CharacterBasedPhylogenySpec` with the canonical sample (assert the
  induced non-trivial split set equals the three expected splits), the every-taxon-once
  check, and the empty-table star-tree case; confirm RED
- [x] 4.2 GREEN: implement `bio.algorithms.graph.CharacterBasedPhylogeny.build(problem):
  NewickTree` — reference-orient each character split away from taxon index 0, keep splits
  with both sides ≥ 2, assemble the laminar cluster family (clusters ∪ singletons ∪ full
  set), parent each cluster by smallest strict superset, and fold into a `NewickTree`;
  confirm `sbt test` green
- [x] 4.3 REFACTOR: ensure pure functional style (no `var`/mutable/imperative loops);
  deterministic child ordering; add Scaladoc explaining the laminar construction; rerun
  tests

## 5. IO runner and wiring

- [x] 5.1 Create `bio.problems.CHBPProb` reading `resources/chbp_data.txt` (a whitespace-
  separated taxa line + one character row per remaining non-empty line), building the
  problem, chaining Eithers, and printing `tree.render`
- [x] 5.2 Wire `CHBPProb.solve()` into `Main.scala` (comment out the prior runner)
- [x] 5.3 Run `Main` against the sample `chbp_data.txt` and confirm the output Newick
  models the sample (same unrooted tree as `(dog,(cat,rabbit),(rat,(elephant,mouse)));`)

## 6. Verification

- [x] 6.1 Run full `sbt test`; confirm all suites green and the new tests included
