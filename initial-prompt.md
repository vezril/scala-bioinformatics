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

Constraints / non-goals:
- No auth/roles (for now)
- This is a framework, so no UI
- Provide Given/When/Then acceptance criteria with at least 2 edge cases per feature.
- Use SBT for building the project.
- Use the IO Monad for side effects
- Functional Programming highly desired over imperative style
- Scala Best practices encouraged
- Usage of ADTs for Domain types are a must 

