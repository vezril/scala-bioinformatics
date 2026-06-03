This will be a bioinformatics project. The goal of this project is to build a framework to be able to work bioinformatics problems.

Tech stack:
- Backend: Scala 2.13.18 + Cats Effect
- Tests: Scalatest

TDD is REQUIRED (non-negotiable):
- Follow Red–Green–Refactor.
- For every behavior in the specs, write tests FIRST (failing), then implement.
- The task list must explicitly sequence: tests → implementation → refactor.
- Always RUN tests after each implementation to ensure they pass.

Include a comprehensive README.md file in the root of the project that explains how to run the application and how to run the tests.

Features (minimal, more features to be added later, this is just to get started). Features will be described as Acceptance Criteria, use these for your TDD tests, additionally, think of at least two edge cases for the tests:

1. Project Setup
- SBT Config
- Minimal Hello World Application that uses Cats Effect

2. Counting DNA Nucleotides
- Problem: A string is simply an ordered collection of symbols selected from some alphabet and formed into a word; the length of a string is the number of symbols that it contains. An example of a length 21 DNA string (whose alphabet contains the symbols 'A', 'C', 'G', and 'T') is "ATGCTTCAGAAAGGTCTTACG."
- Given: A DNA string s of length at most 1000 nt. 
- Return: Four integers (separated by spaces) counting the respective number of times that the symbols 'A', 'C', 'G', and 'T' occur in s.

3. The Second Nucleic Acid
- Problem: An RNA string is a string formed from the alphabet containing 'A', 'C', 'G', and 'U'. Given a DNA string t corresponding to a coding strand, its transcribed RNA string u is formed by replacing all occurrences of 'T' in t with 'U' in u.
- Given: A DNA string t having length at most 1000 nt.
- Return: The transcribed RNA string of t
- Sample
  - Input: GATGGAACTTGACTACGTAAATT
  - Output: GAUGGAACUUGACUACGUAAAUU
4. The Secondary and Tertiary Structures of DNA
- Problem: In DNA strings, symbols 'A' and 'T' are complements of each other, as are 'C' and 'G'. The reverse complement of a DNA string s is the string s<sup>c</sup> formed by reversing the symbols of s, then taking the complement of each symbol (e.g., the reverse complement of "GTCA" is "TGAC").
- Given: A DNA string s of length at most 1000 bp. (bp = Base Pair)
- Return: The reverse complement s<sup>c</sup> of s
- Sample
  - Input: "AAAACCCGGT"
  - Output: "ACCGGGTTTT"
5. Introduction to Mendelian Inheritance
- Problem: Probability is the mathematical study of randomly occurring phenomena. We will model such a phenomenon with a random variable, which is simply a variable that can take a number of different distinct outcomes depending on the result of an underlying random process. For example, say that we have a bag containing 3 red balls and 2 blue balls. If we let X  represent the random variable corresponding to the color of a drawn ball, then the probability of each of the two outcomes is given by Pr(X=red)=35  and Pr(X=blue)=25 . Random variables can be combined to yield new random variables. Returning to the ball example, let Y model the color of a second ball drawn from the bag (without replacing the first ball). The probability of Y being red depends on whether the first ball was red or blue. To represent all outcomes of X and Y, we therefore use a probability tree diagram. This branching diagram represents all possible individual probabilities for X and Y, with outcomes at the endpoints ("leaves") of the tree. The probability of any outcome is given by the product of probabilities along the path from the beginning of the tree; see Figure 2 for an illustrative example. An event is simply a collection of outcomes. Because outcomes are distinct, the probability of an event can be written as the sum of the probabilities of its constituent outcomes. For our colored ball example, let A be the event "Y is blue." Pr(A) is equal to the sum of the probabilities of two different outcomes: Pr(X=blue and Y=blue)+Pr(X=red and Y=blue), or  3/10+1/10=2/5
- Given: Three positive integers k, m, and n, representing a population containing k+m+n organisms: k individuals are homozygous dominant for a factor, m are heterozygous, and n are homozygous recessive.
- Return: The probability that two randomly selected mating organisms will produce an individual possessing a dominant allele (and thus displaying the dominant phenotype). Assume that any two organisms can mate
- Sample
  - Input: 2 2 2
  - Output: 0.78333
- Considering simulating inheritance on a number of small test cases in order to check the solution
- Follow best practices established, such as using ADTs and functional programming
6. Rabbits and Recurrence Relations
- Problem: A sequence is an ordered collection of objects (usually numbers), which are allowed to repeat. Sequences can be finite or infinite. Two examples are the finite sequence (π,−2‾√,0,π) and the infinite sequence of odd numbers (1,3,5,7,9,…). We use the notation a<sub>n</sub> to represent the n-th term of a sequence. A recurrence relation is a way of defining the terms of a sequence with respect to the values of previous terms. In the case of Fibonacci's rabbits from the introduction, any given month will contain the rabbits that were alive the previous month, plus any new offspring. A key observation is that the number of offspring in any month is equal to the number of rabbits that were alive two months prior. As a result, if F<sub>n</sub> represents the number of rabbit pairs alive after the n-th month, then we obtain the Fibonacci sequence having terms F<sub>n</sub> that are defined by the recurrence relation F<sub>n</sub>=F<sub>n−1</sub>+F<sub>n−2</sub> (with F<sub>1</sub>=F<sub>2</sub>=1 to initiate the sequence). Although the sequence bears Fibonacci's name, it was known to Indian mathematicians over two millennia ago. When finding the n-th term of a sequence defined by a recurrence relation, we can simply use the recurrence relation to generate terms for progressively larger values of n. This problem introduces us to the computational technique of dynamic programming, which successively builds up solutions by using the answers to smaller cases.
- Given: Positive integers n≤40 and k≤5.
- Return:  The total number of rabbit pairs that will be present after n months, if we begin with 1 pair and in each generation, every pair of reproduction-age rabbits produces a litter of k rabbit pairs (instead of only 1 pair).
- Sample
  - Input: 5 3
  - Output: 19
7. Identifying Unkwown DNA Quickly
- Problem: The GC-content of a DNA string is given by the percentage of symbols in the string that are 'C' or 'G'. For example, the GC-content of "AGCTATAG" is 37.5%. Note that the reverse complement of any DNA string has the same GC-content. DNA strings must be labeled when they are consolidated into a database. A commonly used method of string labeling is called FASTA format. In this format, the string is introduced by a line that begins with '>', followed by some labeling information. Subsequent lines contain the string itself; the first line to begin with '>' indicates the label of the next string. n Rosalind's implementation, a string in FASTA format will be labeled by the ID "Rosalind_xxxx", where "xxxx" denotes a four-digit code between 0000 and 9999.
- Given: At most 10 DNA strings in FASTA format (of length at most 1 kbp each). (kbp = kilobasepair)
- Return: The ID of the string having the highest GC-content, followed by the GC-content of that string. Rosalind allows for a default error of 0.001 in all decimal answers unless otherwise stated; please see the note on absolute error below.
- Sample:
  - Input
  >Rosalind_6404
  CCTGCGGAAGATCGGCACTAGAATAGCCAGAACCGTTTCTCTGAGGCTTCCGGCCTTCCCTCCCACTAATAATTCTGAGG
  >Rosalind_5959
  CCATCGGTAGCGCATCCTTAGTCCAATTAAGTCCCTATCCAGGCGCTCCGCCGAAGGTCTATATCCATTTGTCAGCAGACACGC
  >Rosalind_0808 
  CCACCCTCGTGGTATGGCTAGGCATTCAGGAACCGGAGAACGCTTCAGACCAGCCCGGACTGGGAACCTGCGGGCAGTAGGTGGAAT
  - Rosalind_0808
    60.919540
- Note on Absolute Error
  - We say that a number x is within an absolute error of y to a correct solution if x is within y of the correct solution. For example, if an exact solution is 6.157892, then for x to be within an absolute error of 0.001, we must have that |x−6.157892|<0.001 , or 6.156892<x<6.158892. rror bounding is a vital practical tool because of the inherent round-off error in representing decimals in a computer, where only a finite number of decimal places are allotted to any number. After being compounded over a number of operations, this round-off error can become evident. As a result, rather than testing whether two numbers are equal with x=z, you may wish to simply verify that |x−z| is very small. The mathematical field of numerical analysis is devoted to rigorously studying the nature of computational approximation.
8. Translating RNA Into Protein
- Problem: The 20 commonly occurring amino acids are abbreviated by using 20 letters from the English alphabet (all letters except for B, J, O, U, X, and Z). Protein strings are constructed from these 20 symbols. Henceforth, the term genetic string will incorporate protein strings along with DNA strings and RNA strings. The RNA codon table dictates the details regarding the encoding of specific codons into the amino acid alphabet.
- Given: An RNA string s corresponding to a strand of mRNA (of length at most 10 kbp).
- Return: The protein string encoded by s.
- RNA Codon Table

| RNA Codon | Amino Acid |
|------|------------|
| UUU  | F          |
| CUU  | L          |
| AUU | I          |
|GUU| V          |
|UUC| F          |      
|CUC| L          |
|AUC| I          |
|GUC| V          |
|UUA| L          |
|CUA| L          |
|AUA| I          |
|GUA| V          |
|UUG| L          |
|CUG| L          |
|AUG| M          |
|GUG| V          |
|UCU| S          |
|CCU| P          |
|ACU| T          |
|GCU| A          |
|UCC| S          |
|CCC| P          |
|ACC| T          |
|GCC| A          |
|UCA| S          |
|CCA| P          |
|ACA| T          |
|GCA| A          |
|UCG| S          |
|CCG| P          |
|ACG| T          |
|GCG| A          |
|UAU| Y          |
|CAU| H          |
|AAU| N          |
|GAU| D          |
|UAC| Y          |
|CAC| H          |
|AAC| N          |
|GAC| D          |
|UAA| Stop       |
|CAA| Q          |
|AAA| K          |
|GAA| E          |
|UAG| Stop       |
|CAG| Q          |
|AAG| K          |
|GAG| E          |
|UGU| C          |
|CGU| R          |
|AGU| S          |
|GGU| G          |
|UGC|C|
|CGC|R|
|AGC|S|
|GGC|G|
|UGA|Stop|
|CGA|R|
|AGA|R|
|GGA|G|
|UGG|W|
|CGG|R|
|AGG|R|
|GGG|G|
- Sample Dataset
  - Input: AUGGCCAUGGCGCCCAGAACUGAGAUCAAUAGUACCCGUAUUAACGGGUGA
  - Output: MAMAPRTEINSTRING
- Additional Info: https://en.wikipedia.org/wiki/DNA_and_RNA_codon_tables

9. Finding a Motif in DNA
- Problem: Given two strings s and t, t is a substring of s if t is contained as a contiguous collection of symbols in s (as a result, t must be no longer than s). The position of a symbol in a string is the total number of symbols found to its left, including itself (e.g., the positions of all occurrences of 'U' in "AUGCUUCAGAAAGGUCUUACG" are 2, 5, 6, 15, 17, and 18). The symbol at position i of s is denoted by s[i]. A substring of s can be represented as s[j:k], where j and k represent the starting and ending positions of the substring in s ; for example, if s = "AUGCUUCAGAAAGGUCUUACG", then s[2:5] = "UGCU". The location of a substring s[j:k] is its beginning position j; note that t will have multiple locations in s if it occurs more than once as a substring of s (see the Sample below).
- Given: Two DNA strings s and t (each of length at most 1 kbp).
- Return: All locations of t as a substring of s.
- Sample Dataset
  - Input:
    GATATATGCATATACTT
    ATAT
  - Output: 2 4 10

10. Counting Point Mutations
- Problem: Given two strings s and t of equal length, the Hamming distance between s and t, denoted d<sub>H</sub>(s,t), is the number of corresponding symbols that differ in s and t.
- Given: Two DNA strings s and t of equal length (not exceeding 1 kbp).
- Return: The Hamming distance d<sub>H</sub>(s,t)
- Sample Dataset
  - Input:
    GAGCCTACTAACGGGAT
    CATCGTAATGACGGCCT
  - Output: 7

11. Calculating Expected Offspring
- Problem: For a random variable X taking integer values between 1 and n, the expected value of X is E(X)=∑<sup>n</sup><sub>k</sub>=1k×Pr(X=k). The expected value offers us a way of taking the long-term average of a random variable over a large number of trials. As a motivating example, let X be the number on a six-sided die. Over a large number of rolls, we should expect to obtain an average of 3.5 on the die (even though it's not possible to roll a 3.5). The formula for expected value confirms that E(X)=∑<sup>6</sup><sub>k=1</sub>k × Pr(X=k)=3.5 . More generally, a random variable for which every one of a number of equally spaced outcomes has the same probability is called a uniform random variable (in the die example, this "equal spacing" is equal to 1). We can generalize our die example to find that if X is a uniform random variable with minimum possible value a and maximum possible value b, then E(X)=(a+b)/2 . You may also wish to verify that for the dice example, if Y is the random variable associated with the outcome of a second die roll, then E(X+Y)=7.
- Given: Six nonnegative integers, each of which does not exceed 20,000. The integers correspond to the number of couples in a population possessing each genotype pairing for a given factor. In order, the six given integers represent the number of couples having the following genotypes:
  1. AA-AA
  2. AA-Aa
  3. AA-aa
  4. Aa-Aa
  5. Aa-aa
  6. aa-aa
- Return: The expected number of offspring displaying the dominant phenotype in the next generation, under the assumption that every couple has exactly two offspring.
- Sample Dataset
  - Input: 1 0 0 1 0 1
  - Output: 3.5

12. Mortal Fibonacci Rabbits
- Problem: Recall the definition of the Fibonacci numbers from “Rabbits and Recurrence Relations”, which followed the recurrence relation F<sub>n</sub>=F<sub>n−1</sub>+F<sub>n−2<.sub> and assumed that each pair of rabbits reaches maturity in one month and produces a single pair of offspring (one male, one female) each subsequent month. Our aim is to somehow modify this recurrence relation to achieve a dynamic programming solution in the case that all rabbits die out after a fixed number of months.
- Given: Positive integers n≤100 and m≤20.
- Return: The total number of pairs of rabbits that will remain after the n-th month if all rabbits live for m months.
- Sample Data
  - Input: 6 3
  - Output: 4

13. Inferring mRNA from Protein
- Problem: For positive integers a and n, a modulo n (written a mod n in shorthand) is the remainder when a is divided by n. For example, 29mod11=7 because 29=11×2+7. Modular arithmetic is the study of addition, subtraction, multiplication, and division with respect to the modulo operation. We say that a and b are congruent modulo n if a mod n = b mod n; in this case, we use the notation a ≡ b mod n. Two useful facts in modular arithmetic are that if a ≡ b mod n and c ≡ d mod n, then a+c ≡ b+d mod n and a×c ≡ b×d mod n. To check your understanding of these rules, you may wish to verify these relationships for a=29, b=73, c=10, d=32, and n=11. As you will see in this exercise, some Rosalind problems will ask for a (very large) integer solution modulo a smaller number to avoid the computational pitfalls that arise with storing such large numbers.
- Given: A protein string of length at most 1000 aa. (aa = amino acid)
- Return: The total number of different RNA strings from which the protein could have been translated, modulo 1,000,000. (Don't neglect the importance of the stop codon in protein translation.)
- Sample Dataset
  - Input: MA
  - Output: 12
- Hint: What does it mean intuitively to take a number modulo 1,000,000?

14. Independent Alleles
- Problem: Two events A and B are independent if Pr(A and B) is equal to Pr(A)×Pr(B). In other words, the events do not influence each other, so that we may simply calculate each of the individual probabilities separately and then multiply. More generally, random variables X and Y are independent if whenever A and B are respective events for X and Y , A and B are independent (i.e., Pr(A and B)=Pr(A)×Pr(B)). As an example of how helpful independence can be for calculating probabilities, let X and Y represent the numbers showing on two six-sided dice. Intuitively, the number of pips showing on one die should not affect the number showing on the other die. If we want to find the probability that X+Y is odd, then we don't need to draw a tree diagram and consider all possibilities. We simply first note that for X+Y to be odd, either X is even and Y is odd or X is odd and Y is even. In terms of probability, Pr(X+Y is odd)=Pr(X is even and Y is odd)+Pr(X is odd and Y is even). Using independence, this becomes [Pr(X is even)×Pr(Y is odd)]+[Pr(X is odd)×Pr(Y is even)], or (1/2)<sup>2</sup>+(1/2)<sup>2<=12. You can verify this result in
- Given: Two positive integers k (k≤7) and N (N≤2k). In this problem, we begin with Tom, who in the 0th generation has genotype Aa Bb. Tom has two children in the 1st generation, each of whom has two children, and so on. Each organism always mates with an organism having genotype Aa Bb.
- Return: The probability that at least N Aa Bb organisms will belong to the k-th generation of Tom's family tree (don't count the Aa Bb mates at each level). Assume that Mendel's second law holds for the factors.
- Sample Data:
  - Input: 2 1
  - Output: 0.684
- Mendels Second Law: Recall that Mendel's first law states that for any factor, an individual randomly assigns one of its two alleles to its offspring. Yet this law does not state anything regarding the relationship with which alleles for different factors will be inherited. After recording the results of crossing thousands of pea plants for seven years, Mendel surmised that alleles for different factors are inherited with no dependence on each other. This statement has become his second law, also known as the law of independent assortment. What does it mean for factors to be "assorted independently?" If we cross two organisms, then a shortened form of independent assortment states that if we look only at organisms having the same alleles for one factor, then the inheritance of another factor should not change. For example, Mendel's first law states that if we cross two Aa organisms, then 1/4 of their offspring will be aa, 1/4 will be AA, and 1/2 will be Aa. Now, say that we cross plants that are both heterozygous for two factors, so that both of their genotypes may be written as Aa Bb. Next, examine only Bb offspring: Mendel's second law states that the same proportions of AA, Aa, and aa individuals will be observed in these offspring. The same fact holds for BB and bb offspring. As a result, independence will allow us to say that the probability of an aa BB offspring is simply equal to the probability of an aa offspring times the probability of a BB organism, i.e., 1/16. Because of independence, we can also extend the idea of Punnett squares to multiple factors, as shown in Figure 1. We now wish to quantify Mendel's notion of independence using probability.

15. Enumerating Gene Orders
- Problem: A permutation of length n is an ordering of the positive integers {1,2,…,n}. For example, π=(5,3,2,1,4) is a permutation of length 5.
- Given: A positive integer n≤7.
- Return: The total number of permutations of length n, followed by a list of all such permutations (in any order).
- Sample Dataset
  - Input: 3
  - Output
    6
    1 2 3
    1 3 2
    2 1 3
    2 3 1
    3 1 2
    3 2 1

16. Partial Permutations
- Problem: A partial permutation is an ordering of only k objects taken from a collection containing n objects (i.e., k≤n). For example, one partial permutation of three of the first eight positive integers is given by (5,7,2). The statistic P(n,k) counts the total number of partial permutations of k  objects that can be formed from a collection of n objects. Note that P(n,n) is just the number of permutations of n objects, which we found to be equal to n!=n(n−1)(n−2)⋯(3)(2) in “Enumerating Gene Orders”.
- Given: Positive integers n and k such that 100≥n>0 and 10≥k>0.
- Return: The total number of partial permutations P(n,k), modulo 1,000,000.
- Sample Dataset
  - Input: 21 7
  - Output: 51200

17. Overlap Graphs
- Problem: A graph whose nodes have all been labeled can be represented by an adjacency list, in which each row of the list contains the two node labels corresponding to a unique edge. A directed graph (or digraph) is a graph containing directed edges, each of which has an orientation. That is, a directed edge is represented by an arrow instead of a line segment; the starting and ending nodes of an edge form its tail and head, respectively. The directed edge with tail v and head w is represented by (v,w) (but not by (w,v)). A directed loop is a directed edge of the form (v,v). For a collection of strings and a positive integer k, the overlap graph for the strings is a directed graph Ok in which each string is represented by a node, and string s is connected to string t with a directed edge when there is a length k suffix of s that matches a length k prefix of t, as long as s≠t; we demand s≠t to prevent directed loops in the overlap graph (although directed cycles may be present).
- Given: A collection of DNA strings in FASTA format having total length at most 10 kbp.
- Return: The adjacency list corresponding to O3. You may return edges in any order.
- Sample Dateset
  - Input:
  >Rosalind_0498
  AAATAAA
  >Rosalind_2391
  AAATTTT
  >Rosalind_2323
  TTTTCCC
  >Rosalind_0442
  AAATCCC
  >Rosalind_5013
  GGGTGGG
- Output:
  Rosalind_0498 Rosalind_2391
  Rosalind_0498 Rosalind_0442
  Rosalind_2391 Rosalind_2323
- Extra requirements: Provide a way to read a file in FASTA format to ingest into the proper datatypes

18. Concensus and Profile
- Problem: A matrix is a rectangular table of values divided into rows and columns. An m×n matrix has m rows and n columns. Given a matrix A, we write Ai,j to indicate the value found at the intersection of row i and column j. Say that we have a collection of DNA strings, all having the same length n. Their profile matrix is a 4×n matrix P in which P<sub>1,j</sub> represents the number of times that 'A' occurs in the jth position of one of the strings, P<sub>2,j</sub> represents the number of times that C occurs in the jth position, and so on (see below). A consensus string c is a string of length n formed from our collection by taking the most common symbol at each position; the jth symbol of c therefore corresponds to the symbol having the maximum value in the j-th column of the profile matrix. Of course, there may be more than one most common symbol, leading to multiple possible consensus strings.
- DNA Strings
  A T C C A G C T
  G G G C A A C T
  A T G G A T C T
  A A G C A A C C
  T T G G A A C T
  A T G C C A T T
  A T G G C A C T
- Profile
  A   5 1 0 0 5 5 0 0
  C   0 0 1 4 2 0 6 1
  G   1 1 6 3 0 1 0 0
  T   1 5 0 0 0 1 1 6
- Consensus
  A T G C A A C T
- Given: A collection of at most 10 DNA strings of equal length (at most 1 kbp) in FASTA format.
- Return: A consensus string and profile matrix for the collection. (If several possible consensus strings exist, then you may return any one of them.)
- Sample Dataset
  - Input:
  >Rosalind_1
  ATCCAGCT
  >Rosalind_2
  GGGCAACT
  >Rosalind_3
  ATGGATCT
  >Rosalind_4
  AAGCAACC
  >Rosalind_5
  TTGGAACT
  >Rosalind_6
  ATGCCATT
  >Rosalind_7
  ATGGCACT
  -Output:
  ATGCAACT
  A: 5 1 0 0 5 5 0 0
  C: 0 0 1 4 2 0 6 1
  G: 1 1 6 3 0 1 0 0
  T: 1 5 0 0 0 1 1 6

19. Introduction to Random Strings
- Problem: An array is a structure containing an ordered collection of objects (numbers, strings, other arrays, etc.). We let A[k] denote the k-th value in array A. You may like to think of an array as simply a matrix having only one row. A random string is constructed so that the probability of choosing each subsequent symbol is based on a fixed underlying symbol frequency. GC-content offers us natural symbol frequencies for constructing random DNA strings. If the GC-content is x, then we set the symbol frequencies of C and G equal to x/2 and the symbol frequencies of A and T equal to (1−x)/2. For example, if the GC-content is 40%, then as we construct the string, the next symbol is 'G'/'C' with probability 0.2, and the next symbol is 'A'/'T' with probability 0.3. In practice, many probabilities wind up being very small. In order to work with small probabilities, we may plug them into a function that "blows them up" for the sake of comparison. Specifically, the common logarithm of x (defined for x>0 and denoted log10(x)) is the exponent to which we must raise 10 to obtain x. See Figure 1 for a graph of the common logarithm function y=log10(x). In this graph, we can see that the logarithm of x-values between 0 and 1 always winds up mapping to y-values between −∞ and 0: x-values near 0 have logarithms close to −∞, and x-values close to 1 have logarithms close to 0. Thus, we will select the common logarithm as our function to "blow up" small probability values for comparison.
- Given: A DNA string s of length at most 100 bp and an array A containing at most 20 numbers between 0 and 1
- Return: An array B having the same length as A in which B[k] represents the common logarithm of the probability that a random string constructed with the GC-content found in A[k] will match s exactly.
- Sample Dataset:
  - Input:
    ACGATACAA
    0.129 0.287 0.423 0.476 0.641 0.742 0.783
  - Output:
    -5.737 -5.217 -5.263 -5.360 -5.958 -6.628 -7.009
- Hint: One property of the logarithm function is that for any positive numbers x and y, log10(x⋅y)=log10(x)+log10(y).

20. Completing a Tree
- Problem: An undirected graph is connected if there is a path connecting any two nodes. A tree is a connected (undirected) graph containing no cycles; this definition forces the tree to have a branching structure organized around a central core of nodes, just like its living counterpart. We have already grown familiar with trees in “Mendel's First Law”, where we introduced the probability tree diagram to visualize the outcomes of a random variable. In the creation of a phylogeny, taxa are encoded by the tree's leaves, or nodes having degree 1. A node of a tree having degree larger than 1 is called an internal node.
- Given: A positive integer n (n≤1000) and an adjacency list corresponding to a graph on n nodes that contains no cycles.
- Return: The minimum number of edges that can be added to the graph to produce a tree.
- Sample Dataset
  - Input:
    10
    1 2
    2 8
    4 10
    5 9
    6 10
    7 9
  - Output 
    3
- Extra info: After solving this problem, a standard mathematical exercise for the technically minded is to verify that every tree having 2 or more nodes must contain at least two leaves.

21. RNA Splicing
- Problem: After identifying the exons and introns of an RNA string, we only need to delete the introns and concatenate the exons to form a new string ready for translation.
- Given: A DNA string s (of length at most 1 kbp) and a collection of substrings of s acting as introns. All strings are given in FASTA format.
- Return: A protein string resulting from transcribing and translating the exons of s. (Note: Only one solution will exist for the dataset provided.)
- Sample Dataset
  - Input
  >Rosalind_10
  ATGGTCTACATAGCTGACAAACAGCACGTAGCAATCGGTCGAATCTCGAGAGGCATATGGTCACATGATCGGTCGAGCGTGTTTCAAAGTTTGCGCCTAG
  >Rosalind_12
  ATCGGTCGAA
  >Rosalind_15
  ATCGGTCGAGCGTGT
  -Ouput: MVYIADKQHVASREAYGHMFKVCA

22. Counting Subsets
- Problem: A set is the mathematical term for a loose collection of objects, called elements. Examples of sets include {the moon, the sun, Wilford Brimley} and ℝ , the set containing all real numbers. We even have the empty set, represented by ∅ or {}, which contains no elements at all. Two sets are equal when they contain the same elements. In other words, in contrast to permutations, the ordering of the elements of a set is unimportant (e.g., {the moon, the sun, Wilford Brimley} is equivalent to {Wilford Brimley, the moon, the sun}). Sets are not allowed to contain duplicate elements, so that {Wilford Brimley, the sun, the sun} is not a set. We have already used sets of 2 elements to represent edges from a graph. A set A is a subset of B if every element of A is also an element of B, and we write A ⊆ B . For example, {the sun, the moon} ⊆ {the sun, the moon, Wilford Brimley}, and ∅ is a subset of every set (including itself!). As illustrated in the biological introduction, we can use subsets to represent the collection of taxa possessing a character. However, the number of applications is endless; for example, an event in probability can now be defined as a subset of the set containing all possible outcomes. Our first question is to count the total number of possible subsets of a given set.
- Given: A positive integer n (n≤1000).
- Return: The total number of subsets of {1,2,…,n} modulo 1,000,000.
- Sample Dataset
  - Input: 3
  - Output: 8
- Hint: What does counting subsets have to do with characters and "ON"/"OFF" switches?

23. Introduction to Alternative Splicing
- Problem: In “Counting Subsets”, we saw that the total number of subsets of a set S containing n elements is equal to 2n. However, if we intend to count the total number of subsets of S having a fixed size k, then we use the combination statistic C(n,k), also written (<sup>n</sup><sub>k</sub>).
- Given: Positive integers n and m with 0 ≤ m ≤ n ≤ 2000.
- Return: The sum of combinations C(n,k) for all k satisfying m ≤ k ≤ n, modulo 1,000,000. In shorthand, ∑<sup>n</sup><sub>k</sub>=m(<sup>n</sup><sub>k</sub>).
- Sample Dataset:
  - Input: 6 3
  - Output: 42

24. Independent Segregation of Chromosomes
- Problem: Consider a collection of coin flips. One of the most natural questions we can ask is if we flip a coin 92 times, what is the probability of obtaining 51 "heads", vs. 27 "heads", vs. 92 "heads"? Each coin flip can be modeled by a uniform random variable in which each of the two outcomes ("heads" and "tails") has probability equal to 1/2. We may assume that these random variables are independent (see “Independent Alleles”); in layman's terms, the outcomes of the two coin flips do not influence each other. A binomial random variable X takes a value of k if n consecutive "coin flips" result in k total "heads" and n−k total "tails." We write that X ∈ Bin(n,1/2).
- Given: A positive integer n≤50.
- Return: An array A of length 2n in which A[k] represents the common logarithm of the probability that two diploid siblings share at least k of their 2n chromosomes (we do not consider recombination for now).
- Sample Dataset
  - Input: 5
  - Output:
    0.000 -0.004 -0.024 -0.082 -0.206 -0.424 -0.765 -1.262 -1.969 -3.010

25. Counting Disease Carriers
- Problem: To model the Hardy-Weinberg principle, assume that we have a population of N diploid individuals. If an allele is in genetic equilibrium, then because mating is random, we may view the 2N chromosomes as receiving their alleles uniformly. In other words, if there are m dominant alleles, then the probability of a selected chromosome exhibiting the dominant allele is simply p=m/(2N). Because the first assumption of genetic equilibrium states that the population is so large as to be ignored, we will assume that N is infinite, so that we only need to concern ourselves with the value of p.
- Given: An array A for which A[k] represents the proportion of homozygous recessive individuals for the k-th Mendelian factor in a diploid population. Assume that the population is in genetic equilibrium for all factors.
- Return: An array B having the same length as A in which B[k] represents the probability that a randomly selected individual carries at least one copy of the recessive allele for the k-th factor.
- Sample Dataset
  - Input: 0.1 0.25 0.5
  - Output: 0.532 0.75 0.914

26. The Wright-Fisher Model of Genetic Drift
- Problem: Consider flipping a weighted coin that gives "heads" with some fixed probability p (i.e., p is not necessarily equal to 1/2). We generalize the notion of binomial random variable from “Independent Segregation of Chromosomes” to quantify the sum of the weighted coin flips. Such a random variable X takes a value of k if a sequence of n independent "weighted coin flips" yields k "heads" and n−k "tails." We write that X ∈ Bin(n,p). To quantify the Wright-Fisher Model of genetic drift, consider a population of N diploid individuals, whose 2N chromosomes possess m copies of the dominant allele. As in “Counting Disease Carriers”, set p=m/(2N). Next, recall that the next generation must contain exactly N individuals. These individuals' 2N alleles are selected independently: a dominant allele is chosen with probability p, and a recessive allele is chosen with probability 1−p.
- Given: Positive integers N ( N≤ 7), m (m ≤ 2N), g (g ≤ 6) and k (k ≤ 2N).
- Return: The probability that in a population of N diploid individuals initially possessing m copies of a dominant allele, we will observe after g generations at least k copies of a recessive allele. Assume the Wright-Fisher model.
- Sample Dataset
  - Input: 4 6 2 1
  - Output: 0.772

27. Wright-Fisher's Expected Behaviour
- Problem: In “The Wright-Fisher Model of Genetic Drift”, we generalized the concept of a binomial random variable Bin(n,p) as a "weighted coin flip." It is only natural to calculate the expected value of such a random variable.  For example, in the case of unweighted coin flips (i.e., p=1/2), our intuition would indicate that E(Bin(n,1/2)) is n/2; what should be the expected value of a binomial random variable?
- Given: A positive integer n (n ≤ 1000000) followed by an array P of length m (m ≤ 20) containing numbers between 0 and 1. Each element of P can be seen as representing a probability corresponding to an allele frequency.
- Return: An array B of length m for which B[k] is the expected value of Bin(n,P[k]); in terms of Wright-Fisher, it represents the expected allele frequency of the next generation.
- Sample Dataset:
  - Input:
    17
    0.1 0.2 0.3
  - Output:
    1.7 3.4 5.1

28. The Founder Effect and Genetic Drift
- Given: Two positive integers N and m, followed by an array A containing k integers between 0 and 2N. A[j] represents the number of recessive alleles for the j-th factor in a population of N diploid individuals.
- Return: An m×k matrix B for which B<sub>i,j</sub> represents the common logarithm of the probability that after i generations, no copies of the recessive allele for the j-th factor will remain in the population. Apply the Wright-Fisher model.
- Sample Dataset:
  - Input
    4 3
    0 1 2
  - Output:
    0.0 -0.463935575821 -0.999509892866
    0.0 -0.301424998891 -0.641668367342
    0.0 -0.229066698008 -0.485798552456

29. Counting Phylogenetic Ancestors
- Problem: A binary tree is a tree in which each node has degree equal to at most 3. The binary tree will be our main tool in the construction of phylogenies. A rooted tree is a tree in which one node (the root) is set aside to serve as the pinnacle of the tree. A standard graph theory exercise is to verify that for any two nodes of a tree, exactly one path connects the nodes. In a rooted tree, every node v will therefore have a single parent, or the unique node w such that the path from v to the root contains {v,w}. Any other node x adjacent to v is called a child of v because v must be the parent of x; note that a node may have multiple children. In other words, a rooted tree possesses an ordered hierarchy from the root down to its leaves, and as a result, we may often view a rooted tree with undirected edges as a directed graph in which each edge is oriented from parent to child. We should already be familiar with this idea; it's how the Rosalind problem tree works! Even though a binary tree can include nodes having degree 2, an unrooted binary tree is defined more specifically: all internal nodes have degree 3. In turn, a rooted binary tree is such that only the root has degree 2 (all other internal nodes have degree 3).
- Given: A positive integer n (3 ≤ n ≤ 10000).
- Return: The number of internal nodes of any unrooted binary tree having n leaves.
- Sample Dataset:
  - Input: 4
  - Sample Output: 2

30. Distances in Trees
- Problem: Newick format is a way of representing trees even more concisely than using an adjacency list, especially when dealing with trees whose internal nodes have not been labeled. First, consider the case of a rooted tree T. A collection of leaves v<sub>1</sub>,v<sub>2</sub>,…,v<sub>n</sub> of T are neighbors if they are all adjacent to some internal node u. Newick format for T is obtained by iterating the following key step: delete all the edges {v<sub>i</sub>,u} from T and label u with (v<sub>1</sub>,v<sub>2</sub>,…,v<sub>n</sub>)u. This process is repeated all the way to the root, at which point a semicolon signals the end of the tree. A number of variations of Newick format exist. First, if a node is not labeled in T, then we simply leave blank the space occupied by the node. In the key step, we can write (v<sub>1</sub>,v<sub>2</sub>,…,v<sub>n</sub>) in place of (v<sub>1</sub>,v<sub>2</sub>,…,v<sub>n<.sub>)u if the v<sub>i</sub> are labeled; if none of the nodes are labeled, we can write (,,…,). A second variation of Newick format occurs when T is unrooted, in which case we simply select any internal node to serve as the root of T. A particularly peculiar case of Newick format arises when we choose a leaf to serve as the root. Note that there will be a large number of different ways to represent T in Newick format;
- Given: A collection of n trees (n ≤ 40) in Newick format, with each tree containing at most 200 nodes; each tree T<sub>k</sub> is followed by a pair of nodes x<sub>k</sub> and y<sub>k</sub> in T<sub>k</sub>.
- Return: A collection of n positive integers, for which the k th integer represents the distance between x<sub>k</sub> and y<sub>k</sub> in T<sub>k</sub>.
- Sample Dataset:
  - Input:
    (cat)dog;
    dog cat

    (dog,cat);
    dog cat
  - Output: 1 2

31. Speeding Up Motif Finding
- Problem: A prefix of a length n string s is a substring s[1:j]; a suffix of s is a substring s[k:n]. The failure array of s is an array P of length n for which P[k] is the length of the longest substring s[j:k]that is equal to some prefix s[1:k−j+1], where j cannot equal 1 (otherwise, P[k] would always equal). By convention, P[1]=0.
- Given: A DNA string s (of length at most 100 kbp) in FASTA format.
- Return: The failure array of s
- Sample Dataset
  - Input:
  >Rosalind_87
  CAGCATGGTATCACAGCAGAG
  - Output: 0 0 0 1 2 0 0 0 0 0 0 1 2 1 2 3 4 5 3 0 0
- Extra info: If you would like a more precise technical explanation of the Knuth-Morris-Pratt algorithm, please take a look at this site

32. Creating a Character Table
- Problem: Given a collection of n taxa, any subset S of these taxa can be seen as encoding a character that divides the taxa into the sets S and S<sup>c</sup>; we can represent the character by S∣S<sup>c</sup>, which is called a split. Alternately, the character can be represented by a character array A of length n for which A[j]=1 if the jth taxon belongs to S and A[j]=0 if the jth taxon belongs to S<sup>c</sup> (recall the "ON"/"OFF" analogy from “Counting Subsets”). At the same time, observe that the removal of an edge from an unrooted binary tree produces two separate trees, each one containing a subset of the original taxa. So each edge may also be encoded by a split S∣Sc . A trivial character isolates a single taxon into a group of its own. The corresponding split S∣S<sup>c</sup> must be such that S or S<sup>c</sup> contains only one element; the edge encoded by this split must be incident to a leaf of the unrooted binary tree, and the array for the character contains exactly one 0 or exactly one 1. Trivial characters are of no phylogenetic interest because they fail to provide us with information regarding the relationships of taxa to each other. All other characters are called nontrivial characters (and the associated splits are called nontrivial splits). A character table is a matrix C in which each row represents the array notation for a nontrivial character. That is, entry C<sub>i,j</sub> denotes the "ON"/"OFF" position of the ith character with respect to the jth taxon.
- Given: n unrooted binary tree T in Newick format for at most 200 species taxa.
- Return: A character table having the same splits as the edge splits of T. The columns of the character table should encode the taxa ordered lexicographically; the rows of the character table may be given in any order. Also, for any given character, the particular subset of taxa to which 1s are assigned is arbitrary.
- Sample Dataset:
  - Input:
    (dog,((elephant,mouse),robot),cat);
  - Output:
    00110
    00111

33. Creating a Character Table from Genetic Strings
- Problem: A collection of strings is characterizable if there are at most two possible choices for the symbol at each position of the strings.
- Given: A collection of at most 100 characterizable DNA strings, each of length at most 300 bp.
- Return: A character table for which each nontrivial character encodes the symbol choice at a single position of the strings. (Note: the choice of assigning '1' and '0' to the two states of each SNP in the strings is arbitrary.)
- Sample Dataset
  - Input
    ATGCTACC
    CGTTTACC
    ATTCGACC
    AGTCTCCC
    CGTCTATC
  - Output:
    10110
    10100
- Note: Recall that the character table does not encode trivial characters.

34. Perfect Matchings and RNA Secondary Structures
- Problem: A matching in a graph G is a collection of edges of G for which no node belongs to more than one edge in the collection. See Figure 2 for examples of matchings. If G contains an even number of nodes (say 2n), then a matching on G is perfect if it contains n edges, which is clearly the maximum possible. An example of a graph containing a perfect matching is shown in Figure 3. First, let K<sub>n</sub> denote the complete graph on 2n labeled nodes, in which every node is connected to every other node with an edge, and let p<sub>n</sub> denote the total number of perfect matchings in K<sub>n</sub>. For a given node x , there are 2n−1 ways to join x to the other nodes in the graph, after which point we must form a perfect matching on the remaining 2n−2 nodes. This reasoning provides us with the recurrence relation p<sub>n</sub>=(2n−1)⋅p<sub>n−1</sub>; using the fact that p<sub>1</sub> is 1, this recurrence relation implies the closed equation pn=(2n−1)(2n−3)(2n−5)⋯(3)(1). Given an RNA string s=s<sub>1</sub>…s<sub>n</sub> , a bonding graph for s is formed as follows. First, assign each symbol of s to a node, and arrange these nodes in order around a circle, connecting them with edges called adjacency edges. Second, form all possible edges {A, U} and {C, G}, called basepair edges; we will represent basepair edges with dashed edges, as illustrated by the bonding graph in Figure 4. Note that a matching contained in the basepair edges will represent one possibility for base pairing interactions in s , as shown in Figure 5. For such a matching to exist, s must have the same number of occurrences of 'A' as 'U' and the same number of occurrences of 'C' as 'G'.
- Given: An RNA string s of length at most 80 bp having the same number of occurrences of 'A' as 'U' and the same number of occurrences of 'C' as 'G'.
- Return: The total possible number of perfect matchings of basepair edges in the bonding graph of s
- Sample Dataset
  - Input
  >Rosalind_23
  AGCUAGUCAU
  - Output: 12

35. Catalan Numbers and RNA Secondary Structures
- Problem: A matching in a graph is noncrossing if none of its edges cross each other. If we assume that the n nodes of this graph are arranged around a circle, and if we label these nodes with positive integers between 1 and n, then a matching is noncrossing as long as there are not edges {i,j} and {k,l} such that i<k<j<l. A noncrossing matching of basepair edges in the bonding graph corresponding to an RNA string will correspond to a possible secondary structure of the underlying RNA strand that lacks pseudoknots, as shown in Figure 3. In this problem, we will consider counting noncrossing perfect matchings of basepair edges. As a motivating example of how to count noncrossing perfect matchings, let c<sub>n</sub> denote the number of noncrossing perfect matchings in the complete graph K2n. After setting c<sub>0</sub>=1, we can see that c<sub>1</sub> should equal 1 as well. As for the case of a general n, say that the nodes of K<sub>2n</sub> are labeled with the positive integers from 1 to 2n. We can join node 1 to any of the remaining 2n−1 nodes; yet once we have chosen this node (say m), we cannot add another edge to the matching that crosses the edge {1,m}. As a result, we must match all the edges on one side of {1,m} to each other. This requirement forces m to be even, so that we can write m=2k for some positive integer k . There are 2k−2 nodes on one side of {1,m} and 2n−2k nodes on the other side of {1,m}, so that in turn there will be c<sub>k−1</sub>⋅c<sub>n−k</sub> different ways of forming a perfect matching on the remaining nodes of K<sub>2n</sub>. If we let m vary over all possible n−1 choices of even numbers between 1 and 2n, then we obtain the recurrence relation c<sub>n</sub>=∑<sup>n</sup><sub>k=1</sub>c<sub>k−1</sub>⋅c<sub>n−k</sub>. The resulting numbers c<sub>n</sub> counting noncrossing perfect matchings in K<sub>2n</sub> are called the Catalan numbers, and they appear in a huge number of other settings. See Figure 4 for an illustration counting the first four Catalan numbers.
- Given: An RNA string s having the same number of occurrences of 'A' as 'U' and the same number of occurrences of 'C' as 'G'. The length of the string is at most 300 bp.
- Return: The total number of noncrossing perfect matchings of basepair edges in the bonding graph of s, modulo 1,000,000.
- Sample Dataset:
  - Input
  >Rosalind_57
  AUAU
  - Output: 2
- Hint: Write a function that counts Catalan numbers via dynamic programming. How can we modify this function to apply to our given problem?

36. Motzkin Numbers and RNA Secondary Structures
- Problem: Similarly to our definition of the Catalan numbers, the n-th Motzkin number m<sub>n</sub> counts the number of ways to form a (not necessarily perfect) noncrossing matching in the complete graph K<sub>n</sub> containing n nodes. For example, Figure 1 demonstrates that m<sub>5</sub>=21. Note in this figure that technically, the "trivial" matching that contains no edges at all is considered to be a matching, because it satisfies the defining condition that no two edges are incident to the same node. How should we compute the Motzkin numbers? As with Catalan numbers, we will take m<sub>0</sub>=m<sub>1</sub>=1. To calculate m<sub>n</sub> in general, assume that the nodes of K<sub>n</sub> are labeled around the outside of a circle with the integers between 1 and n, and consider node 1, which may or may not be involved in a matching. If node 1 is not involved in a matching, then there are m<sub>n−1</sub> ways of matching the remaining n−1 nodes. If node 1 is involved in a matching, then say it is matched to node k: this leaves k−2 nodes on one side of edge {1,k} and n−k nodes on the other side; as with the Catalan numbers, no edge can connect the two sides, which gives us m<sub>k−2</sub>⋅m<sub>n−k</sub> ways of matching the remaining edges. Allowing k to vary between 2 and n yields the following recurrence relation for the Motzkin numbers: m<sub>n</sub>=m<sub>n−1</sub>+∑<sup>n</sup><sub>k=2</sub>m<sub>k−2</sub>⋅m<sub>n−k</sub>. To count all possible secondary structures of a given RNA string that do not contain pseudoknots, we need to modify the Motzkin recurrence so that it counts only matchings of basepair edges in the bonding graph corresponding to the RNA string;
- Given: An RNA string s of length at most 300 bp.
- Return: The total number of noncrossing matchings of basepair edges in the bonding graph of s, modulo 1,000,000.
- Sample Dataset
  - Input:
  >Rosalind_57
  AUAU
  - Output: 7

37. Finding a Spliced Motif
- Problem: A subsequence of a string is a collection of symbols contained in order (though not necessarily contiguously) in the string (e.g., ACG is a subsequence of TATGCTAAGATC). The indices of a subsequence are the positions in the string at which the symbols of the subsequence appear; thus, the indices of ACG in TATGCTAAGATC can be represented by (2, 5, 9). As a substring can have multiple locations, a subsequence can have multiple collections of indices, and the same index can be reused in more than one appearance of the subsequence; for example, ACG is a subsequence of AACCGGTT in 8 different ways.
- Given: Two DNA strings s and t (each of length at most 1 kbp) in FASTA format.
- Return: One collection of indices of s in which the symbols of t appear as a subsequence of s. If multiple solutions exist, you may return any one.
- Sample Dataset:
  - Input:
  >Rosalind_14
  ACGTACGTGACG
  >Rosalind_18
  GTA
  - Output: 3 8 10
- Extra Info: For the mathematically inclined, we may equivalently say that t=t<sub>1</sub>t<sub>2</sub>⋯t<sub>m</sub>
  is a subsequence of s=s<sub>1</sub>s<sub>2</sub>⋯s<sub>n</sub> if the characters of t appear in the same order within s. Even more formally, a subsequence of s is a string s<sub>i1</sub>s<sub>i2</sub>…s<sub>ik</sub>, where 1≤i<sub>1</sub><i<sub>2</sub>⋯<i<sub>k</sub>≤n.

38. Finding a Shared Motif
- Problem: A common substring of a collection of strings is a substring of every member of the collection. We say that a common substring is a longest common substring if there does not exist a longer common substring. For example, "CG" is a common substring of "ACGTACGT" and "AACCGTATA", but it is not as long as possible; in this case, "CGTA" is a longest common substring of "ACGTACGT" and "AACCGTATA". Note that the longest common substring is not necessarily unique; for a simple example, "AA" and "CC" are both longest common substrings of "AACC" and "CCAA".
- Given:  A collection of k (k≤100) DNA strings of length at most 1 kbp each in FASTA format.
- Return: A longest common substring of the collection. (If multiple solutions exist, you may return any single solution.)
- Sample Data Set
  - Input:
  >Rosalind_1
  GATTACA
  >Rosalind_2
  TAGACCA
  >Rosalind_3
  ATACA
  - Output: AC

39. Finding a Shared Spliced Motif
- Problem: A string u is a common subsequence of strings s and t if the symbols of u appear in order as a subsequence of both s and t. For example, "ACTG" is a common subsequence of "AACCTTGG" and "ACACTGTGA". Analogously to the definition of longest common substring, u is a longest common subsequence of s and t if there does not exist a longer common subsequence of the two strings. Continuing our above example, "ACCTTG" is a longest common subsequence of "AACCTTGG" and "ACACTGTGA", as is "AACTGG".
- Given: Two DNA strings s and t(each having length at most 1 kbp) in FASTA format.
- Return: A longest common subsequence of s and t. (If more than one solution exists, you may return any one.)
- Sample Dataset
  - Input:
  >Rosalind_23
  AACCTTGG
  >Rosalind_64
  ACACTGTGA
  - Output: AACTGG
  
40. Edit Distance
- Problem: Given two strings s and t (of possibly different lengths), the edit distance dE(s,t) is the minimum number of edit operations needed to transform s into t, where an edit operation is defined as the substitution, insertion, or deletion of a single symbol. The latter two operations incorporate the case in which a contiguous interval is inserted into or deleted from a string; such an interval is called a gap. For the purposes of this problem, the insertion or deletion of a gap of length k still counts as k distinct edit operations.
- Given: Two protein strings s and t in FASTA format (each of length at most 1000 aa).\
- Return: he edit distance dE(s,t)
- Sample Dataset:
  - Input:
  >Rosalind_39
  PLEASANTLY
  >Rosalind_11
  MEANLY
  - Output: 5

41. Edit Distance Alignment
- Problem: An alignment of two strings s and t is defined by two strings s′ and t′ satisfying the following three conditions: 1. s′ and t′ must be formed from adding gap symbols "-" to each of s and t , respectively; as a result, s and t will form subsequences of s′ and t′. 2. s′ and t′ must have the same length. 3. Two gap symbols may not be aligned; that is, if s′[j] is a gap symbol, then t′[j] cannot be a gap symbol, and vice-versa. We say that s′ and t′ augment s and t . Writing s′ directly over t′ so that symbols are aligned provides us with a scenario for transforming s into t. Mismatched symbols from s and t correspond to symbol substitutions; a gap symbol s′[j] aligned with a non-gap symbol t′[j] implies the insertion of this symbol into t; a gap symbol t′[j] aligned with a non-gap symbol s′[j]implies the deletion of this symbol from s. Thus, an alignment represents a transformation of s into t via edit operations. We define the corresponding edit alignment score of s′ and t′ as d<sub>H</sub>(s′,t′) (Hamming distance is used because the gap symbol has been introduced for insertions and deletions). It follows that d<sub>E</sub>(s,t)=min<sub>s′,t′</sub>d<sub>H</sub>(s′,t′), where the minimum is taken over all alignments of s and t. We call such a minimum score alignment an optimal alignment (with respect to edit distance).
- Given: Two protein strings s and t in FASTA format (with each string having length at most 1000 aa).
- The edit distance d<sub>E</sub>(s,t) followed by two augmented strings s′ and t′ representing an optimal alignment of s and t .
- Sample Dataset
  - Input:
  >Rosalind_43
  PRETTY
  >Rosalind_97
  PRTTEIN
  - Output:
    4
    PRETTY--
    PR-TTEIN

42. Global Alignment with Scoring Matrix
- Problem: To penalize symbol substitutions differently depending on which two symbols are involved in the substitution, we obtain a scoring matrix S in which S<sub>i,j</sub> represents the (negative) score assigned to a substitution of the i th symbol of our alphabet 𝒜 with the j th symbol of 𝒜. A gap penalty is the component deducted from alignment score due to the presence of a gap. A gap penalty may be a function of the length of the gap; for example, a linear gap penalty is a constant g such that each inserted or deleted symbol is charged g; as a result, the cost of a gap of length L is equal to gL.
- Given: Two protein strings s and t in FASTA format (each of length at most 1000 aa).
- Return: 
  - The maximum alignment score between s and t. Use:
    - The BLOSUM62 scoring matrix. 
    - Linear gap penalty equal to 5 (i.e., a cost of -5 is assessed for each gap symbol).
- Sample Dataset:
  - Input:
  >Rosalind_67
  PLEASANTLY
  >Rosalind_17
  MEANLY
  - Output: 8

43. Multiple Alignment
- Problem: A multiple alignment of a collection of three or more strings is formed by adding gap symbols to the strings to produce a collection of augmented strings all having the same length. A multiple alignment score is obtained by taking the sum of an alignment score over all possible pairs of augmented strings. The only difference in scoring the alignment of two strings is that two gap symbols may be aligned for a given pair (requiring us to specify a score for matched gap symbols).
- Given:  A collection of four DNA strings of length at most 10 bp in FASTA format.
- Return:  A multiple alignment of the strings having maximum score, where we score matched symbols 0 (including matched gap symbols) and all mismatched symbols -1 (thus incorporating a linear gap penalty of 1).
- Sample Dataset:
  - Input:
  >Rosalind_7
  ATATCCG
  >Rosalind_35
  TCCG
  >Rosalind_23
  ATGTACTG
  >Rosalind_44
  ATGTCTG
  - Output:
    -18
    ATAT-CCG
    -T---CCG
    ATGTACTG
    ATGT-CTG

44. Alignment-Based Phylogeny
- Problem: Say that we have n taxa represented by strings s<sub>1</sub>,s<sub>2</sub>,…,s<sub>n</sub> with a multiple alignment inducing corresponding augmented strings s<sub>1</sub>,s<sub>2</sub>,…,s<sub>n</sub>. Recall that the number of single-symbol substitutions required to transform one string into another is the Hamming distance between the strings (see “Counting Point Mutations”). Say that we have a rooted binary tree T containing s<sub>1</sub>,s<sub>2</sub>,…,s<sub>n</sub> at its leaves and additional strings s<sub>n+1</sub>,s<sub>n+2</sub>,…,s<sub>2n−1</sub> at its internal nodes, including the root (the number of internal nodes is n−1 by extension of “Counting Phylogenetic Ancestors”). Define d<sub>H</sub>(T) as the sum of dH(s<sub>i</sub>,s<sub>j</sub>) over all edges {s<sub>i</sub>,s<sub>j</sub>} in T: d<sub>H</sub>(T)=∑<sub>{s<sub>i</sub>,s<sub>j</sub>}∈E(T)</sub>d<sub>H</sub>(s<sub>i</sub>,s<sub>j</sub>) Thus, our aim is to minimize d<sub>H</sub>(T).
- Given: A rooted binary tree T on n (n≤500) species, given in Newick format, followed by a multiple alignment of m (m≤n) augmented DNA strings having the same length (at most 300 bp) corresponding to the species and given in FASTA format.
- Return: The minimum possible value of d<sub>H</sub>(T), followed by a collection of DNA strings to be assigned to the internal nodes of T that will minimize d<sub>H</sub>(T) (multiple solutions will exist, but you need only output one).
- Sample Dataset
  - Input:
    (((ostrich,cat)rat,(duck,fly)mouse)dog,(elephant,pikachu)hamster)robot;
    >ostrich
    AC
    >cat
    CA
    >duck
    T-
    >fly
    GC
    >elephant
    -T
    >pikachu
    AA
  - Output:
    8
    >rat
    AC
    >mouse
    TC
    >dog
    AC
    >hamster
    AT
    >robot
    AC
- Note: Given internal strings minimizing d<sub>H</sub>(T), the alignment between any two adjacent strings is not necessarily an optimal global paired alignment. In other words, it may not be the case that dH(s<sub>i</sub>,s<sub>j</sub>) is equal to the edit distance d<sub>E</sub>(s<sub>i</sub>,s<sub>j</sub>).
- Extra Requirements: Include file parsing of sample data

45. Identifying Reversing Substitutions
- Problem: For a rooted tree T whose internal nodes are labeled with genetic strings, our goal is to identify reversing substitutions in T. Assuming that all the strings of T have the same length, a reversing substitution is defined formally as two parent-child string pairs (s,t) and (v,w) along with a position index i, where: there is a path in T from s down to w; s[i]=w[i]≠v[i]=t[i]; and if u is on the path connecting t to v , then t[i]=u[i]. In other words, the third condition demands that a reversing substitution must be contiguous: no other substitutions can appear between the initial and reversing substitution.
- Given: A rooted binary tree T with labeled nodes in Newick format, followed by a collection of at most 100 DNA strings in FASTA format whose labels correspond to the labels of T. We will assume that the DNA strings have the same length, which does not exceed 400 bp).
- Return:  A list of all reversing substitutions in T (in any order), with each substitution encoded by the following three items:
  - the name of the species in which the symbol is first changed, followed by the name of the species in which it changes back to its original state
  - the position in the string at which the reversing substitution occurs; and
  - the reversing substitution in the form original_symbol->substituted_symbol->reverted_symbol.
- Sample Dataset
  - Input:
    (((ostrich,cat)rat,mouse)dog,elephant)robot;
    >robot
    AATTG
    >dog
    GGGCA
    >mouse
    AAGAC
    >rat
    GTTGT
    >cat
    GAGGC
    >ostrich
    GTGTC
    >elephant
    AATTC
  - Output:
    dog mouse 1 A->G->A
    dog mouse 2 A->G->A
    rat ostrich 3 G->T->G
    rat cat 3 G->T->G
    dog rat 3 T->G->T

46. Counting Optimal Alignments
- Problem: Recall from “Edit Distance Alignment” that if s′ and t′ are the augmented strings corresponding to an alignment of strings s and t , then the edit alignment score of s′ and t′ was given by the Hamming distance d<sub>H</sub>(s′,t′) (because s′ and t′ have the same length and already include gap symbols to denote insertions/deletions). As a result, we obtain d<sub>E</sub>(s,t)=mins′,t′d<sub>H</sub>(s′,t′), where the minimum is taken over all alignments of s and t . Strings s′ and t′ achieving this minimum correspond to an optimal alignment with respect to edit alignment score.
- Given: Two protein strings s and t in FASTA format, each of length at most 1000 aa.
- Return: he total number of optimal alignments of s and t with respect to edit alignment score, modulo 134,217,727 (227-1).
- Sample Dataset:
  - Input:
  >Rosalind_78
  PLEASANTLY
  >Rosalind_33
  MEANLY
  - Output: 4

47. Local Alignment with Scoring Matrix
- Problem: A local alignment of two strings s and t is an alignment of substrings r and u of s and t, respectively. Let opt(r,u) denote the score of an optimal alignment of r and u with respect to some predetermined alignment score.
- Given: Two protein strings s and t in FASTA format (each having length at most 1000 aa).
- Return: A maximum alignment score along with substrings r and u of s and t, respectively, which produce this maximum alignment score (multiple solutions may exist, in which case you may output any one). Use:
  - The PAM250 scoring matrix.
  - Linear gap penalty equal to 5.
- Sample Dataset:
  - Input:
  >Rosalind_80
  MEANLYPRTEINSTRING
  >Rosalind_21
  PLEASANTLYEINSTEIN
  - Output:
    23
    LYPRTEINSTRIN
    LYEINSTEIN

48. Finding a Motif with Modifications
- Problem: Given a string s and a motif t, an alignment of a substring of s against all of t is called a fitting alignment. Our aim is to find a substring s′ of s that maximizes an alignment score with respect to t. Note that more than one such substring of s may exist, depending on the particular strings and alignment score used. One candidate for scoring function is the one derived from edit distance; In this problem, we will consider a slightly different alignment score, in which all matched symbols count as +1 and all mismatched symbols (including insertions and deletions) receive a cost of -1. Let's call this scoring function the mismatch score. See Figure 1 for a comparison of global, local, and fitting alignments with respect to mismatch score.
- Given: Two DNA strings s and t, where s has length at most 10 kbp and t represents a motif of length at most 1 kbp.
- Return: An optimal fitting alignment score with respect to the mismatch score defined above, followed by an optimal fitting alignment of a substring of s against t. If multiple such alignments exist, then you may output any one.
- Sample Dataset
  - Input:
  >Rosalind_54
  GCAAACCATAAGCCCTACGTGCCGCCTGTTTAAACTCGCGAACTGAATCTTCTGCTTCACGGTGAAAGTACCACAATGGTATCACACCCCAAGGAAAC
  >Rosalind_46
  GCCGTCAGGCTGGTGTCCG
  - Output:
    5
    ACCATAAGCCCTACGTG-CCG
    GCCGTCAGGC-TG-GTGTCCG

49. Isolating Symbols in Alignments
- Problem: Say that we have two strings s and t of respective lengths m and n and an alignment score. Let's define a matrix M corresponding to s and t by setting M<sub>j,k</sub> equal to the maximum score of any alignment that aligns s[j] with t[k]. So each entry in M can be equal to at most the maximum score of any alignment of s and t.
- Given: Two DNA strings s and t in FASTA format, each having length at most 1000 bp.
- Return: The maximum alignment score of a global alignment of s and t, followed by the sum of all elements of the matrix M corresponding to s and t that was defined above. Apply the mismatch score introduced in “Finding a Motif with Modifications”.
- Sample Dataset:
  - Input
  >Rosalind_35
  ATAGATA
  >Rosalind_5
  ACAGGTA
  - Output
  3
  -139

50. Finding All Similar Motifs
- Given: A positive integer k(k≤50), a DNA string s of length at most 5 kbp representing a motif, and a DNA string t of length at most 50 kbp representing a genome.
- Return: All substrings t′ of t such that the edit distance d<sub>E</sub>(s,t′)is less than or equal to k . Each substring should be encoded by a pair containing its location in t followed by its length.
- Sample Dataset
  - Input:
    2
    ACGTAG
    ACGGATCGGCATCGT
  - Output
    1 4
    1 5
    1 6

51. Global Alignment with Constant Gap Penalty
- Problem: In a constant gap penalty, every gap receives some predetermined constant penalty, regardless of its length. Thus, the insertion or deletion of 1000 contiguous symbols is penalized equally to that of a single symbol.
- Given: Two protein strings s and t in FASTA format (each of length at most 1000 aa).
- Return: The maximum alignment score between s
  and t. Use:
  - The BLOSUM62 scoring matrix.
  - Constant gap penalty equal to 5.
- Sample Dataset:
  - Input:
  >Rosalind_79
  PLEASANTLY
  >Rosalind_41
  MEANLY
  - Output:
  13

52. Global Alignment with Scoring Matrix and Affine Gap Penalty
- Problem: Problem An affine gap penalty is written as a+b⋅(L−1), where L is the length of the gap, a is a positive constant called the gap opening penalty, and b is a positive constant called the gap extension penalty. We can view the gap opening penalty as charging for the first gap symbol, and the gap extension penalty as charging for each subsequent symbol added to the gap. For example, if a=11 and b=1 , then a gap of length 1 would be penalized by 11 (for an average cost of 11 per gap symbol), whereas a gap of length 100 would have a score of 110 (for an average cost of 1.10 per gap symbol). Consider the strings "PRTEINS" and "PRTWPSEIN". If we use the BLOSUM62 scoring matrix and an affine gap penalty with a=11 and b=1, then we obtain the following optimal alignment.
  PRT---EINS
  |||   |||
  PRTWPSEIN-

  Matched symbols contribute a total of 32 to the calculation of the alignment's score, and the gaps cost 13 and 11 respectively, yielding a total score of 8.

- Given: Two protein strings s and t in FASTA format (each of length at most 100 aa).
- Return: he maximum alignment score between s and t, followed by two augmented strings s′ and t′ representing an optimal alignment of s and t. Use:
  - The BLOSUM62 scoring matrix.
  - Gap opening penalty equal to 11.
  - Gap extension penalty equal to 1.
- Sample Dataset
  - Input:
  >Rosalind_49
  PRTEINS
  >Rosalind_47
  PRTWPSEIN
  - Output
    8
    PRT---EINS
    PRTWPSEIN-

53. Local Alignment with Affine Gap Penalty
- Given: Two protein strings s and t in FASTA format (each having length at most 10,000 aa).
- Return: The maximum local alignment score of s and t , followed by substrings r and u of s and t, respectively, that correspond to the optimal local alignment of s and t. Use:
  - The BLOSUM62 scoring matrix.
  - Gap opening penalty equal to 11.
  - Gap extension penalty equal to 1.
If multiple solutions exist, then you may output any one.
  - Sample Data:
    - Input
    >Rosalind_8
    PLEASANTLY
    >Rosalind_18
    MEANLY
  - Output
    12
    LEAS
    MEAN

54. Counting Unrooted Binary Trees
- Problem: Two unrooted binary trees T<sub>1</sub> and T<sub>2</sub> having the same n labeled leaves are considered to be equivalent if there is some assignment of labels to the internal nodes of T<sub>1</sub> and T<sub>2</sub> so that the adjacency lists of the two trees coincide. As a result, note that T<sub>1</sub> and T<sub>2</sub> must have the same splits; conversely, if the two trees do not have the same splits, then they are considered distinct. Let b(n) denote the total number of distinct unrooted binary trees having n labeled leaves.
- Given:  A positive integer n (n≤1000)
- Return: The value of b(n) modulo 1,000,000.
- Sample Dataset
  - Input: 5
  - Sample Output: 15

55. Quartets
- Problem: A partial split of a set S of n taxa models a partial character and is denoted by A∣B, where A and B are still the two disjoint subsets of taxa divided by the character. Unlike in the case of splits, we do not necessarily require that A∪B=S; (A ∪ B)<sup>c</sup? corresponds to those taxa for which we lack conclusive evidence regarding the character. We can assemble a collection of partial characters into a generalized partial character table C in which the symbol x is placed in C<sub>i</sub>,j if we do not have conclusive evidence regarding the jth taxon with respect to the ith partial character. A quartet is a partial split A ∣ B in which both A and B contain precisely two elements. For the sake of simplicity, we often will consider quartets instead of partial characters. We say that a quartet A∣B is inferred from a partial split C ∣ D if A ⊆ C and B ⊆ D (or equivalently A ⊆ D and B ⊆ C). For example, {1,3}∣{2,4} and {3,5}∣{2,4} can be inferred from {1,3,5}∣{2,4}.
- Given: A partial character table C.
- Return: The collection of all quartets that can be inferred from the splits corresponding to the underlying characters of C.
- Sample Dataset
  - Input:
    cat dog elephant ostrich mouse rabbit robot
    01xxx00
    x11xx00
    111x00x
  - Output:
    {elephant, dog} {rabbit, robot}
    {cat, dog} {mouse, rabbit}
    {mouse, rabbit} {cat, elephant}
    {dog, elephant} {mouse, rabbit}

56. Phylogeny Comparison with Split Distance
- Problem: Define the split distance between two unrooted binary trees as the number of nontrivial splits contained in one tree but not the other. Formally, if s(T<sub>1</sub>,T<sub>2</sub>) denotes the number of nontrivial splits shared by unrooted binary trees T<sub>1</sub> and T<sub>2</sub>, Then their split distance is d<sub>split</sub>(T<sub>1</sub>,T<sub>2</sub>)=2(n−3)−2s(T<sub>1</sub>,T<sub>2</sub>).
- Given: A collection of at most 3,000 species taxa and two unrooted binary trees T<sub>1</sub> and T<sub>2</sub> on these taxa in Newick format.
- Return: The split distance d<sub>split</sub>(T<sub>1</sub>, T<sub>2</sub>)
- Sample Dataset:
  - Input:
    dog rat elephant mouse cat rabbit
    (rat,(dog,cat),(rabbit,(elephant,mouse)));
    (rat,(cat,dog),(elephant,(mouse,rabbit)));
  - Output: 2
- Extra: Make sure to read from the sample txt file located here: /Users/cference/Code/scala-bioinformatics/src/main/scala/resources/sptd_data.txt for the solution algorithm

57. Counting Quartets
- Problem: A quartet AB∣CD is consistent with a binary tree T if the quartet can be inferred from one of the splits of T (see “Quartets” for a description of inferring quartets from splits). Let q(T) denote the total number of quartets that are consistent with T.
- Given:  A positive integer n (4≤n≤5000), followed by an unrooted binary tree T in Newick format on n taxa.
- Return: The value of q(T) modulo 1,000,000.
- Sample Dataset
  - Input:
    6
    (lobster,(cat,dog),(caterpillar,(elephant,mouse)));
  - Output:
  15
- Extra: Please use the file: /Users/cference/Code/scala-bioinformatics/src/main/scala/resources/cntq_data.txt to ingest the data into the solutions algorithm

58. In “Counting Quartets”, we found an expression for q(T), the number of quartets that can be inferred from an unrooted binary tree containing n taxa. If T<sub>1</sub> and T<sub>2</sub> are both unrooted binary trees on the same n taxa, then we now let q(T<sub>1</sub>,T<sub>2</sub>) denote the number of inferred quartets that are common to both trees. The quartet distance between T<sub>1</sub> and T<sub>2</sub>, dq(T<sub>1</sub>,T<sub>2</sub>) is the number of quartets that are only inferred from one of the trees. More precisely, dq(T<sub>1</sub>,T<sub>2</sub>)=q(T<sub>1</sub>)+q(T<sub>2</sub>)−2q(T<sub>1</sub>,T<sub>2</sub>).
- Given: A list containing n taxa (n≤2000) and two unrooted binary trees T<sub>1</sub> and T<sub>2</sub> on the given taxa. Both T<sub>1</sub> and T<sub>2</sub> are given in Newick format.
- Return: The quartet distance d<sub>q</sub>(T<sub>1</sub>,T<sub>2</sub>)
- Sample Dataset
  - Input:
    A B C D E
    (A,C,((B,D),E));
    (C,(B,D),(A,E));
  - Output: 4
- Extra, use the file located in: /Users/cference/Code/scala-bioinformatics/src/main/scala/resources/qrtd_data.txt to ingest the data into the algorithm

59. Introduction to Set Operations
- Problem: If A and B are sets, then their union A ∪ B is the set comprising any elements in either A or B; their intersection A ∩ B is the set of elements in both A and B; and their set difference A−B is the set of elements in A but not in B. Furthermore, if A is a subset of another set U, then the set complement of A with respect to U is defined as the set A<sup>c</sup>=U−A. See the Sample sections below for examples.
- Given: A positive integer n (n≤20,000) and two subsets A and B of {1,2,…,n} .]
- Return: Six sets: A∪B , A∩B , A−B, B−A, A<sup>c</sup>, and B<sup>c</sup> (where set complements are taken with respect to {1,2,…,n}).
- Sample Dataset
  - Input:
    10
    {1, 2, 3, 4, 5}
    {2, 8, 5, 10}
  - Output
    {1, 2, 3, 4, 5, 8, 10}
    {2, 5}
    {1, 3, 4}
    {8, 10}
    {8, 9, 10, 6, 7}
    {1, 3, 4, 6, 7, 9}
- Extra Info: From the definitions above, one can see that A∪B=B∪A and A∩B=B∩A for all sets A and B , but it is not necessarily the case that A−B=B−A(as seen in the Sample
- Extra: Sample data is located in: /Users/cference/Code/scala-bioinformatics/src/main/scala/resources/seto_data.txt, use the file content for the algorithm

60. Character-Based Phylogeny
- Problem: Because a tree having n nodes has n−1 edges (see “Completing a Tree”), removing a single edge from a tree will produce two smaller, disjoint trees. Recall from “Creating a Character Table” that for this reason, each edge of an unrooted binary tree corresponds to a split S∣S<sup>c</sup>, where S is a subset of the taxa. A consistent character table is one whose characters' splits do not conflict with the edge splits of some unrooted binary tree T on the n taxa. More precisely, S1∣S<sup>c</sup><sub>1</sub> conflicts with S<sub>2</sub>∣S<sup>c</sup><sub>2</sub> if all four intersections S<sub>1</sub>∩S<sub>2</sub>, S<sub>1</sub>∩S<sup>c</sup><sub>2</sub>, S<sup>c</sup>1∩S<sub>2</sub>, and S<sup>c</sup><sub>1</sub>∩S<sup>c</sup><sub>2</sub> are nonempty. As a simple example, consider the conflicting splits {a,b}∣{c,d} and {a,c}∣{b,d} . More generally, given a consistent character table C, an unrooted binary tree T "models" C if the edge splits of T agree with the splits induced from the characters of C.
- Given:  list of n species (n≤80) and an n-column character table C in which the jth column denotes the jth species.
- Return: An unrooted binary tree in Newick format that models C.
- Sample Dataset
  - Input:
    cat dog elephant mouse rabbit rat
    011101
    001101
    001100
  - Output:
    (dog,(cat,rabbit),(rat,(elephant,mouse)));
- Extra: Use the file located here: /Users/cference/Code/scala-bioinformatics/src/main/scala/resources/chbp_data.txt as the input data for the algorithm

61. Fixing an Inconsistent Character Set
- Problem: A submatrix of a matrix M is a matrix formed by selecting rows and columns from M and taking only those entries found at the intersections of the selected rows and columns. We may also think of a submatrix as formed by deleting the remaining rows and columns from M.
- Given: An inconsistent character table C on at most 100 taxa.
- Return: A submatrix of C′ representing a consistent character table on the same taxa and formed by deleting a single row of C. (If multiple solutions exist, you may return any one.)
- Sample Dataset:
  - Input:
    100001
    000110
    111000
    100111
  - Output:
    000110
    100001
    100111
- Extra: Read the data to input into the algorithm from /Users/cference/Code/scala-bioinformatics/src/main/scala/resources/cset_data.txt

62. Genome Assembly as Shortest Superstring
- Problem: For a collection of strings, a larger string containing every one of the smaller strings as a substring is called a superstring. By the assumption of parsimony, a shortest possible superstring over a collection of reads serves as a candidate chromosome.
- Given: At most 50 DNA strings of approximately equal length, not exceeding 1 kbp, in FASTA format (which represent reads deriving from the same strand of a single linear chromosome). The dataset is guaranteed to satisfy the following condition: there exists a unique way to reconstruct the entire chromosome from these reads by gluing together pairs of reads that overlap by more than half their length.
- Return: A shortest superstring containing all the given strings (thus corresponding to a reconstructed chromosome).
- Sample Dataset:
  - Input
  >Rosalind_56
  ATTAGACCTG
  >Rosalind_57
  CCTGCCGGAA
  >Rosalind_58
  AGACCTGCCG
  >Rosalind_59
  GCCGGAATAC
  - Output: ATTAGACCTGCCGGAATAC
- Extra Info: Although the goal of fragment assembly is to produce an entire genome, in practice it is only possible to construct several contiguous portions of each chromosome, called contigs. Furthermore, the assumption made above that reads all derive from the same strand is also practically unrealistic; in reality, researchers will not know the strand of DNA from which a given read has been sequenced.
- Extra: Use the file located here for the input of the algorithm: /Users/cference/Code/scala-bioinformatics/src/main/scala/resources/long_data.txt

63. Enumerating k-mers Lexicographically
- Problem: Assume that an alphabet 𝒜 has a predetermined order; that is, we write the alphabet as a permutation 𝒜=(a<sub>1</sub>,a<sub>2</sub>,…,a<sub>k</sub>), where a<sub>1</sub><a<sub>2</sub><⋯<a<sub>k</sub>. For instance, the English alphabet is organized as (A,B,…,Z). Given two strings s and t having the same length n , we say that s precedes t in the lexicographic order (and write s<L<sub>ext</sub>) if the first symbol s[j] that doesn't match t[j] satisfies s<sub>j</sub> < t<sub>j</sub> in 𝒜
- Given: A collection of at most 10 symbols defining an ordered alphabet, and a positive integer n (n≤10).
- Return: All strings of length n that can be formed from the alphabet, ordered lexicographically (use the standard order of symbols in the English alphabet).
- Sample Dataset
  - Input:
    A C G T
    2
  - Output:
    AA
    AC
    AG
    AT
    CA
    CC
    CG
    CT
    GA
    GC
    GG
    GT
    TA
    TC
    TG
    TT
- Extra: Use /Users/cference/Code/scala-bioinformatics/src/main/scala/resources/lexf_data.txt for the input data into the algorithm

64. k-Mer Composition
- Problem: For a fixed positive integer k, order all possible k-mers taken from an underlying alphabet lexicographically. Then the k-mer composition of a string s can be represented by an array A for which A[m] denotes the number of times that the m th k-mer (with respect to the lexicographic order) appears in s.
- Given: A DNA string s in FASTA format (having length at most 100 kbp).
- Return: The 4-mer composition of s
- Sample Data
  - Input:
  >Rosalind_6431
  CTTCGAAAGTTTGGGCCGAGTCTTACAGTCGGTCTTGAAGCAAAGTAACGAACTCCACGG
  CCCTGACTACCGAACCAGTTGTGAGTACTCAACTGGGTGAGAGTGCAGTCCCTATTGAGT
  TTCCGAGACTCACCGGGATTTTCGATCCAGCCTCAGTCCAGTCTTGTGGCCAACTCACCA
  AATGACGTTGGAATATCCCTGTCTAGCTCACGCAGTACTTAGTAAGAGGTCGCTGCAGCG
  GGGCAAGGAGATCGGAAAATGTGCTCTATATGCGACTAAAGCTCCTAACTTACACGTAGA
  CTTGCCCGTGTTAAAAACTCGGCTCACATGCTGTCTGCGGCTGGCTGTATACAGTATCTA
  CCTAATACCCTTCAGTTCGCCGCACAAAAGCTGGGAGTTACCGCGGAAATCACAG
  - Output:
    4 1 4 3 0 1 1 5 1 3 1 2 2 1 2 0 1 1 3 1 2 1 3 1 1 1 1 2 2 5 1 3 0 2 2 1 1 1 1 3 1 0 0 1 5 5 1 5 0 2 0 2 1 2 1 1 1 2 0 1 0 0 1 1 3 2 1 0 3 2 3 0 0 2 0 8 0 0 1 0 2 1 3 0 0 0 1 4 3 2 1 1 3 1 2 1 3 1 2 1 2 1 1 1 2 3 2 1 1 0 1 1 3 2 1 2 6 2 1 1 1 2 3 3 3 2 3 0 3 2 1 1 0 0 1 4 3 0 1 5 0 2 0 1 2 1 3 0 1 2 2 1 1 0 3 0 0 4 5 0 3 0 2 1 1 3 0 3 2 2 1 1 0 2 1 0 2 2 1 2 0 2 2 5 2 2 1 1 2 1 2 2 2 2 1 1 3 4 0 2 1 1 0 1 2 2 1 1 1 5 2 0 3 2 1 1 2 2 3 0 3 0 1 3 1 2 3 0 2 1 2 2 1 2 3 0 1 2 3 1 1 3 1 0 1 1 3 0 2 1 2 2 0 2 1 1
- Extra: Use /Users/cference/Code/scala-bioinformatics/src/main/scala/resources/kmer_data.txt for the data input into the algorithm

65. Constructing a De Bruijn Graph
- Problem: Consider a set S of (k+1)-mers of some unknown DNA string. Let S<sup>rc<s/up> denote the set containing all reverse complements of the elements of S. (recall from “Counting Subsets” that sets are not allowed to contain duplicate elements). The de Bruijn graph B<sub>k</sub> of order k corresponding to S ∪ S<sup>rc</sup> is a digraph defined in the following way:
  - Nodes of B<sub>k</sub> correspond to all k-mers that are present as a substring of a (k+1)-mer from S ∪ S<sup>rc</sup>
  - Edges of B<sub>k</sub> are encoded by the (k+1)-mers of S ∪ S<sup>rc</sup> in the following way: for each (k+1)-mer r in S ∪ S<sup>rc</sup>, form a directed edge (r[1:k], r[2:k+1]).
- Given: A collection of up to 1000 (possibly repeating) DNA strings of equal length (not exceeding 50 bp) corresponding to a set S of (k+1)-mers.
- Return: The adjacency list corresponding to the de Bruijn graph corresponding to S ∪ S<sup>rc</sup>
- Sample Dataset
  - Input
    TGAT
    CATG
    TCAT
    ATGC
    CATC
    CATC
  - Output
    (ATC, TCA)
    (ATG, TGA)
    (ATG, TGC)
    (CAT, ATC)
    (CAT, ATG)
    (GAT, ATG)
    (GCA, CAT)
    (TCA, CAT)
    (TGA, GAT)
- Extra: Use /Users/cference/Code/scala-bioinformatics/src/main/scala/resources/dbru_data.txt for the data input for the algorithm

66. Genome Assembly with Perfect Coverage
- Problem: A circular string is a string that does not have an initial or terminal element; instead, the string is viewed as a necklace of symbols. We can represent a circular string as a string enclosed in parentheses. For example, consider the circular DNA string (ACGTAC), and note that because the string "wraps around" at the end, this circular string can equally be represented by (CGTACA), (GTACAC), (TACACG), (ACACGT), and (CACGTA). The definitions of substrings and superstrings are easy to generalize to the case of circular strings (keeping in mind that substrings are allowed to wrap around).
- Given: A collection of (error-free) DNA k-mers (k≤50) taken from the same strand of a circular chromosome. In this dataset, all k-mers from this strand of the chromosome are present, and their de Bruijn graph consists of exactly one simple cycle.
- Return: A cyclic superstring of minimal length containing the reads (thus corresponding to a candidate cyclic chromosome).
- Sample Data
  - Input
    ATTAC
    TACAG
    GATTA
    ACAGA
    CAGAT
    TTACA
    AGATT
  - Output: GATTACA
- Note: The assumption made above that all reads derive from the same strand is practically unrealistic; in reality, researchers will not know the strand of DNA from which a given read has been sequenced.
- Extra: Use /Users/cference/Code/scala-bioinformatics/src/main/scala/resources/pcov_data.txt for data input to solve the algorithm

67. Genome Assembly Using Reads
- Problem: A directed cycle is simply a cycle in a directed graph in which the head of one edge is equal to the tail of the next (so that every edge in the cycle is traversed in the same direction). For a set of DNA strings S and a positive integer k, let S<sub>k</sub> denote the collection of all possible k-mers of the strings in S.
- Given: A collection S of (error-free) reads of equal length (not exceeding 50 bp). In this dataset, for some positive integer k, the de Bruijn graph B<sub>k</sub> on S<sub>k+1</sub> ∪ S <sup>rc</sup><sub>k+1</sub> consists of exactly two directed cycles.
- Return: A cyclic superstring of minimal length containing every read or its reverse complement.
- Sample Dataset:
  - Input:
    AATCT
    TGTAA
    GATTA
    ACAGA
  - Output: GATTACA
- Note: The reads "AATCT" and "TGTAA" are not present in the answer, but their reverse complements "AGATT" and "TTACA" are present in the circular string (GATTACA).
- Extra: Use /Users/cference/Code/scala-bioinformatics/src/main/scala/resources/gasm_data.txt as the file input for data, to solve the algorithm

68. Assessing Assembly Quality with N50 and N75
- Problem: Given a collection of DNA strings representing contigs, we use the N statistic NXX (where XX ranges from 01 to 99) to represent the maximum positive integer L such that the total number of nucleotides of all contigs having length ≥L is at least XX% of the sum of contig lengths. The most commonly used such statistic is N50, although N75 is also worth mentioning.
- Given: A collection of at most 1000 DNA strings (whose combined length does not exceed 50 kbp).
- Return: N50 and N75 for this collection of strings.
- Sample Dataset:
  - Input
    GATTACA
    TACTACTAC
    ATTGAT
    GAAGA
  - Output:
    7 6
- Extra Info: For an explanation of the results obtained in the sample above, contigs of length at least 7 total 7 + 9 = 16 bp, which is more than 50% of the total 27). Contigs of length at least 8 total only 9 bp (less than 50%). Contigs of length at least 6 total 6 + 7 + 9 = 22 bp, which is more than 75% of all base pairs. Contigs of length at least 7 total only 16 bp (less than 75%).
- Extra: Use this file /Users/cference/Code/scala-bioinformatics/src/main/scala/resources/asmq_data.txt to ingest the data to solve the algorithm

69. Overlap Alignment
- Problem: An overlap alignment between two strings s and t is a local alignment of a suffix of s with a prefix of t. An optimal overlap alignment will therefore maximize an alignment score over all such substrings of s and t. The term "overlap alignment" has also been used to describe what Rosalind defines as a semiglobal alignment. See “Semiglobal Alignment” for details.
- Given: Two DNA strings s and t in FASTA format, each having length at most 10 kbp.
- Return: The score of an optimal overlap alignment of s and t, followed by an alignment of a suffix s′ of s and a prefix t′ of t achieving this optimal score. Use an alignment score in which matching symbols count +1, substitutions count -2, and there is a linear gap penalty of 2. If multiple optimal alignments exist, then you may return any one.
- Sample Dataset:
  - Input
  >Rosalind_54
  CTAAGGGATTCCGGTAATTAGACAG
  >Rosalind_45
  ATAGACCATATGTCAGTGACTGTGTAA
  - Output:
    1
    ATTAGAC-AG
    AT-AGACCAT
- Extra: Use /Users/cference/Code/scala-bioinformatics/src/main/scala/resources/oap_data.txt for the input data like in the previous problems

70. Genome Assembly with Perfect Coverage and Repeats
- Problem: Recall that a directed cycle is a cycle in a directed graph in which the head of one edge is equal to the tail of the following edge. In a de Bruijn graph of k-mers, a circular string s is constructed from a directed cycle s<sub>1</sub>→s<sub>2</sub>→...→s<sub>i</sub>→s<sub>1</sub> is given by s<sub>1</sub>+s<sub>2</sub>[k]+...+s<sub>i−k</sub>[k]+s<sub>i−k+1</sub>[k]. That is, because the final k−1 symbols of s<sub>1</sub> overlap with the first k−1 symbols of s<sub>2</sub>, we simply tack on the k-th symbol of s<sub>2</sub> to s, then iterate the process. For example, the circular string assembled from the cycle "AC" → "CT" → "TA" → "AC" is simply (ACT). Note that this string only has length three because the 2-mers "wrap around" in the string. If every k-mer in a collection of reads occurs as an edge in a de Bruijn graph cycle the same number of times as it appears in the reads, then we say that the cycle is "complete."
- Given: A list S<sub>k+1</sub> of error-free DNA (k+1)-mers (k≤5) taken from the same strand of a circular chromosome (of length ≤50).
- Return: All circular strings assembled by complete cycles in the de Bruijn graph B<sub>k</sub> of S<sub>k+1</sub>. The strings may be given in any order, but each one should begin with the first (k+1)-mer provided in the input.
- Sample Dataset
  - Input
    CAG
    AGT
    GTT
    TTT
    TTG
    TGG
    GGC
    GCG
    CGT
    GTT
    TTC
    TCA
    CAA
    AAT
    ATT
    TTC
    TCA
  - Output
    CAGTTCAATTTGGCGTT
    CAGTTCAATTGGCGTTT
    CAGTTTCAATTGGCGTT
    CAGTTTGGCGTTCAATT
    CAGTTGGCGTTCAATTT
    CAGTTGGCGTTTCAATT
- Extra: Use /Users/cference/Code/scala-bioinformatics/src/main/scala/resources/grep_data.txt as the input data for the problem

71. Semiglobal Alignment
- Problem: A semiglobal alignment of strings s and t is an alignment in which any gaps appearing as prefixes or suffixes of s and t do not contribute to the alignment score. Semiglobal alignment has sometimes also been called "overlap alignment". Rosalind defines overlap alignment differently (see “Overlap Alignment”).
- Given: Two DNA strings s and t in FASTA format, each having length at most 10 kbp.
- Return: The maximum semiglobal alignment score of s and t, followed by an alignment of s and t achieving this maximum score. Use an alignment score in which matching symbols count +1, substitutions count -1, and there is a linear gap penalty of 1. If multiple optimal alignments exist, then you may return any one.
- Sample Dataset:
  - Input
  >Rosalind_79
  CAGCACTTGGATTCTCGG
  >Rosalind_98
  CAGCGTGG
  - Output:
    4
    CAGCA-CTTGGATTCTCGG
    ---CAGCGTGG--------
- Extra: Use /Users/cference/Code/scala-bioinformatics/src/main/scala/resources/smgb_data.txt for data input


72. Inferring Protein from Spectrum
- Problem: The prefix spectrum of a weighted string is the collection of all its prefix weights.
- Given: A list L of n (n≤100) positive real numbers.
- Return: A protein string of length n−1 whose prefix spectrum is equal to L (if multiple solutions exist, you may output any one of them). Consult the monoisotopic mass table.
- Sample Dataset:
  - Input:
    3524.8542
    3710.9335
    3841.974
    3970.0326
    4057.0646
  - Output
    WMQS
- Extra: Use /Users/cference/Code/scala-bioinformatics/src/main/scala/resources/spec_data.txt for the input data 

73. Calculating Protein Mass
- Problem: In a weighted alphabet, every symbol is assigned a positive real number called a weight. A string formed from a weighted alphabet is called a weighted string, and its weight is equal to the sum of the weights of its symbols. The standard weight assigned to each member of the 20-symbol amino acid alphabet is the monoisotopic mass of the corresponding amino acid.
- Given: A protein string P of length at most 1000 aa.
- Return: The total weight of P. Consult the monoisotopic mass table.
- Monoisotopic Mass Table
  A   71.03711
  C   103.00919
  D   115.02694
  E   129.04259
  F   147.06841
  G   57.02146
  H   137.05891
  I   113.08406
  K   128.09496
  L   113.08406
  M   131.04049
  N   114.04293
  P   97.05276
  Q   128.05858
  R   156.10111
  S   87.03203
  T   101.04768
  V   99.06841
  W   186.07931
  Y   163.06333 
- Sample Dataset:
  - Input: SKADYEK
  - Output: 821.392
- Extra: Use for /Users/cference/Code/scala-bioinformatics/src/main/scala/resources/prtm_data.txt input data

74. Comparing Spectra with the Spectral Convolution
- Problem: A multiset is a generalization of the notion of set to include a collection of objects in which each object may occur more than once (the order in which objects are given is still unimportant). For a multiset S , the multiplicity of an element x is the number of times that x occurs in the set; this multiplicity is denoted S(x). Note that every set is included in the definition of multiset. The Minkowski sum of multisets S<sub>1</sub> and S<sub>2</sub> containing real numbers is the new multiset S<sub>1</sub>⊕S<sub>2</sub> formed by taking all possible sums s<sub>1</sub>+s<sub>2</sub> of an element s<sub>1</sub> from S<sub>1</sub> and an element s<sub>2</sub> from S<sub>2</sub>. The Minkowski sum could be defined more concisely as S<sub>1</sub>⊕S<sub>2</sub>=s<sub>1</sub>+s<sub>2</sub>:s<sub>1</sub>∈S<sub>1</sub>, s2∈S2, The Minkowski difference S<sub>1</sub>⊖S<sub>2</sub> is defined analogously by taking all possible differences s<sub>1</sub>−s<sub>2</sub>. If S<sub>1</sub> and S<sub>2</sub> represent simplified spectra taken from two peptides, then S<sub>1</sub>⊖S<sub>2</sub> is called the spectral convolution of S<sub>1</sub> and S<sub>2</sub>. In this notation, the shared peaks count is represented by (S<sub>2</sub>⊖S<sub>1</sub>)(0), and the value of x for which (S<sub>2</sub>⊖S<sub>1</sub>)(x) has the maximal value is the shift value maximizing the number of shared masses of S<sub>1</sub> and S<sub>2</sub>.
- Given: Two multisets of positive real numbers S<sub>1</sub> and S<sub>2</sub>. The size of each multiset is at most 200.
- Return: The largest multiplicity of S<sub>1</sub>⊖S<sub>2</sub>, as well as the absolute value of the number x maximizing (S<sub>1</sub>⊖S<sub>2</sub>)(x) (you may return any such value if multiple solutions exist).
- Sample Dataset:
  - Input
    186.07931 287.12699 548.20532 580.18077 681.22845 706.27446 782.27613 968.35544 968.35544
    101.04768 158.06914 202.09536 318.09979 419.14747 463.17369
  - Output:
    3
    85.03163
- Note: Observe that S<sub>1</sub>⊕S<sub>2</sub> is equivalent to S<sub>2</sub>⊕S<sub>1</sub>, but it is not usually the case that S<sub>1</sub>⊖S<sub>2</sub> is the same as S<sub>2</sub>⊖S<sub>1</sub>; in this case, one multiset can be obtained from the other by negating every element.
- Extra: Use /Users/cference/Code/scala-bioinformatics/src/main/scala/resources/conv_data.txt for data input

75. Locating Restriction Sites
- Problem: A DNA string is a reverse palindrome if it is equal to its reverse complement. For instance, GCATGC is a reverse palindrome because its reverse complement is GCATGC.
- Given: A DNA string of length at most 1 kbp in FASTA format.
- Return: The position and length of every reverse palindrome in the string having length between 4 and 12. You may return these pairs in any order.
- Sample Dataset
  - Input
  >Rosalind_24
  TCAATGCATGCGGGTCTATATGCAT
  - Output
    4 6
    5 4
    6 6
    7 4
    17 4
    18 4
    20 6
    21 4
- Extra: Use /Users/cference/Code/scala-bioinformatics/src/main/scala/resources/revp_data.txt for data input
- Extra Info: You may be curious how the bacterium prevents its own DNA from being cut by restriction enzymes. The short answer is that it locks itself from being cut through a chemical process called DNA methylation.

76. Open Reading Frames
- Problem: Either strand of a DNA double helix can serve as the coding strand for RNA transcription. Hence, a given DNA string implies six total reading frames, or ways in which the same region of DNA can be translated into amino acids: three reading frames result from reading the string itself, whereas three more result from reading its reverse complement. An open reading frame (ORF) is one which starts from the start codon and ends by stop codon, without any other stop codons in between. Thus, a candidate protein string is derived by translating an open reading frame into amino acids until a stop codon is reached.
- Given: A DNA string s of length at most 1 kbp in FASTA format.
- Return:  Every distinct candidate protein string that can be translated from ORFs of s. Strings can be returned in any order.
- Sample Dataset
  - Input
  >Rosalind_99
  AGCCATGTAGCTAACTCAGGTTACATGGGGATGACCCCGCGACTTGGATTAGAGTCTCTTTTGGAATAAGCCTGAATGATCCGAGTAGCATCTCAG
  - Output
    MLLGSFRLIPKETLIQVAGSSPCNLS
    M
    MGMTPRLGLESLLE
    MTPRLGLESLLE
- Extra: Use /Users/cference/Code/scala-bioinformatics/src/main/scala/resources/orf_data.txt for data input

77. Matching Random Motifs
- Problem: Our aim in this problem is to determine the probability with which a given motif (a known promoter, say) occurs in a randomly constructed genome. Unfortunately, finding this probability is tricky; instead of forming a long genome, we will form a large collection of smaller random strings having the same length as the motif; these smaller strings represent the genome's substrings, which we can then test against our motif. Given a probabilistic event A , the complement of A is the collection A<sup>c</sup> of outcomes not belonging to A. Because A<sup>c</sup> takes place precisely when A does not, we may also call A<sup>c</sup> "not A ." For a simple example, if A is the event that a rolled die is 2 or 4, then Pr(A)=1/3 . A<sup>c</sup> is the event that the die is 1, 3, 5, or 6, and Pr(A<sup>c</sup>)=2/3. In general, for any event we will have the identity that Pr(A)+Pr(A<sup>c</sup>)=1.
- Given: A positive integer N≤100000, a number x between 0 and 1, and a DNA string s of length at most 10 bp.
- Return: The probability that if N random DNA strings having the same length as s are constructed with GC-content x (see “Introduction to Random Strings”), then at least one of the strings equals s. We allow for the same random string to be created more than once.
- Sample Dataset:
  - Input
    90000 0.6
    ATAGCCGA
  - Output:
  0.689
- Extra: Use /Users/cference/Code/scala-bioinformatics/src/main/scala/resources/rstr_data.txt for file input

78. Expected Number of Restriction Sites
- Problem: Say that you place a number of bets on your favorite sports teams. If their chances of winning are 0.3, 0.8, and 0.6, then you should expect on average to win 0.3 + 0.8 + 0.6 = 1.7 of your bets (of course, you can never win exactly 1.7!). More generally, if we have a collection of events A<sub>1</sub>,A<sub>2</sub>,…,A<sub>n</sub>, then the expected number of events occurring is Pr(A<sub>1</sub>)+Pr(A<sub>2</sub>)+⋯+Pr(A<sub>n</sub>) (consult the note following the problem for a precise explanation of this fact). In this problem, we extend the idea of finding an expected number of events to finding the expected number of times that a given string occurs as a substring of a random string.
- Given: A positive integer n (n≤1,000,000), a DNA string s of even length at most 10, and an array A of length at most 20, containing numbers between 0 and 1.
- Return: An array B having the same length as A in which B[i] represents the expected number of times that s will appear as a substring of a random DNA string t of length n, where t is formed with GC-content A[i] (see “Introduction to Random Strings”).
- Mathematical Details: In this problem, we are speaking of an expected number of events; how can we tie this into the definition of expected value that we already have from “Calculating Expected Offspring”? The answer relies on a slick mathematical trick. For any event A, we can form a random variable for A, called an indicator random variable I<sub>A</sub>. For an outcome x, I<sub>A(</sub>x)=1 when x belongs to A and I<sub>A</sub>(x)=0 when x belongs to A<sub>c</sub>. For an indicator random variable I<sub>A</sub>(x)=1, verify that E(I<sub>A</sub>)=Pr(A). You should also verify from our original formula for expected value that for any two random variables X and Y, E(X+Y) is equal to E(X)+E(Y). As a result, the expected number of events A<sub>1</sub>,A<sub>2</sub>,…,A<sub>m</sub> occurring, or E(I<sub>A1</sub>+I<sub>A2</sub>+⋯+I<sub>Am</sub>), reduces to Pr(A<sub>1</sub>)+Pr(A<sub>2</sub>)+⋯+Pr(A<sub>m</sub>).
- Sample Dataset
  - Input
    10
    AG
    0.25 0.5 0.75
  - Output
    0.422 0.563 0.422
- Extra: Use /Users/cference/Code/scala-bioinformatics/src/main/scala/resources/eval_data.txt for data input

79. Introduction to Pattern Matching
- Problem: Given a collection of strings, their trie (often pronounced "try" to avoid ambiguity with the general term tree) is a rooted tree formed as follows. For every unique first symbol in the strings, an edge is formed connecting the root to a new vertex. This symbol is then used to label the edge. We may then iterate the process by moving down one level as follows. Say that an edge connecting the root to a node v is labeled with 'A'; then we delete the first symbol from every string in the collection beginning with 'A' and then treat v as our root. We apply this process to all nodes that are adjacent to the root, and then we move down another level and continue. See Figure 1 for an example of a trie. As a result of this method of construction, the symbols along the edges of any path in the trie from the root to a leaf will spell out a unique string from the collection, as long as no string is a prefix of another in the collection (this would cause the first string to be encoded as a path terminating at an internal node).
- Given: A list of at most 100 DNA strings of length at most 100 bp, none of which is a prefix of another.
- Return: The adjacency list corresponding to the trie T for these patterns, in the following format. If T has n nodes, first label the root with 1 and then label the remaining nodes with the integers 2 through n in any order you like. Each edge of the adjacency list of T will be encoded by a triple containing the integer representing the edge's parent node, followed by the integer representing the edge's child node, and finally the symbol labeling the edge.
- Sample Dataset
  - Input
    ATAGA
    ATC
    GAT
  - Output
    1 2 A
    2 3 T
    3 4 A
    4 5 G
    5 6 A
    3 7 C
    1 8 G
    8 9 A
    9 10 T
- Extra: Use /Users/cference/Code/scala-bioinformatics/src/main/scala/resources/trie_data.txt for input data

80. Creating a Restriction Map
- Problem: For a set X containing numbers, the difference multiset of X is the multiset ΔX defined as the collection of all positive differences between elements of X. As a quick example, if X={2,4,7}, then we will have that ΔX={2,3,5}. If X contains n elements, then ΔX will contain one element for each pair of elements from X, so that ΔX contains (n2) elements (see combination statistic). You may note the similarity between the difference multiset and the Minkowski difference X⊖X, which contains the elements of ΔX and their negatives. For the above set X, X⊖X is {−5,−3,−2,2,3,5}. In practical terms, we can easily obtain a multiset L corresponding to the distances between restriction sites on a chromosome. If we can find a set X whose difference multiset ΔX is equal to L, then X will represent possible locations of these restriction sites.
- Given: A multiset L containing (<sup>n</sup><sub>2</sub>) positive integers for some positive integer n.
- Return: A set X containing n nonnegative integers such that ΔX=L .
- Sample Dataset
  - Input: 2 2 3 3 4 5 6 7 8 10
  - Output: 0 2 4 7 10
- Extra: Use /Users/cference/Code/scala-bioinformatics/src/main/scala/resources/pdpl_data.txt for data input

81. Finding the Longest Multiple Repeat
- Problem: A repeated substring of a string s of length n is simply a substring that appears in more than one location of s; more specifically, a k-fold substring appears in at least k distinct locations. The suffix tree of s, denoted T(s), is defined as follows:
  - T(s) is a rooted tree having exactly n leaves. 
  - Every edge of T(s) is labeled with a substring of s<sup>∗</sup>, where s<sup>∗</sup> is the string formed by adding a placeholder symbol $ to the end of s.
  - Every internal node of T(s) other than the root has at least two children; i.e., it has degree at least 3.
  - The substring labels for the edges leading from a node to its children must begin with different symbols.
  - By concatenating the substrings along edges, each path from the root to a leaf corresponds to a unique suffix of s<sup>∗</sup>.
- Given: A DNA string s (of length at most 20 kbp) with $ appended, a positive integer k, and a list of edges defining the suffix tree of s. Each edge is represented by four components:
  - the label of its parent node in T(s);
  - the label of its child node in T(s);
  - the location of the substring t of s∗ assigned to the edge; and 
  - the length of t
- Return: The longest substring of s that occurs at least k times in s. (If multiple solutions exist, you may return any single solution.)
- Sample Dataset
  - Input
    CATACATAC$
    2
    node1 node2 1 1
    node1 node7 2 1
    node1 node14 3 3
    node1 node17 10 1
    node2 node3 2 4
    node2 node6 10 1
    node3 node4 6 5
    node3 node5 10 1
    node7 node8 3 3
    node7 node11 5 1
    node8 node9 6 5
    node8 node10 10 1
    node11 node12 6 5
    node11 node13 10 1
    node14 node15 6 5
    node14 node16 10 1
  - Output: CATAC
- Hint: How can repeated substrings of s be located in T(s)?
- Extra: Use /Users/cference/Code/scala-bioinformatics/src/main/scala/resources/lrep_data.txt for data input

82. Encoding Suffix Trees
- Problem: Given a string s having length n, recall that its suffix tree T(s) is defined by the following properties:
  - T(s) is a rooted tree having exactly n leaves. 
  - Every edge of T(s) is labeled with a substring of s<sup>∗</sup>, where s<sup>∗</sup> is the string formed by adding a placeholder symbol $ to the end of s.
  - Every internal node of T(s) other than the root has at least two children; i.e., it has degree at least 3.
  - The substring labels for the edges leading down from a node to its children must begin with different symbols.
  - By concatenating the substrings along edges, each path from the root to a leaf corresponds to a unique suffix of s<sup>∗</sup>.
- Given: A DNA string s of length at most 1kbp
- Return: The substrings of s<sup>∗</sup> encoding the edges of the suffix tree for s. You may list these substrings in any order.
- Sample Dataset
  - Input: ATAAATG$
  - Output
    AAATG$
    G$
    T
    ATG$
    TG$
    A
    A
    AAATG$
    G$
    T
    G$
    $
- Extra: Use /Users/cference/Code/scala-bioinformatics/src/main/scala/resources/suff_data.txt for data input

83. Linguistic Complexity of a Genome
- Problem: Given a length n string s formed over an alphabet 𝒜 of size a, let the "substring count" sub(s) denote the total number of distinct substrings of s. Furthermore, let the "maximum substring count" m(a,n)denote the maximum number of distinct substrings that could appear in a string of length n formed over 𝒜. The linguistic complexity of s (written lc(s)) is equal to sub(s)/m(a,n); in other words, lc(s) represents the percentage of observed substrings of s to the total number that are theoretically possible. Note that 0<lc(s)<1, with smaller values of lc(s) indicating that s is more repetitive. As an example, consider the DNA string (a=4) s=ATTTGGATT. In the following table, we demonstrate that lc(s)=35/40=0.875 by considering the number of observed and possible length k substrings of s, which are denoted by sub<sub>k</sub>(s)and m(a,k,n), respectively. (Observe that m(a,n)=∑<sup>n</sup><sub>k=1</sub>m(a,k,n)=40 and sub(s)=∑<sup>n</sup><sub>k=1</sub>sub<sub>k</sub>(s)=35.)

|k|subk(s)|m(a,k,n)|
|---|---|---|
|1|	3|	4|
|2|	5|	8|
|3|	6|	7|
|4|	6|	6|
|5|	5|	5|
|6|	4|	4|
|7|	3|	3|
|8|	2|	2|
|9|	1|	1|
|Total|	35|	40|

- Given: A DNA string s of length at most 100 kbp.
- Return: The linguistic complexity lc(s)
- Sample Dataset
  - Input: ATTTGGATT
  - Output: 0.875
- Hint: Why does this problem follow “Encoding Suffix Trees”?
- Extra: Use /Users/cference/Code/scala-bioinformatics/src/main/scala/resources/ling_data.txt for data input

84. Enumerating Unrooted Binary Trees
- Problem: Recall the definition of Newick format from “Distances in Trees” as a way of encoding trees. See Figure 1 for an example of Newick format applied to an unrooted binary tree whose five leaves are labeled (note that the same tree can have multiple Newick representations).
- Given: A collection of species names representing n taxa.
- Return: A list containing all unrooted binary trees whose leaves are these n taxa. Trees should be given in Newick format, with one tree on each line; the order of the trees is unimportant.
- Sample Dataset
  - Input
    dog cat mouse elephant
  - Output
    (((mouse,cat),elephant))dog;
    (((elephant,mouse),cat))dog;
    (((elephant,cat),mouse))dog;
- Extra: Use /Users/cference/Code/scala-bioinformatics/src/main/scala/resources/eubt_data.txt for data input

85. Counting Rooted Binary Trees
- Problem: As in the case of unrooted trees, say that we have a fixed collection of n taxa labeling the leaves of a rooted binary tree T. You may like to verify that (by extension of “Counting Phylogenetic Ancestors”) such a tree will contain n−1 internal nodes and 2n−2 total edges. Any edge will still encode a split of taxa; however, the two splits corresponding to the edges incident to the root of T will be equal. We still consider two trees to be equivalent if they have the same splits (which requires that they must also share the same duplicated split to be equal). Let B(n) represent the total number of distinct rooted binary trees on n labeled taxa.
- Given: A positive integer n (n≤1000).
- Return: The value of B(n) modulo 1,000,000.
- Sample Dataset
  - Input: 4
  - Output: 15
- Use /Users/cference/Code/scala-bioinformatics/src/main/scala/resources/root_data.txt for data input

86. Inferring GEnotype from a Pedigree
- Problem: A rooted binary tree can be used to model the pedigree of an individual. In this case, rather than time progressing from the root to the leaves, the tree is viewed upside down with time progressing from an individual's ancestors (at the leaves) to the individual (at the root). An example of a pedigree for a single factor in which only the genotypes of ancestors are given is shown in Figure 1.
- Given: A rooted binary tree T in Newick format encoding an individual's pedigree for a Mendelian factor whose alleles are A (dominant) and a (recessive).
- Return: Three numbers between 0 and 1, corresponding to the respective probabilities that the individual at the root of T will exhibit the "AA", "Aa" and "aa" genotypes.
- Sample Dataset
  - Input
    ((((Aa,aa),(Aa,Aa)),((aa,aa),(aa,AA))),Aa);
  - Output
    0.156 0.5 0.344
- Extra: Use /Users/cference/Code/scala-bioinformatics/src/main/scala/resources/mend_data.txt for data input

87. Sex-Linked Inheritance
- Problem: The conditional probability of an event A given another event B, written Pr(A∣B), is equal to Pr(A and B) divided by Pr(B). Note that if A and B are independent, then Pr(A and B) must be equal to Pr(A)×Pr(B), which results in Pr(A∣B)=Pr(A) . This equation offers an intuitive view of independence: the probability of A, given the occurrence of event B, is simply the probability of A (which does not depend on B). In the context of sex-linked traits, genetic equilibrium requires that the alleles for a gene k are uniformly distributed over the males and females of a population. In other words, the distribution of alleles is independent of sex.
- Given: An array A of length n for which A[k] represents the proportion of males in a population exhibiting the k-th of n total recessive X-linked genes. Assume that the population is in genetic equilibrium for all n genes.
- Return: An array B of length n in which B[k] equals the probability that a randomly selected female will be a carrier for the k-th gene.
- Sample Dataset:
  - Input:
    0.1 0.5 0.8
  - Output
    0.18 0.5 0.32
- Extra: Use /Users/cference/Code/scala-bioinformatics/src/main/scala/resources/sexl_data.txt for data input

88. Identifying Maximal Repeats
- Problem: A maximal repeat of a string s is a repeated substring t of s having two occurrences t<sub>1</sub> and <sub>t2</sub> such that <sub>t1</sub> and t<sub>2</sub> cannot be extended by one symbol in either direction in s and still agree. For example, "AG" is a maximal repeat in "TAGTTAGCGAGA" because even though the first two occurrences of "AG" can be extended left into "TAG", the first and third occurrences differ on both sides of the repeat; thus, we conclude that "AG" is a maximal repeat. Note that "TAG" is also a maximal repeat of "TAGTTAGCGAGA", since its only two occurrences do not still match if we extend them in either direction.
- Given: A DNA string s of length at most 1 kbp.
- Return: A list containing all maximal repeats of s having length at least 20.
- Sample Dataset:
  - Input
    TAGAGATAGAATGGGTCCAGAGTTTTGTAATTTCCATGGGTCCAGAGTTTTGTAATTTATTATATAGAGATAGAATGGGTCCAGAGTTTTGTAATTTCCATGGGTCCAGAGTTTTGTAATTTAT
  - Output
    TAGAGATAGAATGGGTCCAGAGTTTTGTAATTTCCATGGGTCCAGAGTTTTGTAATTTAT
    ATGGGTCCAGAGTTTTGTAATTT
- Hint: How can we use the suffix tree of s to find maximal repeats?
- Extra: Use /Users/cference/Code/scala-bioinformatics/src/main/scala/resources/mrep_data.txt for data input

89. Maximizing the Gap Symbols of an Optimal Alignment
- Problem: For the computation of an alignment score generalizing the edit alignment score, let m denote the score assigned to matched symbols, d denote the score assigned to mismatched non-gap symbols, and g denote the score assigned a symbol matched to a gap symbol '-' (i.e., g is a linear gap penalty).
- Given: Two DNA strings s and t in FASTA format (each of length at most 5000 bp).
- Return: The maximum number of gap symbols that can appear in any maximum score alignment of s and t with score parameters satisfying m>0 , d<0 , and g<0 .
- Sample Dataset
  - Input
  >Rosalind_92
  AACGTA
  >Rosalind_47
  ACACCTA
  - Output: 3
- Extra: Use /Users/cference/Code/scala-bioinformatics/src/main/scala/resources/mgap_data.txt for data input

90. Matching a Spectrum to a Protein
- Problem: The complete spectrum of a weighted string s is the multiset S[s] containing the weights of every prefix and suffix of s.
- Given: A positive integer n followed by a collection of n protein strings s<sub>1</sub>, s<sub>2</sub>, ..., s<sub>n</sub> and a multiset R of positive numbers (corresponding to the complete spectrum of some unknown protein string).
- Return: The maximum multiplicity of R⊖S[sk] taken over all strings s<sub>k</sub>, followed by the string s<sub>k</sub> for which this maximum multiplicity occurs (you may output any such value if multiple solutions exist).
- Sample Dataset:
  - Input
    4
    GSDMQS
    VWICN
    IASWMQS
    PVSMGAD
    445.17838
    115.02694
    186.07931
    314.13789
    317.1198
    215.09061
  - Output:
    3
    IASWMQS
- Extra: Use /Users/cference/Code/scala-bioinformatics/src/main/scala/resources/prsm_data.txt for data input

91. Inferring Peptide from Full Spectrum
- Problem: Say that we have a string s containing t as an internal substring, so that there exist nonempty substrings s<sub>1</sub> and s<sub>2</sub> of s such that s can be written as s<sub>1</sub>ts<sub>2</sub>. A t-prefix contains all of s<sub>1</sub> and none of s<sub>2</sub>; likewise, a t-suffix contains all of s<sub>2</sub> and none of s<sub>1</sub>.
- Given: A list L containing 2n+3 positive real numbers (n≤100). The first number in L is the parent mass of a peptide P, and all other numbers represent the masses of some b-ions and y-ions of P (in no particular order). You may assume that if the mass of a b-ion is present, then so is that of its complementary y-ion, and vice-versa.
- Return: A protein string t of length n for which there exist two positive real numbers w<sub>1</sub> and w<sub>2</sub> such that for every prefix p and suffix s of t, each of w(p)+w<sub>1</sub> and w(s)+w<sub>2</sub> is equal to an element of L. (In other words, there exists a protein string whose t-prefix and t-suffix weights correspond to the non-parent mass values of L.) If multiple solutions exist, you may output any one.
- Sample Dataset:
  - Input
    1988.21104821
    610.391039105
    738.485999105
    766.492149105
    863.544909105
    867.528589105
    992.587499105
    995.623549105
    1120.6824591
    1124.6661391
    1221.7188991
    1249.7250491
    1377.8200091
  - Output
    KEKEP
- Extra: Use /Users/cference/Code/scala-bioinformatics/src/main/scala/resources/full_data.txt for data input

Constraints / non-goals:
- No auth/roles (for now)
- This is a framework, so no UI
- Provide Given/When/Then acceptance criteria with at least 2 edge cases per feature.
- Use SBT for building the project.
- Use the IO Monad for side effects
- Functional Programming highly desired over imperative style
- Scala Best practices encouraged
- Usage of ADTs for Domain types are a must 

