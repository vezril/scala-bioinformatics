package bio.domain.graph

import scala.collection.immutable.BitSet

/** A split — the bipartition of a tree's taxa into two sides induced by removing
  * one edge (Rosalind SPTD). Used by [[bio.algorithms.graph.SplitDistance.compute]].
  *
  * A split is invariant under swapping its two sides (`A | B = B | A`). To make
  * equal splits compare equal — so the splits of two trees can be intersected
  * with ordinary `Set` semantics — every instance is stored in a canonical form:
  * `side` is whichever side does **not** contain the reference taxon index `0`.
  * Because index `0` lies in exactly one side of any split, this orients every
  * split uniquely, and (taxa being indexed identically across both trees) equal
  * canonical sides denote the same split.
  *
  * Construct via the total smart constructor [[Split.of]]; the synthesized
  * `apply`/`copy` are not public, so the canonical invariant always holds.
  */
sealed abstract case class Split(side: BitSet)

object Split {

  /** Builds a split from its two sides, canonicalising the result so the stored
    * `side` is the one without the reference index `0`. Orientation of the two
    * arguments is irrelevant to the resulting value.
    */
  def of(sideA: BitSet, sideB: BitSet): Split =
    if (sideA.contains(0)) new Split(sideB) {} else new Split(sideA) {}
}
