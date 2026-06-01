package bio.algorithms.graph

import bio.domain.graph.{Quartet, QuartetsProblem}

/** Infers all quartets from a partial character table (Rosalind QRT —
  * "Quartets").
  *
  * Each character row is a partial split: the taxa marked `'1'` form one side
  * `S₁`, those marked `'0'` the other side `S₀`, and `'x'`-marked taxa are
  * excluded. A quartet `{a,b} | {c,d}` is inferred from that split iff
  * `{a,b} ⊆ S₁` and `{c,d} ⊆ S₀`. So each character contributes the
  * cross-product of the 2-combinations of `S₁` with the 2-combinations of `S₀`.
  *
  * **Algorithm.** For every row, gather `S₁` and `S₀` (preserving taxon order),
  * then emit `Quartet.of(a₁, a₂, b₁, b₂)` for each pair `{a₁,a₂}` from `S₁` and
  * `{b₁,b₂}` from `S₀`. `combinations(2)` is empty when a side has fewer than
  * two taxa, so sparse / `x`-heavy rows contribute nothing. Results across all
  * rows are deduplicated (a quartet inferable from several characters appears
  * once — [[Quartet]]'s canonical form makes equal quartets compare equal) and
  * returned in a deterministic, lexicographically-sorted order.
  *
  * **Complexity.** Per character, `O(|S₁|² · |S₀|²)` quartets — inherent in
  * "return all quartets"; the enumeration is output-optimal.
  */
object Quartets {

  def compute(problem: QuartetsProblem): Vector[Quartet] = {
    val taxa = problem.taxa

    problem.characters
      .flatMap { row =>
        val ones  = taxa.indices.collect { case i if row.charAt(i) == '1' => taxa(i) }
        val zeros = taxa.indices.collect { case i if row.charAt(i) == '0' => taxa(i) }
        for {
          a <- ones.combinations(2)
          b <- zeros.combinations(2)
        } yield Quartet.of(a(0), a(1), b(0), b(1))
      }
      .distinct
      .sortBy(q => (q.pairA._1, q.pairA._2, q.pairB._1, q.pairB._2))
  }
}
