package bio.domain.graph

/** A quartet — a partial split `{a, b} | {c, d}` with exactly two taxa per side
  * (Rosalind QRT). Produced by [[bio.algorithms.graph.Quartets.compute]].
  *
  * A quartet is invariant under swapping the two taxa within a side and under
  * swapping the two sides (`A|B = B|A`). To make equal quartets compare equal —
  * so a collection can be deduplicated by ordinary set/`distinct` semantics —
  * every instance is stored in a canonical form:
  *   - each side's two taxa are sorted lexicographically into a `(String, String)`;
  *   - the two sides are ordered so the lexicographically-smaller pair is `pairA`.
  *
  * Construct via the total smart constructor [[Quartet.of]]; the synthesized
  * `apply`/`copy` are not public, so the canonical invariant always holds and
  * the case-class equality is sound for deduplication.
  */
sealed abstract case class Quartet(pairA: (String, String), pairB: (String, String)) {

  /** Renders as `"{a, b} {c, d}"` in canonical order. */
  def render: String =
    s"{${pairA._1}, ${pairA._2}} {${pairB._1}, ${pairB._2}}"
}

object Quartet {

  /** Builds a quartet from four taxa — two on each side — canonicalising the
    * result. The first two arguments form one side, the last two the other;
    * orientation and within-side order are irrelevant to the resulting value.
    */
  def of(w: String, x: String, y: String, z: String): Quartet = {
    val side1 = sortPair(w, x)
    val side2 = sortPair(y, z)
    val (a, b) = if (lePair(side1, side2)) (side1, side2) else (side2, side1)
    new Quartet(a, b) {}
  }

  private def sortPair(p: String, q: String): (String, String) =
    if (p <= q) (p, q) else (q, p)

  /** Lexicographic `<=` on sorted pairs: compare first elements, then second. */
  private def lePair(p: (String, String), q: (String, String)): Boolean =
    if (p._1 != q._1) p._1 < q._1 else p._2 <= q._2
}
