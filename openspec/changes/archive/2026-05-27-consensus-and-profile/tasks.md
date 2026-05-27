## 1. ConsensusProfileProblem + Error (TDD)

- [x] 1.1 Write `ConsensusProfileProblemErrorSpec` covering: `EmptyInput` singleton identity; `LengthMismatch(Vector(8,7,8))` field exposure; run, observe red
- [x] 1.2 Write `ConsensusProfileProblemSpec` covering: accept seven Rosalind sample records, accept single record, reject empty input (`EmptyInput`), reject length-mismatched records (`LengthMismatch(Vector(8,7,8))`), validation order (empty before mismatch), `assertDoesNotCompile` for direct `ConsensusProfileProblem(records)`; run, observe red
- [x] 1.3 Implement `bio.domain.matrix.ConsensusProfileProblemError` sealed trait with `EmptyInput` (case object) and `LengthMismatch(lengths: Vector[Int])` (final case class)
- [x] 1.4 Implement `bio.domain.matrix.ConsensusProfileProblem` as `sealed abstract case class ConsensusProfileProblem(records: Vector[FastaRecord])` with `from` smart constructor enforcing non-empty + equal lengths; run all tests green
- [x] 1.5 Refactor pass: scaladoc describing both validation rules, validation order (empty first), and the `sealed abstract case class` pattern

## 2. ProfileMatrix + Error (TDD)

- [x] 2.1 Write `ProfileMatrixErrorSpec` covering: `MissingNucleotide(G)` field exposure; `UnequalColumnLengths(Map(A -> 8, C -> 8, G -> 7, T -> 8))` field exposure; run, observe red
- [x] 2.2 Write `ProfileMatrixSpec` covering: accept the canonical Rosalind sample's counts (exposes `.a`, `.c`, `.g`, `.t`, `.width` correctly); accept width-0 (all empty vectors); reject map missing `G` key as `MissingNucleotide(G)`; reject unequal column lengths as `UnequalColumnLengths(...)`; `assertDoesNotCompile` for direct `ProfileMatrix(Map.empty)`; run, observe red
- [x] 2.3 Implement `bio.domain.matrix.ProfileMatrixError` sealed trait with `MissingNucleotide(missing: DnaNucleotide)` and `UnequalColumnLengths(lengths: Map[DnaNucleotide, Int])` cases
- [x] 2.4 Implement `bio.domain.matrix.ProfileMatrix` as `sealed abstract case class ProfileMatrix(counts: Map[DnaNucleotide, Vector[Int]])` with public `a`/`c`/`g`/`t`/`width` accessors, public `from` smart constructor, and `private[bio] unsafeFrom`; run all tests green
- [x] 2.5 Refactor pass: scaladoc describing the map representation (ADT-symmetric iteration), the `unsafeFrom` usage rules, and the `sealed abstract case class` pattern

## 3. ConsensusProfileResult (TDD)

- [x] 3.1 Write `ConsensusProfileResultSpec` covering the field-exposure scenario (`consensus.value == "ATGCAACT"` and `profile` is the supplied matrix); run, observe red
- [x] 3.2 Implement `bio.domain.matrix.ConsensusProfileResult` as `final case class ConsensusProfileResult(consensus: DnaString, profile: ProfileMatrix)`; run all tests green
- [x] 3.3 Refactor pass: scaladoc describing the "pair of already-validated fields, no further invariant" decision

## 4. ConsensusProfile.compute algorithm (TDD)

- [x] 4.1 Write `ConsensusProfileSpec` covering all 6 scenarios from the spec: Rosalind sample produces canonical profile + `"ATGCAACT"` consensus; single-record input yields trivial counts and that record as consensus; tie A/C → consensus `A` (alphabetical); tie C/G → consensus `C`; `profile.width` matches input length; column-counts-sum equals record count at every column. Run, observe red
- [x] 4.2 Implement `bio.algorithms.matrix.ConsensusProfile.compute(problem: ConsensusProfileProblem): ConsensusProfileResult` per design.md (column-major counting, alphabetical tie-break via `Vector(A,C,G,T).find`, `ProfileMatrix.unsafeFrom` for the result, `DnaString.unsafeFrom` for the consensus); run all tests green
- [x] 4.3 Refactor pass: scaladoc describing the O(width × records × 4) complexity, the alphabetical tie-break rule, and the `unsafeFrom` invariant justifications

## 5. Whole-suite verification

- [x] 5.1 Run `sbt test` and confirm all existing suites plus all new tests pass with no warnings
- [x] 5.2 Verify no accidental modifications to existing files (`git status` should show only additions under `bio/domain/matrix/`, `bio/algorithms/matrix/`, and the new test files)
