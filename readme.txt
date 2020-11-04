-------------
OWLDiff 0.1.1
-------------

A tool to perform a 2-way diff of OWL ontologies.

****************
* REQUIREMENTS *
****************
	- Java 1.5

************
* FEATURES *
************  
- syntactic diff/merge functionality
- explanations for inferred axioms
- various visualization options - Manchester/DL syntax, classified view, plain axiom list
- fully semantic diff for EL using the CEX algorithm.
- easy Subversion integration
    
* PLAIN (SYNTACTIC) DIFF *
	- is provides an axiom by axiom difference of the ontologies
	- different axioms are shown in 'green', the common parts are blue (and hidden by default).
		
* ENTAILMENT CHECK *
	- for each axiom that is not contained in the other ontology an entailment check tests whether it can be derived from the other ontology or not.
	- entailed axioms are shown in 'red' 
	- for inconsistent ontologies the entailment check is disabled

* CEX DIFF *
	- if either of the ontologies is not EL, no CEX is performed
	- axioms with colored background denote those that appear either in DiffR or DiffL list - they represent semantic differences between the classes the semantics of whose has changed 
	  (as described in http://www.webont.org/owled/2008dc/papers/owled2008dc_paper_12.pdf)

* ENTAILMENT EXPLANATIONS *
	- the incremental algorithm for single explanation generation is used (as described in Křemen, P. - Kouba, Z.: Incremental Approach to Error Explanations in Ontologies. In Proceedings of I-KNOW `07. Graz: Graz University of Technology, 2007, p. 332-339. ISSN 0948-695X )

Known Limitations :
	- imports are currently NOT resolved
	- just one explanation generated for a single axiom - TODO provide the possibility to get all explanations
	- usually the only sensible use is to compare ontologies with the same URI - no check is performed to find out whether the ontology/class/property/individual URI changed.

********************
* RUNNING THE DIFF *
********************
- to show the GUI without setting the input files in advance run
			'java -jar owldiff.jar' (or the convenience script 'owldiff' on linux)
- to perform the diff of file/URI <u1> and file/URI <u2> run
			'java -jar owldiff.jar <u1> <u2>' (or the convenience script 'owldiff <u1> <u2>' on linux)

===========================================================================
2008 Czech Technical University, Knowledge Based and Software Systems Group
Petr Kremen, Marek Smid, Jan Abrahamcik, Jaromir Pufler 
