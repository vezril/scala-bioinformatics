## Context

Rosalind **CSET** gives an inconsistent `0/1` character table `C` on at most 100
taxa (each row is a character; each column is a taxon) and asks for a consistent
submatrix obtained by **deleting exactly one row**. Any valid solution is
accepted. Two characters' splits `S₁|S₁ᶜ`, `S₂|S₂ᶜ` *conflict* iff all four
intersections `S₁∩S₂`, `S₁∩S₂ᶜ`, `S₁ᶜ∩S₂`, `S₁ᶜ∩S₂ᶜ` are non-empty — the same
rule the just-archived CHBP capability uses inside its smart constructor. A
table is *consistent* iff no pair of its characters conflicts.

Deleting one row to remove all conflicts is exactly asking: is there a single
character that participates in **every** conflicting pair?

## Goals / Non-Goals

**Goals:**
- A validated `InconsistentCharacterSetProblem` (the rows) via a smart
  constructor returning `Either`, following the `sealed abstract case class`
  pattern (no leaking `apply`/`copy`).
- A pure `FixInconsistentCharacterSet.fix(problem): Option[ConsistentCharacterTable]`
  that deletes one conflicting row to yield a consistent table, or `None` if no
  single deletion works.
- A `ConsistentCharacterTable` result with `format: String` (rows newline-joined).
- An IO runner reading `cset_data.txt` and printing the repaired table.

**Non-Goals:**
- No minimum-deletion / multi-row repair — the problem fixes by deleting exactly
  one row. If a single deletion cannot make the table consistent, return `None`
  (Rosalind guarantees a single-row fix exists for its inputs).
- No canonical/sorted row ordering of the output; the retained rows keep their
  original input order (Rosalind grades the character *set*, not order, and
  accepts any valid solution).
- Not changing CHBP or extracting a shared conflict utility (avoid a
  cross-cutting refactor of archived code); the tiny rule is re-implemented
  locally.

## Decisions

### 1. The deleted row must lie in the first conflicting pair

A single row `r` removes all conflicts iff every conflicting pair contains `r`.
In particular `r` must belong to the **first** conflicting pair `(a, b)`, so the
only candidates are `a` and `b`. Enumerate all conflicting pairs once; if there
are none the table is already consistent. Otherwise test `a` then `b` (smallest
index first, for determinism): the first that occurs in *every* conflicting pair
is the row to delete. If neither qualifies, no single-row fix exists → `None`.
This is provably complete (any covering row is in the first edge) and avoids the
naive "try deleting each row, re-check all pairs" `O(m² · n)` rescan per row.
*Alternative considered:* try every row and recheck consistency. Rejected as
needlessly quadratic in the number of rows; the candidate argument reduces it to
two checks.

### 2. Conflict test via index sets

For each row, the `'1'`-columns and `'0'`-columns are taxon-index sets; two rows
conflict iff all four cross-intersections are non-empty. Rows share a validated
common width, so the sets are directly comparable. This mirrors the CHBP
constructor's check exactly.

### 3. `fix` returns `Option`, not a bare table

A single-row deletion is not guaranteed to fix an arbitrary table, so `fix`
returns `Option[ConsistentCharacterTable]` rather than pretending totality. The
runner prints the table when `Some`, or a clear no-solution message when `None`.
*Alternative considered:* a dedicated `Left` error case for "no fix". Rejected —
"no single-row fix" is an algorithmic outcome, not an input-validation failure,
so `Option` on the algorithm is the cleaner home for it.

### 4. Already-consistent input

If the validated table has no conflicts, `fix` still returns a single-row
deletion (the input minus its first row) so the contract "a submatrix formed by
deleting a single row" always holds when `Some`. Documented; Rosalind's inputs
are inconsistent so this branch is for totality/tests only.

## Risks / Trade-offs

- [The repaired table may differ from the sample output (a different valid row
  deleted)] → Acceptable: Rosalind accepts any valid solution. Tests assert the
  *result* is consistent and equals the input minus exactly one row, not byte
  equality with the sample.
- [Re-implementing the conflict rule duplicates CHBP's private check] → Small,
  self-contained rule; duplication is cheaper than refactoring archived code.
  Noted as a future unification opportunity if a third caller appears.
- [`fix` returning `None` on pathological input] → By design; the runner surfaces
  it explicitly rather than crashing, and a test pins the multi-conflict `None`
  case.
