## 1. OverlapLength domain type (TDD)

- [x] 1.1 Write `OverlapLengthErrorSpec` covering both `NonPositive` field-carrying scenarios (value=0, value=-3); run, observe red
- [x] 1.2 Write `OverlapLengthSpec` covering `from` scenarios: accept k=3, accept k=1, reject k=0 (`NonPositive(0)`), reject k=-5 (`NonPositive(-5)`), plus `assertDoesNotCompile` for direct `OverlapLength(3)`; run, observe red
- [x] 1.3 Implement `bio.domain.analysis.OverlapLengthError` sealed trait with `NonPositive(value: Int)` case
- [x] 1.4 Implement `bio.domain.analysis.OverlapLength` as `sealed abstract case class` with private `from` smart constructor; run all tests green
- [x] 1.5 Refactor pass: scaladoc on both types describing the validation rule and the `sealed abstract case class` pattern

## 2. OverlapEdge domain type (TDD)

- [x] 2.1 Write `OverlapEdgeSpec` covering the field-exposure scenario; run, observe red
- [x] 2.2 Implement `bio.domain.analysis.OverlapEdge` as `final case class OverlapEdge(from: String, to: String)`; run all tests green
- [x] 2.3 Refactor pass: scaladoc describing the "ids only, not record refs" decision

## 3. OverlapGraph.adjacency algorithm (TDD)

- [x] 3.1 Write `OverlapGraphSpec` covering all 8 scenarios from the spec: Rosalind sample produces 3 edges in canonical order, empty list → empty, single record → empty, self-loop excluded when own suffix=own prefix, sequence shorter than k → no edges, one record can source multiple edges, edges respect direction (no automatic reverse), k=1 single-character match; run, observe red
- [x] 3.2 Implement `bio.algorithms.analysis.OverlapGraph.adjacency(records: Vector[FastaRecord], k: OverlapLength): Vector[OverlapEdge]` using the for-comprehension from design.md (outer × inner over `records`, skip self by id, guard both lengths ≥ k, use `endsWith(t.take(k))`); run all tests green
- [x] 3.3 Refactor pass: scaladoc describing the O(n²) trade-off and the deterministic ordering guarantee

## 4. FastaError ADT (TDD)

- [x] 4.1 Write `FastaErrorSpec` covering both field-exposure scenarios (`IoFailure` carrying `NoSuchFileException`, `Parse` wrapping `FastaParseError.MissingHeader`); run, observe red
- [x] 4.2 Implement `bio.parsing.FastaError` sealed trait with `IoFailure(cause: Throwable)` and `Parse(error: FastaParseError)` cases; run all tests green

## 5. FastaFileReader (TDD)

- [x] 5.1 Write `FastaFileReaderSpec` (extends `AnyFunSpec with Matchers` + `cats.effect.unsafe.implicits.global`) with a helper that writes a temp file in `IO`, yields it to a body, and deletes it on close; cover all 7 scenarios: well-formed single record, Rosalind sample three records in order, empty file → empty list, non-existent path → `IoFailure` containing `NoSuchFileException`, sequence-before-header → `Parse(MissingHeader)`, invalid DNA char → `Parse(InvalidDna("Rosalind_0001", InvalidCharacter('X')))`, referential transparency (constructing `read(path)` for a non-existent path does not throw before `unsafeRunSync`); run, observe red
- [x] 5.2 Implement `bio.parsing.FastaFileReader.read(path: java.nio.file.Path): IO[Either[FastaError, List[FastaRecord]]]` using `IO.blocking(new String(Files.readAllBytes(path), StandardCharsets.UTF_8)).attempt.map { case Left(t) => Left(IoFailure(t)); case Right(s) => FastaParser.parse(s).left.map(FastaError.Parse) }`; run all tests green
- [x] 5.3 Refactor pass: scaladoc describing the blocking-pool usage, UTF-8 default, and the "delegate to pure parser" pattern

## 6. Whole-suite verification

- [x] 6.1 Run `sbt test` and confirm the existing suite plus all new tests pass with no warnings
- [x] 6.2 Verify no accidental modifications to existing files (`git diff --stat` should show only additions under `bio/domain/analysis/`, `bio/algorithms/analysis/`, `bio/parsing/`, and the new test files)
