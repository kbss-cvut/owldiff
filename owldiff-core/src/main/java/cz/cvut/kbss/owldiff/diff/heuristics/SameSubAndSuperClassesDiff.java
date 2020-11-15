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

import java.util.stream.Collectors;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;

import cz.cvut.kbss.owldiff.change.HeuristicOWLChange;
import cz.cvut.kbss.owldiff.change.OWLChangeType;
import cz.cvut.kbss.owldiff.diff.AbstractDiff;
import cz.cvut.kbss.owldiff.diff.OWLDiffOutput;
import cz.cvut.kbss.owldiff.ontology.OntologyHandler;

public class SameSubAndSuperClassesDiff extends AbstractDiff {

    public SameSubAndSuperClassesDiff(OntologyHandler ontologyHandler) {
        super(ontologyHandler);
    }

    @Override
    public OWLDiffOutput diff() {

        SameSubAndSuperClassesDiffOutput output = new SameSubAndSuperClassesDiffOutput();

        OWLOntology orig = ontologyHandler.getOriginalOntology();
        OWLOntology upd = ontologyHandler.getUpdateOntology();

        Set<OWLClass> unmatchedClassesOrig = new HashSet<OWLClass>();
        Set<OWLClass> unmatchedClassesUpd = new HashSet<OWLClass>();

        HeuristicsUtil.getUnmatchedClasses(orig, upd, unmatchedClassesOrig, unmatchedClassesUpd);

        for (Iterator<OWLClass> i = unmatchedClassesOrig.iterator(); i.hasNext(); ) {
            OWLClass cl = i.next();
            Set<OWLClassExpression> sub1 = HeuristicsUtil.getSubClasses(orig,cl);
            Set<OWLClassExpression> sup1 = HeuristicsUtil.getSuperClasses(orig,cl);
            for (Iterator<OWLClass> j = unmatchedClassesUpd.iterator(); j.hasNext(); ) {
                OWLClass clu = j.next();
                Set<OWLClassExpression> sub2 = HeuristicsUtil.getSubClasses(upd,clu);
                Set<OWLClassExpression> sup2 = HeuristicsUtil.getSubClasses(upd,clu);
                if (sub1.equals(sub2) && sup1.equals(sup2)) {
                    output.getPossiblyRenamed().put(cl, clu);
                    output.getOWLChanges().add(new HeuristicOWLChange(cl, clu, OWLChangeType.HEURISTIC_SAME_SUB_AND_SUPER_CLASSES));
                }
            }
        }

        return output;
    }

}
