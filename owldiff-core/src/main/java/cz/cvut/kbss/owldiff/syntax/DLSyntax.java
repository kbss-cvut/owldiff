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
package cz.cvut.kbss.owldiff.syntax;

import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Logger;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationValueVisitor;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
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
import org.semanticweb.owlapi.model.OWLDataRange;
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
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
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
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLPropertyExpressionVisitor;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLUnaryPropertyAxiom;
import org.semanticweb.owlapi.model.SWRLRule;

public class DLSyntax implements Syntax {

    private static final Logger LOG = Logger
            .getLogger(DLSyntax.class.getName());

    private static final String SUB = "\u2291";
    private static final String EQUIVALENT = "\u2261";
    private static final String NEGATION = "\u00ac";

    /**
     * {@inheritDoc}
     */
    public String writeAxiom(final OWLAxiom axiom, final boolean fullURI,
                             final Object context, final boolean html) {
        if (axiom == null) {
            throw new IllegalArgumentException();
        }

        final StringBuilder b = new StringBuilder();

        axiom.accept(new OWLAxiomVisitor() {

            private void propertyCharacteristics(final String type,
                                                 final OWLUnaryPropertyAxiom<?> p, final boolean fullURI) {
                if (context == null) {
                    b.append(type).append("(")
                            .append(write(p.getProperty(), fullURI, html))
                            .append(")");
                } else if (context.equals(p.getProperty())) {
                    b.append(type);
                } else {
                    LOG.warning("The context '" + context
                            + "' is not equal to the relation of " + p);
                    b.append(p.toString());
                }
            }

            private void symmetricConceptAxiom(
                    final Collection<OWLClassExpression> de,
                    final Object context, final String separator,
                    final boolean fullURI) {
                if (context != null && de.contains(context)) { // TODO remove
                    // - for
                    // cases
                    // that de
                    // is a
                    // TreeSet
                    // and
                    // context
                    // is not
                    // Comparable
                    for (final OWLClassExpression d : de) {
                        if (!d.equals(context)) {
                            b.append(separator).append(write(d, fullURI, html));
                        }
                    }
                } else {
                    if (context != null) {
                        LOG.warning("The context '" + context
                                + "' is not equal to an element of " + de);
                    }
                    boolean first = true;

                    for (final OWLClassExpression d : de) {
                        if (!first) {
                            b.append(separator);
                        }
                        first = false;
                        b.append(write(d, fullURI, html));
                    }
                }
            }

            private void symmetricPropertiesAxiom(
                    final Collection<? extends OWLPropertyExpression<?, ?>> de,
                    final Object context, final String separator,
                    final boolean fullURI) {
                if (new HashSet<Object>(de).contains(context)) { // TODO remove
                    // - for
                    // cases
                    // that de
                    // is a
                    // TreeSet
                    // and
                    // context
                    // is not
                    // Comparable
                    for (final OWLPropertyExpression<?, ?> d : de) {
                        if (!d.equals(context)) {
                            b.append(separator).append(write(d, fullURI, html));
                        }
                    }
                } else {
                    if (context != null) {
                        LOG.warning("The context '" + context
                                + "' is not equal to an element of " + de);
                    }
                    boolean first = true;

                    for (final OWLPropertyExpression<?, ?> d : de) {
                        if (!first) {
                            b.append(separator);
                        }
                        first = false;
                        b.append(write(d, fullURI, html));
                    }
                }
            }

            private void symmetricIndividualsAxiom(
                    final Collection<OWLIndividual> de, final Object context,
                    final String separator, final boolean fullURI) {
                if (de.contains(context)) {
                    for (final OWLIndividual d : de) {
                        b.append(separator).append(write(d, fullURI, html));
                    }
                } else {
                    if (context != null) {
                        LOG.warning("The context '" + context
                                + "' is not equal to an element of " + de);
                    }
                    boolean first = true;

                    for (final OWLIndividual d : de) {
                        if (!first) {
                            b.append(separator);
                        }
                        first = false;
                        b.append(write(d, fullURI, html));
                    }
                }
            }

            private void domainAxiom(OWLPropertyDomainAxiom<?> arg0) {
                if (arg0.getProperty().equals(context)) {
                    b.append("Domain=").append(
                            write(arg0.getDomain(), fullURI, html));
                } else {
                    if (context != null) {
                        LOG.warning("The context '"
                                + context
                                + "' is not equal to the 'property' part of the axiom "
                                + arg0);
                    }

                    b.append("Domain(")
                            .append(write(arg0.getProperty(), fullURI, html))
                            .append(")=")
                            .append(write(arg0.getDomain(), fullURI, html));
                }
            }

            private void propertyValue(final boolean object,
                                       final boolean notNeg,
                                       final OWLPropertyAssertionAxiom<?, ?> arg0) {

                String o;

                if (object) {
                    o = write(((OWLIndividual) arg0.getObject()), fullURI, html);
                } else {
                    o = write(((OWLLiteral) arg0.getObject()), fullURI, html);
                }

                if (arg0.getSubject().equals(context)) {
                    if (!notNeg)
                        b.append(NEGATION);

                    b.append(write(arg0.getProperty(), fullURI, html))
                            .append(" \u2192 ").append(o);
                } else {
                    if (context != null) {
                        LOG.warning("The context '"
                                + context
                                + "' is not equal to the 'subejct' part of the axiom "
                                + arg0);
                    }

                    if (!notNeg)
                        b.append(NEGATION);

                    b.append(write(arg0.getProperty(), fullURI, html))
                            .append("(")
                            .append(write(arg0.getSubject(), fullURI, html))
                            .append(", ").append(o).append(")");
                }
            }

            private void subProperty(OWLSubPropertyAxiom<?> arg0) {
                if (arg0.getSubProperty().equals(context)) {
                    b.append(" ")
                            .append(SUB)
                            .append(" ")
                            .append(write(arg0.getSuperProperty(), fullURI,
                                    html));
                } else {
                    if (context != null) {
                        LOG.warning("The context '"
                                + context
                                + "' is not equal to the 'sub' part of the axiom "
                                + arg0);
                    }

                    b.append(write(arg0.getSubProperty(), fullURI, html))
                            .append(" ")
                            .append(SUB)
                            .append(" ")
                            .append(write(arg0.getSuperProperty(), fullURI,
                                    html));
                }
            }

            public void visit(OWLSubClassOfAxiom arg0) {
                if (arg0.getSubClass().equals(context)) {
                    b.append(" ").append(SUB).append(" ")
                            .append(write(arg0.getSuperClass(), fullURI, html));
                } else {
                    if (context != null) {
                        LOG.warning("The context '"
                                + context
                                + "' is not equal to the 'sub' part of the axiom "
                                + arg0);
                    }

                    b.append(write(arg0.getSubClass(), fullURI, html))
                            .append(" ").append(SUB).append(" ")
                            .append(write(arg0.getSuperClass(), fullURI, html));
                }
            }

            public void visit(OWLNegativeObjectPropertyAssertionAxiom arg0) {
                propertyValue(true, false, arg0);
            }

            public void visit(OWLAsymmetricObjectPropertyAxiom arg0) {
                propertyCharacteristics("anti-symmetric", arg0, fullURI);
            }

            public void visit(OWLReflexiveObjectPropertyAxiom arg0) {
                propertyCharacteristics("reflexive", arg0, fullURI);
            }

            public void visit(OWLDisjointClassesAxiom arg0) {
                symmetricConceptAxiom(arg0.getClassExpressions(), context,
                        " \u22A0 ", fullURI);
            }

            public void visit(OWLDataPropertyDomainAxiom arg0) {
                domainAxiom(arg0);
            }

            public void visit(OWLObjectPropertyDomainAxiom arg0) {
                domainAxiom(arg0);
            }

            public void visit(OWLEquivalentObjectPropertiesAxiom arg0) {
                symmetricPropertiesAxiom(arg0.getProperties(), context, " "
                        + EQUIVALENT + " ", fullURI);
            }

            public void visit(OWLNegativeDataPropertyAssertionAxiom arg0) {
                propertyValue(false, false, arg0);
            }

            public void visit(OWLDifferentIndividualsAxiom arg0) {
                symmetricIndividualsAxiom(arg0.getIndividuals(), context,
                        " \u2260 ", fullURI);
            }

            public void visit(OWLDisjointDataPropertiesAxiom arg0) {
                symmetricPropertiesAxiom(arg0.getProperties(), context,
                        " \u22A0 ", fullURI);
            }

            public void visit(OWLDisjointObjectPropertiesAxiom arg0) {
                symmetricPropertiesAxiom(arg0.getProperties(), context,
                        " \u22A0 ", fullURI);
            }

            public void visit(OWLObjectPropertyRangeAxiom arg0) {
                if (arg0.getProperty().equals(context)) {
                    b.append("Range=").append(
                            write(arg0.getRange(), fullURI, html));
                } else {
                    if (context != null) {
                        LOG.warning("The context '"
                                + context
                                + "' is not equal to the 'property' part of the axiom "
                                + arg0);
                    }

                    b.append("Range(")
                            .append(write(arg0.getProperty(), fullURI, html))
                            .append(")=")
                            .append(write(arg0.getRange(), fullURI, html));
                }
            }

            public void visit(OWLObjectPropertyAssertionAxiom arg0) {
                propertyValue(true, true, arg0);
            }

            public void visit(OWLFunctionalObjectPropertyAxiom arg0) {
                propertyCharacteristics("functional", arg0, fullURI);

            }

            public void visit(OWLSubObjectPropertyOfAxiom arg0) {
                subProperty(arg0);
            }

            public void visit(OWLDisjointUnionAxiom arg0) {
                b.append(write(arg0.getOWLClass(), fullURI, html)).append(
                        "=DisjointUnionOf(");

                boolean first = true;

                for (final OWLClassExpression d : arg0.getClassExpressions()) {
                    if (!first) {
                        b.append(", ");
                    }
                    first = false;
                    b.append(write(d, fullURI, html));
                }

                b.append(")");
                // TODO
            }

            public void visit(OWLDeclarationAxiom arg0) {
                b.append("declares ").append(" ");
                b.append(getName(arg0.getEntity().getIRI(), fullURI));
            }

            public void visit(OWLSymmetricObjectPropertyAxiom arg0) {
                propertyCharacteristics("symmetric", arg0, fullURI);
            }

            public void visit(OWLDataPropertyRangeAxiom arg0) {
                if (arg0.getProperty().equals(context)) {
                    b.append("Range=").append(
                            write(arg0.getRange(), fullURI, html));
                } else {
                    if (context != null) {
                        LOG.warning("The context '"
                                + context
                                + "' is not equal to the 'property' part of the axiom "
                                + arg0);
                    }

                    b.append("Range(")
                            .append(write(arg0.getProperty(), fullURI, html))
                            .append(")=")
                            .append(write(arg0.getRange(), fullURI, html));
                }

            }

            public void visit(OWLFunctionalDataPropertyAxiom arg0) {
                propertyCharacteristics("functional", arg0, fullURI);
            }

            public void visit(OWLEquivalentDataPropertiesAxiom arg0) {
                symmetricPropertiesAxiom(arg0.getProperties(), context, " "
                        + EQUIVALENT + " ", fullURI);
            }

            public void visit(OWLClassAssertionAxiom arg0) {
                if (arg0.getIndividual().equals(context)) {
                    b.append(" \u2208 ").append(
                            write(arg0.getClassExpression(), fullURI, html));
                } else {
                    if (context != null) {
                        LOG.warning("The context '"
                                + context
                                + "' is not equal to the 'property' part of the axiom "
                                + arg0);
                    }

                    b.append(write(arg0.getClassExpression(), fullURI, html))
                            .append("(")
                            .append(write(arg0.getIndividual(), fullURI, html))
                            .append(")");
                }
            }

            public void visit(OWLEquivalentClassesAxiom arg0) {
                symmetricConceptAxiom(arg0.getClassExpressions(), context, " "
                        + EQUIVALENT + " ", fullURI);
            }

            public void visit(OWLDataPropertyAssertionAxiom arg0) {
                propertyValue(false, true, arg0);
            }

            public void visit(OWLTransitiveObjectPropertyAxiom arg0) {
                propertyCharacteristics("transitive", arg0, fullURI);
            }

            public void visit(OWLIrreflexiveObjectPropertyAxiom arg0) {
                propertyCharacteristics("irreflexive", arg0, fullURI);
            }

            public void visit(OWLSubDataPropertyOfAxiom arg0) {
                subProperty(arg0);
            }

            public void visit(OWLInverseFunctionalObjectPropertyAxiom arg0) {
                propertyCharacteristics("inverse-functional", arg0, fullURI);
            }

            public void visit(OWLSameIndividualAxiom arg0) {
                symmetricIndividualsAxiom(arg0.getIndividuals(), context,
                        " = ", fullURI);
            }

            public void visit(OWLSubPropertyChainOfAxiom arg0) {
                boolean first = true;
                for (final OWLObjectPropertyExpression e : arg0
                        .getPropertyChain()) {
                    if (!first) {
                        b.append(" \u2218 ");
                    }
                    first = false;
                    b.append(write(e, fullURI, html));
                }

                b.append(" \u2291 ").append(
                        write(arg0.getSuperProperty(), fullURI, html));
                // TODO
            }

            public void visit(OWLInverseObjectPropertiesAxiom arg0) {
                symmetricPropertiesAxiom(arg0.getProperties(), context,
                        " inverse-of ", fullURI);
            }

            public void visit(SWRLRule arg0) {
                b.append(arg0.toString());
                // TODO
            }

            public void visit(OWLHasKeyAxiom arg0) {
                b.append(arg0.toString());
                // TODO
            }

            public void visit(OWLDatatypeDefinitionAxiom arg0) {
                b.append(arg0.toString());
                // TODO
            }

            public void visit(OWLAnnotationAssertionAxiom arg0) {
                b.append(write(arg0.getAnnotation(), fullURI, html));
            }

            public void visit(OWLSubAnnotationPropertyOfAxiom arg0) {
                if (arg0.getSubProperty().equals(context)) {
                    b.append(" ")
                            .append(SUB)
                            .append(" ")
                            .append(getName(arg0.getSuperProperty().getIRI(),
                                    fullURI));
                } else {
                    if (context != null) {
                        LOG.warning("The context '"
                                + context
                                + "' is not equal to the 'sub' part of the axiom "
                                + arg0);
                    }

                    b.append(getName(arg0.getSubProperty().getIRI(), fullURI))
                            .append(" ")
                            .append(SUB)
                            .append(" ")
                            .append(getName(arg0.getSuperProperty().getIRI(),
                                    fullURI));
                }
            }

            public void visit(OWLAnnotationPropertyDomainAxiom arg0) {
                // TODO Auto-generated method stub

            }

            public void visit(OWLAnnotationPropertyRangeAxiom arg0) {
                // TODO Auto-generated method stub

            }
        });

        return b.toString();
    }

    private String write(final OWLClassExpression concept,
                         final boolean fullURI, final boolean html) {
        if (concept == null) {
            throw new IllegalArgumentException();
        }

        final StringBuilder b = new StringBuilder();

        concept.accept(new OWLClassExpressionVisitor() {

            public void visit(OWLClass arg0) {
                if (arg0.isOWLThing()) {
                    b.append("\u22a4");
                } else if (arg0.isOWLNothing()) {
                    b.append("\u22a5");
                } else {
                    b.append(getName(arg0.getIRI(), fullURI));
                }
            }

            public void visit(OWLObjectIntersectionOf arg0) {
                b.append("(");
                boolean first = true;

                for (final OWLClassExpression d : arg0.getOperands()) {
                    if (!first) {
                        b.append(" \u2293 ");
                    }
                    first = false;
                    b.append(write(d, fullURI, html));
                }

                b.append(")");
            }

            public void visit(OWLObjectUnionOf arg0) {
                b.append("(");
                boolean first = true;

                for (final OWLClassExpression d : arg0.getOperands()) {
                    if (!first) {
                        b.append(" \u2294 ");
                    }
                    first = false;
                    b.append(write(d, fullURI, html));
                }

                b.append(")");
            }

            public void visit(OWLObjectComplementOf arg0) {
                b.append(NEGATION).append(
                        write(arg0.getOperand(), fullURI, html));
            }

            public void visit(OWLObjectSomeValuesFrom arg0) {
                b.append("(\u2203 ")
                        .append(write(arg0.getProperty(), fullURI, html))
                        .append("\u22c5")
                        .append(write(arg0.getFiller(), fullURI, html))
                        .append(")");
            }

            public void visit(OWLObjectAllValuesFrom arg0) {
                b.append("(\u2200 ")
                        .append(write(arg0.getProperty(), fullURI, html))
                        .append("\u22c5")
                        .append(write(arg0.getFiller(), fullURI, html))
                        .append(")");
            }

            public void visit(OWLObjectHasValue arg0) {
                b.append("(\u2203 ")
                        .append(write(arg0.getProperty(), fullURI, html))
                        .append("\u22c5{")
                        .append(write(arg0.getValue(), fullURI, html))
                        .append("})");
            }

            public void visit(OWLObjectMinCardinality arg0) {
                b.append("(\u2265 ").append(arg0.getCardinality()).append(" ")
                        .append(write(arg0.getProperty(), fullURI, html));
                if (!arg0.getFiller().isOWLThing()) {
                    b.append(" ")
                            .append(write(arg0.getFiller(), fullURI, html));
                }
                b.append(")");
            }

            public void visit(OWLObjectExactCardinality arg0) {
                b.append("(= ").append(arg0.getCardinality()).append(" ")
                        .append(write(arg0.getProperty(), fullURI, html));
                if (!arg0.getFiller().isOWLThing()) {
                    b.append(" ")
                            .append(write(arg0.getFiller(), fullURI, html));
                }
                b.append(")");
            }

            public void visit(OWLObjectMaxCardinality arg0) {
                b.append("(\u2264 ").append(arg0.getCardinality()).append(" ")
                        .append(write(arg0.getProperty(), fullURI, html));
                if (!arg0.getFiller().isOWLThing()) {
                    b.append(" ")
                            .append(write(arg0.getFiller(), fullURI, html));
                }
                b.append(")");
            }

            public void visit(OWLObjectHasSelf arg0) {
                b.append("(\u2203 ")
                        .append(write(arg0.getProperty(), fullURI, html))
                        .append("\u22c5").append("{SELF}").append(")");
            }

            public void visit(OWLObjectOneOf arg0) {
                b.append("{");
                boolean first = true;

                for (final OWLIndividual d : arg0.getIndividuals()) {
                    if (!first) {
                        b.append(", ");
                    }
                    first = false;
                    b.append(write(d, fullURI, html));
                }

                b.append("}");
            }

            public void visit(OWLDataSomeValuesFrom arg0) {
                b.append("(\u2203 ")
                        .append(write(arg0.getProperty(), fullURI, html))
                        .append("\u22c5")
                        .append(write(arg0.getFiller(), fullURI, html))
                        .append(")");
            }

            public void visit(OWLDataAllValuesFrom arg0) {
                b.append("(\u2200 ")
                        .append(write(arg0.getProperty(), fullURI, html))
                        .append("\u22c5")
                        .append(write(arg0.getFiller(), fullURI, html))
                        .append(")");
            }

            public void visit(OWLDataHasValue arg0) {
                b.append("(\u2203 ")
                        .append(write(arg0.getProperty(), fullURI, html))
                        .append("\u22c5{")
                        .append(write(arg0.getValue(), fullURI, html))
                        .append("})");
            }

            public void visit(OWLDataMinCardinality arg0) {
                b.append("(\u2265 ").append(arg0.getCardinality()).append(" ")
                        .append(write(arg0.getProperty(), fullURI, html));

                if (arg0.isQualified()) {
                    b.append("\u22c5").append(
                            write(arg0.getFiller(), fullURI, html));
                }
                b.append(")");
            }

            public void visit(OWLDataExactCardinality arg0) {
                b.append("(= ").append(arg0.getCardinality()).append(" ")
                        .append(write(arg0.getProperty(), fullURI, html));

                if (arg0.isQualified()) {
                    b.append("\u22c5").append(
                            write(arg0.getFiller(), fullURI, html));
                }
                b.append(")");
            }

            public void visit(OWLDataMaxCardinality arg0) {
                b.append("(\u2264 ").append(arg0.getCardinality()).append(" ")
                        .append(write(arg0.getProperty(), fullURI, html));
                if (arg0.isQualified()) {
                    b.append("\u22c5").append(
                            write(arg0.getFiller(), fullURI, html));
                }
                b.append(")");
            }
        });

        return b.toString();
    }

    public String write(final OWLLiteral c, final boolean fullURI,
                        final boolean html) {
        if (c.isRDFPlainLiteral()) {
            return "\"" + c.getLiteral() + "\"" + ((c.getLang() != null && !c.getLang().isEmpty()) ? "@" + c.getLang() : "");
        } else {
            return "\"" + c.getLiteral() + "\"^^"
                    + write(c.getDatatype(), fullURI, html);
        }
    }

    private String write(final OWLDataRange dt, final boolean fullURI,
                         final boolean html) {
        return dt.toString();
    }

    private String write(final OWLPropertyExpression<?, ?> relation,
                         final boolean full, final boolean html) {
        if (relation == null) {
            throw new IllegalArgumentException("Relation is null.");
        }

        final StringBuffer b = new StringBuffer();

        relation.accept(new OWLPropertyExpressionVisitor() {

            public void visit(OWLObjectProperty arg0) {
                b.append(getName(arg0.getIRI(), full));
            }

            public void visit(OWLObjectInverseOf arg0) {
                b.append(write(arg0.getInverse(), full, html)).append("\u02c9");
            }

            public void visit(OWLDataProperty arg0) {
                b.append(getName(arg0.getIRI(), full));
            }

        });

        return b.toString();
    }

    private String write(OWLIndividual individual, boolean full,
                         final boolean html) {
        if (individual.isAnonymous()) {
            return individual.asOWLAnonymousIndividual().getID().getID();
        } else {
            return getName(individual.asOWLNamedIndividual().getIRI(), full);
        }
    }

    private static String getName(final IRI uri, final boolean fullURI) {
        if (fullURI) {
            return uri.toString();
        } else if (uri.getFragment() == null) {
            return uri.toURI().getPath()
                    .substring(uri.toURI().getPath().lastIndexOf('/') + 1);
        } else {

            return uri.getFragment();
        }
    }

    public String writeImportDeclaration(OWLImportsDeclaration axiom,
                                         boolean fullURI) {
        final StringBuffer b = new StringBuffer();
        b.append(" <- ").append(" ");
        b.append(getName(axiom.getIRI(), true));
        return b.toString();
    }

    public String write(OWLAnnotation arg0, final boolean fullURI, final boolean html) {
        final StringBuffer b = new StringBuffer();

        b.append("@").append(" ");
        b.append(" ").append(getName(arg0.getProperty().getIRI(), fullURI));

        arg0.getValue().accept(new OWLAnnotationValueVisitor() {

            public void visit(OWLLiteral arg0) {
                b.append(" ").append(write(arg0));
            }

            public void visit(OWLAnonymousIndividual arg0) {
                b.append(" ").append(write(arg0, fullURI, html));
            }

            public void visit(IRI arg0) {
                b.append(" ").append(getName(arg0, fullURI));
            }
        });

        return b.toString();
    }

    private String write(final OWLLiteral c) {
        if (c.isRDFPlainLiteral()) {
            return "\"" + c.getLiteral() + "\"" + ((c.getLang() != null && !c.getLang().isEmpty()) ? "@" + c.getLang() : "");
        } else {
            return "\"" + c.getLiteral() + "\"^^"
                    + getName(c.getDatatype().getIRI(), false);
        }
    }

}
