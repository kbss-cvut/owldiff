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

package cz.cvut.kbss.owldiff.diff.heuristics;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;

import cz.cvut.kbss.owldiff.change.HeuristicOWLChange;
import cz.cvut.kbss.owldiff.change.OWLChangeType;
import cz.cvut.kbss.owldiff.diff.AbstractDiff;
import cz.cvut.kbss.owldiff.diff.OWLDiffOutput;
import cz.cvut.kbss.owldiff.ontology.OntologyHandler;

public class SingleUnmatchedSiblingDiff extends AbstractDiff {

    public SingleUnmatchedSiblingDiff(OntologyHandler ontologyHandler) {
        super(ontologyHandler);
    }

    @Override
    public OWLDiffOutput diff() {

        SingleUnmatchedSiblingDiffOutput output = new SingleUnmatchedSiblingDiffOutput();
        OWLOntology orig = ontologyHandler.getOriginalOntology();
        OWLOntology upd = ontologyHandler.getUpdateOntology();

        Set<OWLClass> unmatchedClassesOrig = new HashSet<OWLClass>();
        Set<OWLClass> unmatchedClassesUpd = new HashSet<OWLClass>();

        HeuristicsUtil.getUnmatchedClasses(orig, upd, unmatchedClassesOrig, unmatchedClassesUpd);

        for (Iterator<OWLClass> i = unmatchedClassesOrig.iterator(); i.hasNext(); ) {
            OWLClass clo = i.next();
            Set<OWLClassExpression> supo = clo.getSuperClasses(orig);
            for (Iterator<OWLClass> j = unmatchedClassesUpd.iterator(); j.hasNext(); ) {
                OWLClass clu = j.next();
                Set<OWLClassExpression> supu = clu.getSuperClasses(upd);

                if (!(supo.size() == 1 && supo.containsAll(supu))) continue;
                else {
                    Set<OWLClassExpression> supoSib = supo.iterator().next().asOWLClass().getSubClasses(orig);
                    Set<OWLClassExpression> supuSib = supu.iterator().next().asOWLClass().getSubClasses(upd);
                    supoSib.remove(clo);
                    supuSib.remove(clu);
                    if (supoSib.containsAll(supuSib)) {
                        output.getPossiblyRenamed().put(clo, clu);
                        output.getOWLChanges().add(new HeuristicOWLChange(clo, clu, OWLChangeType.HEURISTIC_SINGLE_UNMATCHED_SIBLING));
                    }
                }
            }
        }

        return output;
    }

}
