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

package cz.cvut.kbss.owldiff.cli;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiomVisitor;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
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
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLNaryClassAxiom;
import org.semanticweb.owlapi.model.OWLNaryIndividualAxiom;
import org.semanticweb.owlapi.model.OWLNaryPropertyAxiom;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLUnaryPropertyAxiom;
import org.semanticweb.owlapi.model.SWRLRule;

public class MainOwlEntityResolver implements OWLAxiomVisitor {

    private IRI entity;

    public IRI getEntity() {
        return entity;
    }

    @Override public void visit(OWLDeclarationAxiom axiom) {
        entity = axiom.getEntity().getIRI();
    }

    @Override public void visit(OWLDatatypeDefinitionAxiom axiom) {
        entity = axiom.getDatatype().getIRI();
    }

    @Override public void visit(OWLAnnotationAssertionAxiom axiom) {
        if (axiom.getSubject().isNamed()) {
            entity = axiom.getSubject().asIRI().get();
        } else {
            entity = null;
        }
    }

    @Override public void visit(OWLSubAnnotationPropertyOfAxiom axiom) {
        entity = axiom.getSubProperty().getIRI();
    }

    @Override public void visit(OWLAnnotationPropertyDomainAxiom axiom) {
        entity = axiom.getProperty().getIRI();
    }

    @Override public void visit(OWLAnnotationPropertyRangeAxiom axiom) {
        entity = axiom.getProperty().getIRI();
    }

    @Override public void visit(OWLSubClassOfAxiom axiom) {
        if (axiom.getSubClass().isNamed()) {
            entity = axiom.getSubClass().asOWLClass().getIRI();
        } else {
            entity = null;
        }
    }

    @Override public void visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
        if (axiom.getProperty().isNamed()) {
            entity = axiom.getProperty().asOWLObjectProperty().getIRI();
        } else {
            entity = null;
        }
    }

    private void visitOWLObjectPropertyAxiom(
        OWLUnaryPropertyAxiom<OWLObjectPropertyExpression> axiom) {
        if (axiom.getProperty().isOWLObjectProperty()) {
            entity = axiom.getProperty().asOWLObjectProperty().getIRI();
        } else {
            entity = null;
        }
    }

    private void visitOWLDatatypePropertyAxiom(
        OWLUnaryPropertyAxiom<OWLDataPropertyExpression> axiom) {
        if (axiom.getProperty().isOWLDataProperty()) {
            entity = axiom.getProperty().asOWLDataProperty().getIRI();
        } else {
            entity = null;
        }
    }

    @Override public void visit(OWLAsymmetricObjectPropertyAxiom axiom) {
        visitOWLObjectPropertyAxiom(axiom);
    }

    @Override public void visit(OWLReflexiveObjectPropertyAxiom axiom) {
        visitOWLObjectPropertyAxiom(axiom);
    }

    private void visitNaryClassAxiom(OWLNaryClassAxiom axiom) {
        final OWLClass c = (OWLClass) axiom.components()
            .sorted()
            .filter(cx -> cx instanceof OWLClass)
            .findFirst()
            .orElseGet(null);
        entity = c != null ? c.getIRI() : null;
    }

    private void visitNaryObjectPropertyAxiom(
        OWLNaryPropertyAxiom<OWLObjectPropertyExpression> axiom) {
        final OWLObjectProperty c = (OWLObjectProperty) axiom.components()
            .sorted()
            .filter(cx -> cx instanceof OWLObjectProperty)
            .findFirst()
            .orElseGet(null);
        entity = c != null ? c.getIRI() : null;
    }

    private void visitNaryDatatypePropertyAxiom(
        OWLNaryPropertyAxiom<OWLDataPropertyExpression> axiom) {
        final OWLDataProperty c = (OWLDataProperty) axiom.components()
            .sorted()
            .filter(cx -> cx instanceof OWLDataProperty)
            .findFirst()
            .orElseGet(null);
        entity = c != null ? c.getIRI() : null;
    }


    private void visitNaryIndividualsAxiom(OWLNaryIndividualAxiom axiom) {
        final OWLNamedIndividual c = (OWLNamedIndividual) axiom.components()
            .sorted()
            .filter(cx -> cx instanceof OWLNamedIndividual)
            .findFirst()
            .get();
        entity = c.asOWLNamedIndividual().getIRI();
    }

    @Override public void visit(OWLDataPropertyDomainAxiom axiom) {
        visitOWLDatatypePropertyAxiom(axiom);
    }

    @Override public void visit(OWLObjectPropertyDomainAxiom axiom) {
        visitOWLObjectPropertyAxiom(axiom);
    }

    @Override public void visit(OWLEquivalentObjectPropertiesAxiom axiom) {
        visitNaryObjectPropertyAxiom(axiom);
    }

    @Override public void visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
        if (axiom.getSubject().isIRI()) {
            entity = axiom.getSubject().asOWLNamedIndividual().getIRI();
        } else {
            entity = null;
        }
    }

    @Override public void visit(OWLDifferentIndividualsAxiom axiom) {
        visitNaryIndividualsAxiom(axiom);
    }

    @Override public void visit(OWLDisjointDataPropertiesAxiom axiom) {
        visitNaryDatatypePropertyAxiom(axiom);
    }

    @Override public void visit(OWLDisjointObjectPropertiesAxiom axiom) {
        visitNaryObjectPropertyAxiom(axiom);
    }

    @Override public void visit(OWLObjectPropertyRangeAxiom axiom) {
        visitOWLObjectPropertyAxiom(axiom);
    }

    @Override public void visit(OWLObjectPropertyAssertionAxiom axiom) {
        if (axiom.getSubject().isNamed()) {
            entity = axiom.getSubject().asOWLNamedIndividual().getIRI();
        } else {
            entity = null;
        }
    }

    @Override public void visit(OWLFunctionalObjectPropertyAxiom axiom) {
        visitOWLObjectPropertyAxiom(axiom);
    }

    @Override public void visit(OWLSubObjectPropertyOfAxiom axiom) {
        if (axiom.getSubProperty().isNamed()) {
            entity = axiom.getSubProperty().asOWLObjectProperty().getIRI();
        } else {
            entity = null;
        }
    }

    @Override public void visit(OWLDisjointUnionAxiom axiom) {
        final OWLClass c = (OWLClass) axiom.components()
            .sorted()
            .filter(cx -> cx instanceof OWLClass)
            .findFirst()
            .orElseGet(null);
        entity = c != null ? c.getIRI() : null;
    }

    @Override public void visit(OWLSymmetricObjectPropertyAxiom axiom) {
        visitOWLObjectPropertyAxiom(axiom);
    }

    @Override public void visit(OWLDataPropertyRangeAxiom axiom) {
        visitOWLDatatypePropertyAxiom(axiom);
    }

    @Override public void visit(OWLFunctionalDataPropertyAxiom axiom) {
        visitOWLDatatypePropertyAxiom(axiom);
    }

    @Override public void visit(OWLEquivalentDataPropertiesAxiom axiom) {
        visitNaryDatatypePropertyAxiom(axiom);
    }

    @Override public void visit(OWLClassAssertionAxiom axiom) {
        if (axiom.getIndividual().isNamed()) {
            entity = axiom.getIndividual().asOWLNamedIndividual().getIRI();
        } else {
            entity = null;
        }

    }

    @Override public void visit(OWLEquivalentClassesAxiom axiom) {
        visitNaryClassAxiom(axiom);
    }

    @Override public void visit(OWLDataPropertyAssertionAxiom axiom) {
        if (axiom.getSubject().isNamed()) {
            entity = axiom.getSubject().asOWLNamedIndividual().getIRI();
        } else {
            entity = null;
        }
    }

    @Override public void visit(OWLTransitiveObjectPropertyAxiom axiom) {
        visitOWLObjectPropertyAxiom(axiom);
    }

    @Override public void visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
        visitOWLObjectPropertyAxiom(axiom);
    }

    @Override public void visit(OWLSubDataPropertyOfAxiom axiom) {
        if (axiom.getSubProperty().isNamed()) {
            entity = axiom.getSubProperty().asOWLDataProperty().getIRI();
        } else {
            entity = null;
        }
    }

    @Override public void visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
        visitOWLObjectPropertyAxiom(axiom);
    }

    @Override public void visit(OWLSameIndividualAxiom axiom) {
        visitNaryIndividualsAxiom(axiom);
    }

    @Override public void visit(OWLSubPropertyChainOfAxiom axiom) {
        if (axiom.getSuperProperty().isNamed()) {
            entity = axiom.getSuperProperty().asOWLObjectProperty().getIRI();
        } else {
            entity = null;
        }
    }

    @Override public void visit(OWLInverseObjectPropertiesAxiom axiom) {
        visitNaryObjectPropertyAxiom(axiom);
    }

    @Override public void visit(OWLHasKeyAxiom axiom) {
        if (axiom.getClassExpression().isIRI()) {
            entity = axiom.getClassExpression().asOWLClass().getIRI();
        } else {
            entity = null;
        }
    }

    @Override public void visit(SWRLRule node) {
        entity = null;
    }
}
