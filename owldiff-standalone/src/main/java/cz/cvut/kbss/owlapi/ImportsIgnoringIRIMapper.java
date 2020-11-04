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

package cz.cvut.kbss.owlapi;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;

import java.util.regex.Pattern;

public class ImportsIgnoringIRIMapper implements OWLOntologyIRIMapper {

    private final Pattern allowedPattern;

    public ImportsIgnoringIRIMapper(final String allowedIRI) {
        this(Pattern.compile(allowedIRI));
    }

    public ImportsIgnoringIRIMapper(final Pattern allowedPattern) {
        this.allowedPattern = allowedPattern;
    }

    public IRI getDocumentIRI(IRI ontologyIRI) {
        if (allowedPattern.matcher(ontologyIRI.toString()).matches()) {
            return ontologyIRI;
        } else {
            return IRI.create(String.format("file://%s", ontologyIRI.toString()));
        }
    }

    ;
}
