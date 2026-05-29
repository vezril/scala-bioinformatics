package bio.problems

import bio.algorithms.analysis.AlignmentBasedPhylogeny
import bio.domain.analysis.{AlignmentBasedPhylogenyProblem, NamedSequence}
import bio.parsing.NewickParser
import cats.effect.IO

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, Paths}

/** Rosalind ALPH — "Alignment-Based Phylogeny".
  *
  * Input file format:
  *   - Line 1: a Newick-format rooted binary tree terminated by `;`.
  *   - Following lines: a FASTA-style alignment of leaf sequences over the
  *     alphabet `{A, C, G, T, -}`. Each record is a `>label` header line
  *     followed by one or more sequence lines (concatenated).
  *
  * Output: the integer total parsimony distance on a line by itself, followed
  * by one FASTA record per internal node (`>label\nsequence\n`), in pre-order
  * tree traversal.
  *
  * Note: this runner uses an in-place FASTA-style parser rather than the
  * shared `bio.parsing.FastaParser` because the alignment includes the `-`
  * gap symbol, which `FastaParser` (backed by `DnaString.from`) does not
  * accept.
  */
object ALPHProb {

  private val InputPath: Path = Paths.get(
    "/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/alph_data.txt"
  )

  def solve(): IO[Unit] =
    for {
      raw <- IO.blocking(new String(Files.readAllBytes(InputPath), StandardCharsets.UTF_8))
      _ <- run(raw) match {
        case Left(err)    => IO.println(s"ALPH error: $err")
        case Right(result) =>
          IO.println(result.totalDistance) *>
            result.internalAssignments.foldLeft(IO.unit) { (acc, ns) =>
              acc *> IO.println(s">${ns.label}") *> IO.println(ns.sequence)
            }
      }
    } yield ()

  /** Parses the raw file content, builds the problem, and runs the algorithm.
    * Short-circuits on the first error with a human-readable message.
    * Exposed `private[problems]` for direct unit-style testing without
    * the IO layer.
    */
  private[problems] def run(
      raw: String
  ): Either[String, bio.domain.analysis.AlignmentBasedPhylogeny] = {
    val (newickLine, fastaBody) = splitInput(raw)
    for {
      tree      <- NewickParser.parse(newickLine.trim).left.map(e => s"parse error: $e")
      alignment <- parseAlignment(fastaBody)
      problem   <- AlignmentBasedPhylogenyProblem.from(tree, alignment).left.map(_.toString)
    } yield AlignmentBasedPhylogeny.solve(problem)
  }

  /** Split the raw input at the first `>` character into (Newick line,
    * remaining FASTA body). If there is no `>`, the whole input is treated
    * as the Newick line and the body is empty.
    */
  private def splitInput(raw: String): (String, String) = {
    val idx = raw.indexOf('>')
    if (idx < 0) (raw, "")
    else (raw.substring(0, idx), raw.substring(idx))
  }

  /** Parse a FASTA-style body into ordered `NamedSequence` rows. Allows any
    * character in sequence lines — the alphabet check is the
    * `AlignmentBasedPhylogenyProblem` smart constructor's responsibility.
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
