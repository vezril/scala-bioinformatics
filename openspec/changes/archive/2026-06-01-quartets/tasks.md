## 1. Error ADT

- [x] 1.1 Create `bio.domain.graph.QuartetsProblemError` (sealed trait + `EmptyTaxa`, `DuplicateTaxon(name)`, `EmptyTable`, `InconsistentWidth(rowIndex, expected, actual)`, `InvalidSymbol(rowIndex, colIndex, symbol)`)

## 2. Validated input bundle (TDD)

- [x] 2.1 Write `QuartetsProblemSpec` covering: accepts the canonical sample; rejects empty taxa → `EmptyTaxa`, duplicate taxon → `DuplicateTaxon`, empty table → `EmptyTable`, width mismatch → `InconsistentWidth(1, 7, 6)`, bad symbol → `InvalidSymbol(0, 3, '2')`; first-failure-wins ordering; `assertDoesNotCompile` for `apply`/`copy` (RED)
- [x] 2.2 Implement `bio.domain.graph.QuartetsProblem` as a `sealed abstract case class` with first-failure-wins `from(taxa, characters)` (GREEN)
- [x] 2.3 Run `sbt test` and confirm the input-bundle spec passes

## 3. Quartet ADT (TDD)

- [x] 3.1 Write `QuartetSpec` covering: within-side order invariance, side-order invariance (equality), and `render` → `"{dog, elephant} {rabbit, robot}"`; `assertDoesNotCompile` for `apply`/`copy` (RED)
- [x] 3.2 Implement `bio.domain.graph.Quartet` as a `sealed abstract case class` with a total canonicalising `Quartet.of(w, x, y, z)` and `render` (GREEN)
- [x] 3.3 Run `sbt test` and confirm the Quartet spec passes

## 4. Algorithm (TDD)

- [x] 4.1 Write `QuartetsSpec` covering: canonical sample → the 4 expected rendered quartets (as a set); a single `<2`-on-a-side row → empty; all-`x` row → empty; duplicate-inferring rows `[1100, 1100]` → size 1; even split `1100` → size 1 with `"{a, b} {c, d}"` (RED)
- [x] 4.2 Implement `bio.algorithms.graph.Quartets.compute` (per-row cross-product of 2-combinations of the `1`-side and `0`-side via `Quartet.of`, `.distinct`, deterministic `sortBy`) (GREEN)
- [x] 4.3 Run `sbt test` and confirm the algorithm spec passes

## 5. Runner and wiring

- [x] 5.1 Create `bio.problems.QRTProb` (`solve(): IO[Unit]`) parsing taxa (first line) + character rows and printing each quartet's `render`
- [x] 5.2 Wire `QRTProb.solve()` into `bio.Main.run`

## 6. Verification

- [x] 6.1 Run the full `sbt test` suite and confirm zero regressions
