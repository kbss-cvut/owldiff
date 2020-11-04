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
import java.util.logging.Logger;
import java.util.logging.Level;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

public class DiffL {

    private static final Logger LOG = Logger.getLogger(DiffL.class.getName());
    private OWLOntology original, update;
    private Sig origSig, updateSig, sigma;
    private Noimply nio, niu;
    private Set<OWLClassPair> oo;
    private Map<OWLClass, Set<OWLClass>> oos;
    private Set<OWLClass> leftOver = null;

    public DiffL(OWLOntology original, OWLOntology update, Sig origSig, Sig updateSig, Sig sigma, Noimply nio, Noimply niu) {
        this.original = original;
        this.update = update;
        this.origSig = origSig;
        this.updateSig = updateSig;
        this.sigma = sigma;
        this.nio = nio;
        this.niu = niu;
    }

    public void generateOs() {
        generateO();
        oos = new HashMap<OWLClass, Set<OWLClass>>();

        Set<OWLClass> osig = new HashSet<OWLClass>();
        osig.addAll(origSig.getSig());

        generateOsPrimitive(osig);

        int iter = 0;
        while (!osig.isEmpty()) {
            LOG.info("DiffL generateOs iter=" + (iter++));
            boolean changed = false;

            if (generateOsConj(osig)) {
                changed = true;
            }

            if (osig.isEmpty()) {
                break;
            }

            if (generateOsExist(osig)) {
                changed = true;
            }

            if (!changed) {
                break;
            }
        }

        if (osig.size() > 0) {
            LOG.warning("DiffL: left over classes: " + Diff.classesToString(osig));
            leftOver = osig;
        } else {
            leftOver = null;
        }

    }

    private void generateOsPrimitive(Set<OWLClass> osig) {
        Iterator<OWLClass> iter = osig.iterator();
        while (iter.hasNext()) {
            OWLClass a = iter.next();
            if (isOPrimitive(a)) {
                iter.remove();
            }
        }
    }

    private boolean isOPrimitive(OWLClass a) {
        Set<OWLClassAxiom> axioms = niu.getAxioms(a);

        if (niu.primitive(axioms)) {
            Set<OWLClass> set = getOClassesRightTo(a);
            oos.put(a, set);
            if (LOG.isLoggable(Level.INFO)) {
                LOG.info("O(" + a.getIRI().getFragment() + ") (primitive) = " + Diff.classesToString(set));
            }
            return true;
        }

        if (axioms.size() != 1) {
            LOG.warning("DiffL: too many axioms for " + a.getIRI().getFragment());
            return false;
        }
        OWLClassAxiom axiom = axioms.iterator().next();
        OWLClassExpression d;
        // TODO bud je chyba v paperu, nebo to "a" (v paperu A') ma byt v axiomech na prave strane
        if (axiom instanceof OWLEquivalentClassesAxiom) {
            OWLEquivalentClassesAxiom equAxiom = (OWLEquivalentClassesAxiom) axiom;
            if (equAxiom.getClassExpressions().size() != 2) {
                LOG.warning("DiffL: too many fillers for equ axiom for " + a.getIRI().getFragment());
                return false;
            }
            d = Noimply.otherDesription(equAxiom.getClassExpressions(), a);
        } else if (axiom instanceof OWLSubClassOfAxiom) {
            OWLSubClassOfAxiom subAxiom = (OWLSubClassOfAxiom) axiom;
            d = subAxiom.getSuperClass();
        } else {
            LOG.warning("DiffL: unknown axiom type for " + a.getIRI().getFragment());
            return false;
        }
        if (!(d instanceof OWLObjectSomeValuesFrom)) {
            return false;
        }
        OWLObjectSomeValuesFrom restr = (OWLObjectSomeValuesFrom) d;
        // if (!(restr.getFiller() instanceof OWLClass)) {continue;}
        if (!(restr.getProperty() instanceof OWLObjectProperty)) {
            LOG.warning("DiffL: unknown axiom property for " + a.getIRI().getFragment());
            return false;
        }
        // OWLClass a1p = (OWLClass) restr.getFiller();
        OWLObjectProperty r = (OWLObjectProperty) restr.getProperty();

        if (!sigma.getRoles().contains(r)) {
            Set<OWLClass> set = getOClassesRightTo(a);
            oos.put(a, set);
            if (LOG.isLoggable(Level.INFO)) {
                LOG.info("O(" + a.getIRI().getFragment() + ") (prim exist) = " + Diff.classesToString(set));
            }
            return true;
        }

        return false;
    }

    private boolean generateOsConj(Set<OWLClass> osig) {
        Iterator<OWLClass> iter = osig.iterator();
        boolean changed = false;
        while (iter.hasNext()) {
            OWLClass a = iter.next();
            if (isOConj(a)) {
                iter.remove();
                changed = true;
            }

        }
        return changed;
    }

    private boolean isOConj(OWLClass a) {
        Set<OWLClassAxiom> axioms = niu.getAxioms(a);
        if (axioms.size() != 1) {
            LOG.warning("DiffL: too many axioms for " + a.getIRI().getFragment());
            return false;
        }
        OWLClassAxiom axiom = axioms.iterator().next();
        OWLClassExpression d;
        if (axiom instanceof OWLEquivalentClassesAxiom) {
            OWLEquivalentClassesAxiom equAxiom = (OWLEquivalentClassesAxiom) axiom;
            if (equAxiom.getClassExpressions().size() != 2) {
                LOG.warning("DiffL: too many fillers for equ axiom for " + a.getIRI().getFragment());
                return false;
            }
            d = Noimply.otherDesription(equAxiom.getClassExpressions(), a);
        } else if (axiom instanceof OWLSubClassOfAxiom) {
            OWLSubClassOfAxiom subAxiom = (OWLSubClassOfAxiom) axiom;
            d = subAxiom.getSuperClass();
        } else {
            LOG.warning("DiffL: unknown axiom type for " + a.getIRI().getFragment());
            return false;
        }

        Set<OWLClass> operands = new HashSet<OWLClass>();
        if (d instanceof OWLObjectIntersectionOf) {
            OWLObjectIntersectionOf inters = (OWLObjectIntersectionOf) d;
            for (OWLClassExpression e : inters.getOperands()) {
                if (e instanceof OWLClass) {
                    operands.add((OWLClass) e);
                } else {
                    LOG.warning("DiffL: unknown description in intersection of axiom for " + a.getIRI().getFragment());
                    return false;
                }
            }
        } else if (d instanceof OWLClass) {
            operands.add((OWLClass) d);
        } else {
            return false;
        }

        Set<OWLClass> o = getOClassesRightTo(a);
        for (OWLClass c : operands) {
            Set<OWLClass> ooax = oos.get(c);
            if (ooax != null) {
                o.retainAll(ooax);
            } else {
                LOG.info("DiffL: not found a primitive concept in Os for conj for " + c.getIRI().getFragment() + " for " + a.getIRI().getFragment());
                /*o.clear();
                break;*/
                return false;
            }
        }

        oos.put(a, o);
        if (LOG.isLoggable(Level.INFO)) {
            LOG.info("O(" + a.getIRI().getFragment() + ") (conj) = " + Diff.classesToString(o));
        }
        return true;
    }

    private boolean generateOsExist(Set<OWLClass> osig) {
        OWLReasoner em = nio.getEM();
        OWLDataFactory fact = nio.getFactory();

        Iterator<OWLClass> iter = osig.iterator();
        boolean changed = false;
        while (iter.hasNext()) {
            OWLClass ap = iter.next();
            if (isOExist(ap, em, fact)) {
                iter.remove();
                changed = true;
            }
        }
        return changed;
    }

    private boolean isOExist(OWLClass ap, OWLReasoner em, OWLDataFactory fact) {
        Set<OWLClassAxiom> axioms = niu.getAxioms(ap);
        if (axioms.size() != 1) {
            LOG.warning("DiffL: too many axioms for " + ap.getIRI().getFragment());
            return false;
        }
        OWLClassAxiom axiom = axioms.iterator().next();
        OWLClassExpression d;
        if (axiom instanceof OWLEquivalentClassesAxiom) {
            OWLEquivalentClassesAxiom equAxiom = (OWLEquivalentClassesAxiom) axiom;
            if (equAxiom.getClassExpressions().size() != 2) {
                LOG.warning("DiffL: too many fillers for equ axiom for " + ap.getIRI().getFragment());
                return false;
            }
            d = Noimply.otherDesription(equAxiom.getClassExpressions(), ap);
        } else if (axiom instanceof OWLSubClassOfAxiom) {
            OWLSubClassOfAxiom subAxiom = (OWLSubClassOfAxiom) axiom;
            d = subAxiom.getSuperClass();
        } else {
            LOG.warning("DiffL: unknown axiom type for " + ap.getIRI().getFragment());
            return false;
        }
        if (!(d instanceof OWLObjectSomeValuesFrom)) {
            return false;
        }
        OWLObjectSomeValuesFrom restr = (OWLObjectSomeValuesFrom) d;
        if (!(restr.getFiller() instanceof OWLClass)) {
            return false;
        }
        if (!(restr.getProperty() instanceof OWLObjectProperty)) {
            return false;
        }
        OWLClass a1p = (OWLClass) restr.getFiller();
        OWLObjectProperty r = (OWLObjectProperty) restr.getProperty();
        if (!sigma.getRoles().contains(r)) {
            return false;
        }

        Set<OWLClass> a1poos = oos.get(a1p);
        Set<OWLClass> o = getOClassesRightTo(ap);
        if (a1poos == null) {
            LOG.info("DiffL: not found Os for exist for a1p " + a1p.getIRI().getFragment() + " for " + ap.getIRI().getFragment());
            //o.clear();
            return false;
        } else {
            Iterator<OWLClass> iter2 = o.iterator();
            while (iter2.hasNext()) {
                OWLClass a = iter2.next();

                boolean exists = false;
                for (OWLClass a1 : a1poos) {
                    OWLAxiom ax = fact.getOWLSubClassOfAxiom(a, fact.getOWLObjectSomeValuesFrom(r, a1));
                    if (em.isEntailed(ax)) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    iter2.remove();
                }
            }
        }
        oos.put(ap, o);
        if (LOG.isLoggable(Level.INFO)) {
            LOG.info("O(" + ap.getIRI().getFragment() + ") (exist) = " + Diff.classesToString(o));
        }
        return true;
    }

    private Set<OWLClass> getOClassesRightTo(OWLClass a) {
        Set<OWLClass> set = new HashSet<OWLClass>();
        for (OWLClassPair p : oo) {
            if (a.equals(p.a1)) {
                set.add(p.a2);
            }
        }
        return set;
    }

    private void generateO() {
        oo = new HashSet<OWLClassPair>();
        Set<OWLClass> osig = origSig.getSig();
        Set<OWLClass> sig = sigma.getSig();
        OWLReasoner emo = nio.getEM();
        OWLReasoner emu = niu.getEM();
        OWLDataFactory facto = nio.getFactory();
        OWLDataFactory factu = niu.getFactory();

        for (OWLClass ap : osig) {
            for (OWLClass a : osig) {
                boolean ok = true;

                for (OWLClass b : sig) {

                    OWLAxiom ax = factu.getOWLSubClassOfAxiom(ap, b);
                    if (!emu.isEntailed(ax)) {
                        continue;
                    }

                    ax = facto.getOWLSubClassOfAxiom(a, b);
                    if (emo.isEntailed(ax)) {
                        continue;
                    }

                    ok = false;
                    break;
                }

                if (ok) {
                    OWLClassPair p = new OWLClassPair();
                    p.a1 = ap;
                    p.a2 = a;
                    oo.add(p);
                }
            }
        }
    }

    public Set<OWLClassPair> getO() {
        return oo;
    }

    public Set<OWLClass> diffL() {
        if (oos == null) {
            LOG.severe("DiffL: not generated O(a) yet!");
            return null;
        }
        Set<OWLClass> res = new HashSet<OWLClass>();
        for (OWLClass a : sigma.getSig()) {
            Set<OWLClass> ooa = oos.get(a);
            if ((ooa == null) || !ooa.contains(a)) {
                res.add(a);
            }
        }
        // if (leftOver != null) {res.removeAll(leftOver);} // TODO - je to v poradku ?
        return res;
    }

    public class OWLClassPair {

        OWLClass a1;
        OWLClass a2;

        public boolean equals(Object obj) {
            if (!(obj instanceof OWLClassPair)) {
                return false;
            }
            if (this == obj) {
                return true;
            }
            OWLClassPair p = (OWLClassPair) obj;
            if ((p.a1 == a1) && (p.a2 == a2)) {
                return true;
            }
            if ((a1 == null) && (p.a1 != null)) {
                return false;
            }
            if ((a2 == null) && (p.a2 != null)) {
                return false;
            }
            if ((a1 != null) && !a1.equals(p.a1)) {
                return false;
            }
            if ((a2 != null) && !a2.equals(p.a2)) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            int h = 0;
            if (a1 != null) {
                h += a1.hashCode();
            }
            if (a2 != null) {
                h += a2.hashCode();
            }
            return h;
        }

        public String toString() {
            return "[" + a1.getIRI().getFragment() + "," + a2.getIRI().getFragment() + "]";
        }
    }
}
