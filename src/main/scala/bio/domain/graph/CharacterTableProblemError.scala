package bio.domain.graph

/** Construction failures for [[CharacterTableProblem]]. */
sealed trait CharacterTableProblemError
object CharacterTableProblemError {

  /** The tree's labelled-leaf count exceeded the Rosalind CSTR cap of 200. */
  final case class TooManyTaxa(count: Int, max: Int) extends CharacterTableProblemError
}
