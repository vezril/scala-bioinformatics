## ADDED Requirements

### Requirement: RandomMatchProblemError is a sealed ADT of RandomMatchProblem construction failures
The system SHALL provide a `sealed trait RandomMatchProblemError` with cases `final case class DnaTooLong(length: Int, max: Int)` (the supplied DNA exceeded the per-problem maximum length) and `final case class TooManyGcContents(size: Int, max: Int)` (the GC-content array exceeded the per-problem maximum size). The type SHALL reside in the `bio.domain.analysis` package.

#### Scenario: DnaTooLong carries the offending length and the maximum
- **WHEN** `RandomMatchProblemError.DnaTooLong(150, 100)` is constructed
- **THEN** the value's `length` field equals `150` and `max` equals `100`

#### Scenario: TooManyGcContents carries the offending size and the maximum
- **WHEN** `RandomMatchProblemError.TooManyGcContents(25, 20)` is constructed
- **THEN** the value's `size` field equals `25` and `max` equals `20`

### Requirement: RandomMatchProblem is a validated parameter bundle for the random-string-matching algorithm
The system SHALL provide a `sealed abstract case class RandomMatchProblem(dna: bio.domain.nucleic.DnaString, gcContents: Vector[bio.domain.stats.Probability])`. Construction SHALL be possible only through `RandomMatchProblem.from(dna: DnaString, gcContents: Vector[Probability]): Either[RandomMatchProblemError, RandomMatchProblem]` enforcing `dna.value.length <= 100` and `gcContents.size <= 20`. Validation SHALL apply in the order: DNA length, then GC-content array size (first failure wins). The synthesized `apply` and `copy` SHALL NOT be public — direct construction `RandomMatchProblem(dna, gcContents)` MUST be a compile error. The type SHALL reside in the `bio.domain.analysis` package.

#### Scenario: Rosalind sample parameters are accepted
- **WHEN** `RandomMatchProblem.from(dna, gcContents)` is called with `dna = DnaString.from("ACGATACAA").toOption.get` and `gcContents = Vector(0.129, 0.287, 0.423, 0.476, 0.641, 0.742, 0.783).map(Probability.from(_).toOption.get)`
- **THEN** the result is `Right(<RandomMatchProblem with the supplied dna and 7 GC values>)`

#### Scenario: Empty DNA is accepted
- **WHEN** `RandomMatchProblem.from(DnaString.from("").toOption.get, Vector(Probability.from(0.5).toOption.get))` is called
- **THEN** the result is `Right(<RandomMatchProblem with an empty DnaString and one GC value>)`

#### Scenario: Empty GC array is accepted
- **WHEN** `RandomMatchProblem.from(DnaString.from("ACGT").toOption.get, Vector.empty)` is called
- **THEN** the result is `Right(<RandomMatchProblem with the supplied dna and zero GC values>)`

#### Scenario: Upper-bound DNA length (100) is accepted
- **WHEN** `RandomMatchProblem.from(dna, Vector.empty)` is called with a `DnaString` of length 100
- **THEN** the result is `Right(<RandomMatchProblem>)`

#### Scenario: Upper-bound GC-content count (20) is accepted
- **WHEN** `RandomMatchProblem.from(DnaString.from("A").toOption.get, gcContents)` is called with `gcContents.size == 20`
- **THEN** the result is `Right(<RandomMatchProblem>)`

#### Scenario: DNA longer than 100 is rejected
- **WHEN** `RandomMatchProblem.from(dna, Vector.empty)` is called with a `DnaString` of length 101
- **THEN** the result is `Left(RandomMatchProblemError.DnaTooLong(101, 100))`

#### Scenario: GC-content array larger than 20 is rejected
- **WHEN** `RandomMatchProblem.from(DnaString.from("A").toOption.get, gcContents)` is called with `gcContents.size == 21`
- **THEN** the result is `Left(RandomMatchProblemError.TooManyGcContents(21, 20))`

#### Scenario: Validation order — DNA length checked before GC-content array size
- **WHEN** `RandomMatchProblem.from(dna, gcContents)` is called with a `DnaString` of length 101 and a `gcContents` of size 21 (both invalid)
- **THEN** the result is `Left(RandomMatchProblemError.DnaTooLong(101, 100))`

#### Scenario: Direct apply does not compile
- **WHEN** source code `bio.domain.analysis.RandomMatchProblem(bio.domain.nucleic.DnaString.from("A").toOption.get, Vector.empty)` is compiled
- **THEN** the compiler rejects the expression

### Requirement: RandomMatch.logProbabilities computes per-GC log10 match probabilities
The system SHALL provide `RandomMatch.logProbabilities(problem: RandomMatchProblem): Vector[Double]` returning a `Vector[Double]` whose length equals `problem.gcContents.size`. For each index `k`, `result(k)` SHALL equal `Σ_{c ∈ problem.dna.value} log10(symbolProbability(c, problem.gcContents(k).value))` where `symbolProbability('G', x) = symbolProbability('C', x) = x / 2` and `symbolProbability('A', x) = symbolProbability('T', x) = (1 − x) / 2`. The algorithm SHALL reside in the `bio.algorithms.analysis` package. The function SHALL be total — every valid `RandomMatchProblem` produces a defined `Vector[Double]`.

#### Scenario: Rosalind sample produces the canonical log probabilities (within 0.001 absolute error)
- **WHEN** `RandomMatch.logProbabilities(problem)` is called with `problem` built from `dna = "ACGATACAA"` and `gcContents = Vector(0.129, 0.287, 0.423, 0.476, 0.641, 0.742, 0.783)`
- **THEN** the result is `Vector(-5.737, -5.217, -5.263, -5.360, -5.958, -6.628, -7.009)` (each value within `0.001` absolute error of the corresponding expected value)

#### Scenario: Empty DNA yields zero log-probability for every GC value
- **WHEN** `RandomMatch.logProbabilities(problem)` is called with `problem.dna.value == ""` and three arbitrary GC values
- **THEN** the result is `Vector(0.0, 0.0, 0.0)` (empty product = 1, log10(1) = 0)

#### Scenario: Empty GC-content array yields an empty result
- **WHEN** `RandomMatch.logProbabilities(problem)` is called with `problem.gcContents == Vector.empty`
- **THEN** the result is `Vector.empty`

#### Scenario: gc = 0 with a G in the sequence yields negative infinity
- **WHEN** `RandomMatch.logProbabilities(problem)` is called with `problem.dna.value == "G"` and `problem.gcContents == Vector(Probability.from(0.0).toOption.get)`
- **THEN** the result is `Vector(Double.NegativeInfinity)` (since `log10(0) = -∞`)

#### Scenario: gc = 1 with an A in the sequence yields negative infinity
- **WHEN** `RandomMatch.logProbabilities(problem)` is called with `problem.dna.value == "A"` and `problem.gcContents == Vector(Probability.from(1.0).toOption.get)`
- **THEN** the result is `Vector(Double.NegativeInfinity)` (since `log10(0) = -∞` for `P(A) = (1 − 1) / 2 = 0`)

#### Scenario: A single G with gc = 0.5 yields log10(0.25) ≈ -0.6021
- **WHEN** `RandomMatch.logProbabilities(problem)` is called with `problem.dna.value == "G"` and `problem.gcContents == Vector(Probability.from(0.5).toOption.get)`
- **THEN** the result is `Vector(d)` where `|d − Math.log10(0.25)| < 1e-9` (i.e., `d ≈ -0.6021`)

#### Scenario: Result length always equals input gcContents length
- **WHEN** `RandomMatch.logProbabilities(problem)` is called with a `gcContents` of size 7
- **THEN** the result has size 7
