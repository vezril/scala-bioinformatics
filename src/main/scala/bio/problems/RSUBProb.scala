package bio.problems

import bio.algorithms.analysis.ReversingSubstitutions
import bio.domain.analysis.{
  NamedSequence,
  ReversingSubstitution,
  ReversingSubstitutionsProblem
}
import bio.parsing.NewickParser
import cats.effect.IO

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, Paths}

/** Rosalind RSUB — "Identifying Reversing Substitutions".
  *
  * Input file format:
  *   - Line 1: a Newick-format rooted binary tree terminated by `;`, with
  *     every node (leaves and internals) labeled.
  *   - Following lines: a FASTA-style alignment of *all* node sequences over
  *     the alphabet `{A, C, G, T}` (no gaps). Each record is a `>label`
  *     header followed by one or more sequence lines (concatenated).
  *
  * Output: one line per reversing substitution, in the format
  * `firstChangeSpecies reversionSpecies position originalSymbol->substitutedSymbol->revertedSymbol`.
  */
object RSUBProb {

  private val InputPath: Path = Paths.get(
    "/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/rsub_data.txt"
  )

  def solve(): IO[Unit] =
    for {
      raw <- IO.blocking(new String(Files.readAllBytes(InputPath), StandardCharsets.UTF_8))
      _ <- run(raw) match {
        case Left(err) =>
          IO.println(s"RSUB error: $err")
        case Right(reversions) =>
          if (reversions.isEmpty) IO.println("(no reversing substitutions)")
          else
            reversions.foldLeft(IO.unit) { (acc, rs) =>
              acc *> IO.println(formatReversion(rs))
            }
      }
    } yield ()

  /** Parses the raw file content, builds the problem, and runs the
    * algorithm. Short-circuits on the first error with a human-readable
    * message. Exposed `private[problems]` for direct unit-style testing.
    */
  private[problems] def run(raw: String): Either[String, Vector[ReversingSubstitution]] = {
    val (newickLine, fastaBody) = splitInput(raw)
    for {
      tree      <- NewickParser.parse(newickLine.trim).left.map(e => s"parse error: $e")
      alignment <- parseAlignment(fastaBody)
      problem   <- ReversingSubstitutionsProblem.from(tree, alignment).left.map(_.toString)
    } yield ReversingSubstitutions.findAll(problem)
  }

  /** Format one reversion as Rosalind's expected line:
    * `firstChangeSpecies reversionSpecies position O->S->R`.
    */
  private[problems] def formatReversion(rs: ReversingSubstitution): String =
    s"${rs.firstChangeSpecies} ${rs.reversionSpecies} ${rs.position} " +
      s"${rs.originalSymbol}->${rs.substitutedSymbol}->${rs.revertedSymbol}"

  /** Split the raw input at the first `>` character into (Newick line,
    * remaining FASTA body). If there is no `>`, the whole input is treated
    * as the Newick line and the body is empty.
    */
  private def splitInput(raw: String): (String, String) = {
    val idx = raw.indexOf('>')
    if (idx < 0) (raw, "")
    else (raw.substring(0, idx), raw.substring(idx))
  }

  /** Parse a FASTA-style body into ordered `NamedSequence` rows. Alphabet
    * validation is the `ReversingSubstitutionsProblem` smart constructor's
    * responsibility.
    */
  private def parseAlignment(body: String): Either[String, Vector[NamedSequence]] = {
    val out      = Vector.newBuilder[NamedSequence]
    var curLabel = Option.empty[String]
    var curSeq   = new StringBuilder

    def flush(): Unit = curLabel.foreach { lbl =>
      out += NamedSequence(lbl, curSeq.toString)
      curSeq = new StringBuilder
    }

    val lines = body.linesIterator
    while (lines.hasNext) {
      val raw     = lines.next()
      val trimmed = raw.trim
      if (trimmed.isEmpty) {
        // skip blank line
      } else if (trimmed.startsWith(">")) {
        val id = trimmed.drop(1).trim
        if (id.isEmpty) return Left("empty FASTA id")
        flush()
        curLabel = Some(id)
      } else {
        if (curLabel.isEmpty) return Left("sequence line without preceding header")
        curSeq.append(trimmed)
      }
    }
    flush()
    Right(out.result())
  }
}
