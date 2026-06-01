package bio.algorithms.graph

import bio.domain.graph.{NewickTree, QuartetDistanceProblem}

import scala.collection.mutable

/** Computes the quartet distance between two unrooted binary trees on the same
  * taxa (Rosalind QRTD — "Quartet Distance").
  *
  * The quartet distance is `dq(T1, T2) = q(T1) + q(T2) − 2·q(T1, T2)`. Because both
  * inputs are fully resolved unrooted binary trees, every 4-taxon subset is resolved
  * in each tree, so `q(T1) = q(T2) = C(n, 4)` and `dq = 2·(C(n, 4) − shared)`, where
  * `shared` is the number of 4-subsets whose induced quartet topology agrees between
  * the two trees.
  *
  * '''Counting shared quartets without enumerating all `C(n, 4)` subsets.'''
  * A resolved quartet has topology `ab|cd` exactly when, along the tree path between
  * leaves `a` and `b`, both `c` and `d` branch off at the '''same''' internal node
  * (their off-path subtree). Equivalently, for the leaf pair `{a, b}`, every other
  * leaf `x` is labelled by its ''median'' node `median(a, b, x)` — the unique node
  * where the path to `x` meets the `a`–`b` path — and `{c, d}` forms an `ab|cd`
  * quartet iff `c` and `d` carry the same median label.
  *
  * The median of three leaves in a rooted tree is the deepest of their three pairwise
  * least-common ancestors and is independent of where the tree is rooted, so each
  * tree is rooted at its own parsed Newick root.
  *
  * For a fixed pair `{a, b}`, label every other leaf by its median in `T1` and its
  * median in `T2`; two leaves `c, d` yield a quartet shared by both trees (and
  * attributed to the cherry `{a, b}`) iff they agree on '''both''' labels. Summing
  * `C(cellSize, 2)` over the cells of the common refinement of the two labellings
  * counts these. Each shared quartet is attributed to both of its cherries, so the
  * grand total over all leaf pairs is halved:
  * `shared = ½ · Σ_{ {a,b} } Σ_{cells} C(cellSize, 2)`.
  *
  * '''Complexity.''' `O(n²)` per-tree all-pairs LCA precomputation, then `O(n)` work
  * per leaf pair for `O(n³)` total — tractable for the full Rosalind ceiling
  * (`n ≤ 2000`) in seconds, where the previous `O(n⁴)` subset enumeration was not.
  *
  * The result is returned as a `Long`: the maximum `dq` for `n = 2000` is
  * `2·C(2000, 4) ≈ 1.3×10¹²`, which exceeds `Int` range.
  */
object QuartetDistance {

  def compute(problem: QuartetDistanceProblem): Long = {
    val n = problem.taxa.size
    if (n < 4) return 0L

    // Deep, possibly caterpillar-shaped trees can recurse ~n levels during the
    // build; run on a thread with a generous stack to avoid StackOverflowError.
    var result = 0L
    val worker = new Thread(null, () => result = core(problem), "qrtd-compute", 64L * 1024 * 1024)
    worker.start()
    worker.join()
    result
  }

  private def core(problem: QuartetDistanceProblem): Long = {
    val taxa = problem.taxa
    val n    = taxa.size

    val taxonIndex: Map[String, Int] = taxa.iterator.zipWithIndex.toMap
    val t1                           = RootedTree.build(problem.tree1, taxonIndex, n)
    val t2                           = RootedTree.build(problem.tree2, taxonIndex, n)

    2L * (choose4(n) - countShared(n, t1, t2))
  }

  /** `C(n, 4)` as a `Long` (`0` for `n < 4`). The product of four consecutive
    * integers is divisible by 24, so the division is exact.
    */
  private def choose4(n: Int): Long = {
    val m = n.toLong
    if (m < 4) 0L else m * (m - 1) * (m - 2) * (m - 3) / 24L
  }

  /** Counts quartets resolved identically in both trees (see object Scaladoc). */
  private def countShared(n: Int, t1: RootedTree, t2: RootedTree): Long = {
    val lca1 = t1.lca; val dep1 = t1.depth
    val lca2 = t2.lca; val dep2 = t2.depth

    val labA     = new Array[Int](n)            // T1 median label per collected leaf x
    val labB     = new Array[Int](n)            // T2 median label per collected leaf x
    val head     = Array.fill(t1.numNodes)(-1)  // bucket head, keyed by T1 label
    val nextIdx  = new Array[Int](n)            // intrusive bucket chain
    val touched1 = new Array[Int](n)            // T1 labels used this pair (for cheap reset)
    val cnt2     = new Array[Int](t2.numNodes)  // multiplicity of each T2 label within a bucket
    val touched2 = new Array[Int](n)            // T2 labels used this bucket (for cheap reset)

    var total = 0L
    var a     = 0
    while (a < n) {
      val aRow = a * n
      var b    = a + 1
      while (b < n) {
        val bRow    = b * n
        val medAB1  = lca1(aRow + b); val dAB1 = dep1(medAB1)
        val medAB2  = lca2(aRow + b); val dAB2 = dep2(medAB2)

        // Label every other leaf x by its median in T1 and T2.
        var m = 0
        var x = 0
        while (x < n) {
          if (x != a && x != b) {
            var med1 = medAB1; var d1 = dAB1
            val ax1 = lca1(aRow + x); val dax1 = dep1(ax1); if (dax1 > d1) { med1 = ax1; d1 = dax1 }
            val bx1 = lca1(bRow + x); val dbx1 = dep1(bx1); if (dbx1 > d1) { med1 = bx1; d1 = dbx1 }

            var med2 = medAB2; var d2 = dAB2
            val ax2 = lca2(aRow + x); val dax2 = dep2(ax2); if (dax2 > d2) { med2 = ax2; d2 = dax2 }
            val bx2 = lca2(bRow + x); val dbx2 = dep2(bx2); if (dbx2 > d2) { med2 = bx2; d2 = dbx2 }

            labA(m) = med1; labB(m) = med2
            m += 1
          }
          x += 1
        }

        // Bucket by T1 label.
        var used1 = 0
        var i     = 0
        while (i < m) {
          val g = labA(i)
          if (head(g) == -1) { touched1(used1) = g; used1 += 1 }
          nextIdx(i) = head(g)
          head(g) = i
          i += 1
        }

        // Within each T1 bucket, sum C(count, 2) over T2 labels.
        var pairSum = 0L
        var ti      = 0
        while (ti < used1) {
          val g     = touched1(ti)
          var used2 = 0
          var idx   = head(g)
          while (idx != -1) {
            val h = labB(idx)
            val c = cnt2(h)
            if (c == 0) { touched2(used2) = h; used2 += 1 }
            cnt2(h) = c + 1
            idx = nextIdx(idx)
          }
          var tj = 0
          while (tj < used2) {
            val h = touched2(tj)
            val c = cnt2(h).toLong
            pairSum += c * (c - 1) / 2
            cnt2(h) = 0
            tj += 1
          }
          head(g) = -1
          ti += 1
        }

        total += pairSum
        b += 1
      }
      a += 1
    }

    total / 2
  }

  /** A tree rooted at its parsed Newick root, exposing node depths and an all-pairs
    * least-common-ancestor table over leaves (keyed by taxon index).
    *
    * @param numNodes total node count (ids are `0 until numNodes`)
    * @param depth    `depth(id)` is the node's distance from the root
    * @param lca      flattened `n × n` table: `lca(i * n + j)` is the node id of the
    *                 LCA of the leaves at taxon indices `i` and `j` (`i != j`)
    */
  private final class RootedTree(val numNodes: Int, val depth: Array[Int], val lca: Array[Int])

  private object RootedTree {

    def build(tree: NewickTree, taxonIndex: Map[String, Int], n: Int): RootedTree = {
      val depthBuf = mutable.ArrayBuffer.empty[Int]
      val lca      = new Array[Int](n * n)
      var nextId   = 0

      // Returns this node's id and the taxon indices of leaves in its subtree,
      // filling `lca` for every leaf pair whose LCA is this node.
      def dfs(node: NewickTree, d: Int): (Int, Array[Int]) = {
        val id = nextId
        nextId += 1
        depthBuf += d // depthBuf(id) == d, since ids are assigned in append order

        if (node.children.isEmpty) {
          (id, Array(taxonIndex(node.label.get)))
        } else {
          val lists = node.children.map(c => dfs(c, d + 1)._2)

          var ii = 0
          while (ii < lists.length) {
            var jj = ii + 1
            while (jj < lists.length) {
              val li = lists(ii); val lj = lists(jj)
              var p = 0
              while (p < li.length) {
                val lp = li(p)
                var q  = 0
                while (q < lj.length) {
                  val lq = lj(q)
                  lca(lp * n + lq) = id
                  lca(lq * n + lp) = id
                  q += 1
                }
                p += 1
              }
              jj += 1
            }
            ii += 1
          }

          val merged = new Array[Int](lists.foldLeft(0)(_ + _.length))
          var off    = 0
          lists.foreach { l => System.arraycopy(l, 0, merged, off, l.length); off += l.length }
          (id, merged)
        }
      }

      dfs(tree, 0)
      new RootedTree(nextId, depthBuf.toArray, lca)
    }
  }
}
