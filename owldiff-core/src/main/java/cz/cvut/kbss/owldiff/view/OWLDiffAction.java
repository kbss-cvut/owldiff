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

import javax.swing.KeyStroke;

public enum OWLDiffAction {
    // http://www.famfamfam.com/lab/icons/silk/preview.php
    exit("core.action.exit", KeyStroke.getKeyStroke("control X")),
    useCEX("core.action.use-cex", KeyStroke.getKeyStroke("control W")), // TODO icon 'CEX'
    showExplanations("core.action.show-explanations", KeyStroke.getKeyStroke("control E")), // book_open.png
    selectAllOriginal("core.action.select-all-original", KeyStroke.getKeyStroke("control S")), // table_row_insert.png
    selectAllUpdate("core.action.select-all-update", KeyStroke.getKeyStroke("control U")), // table_row_insert.png
    deselectAllOriginal("core.action.deselect-all-original", KeyStroke.getKeyStroke("control D")), // table_row_delete.png
    deselectAllUpdate("core.action.deselect-all-update", KeyStroke.getKeyStroke("control Q")), // table_row_delete.png
    merge("core.action.merge", KeyStroke.getKeyStroke("control M")), // table_relationship.png
    mergeToFile("core.action.merge-to-file", KeyStroke.getKeyStroke("control M")), // table_relationship.png
    showCommon("core.action.show-common-axioms", KeyStroke.getKeyStroke("control A"), true), //eye.png
    showAxiomList("core.action.show-axiom-list", KeyStroke.getKeyStroke("control L")), // application_view_list.png
    showAssertedFrames("core.action.show-asserted-frames", KeyStroke.getKeyStroke("control F")), // chart_organisation.png
    showClassifiedFrames("core.action.show-classified-frames", KeyStroke.getKeyStroke("control C")), // chart_organisation.png - prebarvit
    manchester("core.action.manchester-syntax", KeyStroke.getKeyStroke("control R")), // TODO icon 'M'
    descriptionLogic("core.action.description-logic-syntax", KeyStroke.getKeyStroke("control I")), // TODO icon subsumpce
    considerAnnotations("core.action.consider-annotations", KeyStroke.getKeyStroke("control J"), true);

    private Object keyStroke;
    private String actionId;
    private boolean toggle;

    OWLDiffAction(String actionId, Object keyStroke) {
        this(actionId, keyStroke, false);
    }

    OWLDiffAction(String actionId, Object keyStroke, boolean toggle) {
        this.keyStroke = keyStroke;
        this.actionId = actionId;
        this.toggle = toggle;
    }

    public Object getKeyStroke() {
        return keyStroke;
    }

    public String getActionId() {
        return actionId;
    }

    public boolean isToggle() {
        return toggle;
    }
}
