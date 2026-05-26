## Why

Spec 9 of the project brief — "Finding a Motif in DNA" — introduces the framework's first *positional* operation on a DNA sequence: identifying every location where a short pattern occurs as a contiguous substring of a longer one. Up to now, every algorithm has produced a scalar (a count, a transcribed string, a percentage, a protein, a record) or a single best record. Motif finding produces a *list of indices*, which is the shape of nearly every future sequence-search problem (orf finding, restriction-enzyme cut sites, primer matching, repeat detection). Establishing the convention now — 1-based positions, overlapping matches included, total function — pays off across the next dozen problems.

## What Changes

- **NEW** `MotifLocations` algorithm object in `bio.algorithms.analysis`
- **NEW** `MotifLocations.find(text: DnaString, motif: DnaString): Vector[Int]` — total function returning 1-based starting positions of every occurrence of `motif` as a contiguous substring of `text`. Overlapping matches are included. The result is sorted in ascending order (a natural consequence of the left-to-right scan).
- **NO** new domain types. The function returns a `Vector[Int]` documented as "1-based positions". This matches the framework's pragmatic precedent (e.g., `Population.from` takes raw `Int` counts; positions are bare `Int` with the convention documented).
- **NO** new error types. The function is total — every input pair produces a valid `Vector[Int]`. Edge cases (empty motif, motif longer than text, no matches, empty text) all return `Vector.empty` by convention.

## Capabilities

### New Capabilities

- `motif-finding`: The `MotifLocations.find(text: DnaString, motif: DnaString): Vector[Int]` algorithm. 1-based positions, overlapping matches included, total over all DnaString pairs. Empty motif returns `Vector.empty` by convention.

### Modified Capabilities

None.

## Impact

- New file: `src/main/scala/bio/algorithms/analysis/MotifLocations.scala`
- New test file: `src/test/scala/bio/algorithms/analysis/MotifLocationsSpec.scala`
- No changes to existing files
- No new dependencies
- No new domain types
- All existing 190 tests continue passing
