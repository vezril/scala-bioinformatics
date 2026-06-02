## 1. Error type

- [x] 1.1 RED: write `PedigreeProblemErrorSpec` asserting `InvalidGenotype(label)` and `NotBinary(childCount)` construct and expose their fields; confirm it fails to compile (`not found`).
- [x] 1.2 GREEN: create `bio.domain.genetics.PedigreeProblemError` — `sealed trait` + `InvalidGenotype(label: String)`, `NotBinary(childCount: Int)`; run the spec green.

## 2. Pedigree ADT and PedigreeProblem domain type

- [x] 2.1 RED: write `PedigreeProblemSpec` — builds from the parsed tree of `((Aa,aa),(Aa,Aa));` (Right), accepts `Aa;` (Right), rejects a leaf labelled `Bb` (`InvalidGenotype("Bb")`), rejects an internal node with three children (`NotBinary(3)`), and `assertDoesNotCompile` for public `apply` and `copy`. Build `NewickTree` inputs via `NewickParser.parse` (or directly). Confirm RED.
- [x] 2.2 GREEN: create `bio.domain.genetics.Pedigree` (`sealed trait` + `KnownAncestor(genotype: Genotype)`, `Offspring(parent1: Pedigree, parent2: Pedigree)`) and `bio.domain.genetics.PedigreeProblem` as `sealed abstract case class PedigreeProblem(pedigree: Pedigree)` with `from(tree: NewickTree): Either[PedigreeProblemError, PedigreeProblem]`. Recursively convert: 0 children → map label (`AA`→`HomozygousDominant`, `Aa`→`Heterozygous`, `aa`→`HomozygousRecessive`, else `InvalidGenotype`); 2 children → `Offspring(convert(c0), convert(c1))`; other arity → `NotBinary(n)`; first failure wins; build via `Right(new PedigreeProblem(p) {})`. Run green.

## 3. GenotypeProbabilities result type

- [x] 3.1 RED: write `GenotypeProbabilitiesResultSpec` — field exposure (`homozygousDominant`, `heterozygous`, `homozygousRecessive`), `format` of `(0.156, 0.5, 0.344)` → `"0.156 0.500 0.344"`. Confirm RED.
- [x] 3.2 GREEN: create `bio.domain.genetics.GenotypeProbabilities` — `final case class GenotypeProbabilities(homozygousDominant: Double, heterozygous: Double, homozygousRecessive: Double)` with `format: String = f"$homozygousDominant%.3f $heterozygous%.3f $homozygousRecessive%.3f"`. Run green.

## 4. InferGenotype algorithm

- [x] 4.1 RED: write `InferGenotypeSpec` — canonical `((((Aa,aa),(Aa,Aa)),((aa,aa),(aa,AA))),Aa);` → within 0.001 of `(0.156, 0.5, 0.344)`; `Aa;` → `(0.0, 1.0, 0.0)`; `(Aa,Aa);` → within 0.001 of `(0.25, 0.5, 0.25)`; `(AA,aa);` → `(0.0, 1.0, 0.0)`. Build inputs via `NewickParser.parse` + `PedigreeProblem.from`. Confirm RED.
- [x] 4.2 GREEN: create `bio.algorithms.genetics.InferGenotype` with `infer(problem): GenotypeProbabilities`. Recurse over `Pedigree`: `KnownAncestor(HomozygousDominant)` → `(1,0,0)`, `Heterozygous` → `(0,1,0)`, `HomozygousRecessive` → `(0,0,1)`; `Offspring(p1,p2)` → with `tA = pAA + pAa/2` for each child, `pAA = tA1*tA2`, `paa = (1-tA1)*(1-tA2)`, `pAa = 1 - pAA - paa`. Wrap the root triple in `GenotypeProbabilities`. Run green.
- [x] 4.3 REFACTOR: review for `var`/`while`/mutable collections (none — pure recursion); extract a private `distribution(Pedigree): (Double,Double,Double)` helper and a `transmitA` helper; confirm `Genotype` reuse and exhaustive matching. Run full `sbt test` green.

## 5. Runner

- [x] 5.1 Populate `src/main/scala/resources/mend_data.txt` with the canonical sample Newick `((((Aa,aa),(Aa,Aa)),((aa,aa),(aa,AA))),Aa);` (the file is currently empty). Create `bio.problems.MENDProb` reading the single Newick line, parsing via `NewickParser.parse`, building `PedigreeProblem.from`, running `InferGenotype.infer`, printing `format` via `IO.println`; all errors printed (never thrown).
- [x] 5.2 Wire `MENDProb.solve()` as the active runner in `bio.Main` (comment the prior active line), and run the full `sbt test` suite to confirm everything is green.
