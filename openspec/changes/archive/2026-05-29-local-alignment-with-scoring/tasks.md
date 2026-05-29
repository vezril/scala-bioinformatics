## 1. Error ADT (Red → Green)

- [x] 1.1 Write failing tests for `LocalAlignmentProblemError` in `src/test/scala/bio/domain/protein/LocalAlignmentProblemErrorSpec.scala` covering `LeftTooLong(length, max)` and `RightTooLong(length, max)` shapes and equality.
- [x] 1.2 Create `src/main/scala/bio/domain/protein/LocalAlignmentProblemError.scala` as a `sealed trait` with two case classes carrying `length: Int` and `max: Int`.
- [x] 1.3 Run `sbt test` and confirm the error-ADT tests pass.

## 2. LocalAlignment output ADT (Red → Green)

- [x] 2.1 Write failing tests for `LocalAlignment` (the output ADT) in `src/test/scala/bio/domain/protein/LocalAlignmentSpec.scala` covering named-field construction, equality, and `copy`.
- [x] 2.2 Create `src/main/scala/bio/domain/protein/LocalAlignment.scala` as a plain `final case class` carrying `score: Int`, `leftSubstring: String`, `rightSubstring: String`.
- [x] 2.3 Run `sbt test` and confirm the output-ADT tests pass.

## 3. Domain bundle (Red → Green)

- [x] 3.1 Write failing tests for `LocalAlignmentProblem.from` in `src/test/scala/bio/domain/protein/LocalAlignmentProblemSpec.scala` covering every scenario (canonical sample, empties, both-at-cap, 1001-char rejections in first-failure-wins order).
- [x] 3.2 Add no-leak compile-time guards for `apply` and `copy` via `assertDoesNotCompile`.
- [x] 3.3 Create `src/main/scala/bio/domain/protein/LocalAlignmentProblem.scala` as `sealed abstract case class LocalAlignmentProblem(left: ProteinString, right: ProteinString)` with a smart constructor `from(left, right)` enforcing the 1000-cap checks in documented order.
- [x] 3.4 Run `sbt test` and confirm all domain-level tests pass.

## 4. PAM250 matrix (Red → Green)

- [x] 4.1 Write failing tests for `Pam250.score` in `src/test/scala/bio/algorithms/protein/Pam250Spec.scala` covering canonical reference values (`A/A=2`, `W/W=17`, `C/C=12`, `Y/Y=10`, `L/L=6`, `F/F=9`, `P/P=6`, `A/R=-2`, `W/C=-8`, `L/M=4`, `F/Y=7`, `I/V=4`, `L/I=2`) and a symmetry check over every pair of amino acids in `AminoAcid.all`.
- [x] 4.2 Create `src/main/scala/bio/algorithms/protein/Pam250.scala` exposing `score(a: AminoAcid, b: AminoAcid): Int`. Encode the 20 × 20 canonical PAM250 matrix as a private `Array[Array[Int]]` indexed by amino-acid ordinal (built once at object init via `AminoAcid.all`), using the same canonical NCBI ordering as `Blosum62`.
- [x] 4.3 Run `sbt test` and confirm all `Pam250` tests pass.

## 5. Algorithm (Red → Green)

- [x] 5.1 Write failing tests for `LocalAlignment.compute` in `src/test/scala/bio/algorithms/protein/LocalAlignmentSpec.scala` covering every scenario in the spec (canonical Rosalind sample → score 23 + invariants, empty/empty, empty-left, empty-right, identical strings, single-letter self-substitution, single-letter cross-substitution with negative PAM250 yields score 0, symmetry).
- [x] 5.2 Create `src/main/scala/bio/algorithms/protein/LocalAlignment.scala` implementing the classical Smith-Waterman `O(m · n)` DP with 0-clamp + traceback from the global max cell. Use a hardcoded `private val Gap = -5` linear gap penalty.
- [x] 5.3 Run `sbt test` and confirm the algorithm tests pass.

## 6. Runner

- [x] 6.1 Create `src/main/scala/bio/problems/LOCAProb.scala` mirroring the EDTAProb pattern (build `LocalAlignmentProblem` from two `ProteinString`s, call `LocalAlignment.compute`, `IO.println` the score on one line followed by `leftSubstring` and `rightSubstring` on separate lines — matching the Rosalind output format).
- [x] 6.2 Do NOT modify `src/main/scala/bio/Main.scala` (it is user-managed).

## 7. Refactor & verify

- [x] 7.1 Read through the new files and refactor for clarity (rename locals, tighten Scaladoc, ensure imports are minimal). In particular, double-check the PAM250 matrix entries against the canonical reference table.
- [x] 7.2 Run `sbt test` one final time and confirm all suites pass with zero regressions.
