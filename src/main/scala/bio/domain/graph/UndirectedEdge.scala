package bio.domain.graph

/** A single undirected edge in a graph whose nodes are labeled by 1-indexed positive
  * integers.
  *
  * Constructable only via [[UndirectedEdge.from]] which enforces:
  *   - `u >= 1` (positive 1-indexed endpoint)
  *   - `v >= 1` (positive 1-indexed endpoint)
  *   - `u != v` (no self-loops)
  *
  * Validation order: `u` lower bound, `v` lower bound, then self-loop. First failure
  * wins.
  *
  * Unlike [[OverlapEdge]] (which holds two arbitrary `String` ids and has no further
  * invariant), `UndirectedEdge` has real per-edge invariants worth enforcing at the
  * value level — hence the smart-constructor pattern. The two types are deliberately
  * not unified under a common supertype: their direction (directed vs. undirected),
  * node identifier (`String` vs. `Int`), and invariants diverge.
  *
  * `UndirectedEdge(1, 2)` and `UndirectedEdge(2, 1)` are distinct values even though
  * they represent the same undirected edge mathematically. Canonicalization (always
  * `u <= v`) would surprise callers who expect input order preserved; downstream
  * algorithms that need canonical edges can wrap this type.
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and `copy`
  * cannot leak around the smart constructor.
  */
sealed abstract case class UndirectedEdge(u: Int, v: Int)

object UndirectedEdge {

  def from(u: Int, v: Int): Either[UndirectedEdgeError, UndirectedEdge] =
    if (u < 1) Left(UndirectedEdgeError.NonPositiveU(u))
    else if (v < 1) Left(UndirectedEdgeError.NonPositiveV(v))
    else if (u == v) Left(UndirectedEdgeError.SelfLoop(u))
    else Right(new UndirectedEdge(u, v) {})
}
