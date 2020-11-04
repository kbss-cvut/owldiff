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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;


public class CEXFrame extends JFrame {

    private static final long serialVersionUID = 2146645778691640850L;

    public CEXFrame(cz.cvut.kbss.owldiff.diff.cex.impl.Diff diff) {
        super("CEX result");

        JList diffR = new JList(diff.getDiffR().toArray());
        JList diffL = new JList(diff.getDiffL().toArray());

        JPanel diffRPanel = new JPanel();
        diffRPanel.setLayout(new BorderLayout());
        diffRPanel.add(new JLabel("diffR"), BorderLayout.NORTH);
        diffRPanel.add(diffR, BorderLayout.CENTER);
        diffRPanel.setBackground(new Color(120, 180, 255));
        JPanel diffLPanel = new JPanel();
        diffLPanel.setLayout(new BorderLayout());
        diffLPanel.add(new JLabel("diffL"), BorderLayout.NORTH);
        diffLPanel.add(diffL, BorderLayout.CENTER);
        diffLPanel.setBackground(new Color(180, 180, 255));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, diffLPanel, diffRPanel);
        Container root = getContentPane();
        root.setLayout(new BorderLayout());
        root.add(splitPane, BorderLayout.CENTER);
        pack();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screen.width / 2 - getWidth() / 2, screen.height / 2 - getHeight());
        pack();
        setVisible(true);
    }

}
