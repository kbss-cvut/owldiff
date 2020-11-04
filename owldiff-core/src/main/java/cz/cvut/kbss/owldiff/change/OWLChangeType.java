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

package cz.cvut.kbss.owldiff.change;

public enum OWLChangeType {
    SYNTACTIC_ORIG_REST("This axiom has no connection to the other ontology"),
    SYNTACTIC_UPD_REST("This axiom has no connection to the other ontology"),
    ENTAILEXPL_INFERRED("Inferred from the following axioms in the other ontology"),
    ENTAILEXPL_POSSIBLY_REMOVE("Inferred from the following axioms in the other ontology (possibly remove)"),
    CEX_ISIN_DIFFR_DIFFL("Extension both contains and lacks some domain elements comparing to the other ontology"),
    CEX_ISIN_DIFFR("Extension contains some extra domain elements comparing to the other ontology"),
    CEX_ISIN_DIFFL("Extension lacks some domain elements comparing to the other ontology"),
    HEURISTIC_SAME_SUB_AND_SUPER_CLASSES("Possibly renamed class (heuristic matcher) - due to same sub/super-classes"),
    HEURISTIC_SINGLE_UNMATCHED_SIBLING("Possibly renamed class (heuristic matcher) - single unmatched sibling");


    private final String reason;

    private OWLChangeType(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
