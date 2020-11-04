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

import cz.cvut.kbss.owldiff.change.HeuristicOWLChange;
import cz.cvut.kbss.owldiff.change.OWLChange;
import cz.cvut.kbss.owldiff.diff.OWLDiff;
import cz.cvut.kbss.owldiff.diff.OWLDiffOutput;
import cz.cvut.kbss.owldiff.diff.heuristics.SameSubAndSuperClassesDiff;
import cz.cvut.kbss.owldiff.diff.heuristics.SingleUnmatchedSiblingDiff;
import cz.cvut.kbss.owldiff.ontology.impl.OntologyHandlerImpl;

public class TestH1 {

    /**
     * @param args
     */

    private static File file1 = new File("C:/simple/file1.owl");
    private static File file2 = new File("C:/simple/file1-renamed.owl");

    public static void main(String[] args) {

        URI orig = file1.toURI();
        URI upd = file2.toURI();

        try {
            OWLDiff d = new SameSubAndSuperClassesDiff(new OntologyHandlerImpl(orig, upd));
            OWLDiffOutput output = d.diff();
            System.out.println(output.outputToString());

            Set<? extends OWLChange> changes = output.getOWLChanges();
            for (Iterator<? extends OWLChange> i = changes.iterator(); i.hasNext(); ) {
                HeuristicOWLChange ch = (HeuristicOWLChange) i.next();
                System.out.println("ChangeType: " + ch.getOWLChangeType() + " ClassOrig: " + ch.getOriginal().toString() + " ClassUpdate: " + ch.getUpdate().toString() + " Reason: " + ch.getOWLChangeType().getReason());
            }

            OWLDiff d2 = new SingleUnmatchedSiblingDiff(new OntologyHandlerImpl(orig, upd));
            OWLDiffOutput output2 = d2.diff();
            System.out.println(output2.outputToString());

            Set<? extends OWLChange> changes2 = output2.getOWLChanges();
            for (Iterator<? extends OWLChange> i = changes2.iterator(); i.hasNext(); ) {
                HeuristicOWLChange ch = (HeuristicOWLChange) i.next();
                System.out.println("ChangeType: " + ch.getOWLChangeType() + " ClassOrig: " + ch.getOriginal().toString() + " ClassUpdate: " + ch.getUpdate().toString() + " Reason: " + ch.getOWLChangeType().getReason());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
