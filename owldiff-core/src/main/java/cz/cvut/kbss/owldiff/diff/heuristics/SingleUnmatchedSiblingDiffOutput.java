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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;

import cz.cvut.kbss.owldiff.change.OWLChange;
import cz.cvut.kbss.owldiff.diff.OWLDiffOutput;

public class SingleUnmatchedSiblingDiffOutput implements OWLDiffOutput {

    private Map<OWLClass, OWLClass> possiblyRenamed;
    private Set<OWLChange> changes;

    public SingleUnmatchedSiblingDiffOutput() {
        possiblyRenamed = new HashMap<OWLClass, OWLClass>();
        changes = new HashSet<OWLChange>();
    }

    public Map<OWLClass, OWLClass> getPossiblyRenamed() {
        return possiblyRenamed;
    }

    public void setPossiblyRenamed(Map<OWLClass, OWLClass> possiblyRenamed) {
        this.possiblyRenamed = possiblyRenamed;
    }

    public Set<OWLChange> getOWLChanges() {
        return changes;
    }

    public void setOWLChanges(Set<OWLChange> changes) {
        this.changes = changes;
    }

    public String outputToString() {
        String out = "\nOWLDiff - Heuristic Diff - Single unmatched siblings\n\n";
        if (possiblyRenamed.isEmpty()) {
            out += "No changes of such type!\n";
            return out;
        }
        for (Iterator<OWLClass> i = possiblyRenamed.keySet().iterator(); i.hasNext(); ) {
            OWLClass ax = i.next();
            out += "Original: ";
            out += ax.toString();
            out += " -> Update: ";
            out += possiblyRenamed.get(ax);
            out += "\n";
        }
        return out;
    }

}
