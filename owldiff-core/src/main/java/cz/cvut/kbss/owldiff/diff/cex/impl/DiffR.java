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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.RemoveAxiom;

public class DiffR {

    private static final Logger LOG = Logger.getLogger(DiffR.class.getName());

    private OWLOntology original, update;
    private Sig origSig, updateSig, sigma;
    private Noimply nio, niu;
    private OWLOntologyManager updateManager;

    private Map<OWLClass, Set<OWLClass>> marks;
    private Set<OWLClass> leftOver = null;

    // private OWLOntologyManager upamanager;
    // private OWLDataFactory upafactory;
    // private OWLOntology updatePlusAlpha;
    // private final ExplanationManager<OWLAxiom> updatePlusAlphaEM;
    private OWLClassExpression intersOfSigmaAndAllSigma;

    public DiffR(OWLOntology original, OWLOntology update, Sig origSig, Sig updateSig, Sig sigma, Noimply nio, Noimply niu, OWLOntologyManager updateManager) {
        this.original = original;
        this.update = update;
        this.origSig = origSig;
        this.updateSig = updateSig;
        this.sigma = sigma;
        this.nio = nio;
        this.niu = niu;
        this.updateManager = updateManager;

        /*
           * upamanager = OWLManager.createOWLOntologyManager(); upafactory =
           * upamanager.getOWLDataFactory(); //nont =
           * manager.createOntology(ont.getURI()); updatePlusAlpha = upamanager.
           */

        // updatePlusAlphaEM = ;
        Set<OWLClassExpression> interset = new HashSet<OWLClassExpression>();
        interset.addAll(sigma.getSig());
        interset.add(niu.getAllSigma());
        // intersOfSigmaAndAllSigma = upafactory.getOWLObjectIntersectionOf(interset);
        intersOfSigmaAndAllSigma = niu.getFactory().getOWLObjectIntersectionOf(interset);
    }

    public void mark() {
        marks = new HashMap<OWLClass, Set<OWLClass>>();
        Set<OWLClass> usig = new HashSet<OWLClass>();
        usig.addAll(updateSig.getSig());
        Iterator<OWLClass> iter = usig.iterator();
        while (iter.hasNext()) {
            OWLClass e = iter.next();
            if (niu.pseudoPrimitive(niu.getAxioms(e))) {
                // usig.remove(e);
                iter.remove();
                Set<OWLClass> xis = new HashSet<OWLClass>();
                for (OWLClass xi : nio.getXi()) {
                    Set<OWLClass> prexi = nio.pre(xi);
                    Set<OWLClass> pree = niu.pre(e);
                    // if ((pree == null) || ((prexi != null) && prexi.containsAll(pree))) { // preU subset of preO
                    if ((prexi != null) && prexi.containsAll(pree)) { // preU subset of preO
                        xis.add(xi);
                    }
                }
                marks.put(e, xis);
            }
        }

        iter = usig.iterator();
        while (iter.hasNext()) {
            OWLClass e = iter.next();
            Set<OWLClassAxiom> axioms = niu.getAxioms(e);
            if (axioms.size() != 1) {
                continue;
            }
            OWLClassAxiom axiom = axioms.iterator().next();
            if (!(axiom instanceof OWLEquivalentClassesAxiom)) {
                continue;
            }
            OWLEquivalentClassesAxiom equAxiom = (OWLEquivalentClassesAxiom) axiom;
            if (equAxiom.getClassExpressions().size() != 2) {
                continue;
            }
            OWLClassExpression od = Noimply.otherDesription(equAxiom
                    .getClassExpressions(), e);
            if (!(od instanceof OWLObjectIntersectionOf)) {
                continue;
            }
            OWLObjectIntersectionOf inters = (OWLObjectIntersectionOf) od;

            Set<OWLClass> xis = new HashSet<OWLClass>();

            for (OWLClassExpression d : inters.getOperands()) {
                // TODO pouzit radeji visitor ???
                if (!(d instanceof OWLClass)) {
                    continue;
                }

                xis.addAll(marks.get(d));
            }

            marks.put(e, xis);
            iter.remove();
        }

        if (usig.size() == 0) {
            return;
        }
        try {
            updateManager.applyChange(new AddAxiom(update, niu.getAlpha()));
        } catch (Exception ex) {
            LOG.log(Level.SEVERE,
                    "Failed to add alpha axiom to update ontology ", ex);
        }

        iter = usig.iterator();
        while (iter.hasNext()) {
            OWLClass e = iter.next();
            Set<OWLClassAxiom> axioms = niu.getAxioms(e);
            if (axioms.size() != 1) {
                continue;
            }
            OWLClassAxiom axiom = axioms.iterator().next();
            if (!(axiom instanceof OWLEquivalentClassesAxiom)) {
                continue;
            }
            OWLEquivalentClassesAxiom equAxiom = (OWLEquivalentClassesAxiom) axiom;
            if (equAxiom.getClassExpressions().size() != 2) {
                continue;
            }
            OWLClassExpression od = Noimply.otherDesription(equAxiom
                    .getClassExpressions(), e);
            if (!(od instanceof OWLObjectSomeValuesFrom)) {
                continue;
            }
            OWLObjectSomeValuesFrom restr = (OWLObjectSomeValuesFrom) od;
            if (!(restr.getFiller() instanceof OWLClass)) {
                continue;
            }
            if (!(restr.getProperty() instanceof OWLObjectProperty)) {
                continue;
            }
            OWLClass ep = (OWLClass) restr.getFiller();
            OWLObjectProperty r = (OWLObjectProperty) restr.getProperty();
            boolean cond = !sigma.getRoles().contains(r);
            if (!cond) {
                cond |= marking3CondPart2(ep);
            }
            Set<OWLClass> xis = new HashSet<OWLClass>();
            if (cond) {
                for (OWLClass xi : nio.getXi()) {
                    if (nio.pre(xi).containsAll(niu.pre(e))) { // preU subset of
                        // preO
                        xis.add(xi);
                    }
                }
            } else {
                for (OWLClass xi : nio.getXi()) {
                    if (nio.pre(xi).containsAll(niu.pre(e))) { // preU subset of
                        // preO
                        OWLClass a = nio.xiinv(xi);

                        Set<OWLClassAxiom> axioms2 = nio.getAxioms(a);
                        if (axioms2.size() != 1) {
                            continue;
                        }
                        OWLClassAxiom axiom2 = axioms2.iterator().next();
                        if (!(axiom2 instanceof OWLEquivalentClassesAxiom)) {
                            continue;
                        }
                        OWLEquivalentClassesAxiom equAxiom2 = (OWLEquivalentClassesAxiom) axiom2;
                        if (equAxiom2.getClassExpressions().size() != 2) {
                            continue;
                        }
                        OWLClassExpression od2 = Noimply.otherDesription(
                                equAxiom2.getClassExpressions(), a);
                        if (!(od2 instanceof OWLObjectSomeValuesFrom)) {
                            continue;
                        }
                        OWLObjectSomeValuesFrom restr2 = (OWLObjectSomeValuesFrom) od2;
                        if (!(restr2.getFiller() instanceof OWLClass)) {
                            continue;
                        }
                        if (!(restr2.getProperty() instanceof OWLObjectProperty)) {
                            continue;
                        }
                        OWLClass ap = (OWLClass) restr.getFiller();
                        // OWLObjectProperty r2 = (OWLObjectProperty)
                        // restr.getProperty();
                        boolean ok = true;
                        for (OWLClass xip : nio.noImply(ap)) {
                            Set<OWLClass> ms = marks.get(ep);
                            if ((ms == null) || (!ms.contains(xip))) {
                                ok = false;
                                break;
                            }
                        }

                        if (ok) {
                            xis.add(xi);
                        }
                    }
                }
            }

            marks.put(e, xis);
            iter.remove();

        }
        try {
            updateManager.applyChange(new RemoveAxiom(update, niu.getAlpha()));
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Failed to remove alpha axiom from update ontology ", ex);
        }

        if (usig.size() > 0) {
            LOG.log(Level.WARNING, "DiffR: left over e-classes: " + Diff.classesToString(usig));
            leftOver = usig;
        }
    }

    private boolean marking3CondPart2(OWLClass ep) {
        // OWLAxiom ax = upafactory.getOWLSubClassOfAxiom(intersOfSigmaAndAllSigma, ep);
        OWLAxiom ax = niu.getFactory().getOWLSubClassOfAxiom(intersOfSigmaAndAllSigma, ep);
        // return updatePlusAlphaEM.getSingleExplanation(ax) == null;
        return !niu.getEM().isEntailed(ax);
    }

    public Set<OWLClass> diffR() {
        if (marks == null) {
            LOG.severe("DiffR: not marked yet!");
            return null;
        }
        Set<OWLClass> res = new HashSet<OWLClass>();
        for (OWLClass a : sigma.getSig()) {
            Set<OWLClass> set = new HashSet<OWLClass>();
            set.addAll(nio.noImply(a));
            Set<OWLClass> marksa = marks.get(a);
            if (marksa != null) {
                set.removeAll(marksa);
            }
            if (set.size() > 0) {
                res.add(a);
            }
        }
        // if (leftOver != null) {res.removeAll(leftOver);} // TODO - je to v poradku ?
        return res;
    }
}
