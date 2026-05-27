## Context

The framework already has a pure `FastaParser.parse(input: String): Either[FastaParseError, List[FastaRecord]]` and a populated `bio.domain.analysis` subdomain (`GcContent`, `HammingError`). Spec 17 introduces two new responsibilities at once:

1. The overlap-graph algorithm itself — computing `O_k`, the directed graph where there is an edge `s → t` iff a length-`k` suffix of `s.dna` equals a length-`k` prefix of `t.dna` and `s ≠ t`.
2. A FASTA file-reading layer — the Rosalind problem statement explicitly says "Provide a way to read a file in FASTA format". The framework has no I/O layer for FASTA today.

These are intentionally split into two separate capabilities (`overlap-graphs` and `fasta-file-reading`) because they are independently useful and have distinct test surfaces.

## Goals / Non-Goals

**Goals:**
- Validated `OverlapLength` value type — domain-typed `k`, smart constructor enforcing `k ≥ 1`.
- `OverlapEdge(from: String, to: String)` ADT — first-class representation of one directed edge in the overlap graph.
- `OverlapGraph.adjacency(records, k): Vector[OverlapEdge]` — total, pure function returning the adjacency list. Self-loops excluded.
- Deterministic edge ordering (input order) so tests can match against a `Vector`.
- `FastaFileReader.read(path): IO[Either[FastaError, List[FastaRecord]]]` — Cats Effect entry point.
- `FastaError` ADT — `IoFailure(cause: Throwable)` for I/O errors, `Parse(error: FastaParseError)` for parse errors.

**Non-Goals:**
- Other graph algorithms (BFS/DFS, connected components, etc.) — out of scope for this spec.
- A generic `Graph[V, E]` type — overkill for a single-use adjacency list. Stay minimal until the second graph algorithm shows up.
- Streaming or chunked FASTA reading — Rosalind inputs are ≤ 10 kbp total; reading the whole file into a `String` is fine.
- Writing FASTA. Out of scope.
- Validating that `id`s are unique across records — Rosalind inputs are well-formed; we treat duplicates as allowed and let `from`/`to` strings collide naturally.
- Treating "no edges" as an error — an empty adjacency list is a valid output.

## Decisions

### Decision 1: `OverlapLength` as a validated value type (not a bare `Int`, not a position in a problem bundle)

```scala
sealed abstract case class OverlapLength(value: Int)
object OverlapLength {
  def from(value: Int): Either[OverlapLengthError, OverlapLength] =
    if (value < 1) Left(OverlapLengthError.NonPositive(value))
    else Right(new OverlapLength(value) {})
}
```

`OverlapGraph.adjacency` takes `(records: Vector[FastaRecord], k: OverlapLength)` — the algorithm is total over its validated inputs. No problem bundle, because the algorithm is parameterized by *two* independent things (a collection and a length) and there is no cross-constraint to enforce (the algorithm handles `k > min sequence length` by emitting no edges — see Decision 4).

**Alternatives considered:**
- *Plain `Int`* — the framework convention is to wrap dimensional/structural numbers (e.g., `OverlapLength.value`) so the algorithm signature is self-documenting and unsafe values can't reach it.
- *`OverlapGraphProblem(records, k)` bundle* — there is no cross-constraint between `records` and `k` to validate, and bundling forces a needless allocation per call. Use a bundle only when validation depends on multiple fields together (as in `PartialPermutationProblem` with `k ≤ n`).

### Decision 2: `OverlapEdge(from: String, to: String)` — strings, not record references

Edges hold the `id` strings, not the source `FastaRecord`s. This matches the Rosalind output format ("Rosalind_0498 Rosalind_2391") and keeps the result independent of any sequence data after the algorithm has run.

```scala
final case class OverlapEdge(from: String, to: String)
```

Public constructor — there is no invariant beyond "two strings", so no smart-constructor ceremony. Following the `FastaRecord` precedent.

### Decision 3: `s ≠ t` is identity-by-`id`, not by sequence

Two records with different ids but identical DNA sequences can produce edges in both directions. The Rosalind spec disambiguates "we demand s ≠ t to prevent directed loops", and the natural notion of identity in the FASTA pipeline is the id. Comparing references (`eq`) would be fragile — it would silently exclude duplicates that the user assembled from separate sources. Comparing by id makes the rule observable and matches how the input is labeled.

If two records share the same id we emit two distinct (but textually identical) edges — we do not deduplicate, because that's a separate concern (input validity, not the graph algorithm).

### Decision 4: Algorithm is a double loop with `startsWith`/`endsWith`, computed against the raw string

```scala
def adjacency(records: Vector[FastaRecord], k: OverlapLength): Vector[OverlapEdge] = {
  val kv = k.value
  for {
    s <- records
    t <- records
    if s.id != t.id
    sStr = s.dna.value
    tStr = t.dna.value
    if sStr.length >= kv && tStr.length >= kv && sStr.endsWith(tStr.take(kv))
  } yield OverlapEdge(s.id, t.id)
}
```

Quadratic in `records.length`. Rosalind caps total input at 10 kbp / 10 records → at most 100 comparisons per call. We are nowhere near needing a suffix-tree or hash-prefix index.

The `sStr.length >= kv && tStr.length >= kv` guard yields no edges for sequences shorter than `k`. This is the intuitive "you can't have an overlap that's longer than either string" behavior and avoids exceptions on the `endsWith`/`take` calls.

**Alternative considered:** Build a `Map[String /* prefix */, Vector[FastaRecord]]` once and look up each suffix in it (O(n × |dna|) prep + O(n) lookups). Not worth the complexity at n ≤ 10.

### Decision 5: Edge ordering is "outer loop = `from`, inner loop = `to`, both in input order"

The Rosalind problem says "You may return edges in any order", so any ordering is correct. We document the deterministic order (input × input, self skipped) so test assertions can compare against an expected `Vector` directly without sorting.

### Decision 6: `FastaFileReader` reads the entire file as UTF-8 and reuses `FastaParser.parse`

```scala
object FastaFileReader {
  def read(path: Path): IO[Either[FastaError, List[FastaRecord]]] =
    IO.blocking(new String(Files.readAllBytes(path), StandardCharsets.UTF_8))
      .attempt
      .map {
        case Left(t)        => Left(FastaError.IoFailure(t))
        case Right(content) => FastaParser.parse(content).left.map(FastaError.Parse)
      }
}
```

- Uses `IO.blocking` because `Files.readAllBytes` is a synchronous JDK call — it will be scheduled on the blocking pool.
- UTF-8 is the universal default for FASTA.
- `.attempt` traps file-not-found / permission / decode exceptions and lifts them into `Left(IoFailure(_))`. We deliberately don't *try to classify* `Throwable` subtypes — every I/O failure becomes one `IoFailure` for downstream simplicity.
- Reusing the pure parser means the I/O layer has zero parsing logic of its own.

**Alternative considered:** `fs2.io.file.Files[IO].readUtf8(path)`. Adds an fs2 dependency that's not on the classpath; not worth pulling in for a single function. Can be revisited if a future spec needs streaming.

### Decision 7: `FastaError` is two cases, not three

```scala
sealed trait FastaError
object FastaError {
  final case class IoFailure(cause: Throwable) extends FastaError
  final case class Parse(error: FastaParseError) extends FastaError
}
```

We *could* flatten `Parse` and re-export each `FastaParseError` case, but that breaks the boundary: `FastaParseError` is the pure-parser vocabulary, `FastaError` is the file-reading vocabulary. Wrapping preserves both ADTs as separately-archived capabilities.

## Risks / Trade-offs

- **Risk:** `OverlapGraph.adjacency` is quadratic; if a future Rosalind variant raises the input cap (e.g., "10 000 records") we'd need a prefix-index rewrite. → **Mitigation:** the signature is stable (`Vector[FastaRecord], OverlapLength`), so an algorithmic swap is a behavior-preserving refactor.
- **Risk:** `IO.blocking` on the default blocking pool will serialize file reads if the caller is reading thousands of FASTA files. → **Mitigation:** out of scope; current callers read one file per program invocation. Document the choice in scaladoc so callers know it's blocking.
- **Trade-off:** treating duplicate ids as allowed means malformed input can produce nonsensical edges. → **Mitigation:** identity-by-id is documented; input-validation belongs upstream (the FASTA parser already rejects empty ids).
- **Trade-off:** `FastaError.IoFailure(cause: Throwable)` exposes the raw exception. → **Mitigation:** this is a debugging convenience — the alternative (a sum of `FileNotFound` / `PermissionDenied` / `DecodeFailure`) over-promises classification we'd just be guessing at.
