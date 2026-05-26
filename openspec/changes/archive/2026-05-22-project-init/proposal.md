## Why

This project establishes the foundational Scala bioinformatics framework. There is no existing codebase — this change bootstraps the project with a working SBT build, Cats Effect integration, and the first bioinformatics algorithm (DNA nucleotide counting), demonstrating the framework pattern for future problems.

## What Changes

- New SBT project with Scala 2.13.18, Cats Effect 3.x, and ScalaTest
- Minimal `Main` entry point using `IOApp` to prove the stack compiles and runs
- `DnaNucleotides` capability for counting A, C, G, T occurrences in a DNA string
- Comprehensive `README.md` covering how to build, run, and test
- Full TDD implementation: failing tests written first, then implementation

## Capabilities

### New Capabilities

- `project-setup`: SBT configuration, Cats Effect Hello World entry point, and project scaffolding
- `dna-nucleotides`: Count occurrences of A, C, G, T in a DNA string of up to 1000 nt; returns four space-separated integers

### Modified Capabilities

## Impact

- Creates the project from scratch; no existing code is affected
- Introduces dependencies: `cats-effect`, `scalatest`
- All domain types modeled as ADTs; side effects isolated to `IO`
