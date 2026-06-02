## Why

Rosalind CONV ("Comparing Spectra with the Spectral Convolution") asks us to
compare two simplified mass spectra by computing their spectral convolution —
the multiset of all pairwise differences — and reporting the most common shift.
This shared-peaks measure is the foundation for spectrum alignment, and it is the
next problem (spec 74) in the framework's progression after PRTM/SPEC.

## What Changes

- Add a `MassMultiset` domain type: a validated multiset of positive real masses
  (size ≤ 200), built only through a smart constructor returning `Either`.
- Add a `SpectralConvolutionProblem` domain type bundling the two input multisets
  (the minuend S₁ and the subtrahend S₂).
- Add a `SpectralConvolution` result type carrying the largest multiplicity and
  the maximizing shift, with a `format` rendering the two-line Rosalind output
  (multiplicity, then the absolute value of the shift to five decimals).
- Add a pure `SpectralConvolution.convolve` algorithm that forms every difference
  `s₁ − s₂`, buckets them by value (rounded to five decimals to absorb floating
  error), and returns the largest bucket's multiplicity and representative shift.
- Add a `CONVProb` runner that reads the two whitespace-separated mass lines from
  `conv_data.txt`, solves the problem, and prints the result through `IO`.

## Capabilities

### New Capabilities
- `spectral-convolution`: validate two mass multisets, compute the spectral
  convolution, and report the largest multiplicity and maximizing shift.

### Modified Capabilities
<!-- None: this is a self-contained new capability. -->

## Impact

- New code: `bio.domain.protein.{MassMultiset, MassMultisetError,
  SpectralConvolutionProblem, SpectralConvolution}`,
  `bio.algorithms.protein.SpectralConvolution`, `bio.problems.CONVProb`.
- New tests mirroring those packages under `src/test`.
- `bio.Main` switches its active runner to `CONVProb`.
- Reads existing dataset `src/main/scala/resources/conv_data.txt`.
- No changes to existing domain types or algorithms.
