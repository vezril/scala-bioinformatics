package bio.parsing

import bio.domain.graph.NewickTree

import scala.annotation.tailrec

/** Pure parser for Newick-format tree strings (see Rosalind NWCK).
  *
  * Grammar (no branch lengths, no quoted labels — only what NWCK requires):
  * {{{
  *   tree     := node ";"
  *   node     := ( "(" node ("," node)* ")" )? label?
  *   label    := one or more characters from `[^(),;]`
  * }}}
  *
  * The parser:
  *   1. Trims surrounding whitespace; rejects empty input.
  *   2. Requires a terminating `;`; everything before is the tree body, everything
  *      (non-whitespace) after is [[NewickParseError.TrailingContent]].
  *   3. Validates paren balance in the body before recursive descent, so paren
  *      errors carry the offending position.
  *   4. Recursively parses the body; an internal node is `(` children `)` `label?`,
  *      a leaf is bare `label`.
  *
  * The public surface is pure and returns `Either`; only local mutation appears
  * inside the recursive descent.
  */
object NewickParser {

  def parse(input: String): Either[NewickParseError, NewickTree] = {
    val trimmed = input.trim
    if (trimmed.isEmpty) Left(NewickParseError.EmptyInput)
    else {
      val semiIdx = trimmed.indexOf(';')
      if (semiIdx < 0) Left(NewickParseError.MissingTerminator)
      else {
        val body     = trimmed.substring(0, semiIdx)
        val trailing = trimmed.substring(semiIdx + 1).trim
        if (trailing.nonEmpty) Left(NewickParseError.TrailingContent(trailing))
        else
          for {
            _    <- checkParenBalance(body)
            tree <- parseBody(body)
          } yield tree
      }
    }
  }

  private def checkParenBalance(body: String): Either[NewickParseError, Unit] = {
    @tailrec
    def loop(i: Int, depth: Int, firstOpen: Int): Either[NewickParseError, Unit] =
      if (i == body.length)
        if (depth == 0) Right(())
        else Left(NewickParseError.UnmatchedOpenParen(firstOpen))
      else
        body.charAt(i) match {
          case '(' => loop(i + 1, depth + 1, if (depth == 0) i else firstOpen)
          case ')' =>
            if (depth == 0) Left(NewickParseError.UnmatchedCloseParen(i))
            else loop(i + 1, depth - 1, firstOpen)
          case _ => loop(i + 1, depth, firstOpen)
        }

    loop(0, 0, -1)
  }

  private def parseBody(body: String): Either[NewickParseError, NewickTree] =
    parseNode(body, 0).flatMap { case (tree, end) =>
      if (end == body.length) Right(tree)
      else Left(NewickParseError.UnexpectedCharacter(body.charAt(end), end))
    }

  private def parseNode(s: String, pos: Int): Either[NewickParseError, (NewickTree, Int)] =
    if (pos < s.length && s.charAt(pos) == '(')
      parseChildren(s, pos + 1).map { case (children, afterClose) =>
        val (label, afterLabel) = parseLabel(s, afterClose)
        (NewickTree(label, children), afterLabel)
      }
    else {
      val (label, afterLabel) = parseLabel(s, pos)
      Right((NewickTree(label, Vector.empty), afterLabel))
    }

  private def parseChildren(
      s: String,
      pos: Int
  ): Either[NewickParseError, (Vector[NewickTree], Int)] = {
    @tailrec
    def loop(
        p: Int,
        acc: Vector[NewickTree]
    ): Either[NewickParseError, (Vector[NewickTree], Int)] =
      parseNode(s, p) match {
        case Left(err) => Left(err)
        case Right((node, p2)) =>
          if (p2 < s.length && s.charAt(p2) == ',') loop(p2 + 1, acc :+ node)
          else if (p2 < s.length && s.charAt(p2) == ')') Right((acc :+ node, p2 + 1))
          else {
            val ch = if (p2 < s.length) s.charAt(p2) else ' '
            Left(NewickParseError.UnexpectedCharacter(ch, p2))
          }
      }

    loop(pos, Vector.empty)
  }

  /** A label is a maximal run of characters not in the Newick punctuation set
    * `(`, `,`, `)`, `;`. An empty run yields `None` (unlabelled node).
    */
  private def parseLabel(s: String, pos: Int): (Option[String], Int) = {
    @tailrec
    def loop(i: Int): Int =
      if (i < s.length && !isPunctuation(s.charAt(i))) loop(i + 1) else i

    val end = loop(pos)
    if (end == pos) (None, pos) else (Some(s.substring(pos, end)), end)
  }

  private def isPunctuation(c: Char): Boolean =
    c == '(' || c == ',' || c == ')' || c == ';'
}
