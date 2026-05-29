## 1. Error ADT (Red → Green)

- [x] 1.1 Write failing tests for `AlignmentBasedPhylogenyProblemError` in `src/test/scala/bio/domain/analysis/AlignmentBasedPhylogenyProblemErrorSpec.scala` covering each variant's shape and equality: `EmptyAlignment`, `LengthMismatch`, `SequenceTooLong`, `InvalidCharacter`, `TooManyLeaves`, `InternalNodeMissingLabel`, `NonBinaryInternalNode`, `LeafLabelMismatch`.
- [x] 1.2 Create `src/main/scala/bio/domain/analysis/AlignmentBasedPhylogenyProblemError.scala` as a `sealed trait` with eight variants (some case classes, some case objects where there's no associated data).
- [x] 1.3 Run `sbt test` and confirm the new error-ADT tests pass.

## 2. NamedSequence and AlignmentBasedPhylogeny output ADTs (Red → Green)

- [x] 2.1 Write failing tests for `NamedSequence` in `src/test/scala/bio/domain/analysis/NamedSequenceSpec.scala` covering named-field construction and equality.
- [x] 2.2 Write failing tests for `AlignmentBasedPhylogeny` (the output ADT) in `src/test/scala/bio/domain/analysis/AlignmentBasedPhylogenySpec.scala` covering named-field construction, equality, and `copy`.
- [x] 2.3 Create `src/main/scala/bio/domain/analysis/NamedSequence.scala` as a plain `final case class` with `label: String` and `sequence: String`.
- [x] 2.4 Create `src/main/scala/bio/domain/analysis/AlignmentBasedPhylogeny.scala` as a plain `final case class` carrying `totalDistance: Int` and `internalAssignments: Vector[NamedSequence]`.
- [x] 2.5 Run `sbt test` and confirm all output-ADT tests pass.

## 3. Domain bundle (Red → Green)

- [x] 3.1 Write failing tests for `AlignmentBasedPhylogenyProblem.from` in `src/test/scala/bio/domain/analysis/AlignmentBasedPhylogenyProblemSpec.scala` covering every scenario (canonical sample, empty-alignment rejection, length-mismatch rejection, over-length sequence, invalid character, unlabeled-internal-node rejection, non-binary-internal-node rejection, leaf-label-mismatch rejection).
- [x] 3.2 Add no-leak compile-time guards for `apply` and `copy` via `assertDoesNotCompile`.
- [x] 3.3 Create `src/main/scala/bio/domain/analysis/AlignmentBasedPhylogenyProblem.scala` as `sealed abstract case class AlignmentBasedPhylogenyProblem(tree: NewickTree, alignment: Vector[NamedSequence])` with a smart constructor `from(tree, alignment)` performing all eight validation steps in priority order.
- [x] 3.4 Run `sbt test` and confirm all domain-level tests pass.

## 4. Algorithm (Red → Green)

- [x] 4.1 Write failing tests for `AlignmentBasedPhylogeny.solve` in `src/test/scala/bio/algorithms/analysis/AlignmentBasedPhylogenySpec.scala` covering the canonical Rosalind sample (totalDistance == 8 + invariants), all-identical-leaves yields totalDistance 0, two-leaf trees give a Hamming-equal distance, single-column gap-vs-non-gap yields totalDistance 1, and a sum-of-edges invariant for arbitrary input.
- [x] 4.2 Add helpers `hamming(a, b): Int`, `sumOverEdges(tree, assignments): Int`, and `assertValidPhylogeny(result, problem)` inside the spec file to express the five invariants as reusable assertions.
- [x] 4.3 Create `src/main/scala/bio/algorithms/analysis/AlignmentBasedPhylogeny.scala` implementing the per-column Sankoff small-parsimony DP + traceback over the 5-symbol alphabet `{A, C, G, T, -}`. Each column independently runs bottom-up cost computation then top-down symbol selection; per-internal-node `StringBuilder` accumulates the per-column chosen symbol.
- [x] 4.4 Run `sbt test` and confirm the algorithm tests pass.

## 5. Runner

- [x] 5.1 Create `src/main/scala/bio/problems/ALPHProb.scala` mirroring the EDTAProb pattern: parse a hardcoded Newick string via `NewickParser.parse`, build a `Vector[NamedSequence]`, construct the problem, call `solve`, and print the integer total distance followed by each internal-node FASTA record (`>label` line + sequence line).
- [x] 5.2 Do NOT modify `src/main/scala/bio/Main.scala` (it is user-managed).

## 6. Refactor & verify

- [x] 6.1 Read through the new files and refactor for clarity (rename locals, tighten Scaladoc, ensure imports are minimal). In particular, double-check the per-column traceback table arithmetic and the pre-order traversal output.
- [x] 6.2 Run `sbt test` one final time and confirm all suites pass with zero regressions.
