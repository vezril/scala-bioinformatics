## 1. Error type

- [x] 1.1 RED: write `UnrootedBinaryTreesProblemErrorSpec` asserting `TooFewTaxa(count, min)`, `TooManyTaxa(count, max)`, and `DuplicateTaxon(name)` construct and expose their fields; confirm it fails to compile (`not found`).
- [x] 1.2 GREEN: create `bio.domain.graph.UnrootedBinaryTreesProblemError` — `sealed trait` + `TooFewTaxa(count: Int, min: Int)`, `TooManyTaxa(count: Int, max: Int)`, `DuplicateTaxon(name: String)`; run the spec green.

## 2. UnrootedBinaryTreesProblem domain type

- [x] 2.1 RED: write `UnrootedBinaryTreesProblemSpec` — accepts `dog cat mouse elephant` (Right, `taxa` preserved), accepts exactly 3 taxa, rejects 2 taxa (`TooFewTaxa(2, 3)`), rejects 11 taxa (`TooManyTaxa(11, 10)`), rejects duplicates `dog cat cat` (`DuplicateTaxon("cat")`), and `assertDoesNotCompile` for public `apply` and `copy`. Confirm RED.
- [x] 2.2 GREEN: create `bio.domain.graph.UnrootedBinaryTreesProblem` as `sealed abstract case class UnrootedBinaryTreesProblem(taxa: Vector[String])` with `MinTaxa = 3`, `MaxTaxa = 10`, and `from(taxa)` applying first-failure-wins (size < 3 → `TooFewTaxa`; size > 10 → `TooManyTaxa`; first duplicate name → `DuplicateTaxon`), building via `Right(new UnrootedBinaryTreesProblem(taxa) {})`. Run green.

## 3. UnrootedBinaryTrees result type

- [x] 3.1 RED: write `UnrootedBinaryTreesResultSpec` — `trees` field exposure, `format` of two Newick strings → newline-joined, empty → `""`. Confirm RED.
- [x] 3.2 GREEN: create `bio.domain.graph.UnrootedBinaryTrees` result — `final case class UnrootedBinaryTrees(trees: Vector[String])` with `format: String = trees.mkString("\n")`. Run green.

## 4. EnumerateUnrootedBinaryTrees algorithm

- [x] 4.1 RED: write `EnumerateUnrootedBinaryTreesSpec` — canonical `dog cat mouse elephant` → exactly the 3 trees `(((cat,mouse),elephant))dog;`, `(((cat,elephant),mouse))dog;`, `((cat,(mouse,elephant)))dog;` (via `theSameElementsAs`); `dog cat mouse` → exactly `((cat,mouse))dog;`; 5 taxa → 15 distinct trees; every tree ends with `)t0;` for first taxon `t0`. Build inputs via `UnrootedBinaryTreesProblem.from`. Confirm RED.
- [x] 4.2 GREEN: create `bio.algorithms.graph.EnumerateUnrootedBinaryTrees` with `enumerate(problem): UnrootedBinaryTrees`. Define a private `sealed trait BinaryTree` with `Leaf(name)` / `Node(left, right)`. `base = Node(Leaf(taxa(1)), Leaf(taxa(2)))`; fold `taxa.drop(3)` with `acc.flatMap(insertions(_, taxon))`, where `insertions(t, x)` returns one tree per node (Leaf → `List(Node(t, Leaf(x)))`; Node(l,r) → `Node(t, Leaf x) :: insertions(l,x).map(Node(_, r)) ++ insertions(r,x).map(Node(l, _))`). Render each via `NewickTree(Some(taxa(0)), Vector(toNewick(rest))).render`, where `toNewick(Leaf n) = NewickTree(Some(n), Vector.empty)` and `toNewick(Node(l,r)) = NewickTree(None, Vector(toNewick(l), toNewick(r)))`. Wrap in `UnrootedBinaryTrees`. Run green.
- [x] 4.3 REFACTOR: review for `var`/`while`/mutable collections (none — recursion + `flatMap` + immutable `Vector`); confirm `NewickTree.render` reuse and exhaustive `BinaryTree` matching. Run full `sbt test` green.

## 5. Runner

- [x] 5.1 Create `bio.problems.EUBTProb` reading the taxa from `eubt_data.txt` (single line, whitespace-separated), build `UnrootedBinaryTreesProblem.from`, run `EnumerateUnrootedBinaryTrees.enumerate`, print `format` via `IO.println`; all errors printed (never thrown).
- [x] 5.2 Wire `EUBTProb.solve()` as the active runner in `bio.Main` (comment the prior active line), and run the full `sbt test` suite to confirm everything is green.
