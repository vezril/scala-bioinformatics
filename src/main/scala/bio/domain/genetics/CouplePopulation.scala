package bio.domain.genetics

/** A population of couples grouped by their genotype pairing.
  *
  * Each of the six count fields gives the number of couples with that pairing under the
  * Rosalind input ordering:
  *
  *   1. `homDomHomDom` — AA × AA
  *   2. `homDomHet`    — AA × Aa
  *   3. `homDomHomRec` — AA × aa
  *   4. `hetHet`       — Aa × Aa
  *   5. `hetHomRec`    — Aa × aa
  *   6. `homRecHomRec` — aa × aa
  *
  * Constructable only via [[CouplePopulation.from]] which enforces `0 <= each <= 20000`
  * for every count. Implemented as `sealed abstract case class` so the synthesized
  * `apply` and `copy` cannot leak around the smart constructor.
  */
sealed abstract case class CouplePopulation(
    homDomHomDom: Int,
    homDomHet: Int,
    homDomHomRec: Int,
    hetHet: Int,
    hetHomRec: Int,
    homRecHomRec: Int
)

object CouplePopulation {
  private val MaxCount: Int = 20000

  /** Build a `CouplePopulation` from six counts given in Rosalind input order. Returns
    * the first failure encountered if any count is out of range; otherwise `Right` of
    * the validated population.
    */
  def from(
      c1: Int,
      c2: Int,
      c3: Int,
      c4: Int,
      c5: Int,
      c6: Int
  ): Either[CouplePopulationError, CouplePopulation] = {
    val indexed: Vector[(Int, Int)] = Vector(c1, c2, c3, c4, c5, c6).zipWithIndex.map {
      case (v, i) => (i + 1, v)
    }
    indexed.collectFirst {
      case (i, v) if v < 0        => CouplePopulationError.NegativeCount(i, v)
      case (i, v) if v > MaxCount => CouplePopulationError.ExceedsMaxCount(i, v)
    } match {
      case Some(err) => Left(err)
      case None      => Right(new CouplePopulation(c1, c2, c3, c4, c5, c6) {})
    }
  }
}
