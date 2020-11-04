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
package cz.cvut.kbss.owldiff;

public class OWLDiffException extends Exception {

    private static final long serialVersionUID = -1080310498029282948L;

    private Reason r;

    public enum Reason {
        INCONSISTENT_ONTOLOGY, INCOMPATIBLE_ONTOLOGY, PARSING_FAILED, INTERNAL_ERROR;
    }

    public OWLDiffException(final Reason r, String reason) {
        super(reason);

        this.r = r;
    }

    public Reason getReason() {
        return r;
    }

}
