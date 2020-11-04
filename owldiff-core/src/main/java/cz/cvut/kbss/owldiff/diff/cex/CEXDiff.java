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

package cz.cvut.kbss.owldiff.diff.cex;

import java.util.Iterator;
import java.util.Set;

import cz.cvut.kbss.owldiff.view.ProgressListener;
import org.semanticweb.owlapi.model.OWLClass;

import cz.cvut.kbss.owldiff.OWLDiffException;
import cz.cvut.kbss.owldiff.OWLDiffException.Reason;
import cz.cvut.kbss.owldiff.change.CEXOWLChange;
import cz.cvut.kbss.owldiff.change.OWLChangeType;
import cz.cvut.kbss.owldiff.diff.AbstractDiff;
import cz.cvut.kbss.owldiff.diff.cex.impl.Diff;
import cz.cvut.kbss.owldiff.ontology.OntologyHandler;

public class CEXDiff extends AbstractDiff {

    public CEXDiff(OntologyHandler ontologyHandler) {
        super(ontologyHandler);
    }

    public CEXDiff(OntologyHandler ontologyHandler, ProgressListener listener) {
        super(ontologyHandler, listener);
    }

    @Override
    public CEXDiffOutput diff() throws OWLDiffException {

        CEXDiffOutput output = new CEXDiffOutput();

        //call cex diff from cz.cvut.kbss.owldiff.cex.impl
        Diff cexOriginal = new Diff(getListener());
        Diff cexUpdate = new Diff(getListener());

        try {
            cexOriginal.diff(ontologyHandler.getOriginalOntology(), ontologyHandler.getUpdateOntology());
            cexUpdate.diff(ontologyHandler.getUpdateOntology(), ontologyHandler.getOriginalOntology());
        } catch (OWLDiffException e) {
            /*if(e.getReason().equals(Reason.INCOMPATIBLE_ONTOLOGY)){
                output.setOntologyEL(false);
                return output;
            }
            e.printStackTrace();*/
            throw e;
        }

        output.setOriginalDiffR(cexOriginal.getDiffR());
        output.setOriginalDiffL(cexOriginal.getDiffL());
        output.setUpdateDiffR(cexUpdate.getDiffR());
        output.setUpdateDiffL(cexUpdate.getDiffL());

        Set<OWLClass> origDiff = output.getCexDiffOriginalAllElements();
        Set<OWLClass> updDiff = output.getCexDiffUpdateAllElements();
        for (Iterator<OWLClass> i = origDiff.iterator(); i.hasNext(); ) {
            OWLClass cl = i.next();
            if (output.getOriginalDiffR().contains(cl)) {
                if (output.getOriginalDiffL().contains(cl)) {
                    output.getOWLChanges().add(new CEXOWLChange(cl, true, OWLChangeType.CEX_ISIN_DIFFR_DIFFL));
                } else {
                    output.getOWLChanges().add(new CEXOWLChange(cl, true, OWLChangeType.CEX_ISIN_DIFFR));
                }
            } else if (output.getOriginalDiffL().contains(cl)) {
                output.getOWLChanges().add(new CEXOWLChange(cl, true, OWLChangeType.CEX_ISIN_DIFFL));
            }
        }
        for (Iterator<OWLClass> i = updDiff.iterator(); i.hasNext(); ) {
            OWLClass cl = i.next();
            if (output.getUpdateDiffR().contains(cl)) {
                if (output.getUpdateDiffL().contains(cl)) {
                    output.getOWLChanges().add(new CEXOWLChange(cl, false, OWLChangeType.CEX_ISIN_DIFFR_DIFFL));
                } else {
                    output.getOWLChanges().add(new CEXOWLChange(cl, false, OWLChangeType.CEX_ISIN_DIFFR));
                }
            } else if (output.getUpdateDiffL().contains(cl)) {
                output.getOWLChanges().add(new CEXOWLChange(cl, false, OWLChangeType.CEX_ISIN_DIFFL));
            }
        }
        return output;
    }

}

