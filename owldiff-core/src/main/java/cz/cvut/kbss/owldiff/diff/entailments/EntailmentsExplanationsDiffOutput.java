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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;

import cz.cvut.kbss.owldiff.change.OWLChange;
import cz.cvut.kbss.owldiff.diff.OWLDiffOutput;
import cz.cvut.kbss.owldiff.syntax.ManchesterSyntax;
import cz.cvut.kbss.owldiff.syntax.Syntax;

public class EntailmentsExplanationsDiffOutput implements OWLDiffOutput {

    private Set<OWLAxiom> inferred;
    private Set<OWLAxiom> possiblyRemove;
    private Set<OWLChange> changes;
    private Syntax syntax;
    private boolean originalOntologyInconsistent;
    private boolean updateOntologyInconsistent;

    public EntailmentsExplanationsDiffOutput() {
        this(new ManchesterSyntax());
    }

    public EntailmentsExplanationsDiffOutput(Syntax syntax) {
        inferred = new HashSet<OWLAxiom>();
        possiblyRemove = new HashSet<OWLAxiom>();
        changes = new HashSet<OWLChange>();
        this.syntax = syntax;
    }

    public Set<OWLAxiom> getInferred() {
        return inferred;
    }

    public Set<OWLAxiom> getPossiblyRemove() {
        return possiblyRemove;
    }

    public void setOriginalOntologyInconsistent(boolean originalOntologyInconsistent) {
        this.originalOntologyInconsistent = originalOntologyInconsistent;
    }

    public boolean isOriginalOntologyInconsistent() {
        return originalOntologyInconsistent;
    }

    public void setUpdateOntologyInconsistent(boolean updateOntologyInconsistent) {
        this.updateOntologyInconsistent = updateOntologyInconsistent;
    }

    public boolean isUpdateOntologyInconsistent() {
        return updateOntologyInconsistent;
    }

    public Set<OWLChange> getOWLChanges() {
        return changes;
    }

    public String outputToString() {
        String out = "\nOWLDiff - Entailments + Explanations\n\n";
        if (originalOntologyInconsistent) {
            out += "O1 (original) ontology is inconsistent!\n";
        } else if (updateOntologyInconsistent) {
            out += "O2 (update) ontology is inconsistent!\n";
        } else {
            out += "O1 - Inferred:\n--------------\n";
            for (Iterator<OWLAxiom> i = inferred.iterator(); i.hasNext(); ) {
                OWLAxiom ax = i.next();
//	            Set<OWLAxiom> axioms =getExplanation(ax, true);
                out += "Axiom: ";
                out += syntax.writeAxiom(ax, false, null, false);
                out += "\n";
//	            out += writeExplanation(axioms);
//	            out += "\n";
            }
            out += "O2 - Inferred (Possibly Remove):\n--------------------------------\n";
            for (Iterator<OWLAxiom> i = possiblyRemove.iterator(); i.hasNext(); ) {
                OWLAxiom ax = i.next();
//	            Set<OWLAxiom> axioms = getExplanation(ax, false);
                out += "Axiom: ";
                out += syntax.writeAxiom(ax, false, null, false);
                out += "\n";
//	            out += writeExplanation(axioms);
//	            out += "\n";
            }
        }
        return out;
    }

//	public String writeExplanation(Set<OWLAxiom> axioms) {
//        String b = "";
//        b += "->Inferred from the following axioms in the other ontology:";
//
//        for (OWLAxiom a : axioms) {
//            b += "\n  ";
//            b += syntax.writeAxiom(a, false, null, false);
//        }
//        b += "\n";
//        return b;
//    }
}
