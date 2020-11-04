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

import cz.cvut.kbss.owldiff.change.CEXOWLChange;
import cz.cvut.kbss.owldiff.change.OWLChange;
import cz.cvut.kbss.owldiff.diff.OWLDiff;
import cz.cvut.kbss.owldiff.diff.OWLDiffOutput;
import cz.cvut.kbss.owldiff.diff.cex.CEXDiff;
import cz.cvut.kbss.owldiff.ontology.impl.OntologyHandlerImpl;

public class TestCEX {

    /**
     * @param args
     */

    private static File file1 = new File("C:/simple/simple-cex-original.owl");
    private static File file2 = new File("C:/simple/simple-cex-update.owl");
    private static File file1noEL = new File("C:/simple/simple-explanation-original.owl");
    private static File file2noEL = new File("C:/simple/simple-explanation-update.owl");

    public static void main(String[] args) {

        URI orig = file1.toURI();
        URI upd = file2.toURI();
        URI origNoEL = file1noEL.toURI();
        URI updNoEL = file2noEL.toURI();

        try {
            OWLDiff dNoEL = new CEXDiff(new OntologyHandlerImpl(origNoEL, updNoEL));
            OWLDiffOutput outputNoEL = dNoEL.diff();
            OWLDiff d = new CEXDiff(new OntologyHandlerImpl(orig, upd));
            OWLDiffOutput output = d.diff();
            System.out.println(outputNoEL.outputToString());
            System.out.println(output.outputToString());

            Set<? extends OWLChange> changes = output.getOWLChanges();
            for (Iterator<? extends OWLChange> i = changes.iterator(); i.hasNext(); ) {
                CEXOWLChange ch = (CEXOWLChange) i.next();
                System.out.println("ChangeType: " + ch.getOWLChangeType() + " Class: " + ch.getOWLClass().toString() + " fromOrig: " + ch.isFromOriginal() + " Reason: " + ch.getOWLChangeType().getReason());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
