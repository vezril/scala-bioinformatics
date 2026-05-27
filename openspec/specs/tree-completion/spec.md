## Purpose
Define the tree-completion capability: the `UndirectedEdge` validated edge type (1-indexed integer endpoints, no self-loops) and its error ADT, the `TreeCompletionProblem` validated parameter bundle (n ≤ 1000 with endpoints in `[1, n]`) and its error ADT, and the `TreeCompletion.edgesToAdd(problem): Int` algorithm computing the minimum number of edges to add to an acyclic undirected graph to make it a tree. Serves the Rosalind "Completing a Tree" (TREE) problem and seeds the framework's second graph-shaped algorithm alongside the relocated overlap-graphs capability in the `bio.{domain,algorithms}.graph` subdomain.

## Requirements

### Requirement: UndirectedEdgeError is a sealed ADT of UndirectedEdge construction failures
The system SHALL provide a `sealed trait UndirectedEdgeError` with cases `final case class SelfLoop(node: Int)` (the two endpoints were equal), `final case class NonPositiveU(value: Int)` (the `u` endpoint was less than 1), and `final case class NonPositiveV(value: Int)` (the `v` endpoint was less than 1). The type SHALL reside in the `bio.domain.graph` package.

#### Scenario: SelfLoop carries the offending node
- **WHEN** `UndirectedEdgeError.SelfLoop(5)` is constructed
- **THEN** the value's `node` field equals `5`

#### Scenario: NonPositiveU carries the offending value
- **WHEN** `UndirectedEdgeError.NonPositiveU(0)` is constructed
- **THEN** the value's `value` field equals `0`

#### Scenario: NonPositiveV carries the offending value
- **WHEN** `UndirectedEdgeError.NonPositiveV(-1)` is constructed
- **THEN** the value's `value` field equals `-1`

### Requirement: UndirectedEdge is a validated unordered-pair edge with 1-indexed integer endpoints
The system SHALL provide a `sealed abstract case class UndirectedEdge(u: Int, v: Int)`. Construction SHALL be possible only through `UndirectedEdge.from(u: Int, v: Int): Either[UndirectedEdgeError, UndirectedEdge]` enforcing `u >= 1`, `v >= 1`, and `u != v`. Validation SHALL apply in the order: `u` lower bound, `v` lower bound, then self-loop. The synthesized `apply` and `copy` SHALL NOT be public — direct construction `UndirectedEdge(1, 2)` MUST be a compile error. The type SHALL reside in the `bio.domain.graph` package.

#### Scenario: A well-formed edge (1, 2) is accepted
- **WHEN** `UndirectedEdge.from(1, 2)` is called
- **THEN** the result is `Right(<UndirectedEdge with u=1, v=2>)`

#### Scenario: An edge with u = 0 is rejected
- **WHEN** `UndirectedEdge.from(0, 5)` is called
- **THEN** the result is `Left(UndirectedEdgeError.NonPositiveU(0))`

#### Scenario: An edge with negative v is rejected
- **WHEN** `UndirectedEdge.from(5, -3)` is called
- **THEN** the result is `Left(UndirectedEdgeError.NonPositiveV(-3))`

#### Scenario: A self-loop is rejected
- **WHEN** `UndirectedEdge.from(7, 7)` is called
- **THEN** the result is `Left(UndirectedEdgeError.SelfLoop(7))`

#### Scenario: Validation order — u lower bound is checked before v lower bound
- **WHEN** `UndirectedEdge.from(0, -1)` is called (both invalid)
- **THEN** the result is `Left(UndirectedEdgeError.NonPositiveU(0))`

#### Scenario: Validation order — v lower bound is checked before self-loop
- **WHEN** `UndirectedEdge.from(7, 0)` is called (`v = 0` and would otherwise also be checked against `u`)
- **THEN** the result is `Left(UndirectedEdgeError.NonPositiveV(0))`

#### Scenario: Direct apply does not compile
- **WHEN** source code `bio.domain.graph.UndirectedEdge(1, 2)` is compiled
- **THEN** the compiler rejects the expression

### Requirement: TreeCompletionProblemError is a sealed ADT of TreeCompletionProblem construction failures
The system SHALL provide a `sealed trait TreeCompletionProblemError` with cases `final case class NonPositiveN(value: Int)`, `final case class NExceedsMaximum(value: Int, max: Int)`, and `final case class EdgeEndpointOutOfRange(edge: UndirectedEdge, n: Int)`. The type SHALL reside in the `bio.domain.graph` package.

#### Scenario: NonPositiveN carries the offending value
- **WHEN** `TreeCompletionProblemError.NonPositiveN(0)` is constructed
- **THEN** the value's `value` field equals `0`

#### Scenario: NExceedsMaximum carries the offending value and the maximum
- **WHEN** `TreeCompletionProblemError.NExceedsMaximum(1001, 1000)` is constructed
- **THEN** the value's `value` field equals `1001` and `max` equals `1000`

#### Scenario: EdgeEndpointOutOfRange carries the offending edge and n
- **WHEN** `TreeCompletionProblemError.EdgeEndpointOutOfRange(UndirectedEdge.from(3, 11).toOption.get, 10)` is constructed
- **THEN** the value's `edge` field is the supplied edge and `n` equals `10`

### Requirement: TreeCompletionProblem is a validated parameter bundle for the tree-completion algorithm
The system SHALL provide a `sealed abstract case class TreeCompletionProblem(n: Int, edges: Vector[UndirectedEdge])`. Construction SHALL be possible only through `TreeCompletionProblem.from(n: Int, edges: Vector[UndirectedEdge]): Either[TreeCompletionProblemError, TreeCompletionProblem]` enforcing `1 <= n <= 1000` and, for every edge, `u <= n && v <= n`. Validation SHALL apply in the order: `n` lower bound, `n` upper bound, then per-edge endpoint range scan (first offending edge wins, in input order). The synthesized `apply` and `copy` SHALL NOT be public — direct construction `TreeCompletionProblem(10, Vector.empty)` MUST be a compile error. The type SHALL reside in the `bio.domain.graph` package.

#### Scenario: Rosalind sample is accepted
- **WHEN** `TreeCompletionProblem.from(10, edges)` is called with the six Rosalind sample edges `[(1,2), (2,8), (4,10), (5,9), (6,10), (7,9)]`
- **THEN** the result is `Right(<TreeCompletionProblem with n=10 and 6 edges>)`

#### Scenario: Minimum valid input (n=1, no edges) is accepted
- **WHEN** `TreeCompletionProblem.from(1, Vector.empty)` is called
- **THEN** the result is `Right(<TreeCompletionProblem with n=1 and 0 edges>)`

#### Scenario: Upper-bound n (1000) is accepted
- **WHEN** `TreeCompletionProblem.from(1000, Vector.empty)` is called
- **THEN** the result is `Right(<TreeCompletionProblem with n=1000>)`

#### Scenario: n = 0 is rejected as NonPositiveN
- **WHEN** `TreeCompletionProblem.from(0, Vector.empty)` is called
- **THEN** the result is `Left(TreeCompletionProblemError.NonPositiveN(0))`

#### Scenario: n exceeding 1000 is rejected as NExceedsMaximum
- **WHEN** `TreeCompletionProblem.from(1001, Vector.empty)` is called
- **THEN** the result is `Left(TreeCompletionProblemError.NExceedsMaximum(1001, 1000))`

#### Scenario: An edge with u > n is rejected as EdgeEndpointOutOfRange
- **WHEN** `TreeCompletionProblem.from(5, Vector(UndirectedEdge.from(6, 1).toOption.get))` is called
- **THEN** the result is `Left(TreeCompletionProblemError.EdgeEndpointOutOfRange(<edge with u=6, v=1>, 5))`

#### Scenario: An edge with v > n is rejected as EdgeEndpointOutOfRange
- **WHEN** `TreeCompletionProblem.from(5, Vector(UndirectedEdge.from(1, 6).toOption.get))` is called
- **THEN** the result is `Left(TreeCompletionProblemError.EdgeEndpointOutOfRange(<edge with u=1, v=6>, 5))`

#### Scenario: First offending edge in input order wins
- **WHEN** `TreeCompletionProblem.from(5, Vector(<edge (1,2)>, <edge (6,3)>, <edge (1,7)>))` is called
- **THEN** the result is `Left(TreeCompletionProblemError.EdgeEndpointOutOfRange(<edge (6,3)>, 5))`

#### Scenario: Validation order — n lower bound before n upper bound
- **WHEN** `TreeCompletionProblem.from(0, Vector(<some valid edge>))` is called
- **THEN** the result is `Left(TreeCompletionProblemError.NonPositiveN(0))`

#### Scenario: Validation order — n upper bound before edge endpoint scan
- **WHEN** `TreeCompletionProblem.from(1001, Vector(<edge (1,2)>))` is called
- **THEN** the result is `Left(TreeCompletionProblemError.NExceedsMaximum(1001, 1000))`

#### Scenario: Direct apply does not compile
- **WHEN** source code `bio.domain.graph.TreeCompletionProblem(10, Vector.empty)` is compiled
- **THEN** the compiler rejects the expression

### Requirement: TreeCompletion.edgesToAdd computes the minimum edges to add to make the input a tree
The system SHALL provide `TreeCompletion.edgesToAdd(problem: TreeCompletionProblem): Int` returning `problem.n - problem.edges.size - 1`. The algorithm SHALL reside in the `bio.algorithms.graph` package. The function SHALL be total — every valid `TreeCompletionProblem` produces a defined `Int`. The function SHALL trust the precondition that the input graph is acyclic (does not perform cycle detection).

#### Scenario: Rosalind sample (n=10, 6 edges) yields 3
- **WHEN** `TreeCompletion.edgesToAdd(problem)` is called with `problem = TreeCompletionProblem.from(10, <the six Rosalind sample edges>).toOption.get`
- **THEN** the result is `3` (= 10 − 6 − 1)

#### Scenario: A single isolated node (n=1, no edges) needs 0 edges
- **WHEN** `TreeCompletion.edgesToAdd(problem)` is called with `problem = TreeCompletionProblem.from(1, Vector.empty).toOption.get`
- **THEN** the result is `0`

#### Scenario: A graph that is already a tree (n=5, 4 edges) needs 0 edges
- **WHEN** `TreeCompletion.edgesToAdd(problem)` is called with `problem = TreeCompletionProblem.from(5, Vector(<edges (1,2), (2,3), (3,4), (4,5)>)).toOption.get`
- **THEN** the result is `0`

#### Scenario: Fully disconnected graph (n=10, no edges) needs n−1 edges
- **WHEN** `TreeCompletion.edgesToAdd(problem)` is called with `problem = TreeCompletionProblem.from(10, Vector.empty).toOption.get`
- **THEN** the result is `9`

#### Scenario: Two disjoint trees need 1 edge to merge
- **WHEN** `TreeCompletion.edgesToAdd(problem)` is called with `problem = TreeCompletionProblem.from(6, Vector(<edges (1,2), (2,3), (4,5), (5,6)>)).toOption.get` (two paths of 3 nodes each)
- **THEN** the result is `1` (= 6 − 4 − 1)
