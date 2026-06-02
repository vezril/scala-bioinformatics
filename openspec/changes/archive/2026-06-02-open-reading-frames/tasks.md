## 1. Error type

- [x] 1.1 RED: write `OpenReadingFrameProblemErrorSpec` asserting `SequenceTooLong(length, max)` exposes its `length` and `max` fields; confirm it fails to compile (`not found`).
- [x] 1.2 GREEN: create `bio.domain.protein.OpenReadingFrameProblemError` — `sealed trait` + `final case class SequenceTooLong(length: Int, max: Int)`; run the spec green.

## 2. OpenReadingFrameProblem domain type

- [x] 2.1 RED: write `OpenReadingFrameProblemSpec` — accepts a length-25 DnaString (Right, `dna` preserved), accepts empty DnaString (Right), rejects length-1001 DnaString (`Left(SequenceTooLong(1001, 1000))`), and `assertDoesNotCompile` for public `apply` and `copy`. Confirm RED.
- [x] 2.2 GREEN: create `bio.domain.protein.OpenReadingFrameProblem` as `sealed abstract case class OpenReadingFrameProblem(dna: DnaString)` with `MaxLength = 1000` and `from(dna): Either[OpenReadingFrameProblemError, OpenReadingFrameProblem]` rejecting length > 1000 → `SequenceTooLong`, building via `Right(new OpenReadingFrameProblem(dna) {})`. Run green.

## 3. OpenReadingFrames result type

- [x] 3.1 RED: write `OpenReadingFramesResultSpec` — `proteins` field exposure, `format` of two proteins (`MA`, `M`) → `"MA\nM"`, and empty result → `""`. Confirm RED.
- [x] 3.2 GREEN: create `bio.domain.protein.OpenReadingFrames` result — `final case class OpenReadingFrames(proteins: Vector[ProteinString])` with `format: String = proteins.map(_.value).mkString("\n")`. Run green.

## 4. OpenReadingFrames algorithm

- [x] 4.1 RED: write `OpenReadingFramesAlgoSpec` with scenarios — canonical Rosalind sample → the four proteins (`MLLGSFRLIPKETLIQVAGSSPCNLS`, `M`, `MGMTPRLGLESLLE`, `MTPRLGLESLLE`) via `theSameElementsAs`; `ATGAAA` → empty (start codon, no downstream stop); `CCCCCC` → empty (no start codon); `ATGTAAATGTAA` → exactly `M` once (dedup of identical proteins); nested ORF assertion (sample's `MGMTPRLGLESLLE` and `MTPRLGLESLLE` both present). Confirm RED.
- [x] 4.2 GREEN: create `bio.algorithms.protein.OpenReadingFrames`, aliasing the domain result via `import bio.domain.protein.{OpenReadingFrames => Result}`. Implement `find(problem): Result`: compute reverse complement via `DnaReverseComplement.reverseComplement`; for each strand, `RnaTranscription.transcribe` then scan frame offsets 0,1,2; build complete codons (step 3, ignore 1–2 nt remainder) via `RnaNucleotide.fromChar` + `Codon`, map through `GeneticCode.translate`; extract ORFs by, for each `AminoAcidProduct(AminoAcid.M)` index, `drop(i)`+`span` over `AminoAcidProduct`, keeping the run only when followed by a non-empty `Stop` tail, assembling with `ProteinString.fromAminoAcids`; `.distinct` and sort by `value` for determinism. Run green.
- [x] 4.3 REFACTOR: review for `var`/`while`/mutable collections (none — pure FP), extract small private helpers (per-frame codon build, ORF extraction), confirm `RnaNucleotide`/`Codon`/`GeneticCode` are used (no hardcoded codon tables). Run full `sbt test` green.

## 5. Runner

- [x] 5.1 Create `bio.problems.ORFProb` reading a single FASTA record from `orf_data.txt` via `FastaFileReader.read(Paths.get(...))`, taking `records.headOption.map(_.dna)` (error if none), building `OpenReadingFrameProblem.from`, running `OpenReadingFrames.find`, printing `format` via `IO.println`; all errors printed (never thrown).
- [x] 5.2 Wire `ORFProb.solve()` as the active runner in `bio.Main` (comment the prior active line), and run the full `sbt test` suite to confirm everything is green.
