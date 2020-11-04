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

package cz.cvut.kbss.owldiff.diff.syntactic;

import java.util.logging.Level;
import java.util.logging.Logger;

import cz.cvut.kbss.owldiff.view.ProgressListener;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import cz.cvut.kbss.owldiff.change.OWLChangeType;
import cz.cvut.kbss.owldiff.change.SyntacticAxiomChange;
import cz.cvut.kbss.owldiff.diff.AbstractDiff;
import cz.cvut.kbss.owldiff.ontology.OntologyHandler;

public class SyntacticDiff extends AbstractDiff {

    private static Logger LOG = Logger.getLogger(SyntacticDiff.class.getName());

    private SyntacticDiffOutput output = null;

    public SyntacticDiff(OntologyHandler ontologyHandler) {
        this(ontologyHandler, null);
    }

    public SyntacticDiff(OntologyHandler ontologyHandler, ProgressListener listener) {
        super(ontologyHandler, listener);
    }

    @Override
    public SyntacticDiffOutput diff() {

        if (output != null) {
            return output;
        }

        final OWLOntology original = ontologyHandler.getOriginalOntology();
        final OWLOntology update = ontologyHandler.getUpdateOntology();

        if (LOG.isLoggable(Level.INFO)) {
            LOG.info("Original # of all axioms="
                    + original.getAxiomCount()
                    + ", # of logical axioms="
                    + original.getLogicalAxiomCount());
            LOG.info("Update # of all axioms="
                    + update.getAxiomCount()
                    + ", # of logical axioms="
                    + update.getLogicalAxiomCount());
        }

        output = new SyntacticDiffOutput();

        reset(original.getAxiomCount() + update.getAxiomCount());

        for (final OWLAxiom a1 : original.getAxioms()) {
            updateProgress();
            if (update.containsAxiom(a1)) {
                continue;
            }

            output.getInOriginal().add(a1);
            output.getOWLChanges().add(new SyntacticAxiomChange(a1, OWLChangeType.SYNTACTIC_ORIG_REST));
        }

        for (final OWLAxiom a : update.getAxioms()) {
            updateProgress();
            if (original.containsAxiom(a)) {
                continue;
            }

            output.getInUpdate().add(a);
            output.getOWLChanges().add(new SyntacticAxiomChange(a, OWLChangeType.SYNTACTIC_UPD_REST));
        }

        return output;
    }
}
