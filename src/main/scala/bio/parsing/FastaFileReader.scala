package bio.parsing

import cats.effect.IO

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path}

/** Cats Effect–based FASTA file reader.
  *
  * Reads the full file contents as UTF-8 on the Cats Effect *blocking* pool (via
  * [[IO.blocking]]) and then delegates parsing to the pure [[FastaParser.parse]]. All
  * parser logic lives in `FastaParser`; this module only contributes the I/O boundary
  * and the unified [[FastaError]] error type.
  *
  * Failures are surfaced via `Either[FastaError, _]`:
  *   - I/O failures (missing file, permission denied, decode errors) → [[FastaError.IoFailure]]
  *   - Parser failures (malformed FASTA, invalid DNA) → [[FastaError.Parse]]
  *
  * The returned `IO` is referentially transparent — constructing it has no side effect;
  * the file is read only when the `IO` is executed.
  *
  * Sized for the Rosalind use case (≤ 10 kbp). For arbitrarily large files, a streaming
  * variant (e.g. via fs2) would be preferable — out of scope here.
  */
object FastaFileReader {

  def read(path: Path): IO[Either[FastaError, List[FastaRecord]]] =
    IO.blocking(new String(Files.readAllBytes(path), StandardCharsets.UTF_8)).attempt.map {
      case Left(cause) => Left(FastaError.IoFailure(cause))
      case Right(content) =>
        FastaParser.parse(content).left.map(FastaError.Parse)
    }
}
