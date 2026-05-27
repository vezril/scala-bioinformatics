package bio.algorithms.graph

import bio.domain.graph.{OverlapEdge, OverlapLength}
import bio.parsing.FastaRecord

/** Computes the directed overlap graph `O_k` over a collection of labeled DNA sequences.
  *
  * There is an edge `s -> t` whenever the length-`k` suffix of `s.dna.value` equals the
  * length-`k` prefix of `t.dna.value`, with `s.id != t.id` (no self-loops). The result
  * is the adjacency list of that graph.
  *
  * Complexity: O(n²) record-pair comparisons where `n = records.size`. This is fine for
  * the Rosalind problem (n ≤ 10) but would need a prefix-index rewrite for larger
  * inputs. The signature is stable, so a future algorithmic swap is behavior-preserving.
  *
  * Determinism: edges are emitted in the order produced by iterating `records` in the
  * outer loop and `records` in the inner loop (input order × input order), skipping
  * self pairs. Callers can rely on this ordering for test assertions. The Rosalind
  * problem permits any order, so the deterministic choice here is a strict refinement.
  */
object OverlapGraph {

  def adjacency(records: Vector[FastaRecord], k: OverlapLength): Vector[OverlapEdge] = {
    val kv = k.value
    for {
      s <- records
      sStr = s.dna.value
      if sStr.length >= kv
      t <- records
      if s.id != t.id
      tStr = t.dna.value
      if tStr.length >= kv && sStr.endsWith(tStr.take(kv))
    } yield OverlapEdge(s.id, t.id)
  }
}
