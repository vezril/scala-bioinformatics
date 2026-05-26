## Context

This is a greenfield Scala bioinformatics framework. There is no prior codebase. The project must demonstrate the chosen stack (Scala 2.13.18, Cats Effect 3.x, ScalaTest) while establishing conventions that future bioinformatics capabilities will follow. TDD is non-negotiable: every behavior has a failing test before any implementation line is written.

## Goals / Non-Goals

**Goals:**
- Establish a working SBT multi-source-root build with correct Scala 2.13.18 settings
- Integrate Cats Effect `IOApp` as the entry point pattern for all side-effectful operations
- Implement `DnaNucleotides` as the canonical example of how a bioinformatics algorithm is structured: ADT input type, pure function core, `IO`-wrapped entrypoint
- Achieve full test coverage via ScalaTest with Given/When/Then scenarios including edge cases
- Ship a README that lets a new contributor run the project in one command

**Non-Goals:**
- UI, REST API, or HTTP layer of any kind
- Authentication or authorization
- Persistence (files, database)
- Parallelism beyond what Cats Effect provides by default
- Any bioinformatics capability beyond the two listed in the proposal

## Decisions

### ADTs for DNA input

**Decision**: Model the DNA string as a validated ADT (`DnaString`) rather than a raw `String`.

**Rationale**: The problem domain has a finite alphabet (A, C, G, T). Using a raw `String` leaks invalid states into the pure logic. An ADT with a smart constructor (returning `Either[ValidationError, DnaString]`) enforces the invariant at the boundary and makes illegal states unrepresentable, which is idiomatic Scala.

**Alternative considered**: Accept `String` everywhere and validate inside the counting function. Rejected because it pushes error handling into the algorithm layer rather than the domain boundary layer.

### Pure core + IO shell

**Decision**: Separate the nucleotide-counting logic into a pure function (`def count(dna: DnaString): NucleotideCounts`) and wrap side effects (reading input, printing output) in `IO`.

**Rationale**: Pure functions are trivially testable without `IO` machinery. The IO boundary stays at `Main` and any future CLI parsing layer. This is standard Cats Effect architecture.

### Single SBT subproject (no modules yet)

**Decision**: Start as a single root project, no submodules.

**Rationale**: The feature set does not yet warrant module boundaries. Premature modularization adds SBT complexity with no payoff. Add modules when a second capability with distinct dependency needs emerges.

**Alternative considered**: `core` + `app` modules. Rejected as over-engineering for a two-capability initial release.

### ScalaTest with FlatSpec or FunSpec style

**Decision**: Use `AnyFunSpec` (nested `describe`/`it` style) for readability of Given/When/Then scenarios.

**Rationale**: Mirrors the spec scenario naming naturally. `FlatSpec` is also acceptable but nesting improves readability for multi-scenario suites.

## Risks / Trade-offs

- [Scala 2.13 vs 3] Scala 3 is the current default for new projects, but the prompt specifies 2.13.18. No migration risk now; upgrading later will require minor syntax changes. → Accept as constraint.
- [Cats Effect version] CE3 changed the `IOApp` API substantially from CE2. Ensure all SBT dependencies pin CE3. → Pin `cats-effect` to `3.5.x` explicitly in `build.sbt`.
- [TDD discipline] Red-Green-Refactor requires discipline in task ordering. Tasks must explicitly sequence test creation before implementation. → Enforced in `tasks.md` sequencing.
