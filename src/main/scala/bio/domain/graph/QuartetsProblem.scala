package bio.domain.graph

/** Validated input bundle for the Rosalind QRT ("Quartets") problem — see
  * [[bio.algorithms.graph.Quartets.compute]].
  *
  * Bundles a partial character table: `taxa` is the vector of distinct taxon
  * names (the table's columns), and `characters` is the vector of character
  * rows, each a string over `{0, 1, x}` whose length equals the taxon count.
  * A character is a partial split — taxa marked `1` on one side, `0` on the
  * other, `x` excluded.
  *
  * The smart constructor enforces, first-failure-wins:
  *   1. `taxa` non-empty, else `EmptyTaxa`;
  *   2. `taxa` distinct, else `DuplicateTaxon` (first repeat);
  *   3. `characters` non-empty, else `EmptyTable`;
  *   4. every row length `== taxa.size`, else `InconsistentWidth` (first row);
  *   5. every row symbol `∈ {'0','1','x'}`, else `InvalidSymbol` (first cell).
  *
  * The Rosalind QRT prompt specifies no numeric bound on the taxon or character
  * count, so the constructor imposes only the structural contract required for
  * correctness (no arbitrary cap that could reject valid input).
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and
  * `copy` cannot leak around the smart constructor — construct via
  * [[QuartetsProblem.from]].
  */
sealed abstract case class QuartetsProblem(
    taxa: Vector[String],
    characters: Vector[String]
)

object QuartetsProblem {

  private val ValidSymbols: Set[Char] = Set('0', '1', 'x')

  def from(
      taxa: Vector[String],
      characters: Vector[String]
  ): Either[QuartetsProblemError, QuartetsProblem] =
    if (taxa.isEmpty)
      Left(QuartetsProblemError.EmptyTaxa)
    else
      firstDuplicate(taxa) match {
        case Some(name) => Left(QuartetsProblemError.DuplicateTaxon(name))
        case None =>
          if (characters.isEmpty)
            Left(QuartetsProblemError.EmptyTable)
          else
            firstWidthMismatch(characters, taxa.size)
              .orElse(firstInvalidSymbol(characters))
              .toLeft(new QuartetsProblem(taxa, characters) {})
      }

  /** First name (in input order) that has already been seen earlier. */
  private def firstDuplicate(taxa: Vector[String]): Option[String] =
    taxa
      .foldLeft((Set.empty[String], Option.empty[String])) {
        case ((seen, found), name) =>
          if (found.isDefined) (seen, found)
          else if (seen.contains(name)) (seen, Some(name))
          else (seen + name, None)
      }
      ._2

  /** First row whose length differs from `expected`. */
  private def firstWidthMismatch(
      characters: Vector[String],
      expected: Int
  ): Option[QuartetsProblemError] =
    characters.iterator.zipWithIndex.collectFirst {
      case (row, i) if row.length != expected =>
        QuartetsProblemError.InconsistentWidth(i, expected, row.length)
    }

  /** First cell containing a symbol outside `{0, 1, x}`. */
  private def firstInvalidSymbol(
      characters: Vector[String]
  ): Option[QuartetsProblemError] =
    characters.iterator.zipWithIndex
      .flatMap { case (row, i) =>
        row.iterator.zipWithIndex.collect {
          case (c, j) if !ValidSymbols.contains(c) =>
            QuartetsProblemError.InvalidSymbol(i, j, c)
        }
      }
      .nextOption()
}
