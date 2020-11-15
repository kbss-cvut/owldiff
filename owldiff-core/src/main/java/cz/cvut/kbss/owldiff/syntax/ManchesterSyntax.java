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
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
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

/**
 * @author Chucky
 */
public class ManchesterSyntax implements Syntax {

    private String keyword(final String s, final boolean html) {
        if (html) {
            return "<b>" + s + "</b>";
        }
        return s;
    }

    /**
     * {@inheritDoc}
     */
    public String writeAxiom(OWLAxiom axiom, boolean fullURI, Object context,
                             final boolean html) {
        return _writeAxiom(axiom, fullURI, context, html);
    }

    private String _writeAxiom(final OWLAxiom axiom, final boolean fullURI,
                               final Object context, final boolean html) {
        if (axiom == null) {
            throw new IllegalArgumentException();
        }

        final StringBuilder b = new StringBuilder();

        axiom.accept(new OWLAxiomVisitor() {
            public void visit(OWLSubClassOfAxiom arg0) {
                if (context == null) {
                    b.append(write(arg0.getSubClass(), fullURI, html)).append(
                            " ");
                }

                b.append(keyword("SubClassOf", html)).append(" ").append(
                        write(arg0.getSuperClass(), fullURI, html));

            }

            public void visit(OWLDisjointClassesAxiom arg0) {
                b.append(keyword("DisjointWith", html)).append(" ");
                boolean first = true;
                for (final OWLClassExpression d : arg0.getClassExpressions()) {
                    if (!d.equals(context)) {
                        if (!first)
                            b.append(", ");
                        b.append(write(d, fullURI, html));
                        first = false;
                    }
                }
            }

            public void visit(OWLEquivalentClassesAxiom arg0) {
                b.append(keyword("EquivalentTo", html)).append(" ");
                boolean first = true;
                for (final OWLClassExpression d : arg0.getClassExpressions()) {
                    if (!d.equals(context)) {
                        if (!first)
                            b.append(", ");
                        b.append(write(d, fullURI, html));
                        first = false;
                    }
                }
            }

            public void visit(OWLFunctionalObjectPropertyAxiom arg0) {
                if (context == null) {
                    b.append(write(arg0.getProperty(), fullURI)).append(" ");
                }
                b.append(keyword("Characteristics", html))
                        .append(" Functional");
            }

            public void visit(OWLSymmetricObjectPropertyAxiom arg0) {
                if (context == null) {
                    b.append(write(arg0.getProperty(), fullURI)).append(" ");
                }
                b.append(keyword("Characteristics", html)).append(" Symmetric");
            }

            public void visit(OWLTransitiveObjectPropertyAxiom arg0) {
                if (context == null) {
                    b.append(write(arg0.getProperty(), fullURI)).append(" ");
                }
                b.append(keyword("Characteristics", html))
                        .append(" Transitive");
            }

            public void visit(OWLIrreflexiveObjectPropertyAxiom arg0) {
                if (context == null) {
                    b.append(write(arg0.getProperty(), fullURI)).append(" ");
                }
                b.append(keyword("Characteristics", html)).append(
                        " Irreflexive");
            }

            public void visit(OWLAsymmetricObjectPropertyAxiom arg0) {
                if (context == null) {
                    b.append(write(arg0.getProperty(), fullURI)).append(" ");
                }
                b.append(keyword("Characteristics", html))
                        .append(" Asymmetric");
            }

            public void visit(OWLReflexiveObjectPropertyAxiom arg0) {
                if (context == null) {
                    b.append(write(arg0.getProperty(), fullURI)).append(" ");
                }
                b.append(keyword("Characteristics", html)).append(" Reflexive");
            }

            public void visit(OWLInverseFunctionalObjectPropertyAxiom arg0) {
                if (context == null) {
                    b.append(write(arg0.getProperty(), fullURI)).append(" ");
                }
                b.append(keyword("Characteristics", html)).append(
                        " InverseFunctional");
            }

            public void visit(OWLEquivalentObjectPropertiesAxiom arg0) {
                b.append(keyword("EquivalentTo", html)).append(" ");
                boolean first = true;
                for (final OWLObjectPropertyExpression d : arg0.getProperties()) {
                    if (!d.equals(context)) {
                        if (!first)
                            b.append(", ");
                        b.append(write(d, fullURI));
                        first = false;
                    }
                }

            }

            public void visit(OWLDisjointObjectPropertiesAxiom arg0) {
                b.append(keyword("DisjointWith", html)).append(" ");
                boolean first = true;
                for (final OWLObjectPropertyExpression d : arg0.getProperties()) {
                    if (!d.equals(context)) {
                        if (!first)
                            b.append(", ");
                        b.append(write(d, fullURI));
                        first = false;
                    }
                }
            }

            public void visit(OWLObjectPropertyDomainAxiom arg0) {
                if (context == null) {
                    b.append(write(arg0.getProperty(), fullURI)).append(" ");
                }

                b.append(keyword("Domain", html)).append(" ").append(
                        write(arg0.getDomain(), fullURI, html));
            }

            public void visit(OWLObjectPropertyRangeAxiom arg0) {
                if (context == null) {
                    b.append(write(arg0.getProperty(), fullURI)).append(" ");
                }

                b.append(keyword("Range", html)).append(" ").append(
                        write(arg0.getRange(), fullURI, html));
            }

            public void visit(OWLSubPropertyChainOfAxiom arg0) {
                // TODO
                boolean first = true;
                for (final OWLObjectPropertyExpression e : arg0
                        .getPropertyChain()) {
                    if (!first) {
                        b.append(" ").append(keyword("o", html)).append(" ");
                    }
                    first = false;
                    b.append(write(e, fullURI));
                }

                b.append(keyword("SubPropertyOf", html)).append("  ").append(
                        write(arg0.getSuperProperty(), fullURI));
            }

            public void visit(OWLInverseObjectPropertiesAxiom arg0) {
                b.append(keyword("inverseOf", html)).append(" ");
                boolean first = true;
                for (final OWLObjectPropertyExpression d : arg0.getProperties()) {
                    if (!d.equals(context)) {
                        if (!first)
                            b.append(", ");
                        b.append(write(d, fullURI));
                        first = false;
                    }
                }

            }

            public void visit(OWLSubObjectPropertyOfAxiom arg0) {
                if (context == null) {
                    b.append(write(arg0.getSubProperty(), fullURI)).append(" ");
                }

                b.append(keyword("SubPropertyOf", html)).append(" ").append(
                        write(arg0.getSuperProperty(), fullURI));
            }

            public void visit(OWLDataPropertyDomainAxiom arg0) {
                if (context == null) {
                    b.append(write(arg0.getProperty(), fullURI)).append(" ");
                }

                b.append(keyword("Domain", html)).append(" ").append(
                        write(arg0.getDomain(), fullURI, html));
            }

            public void visit(OWLDifferentIndividualsAxiom arg0) {
                b.append(keyword("DifferentFrom", html)).append(" ");
                boolean first = true;
                for (final OWLIndividual d : arg0.getIndividuals()) {
                    if (!d.equals(context)) {
                        if (!first)
                            b.append(", ");
                        b.append(write(d, fullURI, html));
                        first = false;
                    }
                }
            }

            public void visit(OWLDisjointDataPropertiesAxiom arg0) {
                b.append(keyword("DisjointWith", html)).append(" ");
                boolean first = true;
                for (final OWLDataPropertyExpression d : arg0.getProperties()) {
                    if (!d.equals(context)) {
                        if (!first)
                            b.append(", ");
                        b.append(write(d, fullURI));
                        first = false;
                    }
                }
            }

            public void visit(OWLDisjointUnionAxiom arg0) {
                // TODO
                b.append(write(arg0.getOWLClass(), fullURI, html)).append("=");
                b.append(keyword("DisjointUnionOf", html)).append("(");

                boolean first = true;

                for (final OWLClassExpression d : arg0.getClassExpressions()) {
                    if (!first) {
                        b.append(", ");
                    }
                    first = false;
                    b.append(write(d, fullURI, html));
                }

                b.append(")");
            }

            public void visit(OWLDataPropertyRangeAxiom arg0) {
                if (context == null) {
                    b.append(write(arg0.getProperty(), fullURI)).append(" ");
                }

                b.append(keyword("Range", html)).append(" ").append(
                        write(arg0.getRange()));
            }

            public void visit(OWLFunctionalDataPropertyAxiom arg0) {
                if (context == null) {
                    b.append(write(arg0.getProperty(), fullURI)).append(" ");
                }

                b.append(keyword("Characteristics", html))
                        .append(" Functional");
            }

            public void visit(OWLEquivalentDataPropertiesAxiom arg0) {
                b.append(keyword("EquivalentTo", html)).append(" ");
                boolean first = true;
                for (final OWLDataPropertyExpression d : arg0.getProperties()) {
                    if (!d.equals(context)) {
                        if (!first)
                            b.append(", ");
                        b.append(write(d, fullURI));
                        first = false;
                    }
                }
            }

            public void visit(OWLNegativeObjectPropertyAssertionAxiom arg0) {

                if (context == null) {
                    b.append(write(arg0.getSubject(), fullURI, html)).append(
                            " ");
                    b.append(keyword("not", html));
                } else {
                    b.append(keyword("Fact: not", html)).append(" ");
                }

                b.append(write(arg0.getProperty(), fullURI)).append(" ")
                        .append(write(arg0.getObject(), fullURI, html));
            }

            public void visit(OWLObjectPropertyAssertionAxiom arg0) {
                b.append(keyword("Fact:", html)).append(" ");

                if (context == null) {
                    b.append(write(arg0.getSubject(), fullURI, html)).append(
                            " ");
                }
                b.append(write(arg0.getProperty(), fullURI)).append(" ")
                        .append(write(arg0.getObject(), fullURI, html));
            }

            public void visit(OWLClassAssertionAxiom arg0) {
                if (context == null) {
                    b.append(write(arg0.getIndividual(), fullURI, html))
                            .append(" ");
                }

                b.append(keyword("Type:", html)).append(" ").append(
                        write(arg0.getClassExpression(), fullURI, html));
            }

            public void visit(OWLDataPropertyAssertionAxiom arg0) {
                b.append(keyword("Fact:", html)).append(" ");

                if (context == null) {
                    b.append(write(arg0.getSubject(), fullURI, html)).append(
                            " ");
                }
                b.append(write(arg0.getProperty(), fullURI)).append(" ")
                        .append(write(arg0.getObject()));
            }

            public void visit(OWLNegativeDataPropertyAssertionAxiom arg0) {
                b.append(keyword("Fact: not", html)).append(" ");
                if (context == null) {
                    b.append(write(arg0.getSubject(), fullURI, html)).append(
                            " ");
                }
                b.append(write(arg0.getProperty(), fullURI)).append(" ")
                        .append(write(arg0.getObject()));
            }

            public void visit(OWLSubDataPropertyOfAxiom arg0) {
                if (context == null) {
                    b.append(write(arg0.getSubProperty(), fullURI)).append(" ");
                }
                b.append(keyword("SubPropertyOf", html)).append(" ");
                b.append(write(arg0.getSuperProperty(), fullURI));
            }

            public void visit(OWLSameIndividualAxiom arg0) {
                b.append(keyword("SameAs", html)).append(" ");
                boolean first = true;
                for (final OWLIndividual d : arg0.getIndividuals()) {
                    if (!d.equals(context)) {
                        if (!first)
                            b.append(", ");
                        b.append(write(d, fullURI, html));
                        first = false;
                    }
                }
            }

            public void visit(SWRLRule arg0) {
                b.append(arg0.toString());
                // TODO
            }

            public void visit(OWLDeclarationAxiom arg0) {
                // b.append(arg0.toString());
                b.append(keyword("Declaration", html)).append(" ");
                b.append(getName(arg0.getEntity().getIRI(), fullURI));
                // tohle jsem si vymyslel, protoze jsem to v Manchester syntaxi
                // nenasel
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
                if (context == null) {
                    b.append(getName(arg0.getSubProperty().getIRI(), fullURI))
                            .append(" ");
                }

                b.append(keyword("SubPropertyOf", html)).append(" ").append(
                        getName(arg0.getSuperProperty().getIRI(), fullURI));
            }

            public void visit(OWLAnnotationPropertyDomainAxiom arg0) {
                if (context == null) {
                    b.append(getName(arg0.getProperty().getIRI(), fullURI))
                            .append(" ");
                }

                b.append(keyword("Domain", html)).append(" ").append(
                        getName(arg0.getDomain(), fullURI));
            }

            public void visit(OWLAnnotationPropertyRangeAxiom arg0) {
                if (context == null) {
                    b.append(getName(arg0.getProperty().getIRI(), fullURI))
                            .append(" ");
                }

                b.append(keyword("Range", html)).append(" ").append(
                        getName(arg0.getRange(), fullURI));
            }

        });

        return b.toString();
    }

    public String write(final OWLClassExpression concept,
                        final boolean fullURI, final boolean html) {
        if (concept == null) {
            throw new IllegalArgumentException();
        }

        final StringBuilder b = new StringBuilder();

        concept.accept(new OWLClassExpressionVisitor() {

            public void visit(OWLClass arg0) {
                if (arg0.isOWLThing()) {
                    b.append(keyword("Thing", html));
                } else if (arg0.isOWLNothing()) {
                    b.append(keyword("Nothing", html));
                } else {
                    b.append(getName(arg0.getIRI(), fullURI));
                }
            }

            public void visit(OWLObjectIntersectionOf arg0) {
                boolean first = true;
                boolean previous = false;

                for (final OWLClassExpression d : arg0.getOperands()) {
                    if (!first) {
                        b.append(" ");
                        b.append(d.isAnonymous() && !previous ? keyword("that",
                                html) : keyword("and", html));
                        b.append(" ");
                    }
                    previous = d.isAnonymous();
                    first = false;
                    b.append(write(d, fullURI, html));

                }
            }

            public void visit(OWLObjectUnionOf arg0) {
                boolean first = true;

                for (final OWLClassExpression d : arg0.getOperands()) {
                    if (!first) {
                        b.append(" ");
                        b.append(keyword("or", html));
                        b.append(" ");
                    }
                    first = false;
                    b.append(write(d, fullURI, html));
                }
            }

            public void visit(OWLObjectComplementOf arg0) {
                b.append(keyword("not", html)).append(" ");
                b.append(write(arg0.getOperand(), fullURI, html));
            }

            public void visit(OWLObjectSomeValuesFrom arg0) {
                b.append(write(arg0.getProperty(), fullURI)).append(" ")
                        .append(keyword("some", html)).append(" ").append(
                        write(arg0.getFiller(), fullURI, html));
            }

            public void visit(OWLObjectAllValuesFrom arg0) {
                b.append(write(arg0.getProperty(), fullURI)).append(" ")
                        .append(keyword("only", html)).append(" ").append(
                        write(arg0.getFiller(), fullURI, html));
            }

            public void visit(OWLObjectHasValue arg0) {
                b.append(write(arg0.getProperty(), fullURI)).append(" ")
                        .append(keyword("value", html)).append(" ").append(
                        write(arg0.getValue(), fullURI, html));
            }

            public void visit(OWLObjectMinCardinality arg0) {
                b.append(write(arg0.getProperty(), fullURI)).append(" ")
                        .append(keyword("min", html)).append(" ").append(
                        arg0.getCardinality());

                if (!arg0.getFiller().isOWLThing()) {
                    b.append(" ")
                            .append(write(arg0.getFiller(), fullURI, html));
                }
            }

            public void visit(OWLObjectExactCardinality arg0) {
                b.append(write(arg0.getProperty(), fullURI)).append(" ")
                        .append(keyword("exactly", html)).append(" ").append(
                        arg0.getCardinality());

                if (!arg0.getFiller().isOWLThing()) {
                    b.append(" ")
                            .append(write(arg0.getFiller(), fullURI, html));
                }
            }

            public void visit(OWLObjectMaxCardinality arg0) {
                b.append(write(arg0.getProperty(), fullURI)).append(" ")
                        .append(keyword("max", html)).append(" ").append(
                        arg0.getCardinality());

                if (!arg0.getFiller().isOWLThing()) {
                    b.append(" ")
                            .append(write(arg0.getFiller(), fullURI, html));
                }
            }

            public void visit(OWLObjectHasSelf arg0) {
                b.append(write(arg0.getProperty(), fullURI)).append(" ");
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
                b.append(write(arg0.getProperty(), fullURI)).append(" ")
                        .append(keyword("some", html)).append(" ").append(
                        write(arg0.getFiller()));
            }

            public void visit(OWLDataAllValuesFrom arg0) {
                b.append(write(arg0.getProperty(), fullURI)).append(" ")
                        .append(keyword("only", html)).append(" ").append(
                        write(arg0.getFiller()));
            }

            public void visit(OWLDataHasValue arg0) {
                b.append(write(arg0.getProperty(), fullURI)).append(" ")
                        .append(keyword("value", html)).append(" ").append(
                        write(arg0.getValue()));
            }

            public void visit(OWLDataMinCardinality arg0) {
                b.append(write(arg0.getProperty(), fullURI)).append(" ")
                        .append(keyword("min", html)).append(" ").append(
                        arg0.getCardinality());

                b.append(" ").append(write(arg0.getFiller()));
            }

            public void visit(OWLDataExactCardinality arg0) {
                b.append(write(arg0.getProperty(), fullURI)).append(" ")
                        .append(keyword("exactly", html)).append(" ").append(
                        arg0.getCardinality());

                b.append(" ").append(write(arg0.getFiller()));
            }

            public void visit(OWLDataMaxCardinality arg0) {
                b.append(write(arg0.getProperty(), fullURI)).append(" ")
                        .append(keyword("max", html)).append(" ").append(
                        arg0.getCardinality());
                b.append(" ").append(write(arg0.getFiller()));
            }
        });

        return b.toString();
    }

    public String write(final OWLLiteral c) {
        if (c.isRDFPlainLiteral()) {
            return "\"" + c.getLiteral() + "\"" + ((c.getLang() != null && !c.getLang().isEmpty()) ? "@" + c.getLang() : "");
        } else {
            return "\"" + c.getLiteral() + "\"^^" + write(c.getDatatype());
        }
    }

    public String write(final OWLDataRange dt) {
        return dt.toString();
    }

    public String write(final OWLPropertyExpression relation,
                        final boolean fullURI) {
        if (relation == null) {
            throw new IllegalArgumentException("Relation is null.");
        }

        final StringBuilder b = new StringBuilder();

        relation.accept(new OWLPropertyExpressionVisitor() {

            public void visit(OWLObjectProperty arg0) {
                b.append(getName(arg0.getIRI(), fullURI));
            }

            public void visit(OWLObjectInverseOf arg0) {
                b.append(write(arg0.getInverse(), fullURI));
            }

            public void visit(OWLDataProperty arg0) {
                b.append(getName(arg0.getIRI(), fullURI));
            }

        });

        return b.toString();
    }

    private static String getName(final IRI uri, final boolean fullURI) {
        if (fullURI) {
            return uri.toString();
        } else if (uri.getFragment() == null) {
            return uri.toURI().getPath().substring(
                    uri.toURI().getPath().lastIndexOf('/') + 1);
        } else {

            return uri.getFragment();
        }
    }

    public String write(OWLIndividual individual, boolean fullURI, boolean html) {
        final StringBuffer b = new StringBuffer();

        if (html) {
            b.append("<i>");
        }

        if (individual.isAnonymous()) {
            b.append(individual.asOWLAnonymousIndividual().getID().getID());
        } else {
            b.append(getName(individual.asOWLNamedIndividual().getIRI(), fullURI));
        }

        if (html) {
            b.append("</i>");
        }

        return b.toString();
    }

    public String writeImportDeclaration(OWLImportsDeclaration axiom,
                                         boolean fullURI) {
        final StringBuffer b = new StringBuffer();
        b.append(keyword("Import", false)).append(" ");
        b.append(getName(axiom.getIRI(), true));
        return b.toString();
    }

    public String write(OWLAnnotation arg0, final boolean fullURI, final boolean html) {
        final StringBuffer b = new StringBuffer();

        b.append(keyword("Annotation", html)).append(" ");
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

}
