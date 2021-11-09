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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLAxiomVisitor;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitor;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

/**
 * Created with IntelliJ IDEA.
 * User: marek
 * Date: 10/8/12
 * Time: 6:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class AcyclicChecker implements OWLAxiomVisitor {

    private final OWLOntology onto;
    private Iterator<OWLAxiom> axiomIter;
    private boolean changed;
    private final Set<OWLClass> defined;

    public AcyclicChecker(final OWLOntology o) {
        onto = o;
        defined = new HashSet<>();
        changed = true;
    }

    public void check() throws OWLDiffException {
        onto.classesInSignature()
            .filter(c -> onto.axioms(c).findAny().isEmpty())
            .forEach(defined::add);

        Set<OWLAxiom> axioms = onto.axioms().collect(Collectors.toSet());
        while (changed) {
            changed = false;
            axiomIter = axioms.iterator();
            while (axiomIter.hasNext()) {
                axiomIter.next().accept(this);
            }
        }
        if (!axioms.isEmpty()) {
            throw new OWLDiffException(OWLDiffException.Reason.INCOMPATIBLE_ONTOLOGY,
                "Ontology " + onto.getOntologyID().getOntologyIRI() + " is not acyclic");
        }
    }

    @Override
    public void visit(final OWLSubClassOfAxiom axiom) {
        process((OWLClass) axiom.getSubClass(), axiom.getSuperClass());
    }

    @Override
    public void visit(final OWLEquivalentClassesAxiom axiom) {
        final Iterator<OWLClassExpression> i = axiom.classExpressions().iterator();
        final OWLClassExpression e1 = i.next(), e2 = i.next();
        if (e1 instanceof OWLClass) {
            process((OWLClass) e1, e2);
        }
        if (e2 instanceof OWLClass) {
            process((OWLClass) e2, e1);
        }
    }

    private void process(final OWLClass left, final OWLClassExpression right) {
        final Set<OWLClass> rs = new ClassExpVisitor(right).getClasses();
        if (defined.containsAll(rs)) {
            defined.add(left);
            changed = true;
            axiomIter.remove();
        }
    }

    private static class ClassExpVisitor implements OWLClassExpressionVisitor {

        private final Set<OWLClass> classes;

        public ClassExpVisitor(OWLClassExpression ex) {
            classes = new HashSet<>();
            ex.accept(this);
        }

        public void visit(final OWLClass owlClass) {
            classes.add(owlClass);
        }

        public void visit(final OWLObjectSomeValuesFrom owlObjectSomeValuesFrom) {
            owlObjectSomeValuesFrom.getFiller().accept(this);
        }

        public void visit(final OWLObjectIntersectionOf owlObjectIntersectionOf) {
            owlObjectIntersectionOf.operands().forEach(operand -> operand.accept(this));
        }

        public Set<OWLClass> getClasses() {
            return classes;
        }
    }
}
