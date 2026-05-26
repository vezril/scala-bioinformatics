## ADDED Requirements

### Requirement: GcContent is a validated percentage in [0.0, 100.0]
The system SHALL provide a `sealed abstract case class GcContent(value: Double)` representing a GC-content percentage. Construction SHALL be possible only through `GcContent.from(value: Double): Either[GcContentError, GcContent]` (validates `[0.0, 100.0]`, rejects NaN and infinities) or `private[bio] def unsafeFrom(value: Double): GcContent` (internal, bypasses validation). The synthesized `apply` and `copy` SHALL NOT be public — direct construction (e.g., `GcContent(50.0)`) MUST be a compile error.

#### Scenario: Valid percentage is accepted
- **WHEN** `GcContent.from(50.0)` is called
- **THEN** the result is `Right(<GcContent of 50.0>)`

#### Scenario: Lower bound 0.0 is accepted
- **WHEN** `GcContent.from(0.0)` is called
- **THEN** the result is `Right(<GcContent of 0.0>)`

#### Scenario: Upper bound 100.0 is accepted
- **WHEN** `GcContent.from(100.0)` is called
- **THEN** the result is `Right(<GcContent of 100.0>)`

#### Scenario: Value greater than 100 is rejected
- **WHEN** `GcContent.from(100.1)` is called
- **THEN** the result is `Left(GcContentError.OutOfRange(100.1))`

#### Scenario: Negative value is rejected
- **WHEN** `GcContent.from(-0.1)` is called
- **THEN** the result is `Left(GcContentError.OutOfRange(-0.1))`

#### Scenario: NaN is rejected
- **WHEN** `GcContent.from(Double.NaN)` is called
- **THEN** the result is `Left(GcContentError.NotFinite)`

#### Scenario: Infinity is rejected
- **WHEN** `GcContent.from(Double.PositiveInfinity)` is called
- **THEN** the result is `Left(GcContentError.NotFinite)`

#### Scenario: Direct apply does not compile
- **WHEN** source code `bio.domain.GcContent(50.0)` is compiled
- **THEN** the compiler rejects the expression

### Requirement: GcContent.of computes GC content of a DnaString
The system SHALL provide `GcContent.of(dna: DnaString): GcContent` returning the percentage of bases in `dna` that are `G` or `C`. The function SHALL be total — empty `DnaString` SHALL return `GcContent(0.0)` by convention.

#### Scenario: Documented example "AGCTATAG" is 37.5%
- **WHEN** `GcContent.of(<DnaString of "AGCTATAG">)` is called
- **THEN** the resulting value equals `37.5` within tolerance `±1e-9`

#### Scenario: All-GC sequence is 100.0%
- **WHEN** `GcContent.of(<DnaString of "GCGC">)` is called
- **THEN** the resulting value equals `100.0` within tolerance `±1e-9`

#### Scenario: All-AT sequence is 0.0%
- **WHEN** `GcContent.of(<DnaString of "ATAT">)` is called
- **THEN** the resulting value equals `0.0` within tolerance `±1e-9`

#### Scenario: Empty DnaString returns 0.0% by convention
- **WHEN** `GcContent.of(<empty DnaString>)` is called
- **THEN** the resulting value equals `0.0`

#### Scenario: Single G returns 100.0%
- **WHEN** `GcContent.of(<DnaString of "G">)` is called
- **THEN** the resulting value equals `100.0` within tolerance `±1e-9`

#### Scenario: Single A returns 0.0%
- **WHEN** `GcContent.of(<DnaString of "A">)` is called
- **THEN** the resulting value equals `0.0` within tolerance `±1e-9`

### Requirement: HighestGc.find identifies the FastaRecord with highest GC content
The system SHALL provide `HighestGc.find(records: List[FastaRecord]): Option[(FastaRecord, GcContent)]` returning the record with the highest GC content paired with that content value. Empty input SHALL return `None`. Ties SHALL be resolved to the first record encountered (deterministic).

#### Scenario: Rosalind sample identifies Rosalind_0808 with ~60.919540%
- **WHEN** `find` is called with the canonical Rosalind sample (three records: `Rosalind_6404`, `Rosalind_5959`, `Rosalind_0808` with sequences from the problem statement)
- **THEN** the result is `Some((record, gc))` where `record.id == "Rosalind_0808"` and `gc.value` equals `60.919540` within tolerance `±1e-3`

#### Scenario: Empty list returns None
- **WHEN** `find` is called with `List.empty`
- **THEN** the result is `None`

#### Scenario: Single record is returned
- **WHEN** `find` is called with a single-record list `[FastaRecord("only", <DnaString of "GC">)]`
- **THEN** the result is `Some((<that record>, GcContent(100.0)))`

#### Scenario: First record wins a tie
- **WHEN** `find` is called with two records that both contain `GCGC` (same GC content of 100.0%)
- **THEN** the returned record is the first one in the input list
