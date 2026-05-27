# Scala Bioinformatics

A framework for solving bioinformatics problems in Scala, built with functional programming principles using Cats Effect.

This project has the goal of solving [Project Rosalind](https://rosalind.info/problems/tree-view/) problems using Claude AI for exploratory and learning.

## Prerequisites

- JDK 11 or higher
- [SBT](https://www.scala-sbt.org/) 1.x (`brew install sbt` on macOS)

## Building

```bash
sbt compile
```

## Running

```bash
sbt run
```

Expected output:

```
Hello, Bioinformatics!
```

## Testing

```bash
sbt test
```

All tests follow the Red-Green-Refactor TDD cycle and use ScalaTest's `AnyFunSpec` style.

## Project Structure

```
src/
  main/scala/bio/
    Main.scala                        # IOApp entry point
    domain/
      DnaString.scala                 # Validated DNA string ADT
      DnaError.scala                  # Domain error ADT
      NucleotideCounts.scala          # Nucleotide count result ADT
    algorithms/
      DnaNucleotides.scala            # DNA nucleotide counting algorithm
  test/scala/bio/
    MainSpec.scala
    DnaStringSpec.scala
    NucleotideCountsSpec.scala
    algorithms/
      DnaNucleotidesSpec.scala
```

## Features

### DNA Nucleotide Counting

Given a DNA string (uppercase A, C, G, T only; max 1000 characters), returns the count of each nucleotide.

**Example:**

```scala
import bio.domain.DnaString
import bio.algorithms.DnaNucleotides

val result = for {
  dna    <- DnaString.from("AGCTTTTCATTCTGACTGCAACGGGCAATATGTCTCTGTGTGGATTAAAAAAAGAGTGTCTGATAGCAGC")
  counts  = DnaNucleotides.count(dna)
} yield counts.format

// Right("20 12 17 21")
```

Output format: four space-separated integers in A C G T order.

## Design Principles

- **ADTs for domain types**: Invalid states are unrepresentable — `DnaString` can only be constructed via its smart constructor.
- **Pure function core**: `DnaNucleotides.count` is a pure function; `IO` is used only at the application boundary.
- **TDD**: Every behavior has a failing test before any implementation.

## License
No License - I do not own this code as I did not write it, an AI Agent did (Claude Code) besides a few things here and there. Do whatever you want with this. All specs were generated with OpenSpec, and are fully documented with the skill for the Agent.