package bio.domain.protein

sealed trait TranslationError
object TranslationError {
  final case class LengthNotMultipleOfThree(length: Int) extends TranslationError
}
