## Why

`bio.domain` has accumulated 25 files across seven unrelated concerns (DNA, RNA, protein, Mendelian inheritance, Fibonacci recurrence, GC analysis, FASTA format). It's a junk drawer. At the same rate of growth, the next two Rosalind problems will push it past 30 files and make navigation costly. This refactor introduces biology-first subdomains so each file lives in a folder that names *what concept it belongs to*, before any new code lands on top.

The refactor also fixes a long-standing oddity: `FastaRecord` lives in `bio.domain` while `FastaParser` lives in `bio.parsing` — the type and its only parser are separated. We consolidate FASTA-related types into `bio.parsing`.

## What Changes

**Pure code organization. No behavior changes, no new requirements, no API changes visible to the outside of the project.** The full 190-test suite is the regression net.

- **NEW packages** under `bio.domain`:
  - `nucleic/` — DNA + RNA shared types (Dna/Rna nucleotides, strings, counts, `SequenceError`)
  - `protein/` — `AminoAcid`, `Codon`, `CodonOutcome`, `GeneticCode`, `ProteinString`, `ProteinError`, `TranslationError`
  - `genetics/` — `Genotype`, `Population`, `PopulationError`
  - `stats/` — `Probability`, `ProbabilityError`
  - `recurrence/` — `RabbitProblem`, `RabbitProblemError`
  - `analysis/` — `GcContent`, `GcContentError`
- **NEW packages** under `bio.algorithms` (mirroring domain):
  - `nucleic/` — `DnaNucleotides`, `DnaReverseComplement`, `RnaTranscription`
  - `protein/` — `RnaTranslation` (subject is the protein it produces)
  - `analysis/` — `HighestGc`
  - `genetics/` — `MendelianInheritance`
  - `recurrence/` — `FibonacciRabbits`
- **MOVED into `bio.parsing`** from `bio.domain`: `FastaRecord`, `FastaParseError`
- **MOVED test files** mirror the main-source moves under `src/test/scala/bio/...`
- **MODIFIED spec scenarios** in two main specs: the hard-coded paths `bio.domain.GcContent` and `bio.domain.ProteinString` in `assertDoesNotCompile` scenarios become `bio.domain.analysis.GcContent` and `bio.domain.protein.ProteinString`
- **No source files in `bio.domain`** at the top level remain after the move — `bio.domain` becomes a "table of contents" of subdomains

## Capabilities

### New Capabilities

None — no new requirements are introduced.

### Modified Capabilities

- `gc-content`: One scenario (`Direct apply does not compile`) updates the fully-qualified path in its `assertDoesNotCompile` example to reflect the new `bio.domain.analysis.GcContent` location. No behavior change.
- `protein-translation`: One scenario (`Direct apply does not compile`) updates the fully-qualified path in its `assertDoesNotCompile` example to reflect the new `bio.domain.protein.ProteinString` location. No behavior change.

## Impact

- **Source files moved**: 22 files in `bio.domain` redistributed across 6 new subdomain packages; 7 files in `bio.algorithms` redistributed across 5 mirrored subdomain packages; 2 files moved from `bio.domain` to `bio.parsing`
- **Test files moved**: 16 test files in `bio.domain`, 7 test files in `bio.algorithms` follow their counterparts
- **Imports rewritten**: every algorithm and test file's `import bio.domain.X` / `import bio.algorithms.X` lines are rewritten to the new subpackage paths. Roughly 30+ files touched in import statements alone.
- **`private[bio]` visibility preserved**: `unsafeFrom` and similar use `private[bio]` which is scoped to the top-level `bio` package, so subpackaging does not change visibility behavior.
- **Two openspec main specs** edited: one `MODIFIED Requirements` delta scenario in each of `gc-content/spec.md` and `protein-translation/spec.md`.
- **No external dependencies added**.
- **No build configuration changes**.
- All 190 tests continue passing after each subdomain move.
