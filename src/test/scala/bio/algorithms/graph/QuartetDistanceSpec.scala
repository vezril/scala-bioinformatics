package bio.algorithms.graph

import bio.domain.graph.{NewickTree, QuartetDistanceProblem}
import bio.parsing.NewickParser
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class QuartetDistanceSpec extends AnyFunSpec with Matchers {

  private def tree(newick: String): NewickTree =
    NewickParser.parse(newick).getOrElse(sys.error(s"invalid Newick fixture: $newick"))

  private def problem(
      taxa: Vector[String],
      t1: String,
      t2: String
  ): QuartetDistanceProblem =
    QuartetDistanceProblem
      .from(taxa, tree(t1), tree(t2))
      .getOrElse(sys.error("invalid QuartetDistanceProblem fixture"))

  describe("QuartetDistance.compute") {
    it("reproduces the canonical Rosalind sample distance of 4") {
      val p = problem(
        Vector("A", "B", "C", "D", "E"),
        "(A,C,((B,D),E));",
        "(C,(B,D),(A,E));"
      )
      QuartetDistance.compute(p) shouldBe 4L
    }

    it("yields zero for two identical trees") {
      val p = problem(
        Vector("A", "B", "C", "D", "E"),
        "(A,C,((B,D),E));",
        "(A,C,((B,D),E));"
      )
      QuartetDistance.compute(p) shouldBe 0L
    }

    it("yields 2 for the smallest differing case (n = 4)") {
      // AB|CD vs AC|BD differ on the single quartet -> dq = 2.
      val p = problem(
        Vector("A", "B", "C", "D"),
        "((A,B),(C,D));",
        "((A,C),(B,D));"
      )
      QuartetDistance.compute(p) shouldBe 2L
    }

    it("is symmetric in its two trees") {
      val forward = problem(
        Vector("A", "B", "C", "D", "E"),
        "(A,C,((B,D),E));",
        "(C,(B,D),(A,E));"
      )
      val reverse = problem(
        Vector("A", "B", "C", "D", "E"),
        "(C,(B,D),(A,E));",
        "(A,C,((B,D),E));"
      )
      QuartetDistance.compute(forward) shouldBe QuartetDistance.compute(reverse)
    }
  }

  describe("QuartetDistance.compute (cross-checked against brute force)") {
    it("agrees with an independent O(n^4) reference on random tree pairs") {
      val rng = new scala.util.Random(20260601L)
      for (n <- 5 to 12; _ <- 0 until 20) {
        val labels = (0 until n).map(i => s"t$i").toVector
        val t1     = randomTree(labels, rng)
        val t2     = randomTree(labels, rng)
        val p      = QuartetDistanceProblem.from(labels, t1, t2).toOption.get
        withClue(s"n=$n t1=${render(t1)} t2=${render(t2)}: ") {
          QuartetDistance.compute(p) shouldBe bruteForce(labels, t1, t2)
        }
      }
    }
  }

  /** A random rooted binary tree over `labels` (a valid unrooted-binary encoding). */
  private def randomTree(labels: Vector[String], rng: scala.util.Random): NewickTree =
    if (labels.size == 1) NewickTree(Some(labels.head), Vector.empty)
    else {
      val shuffled = rng.shuffle(labels)
      val cut      = 1 + rng.nextInt(shuffled.size - 1)
      val (l, r)   = shuffled.splitAt(cut)
      NewickTree(None, Vector(randomTree(l, rng), randomTree(r, rng)))
    }

  private def render(t: NewickTree): String =
    if (t.children.isEmpty) t.label.getOrElse("")
    else t.children.map(render).mkString("(", ",", ")")

  /** Reference quartet distance via the four-point condition on the tree metric —
    * an approach independent of the production median/LCA implementation.
    */
  private def bruteForce(labels: Vector[String], t1: NewickTree, t2: NewickTree): Long = {
    val d1 = pairwiseDistances(labels, t1)
    val d2 = pairwiseDistances(labels, t2)
    val n  = labels.size
    var differing = 0L
    for {
      a <- 0 until n; b <- a + 1 until n; c <- b + 1 until n; d <- c + 1 until n
    } if (topology(d1, a, b, c, d) != topology(d2, a, b, c, d)) differing += 1
    2L * differing
  }

  /** The pairing of `{a,b,c,d}` whose endpoints are closest, as a canonical
    * `Set[Set[Int]]` bipartition, chosen by the four-point condition.
    */
  private def topology(
      dist: Array[Array[Int]],
      a: Int,
      b: Int,
      c: Int,
      d: Int
  ): Set[Set[Int]] = {
    val abCd = dist(a)(b) + dist(c)(d)
    val acBd = dist(a)(c) + dist(b)(d)
    val adBc = dist(a)(d) + dist(b)(c)
    val min  = math.min(abCd, math.min(acBd, adBc))
    if (abCd == min) Set(Set(a, b), Set(c, d))
    else if (acBd == min) Set(Set(a, c), Set(b, d))
    else Set(Set(a, d), Set(b, c))
  }

  /** All-pairs unit-weight shortest-path distances between the labelled leaves. */
  private def pairwiseDistances(labels: Vector[String], tree: NewickTree): Array[Array[Int]] = {
    var nextId = 0
    val adj    = scala.collection.mutable.Map.empty[Int, List[Int]]
    val leafId = scala.collection.mutable.Map.empty[String, Int]
    def build(node: NewickTree): Int = {
      val id = nextId; nextId += 1
      adj(id) = Nil
      if (node.children.isEmpty) leafId(node.label.get) = id
      node.children.foreach { c =>
        val cid = build(c)
        adj(id) = cid :: adj(id)
        adj(cid) = id :: adj(cid)
      }
      id
    }
    build(tree)
    val n    = labels.size
    val dist = Array.ofDim[Int](n, n)
    labels.indices.foreach { i =>
      val src     = leafId(labels(i))
      val depth   = scala.collection.mutable.Map(src -> 0)
      val queue   = scala.collection.mutable.Queue(src)
      while (queue.nonEmpty) {
        val u = queue.dequeue()
        adj(u).foreach { v =>
          if (!depth.contains(v)) { depth(v) = depth(u) + 1; queue.enqueue(v) }
        }
      }
      labels.indices.foreach(j => dist(i)(j) = depth(leafId(labels(j))))
    }
    dist
  }
}
