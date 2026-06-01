## Context

Rosalind KMER returns the frequency of every length-`k` DNA word in a sequence,
ordered by the lexicographic order of those words. The framework already provides:

- `DnaString` (validated, value class, `from`/`unsafeFrom`, `MaxLength` large enough
  for 100 kbp inputs);
- `FastaFileReader.read(path): IO[Either[FastaError, List[FastaRecord]]]` yielding
  `FastaRecord(id, dna: DnaString)`;
- `EnumerateKmers.enumerate(KmerEnumerationProblem): KmerEnumeration` (LEXF), which
  produces all length-`k` strings over an ordered alphabet in lexicographic order.

KMER is therefore a *composition*: enumerate the ordered k-mers over the DNA
alphabet, slide a window over the sequence to tally counts, and emit one count per
ordered k-mer.

## Goals / Non-Goals

**Goals:**
- A validated `KmerCompositionProblem(dna, k)` with first-failure-wins smart
  constructor returning `Either[KmerCompositionProblemError, _]`.
- A pure, total `ComposeKmers.compose(problem): KmerComposition` that reuses
  `EnumerateKmers` for the canonical lexicographic ordering — no re-deriving order.
- A `KmerComposition` result type that renders space-separated counts via `format`.
- Match the canonical Rosalind 4-mer sample output exactly.

**Non-Goals:**
- No new alphabet types; the DNA alphabet `[A, C, G, T]` is fixed for this problem.
- No generalisation to protein/RNA composition (a later capability if needed).
- No streaming/perf tuning beyond a single linear pass; 100 kbp is small.

## Decisions

### Fix the alphabet to DNA `[A, C, G, T]`, taken in that (lexicographic) order
The Rosalind problem is stated over DNA, and `A < C < G < T` is the lexicographic
order. `ComposeKmers` hardcodes this alphabet rather than accepting an arbitrary one,
keeping the input bundle to `(dna, k)`. *Alternative considered:* a general
`(alphabet, k)` bundle — rejected as scope creep ("feature only"); the input is a
`DnaString`, so the alphabet is already implied.

### Reuse `EnumerateKmers` for the ordering rather than re-sorting
`ComposeKmers` builds the ordered k-mers by constructing a
`KmerEnumerationProblem.from(Vector('A','C','G','T'), k)` and calling
`EnumerateKmers.enumerate`. Because `KmerCompositionProblem` already validates
`1 ≤ k ≤ 10` and the DNA alphabet is non-empty, duplicate-free, and within the
symbol cap, that inner construction is *known* to succeed; the algorithm treats it
as total (documents the invariant, takes the `Right`). *Alternative considered:*
duplicating the fold inside `ComposeKmers` — rejected as non-DRY; LEXF exists to be
reused, and a single source of truth for lexicographic order avoids drift.

### Cap `k` at 10 to mirror the LEXF enumeration bound
The composition has `4^k` entries; the same bound that protects LEXF's Cartesian
product (`length ≤ 10`) applies here, and aligning the caps is exactly what makes
the inner `EnumerateKmers` call total. `KmerCompositionProblemError` therefore
carries `NonPositiveK(k)` and `KExceedsMaximum(k, max)`. DNA validity is handled
upstream by `DnaString`, so it is not re-encoded here.

### Counting via a sliding window and a tally map
`compose` slides a length-`k` window over `dna.value` (`s.sliding(k)`), folds the
windows into a `Map[String, Int]`, then maps the ordered k-mers to their counts
(default 0 for absent k-mers). When `s.length < k` there are no windows, so every
count is 0. This is pure (`foldLeft`, no `var`/mutable collections) and total — a
validated `DnaString` always yields a defined composition. The sum of counts equals
`max(0, |s| - k + 1)`.

### `KmerComposition` result type
`final case class KmerComposition(counts: Vector[Int])` with
`format: String = counts.mkString(" ")`, matching the Rosalind space-separated
integer output and the framework's result-type convention.

## Risks / Trade-offs

- [Inner `EnumerateKmers` call relies on an invariant established elsewhere] → The
  caps on `KmerCompositionProblem` (`1 ≤ k ≤ 10`) are chosen to guarantee the inner
  `KmerEnumerationProblem.from` succeeds; the algorithm documents why taking the
  `Right` is safe, mirroring the project's `unsafeFrom` philosophy.
- [`4^k` grows quickly] → Capping `k` at 10 bounds the output at ~1M entries; the
  Rosalind target is `k = 4` (256 entries), so this is comfortable.
- [Hardcoded DNA alphabet limits reuse] → Acceptable: this capability is DNA-specific
  by definition; a generalised composition can be a separate change later.
