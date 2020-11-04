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
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import cz.cvut.kbss.explanation.Test;
import cz.cvut.kbss.owldiff.diff.OWLDiffConfiguration;

public class OWLAPIEntailmentTest implements Test<OWLAxiom> {

    private static final Logger LOG = Logger
            .getLogger(OWLAPIEntailmentTest.class.getName());

//	private int i;

    private OWLAxiom toTest;

    public void setAxiom(final OWLAxiom a) {
        toTest = a;
    }

    public boolean test(Set<OWLAxiom> v) {
        try {
            final OWLOntologyManager m = OWLManager.createOWLOntologyManager();
            final OWLOntology o = m.createOntology();

            final List<AddAxiom> adds = new ArrayList<AddAxiom>();

            // add referenced named entities to avoid Pellet crash
            for (final OWLEntity e : toTest.getSignature()) {
                adds.add(new AddAxiom(o, m.getOWLDataFactory()
                        .getOWLDeclarationAxiom(e)));
            }

            for (final OWLAxiom a : v) {
                adds.add(new AddAxiom(o, a));
            }

            m.applyChanges(adds);

            return !OWLDiffConfiguration.getOWLReasoner(o).isEntailed(toTest);
        } catch (OWLOntologyCreationException e) {
            LOG.log(Level.SEVERE, e.toString(), e);
        } catch (OWLOntologyChangeException e) {
            LOG.log(Level.SEVERE, e.toString(), e);
        }

        return false;
    }
}
