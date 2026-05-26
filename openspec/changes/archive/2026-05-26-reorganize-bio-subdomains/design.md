## Context

`bio.domain` currently holds 25 source files representing seven unrelated biological / mathematical concerns. The flat structure has been viable through 8 Rosalind problems but is starting to obscure relationships — adjacent files in the directory listing are often conceptually unrelated, and naming collisions are starting to bite (e.g., `AminoAcid.A` shadowing `RnaNucleotide.A` already required aliased imports in `GeneticCode`).

`bio.algorithms` has the same shape on a smaller scale (7 files). A unified refactor that organizes both packages on the same biological axis keeps `bio.domain.protein.X` and `bio.algorithms.protein.useX` discoverable side by side.

`FastaRecord` and `FastaParseError` are an organizational accident: they live in `bio.domain` while their only parser lives in `bio.parsing`. Consolidating them removes that split.

## Goals / Non-Goals

**Goals:**
- Reorganize `bio.domain` into six subdomain packages by biology-first taxonomy: `nucleic`, `protein`, `genetics`, `stats`, `recurrence`, `analysis`
- Reorganize `bio.algorithms` into mirrored subdomain packages so the two trees line up: `nucleic`, `protein`, `analysis`, `genetics`, `recurrence`
- Move `FastaRecord` and `FastaParseError` from `bio.domain` into `bio.parsing` so all FASTA-related types live with their parser
- Keep `private[bio]` visibility working — all internal `unsafeFrom` calls continue to compile
- Update the two main spec scenarios that hard-code `bio.domain.GcContent` and `bio.domain.ProteinString` paths
- Move test files in lockstep so the test tree mirrors the source tree
- Run the full test suite after each subdomain move and confirm zero regressions

**Non-Goals:**
- Any behavior change. The 190 tests are the regression net; if one fails, the move is wrong
- Renaming any type. Only locations change
- Renaming capabilities in openspec. Capabilities are problem-oriented, not package-oriented; the names stay
- Promoting parsing to a separate top-level concern beyond what already exists (`bio.parsing` was added in the gc-content change)
- Splitting algorithms by input *vs.* output. Algorithms are placed by their primary biological subject (see decision below)
- Adding scaladoc / documentation. Stay focused on the move
- Touching `Main.scala` or `MainSpec`; they're already minimal and import nothing being moved at top level

## Decisions

### Subdomain axis: biology-first, not problem-first

**Decision**: Subdomains are named after biological / mathematical *concepts* (`nucleic`, `protein`, `genetics`, etc.), not after Rosalind problems (`counting-nucleotides`, `translation`, etc.).

**Rationale**:
- Concepts are stable; problem catalogs grow. A `bio.domain.protein/` package will keep making sense after we've added 30 more protein-related problems.
- One concept serves many problems. `DnaString` is used by counting nucleotides, transcription, reverse complement, GC content, FASTA parsing — putting it in `nucleic/` says what it is. Putting it in `counting-nucleotides/` would lie.
- OpenSpec capability folders already exist for the problem-oriented view (`openspec/specs/dna-nucleotides/`, etc.). The package structure is the *implementation* view; it's allowed to differ from the *requirement* view.

**Alternative considered**: Problem-oriented packages mirroring openspec capabilities 1:1. Rejected — would put `DnaString` in `dna-nucleotides/` and create cross-folder dependencies for everything that uses DNA.

**Alternative considered**: A flat folder with stricter file-naming conventions (e.g., `DNA_String.scala`, `RNA_String.scala`). Rejected — file naming doesn't fix the discovery problem; package structure does.

### `Probability` lives in `stats/`, not `genetics/`

**Decision**: `Probability` and `ProbabilityError` go into `bio.domain.stats`, not `bio.domain.genetics`.

**Rationale**: Probability is a generic math primitive (validated `[0, 1]` Double). It happens to be used by Mendelian inheritance today, but the type itself carries no biological meaning. Anticipated future users include BLAST e-values, protein-folding probabilities, motif p-values — none of those are genetics.

**Trade-off**: `stats/` starts with two files (the type + its error). That's borderline for "worth its own folder", but the alternative (`genetics/Probability.scala`) would actively mislead.

### `RabbitProblem` lives in `recurrence/`, not `genetics/`

**Decision**: `RabbitProblem` and `RabbitProblemError` go into `bio.domain.recurrence`.

**Rationale**: Fibonacci rabbits is a *discrete dynamical system* / linear recurrence problem dressed up with rabbits. The type even reads `RabbitProblem`, not `FibonacciRabbits` — it's the recurrence parameters, not the genetics. Future recurrence-style Rosalind problems (mortality, perfect-population growth, etc.) will land here.

**Trade-off**: Same as `stats/` — starts with two files. Acceptable on the same grounds.

### `FastaRecord` and `FastaParseError` move into `bio.parsing`

**Decision**: `FastaRecord` and `FastaParseError` move from `bio.domain` into `bio.parsing`, joining `FastaParser`.

**Rationale**: The type and its parser belong together. The current split exists because `bio.parsing` was added later (during the gc-content change) and we left the records behind. The record is more naturally read as "a parsed FASTA entity" than "a domain entity that happens to be FASTA-shaped".

**Alternative considered**: Introduce a `domain/format/` subdomain for the record types while keeping the parser in `bio.parsing`. Rejected — same split, just shorter package paths. Doesn't solve the actual cohesion problem.

**Alternative considered**: Promote a `bio.format` top-level package owning both the record types and the parser. Rejected — adds a top-level package for a single format; revisit if FASTQ / GenBank land.

### `RnaTranslation` lives in `algorithms/protein/`, not `algorithms/nucleic/`

**Decision**: `RnaTranslation` goes into `bio.algorithms.protein` even though its input is RNA.

**Rationale**: Classify algorithms by their *primary biological subject*, not their input type. Transcription is a nucleic-acid process (DNA → RNA, both nucleic) → `nucleic/`. Translation's whole purpose is *to produce protein*; the RNA is just the template. This matches the prior decision putting `Codon` in `domain/protein/` even though it's built from `RnaNucleotide` values.

**Trade-off**: A reader looking for "RNA algorithms" might first check `algorithms/nucleic/` and not find translation. Mitigated by the package being obvious enough on second guess, and by scaladoc on `RnaTranslation` (already mentions both input and output).

### Subdomain naming: `nucleic`, not `sequence`

**Decision**: The DNA + RNA subdomain is called `nucleic` (for "nucleic acids").

**Rationale**: Precise — nucleic acids are exactly DNA and RNA. Pairs naturally with `protein/`. "Sequence" is more inclusive (protein sequences also exist) but proteins have their own home, so `sequence/` would either lie or pull protein types in.

### `SequenceError` stays in `nucleic/`

**Decision**: `SequenceError` (shared by `DnaString` and `RnaString`) goes into `bio.domain.nucleic` alongside its only users.

**Rationale**: Both string types live in `nucleic/`, and `SequenceError` is intrinsically tied to nucleic-acid string validation (alphabet violations, length cap exceedances). The name is slightly generic but the type is not — it's a nucleic-acid concern.

**Alternative considered**: Shared `bio.domain.errors/` or `bio.domain.shared/`. Rejected — splits error types from the values they describe; adds a category-oriented folder to a concept-oriented hierarchy.

### Migration approach: one change, per-subdomain commits

**Decision**: Execute the refactor as one openspec change but commit per subdomain move (~7 commits — one per subdomain plus the spec edits and FASTA consolidation).

**Rationale**:
- Atomic at the change level — the codebase enters and exits this refactor in a single coherent state
- Bisectable at the commit level — if a test breaks midway, `git bisect` lands on the exact subdomain move that introduced it
- sbt's incremental compiler will catch missed import rewrites the moment a file is moved; the 190-test suite catches behavior regressions
- Per-subdomain commits also serve as natural cognitive checkpoints: do nucleic, verify, commit, move on

### Tests move in the same commit as their source

**Decision**: For each subdomain move, the test files move in the same commit as the source files.

**Rationale**: Tests reference the source by class name. If tests stay in the old location while source moves, imports in the test break until tests are also moved. Bundling them keeps every commit green.

### No source files remain at `bio.domain` top level

**Decision**: After the refactor, `bio.domain` contains only subpackage directories (no `.scala` files at its root).

**Rationale**: A mixed top-level (some files, some folders) is the worst of both worlds — readers can't tell at a glance whether a file is "important enough to be at the root" or just hasn't been moved yet. Either everything has a home or the refactor is unfinished.

## Risks / Trade-offs

- [Large number of import-statement rewrites across algorithms and tests] → Mitigation: sbt incremental compile flags every missed update immediately; the test suite confirms no behavior drift. Per-subdomain commits limit blast radius.
- [Two openspec main spec scenarios hard-code `bio.domain.X` paths and would silently pass tests even with stale text] → Mitigation: explicit task to update both `assertDoesNotCompile` strings; the strings are checked at compile time by ScalaTest, so a stale path would actually fail the test.
- [Archived openspec changes (`2026-05-22-*`, `2026-05-26-gc-content`, `2026-05-26-protein-translation`) contain text references to the old paths] → Mitigation: archived files are historical; don't edit them. New code follows the new paths.
- [Subdomain folders with only 2 files (`stats/`, `recurrence/`, `analysis/`)] could feel premature → Mitigation: each is conceptually distinct from its neighbors; folding any of them into an adjacent folder would actively mislead. Living with 2-file folders is better than miscategorizing.
- [`RnaTranslation` placement under `protein/` may surprise a reader looking for "RNA algorithms"] → Mitigation: documented in this design; the algorithm's scaladoc already says it produces protein from RNA.
- [`MemberAcc` rename collisions or other Scala quirks during the move] → Mitigation: the test suite is the safety net. If something compiles but behaves differently, a test catches it.
- [No new tests are added by this refactor] → That is intentional; this is a structural change. Adding tests would dilute the intent of the commit history.
