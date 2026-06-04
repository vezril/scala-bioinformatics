## ADDED Requirements

### Requirement: Signed Permutation Problem domain type

The system SHALL provide a validated `SignedPermutationProblem` domain type in `bio.domain.combinatorics` wrapping the length `n` (`Int`). It SHALL be a `sealed abstract case class` with a private construction path, built only through a smart constructor `from(n: Int): Either[SignedPermutationProblemError, SignedPermutationProblem]`. The constructor SHALL validate, with first-failure-wins ordering: `n ≥ 1`, then `n ≤ 6`.

#### Scenario: Accepts a valid length
- **WHEN** `SignedPermutationProblem.from` is called with `2`
- **THEN** it returns a `Right` holding a `SignedPermutationProblem` with `n == 2`

#### Scenario: Rejects a non-positive length
- **WHEN** `SignedPermutationProblem.from` is called with `0`
- **THEN** it returns a `Left` holding `SignedPermutationProblemError.NonPositive(0)`

#### Scenario: Rejects a length over the cap
- **WHEN** `SignedPermutationProblem.from` is called with `7`
- **THEN** it returns a `Left` holding `SignedPermutationProblemError.ExceedsMaximum(7, 6)`

#### Scenario: Cannot be constructed via a public companion apply
- **WHEN** `bio.domain.combinatorics.SignedPermutationProblem(2)` is referenced in source
- **THEN** the code does not compile

#### Scenario: Does not expose a public copy method
- **WHEN** `copy` is invoked on a constructed `SignedPermutationProblem`
- **THEN** the code does not compile

### Requirement: Signed Permutation Problem error ADT

The system SHALL provide a `SignedPermutationProblemError` sealed ADT in `bio.domain.combinatorics` enumerating the validation failures for `SignedPermutationProblem`: `NonPositive(n: Int)` and `ExceedsMaximum(n: Int, max: Int)`.

#### Scenario: Reports the offending length and the maximum
- **WHEN** length `9` is rejected for exceeding the cap
- **THEN** the error is `SignedPermutationProblemError.ExceedsMaximum(9, 6)`

### Requirement: Signed Permutations result type

The system SHALL provide a `SignedPermutations` result type in `bio.domain.combinatorics` holding the list of signed permutations (`Vector[Vector[Int]]`), exposing `count: Int` (the number of permutations) and a `format: String` that renders the count on the first line followed by each permutation space-separated on its own line.

#### Scenario: Reports the count as the number of permutations
- **WHEN** a `SignedPermutations` holding 8 permutations is queried
- **THEN** `count` is `8`

#### Scenario: Formats the count followed by the permutations
- **WHEN** a `SignedPermutations` holding `Vector(Vector(1, 2), Vector(-1, 2))` is formatted
- **THEN** `format` returns `"2\n1 2\n-1 2"`

### Requirement: Signed permutation enumeration algorithm

The system SHALL provide a `SignedPermutationEnumeration` algorithm in `bio.algorithms.combinatorics` with a pure, total method `enumerate(problem: SignedPermutationProblem): SignedPermutations`. It SHALL produce every signed permutation of length `n` — each of the `n!` orderings of `{1, …, n}` with each element independently assigned a `+` or `−` sign — yielding `n! · 2ⁿ` permutations in total.

#### Scenario: Matches the canonical Rosalind sample
- **WHEN** `enumerate` is run on `n = 2`
- **THEN** the result's `count` is `8`
- **AND** the set of permutations equals `{[-1,-2], [-1,2], [1,-2], [1,2], [-2,-1], [-2,1], [2,-1], [2,1]}`

#### Scenario: A length of one yields two signed permutations
- **WHEN** `enumerate` is run on `n = 1`
- **THEN** the result's `count` is `2` and the set of permutations equals `{[-1], [1]}`

#### Scenario: The count follows the n! · 2^n formula
- **WHEN** `enumerate` is run on `n = 3`
- **THEN** the result's `count` is `48`

#### Scenario: Every permutation is a valid signed ordering
- **WHEN** `enumerate` is run on `n = 3`
- **THEN** each permutation has length 3 and its absolute values are a permutation of `{1, 2, 3}`
