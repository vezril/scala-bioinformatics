package bio.domain.graph

import bio.domain.nucleic.DnaNucleotide

/** One directed, symbol-labelled edge of a trie (Rosalind TRIE): an edge from
  * `parent` to `child` labelled by the nucleotide `symbol`.
  *
  * Node identities are integers (the root is `1`). The symbol is a `DnaNucleotide`
  * ADT value rather than a raw `Char`, so edges stay type-safe.
  *
  * Public constructor — `TrieEdge` carries no invariant beyond holding two integers
  * and a nucleotide, so no smart-constructor ceremony is needed (mirrors
  * [[DeBruijnEdge]]).
  */
final case class TrieEdge(parent: Int, child: Int, symbol: DnaNucleotide)
