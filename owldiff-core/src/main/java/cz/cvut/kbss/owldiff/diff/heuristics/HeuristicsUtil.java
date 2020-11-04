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

import java.util.Iterator;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;

public class HeuristicsUtil {

    public static void getUnmatchedClasses(OWLOntology orig, OWLOntology upd, Set<OWLClass> unmatchedClassesOrig, Set<OWLClass> unmatchedClassesUpd) {
        unmatchedClassesOrig.addAll(orig.getClassesInSignature());
        unmatchedClassesUpd.addAll(upd.getClassesInSignature());
        for (Iterator<OWLClass> i = orig.getClassesInSignature().iterator(); i.hasNext(); ) {
            OWLClass cl = i.next();
            if (upd.containsClassInSignature(cl.getIRI())) {
                unmatchedClassesOrig.remove(cl);
                unmatchedClassesUpd.remove(cl);
            }
        }
    }


    public static void printSet(Set<OWLClass> set) {
        System.out.println("\nSet output: ");
        for (Iterator<OWLClass> i = set.iterator(); i.hasNext(); ) {
            OWLClass cl = i.next();
            System.out.println(cl.toString() + " is class? " + cl.isOWLClass());
        }
    }

    public static void printSetDesc(Set<OWLClassExpression> set) {
        System.out.println("\nSet output: ");
        for (Iterator<OWLClassExpression> i = set.iterator(); i.hasNext(); ) {
            OWLClassExpression cl = i.next();
            System.out.println(cl.toString());
        }
    }
}
