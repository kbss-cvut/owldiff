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
package cz.cvut.kbss.owldiff.standalone;

import cz.cvut.kbss.owldiff.view.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class StandaloneActions extends SwingActions {
    private final Map<OWLDiffStandaloneAction, Action> saActions;

    private final JFileChooser fc;

    private SwingDiffFrame f;

    private ResourceBundle resourceBundle;

    public StandaloneActions(final DiffView controller, final SwingDiffFrame f, final ResourceBundle resourceBundle) {
        super(controller, f);
        this.f = f;
        this.resourceBundle = resourceBundle;
        fc = new JFileChooser();
        saActions = new HashMap<OWLDiffStandaloneAction, Action>();
        fc.setDialogTitle(resourceBundle.getString("standalone.action.open.original.title"));
        fc.setCurrentDirectory(new File("."));
    }

    protected Action createAction(final OWLDiffStandaloneAction a) {
        final AbstractAction aa = new AbstractAction(resourceBundle.getString(a.getActionId())) {

            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                switch (a) {
                    // FILE
                    case open:
                        if (fc.showOpenDialog(f) == JFileChooser.APPROVE_OPTION) {
                            final File oldFile = fc.getSelectedFile();
                            fc.setSelectedFile(null);
                            fc.setDialogTitle(resourceBundle.getString("standalone.action.open.update.file"));
                            if (fc.showOpenDialog(f) == JFileChooser.APPROVE_OPTION) {
                                final File newFile = fc.getSelectedFile();
                                f.setOntologies(oldFile.toURI(), newFile.toURI());
                            }
                        }
                        break;
                }
            }
        };

        aa.putValue(Action.ACCELERATOR_KEY, a.getKeyStroke());
//        aa.putValue(Action.SHORT_DESCRIPTION, a.getActionId());

        return aa;
    }

    public Action getAction(final OWLDiffStandaloneAction a) {
        Action aa = saActions.get(a);

        if (aa == null) {
            aa = createAction(a);
            saActions.put(a, aa);
        }

        return aa;
    }

    public void addAction(JMenu menu, OWLDiffStandaloneAction action) {
        menu.add(new JMenuItem(getAction(action)));
    }
}