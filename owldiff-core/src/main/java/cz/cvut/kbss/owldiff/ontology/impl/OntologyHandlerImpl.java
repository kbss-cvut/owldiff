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

package cz.cvut.kbss.owldiff.ontology.impl;

import java.net.URI;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.MissingImportHandlingStrategy;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import cz.cvut.kbss.owldiff.ontology.OntologyHandler;

public class OntologyHandlerImpl implements OntologyHandler {

    private OWLOntology originalOntology;
    private OWLOntology updateOntology;
    private OWLOntologyManager originalOntologyManager;
    private OWLOntologyManager updateOntologyManager;

    public OntologyHandlerImpl(URI oldFile, URI newFile) {
        parse(oldFile, newFile);
    }

    private void silentImports(final OWLOntologyManager m) {
        m.setOntologyLoaderConfiguration(new OWLOntologyLoaderConfiguration().setMissingImportHandlingStrategy(
            MissingImportHandlingStrategy.SILENT));
    }

    private void parse(URI oldFile, URI newFile) {
        try {
            originalOntologyManager = OWLManager.createOWLOntologyManager();
            silentImports(originalOntologyManager);

            updateOntologyManager = OWLManager.createOWLOntologyManager();
            silentImports(updateOntologyManager);

            originalOntology = originalOntologyManager.loadOntology(IRI
                    .create(oldFile));
            updateOntology = updateOntologyManager.loadOntology(IRI
                    .create(newFile));
        } catch (OWLOntologyCreationException e) {
            try {
                updateOntology = originalOntologyManager.createOntology(IRI
                        .create(""));
                originalOntology = updateOntologyManager.createOntology(IRI
                        .create(""));
            } catch (OWLOntologyCreationException e1) {
                e.printStackTrace();
            }
        }
    }

    public OWLOntology getOriginalOntology() {
        return originalOntology;
    }

    public OWLOntologyManager getOriginalOntologyManager() {
        return originalOntologyManager;
    }

    public OWLOntology getUpdateOntology() {
        return updateOntology;
    }

    public OWLOntologyManager getUpdateOntologyManager() {
        return updateOntologyManager;
    }
}
