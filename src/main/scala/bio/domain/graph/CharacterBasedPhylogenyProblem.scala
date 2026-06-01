package bio.domain.graph

/** Validated input bundle for the Rosalind CHBP ("Character-Based Phylogeny")
  * problem — see [[bio.algorithms.graph.CharacterBasedPhylogeny.build]].
  *
  * Bundles an ordered `taxa` list with the `0`/`1` `characters` (rows) describing
  * splits over those taxa: column `j` of every row denotes `taxa(j)`. The smart
  * constructor validates, first-failure-wins, in the order:
  *   1. taxa non-empty;
  *   2. taxa distinct;
  *   3. at most 80 taxa;
  *   4. every row's length equals the taxa count;
  *   5. every row contains only `'0'`/`'1'`;
  *   6. no two characters' splits conflict (the table is consistent).
  *
  * Two splits `S1|S1ᶜ` and `S2|S2ᶜ` conflict iff all four intersections
  * `S1∩S2`, `S1∩S2ᶜ`, `S1ᶜ∩S2`, `S1ᶜ∩S2ᶜ` are non-empty. A consistent table
  * guarantees the reference-oriented sides form a laminar family, so
  * [[bio.algorithms.graph.CharacterBasedPhylogeny.build]] is total over accepted
  * input. Implemented as `sealed abstract case class` so the synthesized
  * `apply`/`copy` cannot leak around the smart constructor — construct via
  * [[CharacterBasedPhylogenyProblem.from]].
  */
sealed abstract case class CharacterBasedPhylogenyProblem(
    taxa: Vector[String],
    characters: Vector[String]
)

object CharacterBasedPhylogenyProblem {
  private val MaxTaxa: Int = 80

  def from(
      taxa: Vector[String],
      rows: Vector[String]
  ): Either[CharacterBasedPhylogenyProblemError, CharacterBasedPhylogenyProblem] =
    if (taxa.isEmpty) Left(CharacterBasedPhylogenyProblemError.EmptyTaxa)
    else
      firstDuplicate(taxa) match {
        case Some(name) => Left(CharacterBasedPhylogenyProblemError.DuplicateTaxon(name))
        case None =>
          if (taxa.size > MaxTaxa)
            Left(CharacterBasedPhylogenyProblemError.ExceedsMaximumTaxa(taxa.size, MaxTaxa))
          else
            firstRowError(taxa.size, rows)
              .orElse(firstConflict(rows))
              .toLeft(new CharacterBasedPhylogenyProblem(taxa, rows) {})
      }

  /** The first taxon name (in order) that repeats an earlier one, if any. */
  private def firstDuplicate(taxa: Vector[String]): Option[String] =
    taxa
      .foldLeft((Set.empty[String], Option.empty[String])) {
        case ((seen, found), name) =>
          if (found.isDefined) (seen, found)
          else if (seen.contains(name)) (seen, Some(name))
          else (seen + name, None)
      }
      ._2

  /** The first row (in order) whose length is wrong, else whose first non-`0/1`
    * character offends — reporting length before content within a row.
    */
  private def firstRowError(
      n: Int,
      rows: Vector[String]
  ): Option[CharacterBasedPhylogenyProblemError] =
    rows.iterator.zipWithIndex.flatMap { case (row, i) =>
      if (row.length != n)
        Some(CharacterBasedPhylogenyProblemError.RowLengthMismatch(i, n, row.length))
      else
        row
          .find(c => c != '0' && c != '1')
          .map(CharacterBasedPhylogenyProblemError.InvalidCharacter(i, _))
    }.nextOption()

  /** The first pair of character rows (in `i < j` order) whose splits conflict,
    * if any. Only evaluated once all rows are well-formed.
    */
  private def firstConflict(
      rows: Vector[String]
  ): Option[CharacterBasedPhylogenyProblemError] = {
    val ones  = rows.map(sideIndices(_, '1'))
    val zeros = rows.map(sideIndices(_, '0'))
    (for {
      i <- rows.indices.iterator
      j <- (i + 1 until rows.size).iterator
      if conflict(ones(i), zeros(i), ones(j), zeros(j))
    } yield CharacterBasedPhylogenyProblemError.ConflictingCharacters(i, j)).nextOption()
  }

  private def sideIndices(row: String, bit: Char): Set[Int] =
    row.indices.filter(row(_) == bit).toSet

  private def conflict(a1: Set[Int], a0: Set[Int], b1: Set[Int], b0: Set[Int]): Boolean =
    a1.intersect(b1).nonEmpty && a1.intersect(b0).nonEmpty &&
      a0.intersect(b1).nonEmpty && a0.intersect(b0).nonEmpty
}
