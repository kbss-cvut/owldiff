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

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

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
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataProperty;
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
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
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
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLPropertyExpressionVisitor;
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

public class Sig implements OWLAxiomVisitor, OWLClassExpressionVisitor,
        OWLPropertyExpressionVisitor {

    private static final Logger LOG = Logger.getLogger(Sig.class.getName());

    private HashSet<OWLClass> sig;
    private HashSet<OWLObjectProperty> roles;

    private Sig() {
        sig = new HashSet<OWLClass>();
        roles = new HashSet<OWLObjectProperty>();
    }

    public Sig(OWLOntology o) {
        this();
        for (OWLAxiom axiom : o.getAxioms()) {
            axiom.accept(this);
        }

    }

    public Sig(Set<OWLClass> classes, Set<OWLObjectProperty> properties) {
        this();
        if (classes != null) {
            sig.addAll(classes);
        }
        if (properties != null) {
            roles.addAll(properties);
        }
    }

    @SuppressWarnings("unchecked")
    public Set<OWLClass> getSig() {
        return (Set<OWLClass>) sig.clone();
    }

    @SuppressWarnings("unchecked")
    public Set<OWLObjectProperty> getRoles() {
        return (Set<OWLObjectProperty>) roles.clone();
    }

    public void removeClass(OWLClass c) {
        sig.remove(c);
    }

    public void removeRole(OWLObjectProperty r) {
        roles.remove(r);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Sig: classes: ");
        for (OWLClass c : sig) {
            sb.append(c.getIRI().getFragment() + ", ");
        }
        sb.append("; roles: ");
        for (OWLObjectProperty p : roles) {
            sb.append(p.getIRI().getFragment() + ", ");
        }
        return sb.toString();
    }

    private void unusedAxiom(OWLAxiom axiom) {
        LOG.config("Unused axiom: " + axiom);
    }

    private void unusedClassExpression(OWLClassExpression descr) {
        LOG.config("Unused description: " + descr);
    }

    private void unusedProperty(OWLPropertyExpression prop) {
        LOG.config("Unused property: " + prop);
    }

    public void visit(OWLSubClassOfAxiom axiom) {
        axiom.getSubClass().accept(this);
        axiom.getSuperClass().accept(this);
    }

    public void visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
        axiom.getProperty().accept(this);

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
        for (OWLClassExpression d : axiom.getClassExpressions()) {
            d.accept(this);
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

    public void visit(OWLClass cls) {
        sig.add(cls);
    }

    public void visit(OWLObjectIntersectionOf inters) {
        for (OWLClassExpression d : inters.getOperands()) {
            d.accept(this);
        }
    }

    public void visit(OWLObjectUnionOf arg0) {
        unusedClassExpression(arg0);
    }

    public void visit(OWLObjectComplementOf arg0) {
        unusedClassExpression(arg0);
    }

    public void visit(OWLObjectSomeValuesFrom restr) {
        restr.getFiller().accept(this);
        restr.getProperty().accept(this);
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

    public void visit(OWLObjectProperty prop) {
        roles.add(prop);
    }

    public void visit(OWLObjectInverseOf inv) {
        inv.getInverse().accept(this);

    }

    public void visit(OWLDataProperty prop) {
        unusedProperty(prop);
    }

    public void visit(OWLHasKeyAxiom arg0) {
        unusedAxiom(arg0);
    }

    public void visit(OWLDatatypeDefinitionAxiom arg0) {
        unusedAxiom(arg0);
    }

    public void visit(OWLAnnotationAssertionAxiom arg0) {
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

    public void visit(SWRLRule arg0) {
        unusedAxiom(arg0);
    }

}
