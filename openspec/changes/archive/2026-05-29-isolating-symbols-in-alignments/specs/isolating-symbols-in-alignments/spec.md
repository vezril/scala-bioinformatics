## ADDED Requirements

### Requirement: Validated IsolatedSymbolsProblem input bundle

The system SHALL provide a validated domain type
`bio.domain.analysis.IsolatedSymbolsProblem` constructed only through a
smart constructor
`IsolatedSymbolsProblem.from(left: DnaString, right: DnaString): Either[IsolatedSymbolsProblemError, IsolatedSymbolsProblem]`.
The smart constructor MUST enforce, in this order, first-failure-wins:

1. `left.value.length <= 1000`, else `LeftTooLong(length, max)`.
2. `right.value.length <= 1000`, else `RightTooLong(length, max)`.

Empty `left` and/or empty `right` MUST be accepted. The constructed value
MUST expose `left: DnaString` and `right: DnaString`. The case class MUST
be `sealed abstract` so the synthesised `apply` and `copy` cannot leak
around the smart constructor.

#### Scenario: Accepts the canonical Rosalind OSYM sample
- **WHEN** `IsolatedSymbolsProblem.from` is called with `ATAGATA` and `ACAGGTA`
- **THEN** it returns `Right(problem)` where both inputs round-trip into the wrapper

#### Scenario: Accepts two empty strings
- **WHEN** `IsolatedSymbolsProblem.from` is called with two empty `DnaString`s
- **THEN** it returns `Right(problem)` where both fields are empty

#### Scenario: Accepts an empty left with a non-empty right
- **WHEN** `IsolatedSymbolsProblem.from` is called with empty left and `GATTACA` right
- **THEN** it returns `Right(problem)`

#### Scenario: Accepts a non-empty left with an empty right
- **WHEN** `IsolatedSymbolsProblem.from` is called with `GATTACA` left and empty right
- **THEN** it returns `Right(problem)`

#### Scenario: Accepts both strings at the 1000-bp upper bound
- **WHEN** `IsolatedSymbolsProblem.from` is called with `"A" * 1000` and `"A" * 1000`
- **THEN** it returns `Right(problem)`

#### Scenario: Rejects a 1001-bp left as LeftTooLong(1001, 1000)
- **WHEN** `IsolatedSymbolsProblem.from` is called with a 1001-bp left and a short right
- **THEN** it returns `Left(IsolatedSymbolsProblemError.LeftTooLong(1001, 1000))`

#### Scenario: Rejects a 1001-bp right as RightTooLong(1001, 1000)
- **WHEN** `IsolatedSymbolsProblem.from` is called with a short left and a 1001-bp right
- **THEN** it returns `Left(IsolatedSymbolsProblemError.RightTooLong(1001, 1000))`

#### Scenario: Reports LeftTooLong first when both sides exceed the cap
- **WHEN** `IsolatedSymbolsProblem.from` is called with a 1001-bp left and a 1001-bp right
- **THEN** it returns `Left(IsolatedSymbolsProblemError.LeftTooLong(1001, 1000))`

#### Scenario: Companion apply does not leak
- **WHEN** code attempts to construct via `IsolatedSymbolsProblem(left, right)`
- **THEN** compilation fails (the synthesised apply is hidden behind the smart constructor)

#### Scenario: copy does not leak
- **WHEN** code attempts to mutate via `problem.copy(right = other)`
- **THEN** compilation fails (the synthesised copy is hidden behind the smart constructor)

### Requirement: IsolatedSymbols output ADT

The system SHALL provide a domain type
`bio.domain.analysis.IsolatedSymbols` carrying:

- `globalScore: Int` — the maximum global alignment score of `left` and
  `right` under the +1/-1 mismatch score.
- `matrixSum: Long` — the sum of every element of the symbol-isolation
  matrix `M`.

`matrixSum` MUST be a `Long` (the sum of up to `10^6` entries, each as
negative as roughly `-(m + n)`, can approach `-2 · 10^9` and overflow an
`Int`). `IsolatedSymbols` MUST be a plain `final case class` (free `apply`,
`copy`, equality, and pattern-matching — no smart constructor).

#### Scenario: Constructs with named fields
- **WHEN** code calls `IsolatedSymbols(globalScore = 3, matrixSum = -139L)`
- **THEN** the resulting value exposes those two fields and is value-equal to another instance with the same fields

### Requirement: IsolatedSymbols.compute returns the global score and the symbol-isolation matrix sum

The system SHALL provide an algorithm object
`bio.algorithms.analysis.IsolatedSymbols` with a method
`compute(problem: IsolatedSymbolsProblem): IsolatedSymbols` that returns
the maximum global alignment score of `problem.left` and `problem.right`
under the *mismatch score* (`+1` per matched symbol, `-1` per mismatched,
inserted, or deleted symbol), plus the sum of every element of the matrix
`M`, where `M[j][k]` is the maximum score of any global alignment that
aligns `left[j]` with `right[k]`.

The implementation MUST use a forward and a backward `O(m · n)`
dynamic-programming pass:

- forward `f(i)(j)` = best global score of `left[0..i)` vs `right[0..j)`,
  with `f(0)(0) = 0`, `f(i)(0) = -i`, `f(0)(j) = -j`, and
  `f(i)(j) = max(f(i-1)(j-1) + sc, f(i-1)(j) - 1, f(i)(j-1) - 1)`;
- backward `b(i)(j)` = best global score of `left[i..m)` vs `right[j..n)`,
  with `b(m)(n) = 0`, `b(i)(n) = -(m - i)`, `b(m)(j) = -(n - j)`, and
  `b(i)(j) = max(b(i+1)(j+1) + sc, b(i+1)(j) - 1, b(i)(j+1) - 1)`;
- `globalScore = f(m)(n)`;
- `M[j][k] = f(j)(k) + sc(left(j), right(k)) + b(j+1)(k+1)` for
  `j ∈ [0, m)`, `k ∈ [0, n)`, where `sc(a, b) = if a == b then +1 else -1`;
- `matrixSum = Σ M[j][k]`, accumulated in a `Long`.

The returned `IsolatedSymbols` MUST satisfy:

1. `globalScore` equals the maximum global alignment score under the
   mismatch score.
2. `matrixSum` equals the sum of all `M[j][k]`.
3. When `left` or `right` is empty, `matrixSum` MUST be `0`.

#### Scenario: Canonical Rosalind OSYM sample
- **WHEN** `IsolatedSymbols.compute` is called with `ATAGATA` and `ACAGGTA`
- **THEN** the result is `IsolatedSymbols(3, -139L)` — the exact Rosalind published output

#### Scenario: Identical single characters
- **WHEN** `IsolatedSymbols.compute` is called with `A` and `A`
- **THEN** the result is `IsolatedSymbols(1, 1L)` (the lone forced column `M[0][0]` is a match)

#### Scenario: Mismatched single characters
- **WHEN** `IsolatedSymbols.compute` is called with `A` and `C`
- **THEN** the result is `IsolatedSymbols(-1, -1L)` (the lone forced column `M[0][0]` is a mismatch)

#### Scenario: Identical strings score one point per symbol
- **WHEN** `IsolatedSymbols.compute` is called with `GATTACA` and `GATTACA`
- **THEN** `result.globalScore == 7`

#### Scenario: Two empty strings yield score 0 and matrix sum 0
- **WHEN** `IsolatedSymbols.compute` is called with two empty strings
- **THEN** the result is `IsolatedSymbols(0, 0L)`

#### Scenario: Empty left yields the all-gap global score and matrix sum 0
- **WHEN** `IsolatedSymbols.compute` is called with empty left and `GATTACA` right
- **THEN** the result is `IsolatedSymbols(-7, 0L)`

#### Scenario: Empty right yields the all-gap global score and matrix sum 0
- **WHEN** `IsolatedSymbols.compute` is called with `GATTACA` left and empty right
- **THEN** the result is `IsolatedSymbols(-7, 0L)`

#### Scenario: The global score is symmetric in its arguments
- **WHEN** `IsolatedSymbols.compute` is called with `(ATAGATA, ACAGGTA)` and with `(ACAGGTA, ATAGATA)`
- **THEN** both calls return the same `globalScore` and the same `matrixSum`
