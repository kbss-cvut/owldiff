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
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: kremep1
 * Date: 10/3/12
 * Time: 8:01 AM
 * To change this template use File | Settings | File Templates.
 */
public class SyntacticDiffTest {

    OntologyHandler h;

    @Before
    public void setUp() throws Exception {
        IRI iri = IRI.create("http://kbss.felk.cvut.cz/ontologies/2012/owldiff-test-syntactic.owl");
        final OWLOntology o = OWLManager.createOWLOntologyManager().createOntology(iri);
        final OWLDataFactory oF = o.getOWLOntologyManager().getOWLDataFactory();
        final OWLClass oClassA1 = oF.getOWLClass(IRI.create(iri.toString() + "#A1"));
        final OWLClass oClassA2 = oF.getOWLClass(IRI.create(iri.toString() + "#A2"));
        final OWLObjectProperty oPropR = oF.getOWLObjectProperty(IRI.create(iri.toString() + "#R"));

        o.getOWLOntologyManager().addAxiom(o, oF.getOWLDeclarationAxiom(oClassA1));
        o.getOWLOntologyManager().addAxiom(o, oF.getOWLDeclarationAxiom(oClassA2));
        o.getOWLOntologyManager().addAxiom(o, oF.getOWLDeclarationAxiom(oPropR));

        o.getOWLOntologyManager().addAxiom(o, oF.getOWLSubClassOfAxiom(oClassA1, oF.getOWLObjectSomeValuesFrom(oPropR, oClassA2)));

        final OWLOntology u = OWLManager.createOWLOntologyManager().createOntology(iri);

        final OWLDataFactory uF = o.getOWLOntologyManager().getOWLDataFactory();
        final OWLClass uClassA1 = uF.getOWLClass(IRI.create(iri.toString() + "#A1"));
        final OWLObjectProperty uPropR = uF.getOWLObjectProperty(IRI.create(iri.toString() + "#R"));

        u.getOWLOntologyManager().addAxiom(u, uF.getOWLDeclarationAxiom(uClassA1));
        u.getOWLOntologyManager().addAxiom(u, uF.getOWLDeclarationAxiom(uPropR));

        u.getOWLOntologyManager().addAxiom(u, uF.getOWLSubClassOfAxiom(uClassA1, uF.getOWLObjectSomeValuesFrom(uPropR, uClassA1)));
        u.getOWLOntologyManager().addAxiom(u, uF.getOWLSubClassOfAxiom(uClassA1, uF.getOWLThing()));


        h = new OntologyHandler() {
            public OWLOntology getOriginalOntology() {
                return o;
            }

            public OWLOntology getUpdateOntology() {
                return u;
            }
        };
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testDiffBasic() throws Exception {
        OWLDiff diff = new SyntacticDiff(h);
        OWLDiffOutput diffOutput = diff.diff();
        Set<? extends OWLChange> changes = diffOutput.getOWLChanges();

        Assert.assertNotNull(changes);
        Assert.assertEquals(changes.size(), 4);
    }
}
