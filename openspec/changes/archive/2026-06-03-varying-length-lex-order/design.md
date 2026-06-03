## Context

LEXV ("Ordering Strings of Varying Length Lexicographically") takes an ordered alphabet 𝒜 (≤ 12 symbols, the given order *defines* the symbol order) and `n ≤ 4`, and returns every string of length 1 … `n` over 𝒜 in the varying-length lexicographic order. That order is the one where a string precedes any longer string extending it (`APPLE < APPLET`); equivalently, padding the shorter string with a blank symbol ranked before every real symbol. Concretely, the required order is exactly a **pre-order depth-first traversal** of the alphabet tree truncated at depth `n`: emit the current string, then recurse into each child in alphabet order.

The project already solves the fixed-length case in `EnumerateKmers` (LEXF). LEXV reuses the same "given order = lexicographic order" convention but emits *every* prefix along the way, so the types live beside the LEXF ones in `bio.{domain,algorithms}.combinatorics`.

## Goals / Non-Goals

**Goals:**
- Validated `LexOrderProblem(alphabet, maxLength)` (≤ 12 distinct symbols, length 1 … 4) via a smart constructor returning `Either`, `sealed abstract case class` to block `apply`/`copy`.
- Pure, total `VaryingLengthLexOrder.enumerate(problem): LexOrdering`.
- Result type with `format: String` (one string per line).
- Reproduce the canonical sample order exactly.

**Non-Goals:**
- A general comparator/sort — the pre-order DFS produces the order directly.
- Alphabets or lengths beyond the Rosalind caps.

## Decisions

**1. Pre-order DFS to depth `n`.**
A pure recursion builds the output: `go(prefix)` maps each alphabet symbol `c` (in order) to `s = prefix + c`, emitting `s` and, when `s.length < n`, prepending it before the results of `go(s)`. Starting from `go("")` yields the full list. Because the alphabet is iterated in its given order and each node is emitted before its children, the output is exactly the varying-length lexicographic order — no explicit sort or comparator. Total output size is `Σ_{k=1}^{n} |𝒜|^k` (≤ `12 + … + 12⁴ ≈ 22 620` at the caps), trivially fast.

**2. Validation and first-failure-wins ordering.**
`LexOrderProblem.from(alphabet, maxLength)` validates, in order: `alphabet.nonEmpty` (`EmptyAlphabet`); `alphabet.size ≤ 12` (`TooManySymbols(count, 12)`); no duplicate symbol (`DuplicateSymbol(symbol)`, first repeat); `maxLength ≥ 1` (`NonPositiveLength(length)`); `maxLength ≤ 4` (`LengthExceedsMaximum(length, 4)`). This mirrors `KmerEnumerationProblem` (LEXF) with the LEXV caps (12 symbols, length 4).

**3. Result rendering.**
`LexOrdering(strings: Vector[String])` with `format = strings.mkString("\n")` — one string per line, matching the Rosalind output.

**4. Pure functional recursion.**
The DFS uses `flatMap`/`+:` over immutable `Vector`s — no mutation. The public `enumerate` signature is pure and total.

**5. Naming and placement.**
`LexOrderProblem`, `LexOrderProblemError`, and the `LexOrdering` result live in `bio.domain.combinatorics` (beside the LEXF `KmerEnumeration*` types); the algorithm `VaryingLengthLexOrder.enumerate` in `bio.algorithms.combinatorics`. Result (`LexOrdering`) and algorithm (`VaryingLengthLexOrder`) names are distinct, so no import alias is needed.

## Risks / Trade-offs

- **[Order correctness]** → the pre-order DFS reproduces the canonical 39-string sample (alphabet `D N A`, `n = 3`) exactly; verified by a scenario asserting the full ordered list.
- **[Edge cases]** → `n = 1` yields just the alphabet in order; a single-symbol alphabet yields a simple chain; covered by scenarios.
- **[Output size]** → bounded by the Rosalind caps (≈ 22 k strings worst case), well within memory/time.
- **[Recursion depth]** → at most `n = 4`, no stack concern.
