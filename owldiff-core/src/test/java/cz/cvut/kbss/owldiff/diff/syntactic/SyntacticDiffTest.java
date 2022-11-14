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

package cz.cvut.kbss.owldiff.diff.syntactic;

import cz.cvut.kbss.owldiff.change.OWLChange;
import cz.cvut.kbss.owldiff.diff.OWLDiff;
import cz.cvut.kbss.owldiff.diff.OWLDiffOutput;
import cz.cvut.kbss.owldiff.ontology.OntologyHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.util.Set;

public class SyntacticDiffTest {

    private static final String iri = "http://kbss.felk.cvut.cz/ontologies/2012/owldiff-test-syntactic.owl";
    private static final String ns = iri + "#";

    @Test
    public void testDiffBasic() throws Exception {

        final OWLOntology o = OWLManager.createOWLOntologyManager().createOntology(IRI.create(iri));
        final OWLDataFactory oF = o.getOWLOntologyManager().getOWLDataFactory();
        final OWLClass oCA1 = oF.getOWLClass(ns, "A1");
        final OWLClass oCA2 = oF.getOWLClass(ns, "A2");
        final OWLObjectProperty oR = oF.getOWLObjectProperty(ns, "R");

        final OWLOntologyManager om = o.getOWLOntologyManager();
        om.addAxiom(o, oF.getOWLDeclarationAxiom(oCA1));
        om.addAxiom(o, oF.getOWLDeclarationAxiom(oCA2));
        om.addAxiom(o, oF.getOWLDeclarationAxiom(oR));
        om.addAxiom(o, oF.getOWLSubClassOfAxiom(oCA1, oF.getOWLObjectSomeValuesFrom(oR, oCA2)));

        final OWLOntology u = OWLManager.createOWLOntologyManager().createOntology(IRI.create(iri));

        final OWLDataFactory uF = o.getOWLOntologyManager().getOWLDataFactory();
        final OWLClass uCA1 = uF.getOWLClass(ns, "A1");
        final OWLObjectProperty uR = uF.getOWLObjectProperty(ns, "R");

        final OWLOntologyManager um = u.getOWLOntologyManager();
        um.addAxiom(u, uF.getOWLDeclarationAxiom(uCA1));
        um.addAxiom(u, uF.getOWLDeclarationAxiom(uR));
        um.addAxiom(u, uF.getOWLSubClassOfAxiom(uCA1, uF.getOWLObjectSomeValuesFrom(uR, uCA1)));
        um.addAxiom(u, uF.getOWLSubClassOfAxiom(uCA1, uF.getOWLThing()));

        final OWLDiff diff = new SyntacticDiff(new OntologyHandler() {
            public OWLOntology getOriginalOntology() {
                return o;
            }

            public OWLOntology getUpdateOntology() {
                return u;
            }
        });

        final OWLDiffOutput diffOutput = diff.diff();
        final Set<? extends OWLChange> changes = diffOutput.getOWLChanges();

        Assertions.assertNotNull(changes);
        Assertions.assertEquals(4, changes.size());
    }
}
