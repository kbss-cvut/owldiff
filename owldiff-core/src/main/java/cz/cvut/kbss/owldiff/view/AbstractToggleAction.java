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

import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.JToggleButton.ToggleButtonModel;

public abstract class AbstractToggleAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    protected boolean value;

    private List<ToggleButtonModel> listeners = new ArrayList<ToggleButtonModel>();

    public AbstractToggleAction(final String text, final Icon icon) {
        super(text, icon);
    }

    public void setState(final boolean b) {
        this.value = b;
        for (final ToggleButtonModel l : listeners) {
            l.setSelected(b);
        }
    }

    public boolean getState() {
        return value;
    }

    public JCheckBoxMenuItem createCheckBoxMenuItem() {
        final JCheckBoxMenuItem chb = new JCheckBoxMenuItem(this);
        listeners.add((ToggleButtonModel) chb.getModel());
        return chb;
    }

    public JToggleButton createToggleButton() {
        final JToggleButton chb = new JToggleButton(this);
        listeners.add((ToggleButtonModel) chb.getModel());
        return chb;
    }
}
