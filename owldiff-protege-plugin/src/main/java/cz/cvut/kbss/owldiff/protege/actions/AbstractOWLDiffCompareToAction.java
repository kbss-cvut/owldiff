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
package cz.cvut.kbss.owldiff.protege.actions;

import java.awt.event.ActionEvent;

import org.protege.editor.core.ui.view.View;
import org.protege.editor.owl.model.OWLWorkspace;
import org.protege.editor.owl.ui.action.ProtegeOWLAction;

import cz.cvut.kbss.owldiff.protege.OWLDiffPlugin;

abstract class AbstractOWLDiffCompareToAction extends ProtegeOWLAction {
    private static final long serialVersionUID = 1L;

    public void initialise() throws Exception {
    }

    public void dispose() throws Exception {
    }

    public void actionPerformed(ActionEvent e) {
        View view = getOWLWorkspace().showResultsView(
                "cz.cvut.kbss.owldiff.protege.OWLDiff.OWLDiffPluginPanel",
                true, OWLWorkspace.BOTTOM_RESULTS_VIEW);

        run(((OWLDiffPlugin) (view.getViewComponent())));
    }

    protected abstract void run(final OWLDiffPlugin plugin);
}
