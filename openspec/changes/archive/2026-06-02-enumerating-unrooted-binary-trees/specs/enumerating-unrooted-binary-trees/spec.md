## ADDED Requirements

### Requirement: Unrooted binary trees problem validation

The system SHALL provide a validated `UnrootedBinaryTreesProblem` domain type wrapping the taxa as a `Vector[String]`, requiring at least 3 taxa, at most 10 taxa, and no duplicates. It MUST be constructed only through a smart constructor `from(taxa)` returning `Either[UnrootedBinaryTreesProblemError, UnrootedBinaryTreesProblem]`, applying first-failure-wins validation, and MUST NOT expose a public `apply` or `copy` that could bypass validation.

#### Scenario: Accepts a valid collection of taxa

- **WHEN** `UnrootedBinaryTreesProblem.from` is called with `dog`, `cat`, `mouse`, `elephant`
- **THEN** it returns a `Right` whose `taxa` match the inputs

#### Scenario: Accepts the minimum of three taxa

- **WHEN** `UnrootedBinaryTreesProblem.from` is called with `dog`, `cat`, `mouse`
- **THEN** it returns a `Right`

#### Scenario: Rejects fewer than three taxa

- **WHEN** `UnrootedBinaryTreesProblem.from` is called with `dog`, `cat`
- **THEN** it returns `Left(TooFewTaxa(2, 3))`

#### Scenario: Rejects more than ten taxa

- **WHEN** `UnrootedBinaryTreesProblem.from` is called with 11 taxa
- **THEN** it returns `Left(TooManyTaxa(11, 10))`

#### Scenario: Rejects duplicate taxa

- **WHEN** `UnrootedBinaryTreesProblem.from` is called with `dog`, `cat`, `cat`
- **THEN** it returns `Left(DuplicateTaxon("cat"))`

#### Scenario: Cannot be constructed via a public apply or copy

- **WHEN** code attempts `UnrootedBinaryTreesProblem(...)` or `.copy(...)` on a constructed instance
- **THEN** the code fails to compile

### Requirement: Unrooted binary trees result rendering

The system SHALL provide an `UnrootedBinaryTrees` result type holding the Newick strings of the enumerated trees as a `Vector[String]` and exposing a `format: String` rendering one tree per line. The empty result MUST render as the empty string.

#### Scenario: Exposes the Newick strings

- **WHEN** an `UnrootedBinaryTrees` result is constructed from a vector of Newick strings
- **THEN** its `trees` field returns exactly that vector

#### Scenario: Formats one tree per line

- **WHEN** `format` is called on a result holding `((cat,mouse))dog;` and `((mouse,cat))dog;`
- **THEN** it returns `"((cat,mouse))dog;\n((mouse,cat))dog;"`

#### Scenario: Empty result renders as the empty string

- **WHEN** `format` is called on a result holding no trees
- **THEN** it returns `""`

### Requirement: Unrooted binary tree enumeration

The system SHALL provide an algorithm that, given an `UnrootedBinaryTreesProblem` of `n` taxa, returns every unrooted binary tree whose leaves are those taxa, each rendered in Newick format rooted at the first taxon. The number of trees MUST be `(2n−5)!!`, and the trees MUST be distinct.

#### Scenario: Enumerates the canonical Rosalind EUBT sample

- **WHEN** the algorithm is run on `dog`, `cat`, `mouse`, `elephant`
- **THEN** it returns exactly the 3 trees `(((cat,mouse),elephant))dog;`, `(((cat,elephant),mouse))dog;`, and `((cat,(mouse,elephant)))dog;`

#### Scenario: Produces the single tree for three taxa

- **WHEN** the algorithm is run on `dog`, `cat`, `mouse`
- **THEN** it returns exactly the one tree `((cat,mouse))dog;`

#### Scenario: Produces (2n−5)!! distinct trees for five taxa

- **WHEN** the algorithm is run on five taxa
- **THEN** it returns 15 trees, all distinct

#### Scenario: Every tree is rooted at the first taxon

- **WHEN** the algorithm is run on any valid collection of taxa whose first taxon is `t0`
- **THEN** every returned Newick string ends with `)t0;`
