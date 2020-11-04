/*
 * Copyright (c) 2012 Czech Technical University in Prague.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package cz.cvut.kbss.owldiff.diff.owlapi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import cz.cvut.kbss.explanation.SingleMUSAlgorithm;
import cz.cvut.kbss.explanation.reiter.BlackBoxSingleMUS;
import cz.cvut.kbss.owldiff.ExplanationManager;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

public class BBOWLAPIExplanationManager implements ExplanationManager {

    private final OWLOntology o;

    private final OWLAPIEntailmentTest t;

    private SingleMUSAlgorithm<OWLAxiom> single = null;

    private Set<OWLAxiom> a = null;

    public BBOWLAPIExplanationManager(
            final OWLOntology o) {
        this.o = o;
        this.t = new OWLAPIEntailmentTest();
    }

    public void setAxiom(OWLAxiom ax) {
        this.t.setAxiom(ax);
        this.a = null;
    }

    public Set<OWLAxiom> getNextExplanation() {
        if (this.a == null) {
            this.single = new BlackBoxSingleMUS<OWLAxiom>(t);
            this.a = single.find(new ArrayList<OWLAxiom>(o.getLogicalAxioms()));
            if (a == null) {
                return new HashSet<OWLAxiom>();
            }
        }
        return a;
    }
}
