## MODIFIED Requirements

### Requirement: GcContent is a validated percentage in [0.0, 100.0]
The system SHALL provide a `sealed abstract case class GcContent(value: Double)` representing a GC-content percentage. Construction SHALL be possible only through `GcContent.from(value: Double): Either[GcContentError, GcContent]` (validates `[0.0, 100.0]`, rejects NaN and infinities) or `private[bio] def unsafeFrom(value: Double): GcContent` (internal, bypasses validation). The synthesized `apply` and `copy` SHALL NOT be public — direct construction (e.g., `GcContent(50.0)`) MUST be a compile error. The type SHALL reside in the `bio.domain.analysis` package.

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
- **WHEN** source code `bio.domain.analysis.GcContent(50.0)` is compiled
- **THEN** the compiler rejects the expression
