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

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;

import org.protege.editor.core.ui.action.ProtegeDynamicAction;
import org.protege.editor.core.ui.view.View;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.OWLWorkspace;
import org.semanticweb.owlapi.model.OWLOntology;

import cz.cvut.kbss.owldiff.protege.OWLDiffPlugin;

public class CompareToOntologyAction extends ProtegeDynamicAction {

    private static final long serialVersionUID = 1L;
    public static Action INSTANCE;

    public void initialise() throws Exception {
        INSTANCE = this; // TODO more instances ?!?
    }

    @Override
    public void rebuildChildMenuItems(JMenu thisMenuItem) {
        OWLModelManager m = (OWLModelManager) getEditorKit().getModelManager();
        for (final OWLOntology o : m.getOntologies()) {
            thisMenuItem.add(new AbstractAction(o.getOntologyID().toString()) {
                public void actionPerformed(ActionEvent e) {
                    View view = getWorkspace()
                            .showResultsView(
                                    "cz.cvut.kbss.owldiff.protege.OWLDiff.OWLDiffPluginPanel",
                                    true, OWLWorkspace.BOTTOM_RESULTS_VIEW);

                    ((OWLDiffPlugin) (view.getViewComponent())).compareTo(o);
                }
            });
        }
    }

    public void dispose() throws Exception {
    }

    public void actionPerformed(ActionEvent e) {
    }
}
