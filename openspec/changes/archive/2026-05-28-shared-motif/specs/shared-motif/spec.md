## ADDED Requirements

### Requirement: Validated SharedMotifProblem input bundle

The system SHALL provide a validated domain type `bio.domain.analysis.SharedMotifProblem` constructed only through a smart constructor `SharedMotifProblem.from(sequences: Vector[DnaString]): Either[SharedMotifProblemError, SharedMotifProblem]`. The smart constructor MUST enforce, in this order, first-failure-wins:

1. `sequences.nonEmpty`, else `EmptyCollection`.
2. `sequences.size <= 100`, else `TooManyStrings(count, max)`.
3. Each `sequences(i).value.length <= 1000`, else `StringTooLong(index, length, max)` (using the first violating index).

Empty strings WITHIN the collection MUST be accepted — an empty string is trivially a substring of any string, and the algorithm short-circuits to `""` in that case. The constructed value MUST expose `sequences: Vector[DnaString]`. The case class MUST be `sealed abstract` so the synthesised `apply` and `copy` cannot leak around the smart constructor.

#### Scenario: Accepts the canonical 3-string Rosalind LCSM sample
- **WHEN** `SharedMotifProblem.from` is called with `Vector(DnaString.from("GATTACA").toOption.get, DnaString.from("TAGACCA").toOption.get, DnaString.from("ATACA").toOption.get)`
- **THEN** it returns `Right(problem)` where `problem.sequences.size == 3`

#### Scenario: Accepts a single-string collection
- **WHEN** `SharedMotifProblem.from` is called with a single-element `Vector(DnaString.from("ACGT").toOption.get)`
- **THEN** it returns `Right(problem)` where `problem.sequences.size == 1`

#### Scenario: Accepts 100 strings at the upper boundary
- **WHEN** `SharedMotifProblem.from` is called with 100 short DNA strings
- **THEN** it returns `Right(problem)` where `problem.sequences.size == 100`

#### Scenario: Accepts a collection containing an empty string
- **WHEN** `SharedMotifProblem.from` is called with `Vector(DnaString.from("ACGT").toOption.get, DnaString.from("").toOption.get)`
- **THEN** it returns `Right(problem)`

#### Scenario: Rejects an empty collection as EmptyCollection
- **WHEN** `SharedMotifProblem.from` is called with `Vector.empty`
- **THEN** it returns `Left(SharedMotifProblemError.EmptyCollection)`

#### Scenario: Rejects 101 strings as TooManyStrings(101, 100)
- **WHEN** `SharedMotifProblem.from` is called with 101 identical short DNA strings
- **THEN** it returns `Left(SharedMotifProblemError.TooManyStrings(101, 100))`

#### Scenario: Rejects a 1001-character string as StringTooLong(0, 1001, 1000)
- **WHEN** `SharedMotifProblem.from` is called with a single-element collection containing a 1001-char DNA string
- **THEN** it returns `Left(SharedMotifProblemError.StringTooLong(0, 1001, 1000))`

#### Scenario: Companion apply does not leak
- **WHEN** code attempts to construct via `SharedMotifProblem(Vector.empty)`
- **THEN** compilation fails (the synthesised apply is hidden behind the smart constructor)

#### Scenario: copy does not leak
- **WHEN** code attempts to mutate via `problem.copy(sequences = Vector.empty)`
- **THEN** compilation fails (the synthesised copy is hidden behind the smart constructor)

### Requirement: SharedMotif.find returns the lex-smallest longest common substring

The system SHALL provide an algorithm object `bio.algorithms.analysis.SharedMotif` with a method `find(problem: SharedMotifProblem): String` that returns the *lex-smallest* common substring of maximum length among all strings in `problem.sequences`.

The implementation MUST use binary search over candidate length `L ∈ {1, ..., |shortest|}`; for each `L`, build a `Set[String]` of every length-`L` substring of each input string and intersect them all. The maximum `L` for which the intersection is non-empty determines the answer's length; the result is the lexicographically-smallest element of that intersection. When `L = 0` is the only feasible length (or any input string is empty), the result MUST be `""`.

#### Scenario: Canonical Rosalind LCSM sample
- **WHEN** `SharedMotif.find` is called with `GATTACA`, `TAGACCA`, `ATACA`
- **THEN** it returns `"AC"` (the lex-smallest length-2 common substring; `"CA"` and `"TA"` are also valid length-2 answers per spec)

#### Scenario: Single-string collection returns that string
- **WHEN** `SharedMotif.find` is called with `Vector(DnaString.from("ACGT"))`
- **THEN** it returns `"ACGT"`

#### Scenario: Two identical strings return that string
- **WHEN** `SharedMotif.find` is called with `Vector(DnaString.from("ACGT"), DnaString.from("ACGT"))`
- **THEN** it returns `"ACGT"`

#### Scenario: Two strings sharing no character return the empty string
- **WHEN** `SharedMotif.find` is called with `Vector(DnaString.from("AAAA"), DnaString.from("CCCC"))`
- **THEN** it returns `""`

#### Scenario: Collection containing an empty string returns the empty string
- **WHEN** `SharedMotif.find` is called with `Vector(DnaString.from("ACGT"), DnaString.from(""))`
- **THEN** it returns `""`

#### Scenario: Single-character LCS returned when no longer match exists
- **WHEN** `SharedMotif.find` is called with `Vector(DnaString.from("ACGT"), DnaString.from("ATAT"))`
- **THEN** it returns `"A"` (the lex-smallest single-char common substring; `"T"` is also valid)

#### Scenario: One shared run with distractors
- **WHEN** `SharedMotif.find` is called with `Vector(DnaString.from("CCGTAGG"), DnaString.from("AAGTACC"), DnaString.from("TTGTAGT"))`
- **THEN** it returns `"GTA"` (the unique length-3 common substring)
