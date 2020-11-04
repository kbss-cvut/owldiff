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

package cz.cvut.kbss.owldiff.diff.entailments;

import cz.cvut.kbss.owldiff.OWLDiffException;
import cz.cvut.kbss.owldiff.change.EntailmentsExplanationsOWLChange;
import cz.cvut.kbss.owldiff.change.OWLChangeType;
import cz.cvut.kbss.owldiff.change.SyntacticAxiomChange;
import cz.cvut.kbss.owldiff.diff.AbstractDiff;
import cz.cvut.kbss.owldiff.diff.OWLDiffConfiguration;
import cz.cvut.kbss.owldiff.diff.syntactic.SyntacticDiff;
import cz.cvut.kbss.owldiff.diff.syntactic.SyntacticDiffOutput;
import cz.cvut.kbss.owldiff.ontology.OntologyHandler;
import cz.cvut.kbss.owldiff.view.ProgressListener;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

public class EntailmentsExplanationsDiff extends AbstractDiff {

    private SyntacticDiffOutput syntacticDiffOutput;

    public EntailmentsExplanationsDiff(OntologyHandler ontologyHandler, ProgressListener listener, SyntacticDiffOutput syntacticDiffOutput) {
        super(ontologyHandler, listener);
        this.syntacticDiffOutput = syntacticDiffOutput;
    }

    @Override
    public EntailmentsExplanationsDiffOutput diff() {

        EntailmentsExplanationsDiffOutput output = new EntailmentsExplanationsDiffOutput();

        if (syntacticDiffOutput == null) {
            syntacticDiffOutput = new SyntacticDiff(ontologyHandler, getListener()).diff();
        }

        reset(2 + ontologyHandler.getOriginalOntology().getLogicalAxiomCount() + ontologyHandler.getUpdateOntology().getLogicalAxiomCount());

        final OWLReasoner originalEC = OWLDiffConfiguration
                .getOWLReasoner(ontologyHandler.getOriginalOntology());

        if (!originalEC.isConsistent()) {
            output.setOriginalOntologyInconsistent(true);
            return output;
        }
        updateProgress();

        final OWLReasoner updateEC = OWLDiffConfiguration
                .getOWLReasoner(ontologyHandler.getUpdateOntology());

        if (!updateEC.isConsistent()) {
            output.setUpdateOntologyInconsistent(true);
            return output;
        }
        updateProgress();

        for (final SyntacticAxiomChange axiomChange : syntacticDiffOutput.getOWLChanges()) {
            final OWLAxiom a = axiomChange.getAxiom();

            if (!a.isLogicalAxiom()) {
                continue;
            }

            if (OWLChangeType.SYNTACTIC_ORIG_REST.equals(axiomChange.getOWLChangeType())) {
                synchronized (originalEC) {
                    if (updateEC.isEntailed(a)) {
                        output.getInferred().add(a);
                        output.getOWLChanges()
                                .add(new EntailmentsExplanationsOWLChange(
                                        a,
                                        OWLChangeType.ENTAILEXPL_INFERRED));
                    }
                }
            } else if (OWLChangeType.SYNTACTIC_UPD_REST.equals(axiomChange.getOWLChangeType())) {
                synchronized (updateEC) {
                    if (originalEC.isEntailed(a)) {
                        output.getPossiblyRemove().add(a);
                        output.getOWLChanges().add(
                                new EntailmentsExplanationsOWLChange(a,

                                        OWLChangeType.ENTAILEXPL_POSSIBLY_REMOVE));
                    }
                }
            }
            updateProgress();
        }

        return output;
    }
}