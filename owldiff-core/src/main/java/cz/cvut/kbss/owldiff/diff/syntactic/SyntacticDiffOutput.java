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

package cz.cvut.kbss.owldiff.diff.syntactic;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import cz.cvut.kbss.owldiff.change.OWLAxiomChange;
import cz.cvut.kbss.owldiff.change.SyntacticAxiomChange;
import cz.cvut.kbss.owldiff.diff.OWLDiffOutput;
import cz.cvut.kbss.owldiff.syntax.ManchesterSyntax;
import cz.cvut.kbss.owldiff.syntax.Syntax;
import org.semanticweb.owlapi.model.OWLAxiom;

public class SyntacticDiffOutput implements OWLDiffOutput {

    private Set<SyntacticAxiomChange> changes = new HashSet<SyntacticAxiomChange>();

    private Syntax syntax;

    private Set<OWLAxiom> inOriginal;
    private Set<OWLAxiom> inUpdate;

    public SyntacticDiffOutput() {
        this(new ManchesterSyntax());
    }

    public SyntacticDiffOutput(Syntax syntax) {
        this.syntax = syntax;
        inOriginal = new HashSet<OWLAxiom>();
        inUpdate = new HashSet<OWLAxiom>();
    }

    public void addChange(SyntacticAxiomChange change) {
        changes.add(change);
        switch (change.getOWLChangeType()) {
            case SYNTACTIC_ORIG_REST:
                inOriginal.add(change.getAxiom());
                break;
            case SYNTACTIC_UPD_REST:
                inUpdate.add(change.getAxiom());
                break;
            default:
                throw new RuntimeException("Unexpected axiom change type: " + change.getOWLChangeType());
        }
    }

    public Set<SyntacticAxiomChange> getOWLChanges() {
        return changes;
    }

    public Set<OWLAxiom> getInOriginal() {
        return inOriginal;
    }

    public Set<OWLAxiom> getInUpdate() {
        return inUpdate;
    }

    public String outputToString(String prefix, Set<OWLAxiom> axioms) {
        String out = "";
        out += prefix + "additional axioms:\n--------\n\n";
        for (Iterator<OWLAxiom> i = axioms.iterator(); i.hasNext(); ) {
            OWLAxiom ax = i.next();
            out += prefix + "  ";
            out += syntax.writeAxiom(ax, false, null, false);
            out += "\n";
        }
        return out;
    }

    public String outputToString() {
        StringBuilder builder = new StringBuilder();
        builder.append("\nOWLDiff - Syntactic Diff\n\n");

        String outOrig = outputToString("  ", inOriginal);
        if (!outOrig.isEmpty()) {
            builder.append(String.format("original:\n--------\n\n%s", outOrig));
        }

        String outUpdate = outputToString("  ", inUpdate);
        if (!outUpdate.isEmpty()) {
            builder.append(String.format("\n update:\n--------\n\n%s", outUpdate));
        }
        return builder.toString();
    }
}
