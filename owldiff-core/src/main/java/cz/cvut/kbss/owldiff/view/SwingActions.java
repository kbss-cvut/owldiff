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
package cz.cvut.kbss.owldiff.view;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

import cz.cvut.kbss.owldiff.OWLDiffException;
import cz.cvut.kbss.owldiff.OWLDiffException.Reason;
import cz.cvut.kbss.owldiff.diff.OWLDiffConfiguration;
import cz.cvut.kbss.owldiff.syntax.SyntaxEnum;

public class SwingActions {

    private final Map<OWLDiffAction, Action> actions;

    private final DiffView c;

    private final Framework f;

    public SwingActions(final DiffView controller, final Framework f) {
        this.c = controller;
        this.f = f;
        this.actions = new HashMap<OWLDiffAction, Action>();
    }

    protected Action createAction(final OWLDiffAction a, Icon icon) {
        final AbstractAction aa = new AbstractAction(OWLDiffConfiguration.getCoreTranslations().getString(a.getActionId()), icon) {

            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                switch (a) {
                    // FILE
                    case exit:
                        f.quit();
                        break;

                    case useCEX:
                        c.runDiff(DiffView.DiffEnum.CEX);
                        break;

                    case showExplanations:
                        c.runDiff(DiffView.DiffEnum.ENTAILMENT);
                        break;

                    // SELECTION / MERGE
                    case selectAllOriginal:
                        c.selectAllOriginalAxioms();
                        break;
                    case deselectAllOriginal:
                        c.deselectAllOriginalAxioms();
                        break;

                    case selectAllUpdate:
                        c.selectAllUpdateAxioms();
                        break;
                    case deselectAllUpdate:
                        c.deselectAllUpdateAxioms();
                        break;

                    case merge:
                        c.saveMergeResult(false);
                        break;
                    case mergeToFile:
                        c.saveMergeResult(true);
                        break;

                    // STRUCTURE VIEW ACTIONS
                    case showAxiomList:
                        c.setVisualization(DiffVisualization.LIST_VIEW);
                        break;
                    case showAssertedFrames:
                        c.setVisualization(DiffVisualization.SIMPLE_FRAME_VIEW);
                        break;
                    case showClassifiedFrames:
                        c.setVisualization(DiffVisualization.CLASSIFIED_FRAME_VIEW);
                        break;

                    // VISUALIZATION SYNTAX
                    case manchester:
                        c.setSyntax(SyntaxEnum.MANCHESTER);
                        break;

                    case descriptionLogic:
                        c.setSyntax(SyntaxEnum.DL);
                        break;
                    default:
                        String msg = String.format(OWLDiffConfiguration.getCoreTranslations().getString("core.msg.unsupported-action-type"), a);
                        f.showError(new OWLDiffException(Reason.INTERNAL_ERROR, msg), msg);

                }
            }
        };

        aa.putValue(Action.ACCELERATOR_KEY, a.getKeyStroke());
//        aa.putValue(Action.SHORT_DESCRIPTION, a.getActionId());
        return aa;
    }

    protected AbstractToggleAction createToggleAction(final OWLDiffAction a, final Icon icon) {
        final AbstractToggleAction aa = new AbstractToggleAction(OWLDiffConfiguration.getCoreTranslations().getString(a.getActionId()), icon) {

            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                setState(!getState());
            }

            public void setState(boolean state) {
                super.setState(state);
                switch (a) {
                    case showCommon:
                        c.setShowCommonAxioms(state);
                        break;
                    case considerAnnotations:
                        c.setConsiderAnnotations(state);
                        break;

                    default:
                        String msg = String.format(OWLDiffConfiguration.getCoreTranslations().getString("core.msg.unsupported-action-type"), a);
                        f.showError(new OWLDiffException(Reason.INTERNAL_ERROR, msg), msg);
                }
            }
        };

        aa.putValue(Action.ACCELERATOR_KEY, a.getKeyStroke());
//        aa.putValue(Action.SHORT_DESCRIPTION, a.getActionId());

        return aa;
    }

    private Action getAction(final OWLDiffAction a, final Icon icon) {
        Action aa = actions.get(a);

        if (aa == null) {
            if (a.isToggle()) {
                aa = createToggleAction(a, icon);
            } else {
                aa = createAction(a, icon);
            }
            actions.put(a, aa);
        }

        return aa;
    }

    public Action getAction(final OWLDiffAction a) {
        return getAction(a, null);
    }

    public void addRadioButtonGroup(JComponent menu, final Icon[] icons, OWLDiffAction defaultAction, OWLDiffAction... action) {
        final ButtonGroup gView = new ButtonGroup();
        int ix = 0;
        for (final OWLDiffAction a : action) {
            final AbstractButton b = addAction(menu, getAction(a, icons == null ? null : icons[ix++]), action.length);
            gView.add(b);
            if (a == defaultAction) {
                gView.setSelected(b.getModel(), true);
            }
        }
    }

    public void addRadioButtonGroup(JComponent menu, OWLDiffAction defaultAction, OWLDiffAction... action) {
        addRadioButtonGroup(menu, null, defaultAction, action);
    }

    public void addAction(JComponent menu, OWLDiffAction action, Icon icon) {
        addAction(menu, getAction(action, icon), 0);
    }

    private AbstractButton addAction(JComponent c, Action a, int buttonGroupLength) {
        final AbstractButton b;
        if (buttonGroupLength > 1) {
            if (c instanceof JToolBar) {
                b = new JToggleButton(a);
                b.setText(null);
            } else {
                b = new JRadioButtonMenuItem(a);
            }
            c.add(b);
        } else if (a instanceof AbstractToggleAction) {
            AbstractToggleAction ata = (AbstractToggleAction) a;
            if (c instanceof JToolBar) {
                b = ata.createToggleButton();
                b.setText(null);
            } else {
                b = ata.createCheckBoxMenuItem();
            }
            c.add(b);
        } else {
            if (c instanceof JToolBar) {
                b = ((JToolBar) c).add(a);
            } else {
                b = new JMenuItem(a);
                c.add(b);
            }
        }
        return b;
    }

    public void addAction(JComponent menu, OWLDiffAction action) {
        addAction(menu, action, null);
    }
}
