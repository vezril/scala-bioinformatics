package bio.domain.protein

import bio.domain.nucleic.RnaNucleotide

/** The standard RNA genetic code: a pure total function mapping every one of the 64
  * possible codons to either an amino acid or a Stop signal.
  *
  * Implemented as an exhaustive pattern match so the compiler enforces that every codon
  * has a defined outcome. A `Map` lookup would lose that guarantee.
  */
object GeneticCode {

  // RNA nucleotides aliased to avoid the A/C/G letter clash with AminoAcid.
  import RnaNucleotide.{A => Ra, C => Rc, G => Rg, U => Ru}
  import AminoAcid._
  import CodonOutcome.{AminoAcidProduct, Stop}

  def translate(c: Codon): CodonOutcome = c match {
    // A * *
    case Codon(Ra, Ra, Ra) => AminoAcidProduct(K)
    case Codon(Ra, Ra, Rc) => AminoAcidProduct(N)
    case Codon(Ra, Ra, Rg) => AminoAcidProduct(K)
    case Codon(Ra, Ra, Ru) => AminoAcidProduct(N)
    case Codon(Ra, Rc, Ra) => AminoAcidProduct(T)
    case Codon(Ra, Rc, Rc) => AminoAcidProduct(T)
    case Codon(Ra, Rc, Rg) => AminoAcidProduct(T)
    case Codon(Ra, Rc, Ru) => AminoAcidProduct(T)
    case Codon(Ra, Rg, Ra) => AminoAcidProduct(R)
    case Codon(Ra, Rg, Rc) => AminoAcidProduct(S)
    case Codon(Ra, Rg, Rg) => AminoAcidProduct(R)
    case Codon(Ra, Rg, Ru) => AminoAcidProduct(S)
    case Codon(Ra, Ru, Ra) => AminoAcidProduct(I)
    case Codon(Ra, Ru, Rc) => AminoAcidProduct(I)
    case Codon(Ra, Ru, Rg) => AminoAcidProduct(M)
    case Codon(Ra, Ru, Ru) => AminoAcidProduct(I)
    // C * *
    case Codon(Rc, Ra, Ra) => AminoAcidProduct(Q)
    case Codon(Rc, Ra, Rc) => AminoAcidProduct(H)
    case Codon(Rc, Ra, Rg) => AminoAcidProduct(Q)
    case Codon(Rc, Ra, Ru) => AminoAcidProduct(H)
    case Codon(Rc, Rc, Ra) => AminoAcidProduct(P)
    case Codon(Rc, Rc, Rc) => AminoAcidProduct(P)
    case Codon(Rc, Rc, Rg) => AminoAcidProduct(P)
    case Codon(Rc, Rc, Ru) => AminoAcidProduct(P)
    case Codon(Rc, Rg, Ra) => AminoAcidProduct(R)
    case Codon(Rc, Rg, Rc) => AminoAcidProduct(R)
    case Codon(Rc, Rg, Rg) => AminoAcidProduct(R)
    case Codon(Rc, Rg, Ru) => AminoAcidProduct(R)
    case Codon(Rc, Ru, Ra) => AminoAcidProduct(L)
    case Codon(Rc, Ru, Rc) => AminoAcidProduct(L)
    case Codon(Rc, Ru, Rg) => AminoAcidProduct(L)
    case Codon(Rc, Ru, Ru) => AminoAcidProduct(L)
    // G * *
    case Codon(Rg, Ra, Ra) => AminoAcidProduct(E)
    case Codon(Rg, Ra, Rc) => AminoAcidProduct(D)
    case Codon(Rg, Ra, Rg) => AminoAcidProduct(E)
    case Codon(Rg, Ra, Ru) => AminoAcidProduct(D)
    case Codon(Rg, Rc, Ra) => AminoAcidProduct(A)
    case Codon(Rg, Rc, Rc) => AminoAcidProduct(A)
    case Codon(Rg, Rc, Rg) => AminoAcidProduct(A)
    case Codon(Rg, Rc, Ru) => AminoAcidProduct(A)
    case Codon(Rg, Rg, Ra) => AminoAcidProduct(G)
    case Codon(Rg, Rg, Rc) => AminoAcidProduct(G)
    case Codon(Rg, Rg, Rg) => AminoAcidProduct(G)
    case Codon(Rg, Rg, Ru) => AminoAcidProduct(G)
    case Codon(Rg, Ru, Ra) => AminoAcidProduct(V)
    case Codon(Rg, Ru, Rc) => AminoAcidProduct(V)
    case Codon(Rg, Ru, Rg) => AminoAcidProduct(V)
    case Codon(Rg, Ru, Ru) => AminoAcidProduct(V)
    // U * *
    case Codon(Ru, Ra, Ra) => Stop
    case Codon(Ru, Ra, Rc) => AminoAcidProduct(Y)
    case Codon(Ru, Ra, Rg) => Stop
    case Codon(Ru, Ra, Ru) => AminoAcidProduct(Y)
    case Codon(Ru, Rc, Ra) => AminoAcidProduct(S)
    case Codon(Ru, Rc, Rc) => AminoAcidProduct(S)
    case Codon(Ru, Rc, Rg) => AminoAcidProduct(S)
    case Codon(Ru, Rc, Ru) => AminoAcidProduct(S)
    case Codon(Ru, Rg, Ra) => Stop
    case Codon(Ru, Rg, Rc) => AminoAcidProduct(C)
    case Codon(Ru, Rg, Rg) => AminoAcidProduct(W)
    case Codon(Ru, Rg, Ru) => AminoAcidProduct(C)
    case Codon(Ru, Ru, Ra) => AminoAcidProduct(L)
    case Codon(Ru, Ru, Rc) => AminoAcidProduct(F)
    case Codon(Ru, Ru, Rg) => AminoAcidProduct(L)
    case Codon(Ru, Ru, Ru) => AminoAcidProduct(F)
  }
}
