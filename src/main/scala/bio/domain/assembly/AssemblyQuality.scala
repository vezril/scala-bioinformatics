package bio.domain.assembly

/** The result of assessing an assembly's contiguity for the Rosalind ASMQ
  * problem — the N50 and N75 statistics (see
  * [[bio.algorithms.assembly.AssemblyStatistics.assess]]).
  *
  * `format` renders the pair as two space-separated integers, matching Rosalind's
  * output.
  */
final case class AssemblyQuality(n50: Int, n75: Int) {
  def format: String = s"$n50 $n75"
}
