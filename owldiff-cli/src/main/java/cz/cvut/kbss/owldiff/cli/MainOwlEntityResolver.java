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

import org.semanticweb.owlapi.model.*;

public class MainOwlEntityResolver implements OWLAxiomVisitor {

    private IRI entity = null;

    public IRI getEntity() {
        return entity;
    }

    @Override
    public void visit(OWLDeclarationAxiom axiom) {
        entity = axiom.getEntity().getIRI();
    }

    @Override
    public void visit(OWLDatatypeDefinitionAxiom axiom) {
        entity = axiom.getDatatype().getIRI();
    }

    @Override
    public void visit(OWLAnnotationAssertionAxiom axiom) {
        if (axiom.getSubject().isNamed()) {
            entity = axiom.getSubject().asIRI().orElse(null);
        }
    }

    @Override
    public void visit(OWLSubAnnotationPropertyOfAxiom axiom) {
        entity = axiom.getSubProperty().getIRI();
    }

    @Override
    public void visit(OWLAnnotationPropertyDomainAxiom axiom) {
        entity = axiom.getProperty().getIRI();
    }

    @Override
    public void visit(OWLAnnotationPropertyRangeAxiom axiom) {
        entity = axiom.getProperty().getIRI();
    }

    @Override
    public void visit(OWLSubClassOfAxiom axiom) {
        if (axiom.getSubClass().isNamed()) {
            entity = axiom.getSubClass().asOWLClass().getIRI();
        }
    }

    @Override
    public void visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
        if (axiom.getProperty().isNamed()) {
            entity = axiom.getProperty().asOWLObjectProperty().getIRI();
        }
    }

    private void visitOWLObjectPropertyAxiom(
            OWLUnaryPropertyAxiom<OWLObjectPropertyExpression> axiom) {
        if (axiom.getProperty().isOWLObjectProperty()) {
            entity = axiom.getProperty().asOWLObjectProperty().getIRI();
        }
    }

    private void visitOWLDatatypePropertyAxiom(
            OWLUnaryPropertyAxiom<OWLDataPropertyExpression> axiom) {
        if (axiom.getProperty().isOWLDataProperty()) {
            entity = axiom.getProperty().asOWLDataProperty().getIRI();
        }
    }

    @Override
    public void visit(OWLAsymmetricObjectPropertyAxiom axiom) {
        visitOWLObjectPropertyAxiom(axiom);
    }

    @Override
    public void visit(OWLReflexiveObjectPropertyAxiom axiom) {
        visitOWLObjectPropertyAxiom(axiom);
    }

    private void visitNaryClassAxiom(OWLNaryClassAxiom axiom) {
        entity = find(axiom, OWLClass.class);
    }

    private void visitNaryObjectPropertyAxiom(
            OWLNaryPropertyAxiom<OWLObjectPropertyExpression> axiom) {
        entity = find(axiom, OWLObjectProperty.class);
    }

    private void visitNaryDatatypePropertyAxiom(
            OWLNaryPropertyAxiom<OWLDataPropertyExpression> axiom) {
        entity = find(axiom, OWLDataProperty.class);
    }

    private void visitNaryIndividualsAxiom(OWLNaryIndividualAxiom axiom) {
        entity = find(axiom, OWLNamedIndividual.class);
    }

    private <T extends OWLEntity> IRI find(final OWLAxiom axiom, final Class<T> entityType) {
        return axiom.components()
                .filter(entityType::isInstance)
                .sorted()
                .map(cc -> ((T) cc).getIRI())
                .findFirst()
                .orElse(null);
    }

    @Override
    public void visit(OWLDataPropertyDomainAxiom axiom) {
        visitOWLDatatypePropertyAxiom(axiom);
    }

    @Override
    public void visit(OWLObjectPropertyDomainAxiom axiom) {
        visitOWLObjectPropertyAxiom(axiom);
    }

    @Override
    public void visit(OWLEquivalentObjectPropertiesAxiom axiom) {
        visitNaryObjectPropertyAxiom(axiom);
    }

    @Override
    public void visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
        if (axiom.getSubject().isIRI()) {
            entity = axiom.getSubject().asOWLNamedIndividual().getIRI();
        }
    }

    @Override
    public void visit(OWLDifferentIndividualsAxiom axiom) {
        visitNaryIndividualsAxiom(axiom);
    }

    @Override
    public void visit(OWLDisjointDataPropertiesAxiom axiom) {
        visitNaryDatatypePropertyAxiom(axiom);
    }

    @Override
    public void visit(OWLDisjointObjectPropertiesAxiom axiom) {
        visitNaryObjectPropertyAxiom(axiom);
    }

    @Override
    public void visit(OWLObjectPropertyRangeAxiom axiom) {
        visitOWLObjectPropertyAxiom(axiom);
    }

    @Override
    public void visit(OWLObjectPropertyAssertionAxiom axiom) {
        if (axiom.getSubject().isNamed()) {
            entity = axiom.getSubject().asOWLNamedIndividual().getIRI();
        }
    }

    @Override
    public void visit(OWLFunctionalObjectPropertyAxiom axiom) {
        visitOWLObjectPropertyAxiom(axiom);
    }

    @Override
    public void visit(OWLSubObjectPropertyOfAxiom axiom) {
        if (axiom.getSubProperty().isNamed()) {
            entity = axiom.getSubProperty().asOWLObjectProperty().getIRI();
        }
    }

    @Override
    public void visit(OWLDisjointUnionAxiom axiom) {
        final OWLClass c = (OWLClass) axiom.components()
                .sorted()
                .filter(cx -> cx instanceof OWLClass)
                .findFirst()
                .orElse(null);
        entity = c != null ? c.getIRI() : null;
    }

    @Override
    public void visit(OWLSymmetricObjectPropertyAxiom axiom) {
        visitOWLObjectPropertyAxiom(axiom);
    }

    @Override
    public void visit(OWLDataPropertyRangeAxiom axiom) {
        visitOWLDatatypePropertyAxiom(axiom);
    }

    @Override
    public void visit(OWLFunctionalDataPropertyAxiom axiom) {
        visitOWLDatatypePropertyAxiom(axiom);
    }

    @Override
    public void visit(OWLEquivalentDataPropertiesAxiom axiom) {
        visitNaryDatatypePropertyAxiom(axiom);
    }

    @Override
    public void visit(OWLClassAssertionAxiom axiom) {
        if (axiom.getIndividual().isNamed()) {
            entity = axiom.getIndividual().asOWLNamedIndividual().getIRI();
        }

    }

    @Override
    public void visit(OWLEquivalentClassesAxiom axiom) {
        visitNaryClassAxiom(axiom);
    }

    @Override
    public void visit(OWLDataPropertyAssertionAxiom axiom) {
        if (axiom.getSubject().isNamed()) {
            entity = axiom.getSubject().asOWLNamedIndividual().getIRI();
        }
    }

    @Override
    public void visit(OWLTransitiveObjectPropertyAxiom axiom) {
        visitOWLObjectPropertyAxiom(axiom);
    }

    @Override
    public void visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
        visitOWLObjectPropertyAxiom(axiom);
    }

    @Override
    public void visit(OWLSubDataPropertyOfAxiom axiom) {
        if (axiom.getSubProperty().isNamed()) {
            entity = axiom.getSubProperty().asOWLDataProperty().getIRI();
        }
    }

    @Override
    public void visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
        visitOWLObjectPropertyAxiom(axiom);
    }

    @Override
    public void visit(OWLSameIndividualAxiom axiom) {
        visitNaryIndividualsAxiom(axiom);
    }

    @Override
    public void visit(OWLSubPropertyChainOfAxiom axiom) {
        if (axiom.getSuperProperty().isNamed()) {
            entity = axiom.getSuperProperty().asOWLObjectProperty().getIRI();
        }
    }

    @Override
    public void visit(OWLInverseObjectPropertiesAxiom axiom) {
        visitNaryObjectPropertyAxiom(axiom);
    }

    @Override
    public void visit(OWLHasKeyAxiom axiom) {
        if (axiom.getClassExpression().isIRI()) {
            entity = axiom.getClassExpression().asOWLClass().getIRI();
        }
    }

    @Override
    public void visit(SWRLRule node) {
        entity = null;
    }
}
