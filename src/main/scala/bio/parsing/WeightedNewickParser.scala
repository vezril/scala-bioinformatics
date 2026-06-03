package bio.parsing

import bio.domain.graph.{WeightedChild, WeightedNewickTree}

import scala.annotation.tailrec

/** Pure parser for weighted Newick-format tree strings (see Rosalind NKEW).
  *
  * Grammar:
  * {{{
  *   tree   := node ";"
  *   node   := ( "(" child ("," child)* ")" )? label?
  *   child  := node (":" weight)?
  *   label  := one or more characters from `[^(),;:]`
  *   weight := one or more characters from `[0-9.eE+-]`
  * }}}
  *
  * The edge weight follows each child (the edge to its parent); the root has none.
  * A child without an explicit weight defaults to `0.0`. The public surface is pure and
  * returns `Either`; only local recursion-descent indices are used.
  */
object WeightedNewickParser {

  def parse(input: String): Either[WeightedNewickParseError, WeightedNewickTree] = {
    val trimmed = input.trim
    if (trimmed.isEmpty) Left(WeightedNewickParseError.EmptyInput)
    else {
      val semiIdx = trimmed.indexOf(';')
      if (semiIdx < 0) Left(WeightedNewickParseError.MissingTerminator)
      else {
        val body     = trimmed.substring(0, semiIdx)
        val trailing = trimmed.substring(semiIdx + 1).trim
        if (trailing.nonEmpty) Left(WeightedNewickParseError.TrailingContent(trailing))
        else
          for {
            _    <- checkParenBalance(body)
            tree <- parseBody(body)
          } yield tree
      }
    }
  }

  private def checkParenBalance(body: String): Either[WeightedNewickParseError, Unit] = {
    @tailrec
    def loop(i: Int, depth: Int, firstOpen: Int): Either[WeightedNewickParseError, Unit] =
      if (i == body.length)
        if (depth == 0) Right(()) else Left(WeightedNewickParseError.UnmatchedOpenParen(firstOpen))
      else
        body.charAt(i) match {
          case '(' => loop(i + 1, depth + 1, if (depth == 0) i else firstOpen)
          case ')' =>
            if (depth == 0) Left(WeightedNewickParseError.UnmatchedCloseParen(i))
            else loop(i + 1, depth - 1, firstOpen)
          case _ => loop(i + 1, depth, firstOpen)
        }
    loop(0, 0, -1)
  }

  private def parseBody(body: String): Either[WeightedNewickParseError, WeightedNewickTree] =
    parseNode(body, 0).flatMap { case (tree, end) =>
      if (end == body.length) Right(tree)
      else Left(WeightedNewickParseError.UnexpectedCharacter(body.charAt(end), end))
    }

  private def parseNode(s: String, pos: Int): Either[WeightedNewickParseError, (WeightedNewickTree, Int)] =
    if (pos < s.length && s.charAt(pos) == '(')
      parseChildren(s, pos + 1).map { case (children, afterClose) =>
        val (label, afterLabel) = parseLabel(s, afterClose)
        (WeightedNewickTree(label, children), afterLabel)
      }
    else {
      val (label, afterLabel) = parseLabel(s, pos)
      Right((WeightedNewickTree(label, Vector.empty), afterLabel))
    }

  private def parseChildren(
      s: String,
      pos: Int
  ): Either[WeightedNewickParseError, (Vector[WeightedChild], Int)] = {
    @tailrec
    def loop(
        p: Int,
        acc: Vector[WeightedChild]
    ): Either[WeightedNewickParseError, (Vector[WeightedChild], Int)] =
      parseNode(s, p) match {
        case Left(err) => Left(err)
        case Right((node, p2)) =>
          parseWeight(s, p2) match {
            case Left(err) => Left(err)
            case Right((weight, p3)) =>
              val child = WeightedChild(node, weight)
              if (p3 < s.length && s.charAt(p3) == ',') loop(p3 + 1, acc :+ child)
              else if (p3 < s.length && s.charAt(p3) == ')') Right((acc :+ child, p3 + 1))
              else {
                val ch = if (p3 < s.length) s.charAt(p3) else ' '
                Left(WeightedNewickParseError.UnexpectedCharacter(ch, p3))
              }
          }
      }
    loop(pos, Vector.empty)
  }

  /** A label is a maximal run of characters not in `(`, `,`, `)`, `;`, `:`. */
  private def parseLabel(s: String, pos: Int): (Option[String], Int) = {
    @tailrec
    def loop(i: Int): Int =
      if (i < s.length && !isPunctuation(s.charAt(i))) loop(i + 1) else i
    val end = loop(pos)
    if (end == pos) (None, pos) else (Some(s.substring(pos, end)), end)
  }

  /** An optional `:weight`; absent yields `0.0`, a non-numeric run yields `InvalidWeight`. */
  private def parseWeight(s: String, pos: Int): Either[WeightedNewickParseError, (Double, Int)] =
    if (pos < s.length && s.charAt(pos) == ':') {
      @tailrec
      def loop(i: Int): Int =
        if (i < s.length && isWeightChar(s.charAt(i))) loop(i + 1) else i
      val end  = loop(pos + 1)
      val text = s.substring(pos + 1, end)
      text.toDoubleOption match {
        case Some(w) => Right((w, end))
        case None    => Left(WeightedNewickParseError.InvalidWeight(text, pos + 1))
      }
    } else Right((0.0, pos))

  private def isPunctuation(c: Char): Boolean =
    c == '(' || c == ',' || c == ')' || c == ';' || c == ':'

  private def isWeightChar(c: Char): Boolean =
    (c >= '0' && c <= '9') || c == '.' || c == '-' || c == '+' || c == 'e' || c == 'E'
}
