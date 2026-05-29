# finding-all-similar-motifs Specification

## Purpose

Enumerate every substring `t′` of a genome `t` whose unit-cost edit
distance `d_E(s, t′)` to a motif `s` is at most `k`, reporting each as a
`(location, length)` pair (Rosalind KSIM — "Finding All Similar Motifs").

## Requirements

### Requirement: Validated SimilarMotifsProblem input bundle

The system SHALL provide a validated domain type
`bio.domain.analysis.SimilarMotifsProblem` constructed only through a smart
constructor
`SimilarMotifsProblem.from(k: Int, motif: DnaString, genome: DnaString): Either[SimilarMotifsProblemError, SimilarMotifsProblem]`.
The smart constructor MUST enforce, in this order, first-failure-wins:

1. `1 <= k <= 50`, else `KOutOfRange(k, 1, 50)`.
2. `motif.value.length <= 5000`, else `MotifTooLong(length, 5000)`.
3. `genome.value.length <= 50000`, else `GenomeTooLong(length, 50000)`.

Empty `motif` and/or empty `genome` MUST be accepted. The constructed value
MUST expose `k: Int`, `motif: DnaString`, and `genome: DnaString`. The case
class MUST be `sealed abstract` so the synthesised `apply` and `copy` cannot
leak around the smart constructor.

#### Scenario: Accepts the canonical Rosalind KSIM sample
- **WHEN** `SimilarMotifsProblem.from` is called with `k = 2`, motif `ACGTAG`, and genome `ACGGATCGGCATCGT`
- **THEN** it returns `Right(problem)` where all three inputs round-trip into the wrapper

#### Scenario: Accepts k at the lower bound
- **WHEN** `SimilarMotifsProblem.from` is called with `k = 1` and valid strings
- **THEN** it returns `Right(problem)`

#### Scenario: Accepts k at the upper bound
- **WHEN** `SimilarMotifsProblem.from` is called with `k = 50` and valid strings
- **THEN** it returns `Right(problem)`

#### Scenario: Accepts an empty motif and an empty genome
- **WHEN** `SimilarMotifsProblem.from` is called with `k = 1` and two empty `DnaString`s
- **THEN** it returns `Right(problem)` where both strings are empty

#### Scenario: Accepts motif and genome at their upper bounds
- **WHEN** `SimilarMotifsProblem.from` is called with a 5000-bp motif and a 50000-bp genome
- **THEN** it returns `Right(problem)`

#### Scenario: Rejects k = 0 as KOutOfRange(0, 1, 50)
- **WHEN** `SimilarMotifsProblem.from` is called with `k = 0` and valid strings
- **THEN** it returns `Left(SimilarMotifsProblemError.KOutOfRange(0, 1, 50))`

#### Scenario: Rejects k = 51 as KOutOfRange(51, 1, 50)
- **WHEN** `SimilarMotifsProblem.from` is called with `k = 51` and valid strings
- **THEN** it returns `Left(SimilarMotifsProblemError.KOutOfRange(51, 1, 50))`

#### Scenario: Rejects a 5001-bp motif as MotifTooLong(5001, 5000)
- **WHEN** `SimilarMotifsProblem.from` is called with a valid `k`, a 5001-bp motif, and a valid genome
- **THEN** it returns `Left(SimilarMotifsProblemError.MotifTooLong(5001, 5000))`

#### Scenario: Rejects a 50001-bp genome as GenomeTooLong(50001, 50000)
- **WHEN** `SimilarMotifsProblem.from` is called with a valid `k`, a valid motif, and a 50001-bp genome
- **THEN** it returns `Left(SimilarMotifsProblemError.GenomeTooLong(50001, 50000))`

#### Scenario: Reports KOutOfRange first when k is invalid and the motif is too long
- **WHEN** `SimilarMotifsProblem.from` is called with `k = 0` and a 5001-bp motif
- **THEN** it returns `Left(SimilarMotifsProblemError.KOutOfRange(0, 1, 50))`

#### Scenario: Companion apply does not leak
- **WHEN** code attempts to construct via `SimilarMotifsProblem(k, motif, genome)`
- **THEN** compilation fails (the synthesised apply is hidden behind the smart constructor)

#### Scenario: copy does not leak
- **WHEN** code attempts to mutate via `problem.copy(k = other)`
- **THEN** compilation fails (the synthesised copy is hidden behind the smart constructor)

### Requirement: SimilarMotif output ADT

The system SHALL provide a domain type `bio.domain.analysis.SimilarMotif`
carrying:

- `location: Int` — the 1-based start position of the matching substring in
  the genome `t`.
- `length: Int` — the length of the matching substring.

`SimilarMotif` MUST be a plain `final case class` (free `apply`, `copy`,
equality, and pattern-matching — no smart constructor).

#### Scenario: Constructs with named fields
- **WHEN** code calls `SimilarMotif(location = 1, length = 4)`
- **THEN** the resulting value exposes those two fields and is value-equal to another instance with the same fields

### Requirement: SimilarMotifs.findAll returns every substring within the edit-distance budget

The system SHALL provide an algorithm object
`bio.algorithms.analysis.SimilarMotifs` with a method
`findAll(problem: SimilarMotifsProblem): List[SimilarMotif]` that returns
every substring `t′` of the genome `t` whose unit-cost edit distance
`d_E(motif, t′)` (insertion, deletion, and substitution each costing 1) is
less than or equal to `k`. Each substring is reported as a `SimilarMotif`
with its 1-based start `location` in `t` and its `length`.

The returned list MUST:

1. contain exactly the substrings `t′` with `d_E(motif, t′) <= k` — no
   omissions and no substrings exceeding the budget;
2. include nested or overlapping hits that share a start (multiple lengths
   at the same `location` are distinct results);
3. be sorted by `(location, length)` in ascending order;
4. be empty when the genome has no qualifying substring.

The implementation MUST use a forward approximate-matching dynamic program
(free start in `t`, `D[0][b] = 0`, `D[a][0] = a`,
`D[a][b] = min(D[a-1][b-1] + (s[a-1] != t[b-1]), D[a-1][b] + 1, D[a][b-1] + 1)`)
to identify valid end positions `b` with `D[m][b] <= k`, followed by a
per-end backward dynamic program that computes the exact edit distance of
the whole motif against each candidate-length suffix ending at `b` (length
window `|len - m| <= k`) and emits a hit when that exact distance is
`<= k`.

#### Scenario: Canonical Rosalind KSIM sample
- **WHEN** `SimilarMotifs.findAll` is called with `k = 2`, motif `ACGTAG`, and genome `ACGGATCGGCATCGT`
- **THEN** the result is `List(SimilarMotif(1, 4), SimilarMotif(1, 5), SimilarMotif(1, 6))` — the exact Rosalind published output

#### Scenario: Finds every substring within one edit across multiple starts and lengths
- **WHEN** `SimilarMotifs.findAll` is called with `k = 1`, motif `ACG`, and genome `ACG`
- **THEN** the result is `List(SimilarMotif(1, 2), SimilarMotif(1, 3), SimilarMotif(2, 2))`

#### Scenario: A motif longer than the genome can still match within budget
- **WHEN** `SimilarMotifs.findAll` is called with `k = 1`, motif `ACGT`, and genome `ACG`
- **THEN** the result is `List(SimilarMotif(1, 3))` (one deletion)

#### Scenario: Agrees with a brute-force edit-distance enumeration
- **WHEN** `SimilarMotifs.findAll` is called with any valid `(k, motif, genome)`
- **THEN** the result equals the `(location, length)`-sorted list of every substring `t′` of the genome with `d_E(motif, t′) <= k` computed by exhaustive enumeration

#### Scenario: No qualifying substring yields an empty list
- **WHEN** `SimilarMotifs.findAll` is called with `k = 1`, motif `AAAA`, and genome `CCCCCCCC`
- **THEN** the result is `Nil`

#### Scenario: An empty genome yields an empty list
- **WHEN** `SimilarMotifs.findAll` is called with `k = 1`, a non-empty motif, and an empty genome
- **THEN** the result is `Nil`

#### Scenario: Results are sorted by location then length
- **WHEN** `SimilarMotifs.findAll` returns more than one hit
- **THEN** the hits appear in ascending order of `location`, and ties on `location` are broken by ascending `length`
