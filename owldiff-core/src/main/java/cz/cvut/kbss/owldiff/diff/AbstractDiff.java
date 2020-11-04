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

package cz.cvut.kbss.owldiff.diff;

import cz.cvut.kbss.owldiff.OWLDiffException;
import cz.cvut.kbss.owldiff.ontology.OntologyHandler;
import cz.cvut.kbss.owldiff.view.ProgressListener;

public abstract class AbstractDiff implements OWLDiff {

    protected OntologyHandler ontologyHandler;

    private ProgressListener listener;

    private int progress = 0;

    public AbstractDiff(OntologyHandler ontologyHandler) {
        this(ontologyHandler, null);
    }

    public AbstractDiff(OntologyHandler ontologyHandler, ProgressListener listener) {
        this.ontologyHandler = ontologyHandler;
        this.listener = listener;
    }

    protected ProgressListener getListener() {
        return listener;
    }

    public OntologyHandler getOntologyHandler() {
        return ontologyHandler;
    }

    protected void reset(int max) {
        setProgress(0);
        if (listener != null) {
            listener.setProgressMax(max);
        }
    }

    protected void updateProgress() {
        setProgress(progress + 1);
    }

    private void setProgress(int progress) {
        this.progress = progress;
        if (listener != null) {
            listener.setProgress(progress);
        }
    }

    public abstract OWLDiffOutput diff() throws OWLDiffException;

}
