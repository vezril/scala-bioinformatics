---
name: scala-bio-framework
description: Conventions for the scala-bioinformatics project (Scala 2.13.18 + Cats Effect 3 + ScalaTest). Use this skill whenever working in /Users/cference/Code/scala-bioinformatics, adding a new Rosalind-style bioinformatics problem (DNA, RNA, protein, sequence algorithms), creating a new domain type or algorithm under bio.domain / bio.algorithms, or running the OpenSpec change workflow (propose / apply / archive) in this codebase. Covers mandatory TDD Red-Green-Refactor sequencing, ADT-first domain modeling with smart constructors, the nucleotide companion pattern (validChars / fromChar / toChar), pure-function-core + IO-shell architecture, and unsafeFrom for trusted internal construction. Apply this skill even when the user does not name it explicitly — any work touching DNA, RNA, sequences, nucleotides, transcription, complement, or other Rosalind problems in this project should follow these conventions.
---

# Scala Bioinformatics Framework Conventions

A project-scoped skill capturing the architectural conventions of `scala-bioinformatics`. The goal of every new capability is to extend the framework while preserving the patterns established by `project-init`, `nucleotide-adts`, and `rna-transcription` (all archived under `openspec/changes/archive/`).

If anything in this skill conflicts with the user's explicit instructions or the OpenSpec artifacts for the current change, the user and the artifacts win. Use this skill as the default playbook, not as a rigid law.

## Stack and paths

- **Scala** 2.13.18 (not Scala 3 — pinned for now)
- **Cats Effect** 3.5.x (`IOApp`, `IO`)
- **ScalaTest** 3.2.x, `AnyFunSpec` style (`describe` / `it`, mirrors Given/When/Then)
- **SBT** 1.10.x, single root project, no submodules yet
- **JVM**: Java 26 (Corretto). Note: `build.sbt` includes `javaOptions += "-Dcats.effect.warnOnNonMainThreadDetected=false"` to silence a CE3 virtual-threads warning. Don't remove it.

```
src/main/scala/bio/
  Main.scala                    ← IOApp entry point
  domain/                       ← validated ADTs, no side effects
  algorithms/                   ← pure functions over domain types

src/test/scala/bio/
  domain/                       ← mirrors main/domain
  algorithms/                   ← mirrors main/algorithms

openspec/
  specs/                        ← canonical capability specs
  changes/<name>/               ← active change with proposal/design/specs/tasks
  changes/archive/              ← completed changes
```

## TDD is non-negotiable

The project prompt elevates this to a hard requirement, and every archived change has followed it. Treat the Red-Green-Refactor cycle as the unit of work, not the deliverable.

For every behavior:

1. **Red** — write the failing test first. Confirm it fails with `sbt test`. The failure mode matters: "not found: value X" (compile error) is acceptable Red; a passing test or unrelated error is not.
2. **Green** — write the smallest implementation that makes the test pass. No premature generalization.
3. **Refactor** — only after Green. Tighten the code; rerun `sbt test` to confirm still Green.

Task lists in `tasks.md` MUST explicitly sequence test → implementation → refactor for each behavior. The archived changes are good templates — see `openspec/changes/archive/2026-05-22-nucleotide-adts/tasks.md` for the canonical TDD pattern.

When running `sbt test` after each step, you don't need to inspect the full output every time — `tail -8` or `tail -15` is usually enough to confirm pass/fail and count.

## The framework's domain modeling pattern

Every Rosalind-style problem introduces some combination of:
- **Sequence types** (e.g. `DnaString`, `RnaString`, future `ProteinString`)
- **Alphabet types** (e.g. `DnaNucleotide`, `RnaNucleotide`, future `AminoAcid`)
- **Result types** (e.g. `DnaNucleotideCounts`, `RnaNucleotideCounts`)
- **Error types** (currently shared as `SequenceError`)

The conventions for each are:

### Sequence types

```scala
final class DnaString private (val value: String) extends AnyVal

object DnaString {
  private val MaxLength = 1000

  def from(s: String): Either[SequenceError, DnaString] =
    if (s.length > MaxLength)
      Left(SequenceError.ExceedsMaxLength(s.length))
    else
      s.find(!DnaNucleotide.validChars.contains(_)) match {
        case Some(ch) => Left(SequenceError.InvalidCharacter(ch))
        case None     => Right(new DnaString(s))
      }

  /** Internal constructor for trusted callers; bypasses validation. */
  private[bio] def unsafeFrom(s: String): DnaString = new DnaString(s)
}
```

Key points:
- **Value class** (`extends AnyVal`) for zero-overhead wrapping
- **Private constructor** — the only way to build a value is through `from` or `unsafeFrom`
- **`from` returns `Either[SequenceError, T]`** — fail-fast validation at the boundary
- **`unsafeFrom`** is `private[bio]` — for algorithm-internal use when validity is guaranteed by construction (e.g., the output of a transcription whose input was already valid)
- **MaxLength = 1000** matches the Rosalind problem constraints; revisit if a problem allows longer
- **`ValidChars` is derived from the alphabet companion** — never hardcoded. One source of truth per alphabet.

### Alphabet types (sealed nucleotide ADTs)

```scala
sealed trait DnaNucleotide
object DnaNucleotide {
  case object A extends DnaNucleotide
  case object C extends DnaNucleotide
  case object G extends DnaNucleotide
  case object T extends DnaNucleotide

  val validChars: Set[Char] = Set('A', 'C', 'G', 'T')

  def fromChar(c: Char): Option[DnaNucleotide] = c match {
    case 'A' => Some(A)
    case 'C' => Some(C)
    case 'G' => Some(G)
    case 'T' => Some(T)
    case _   => None
  }

  def toChar(n: DnaNucleotide): Char = n match {
    case A => 'A'
    case C => 'C'
    case G => 'G'
    case T => 'T'
  }
}
```

Key points:
- **Sealed trait + case objects** — exhaustive pattern matching enforced by the compiler
- **Companion exposes three things**: `validChars: Set[Char]` (for sequence validators), `fromChar: Char => Option[N]` (for algorithms reading from a String backing), `toChar: N => Char` (for algorithms producing a String backing)
- **No shared parent across molecule types.** `DnaNucleotide` and `RnaNucleotide` are *independent* hierarchies. Sharing a `Nucleotide` parent would admit `T` in an RNA context and defeat the type-safety goal. This was an explicit design decision in `nucleotide-adts` — preserve it.
- `toChar` may not be needed when first introducing an alphabet, but add it the moment any algorithm produces sequences of that alphabet. The transcription algorithm in `rna-transcription` needed it.

### Result types

```scala
final case class DnaNucleotideCounts(a: Int, c: Int, g: Int, t: Int) {
  def format: String = s"$a $c $g $t"
}
```

Key points:
- **Plain case class** with named fields per alphabet base
- **`format: String`** method for the Rosalind-style space-separated output
- **Path A naming**: prefix with the molecule (`DnaNucleotideCounts`, `RnaNucleotideCounts`). Avoid a generic `NucleotideCounts` — the field names are alphabet-specific.

### Invariant-bearing case classes — use `sealed abstract case class`

When a case class has a smart-constructor invariant (like `Population` requiring `total ≥ 2`, or `RabbitProblem` requiring `months ≥ 1`), do **NOT** use `final case class Foo private (...)`. Scala 2.13 synthesizes a public `apply` regardless of constructor visibility, leaking the invariant. Use this pattern instead:

```scala
sealed abstract case class Population(
    homozygousDominant: Int,
    heterozygous: Int,
    homozygousRecessive: Int
) {
  def total: Int = homozygousDominant + heterozygous + homozygousRecessive
}

object Population {
  def from(k: Int, m: Int, n: Int): Either[PopulationError, Population] =
    if (...) Left(...)
    else Right(new Population(k, m, n) {})   // anonymous subclass — only callable inside this file
}
```

Why it works: `sealed` restricts subclassing to this file; `abstract` prevents `apply`/`copy` synthesis; the anonymous subclass `{}` is the only construction path and only exists inside the file. Equals, hashCode, toString are still synthesized.

Verify the invariant with ScalaTest's `assertDoesNotCompile`:

```scala
it("cannot be constructed via a public companion apply") {
  assertDoesNotCompile("""bio.domain.Population(2, 2, 2)""")
}

it("does not expose a public copy method") {
  assertDoesNotCompile("""bio.domain.Population.from(2, 2, 2).toOption.get.copy(homozygousDominant = 99)""")
}
```

### Error types

`SequenceError` is currently shared across `DnaString` and `RnaString`:

```scala
sealed trait SequenceError
object SequenceError {
  final case class InvalidCharacter(ch: Char)    extends SequenceError
  final case class ExceedsMaxLength(length: Int) extends SequenceError
}
```

If a new error case needs molecule-specific semantics, that's the time to split `SequenceError` into `DnaError` / `RnaError` (with optional shared parent). Don't pre-split; only do it when the cases actually differ.

## Algorithm pattern

Algorithms live in `bio.algorithms`. They are **pure functions over domain types** — no `IO`, no side effects, no mutable state.

```scala
object DnaNucleotides {
  def count(dna: DnaString): DnaNucleotideCounts =
    dna.value.foldLeft(DnaNucleotideCounts(0, 0, 0, 0)) { (acc, ch) =>
      DnaNucleotide.fromChar(ch) match {
        case Some(DnaNucleotide.A) => acc.copy(a = acc.a + 1)
        case Some(DnaNucleotide.C) => acc.copy(c = acc.c + 1)
        case Some(DnaNucleotide.G) => acc.copy(g = acc.g + 1)
        case Some(DnaNucleotide.T) => acc.copy(t = acc.t + 1)
        case None                  => acc
      }
    }
}
```

```scala
object RnaTranscription {
  def transcribe(dna: DnaString): RnaString = {
    val transcribed = dna.value.iterator.flatMap { c =>
      DnaNucleotide.fromChar(c).map(dnaToRna).map(RnaNucleotide.toChar)
    }.mkString
    RnaString.unsafeFrom(transcribed)
  }

  private def dnaToRna(n: DnaNucleotide): RnaNucleotide = n match {
    case DnaNucleotide.A => RnaNucleotide.A
    case DnaNucleotide.C => RnaNucleotide.C
    case DnaNucleotide.G => RnaNucleotide.G
    case DnaNucleotide.T => RnaNucleotide.U
  }
}
```

Key points:
- **Dispatch on ADT values, not raw `Char` literals.** Use `fromChar` to lift, pattern-match on the ADT, use `toChar` to lower. The compiler enforces exhaustiveness; if a new nucleotide is added, the match becomes non-exhaustive and warns immediately.
- **`foldLeft` with `.copy`** when accumulating into a result ADT
- **`flatMap` + `mkString`** when producing a new sequence
- **No `var`, no mutable collections, no imperative loops** — review every algorithm for these and refactor before declaring done
- **Total functions stay total.** If the input is a validated `DnaString`, transcription cannot fail — return `RnaString`, not `Either[_, RnaString]`. Don't add `Either` "just in case."
- **`unsafeFrom` when producing a sequence from a guaranteed-valid `String`.** Re-validating wastes work and obscures the invariant. Document why the value is known valid.

## Adding a new capability — the standard playbook

1. **Read this skill and the archived changes** under `openspec/changes/archive/` — they are the canonical reference.

2. **Decide the scope.** Is this a new domain type, a new algorithm, or both? Does it touch existing types? Sketch the impact map before generating artifacts.

3. **Run `/opsx:propose <name>`** — creates `proposal.md`, `design.md`, `specs/<capability>/spec.md`, `tasks.md`. Use the existing archived changes as structural templates.

4. **Sequence the tasks in strict TDD order.** Each behavior gets test → Red confirmation → implementation → Green confirmation. Group by ADT first, algorithm last.

5. **Run `/opsx:apply <name>`** — implement task-by-task, marking each `[x]` immediately after completing it.

6. **Run `/opsx:archive <name>`** when all tasks complete. Sync delta specs to `openspec/specs/` during archive (the prompt offers this).

### Anti-patterns to avoid

- **Raw `Char` literals in algorithms** — always go through `fromChar` / `toChar`
- **`Set('A', 'C', 'G', 'T')` hardcoded in a sequence validator** — derive from the alphabet companion
- **Shared parent for DNA + RNA nucleotides** — they must remain independent types
- **Bare `println`** — all console output goes through `IO.println` or equivalent
- **`var`, mutable collections, imperative loops** — replace with `foldLeft`, `map`, `flatMap`, recursion
- **`Either` wrapping of total functions** — if every input has a defined output, return the bare type
- **Re-validating a known-valid sequence** — use `unsafeFrom` and document the invariant
- **Generic names that span molecules** (e.g. plain `NucleotideCounts`) — prefix with `Dna` / `Rna` / `Protein`
- **Skipping the Red step** — writing test and implementation together defeats the cycle's value

## OpenSpec workflow notes

The project uses an experimental OpenSpec workflow. Three skills are installed locally under `.claude/skills/`:

- `opsx:propose` — generate change artifacts from a description
- `opsx:apply` — implement tasks from a change
- `opsx:archive` — finalize and move to `archive/` with optional spec sync

Each archived change in this project shows the pattern. Notable conventions developed during use:

- **Capability names are kebab-case** and match `specs/<name>/spec.md` paths
- **MODIFIED specs require the full updated requirement content**, not just diffs — otherwise the archive-time merge loses detail
- **Delta-spec sync during archive** populates `openspec/specs/`. If the user skips it, the canonical specs fall out of sync — recommend syncing unless they have a reason not to.
- **`## Purpose` and `## Requirements` headers** are required for strict spec validation. The newer specs have them; the bootstrapped older ones (`dna-nucleotide`, `dna-nucleotides`, `project-setup`, `rna-nucleotide`, `rna-sequence`) currently don't. If you touch those specs, consider adding the headers.

## When SBT isn't installed

Java is usually present (Corretto via system JDK), but SBT may not be. `brew install sbt` is the documented path. The full install pulls openjdk + glib + harfbuzz transitive deps — takes a few minutes. If the user has a different package manager, ask before defaulting to brew.

## Memory

The project memory at `~/.claude/projects/-Users-cference-Code-scala-bioinformatics/memory/project-bioinformatics-framework.md` mirrors the high-level facts in this skill. The skill is the playbook; the memory is the quick reference. Keep them roughly in sync when major framework conventions change.
