package bio.problems

import bio.algorithms.graph.CharacterTable
import bio.domain.graph.CharacterTableProblem
import bio.parsing.NewickParser
import cats.effect.IO

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, Paths}

/** Rosalind CSTR — "Creating a Character Table".
  *
  * Input file format: a single line containing an unrooted binary tree in
  * Newick format, terminated by `;`. Surrounding whitespace is tolerated.
  *
  * Output: the nontrivial character-table rows, one per line, in
  * lexicographic order (as produced by [[CharacterTable.compute]]).
  *
  * For the bundled sample `(dog,((elephant,mouse),robot),cat);` the printed
  * output is:
  * {{{
  *   00110
  *   00111
  * }}}
  */
object CTBLProb {

  private val InputPath: Path = Paths.get(
    "/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/ctbl_prob.txt"
  )

  def solve(): IO[Unit] =
    for {
      raw <- IO.blocking(new String(Files.readAllBytes(InputPath), StandardCharsets.UTF_8))
      _ <- run(raw) match {
        case Left(err)   => IO.println(s"CTBL error: $err")
        case Right(rows) =>
          if (rows.isEmpty) IO.println("(no nontrivial splits)")
          else IO.println(rows.mkString("\n"))
      }
    } yield ()

  /** Parses one Newick string, builds the `CharacterTableProblem`, and computes
    * the character table. Short-circuits on the first error, returning a
    * human-readable message on the `Left`. Exposed `private[problems]` for
    * direct unit-style testing without going through the IO layer.
    */
  private[problems] def run(raw: String): Either[String, Vector[String]] =
    for {
      tree    <- NewickParser.parse(raw.trim).left.map(e => s"parse error: $e")
      problem <- CharacterTableProblem
        .from(tree)
        .left
        .map(e => s"problem-construction error: $e")
    } yield CharacterTable.compute(problem)
}
