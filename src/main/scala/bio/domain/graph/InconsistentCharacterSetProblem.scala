package bio.domain.graph

/** Validated input bundle for the Rosalind CSET ("Fixing an Inconsistent
  * Character Set") problem — see
  * [[bio.algorithms.graph.FixInconsistentCharacterSet.fix]].
  *
  * Bundles the `0`/`1` character `rows` of a table; every column is a taxon, so
  * all rows share a common width. The smart constructor validates,
  * first-failure-wins, in the order:
  *   1. table non-empty;
  *   2. every row's width equals the first row's width (not ragged);
  *   3. at most 100 taxa (columns);
  *   4. every row contains only `'0'`/`'1'`.
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply`/`copy`
  * cannot leak around the smart constructor — construct via
  * [[InconsistentCharacterSetProblem.from]].
  */
sealed abstract case class InconsistentCharacterSetProblem(rows: Vector[String])

object InconsistentCharacterSetProblem {
  private val MaxTaxa: Int = 100

  def from(
      rows: Vector[String]
  ): Either[InconsistentCharacterSetProblemError, InconsistentCharacterSetProblem] =
    if (rows.isEmpty) Left(InconsistentCharacterSetProblemError.EmptyTable)
    else {
      val width = rows.head.length
      firstRagged(width, rows)
        .orElse(
          if (width > MaxTaxa)
            Some(InconsistentCharacterSetProblemError.ExceedsMaximumTaxa(width, MaxTaxa))
          else None
        )
        .orElse(firstInvalidCharacter(rows))
        .toLeft(new InconsistentCharacterSetProblem(rows) {})
    }

  /** The first row (in order) whose width differs from `width`, if any. */
  private def firstRagged(
      width: Int,
      rows: Vector[String]
  ): Option[InconsistentCharacterSetProblemError] =
    rows.iterator.zipWithIndex
      .find { case (row, _) => row.length != width }
      .map { case (row, i) =>
        InconsistentCharacterSetProblemError.RaggedTable(i, width, row.length)
      }

  /** The first row (in order) containing a non-`0/1` character, if any. */
  private def firstInvalidCharacter(
      rows: Vector[String]
  ): Option[InconsistentCharacterSetProblemError] =
    rows.iterator.zipWithIndex.flatMap { case (row, i) =>
      row
        .find(c => c != '0' && c != '1')
        .map(InconsistentCharacterSetProblemError.InvalidCharacter(i, _))
    }.nextOption()
}
