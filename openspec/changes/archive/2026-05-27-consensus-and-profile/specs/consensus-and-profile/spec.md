## ADDED Requirements

### Requirement: ConsensusProfileProblemError is a sealed ADT of ConsensusProfileProblem construction failures
The system SHALL provide a `sealed trait ConsensusProfileProblemError` with cases `case object EmptyInput` and `final case class LengthMismatch(lengths: Vector[Int])`. The type SHALL reside in the `bio.domain.matrix` package.

#### Scenario: EmptyInput is a singleton
- **WHEN** `ConsensusProfileProblemError.EmptyInput` is referenced twice
- **THEN** the two references are the same instance (`eq`)

#### Scenario: LengthMismatch carries the offending lengths in input order
- **WHEN** `ConsensusProfileProblemError.LengthMismatch(Vector(8, 7, 8))` is constructed
- **THEN** the value's `lengths` field equals `Vector(8, 7, 8)`

### Requirement: ConsensusProfileProblem is a validated parameter bundle for the consensus-and-profile algorithm
The system SHALL provide a `sealed abstract case class ConsensusProfileProblem(records: Vector[bio.parsing.FastaRecord])`. Construction SHALL be possible only through `ConsensusProfileProblem.from(records: Vector[FastaRecord]): Either[ConsensusProfileProblemError, ConsensusProfileProblem]` enforcing: (1) `records` is non-empty, (2) every record's `dna.value.length` equals the first record's. The synthesized `apply` and `copy` SHALL NOT be public — direct construction `ConsensusProfileProblem(records)` MUST be a compile error. The type SHALL reside in the `bio.domain.matrix` package.

#### Scenario: Rosalind sample (seven equal-length records) is accepted
- **WHEN** `ConsensusProfileProblem.from(records)` is called with the seven Rosalind sample records (each `dna` of length 8)
- **THEN** the result is `Right(<ConsensusProfileProblem with 7 records>)`

#### Scenario: Single record is accepted
- **WHEN** `ConsensusProfileProblem.from(Vector(FastaRecord("R1", DnaString.from("ACGT").toOption.get)))` is called
- **THEN** the result is `Right(<ConsensusProfileProblem with 1 record>)`

#### Scenario: Empty input is rejected
- **WHEN** `ConsensusProfileProblem.from(Vector.empty)` is called
- **THEN** the result is `Left(ConsensusProfileProblemError.EmptyInput)`

#### Scenario: Differing record lengths are rejected
- **WHEN** `ConsensusProfileProblem.from` is called with three records of lengths 8, 7, 8
- **THEN** the result is `Left(ConsensusProfileProblemError.LengthMismatch(Vector(8, 7, 8)))`

#### Scenario: EmptyInput is checked before length-mismatch
- **WHEN** `ConsensusProfileProblem.from(Vector.empty)` is called
- **THEN** the result is `Left(ConsensusProfileProblemError.EmptyInput)` (not `LengthMismatch(Vector.empty)`)

#### Scenario: Direct apply does not compile
- **WHEN** source code `bio.domain.matrix.ConsensusProfileProblem(Vector.empty)` is compiled
- **THEN** the compiler rejects the expression

### Requirement: ProfileMatrixError is a sealed ADT of ProfileMatrix construction failures
The system SHALL provide a `sealed trait ProfileMatrixError` with cases `final case class MissingNucleotide(missing: bio.domain.nucleic.DnaNucleotide)` and `final case class UnequalColumnLengths(lengths: Map[bio.domain.nucleic.DnaNucleotide, Int])`. The type SHALL reside in the `bio.domain.matrix` package.

#### Scenario: MissingNucleotide carries the missing nucleotide
- **WHEN** `ProfileMatrixError.MissingNucleotide(DnaNucleotide.G)` is constructed
- **THEN** the value's `missing` field equals `DnaNucleotide.G`

#### Scenario: UnequalColumnLengths carries the per-nucleotide lengths
- **WHEN** `ProfileMatrixError.UnequalColumnLengths(Map(A -> 8, C -> 8, G -> 7, T -> 8))` is constructed
- **THEN** the value's `lengths` field equals `Map(A -> 8, C -> 8, G -> 7, T -> 8)`

### Requirement: ProfileMatrix is a validated 4-row per-column-count matrix indexed by DnaNucleotide
The system SHALL provide a `sealed abstract case class ProfileMatrix(counts: Map[bio.domain.nucleic.DnaNucleotide, Vector[Int]])` with public accessors `a: Vector[Int]`, `c: Vector[Int]`, `g: Vector[Int]`, `t: Vector[Int]` returning `counts(DnaNucleotide.A)` etc., and `width: Int` returning the common column length. Construction SHALL be possible only through `ProfileMatrix.from(counts: Map[DnaNucleotide, Vector[Int]]): Either[ProfileMatrixError, ProfileMatrix]` enforcing: (1) `counts` contains all four `DnaNucleotide` keys, (2) all four `Vector[Int]` values have the same length. A `private[bio] unsafeFrom(counts): ProfileMatrix` SHALL exist for trusted internal callers. The synthesized `apply` and `copy` SHALL NOT be public. The type SHALL reside in the `bio.domain.matrix` package.

#### Scenario: A complete 4-key map with equal-length vectors is accepted
- **WHEN** `ProfileMatrix.from(Map(A -> Vector(5,1,0,0), C -> Vector(0,0,1,4), G -> Vector(1,1,6,3), T -> Vector(1,5,0,0)))` is called
- **THEN** the result is `Right(<ProfileMatrix>)` and the resulting `.a`, `.c`, `.g`, `.t`, `.width` accessors return `Vector(5,1,0,0)`, `Vector(0,0,1,4)`, `Vector(1,1,6,3)`, `Vector(1,5,0,0)`, `4`

#### Scenario: A 4-key map with empty vectors is accepted (width-0 matrix)
- **WHEN** `ProfileMatrix.from(Map(A -> Vector.empty, C -> Vector.empty, G -> Vector.empty, T -> Vector.empty))` is called
- **THEN** the result is `Right(<ProfileMatrix>)` and `.width == 0`

#### Scenario: A map missing the G key is rejected
- **WHEN** `ProfileMatrix.from(Map(A -> Vector(1), C -> Vector(1), T -> Vector(1)))` is called
- **THEN** the result is `Left(ProfileMatrixError.MissingNucleotide(DnaNucleotide.G))`

#### Scenario: A map with one column shorter than the others is rejected
- **WHEN** `ProfileMatrix.from(Map(A -> Vector(1,1), C -> Vector(1,1), G -> Vector(1), T -> Vector(1,1)))` is called
- **THEN** the result is `Left(ProfileMatrixError.UnequalColumnLengths(Map(A -> 2, C -> 2, G -> 1, T -> 2)))`

#### Scenario: Direct apply does not compile
- **WHEN** source code `bio.domain.matrix.ProfileMatrix(Map.empty)` is compiled
- **THEN** the compiler rejects the expression

### Requirement: ConsensusProfileResult pairs a consensus DnaString with its ProfileMatrix
The system SHALL provide a `final case class ConsensusProfileResult(consensus: bio.domain.nucleic.DnaString, profile: ProfileMatrix)`. Both fields are already validated by their own types; `ConsensusProfileResult` carries no further invariant. The type SHALL reside in the `bio.domain.matrix` package.

#### Scenario: ConsensusProfileResult exposes consensus and profile fields
- **WHEN** `ConsensusProfileResult(DnaString.from("ATGCAACT").toOption.get, <some ProfileMatrix>)` is constructed
- **THEN** `result.consensus.value == "ATGCAACT"` and `result.profile` is the supplied `ProfileMatrix`

### Requirement: ConsensusProfile.compute produces the profile matrix and consensus string for a validated input
The system SHALL provide `ConsensusProfile.compute(problem: ConsensusProfileProblem): ConsensusProfileResult`. The function SHALL be total — every valid `ConsensusProfileProblem` produces a defined `ConsensusProfileResult`. The profile matrix SHALL be a per-column count of each nucleotide across all records (width = the common record length). The consensus string SHALL be of length `width`; for each column, the consensus character SHALL be the nucleotide with the maximum count in that column, with ties broken alphabetically (A < C < G < T). The algorithm SHALL reside in the `bio.algorithms.matrix` package.

#### Scenario: Rosalind sample produces the canonical profile and consensus
- **WHEN** `ConsensusProfile.compute(problem)` is called with the seven Rosalind sample records (`Rosalind_1="ATCCAGCT"`, `Rosalind_2="GGGCAACT"`, `Rosalind_3="ATGGATCT"`, `Rosalind_4="AAGCAACC"`, `Rosalind_5="TTGGAACT"`, `Rosalind_6="ATGCCATT"`, `Rosalind_7="ATGGCACT"`)
- **THEN** the result's `consensus.value` is `"ATGCAACT"` and the profile's accessors return `a == Vector(5,1,0,0,5,5,0,0)`, `c == Vector(0,0,1,4,2,0,6,1)`, `g == Vector(1,1,6,3,0,1,0,0)`, `t == Vector(1,5,0,0,0,1,1,6)`

#### Scenario: Single-record input yields trivial counts and the record itself as consensus
- **WHEN** `ConsensusProfile.compute(problem)` is called with a single record `R1.dna = "ACGT"`
- **THEN** the result's `consensus.value` is `"ACGT"`, and the profile counts at each column are `1` for the present nucleotide and `0` for the others

#### Scenario: Tie at a column is broken alphabetically (A wins over C/G/T)
- **WHEN** `ConsensusProfile.compute(problem)` is called with two records `R1.dna = "A"` and `R2.dna = "C"` (one-column input, A and C tied at count 1)
- **THEN** the result's `consensus.value` is `"A"` (alphabetical tie-break)

#### Scenario: Tie at a column is broken alphabetically (C wins over G/T)
- **WHEN** `ConsensusProfile.compute(problem)` is called with two records `R1.dna = "C"` and `R2.dna = "G"` (one-column input, C and G tied at count 1)
- **THEN** the result's `consensus.value` is `"C"` (alphabetical tie-break)

#### Scenario: Width matches input length
- **WHEN** `ConsensusProfile.compute(problem)` is called with the Rosalind sample (each record length 8)
- **THEN** the result's `profile.width == 8`

#### Scenario: Column counts sum to the number of records in each column
- **WHEN** `ConsensusProfile.compute(problem)` is called with the seven Rosalind sample records
- **THEN** for every column index `j in 0 until profile.width`, `profile.a(j) + profile.c(j) + profile.g(j) + profile.t(j) == 7`
