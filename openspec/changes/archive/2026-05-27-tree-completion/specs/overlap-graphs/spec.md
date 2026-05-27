## MODIFIED Requirements

### Requirement: OverlapLengthError is a sealed ADT of OverlapLength construction failures
The system SHALL provide a `sealed trait OverlapLengthError` with case `final case class NonPositive(value: Int)`. The type SHALL reside in the `bio.domain.graph` package.

#### Scenario: NonPositive carries the offending value
- **WHEN** `OverlapLengthError.NonPositive(0)` is constructed
- **THEN** the value's `value` field equals `0`

#### Scenario: NonPositive accepts a negative value
- **WHEN** `OverlapLengthError.NonPositive(-3)` is constructed
- **THEN** the value's `value` field equals `-3`

### Requirement: OverlapLength is a validated positive integer wrapper
The system SHALL provide a `sealed abstract case class OverlapLength(value: Int)`. Construction SHALL be possible only through `OverlapLength.from(value: Int): Either[OverlapLengthError, OverlapLength]` enforcing `value >= 1`. The synthesized `apply` and `copy` SHALL NOT be public — direct construction `OverlapLength(3)` MUST be a compile error. The type SHALL reside in the `bio.domain.graph` package.

#### Scenario: k = 3 (Rosalind sample) is accepted
- **WHEN** `OverlapLength.from(3)` is called
- **THEN** the result is `Right(<OverlapLength with value=3>)`

#### Scenario: k = 1 (minimum) is accepted
- **WHEN** `OverlapLength.from(1)` is called
- **THEN** the result is `Right(<OverlapLength with value=1>)`

#### Scenario: k = 0 is rejected
- **WHEN** `OverlapLength.from(0)` is called
- **THEN** the result is `Left(OverlapLengthError.NonPositive(0))`

#### Scenario: Negative k is rejected
- **WHEN** `OverlapLength.from(-5)` is called
- **THEN** the result is `Left(OverlapLengthError.NonPositive(-5))`

#### Scenario: Direct apply does not compile
- **WHEN** source code `bio.domain.graph.OverlapLength(3)` is compiled
- **THEN** the compiler rejects the expression

### Requirement: OverlapEdge represents a single directed edge in an overlap graph
The system SHALL provide a `final case class OverlapEdge(from: String, to: String)` where `from` and `to` are the ids of the source and target FASTA records. The case class has a public constructor — `OverlapEdge` carries no further invariant beyond holding two strings. The type SHALL reside in the `bio.domain.graph` package.

#### Scenario: OverlapEdge exposes from and to fields
- **WHEN** `OverlapEdge("Rosalind_0498", "Rosalind_2391")` is constructed
- **THEN** `edge.from == "Rosalind_0498"` and `edge.to == "Rosalind_2391"`

### Requirement: OverlapGraph.adjacency computes the directed overlap-graph adjacency list
The system SHALL provide `OverlapGraph.adjacency(records: Vector[bio.parsing.FastaRecord], k: OverlapLength): Vector[OverlapEdge]` returning every directed edge `OverlapEdge(s.id, t.id)` such that the length-`k.value` suffix of `s.dna.value` equals the length-`k.value` prefix of `t.dna.value` and `s.id != t.id`. The algorithm SHALL reside in the `bio.algorithms.graph` package. The function SHALL be total — every input produces a defined `Vector[OverlapEdge]`. Edges SHALL be emitted in the order produced by iterating `records` in the outer loop and `records` in the inner loop (input order × input order), skipping self pairs.

#### Scenario: Rosalind sample produces the canonical three edges at k=3
- **WHEN** `OverlapGraph.adjacency(records, OverlapLength.from(3).toOption.get)` is called with the Rosalind sample records (`Rosalind_0498="AAATAAA"`, `Rosalind_2391="AAATTTT"`, `Rosalind_2323="TTTTCCC"`, `Rosalind_0442="AAATCCC"`, `Rosalind_5013="GGGTGGG"` in that order)
- **THEN** the result is `Vector(OverlapEdge("Rosalind_0498", "Rosalind_2391"), OverlapEdge("Rosalind_0498", "Rosalind_0442"), OverlapEdge("Rosalind_2391", "Rosalind_2323"))`

#### Scenario: Empty record list produces no edges
- **WHEN** `OverlapGraph.adjacency(Vector.empty, OverlapLength.from(3).toOption.get)` is called
- **THEN** the result is `Vector.empty`

#### Scenario: Single record produces no edges (self-loops excluded)
- **WHEN** `OverlapGraph.adjacency(Vector(FastaRecord("R1", DnaString.from("AAATAAA").toOption.get)), OverlapLength.from(3).toOption.get)` is called
- **THEN** the result is `Vector.empty`

#### Scenario: Self-loops excluded even when a record's own suffix matches its own prefix
- **WHEN** `OverlapGraph.adjacency` is called with two records, where `R1.dna = "AAATAAA"` (whose own length-3 suffix `"AAA"` equals its own length-3 prefix), paired with an unrelated `R2.dna = "GGGGGGG"`
- **THEN** the result contains no `OverlapEdge("R1", "R1")` edge

#### Scenario: Sequence shorter than k produces no edges involving it
- **WHEN** `OverlapGraph.adjacency` is called with two records `R1.dna = "AC"` (length 2 < k=3) and `R2.dna = "ACGGG"`
- **THEN** the result is `Vector.empty` (no edges from or to `R1` because `R1` is shorter than `k`)

#### Scenario: A record can be the source of multiple edges
- **WHEN** `OverlapGraph.adjacency` is called with three records `R1.dna = "AAATAAA"`, `R2.dna = "AAATTTT"`, `R3.dna = "AAATCCC"` at k=3 (`R1` suffix `"AAA"` matches both `R2` and `R3` prefixes)
- **THEN** the result is `Vector(OverlapEdge("R1", "R2"), OverlapEdge("R1", "R3"))`

#### Scenario: Edges respect direction — suffix(s) == prefix(t) does not imply suffix(t) == prefix(s)
- **WHEN** `OverlapGraph.adjacency` is called with two records `R1.dna = "AAATAAA"` and `R2.dna = "AAATTTT"` at k=3
- **THEN** the result is `Vector(OverlapEdge("R1", "R2"))` (no reverse edge — `R2`'s suffix `"TTT"` does not equal `R1`'s prefix `"AAA"`)

#### Scenario: k = 1 produces edges based on single-character endpoints
- **WHEN** `OverlapGraph.adjacency` is called with `R1.dna = "ACG"` (ends in `"G"`) and `R2.dna = "GCT"` (starts with `"G"`) at k=1
- **THEN** the result is `Vector(OverlapEdge("R1", "R2"))`
