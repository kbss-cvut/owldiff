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

import org.semanticweb.owlapi.model.OWLAxiom;

public class SyntacticAxiomChange implements SyntacticChange, OWLAxiomChange {

    private OWLAxiom axiom;
    private OWLChangeType type;

    public SyntacticAxiomChange(OWLAxiom axiom, OWLChangeType type) {
        this.axiom = axiom;
        this.type = type;
    }

    public OWLChangeType getOWLChangeType() {
        return type;
    }

    public OWLAxiom getAxiom() {
        return axiom;
    }
}
