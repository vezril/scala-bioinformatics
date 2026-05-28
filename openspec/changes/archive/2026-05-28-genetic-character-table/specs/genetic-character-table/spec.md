## ADDED Requirements

### Requirement: Validated GeneticCharacterTableProblem input bundle

The system SHALL provide a validated domain type `bio.domain.analysis.GeneticCharacterTableProblem` constructed only through a smart constructor `GeneticCharacterTableProblem.from(sequences: Vector[DnaString]): Either[GeneticCharacterTableProblemError, GeneticCharacterTableProblem]`. The smart constructor MUST enforce, in this order, first-failure-wins:

1. `sequences` is non-empty, else `EmptyInput`.
2. `1 <= sequences.size <= 100`, else `TooManyStrings(count, max)`.
3. Each `sequences(i).value` has length in `1..300`, else `StringTooLong(index, length, max)` (using the first violating index).
4. Every string has the same length, else `InconsistentLength(index, expected, actual)` (using the first mismatch).
5. Every column `j` of the aligned matrix has at most 2 distinct symbols, else `NonCharacterizable(columnIndex, distinctCount)` (using the first violating column).

The constructed value MUST expose `sequences: Vector[DnaString]`, `size: Int` (= row count), and `length: Int` (= common column count). The case class MUST be `sealed abstract` so the synthesised `apply` and `copy` cannot leak around the smart constructor.

#### Scenario: Accepts the canonical 5×8 Rosalind sample
- **WHEN** `GeneticCharacterTableProblem.from` is called with the five DNA strings `ATGCTACC`, `CGTTTACC`, `ATTCGACC`, `AGTCTCCC`, `CGTCTATC`
- **THEN** it returns `Right(problem)` where `problem.size == 5` and `problem.length == 8`

#### Scenario: Accepts size-100 and length-300 at the upper boundaries
- **WHEN** `GeneticCharacterTableProblem.from` is called with 100 DNA strings each of length 300 made of two repeating symbols
- **THEN** it returns `Right(problem)` where `problem.size == 100` and `problem.length == 300`

#### Scenario: Rejects an empty input as EmptyInput
- **WHEN** `GeneticCharacterTableProblem.from` is called with `Vector.empty`
- **THEN** it returns `Left(GeneticCharacterTableProblemError.EmptyInput)`

#### Scenario: Rejects 101 strings as TooManyStrings(101, 100)
- **WHEN** `GeneticCharacterTableProblem.from` is called with 101 identical short DNA strings
- **THEN** it returns `Left(GeneticCharacterTableProblemError.TooManyStrings(101, 100))`

#### Scenario: Rejects a 301-character string as StringTooLong(0, 301, 300)
- **WHEN** `GeneticCharacterTableProblem.from` is called with a single DNA string of length 301
- **THEN** it returns `Left(GeneticCharacterTableProblemError.StringTooLong(0, 301, 300))`

#### Scenario: Rejects mismatched row lengths as InconsistentLength(1, 8, 7)
- **WHEN** `GeneticCharacterTableProblem.from` is called with `ATGCTACC` then `CGTTTAC`
- **THEN** it returns `Left(GeneticCharacterTableProblemError.InconsistentLength(1, 8, 7))`

#### Scenario: Rejects a non-characterizable column as NonCharacterizable(0, 3)
- **WHEN** `GeneticCharacterTableProblem.from` is called with three DNA strings whose column 0 contains the three distinct symbols `A`, `C`, `G`
- **THEN** it returns `Left(GeneticCharacterTableProblemError.NonCharacterizable(0, 3))`

#### Scenario: Companion apply does not leak
- **WHEN** code attempts to construct via `GeneticCharacterTableProblem(Vector.empty, 0, 0)`
- **THEN** compilation fails (the synthesised apply is hidden behind the smart constructor)

#### Scenario: copy does not leak
- **WHEN** code attempts to mutate via `problem.copy(size = 0)`
- **THEN** compilation fails (the synthesised copy is hidden behind the smart constructor)

### Requirement: GeneticCharacterTable.compute returns the nontrivial column character rows

The system SHALL provide an algorithm object `bio.algorithms.analysis.GeneticCharacterTable` with a method `compute(problem: GeneticCharacterTableProblem): Vector[String]` that returns the nontrivial character rows.

For each column index `j` in `0 until problem.length` (in input order), the implementation MUST:
- partition `0 until problem.size` by `problem.sequences(i).value.charAt(j)`;
- skip the column if it has only one distinct symbol (no character);
- skip the column if either partition has size `< 2` (trivial character);
- otherwise emit one row of length `problem.size`: position `i` is `'1'` iff `problem.sequences(i).value.charAt(j) == problem.sequences(0).value.charAt(j)`, else `'0'`.

Rows MUST be emitted in column-input order (column `0`'s row before column `1`'s row, etc.). The output type is `Vector[String]`. When no column is nontrivial, the result MUST be `Vector.empty`.

#### Scenario: Canonical Rosalind sample
- **WHEN** `GeneticCharacterTable.compute` is called with the problem wrapping the five DNA strings `ATGCTACC`, `CGTTTACC`, `ATTCGACC`, `AGTCTCCC`, `CGTCTATC`
- **THEN** it returns `Vector("10110", "10100")`

#### Scenario: All-identical matrix yields an empty table
- **WHEN** `GeneticCharacterTable.compute` is called with three identical DNA strings `ACGT`
- **THEN** it returns `Vector.empty`

#### Scenario: Single nontrivial column produces one row
- **WHEN** `GeneticCharacterTable.compute` is called with four DNA strings whose columns are constant except column 1 which alternates `A C A C`
- **THEN** it returns a single-element `Vector` whose row encodes that column's partition with `'1'` for the row-0-matching symbol

#### Scenario: All-trivial matrix yields an empty table
- **WHEN** `GeneticCharacterTable.compute` is called with five DNA strings where every column has either one distinct symbol or a 4-1 partition
- **THEN** it returns `Vector.empty`

#### Scenario: Output row length always equals input string count
- **WHEN** `GeneticCharacterTable.compute` is called with any non-empty result
- **THEN** every row has length equal to `problem.size`
