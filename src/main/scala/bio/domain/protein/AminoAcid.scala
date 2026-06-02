package bio.domain.protein

/** The 20 standard amino acids, modeled as a sealed ADT.
  *
  * Each case object exposes its canonical single-letter IUPAC code via [[code]] and
  * its monoisotopic residue mass (in daltons) via [[monoisotopicMass]].
  */
sealed trait AminoAcid {
  def code: Char

  /** Monoisotopic residue mass in daltons, per the standard Rosalind table. */
  def monoisotopicMass: Double
}

object AminoAcid {
  case object F extends AminoAcid { val code: Char = 'F'; val monoisotopicMass: Double = 147.06841 }
  case object L extends AminoAcid { val code: Char = 'L'; val monoisotopicMass: Double = 113.08406 }
  case object I extends AminoAcid { val code: Char = 'I'; val monoisotopicMass: Double = 113.08406 }
  case object V extends AminoAcid { val code: Char = 'V'; val monoisotopicMass: Double = 99.06841 }
  case object S extends AminoAcid { val code: Char = 'S'; val monoisotopicMass: Double = 87.03203 }
  case object P extends AminoAcid { val code: Char = 'P'; val monoisotopicMass: Double = 97.05276 }
  case object T extends AminoAcid { val code: Char = 'T'; val monoisotopicMass: Double = 101.04768 }
  case object A extends AminoAcid { val code: Char = 'A'; val monoisotopicMass: Double = 71.03711 }
  case object Y extends AminoAcid { val code: Char = 'Y'; val monoisotopicMass: Double = 163.06333 }
  case object H extends AminoAcid { val code: Char = 'H'; val monoisotopicMass: Double = 137.05891 }
  case object N extends AminoAcid { val code: Char = 'N'; val monoisotopicMass: Double = 114.04293 }
  case object D extends AminoAcid { val code: Char = 'D'; val monoisotopicMass: Double = 115.02694 }
  case object Q extends AminoAcid { val code: Char = 'Q'; val monoisotopicMass: Double = 128.05858 }
  case object K extends AminoAcid { val code: Char = 'K'; val monoisotopicMass: Double = 128.09496 }
  case object E extends AminoAcid { val code: Char = 'E'; val monoisotopicMass: Double = 129.04259 }
  case object C extends AminoAcid { val code: Char = 'C'; val monoisotopicMass: Double = 103.00919 }
  case object R extends AminoAcid { val code: Char = 'R'; val monoisotopicMass: Double = 156.10111 }
  case object G extends AminoAcid { val code: Char = 'G'; val monoisotopicMass: Double = 57.02146 }
  case object W extends AminoAcid { val code: Char = 'W'; val monoisotopicMass: Double = 186.07931 }
  case object M extends AminoAcid { val code: Char = 'M'; val monoisotopicMass: Double = 131.04049 }

  /** All 20 amino acids, in the canonical order used in this codebase. */
  val all: Vector[AminoAcid] = Vector(F, L, I, V, S, P, T, A, Y, H, N, D, Q, K, E, C, R, G, W, M)

  /** The set of valid single-letter codes (used by [[ProteinString.from]]). */
  val validCodes: Set[Char] = all.map(_.code).toSet

  /** Residue lookup keyed by single-letter code (the 20 codes are distinct). */
  private val byCode: Map[Char, AminoAcid] = all.map(aa => aa.code -> aa).toMap

  /** Lifts a single-letter code to its residue, or `None` if `c` is not one of
    * the 20 valid amino-acid codes.
    */
  def fromChar(c: Char): Option[AminoAcid] = byCode.get(c)

  /** The residue whose monoisotopic mass is nearest to `target`.
    *
    * `Vector.minBy` returns the first minimal element, so ties are broken
    * deterministically by [[all]] order — e.g. the isobaric `I`/`L` pair (both
    * `113.08406`) resolves to `L`, which precedes `I`.
    */
  def closestByMass(target: Double): AminoAcid =
    all.minBy(aa => math.abs(aa.monoisotopicMass - target))
}
