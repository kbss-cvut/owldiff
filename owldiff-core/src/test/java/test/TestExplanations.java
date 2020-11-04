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

package test;

import java.io.File;
import java.net.URI;
import java.util.Iterator;
import java.util.Set;

import cz.cvut.kbss.owldiff.diff.syntactic.SyntacticDiff;
import cz.cvut.kbss.owldiff.ontology.OntologyHandler;

import cz.cvut.kbss.owldiff.change.EntailmentsExplanationsOWLChange;
import cz.cvut.kbss.owldiff.change.OWLChange;
import cz.cvut.kbss.owldiff.diff.OWLDiffOutput;
import cz.cvut.kbss.owldiff.diff.entailments.EntailmentsExplanationsDiff;
import cz.cvut.kbss.owldiff.ontology.impl.OntologyHandlerImpl;

public class TestExplanations {

    /**
     * @param args
     */

    private static File file1 = new File("C:/simple/simple-explanation-original.owl");
    private static File file2 = new File("C:/simple/simple-explanation-update.owl");

    private static File file3 = new File("C:/simple/simple-cex-original.owl");
    private static File file4 = new File("C:/simple/simple-cex-update.owl");

    private static File file5 = new File("C:/simple/simple-inconsistent-original.owl");
    private static File file6 = new File("C:/simple/simple-inconsistent-update.owl");

    public static void main(String[] args) {

        URI orig = file1.toURI();
        URI upd = file2.toURI();
        URI orig2 = file3.toURI();
        URI upd2 = file4.toURI();
        URI orig3 = file5.toURI();
        URI upd3 = file6.toURI();

        OWLDiffOutput output = entailDiff(orig, upd);
        System.out.println(output.outputToString());

        Set<? extends OWLChange> changes = output.getOWLChanges();
        for (Iterator<? extends OWLChange> i = changes.iterator(); i.hasNext(); ) {
            EntailmentsExplanationsOWLChange ch = (EntailmentsExplanationsOWLChange) i.next();
            System.out.println("ChangeType: " + ch.getOWLChangeType() + " Axiom: " + ch.getAxiom().toString() + " Reason: " + ch.getOWLChangeType().getReason());
//			for (Iterator<OWLAxiom> j = ch.getInferred().iterator(); j
//					.hasNext();) {
//				System.out.println(j.next());
//			}
        }

        OWLDiffOutput output2 = entailDiff(orig2, upd2);
        System.out.println(output2.outputToString());

        OWLDiffOutput output3 = entailDiff(orig3, upd3);
        System.out.println(output3.outputToString());
    }

    static OWLDiffOutput entailDiff(URI o1, URI o2) {
        OntologyHandler h = new OntologyHandlerImpl(o1, o2);
        return new EntailmentsExplanationsDiff(h, null, new SyntacticDiff(h).diff()).diff();
    }

}
