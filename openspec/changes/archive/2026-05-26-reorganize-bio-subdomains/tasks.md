## 1. Preparation

- [x] 1.1 Confirm baseline: run `sbt clean compile test` — 190 tests pass, zero warnings (this is the green baseline; every subsequent step must end here too)

## 2. Create `bio.domain.nucleic` subdomain

- [x] 2.1 Create directories `src/main/scala/bio/domain/nucleic/` and `src/test/scala/bio/domain/nucleic/`
- [x] 2.2 Move source files into `bio.domain.nucleic` (update `package` declarations from `bio.domain` to `bio.domain.nucleic`): `DnaNucleotide.scala`, `DnaNucleotideCounts.scala`, `DnaString.scala`, `RnaNucleotide.scala`, `RnaNucleotideCounts.scala`, `RnaString.scala`, `SequenceError.scala`
- [x] 2.3 Move corresponding test files into `bio.domain.nucleic` (update test `package` declarations): `DnaNucleotideSpec.scala`, `DnaNucleotideCountsSpec.scala`, `RnaNucleotideSpec.scala`, `RnaNucleotideCountsSpec.scala`, `RnaStringSpec.scala`. Note: there is no `DnaStringSpec` in `bio/domain/` — `DnaStringSpec.scala` lives at `src/test/scala/bio/`; leave it there for now and verify its import still works (it uses `bio.domain.DnaString`, which now needs to be `bio.domain.nucleic.DnaString`)
- [x] 2.4 Update `src/test/scala/bio/DnaStringSpec.scala` import to reference `bio.domain.nucleic.DnaString`
- [x] 2.5 Update every file in `bio.algorithms` that imports DNA/RNA types to point to `bio.domain.nucleic.*` (touches `DnaNucleotides`, `DnaReverseComplement`, `RnaTranscription`, `RnaTranslation`, `HighestGc`)
- [x] 2.6 Update files in `bio.domain` that imported DNA/RNA types to point to `bio.domain.nucleic.*` (touches `FastaRecord` which references `DnaString`, `GcContent` which references `DnaString` and `DnaNucleotide`, `Codon` which references `RnaNucleotide` and `RnaString`)
- [x] 2.7 Update files in `bio.parsing` that import DNA/RNA types: `FastaParser` references `DnaString`
- [x] 2.8 Run `sbt clean compile test` — confirm 190 tests pass, zero warnings
- [x] 2.9 Commit: "Move DNA/RNA types into bio.domain.nucleic"

## 3. Create `bio.domain.protein` subdomain

- [x] 3.1 Create directories `src/main/scala/bio/domain/protein/` and `src/test/scala/bio/domain/protein/`
- [x] 3.2 Move source files into `bio.domain.protein`: `AminoAcid.scala`, `Codon.scala`, `CodonOutcome.scala`, `GeneticCode.scala`, `ProteinString.scala`, `ProteinError.scala`, `TranslationError.scala`. Update each file's `package` declaration to `bio.domain.protein`
- [x] 3.3 Inside `Codon.scala` and `GeneticCode.scala`, add explicit imports for `bio.domain.nucleic.{RnaNucleotide, RnaString}` (these previously resolved within the same package)
- [x] 3.4 Move corresponding test files: `AminoAcidSpec.scala`, `CodonSpec.scala`, `CodonOutcomeSpec.scala`, `GeneticCodeSpec.scala`, `ProteinStringSpec.scala`. Update test imports as needed
- [x] 3.5 Update files in `bio.algorithms` that import protein types: `RnaTranslation` imports `Codon`, `CodonOutcome`, `GeneticCode`, `ProteinString`, `TranslationError` — point these to `bio.domain.protein.*`
- [x] 3.6 Run `sbt clean compile test` — confirm 190 tests pass, zero warnings
- [x] 3.7 Commit: "Move protein and translation types into bio.domain.protein"

## 4. Create `bio.domain.genetics` subdomain

- [x] 4.1 Create directories `src/main/scala/bio/domain/genetics/` and `src/test/scala/bio/domain/genetics/`
- [x] 4.2 Move source files into `bio.domain.genetics`: `Genotype.scala`, `Population.scala`, `PopulationError.scala`. Update each file's `package` declaration
- [x] 4.3 Move corresponding test files: `GenotypeSpec.scala`, `PopulationSpec.scala`
- [x] 4.4 Update `bio.algorithms.MendelianInheritance` imports to reference `bio.domain.genetics.{Genotype, Population, PopulationError}`
- [x] 4.5 Run `sbt clean compile test` — confirm 190 tests pass, zero warnings
- [x] 4.6 Commit: "Move Mendelian genetics types into bio.domain.genetics"

## 5. Create `bio.domain.stats` subdomain

- [x] 5.1 Create directories `src/main/scala/bio/domain/stats/` and `src/test/scala/bio/domain/stats/`
- [x] 5.2 Move source files into `bio.domain.stats`: `Probability.scala`, `ProbabilityError.scala`. Update each file's `package` declaration
- [x] 5.3 Move corresponding test files: `ProbabilitySpec.scala`
- [x] 5.4 Update `bio.algorithms.MendelianInheritance` imports to reference `bio.domain.stats.{Probability, ProbabilityError}`
- [x] 5.5 Run `sbt clean compile test` — confirm 190 tests pass, zero warnings
- [x] 5.6 Commit: "Move probability types into bio.domain.stats"

## 6. Create `bio.domain.recurrence` subdomain

- [x] 6.1 Create directories `src/main/scala/bio/domain/recurrence/` and `src/test/scala/bio/domain/recurrence/`
- [x] 6.2 Move source files into `bio.domain.recurrence`: `RabbitProblem.scala`, `RabbitProblemError.scala`. Update each file's `package` declaration
- [x] 6.3 Move corresponding test files: `RabbitProblemSpec.scala`
- [x] 6.4 Update `bio.algorithms.FibonacciRabbits` imports to reference `bio.domain.recurrence.{RabbitProblem, RabbitProblemError}`
- [x] 6.5 Run `sbt clean compile test` — confirm 190 tests pass, zero warnings
- [x] 6.6 Commit: "Move recurrence types into bio.domain.recurrence"

## 7. Create `bio.domain.analysis` subdomain

- [x] 7.1 Create directories `src/main/scala/bio/domain/analysis/` and `src/test/scala/bio/domain/analysis/`
- [x] 7.2 Move source files into `bio.domain.analysis`: `GcContent.scala`, `GcContentError.scala`. Update each file's `package` declaration. `GcContent` retains its imports of `bio.domain.nucleic.{DnaNucleotide, DnaString}`
- [x] 7.3 Move corresponding test files: `GcContentSpec.scala`
- [x] 7.4 Update `bio.algorithms.HighestGc` imports to reference `bio.domain.analysis.{GcContent, GcContentError}`
- [x] 7.5 Update the spec scenario in `openspec/specs/gc-content/spec.md`: change `bio.domain.GcContent(50.0)` to `bio.domain.analysis.GcContent(50.0)` in the `Direct apply does not compile` scenario
- [x] 7.6 Update the matching `assertDoesNotCompile` call site in `src/test/scala/bio/domain/analysis/GcContentSpec.scala`: change `"""bio.domain.GcContent(50.0)"""` to `"""bio.domain.analysis.GcContent(50.0)"""` (and the same for the `.copy` assertion)
- [x] 7.7 Update the spec scenario in `openspec/specs/protein-translation/spec.md`: change `bio.domain.ProteinString("MAMA")` to `bio.domain.protein.ProteinString("MAMA")` in the `Direct apply does not compile` scenario
- [x] 7.8 Update the matching `assertDoesNotCompile` call sites in `src/test/scala/bio/domain/protein/ProteinStringSpec.scala`: change `"""bio.domain.ProteinString("MAMA")"""` to `"""bio.domain.protein.ProteinString("MAMA")"""` (and the same for the `.copy` assertion)
- [x] 7.9 Run `sbt clean compile test` — confirm 190 tests pass, zero warnings
- [x] 7.10 Commit: "Move GC analysis types into bio.domain.analysis; update assertDoesNotCompile paths"

## 8. Consolidate FASTA types into `bio.parsing`

- [x] 8.1 Move `FastaRecord.scala` and `FastaParseError.scala` from `src/main/scala/bio/domain/` into `src/main/scala/bio/parsing/`. Update each file's `package` declaration from `bio.domain` to `bio.parsing`
- [x] 8.2 Move `FastaRecordSpec.scala` from `src/test/scala/bio/domain/` to `src/test/scala/bio/parsing/`. Update test `package` declaration
- [x] 8.3 Update `FastaParser.scala` imports — it previously imported `bio.domain.{DnaString, FastaParseError, FastaRecord, SequenceError}`; now only the nucleic imports remain (`bio.domain.nucleic.DnaString`, `bio.domain.nucleic.SequenceError`) since `FastaRecord` and `FastaParseError` are in the same package
- [x] 8.4 Update `bio.algorithms.HighestGc` imports to reference `bio.parsing.FastaRecord` (was `bio.domain.FastaRecord`)
- [x] 8.5 Update test imports in `FastaParserSpec` (was `import bio.domain.{FastaParseError, FastaRecord, SequenceError}`; now `bio.parsing.{FastaParseError, FastaRecord}` plus `bio.domain.nucleic.SequenceError`) and in `HighestGcSpec` (FastaRecord moves to `bio.parsing`)
- [x] 8.6 Run `sbt clean compile test` — confirm 190 tests pass, zero warnings
- [x] 8.7 Commit: "Consolidate FASTA record types into bio.parsing"

## 9. Reorganize `bio.algorithms` into subdomains

- [x] 9.1 Create directories under `src/main/scala/bio/algorithms/` for `nucleic/`, `protein/`, `genetics/`, `recurrence/`, `analysis/`, and mirroring directories under `src/test/scala/bio/algorithms/`
- [x] 9.2 Move source files into `bio.algorithms.nucleic`: `DnaNucleotides.scala`, `DnaReverseComplement.scala`, `RnaTranscription.scala`. Update each file's `package` declaration
- [x] 9.3 Move corresponding test files into `bio.algorithms.nucleic`: `DnaNucleotidesSpec.scala`, `DnaReverseComplementSpec.scala`, `RnaTranscriptionSpec.scala`
- [x] 9.4 Move source files into `bio.algorithms.protein`: `RnaTranslation.scala`. Update `package` declaration. (Decision documented in design.md: subject-based classification places translation under protein, not nucleic)
- [x] 9.5 Move corresponding test file into `bio.algorithms.protein`: `RnaTranslationSpec.scala`
- [x] 9.6 Move source file into `bio.algorithms.genetics`: `MendelianInheritance.scala`. Update `package` declaration
- [x] 9.7 Move corresponding test file into `bio.algorithms.genetics`: `MendelianInheritanceSpec.scala`
- [x] 9.8 Move source file into `bio.algorithms.recurrence`: `FibonacciRabbits.scala`. Update `package` declaration
- [x] 9.9 Move corresponding test file into `bio.algorithms.recurrence`: `FibonacciRabbitsSpec.scala`
- [x] 9.10 Move source file into `bio.algorithms.analysis`: `HighestGc.scala`. Update `package` declaration
- [x] 9.11 Move corresponding test file into `bio.algorithms.analysis`: `HighestGcSpec.scala`
- [x] 9.12 Run `sbt clean compile test` — confirm 190 tests pass, zero warnings
- [x] 9.13 Commit: "Reorganize bio.algorithms into biology-first subdomains"

## 10. Final Verification

- [x] 10.1 Verify `src/main/scala/bio/domain/` contains only subdirectories (no loose `.scala` files)
- [x] 10.2 Verify `src/test/scala/bio/domain/` contains only subdirectories (no loose `.scala` files; only `src/test/scala/bio/DnaStringSpec.scala` remains at `bio/` level)
- [x] 10.3 Verify `src/main/scala/bio/algorithms/` contains only subdirectories
- [x] 10.4 Verify `src/test/scala/bio/algorithms/` contains only subdirectories
- [x] 10.5 Run `sbt clean compile` — confirm zero errors, zero warnings
- [x] 10.6 Run `sbt test` — confirm all 190 tests pass
- [x] 10.7 Cross-check imports: `grep -r "import bio.domain\." src` should now show only `import bio.domain.<subdomain>.X` references (no bare `bio.domain.X`)
- [x] 10.8 Cross-check FASTA consolidation: `grep -r "bio.domain.Fasta" src` returns no results
