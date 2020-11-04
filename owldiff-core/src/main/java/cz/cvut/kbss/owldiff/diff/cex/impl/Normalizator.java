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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLAxiomVisitor;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
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

import cz.cvut.kbss.owldiff.OWLDiffException;
import cz.cvut.kbss.owldiff.OWLDiffException.Reason;
import cz.cvut.kbss.owldiff.view.ProgressListener;

public class Normalizator implements OWLAxiomVisitor {

    private static final Logger LOG = Logger.getLogger(Normalizator.class.getName());

    private static final int PROGRESS_MAX = 100;

    OWLOntologyManager manager;

    OWLDataFactory factory;

    private OWLOntology ont;
    private OWLOntology nont;

    private int errorCnt;

    private ProgressListener progressListener;

    public Normalizator(OWLOntology ont, ProgressListener progressListener) {
        this.ont = ont;
        this.progressListener = progressListener;
        try {
            manager = OWLManager.createOWLOntologyManager();
            factory = manager.getOWLDataFactory();
            nont = manager.createOntology(ont.getOntologyID().getOntologyIRI());
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Failed to init normalizator", ex);
        }
    }

    public OWLOntology normalizeTerminology() throws OWLDiffException {
        int i = 0;
        if (progressListener != null) {
            progressListener.setProgress(0);
            progressListener.setProgressMax(PROGRESS_MAX);
        }

        errorCnt = 0;
        for (OWLAxiom axiom : ont.getAxioms()) {

            axiom.accept(this);

            if (progressListener != null) {
                progressListener.setProgress((++i) % PROGRESS_MAX);
            }

        }

        if (errorCnt > 0) {
            throw new OWLDiffException(Reason.INCOMPATIBLE_ONTOLOGY, "Ontology " + ont.getOntologyID().getOntologyIRI() + " is not EL");
        }

        return nont;
    }

    List<OWLClassExpression> getListOfConjunctions(OWLClassExpression in) {
        List<OWLClassExpression> list = new ArrayList<OWLClassExpression>();
        new NormalizatorClassExpressionVisitor(in, list);
        return list;
    }

    List<OWLObjectIntersectionOf> getConjunctionsFromLists(
            List<List<OWLClassExpression>> lists) {
        List<OWLObjectIntersectionOf> conjs = new ArrayList<OWLObjectIntersectionOf>();
        for (List<OWLClassExpression> list : lists) {
            conjs.add(factory.getOWLObjectIntersectionOf(new HashSet<OWLClassExpression>(list)));
        }
        return conjs;
    }

    @SuppressWarnings("unchecked")
    private List<List<OWLClassExpression>> getCartesianProduct(List<List<OWLClassExpression>> lists) {
        List<List<OWLClassExpression>> out = new ArrayList<List<OWLClassExpression>>();
        if (lists.size() == 0) {
            return out;
        }
        if (lists.size() == 1) {
            for (OWLClassExpression d : lists.get(0)) {
                List<OWLClassExpression> l = new ArrayList<OWLClassExpression>();
                l.add(d);
                out.add(l);
            }
            return out;
        }
        List<OWLClassExpression> ll = lists.remove(lists.size() - 1);
        List<List<OWLClassExpression>> fout = getCartesianProduct(lists);
        for (List<OWLClassExpression> flist : fout) {
            for (OWLClassExpression d : ll) {
                List<OWLClassExpression> l = (List<OWLClassExpression>) ((ArrayList<OWLClassExpression>) flist).clone();
                l.add(d);
                out.add(l);
            }
        }
        return out;
    }

    private void addAxiom(OWLOntology ont, OWLAxiom axiom) {
        try {
            manager.applyChange(new AddAxiom(ont, axiom));
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Failed to add axiom " + axiom + " to ontology " + ont);
        }
    }

    private void unusedAxiom(OWLAxiom axiom, boolean error) {
        LOG.warning("Ignored axiom: " + axiom);
        if (error) {
            errorCnt++;
        }
    }

    private void unusedAxiom(OWLAxiom axiom) {
        unusedAxiom(axiom, true);
    }

    public void visit(OWLSubClassOfAxiom axiom) {
        OWLClassExpression sub = axiom.getSubClass();
        OWLClassExpression sup = axiom.getSuperClass();
        if (sub.isOWLNothing()) {
            return;
        }
        if (sup.isOWLThing()) {
            return;
        }
        if (!(sub instanceof OWLClass)) {
            LOG.log(Level.SEVERE, "On the left side of a subAxiom is not a single class, skipping");
            errorCnt++;
            return;
        }
        // OWLClass subClass = (OWLClass) sub;
        List<OWLClassExpression> list = getListOfConjunctions(sup);
        for (OWLClassExpression d : list) {
            OWLAxiom a = factory.getOWLSubClassOfAxiom(sub, d);
            addAxiom(nont, a);
        }
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
        unusedAxiom(arg0, false);
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
        java.util.Set<OWLClassExpression> descrs = axiom.getClassExpressions();
        OWLClass left = null;
        OWLClassExpression right = null;
        if (descrs.size() != 2) {
            LOG.log(Level.SEVERE, "Unsupported equAxiom - not 2 sides");
            errorCnt++;
            return;
        }
        for (OWLClassExpression d : descrs) {
            if ((d instanceof OWLClass) && (left == null)) {
                left = (OWLClass) d;
            } else {
                right = d;
            }
        }
        if (left == null) {
            LOG.log(Level.SEVERE, "Unsupported equAxiom - at least one side has to be class");
            errorCnt++;
            return;
        }
        List<OWLClassExpression> list = getListOfConjunctions(right);
        // TODO does it make sense to break down equivalence axiom like this ???
        // Probably not:
        if (list.size() != 1) {
            LOG.severe("Unsupported equAxiom - union of some things");
            errorCnt++;
            return;
        }

        for (OWLClassExpression d : list) {
            Set<OWLClassExpression> set = new HashSet<OWLClassExpression>();
            set.add(left);
            set.add(d);
            OWLAxiom a = factory.getOWLEquivalentClassesAxiom(set);
            addAxiom(nont, a);
        }

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

    private class NormalizatorClassExpressionVisitor implements OWLClassExpressionVisitor {

        private List<OWLClassExpression> list;

        public NormalizatorClassExpressionVisitor(OWLClassExpression in, List<OWLClassExpression> list) {
            this.list = list;

            in.accept(this);

            /*
                * } else { LOG.error("Unsupported OWLClassExpression type"); }
                */
        }

        private void unusedClassExpression(OWLClassExpression descr) {
            LOG.warning("Ignored description: " + descr);
            errorCnt++;
        }

        public void visit(OWLClass c) {
            list.add(c);
        }

        public void visit(OWLObjectIntersectionOf inters) {

            Set<OWLClassExpression> ops = inters.getOperands();
            List<List<OWLClassExpression>> lists = new ArrayList<List<OWLClassExpression>>();
            for (OWLClassExpression op : ops) {
                lists.add(getListOfConjunctions(op));
            }
            list.addAll(getConjunctionsFromLists(getCartesianProduct(lists)));

        }

        public void visit(OWLObjectUnionOf union) {

            Set<OWLClassExpression> ops = union.getOperands();
            for (OWLClassExpression op : ops) {
                /*
                     * ArrayList<OWLClassExpression> l = new
                     * ArrayList<OWLClassExpression>(); new
                     * NormalizatorClassExpressionVisitor(op, l); list.addAll(l);
                     */
                list.addAll(getListOfConjunctions(op));
            }

        }

        public void visit(OWLObjectComplementOf arg0) {
            unusedClassExpression(arg0);
        }

        public void visit(OWLObjectSomeValuesFrom restr) {
            list.add(restr);
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

    }

    public void visit(OWLHasKeyAxiom arg0) {
        unusedAxiom(arg0);
    }

    public void visit(OWLDatatypeDefinitionAxiom arg0) {
        unusedAxiom(arg0);
    }

    public void visit(OWLSubAnnotationPropertyOfAxiom arg0) {
        unusedAxiom(arg0, false);
    }

    public void visit(OWLAnnotationPropertyDomainAxiom arg0) {
        unusedAxiom(arg0, false);
    }

    public void visit(OWLAnnotationPropertyRangeAxiom arg0) {
        unusedAxiom(arg0, false);
    }

}
