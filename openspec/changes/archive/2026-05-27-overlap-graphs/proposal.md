## Why

Rosalind problem 17 ("Overlap Graphs") asks us to compute the adjacency list of the directed graph `O_k` over a collection of labeled DNA strings, where there is an edge `s → t` whenever a length-`k` suffix of `s` matches a length-`k` prefix of `t` and `s ≠ t` (no self-loops). This is the framework's first graph-shaped output and the first algorithm that consumes a collection of FASTA records rather than a single sequence. The problem statement also explicitly requires "a way to read a file in FASTA format" — the framework currently only has a pure in-memory parser (`FastaParser.parse`), with no Cats Effect file-reading entry point.

## What Changes

- Add a validated `OverlapLength` value type in `bio.domain.analysis` (smart constructor enforces `k ≥ 1`).
- Add an `OverlapEdge(from: String, to: String)` ADT in `bio.domain.analysis` representing one directed edge in an overlap graph.
- Add the `OverlapGraph.adjacency(records, k): Vector[OverlapEdge]` algorithm in `bio.algorithms.analysis`, computing all `s → t` edges where `suffix(s, k) == prefix(t, k)`, `s ≠ t` (id-based equality, with the stable interpretation defined in design.md).
- Add a `FastaFileReader` capability in `bio.parsing` that reads a FASTA file path via Cats Effect `IO`, returning `IO[Either[FastaError, List[FastaRecord]]]`. A new `sealed trait FastaError` wraps either an `IoFailure(cause: Throwable)` or a `Parse(error: FastaParseError)`.
- No modifications to existing capabilities — the pure `FastaParser.parse` is reused unchanged.

## Capabilities

### New Capabilities
- `overlap-graphs`: The `OverlapLength` validated value type, the `OverlapEdge` ADT, and the `OverlapGraph.adjacency` algorithm computing the directed overlap-graph adjacency list for a collection of FASTA records at a given overlap length `k`.
- `fasta-file-reading`: The `FastaFileReader.read(path): IO[Either[FastaError, List[FastaRecord]]]` capability and the `FastaError` ADT (`IoFailure`, `Parse`), providing Cats Effect–based FASTA file ingestion on top of the pure `FastaParser`.

### Modified Capabilities
<!-- none — `fasta-parsing` is reused as-is; no requirement changes -->

## Impact

- New packages populated: `bio.domain.analysis` (adds `OverlapLength`, `OverlapEdge`), `bio.algorithms.analysis` (adds `OverlapGraph`), `bio.parsing` (adds `FastaFileReader`, `FastaError`).
- New runtime dependency surface: `cats.effect.IO` for file reading (already on the classpath — no new SBT dependencies).
- New tests across three suites (`OverlapLengthSpec`, `OverlapEdgeSpec`, `OverlapGraphSpec`, `FastaFileReaderSpec`); the file-reading tests use temp files created in `IO`.
- No breaking changes — purely additive.
