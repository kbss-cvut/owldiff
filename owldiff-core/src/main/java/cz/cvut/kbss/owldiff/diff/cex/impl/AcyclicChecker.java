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

package cz.cvut.kbss.owldiff.diff.cex.impl;

import cz.cvut.kbss.owldiff.OWLDiffException;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLAxiomVisitorAdapter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: marek
 * Date: 10/8/12
 * Time: 6:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class AcyclicChecker extends OWLAxiomVisitorAdapter {

    private OWLOntology onto;
    private Set<OWLAxiom> axioms;
    private Iterator<OWLAxiom> axiomIter;
    private boolean changed = true;
    private Set<OWLClass> defined;

    public AcyclicChecker(OWLOntology o) {
        onto = o;
        defined = new HashSet<OWLClass>();
    }

    public void check() throws OWLDiffException {
        for (OWLClass c : onto.getClassesInSignature()) {
            if (onto.getAxioms(c).isEmpty()) {
                defined.add(c);
            }
        }

        axioms = new HashSet<OWLAxiom>(onto.getAxioms());
        axiomIter = axioms.iterator();
        while (changed) {
            changed = false;
            while (axiomIter.hasNext()) {
                axiomIter.next().accept(this);
            }
        }
        if (!axioms.isEmpty()) {
            throw new OWLDiffException(OWLDiffException.Reason.INCOMPATIBLE_ONTOLOGY, "Ontology " + onto.getOntologyID().getOntologyIRI() + " is not acyclic");
        }
    }

    @Override
    public void visit(OWLSubClassOfAxiom axiom) {
        process((OWLClass) axiom.getSubClass(), axiom.getSuperClass());
    }

    @Override
    public void visit(OWLEquivalentClassesAxiom axiom) {
        Iterator<OWLClassExpression> i = axiom.getClassExpressions().iterator();
        OWLClassExpression e1 = i.next(), e2 = i.next();
        if (e1 instanceof OWLClass) {
            process((OWLClass) e1, e2);
        }
        if (e2 instanceof OWLClass) {
            process((OWLClass) e2, e1);
        }
    }

    private void process(OWLClass left, OWLClassExpression right) {
        Set<OWLClass> rs = new ClassExpVisitor(right).getClasses();
        if (defined.containsAll(rs)) {
            defined.add(left);
            changed = true;
            axiomIter.remove();
        }
    }

    private class ClassExpVisitor implements OWLClassExpressionVisitor {

        private Set<OWLClass> classes;

        public ClassExpVisitor(OWLClassExpression ex) {
            classes = new HashSet<OWLClass>();
            ex.accept(this);
        }

        public void visit(OWLClass owlClass) {
            classes.add(owlClass);
        }

        public void visit(OWLObjectSomeValuesFrom owlObjectSomeValuesFrom) {
            owlObjectSomeValuesFrom.getFiller().accept(this);
        }

        public void visit(OWLObjectIntersectionOf owlObjectIntersectionOf) {
            for (OWLClassExpression e : owlObjectIntersectionOf.getOperands()) {
                e.accept(this);
            }
        }

        public void visit(OWLObjectUnionOf owlObjectUnionOf) {
        }

        public void visit(OWLObjectComplementOf owlObjectComplementOf) {
        }

        public void visit(OWLObjectAllValuesFrom owlObjectAllValuesFrom) {
        }

        public void visit(OWLObjectHasValue owlObjectHasValue) {
        }

        public void visit(OWLObjectMinCardinality owlObjectMinCardinality) {
        }

        public void visit(OWLObjectExactCardinality owlObjectExactCardinality) {
        }

        public void visit(OWLObjectMaxCardinality owlObjectMaxCardinality) {
        }

        public void visit(OWLObjectHasSelf owlObjectHasSelf) {
        }

        public void visit(OWLObjectOneOf owlObjectOneOf) {
        }

        public void visit(OWLDataSomeValuesFrom owlDataSomeValuesFrom) {
        }

        public void visit(OWLDataAllValuesFrom owlDataAllValuesFrom) {
        }

        public void visit(OWLDataHasValue owlDataHasValue) {
        }

        public void visit(OWLDataMinCardinality owlDataMinCardinality) {
        }

        public void visit(OWLDataExactCardinality owlDataExactCardinality) {
        }

        public void visit(OWLDataMaxCardinality owlDataMaxCardinality) {
        }

        public Set<OWLClass> getClasses() {
            return classes;
        }
    }
}
