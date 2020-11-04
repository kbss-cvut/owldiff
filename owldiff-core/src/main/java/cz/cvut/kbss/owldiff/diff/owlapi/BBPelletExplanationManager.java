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

import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import com.clarkparsia.owlapi.explanation.DefaultExplanationGenerator;
import com.clarkparsia.owlapi.explanation.ExplanationGenerator;
import com.clarkparsia.owlapi.explanation.SatisfiabilityConverter;
import com.clarkparsia.owlapi.explanation.util.SilentExplanationProgressMonitor;

import cz.cvut.kbss.owldiff.ExplanationManager;

public class BBPelletExplanationManager implements ExplanationManager {

    private final ExplanationGenerator g;
    private final SatisfiabilityConverter c;

    private OWLAxiom ax;

    private Set<OWLAxiom> a;

    public BBPelletExplanationManager(
            final OWLOntology o, final OWLReasonerFactory f) {
        this.c = new SatisfiabilityConverter(o.getOWLOntologyManager().getOWLDataFactory());
        this.g = new DefaultExplanationGenerator(o.getOWLOntologyManager(), f, o,
                new SilentExplanationProgressMonitor());
    }

    public void setAxiom(OWLAxiom ax) {
        this.ax = ax;
        a = null;
    }

    public Set<OWLAxiom> getNextExplanation() {
        if (a == null) {
            a = g.getExplanation(c.convert(ax));
        }
        return a;
    }
}
