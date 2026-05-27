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

Constraints / non-goals:
- No auth/roles (for now)
- This is a framework, so no UI
- Provide Given/When/Then acceptance criteria with at least 2 edge cases per feature.
- Use SBT for building the project.
- Use the IO Monad for side effects
- Functional Programming highly desired over imperative style
- Scala Best practices encouraged
- Usage of ADTs for Domain types are a must 

