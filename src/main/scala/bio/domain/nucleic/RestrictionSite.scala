package bio.domain.nucleic

/** A single reverse palindrome located by the Rosalind REVP ("Locating Restriction
  * Sites") problem.
  *
  * @param position the 1-based start position of the palindrome in the DNA string
  * @param length   the length of the palindrome (always even, between 4 and 12)
  */
final case class RestrictionSite(position: Int, length: Int)
