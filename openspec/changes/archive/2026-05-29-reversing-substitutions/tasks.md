## 1. Error ADT (Red → Green)

- [x] 1.1 Write failing tests for `ReversingSubstitutionsProblemError` in `src/test/scala/bio/domain/analysis/ReversingSubstitutionsProblemErrorSpec.scala` covering each variant's shape and equality: `EmptyAlignment`, `LengthMismatch`, `SequenceTooLong`, `InvalidCharacter`, `TooManyStrings`, `InternalNodeMissingLabel`, `LeafMissingLabel`, `NonBinaryInternalNode`, `NodeLabelMismatch`.
- [x] 1.2 Create `src/main/scala/bio/domain/analysis/ReversingSubstitutionsProblemError.scala` as a `sealed trait` with the nine variants.
- [x] 1.3 Run `sbt test` and confirm the error-ADT tests pass.

## 2. ReversingSubstitution output record (Red → Green)

- [x] 2.1 Write failing tests for `ReversingSubstitution` in `src/test/scala/bio/domain/analysis/ReversingSubstitutionSpec.scala` covering named-field construction and equality.
- [x] 2.2 Create `src/main/scala/bio/domain/analysis/ReversingSubstitution.scala` as a plain `final case class` carrying the six fields.
- [x] 2.3 Run `sbt test` and confirm the output-record tests pass.

## 3. Domain bundle (Red → Green)

- [x] 3.1 Write failing tests for `ReversingSubstitutionsProblem.from` in `src/test/scala/bio/domain/analysis/ReversingSubstitutionsProblemSpec.scala` covering every scenario (canonical sample, empty-alignment, length-mismatch, sequence-too-long, invalid character, too-many-strings, internal/leaf missing label, non-binary, label-mismatch).
- [x] 3.2 Add no-leak compile-time guards for `apply` and `copy` via `assertDoesNotCompile`.
- [x] 3.3 Create `src/main/scala/bio/domain/analysis/ReversingSubstitutionsProblem.scala` as `sealed abstract case class ReversingSubstitutionsProblem(tree: NewickTree, alignment: Vector[NamedSequence])` with the smart constructor performing all nine validation steps in priority order.
- [x] 3.4 Run `sbt test` and confirm all domain-level tests pass.

## 4. Algorithm (Red → Green)

- [x] 4.1 Write failing tests for `ReversingSubstitutions.findAll` in `src/test/scala/bio/algorithms/analysis/ReversingSubstitutionsSpec.scala` covering: canonical Rosalind sample (set equality on the 5 expected reversions), single-edge tree (no reversion), all-identical strings (no reversions), and an intermediate-substitution-breaks-chain edge case.
- [x] 4.2 Create `src/main/scala/bio/algorithms/analysis/ReversingSubstitutions.scala` implementing the per-column, per-edge DFS algorithm: build a `labelToSeq` map, walk every parent-child edge for every column, when `s[i] != t[i]` DFS through descendants preserving `Y` until either a `X` (reversion) or a third symbol (stop).
- [x] 4.3 Run `sbt test` and confirm the algorithm tests pass.

## 5. Runner

- [x] 5.1 Create `src/main/scala/bio/problems/RSUBProb.scala` mirroring the ALPHProb pattern: load `src/main/scala/resources/rsub_data.txt` (Newick line + FASTA body, ACGT-only), parse via `NewickParser`, run an inline FASTA-style parser, construct the problem, call `findAll`, and print each reversion as `"firstChangeSpecies reversionSpecies position originalSymbol->substitutedSymbol->revertedSymbol"`.
- [x] 5.2 Place a starter `src/main/scala/resources/rsub_data.txt` with the canonical Rosalind sample so the runner is immediately executable.
- [x] 5.3 Do NOT modify `src/main/scala/bio/Main.scala` (it is user-managed).

## 6. Refactor & verify

- [x] 6.1 Read through the new files and refactor for clarity (rename locals, tighten Scaladoc, ensure imports are minimal). In particular, double-check the DFS termination conditions (stop on a third symbol, emit-and-don't-recurse on reversion).
- [x] 6.2 Run `sbt test` one final time and confirm all suites pass with zero regressions.
