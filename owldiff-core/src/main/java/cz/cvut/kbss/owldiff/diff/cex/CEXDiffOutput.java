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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;

import cz.cvut.kbss.owldiff.change.OWLChange;
import cz.cvut.kbss.owldiff.diff.OWLDiffOutput;

public class CEXDiffOutput implements OWLDiffOutput {

    private Set<OWLClass> originalDiffR;
    private Set<OWLClass> originalDiffL;
    private Set<OWLClass> updateDiffR;
    private Set<OWLClass> updateDiffL;
    private Set<OWLChange> changes;
    //private boolean ontologyEL;

    public CEXDiffOutput() {
        originalDiffR = new HashSet<OWLClass>();
        originalDiffL = new HashSet<OWLClass>();
        updateDiffR = new HashSet<OWLClass>();
        updateDiffL = new HashSet<OWLClass>();
        changes = new HashSet<OWLChange>();
        //ontologyEL = true;
    }

    public Set<OWLClass> getOriginalDiffR() {
        return originalDiffR;
    }

    public void setOriginalDiffR(Set<OWLClass> originalDiffR) {
        this.originalDiffR = originalDiffR;
    }

    public Set<OWLClass> getOriginalDiffL() {
        return originalDiffL;
    }

    public void setOriginalDiffL(Set<OWLClass> originalDiffL) {
        this.originalDiffL = originalDiffL;
    }

    public Set<OWLClass> getUpdateDiffR() {
        return updateDiffR;
    }

    public void setUpdateDiffR(Set<OWLClass> updateDiffR) {
        this.updateDiffR = updateDiffR;
    }

    public Set<OWLClass> getUpdateDiffL() {
        return updateDiffL;
    }

    public void setUpdateDiffL(Set<OWLClass> updateDiffL) {
        this.updateDiffL = updateDiffL;
    }

    /*public boolean isOntologyEL() {
         return ontologyEL;
     }

     public void setOntologyEL(boolean ontologyEL) {
         this.ontologyEL = ontologyEL;
     }*/

    public Set<OWLChange> getOWLChanges() {
        return changes;
    }

    public void setOWLChanges(Set<OWLChange> changes) {
        this.changes = changes;
    }

    public String outputToString() {
        String out = "\nOWLDiff - CEX Diff\n\n";
        //if (ontologyEL) {
        Set<OWLClass> origDiff = getCexDiffOriginalAllElements();
        Set<OWLClass> updDiff = getCexDiffUpdateAllElements();
        out += "O1 - CEX diff O1 -> O2:\n";
        for (Iterator<OWLClass> i = origDiff.iterator(); i.hasNext(); ) {
            OWLClass cl = i.next();
            out += "  Class ";
            out += cl.toString();
            out += "\n  ->";
            out += getCEXExplanation(cl, true);
            out += "\n\n";
        }
        out += "O2 - CEX diff O2 -> O1:\n";
        for (Iterator<OWLClass> i = updDiff.iterator(); i.hasNext(); ) {
            OWLClass cl = i.next();
            out += "  Class ";
            out += cl.toString();
            out += "\n  ->";
            out += getCEXExplanation(cl, false);
            out += "\n\n";
        }
        /*} else {
              out += "Ontology is incompatible for CEX algorithm (is not EL)!\n";
          }*/
        return out;
    }

    private String getCEXExplanation(OWLClass ax, boolean original) {
        String s = "";
        if (original) {
            if (originalDiffR.contains(ax)) {
                if (originalDiffL.contains(ax)) {
                    s += "Extension both contains and lacks some domain elements comparing to the other ontology.";
                } else {
                    s += "Extension contains some extra domain elements comparing to the other ontology.";
                }
            } else if (originalDiffL.contains(ax)) {
                s += "Extension lacks some domain elements comparing to the other ontology.";
            }
        } else {
            if (updateDiffR.contains(ax)) {
                if (updateDiffL.contains(ax)) {
                    s += "Extension both contains and lacks some domain elements comparing to the other ontology.";
                } else {
                    s += "Extension contains some extra domain elements comparing to the other ontology.";
                }
            } else if (updateDiffL.contains(ax)) {
                s += "Extension lacks some domain elements comparing to the other ontology.";
            }
        }
        return s;
    }

    public Set<OWLClass> getCexDiffOriginalAllElements() {
        Set<OWLClass> all = new HashSet<OWLClass>();
        for (Iterator<OWLClass> i = originalDiffR.iterator(); i.hasNext(); )
            all.add(i.next());
        for (Iterator<OWLClass> i = originalDiffL.iterator(); i.hasNext(); )
            all.add(i.next());
        return all;
    }

    public Set<OWLClass> getCexDiffUpdateAllElements() {
        Set<OWLClass> all = new HashSet<OWLClass>();
        for (Iterator<OWLClass> i = updateDiffR.iterator(); i.hasNext(); )
            all.add(i.next());
        for (Iterator<OWLClass> i = updateDiffL.iterator(); i.hasNext(); )
            all.add(i.next());
        return all;
    }

}
