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
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;

import uk.ac.manchester.cs.owl.owlapi.mansyntaxrenderer.ManchesterOWLSyntaxOWLObjectRendererImpl;

public class ManchesterSyntaxOriginal implements Syntax {
    final ManchesterOWLSyntaxOWLObjectRendererImpl s = new ManchesterOWLSyntaxOWLObjectRendererImpl();

    public String writeAxiom(final OWLAxiom axiom, final boolean fullURI,
                             final Object context, final boolean html) {
        return s.render(axiom);
    }

    public String writeImportDeclaration(OWLImportsDeclaration axiom,
                                         boolean fullURI) {
        return getName(axiom.getIRI(), fullURI);
    }

    public String write(OWLAnnotation axiom, boolean fullURI,
                        boolean html) {
        return s.render(axiom);
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
}
