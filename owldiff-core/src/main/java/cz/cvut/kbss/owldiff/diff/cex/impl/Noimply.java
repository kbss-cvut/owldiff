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
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLAxiomVisitor;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitor;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.SWRLRule;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import cz.cvut.kbss.owldiff.diff.OWLDiffConfiguration;

public class Noimply {

    private static final Logger LOG = Logger.getLogger(Noimply.class.getName());

    private OWLOntology ont;
    private Sig sig;
    private Sig sigma;
    private Map<OWLClass, Set<OWLClass>> pres;

    private OWLAxiom alpha;
    private OWLClassExpression allSigmaSuperClass;
    private OWLClass allSigmaClass;
    private Set<OWLClassExpression> sigmaFresh;
    private Map<OWLClass, OWLClass> xiClasses;
    private Map<OWLClass, OWLClass> xiClassesInv;
    private Map<OWLClass, OWLClassExpression> xiSuperClasses;
    private Map<OWLClass, OWLAxiom> alphas;
    private Map<OWLClass, Set<OWLClass>> noImplies;
    private Map<OWLClass, Set<OWLAxiom>> nnoImplies;
    private Set<OWLAxiom> nnoImply;
    private Map<OWLClass, Set<OWLClassAxiom>> classAxioms;
    private Set<OWLClass> xxi;

    private OWLDataFactory factory;
    private final OWLReasoner em;
    private boolean isUpdate;

    public Noimply(OWLOntologyManager m, OWLOntology ont, Sig sig, OWLDataFactory factory, Sig sigma, boolean isUpdate) {
        this.ont = ont;
        this.sig = sig;
        this.factory = factory;
        this.sigma = sigma;
        this.isUpdate = isUpdate;
        //em = getExplanationManager(m, ont);
        em = OWLDiffConfiguration.getOWLReasoner(ont);
        pres = new HashMap<OWLClass, Set<OWLClass>>();
        alphas = new HashMap<OWLClass, OWLAxiom>();
        noImplies = new HashMap<OWLClass, Set<OWLClass>>();
        nnoImplies = new HashMap<OWLClass, Set<OWLAxiom>>();
        classAxioms = new HashMap<OWLClass, Set<OWLClassAxiom>>();

        buildAllSigma();
        buildSigmaFresh();

        xxi = new HashSet<OWLClass>();
        for (OWLClass a : sig.getSig()) {
            if (!conjunctive(a)) {
                xxi.add(xi(a));
            }
        }
    }

    private void buildAllSigma() {
        Set<OWLClass> s2 = new HashSet<OWLClass>();
        s2.addAll(sigma.getSig());
        try {
            allSigmaClass = factory.getOWLClass(IRI.create("#allSigma"));
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Noimply allSigma creation exception", ex);
            return;
        }
        s2.add(allSigmaClass);
        OWLObjectIntersectionOf inters = factory.getOWLObjectIntersectionOf(s2);
        Set<OWLClassExpression> interset = new HashSet<OWLClassExpression>();
        for (OWLObjectProperty role : sigma.getRoles()) {
            OWLClassExpression d = factory.getOWLObjectSomeValuesFrom(role, inters);
            interset.add(d);
        }

        if ( interset.isEmpty() ) {
            allSigmaSuperClass = factory.getOWLThing();
        } else {
            allSigmaSuperClass = factory.getOWLObjectIntersectionOf(interset);
        }
        alpha = factory.getOWLSubClassOfAxiom(allSigmaClass, allSigmaSuperClass);
    }

    private void buildSigmaFresh() {
        sigmaFresh = new HashSet<OWLClassExpression>();
        sigmaFresh.add(allSigmaClass);
        xiClasses = new HashMap<OWLClass, OWLClass>();
        xiClassesInv = new HashMap<OWLClass, OWLClass>();
        xiSuperClasses = new HashMap<OWLClass, OWLClassExpression>();
        for (OWLClass a : sig.getSig()) {
            if (!conjunctive(a)) {
                sigmaFresh.add(xi(a));
            }
        }
    }

    private boolean conjunctive(OWLClass a) {
        Set<OWLClassAxiom> axioms = getAxioms(a);
        return conjunctive(a, axioms);
    }

    private boolean conjunctive(OWLClass a, Set<OWLClassAxiom> axioms) {

        if (pseudoPrimitive(axioms)) {
            return false;
        }

        boolean nonconj = true;
        for (OWLClassAxiom axiom : axioms) {
            if (!(axiom instanceof OWLEquivalentClassesAxiom)) {
                nonconj = false;
                break;
            } else {
                OWLEquivalentClassesAxiom equiAxiom = (OWLEquivalentClassesAxiom) axiom;
                Set<OWLClassExpression> set = new HashSet<OWLClassExpression>();
                set.addAll(equiAxiom.getClassExpressions());
                set.remove(a);
                if (set.size() != 1) {
                    nonconj = false;
                    break;
                } else {
                    OWLClassExpression d = set.iterator().next();
                    if (!(d instanceof OWLObjectSomeValuesFrom)) {
                        nonconj = false;
                        break;
                    }
                }
            }
        }
        return !nonconj;
    }

    public boolean primitive(Set<OWLClassAxiom> axioms) {
        return (axioms.size() == 0);
    }

    public boolean pseudoPrimitive(Set<OWLClassAxiom> axioms) {
        if (primitive(axioms)) {
            return true;
        }

        for (OWLClassAxiom axiom : axioms) {
            if (!(axiom instanceof OWLSubClassOfAxiom)) {
                return false;
            }
        }
        return true;
    }

    public Set<OWLClassAxiom> getAxioms(OWLClass a) {
        Set<OWLClassAxiom> axioms = classAxioms.get(a);
        if (axioms != null) {
            return axioms;
        }

        axioms = ont.getAxioms(a);
        classAxioms.put(a, axioms);
        return axioms;
    }

    public Set<OWLClass> noImply(OWLClass a) {
        Set<OWLClass> r = noImplies.get(a);
        if (r != null) {
            return r;
        }

        Set<OWLClassAxiom> axioms = getAxioms(a);
        r = new HashSet<OWLClass>();

        if (pseudoPrimitive(axioms)) {
            r.add(xi(a));
        } else if (axioms.size() == 1) {
            OWLClassAxiom axiom = axioms.iterator().next();
            axiom.accept(new NoImplyForSingleAxiomVisitor(a, r, conjunctive(a, axioms)));
        } else {
            LOG.warning("Noimply: noImply: Too many axioms for a class");
        }

        noImplies.put(a, r);
        return r;
    }

    public Set<OWLAxiom> nnoImply(OWLClass a) {
        Set<OWLAxiom> r = nnoImplies.get(a);
        if (r != null) {
            return r;
        }

        Set<OWLClassAxiom> axioms = getAxioms(a);
        r = new HashSet<OWLAxiom>();

        if (pseudoPrimitive(axioms)) {
            r.add(factory.getOWLSubClassOfAxiom(xi(a), xiSuperClass(a)));
        } else if (axioms.size() == 1) {
            OWLClassAxiom axiom = axioms.iterator().next();
            axiom.accept(new NNoImplyForSingleAxiomVisitor(a, r, conjunctive(a, axioms)));
        } else {
            LOG.warning("Noimply: nnoImply: Too many axioms for a class");
        }

        nnoImplies.put(a, r);
        return r;
    }

    public static OWLClassExpression otherDesription(
            Set<OWLClassExpression> ds, OWLClassExpression other) {
        for (OWLClassExpression d : ds) {
            if (d.equals(other)) {
                continue;
            }
            return d;
        }
        return null;
    }

    public Set<OWLAxiom> nnoImply() {
        if (nnoImply != null) {
            return nnoImply;
        }
        Set<OWLClass> s = sigma.getSig();
        s.addAll(sig.getSig());
        Set<OWLAxiom> set = new HashSet<OWLAxiom>();
        set.add(alpha);
        for (OWLClass c : s) {
            set.addAll(nnoImply(c));
        }
        nnoImply = set;
        return set;
    }

    /*
      * private void addAllConjuncts(Set<OWLClass> set, OWLObjectIntersectionOf
      * inter) { for (OWLClassExpression d : inter.getOperands()) { if (d
      * instanceof OWLClass) { set.add((OWLClass) d); } else if (d instanceof
      * OWLObjectIntersectionOf) { addAllConjuncts(set, (OWLObjectIntersectionOf)
      * d); } else {LOG.warn(
      * "Unsupported intersection axiom part in (supposed to be) normalized terminology"
      * ); } } }
      */

    public Set<OWLClass> pre(OWLClass a) {
        Set<OWLClass> set = pres.get(a);
        if (set != null) {
            return set;
        }
        OWLClass a2 = xiinv(a);
        if (a2 != null) {
            if (!conjunctive(a2)) {
                return pres.get(a2);
            } else {
                return null;
            }
        }
        set = new HashSet<OWLClass>();
        for (OWLClass b : sigma.getSig()) {
            OWLAxiom ax = factory.getOWLSubClassOfAxiom(b, a);
            if (em.isEntailed(ax)) {
                set.add(b);
            }
        }
        pres.put(a, set);
        return set;
    }

    private OWLClass xi(OWLClass a) {
        OWLClass xi = xiClasses.get(a);
        if (xi != null) {
            return xi;
        }

        try {
            xi = factory.getOWLClass(IRI.create("#xi_" + a.getIRI().toString()));
        } catch (Exception ex) {
            LOG.severe("Noimply xi class creation exception: " + ex);
            return null;
        }

        xiClasses.put(a, xi);
        xiClassesInv.put(xi, a);
        return xi;
    }

    public OWLClass xiinv(OWLClass xi) {
        return xiClassesInv.get(xi);
    }

    private OWLClassExpression xiSuperClass(OWLClass a) {
        OWLClassExpression xsc = xiSuperClasses.get(a);
        if (xsc != null) {
            return xsc;
        }

        HashSet<OWLClass> set = new HashSet<OWLClass>();
        set.add(allSigmaClass);
        set.addAll(sigma.getSig());
        set.removeAll(pre(a));
        xsc = factory.getOWLObjectIntersectionOf(set);

        xiSuperClasses.put(a, xsc);
        return xsc;
    }

    private OWLAxiom alpha(OWLClass a, OWLObjectProperty r, OWLClass b) {
        OWLAxiom alpha = alphas.get(a);
        if (alpha != null) {
            return alpha;
        }

        Set<OWLClassExpression> interset = new HashSet<OWLClassExpression>();
        interset.addAll(sigma.getSig());
        interset.removeAll(pre(a));

        for (OWLObjectProperty s : sigma.getRoles()) {
            if (s.equals(r)) {
                continue;
            }
            Set<OWLClassExpression> interset2 = new HashSet<OWLClassExpression>();
            interset2.addAll(sigma.getSig());
            interset2.add(allSigmaClass);
            interset.add(factory.getOWLObjectSomeValuesFrom(s, factory
                    .getOWLObjectIntersectionOf(interset2)));
        }

        for (OWLClass xi : noImply(b)) {
            interset.add(factory.getOWLObjectSomeValuesFrom(r, xi));
        }

        OWLObjectIntersectionOf inters = factory.getOWLObjectIntersectionOf(interset);
        alpha = factory.getOWLSubClassOfAxiom(xi(a), inters);

        alphas.put(a, alpha);
        return alpha;
    }

    /*
      * public class Result { Set<OWLClass> n = new HashSet<OWLClass>();
      * Set<OWLAxiom> nn = new HashSet<OWLAxiom>(); }
      */

    /*private OWLReasoner getExplanationManager(final OWLOntologyManager m, final OWLOntology o) {
         return OWLDiffConfiguration.getOWLReasoner(o);
     }*/

    public Set<OWLClass> getXi() {
        return xxi;
    }

    public OWLDataFactory getFactory() {
        return factory;
    }

    public OWLClass getAllSigma() {
        return allSigmaClass;
    }

    public OWLReasoner getEM() {
        return em;
    }

    public OWLAxiom getAlpha() {
        return alpha;
    }

    private class NoImplyForSingleAxiomVisitor implements OWLAxiomVisitor, OWLClassExpressionVisitor {
        private OWLClass a;
        private Set<OWLClass> r;
        private boolean conjunctive;
        private boolean equivalentAxiom = false;

        public NoImplyForSingleAxiomVisitor(OWLClass a, Set<OWLClass> r, boolean conjunctive) {
            this.a = a;
            this.r = r;
            this.conjunctive = conjunctive;
        }

        private void unusedAxiom(OWLAxiom axiom) {
            LOG.warning("Unused axiom: " + axiom);
        }

        private void unusedClassExpression(OWLClassExpression descr) {
            LOG.warning("Unused description: " + descr);
        }

        public void visit(OWLSubClassOfAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLNegativeObjectPropertyAssertionAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLAsymmetricObjectPropertyAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLReflexiveObjectPropertyAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLDisjointClassesAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLDataPropertyDomainAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLAnnotationAssertionAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLObjectPropertyDomainAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLEquivalentObjectPropertiesAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLNegativeDataPropertyAssertionAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLDifferentIndividualsAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLDisjointDataPropertiesAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLDisjointObjectPropertiesAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLObjectPropertyRangeAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLObjectPropertyAssertionAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLFunctionalObjectPropertyAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLSubDataPropertyOfAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLDisjointUnionAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLDeclarationAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLSymmetricObjectPropertyAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLDataPropertyRangeAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLFunctionalDataPropertyAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLEquivalentDataPropertiesAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLClassAssertionAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLEquivalentClassesAxiom axiom) {
            Set<OWLClassExpression> descs = axiom.getClassExpressions();
            if (descs.size() != 2) {
                return;
            }
            OWLClassExpression d = otherDesription(descs, a);
            if (d == null) {
                return;
            }
            equivalentAxiom = true;
            d.accept(this);
        }

        public void visit(OWLDataPropertyAssertionAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLTransitiveObjectPropertyAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLIrreflexiveObjectPropertyAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLSubObjectPropertyOfAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLInverseFunctionalObjectPropertyAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLSameIndividualAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLSubPropertyChainOfAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLInverseObjectPropertiesAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(SWRLRule arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLClass cls) {
            if (conjunctive && equivalentAxiom) {
                r.add(xi(cls));
            } else {
                unusedClassExpression(cls);
                // r.n.add(a);
                // TODO r.nn.add();
            }
        }

        public void visit(OWLObjectIntersectionOf inters) {
            if (conjunctive && equivalentAxiom) {
                for (OWLClassExpression d : inters.getOperands()) {
                    d.accept(this);
                }
            } else {
                unusedClassExpression(inters);
                // addAllConjuncts(r.n, inters);
                // r.nn = 0
            }
        }

        public void visit(OWLObjectUnionOf arg0) {
            unusedClassExpression(arg0);
        }

        public void visit(OWLObjectComplementOf arg0) {
            unusedClassExpression(arg0);
        }

        public void visit(OWLObjectSomeValuesFrom rest) {
            if (conjunctive && equivalentAxiom) {
                rest.getFiller().accept(this);
            } else {
                if ((rest.getProperty() instanceof OWLObjectProperty) && (rest.getFiller() instanceof OWLClass)) {
                    r.add(xi(a));
                } else {
                    LOG.warning("Noimply visitor: unexpected params of OWLObjectSomeValuesFrom");
                }
            }

        }

        public void visit(OWLObjectAllValuesFrom arg0) {
            unusedClassExpression(arg0);
        }

        public void visit(OWLObjectHasValue arg0) {
            unusedClassExpression(arg0);
        }

        public void visit(OWLObjectMinCardinality arg0) {
            unusedClassExpression(arg0);
        }

        public void visit(OWLObjectExactCardinality arg0) {
            unusedClassExpression(arg0);
        }

        public void visit(OWLObjectMaxCardinality arg0) {
            unusedClassExpression(arg0);
        }

        public void visit(OWLObjectHasSelf arg0) {
            unusedClassExpression(arg0);
        }

        public void visit(OWLObjectOneOf arg0) {
            unusedClassExpression(arg0);
        }

        public void visit(OWLDataSomeValuesFrom arg0) {
            unusedClassExpression(arg0);
        }

        public void visit(OWLDataAllValuesFrom arg0) {
            unusedClassExpression(arg0);
        }

        public void visit(OWLDataHasValue arg0) {
            unusedClassExpression(arg0);
        }

        public void visit(OWLDataMinCardinality arg0) {
            unusedClassExpression(arg0);
        }

        public void visit(OWLDataExactCardinality arg0) {
            unusedClassExpression(arg0);
        }

        public void visit(OWLDataMaxCardinality arg0) {
            unusedClassExpression(arg0);
        }

        public void visit(OWLHasKeyAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLDatatypeDefinitionAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLSubAnnotationPropertyOfAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLAnnotationPropertyDomainAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLAnnotationPropertyRangeAxiom arg0) {
            unusedAxiom(arg0);
        }
    }

    private class NNoImplyForSingleAxiomVisitor implements OWLAxiomVisitor, OWLClassExpressionVisitor {
        private OWLClass a;
        private Set<OWLAxiom> r;
        private boolean conjunctive;
        private boolean equivalentAxiom = false;

        public NNoImplyForSingleAxiomVisitor(OWLClass a, Set<OWLAxiom> r,
                                             boolean conjunctive) {
            this.a = a;
            this.r = r;
            this.conjunctive = conjunctive;
        }

        private void unusedAxiom(OWLAxiom axiom) {
            LOG.warning("Unused axiom: " + axiom);
        }

        private void unusedClassExpression(OWLClassExpression descr) {
            LOG.warning("Unused description: " + descr);
        }

        public void visit(OWLSubClassOfAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLNegativeObjectPropertyAssertionAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLAsymmetricObjectPropertyAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLReflexiveObjectPropertyAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLDisjointClassesAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLDataPropertyDomainAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLAnnotationAssertionAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLObjectPropertyDomainAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLEquivalentObjectPropertiesAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLNegativeDataPropertyAssertionAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLDifferentIndividualsAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLDisjointDataPropertiesAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLDisjointObjectPropertiesAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLObjectPropertyRangeAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLObjectPropertyAssertionAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLFunctionalObjectPropertyAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLSubObjectPropertyOfAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLDisjointUnionAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLDeclarationAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLSymmetricObjectPropertyAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLDataPropertyRangeAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLFunctionalDataPropertyAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLEquivalentDataPropertiesAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLClassAssertionAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLEquivalentClassesAxiom axiom) {
            Set<OWLClassExpression> descs = axiom.getClassExpressions();
            if (descs.size() != 2) {
                return;
            }
            OWLClassExpression d = otherDesription(descs, a);
            if (d == null) {
                return;
            }
            equivalentAxiom = true;
            d.accept(this);
        }

        public void visit(OWLDataPropertyAssertionAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLTransitiveObjectPropertyAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLIrreflexiveObjectPropertyAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLSubDataPropertyOfAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLInverseFunctionalObjectPropertyAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLSameIndividualAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLSubPropertyChainOfAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLInverseObjectPropertiesAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(SWRLRule arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLClass cls) {
            if (conjunctive && equivalentAxiom) {

            } else {
                unusedClassExpression(cls);
                // r.n.add(a);
                // TODO r.nn.add();
            }
        }

        public void visit(OWLObjectIntersectionOf inters) {
            if (conjunctive && equivalentAxiom) {
                /*
                     * for (OWLClassExpression d : inters.getOperands()) {
                     * d.accept(this); }
                     */
            } else {
                LOG.warning("Nopimply visitor: unexpected OWLObjectIntersectionOf");
                // addAllConjuncts(r.n, inters);
                // r.nn = 0
            }
        }

        public void visit(OWLObjectUnionOf arg0) {
            unusedClassExpression(arg0);
        }

        public void visit(OWLObjectComplementOf arg0) {
            unusedClassExpression(arg0);
        }

        public void visit(OWLObjectSomeValuesFrom rest) {
            // TODO Auto-generated method stub
            if (conjunctive && equivalentAxiom) {
                // rest.getFiller().accept(this);
            } else {
                if ((rest.getProperty() instanceof OWLObjectProperty)
                        && (rest.getFiller() instanceof OWLClass)) {
                    r.add(alpha(a, (OWLObjectProperty) rest.getProperty(),
                            (OWLClass) rest.getFiller()));
                } else {
                    LOG
                            .warning("Nopimply visitor: unexpected params of OWLObjectSomeValuesFrom");
                }
            }

        }

        public void visit(OWLObjectAllValuesFrom arg0) {
            unusedClassExpression(arg0);
        }

        public void visit(OWLObjectHasValue arg0) {
            unusedClassExpression(arg0);
        }

        public void visit(OWLObjectMinCardinality arg0) {
            unusedClassExpression(arg0);
        }

        public void visit(OWLObjectExactCardinality arg0) {
            unusedClassExpression(arg0);
        }

        public void visit(OWLObjectMaxCardinality arg0) {
            unusedClassExpression(arg0);
        }

        public void visit(OWLObjectHasSelf arg0) {
            unusedClassExpression(arg0);
        }

        public void visit(OWLObjectOneOf arg0) {
            unusedClassExpression(arg0);
        }

        public void visit(OWLDataSomeValuesFrom arg0) {
            unusedClassExpression(arg0);
        }

        public void visit(OWLDataAllValuesFrom arg0) {
            unusedClassExpression(arg0);
        }

        public void visit(OWLDataHasValue arg0) {
            unusedClassExpression(arg0);
        }

        public void visit(OWLDataMinCardinality arg0) {
            unusedClassExpression(arg0);
        }

        public void visit(OWLDataExactCardinality arg0) {
            unusedClassExpression(arg0);
        }

        public void visit(OWLDataMaxCardinality arg0) {
            unusedClassExpression(arg0);
        }

        public void visit(OWLHasKeyAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLDatatypeDefinitionAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLSubAnnotationPropertyOfAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLAnnotationPropertyDomainAxiom arg0) {
            unusedAxiom(arg0);
        }

        public void visit(OWLAnnotationPropertyRangeAxiom arg0) {
            unusedAxiom(arg0);
        }
    }

}
