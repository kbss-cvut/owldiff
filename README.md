# OWLDiff [![Java CI with Maven](https://github.com/psiotwo/owldiff/actions/workflows/maven.yml/badge.svg?branch=master)](https://github.com/psiotwo/owldiff/actions/workflows/maven.yml)

A tool to perform a 2-way diff of OWL ontologies.

## Requirements
- Java 14

## Run
In order to run OWLDiff run `mvn clean install` and
- for CLI: `cd owldiff-cli && mvn assembly:assembly` to create an executable JAR file in `target/owldiff-cli-<VERSION>-jar-with-dependencies.jar`
- for Desktop UI: `cd owldiff-standalone && mvn assembly:assembly` to create a ZIP file`target/owldiff-standalone-<VERSION>-dist-bin.zip`. Unzip it and run `bin/owldiff`.

## Features
- syntactic diff/merge functionality
- explanations for inferred axioms
- various visualization options - Manchester/DL syntax, classified view, plain axiom list
- fully semantic diff for EL using the CEX algorithm.
- easy Subversion integration
    
### PLAIN (SYNTACTIC) DIFF
- is provides an axiom by axiom difference of the ontologies
- different axioms are shown in 'green', the common parts are blue (and hidden by default).
		
### ENTAILMENT CHECK
- for each axiom that is not contained in the other ontology an entailment check tests whether it can be derived from the other ontology or not.
- entailed axioms are shown in 'red' 
- for inconsistent ontologies the entailment check is disabled

### CEX DIFF
- if either of the ontologies is not EL, no CEX is performed
- axioms with colored background denote those that appear either in DiffR or DiffL list - they represent semantic differences between the classes the semantics of whose has changed 
	  (as described in http://www.webont.org/owled/2008dc/papers/owled2008dc_paper_12.pdf)

### ENTAILMENT EXPLANATIONS
- the incremental algorithm for single explanation generation is used (as described in Křemen, P. - Kouba, Z.: Incremental Approach to Error Explanations in Ontologies. In Proceedings of I-KNOW `07. Graz: Graz University of Technology, 2007, p. 332-339. ISSN 0948-695X )

## Known Limitations
- imports are currently NOT resolved
- just one explanation generated for a single axiom - TODO provide the possibility to get all explanations
- usually the only sensible use is to compare ontologies with the same URI - no check is performed to find out whether the ontology/class/property/individual URI changed.

## How to run it?
- to show the GUI without setting the input files in advance run
			'java -jar owldiff.jar' (or the convenience script 'owldiff' on linux)
- to perform the diff of file/URI <u1> and file/URI <u2> run
			'java -jar owldiff.jar <u1> <u2>' (or the convenience script 'owldiff <u1> <u2>' on linux)

## Resources
- Kremen, P. & Šmíd, M. & Kouba, Z.. (2011). OWLDiff: A practical tool for comparison and merge of owl ontologies. Proceedings of the 10th International Workshop on Web Semantics. 229-233. 
- Kremen, P. & Kouba, Z.. (2009). Incremental Approach to Error Explanations in Ontologies. Networked Knowledge - Networked Media - Integrating Knowledge Management 2009: 171-185


* * *
2020 Czech Technical University, Knowledge Based and Software Systems Group
