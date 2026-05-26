package bio.parsing

import bio.domain.nucleic.DnaString

/** Pure parser for FASTA-format DNA input.
  *
  * The parser is line-based and structural: it builds a list of [[FastaRecord]] from a
  * string. File I/O is the caller's responsibility (e.g., a Cats Effect `IO` layer above).
  *
  * Parsing rules:
  *   - Lines beginning with `>` start a new record; the rest of the line (trimmed) is the id
  *   - Subsequent non-empty lines (trimmed) are concatenated to form the DNA sequence
  *   - Blank / whitespace-only lines are ignored
  *   - An empty id (`>` alone, or followed only by whitespace) is [[FastaParseError.EmptyId]]
  *   - Sequence content before any header is [[FastaParseError.MissingHeader]]
  *   - Sequences failing [[DnaString.from]] become [[FastaParseError.InvalidDna]]
  *   - A header with no following sequence yields a record with an empty `DnaString`
  *
  * The parser short-circuits on the first error.
  */
object FastaParser {

  private final case class State(
      currentId: Option[String],
      currentChunks: Vector[String],
      completed: Vector[FastaRecord]
  )

  private val Empty: State = State(None, Vector.empty, Vector.empty)

  def parse(input: String): Either[FastaParseError, List[FastaRecord]] = {
    val folded = input.linesIterator.foldLeft[Either[FastaParseError, State]](Right(Empty)) {
      (acc, rawLine) =>
        acc.flatMap(state => step(state, rawLine))
    }
    folded.flatMap(finalize).map(_.completed.toList)
  }

  private def step(state: State, rawLine: String): Either[FastaParseError, State] = {
    val trimmed = rawLine.trim
    if (trimmed.isEmpty) Right(state)
    else if (trimmed.startsWith(">")) {
      val id = trimmed.drop(1).trim
      if (id.isEmpty) Left(FastaParseError.EmptyId)
      else finalize(state).map(_.copy(currentId = Some(id), currentChunks = Vector.empty))
    } else
      state.currentId match {
        case None    => Left(FastaParseError.MissingHeader)
        case Some(_) => Right(state.copy(currentChunks = state.currentChunks :+ trimmed))
      }
  }

  private def finalize(state: State): Either[FastaParseError, State] =
    state.currentId match {
      case None => Right(state)
      case Some(id) =>
        val assembled = state.currentChunks.mkString
        DnaString.from(assembled) match {
          case Right(dna) =>
            Right(
              state.copy(
                currentId = None,
                currentChunks = Vector.empty,
                completed = state.completed :+ FastaRecord(id, dna)
              )
            )
          case Left(err) => Left(FastaParseError.InvalidDna(id, err))
        }
    }
}
