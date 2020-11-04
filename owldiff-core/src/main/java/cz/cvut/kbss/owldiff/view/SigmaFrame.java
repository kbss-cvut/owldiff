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
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import cz.cvut.kbss.owldiff.diff.cex.impl.Sig;

public class SigmaFrame extends JDialog implements ActionListener,
        MouseListener {

    private static final long serialVersionUID = -5564813046769767653L;

    private Sig sigma;
    private Object[] classes, roles;
    private boolean[] classchecks, rolechecks;

    private JList classList, roleList;
    private JButton okButton;

    public SigmaFrame(Sig sigma) {
        // super(null, true);
        setTitle("Select sigma");
        this.sigma = sigma;
        classes = sigma.getSig().toArray();
        roles = sigma.getRoles().toArray();
        classchecks = new boolean[classes.length];
        rolechecks = new boolean[roles.length];
        java.util.Arrays.fill(classchecks, true);
        java.util.Arrays.fill(rolechecks, true);
        setModal(true);

        classList = new JList(new CheckboxListModel(classes, classchecks));
        classList.setCellRenderer(new CheckboxListCellRenderer());
        classList.addMouseListener(this);
        roleList = new JList(new CheckboxListModel(roles, rolechecks));
        roleList.setCellRenderer(new CheckboxListCellRenderer());
        roleList.addMouseListener(this);

        Container root = getContentPane();
        root.setLayout(new BorderLayout());
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new FlowLayout());
        listPanel.add(new JScrollPane(classList));
        listPanel.add(new JScrollPane(roleList));
        root.add(listPanel, BorderLayout.CENTER);
        okButton = new JButton("ok");
        okButton.addActionListener(this);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(okButton);
        root.add(buttonPanel, BorderLayout.SOUTH);

        setSize(500, 400);
    }

    public void actionPerformed(ActionEvent ev) {
        if (ev.getSource() == okButton) {
            for (int i = 0; i < classes.length; i++) {
                if (!classchecks[i]) {
                    sigma.removeClass((OWLClass) classes[i]);
                }
            }
            for (int i = 0; i < roles.length; i++) {
                if (!rolechecks[i]) {
                    sigma.removeRole((OWLObjectProperty) roles[i]);
                }
            }
            setVisible(false);
        }
    }

    static class CheckboxListModel implements ListModel {

        private Object[] items;
        private boolean[] checks;

        public CheckboxListModel(Object[] items, boolean[] checks) {
            this.items = items;
            this.checks = checks;
        }

        public Object getElementAt(int i) {
            return new CheckboxListItem(items[i].toString(), checks[i]);
        }

        public int getSize() {
            return items.length;
        }

        public void addListDataListener(ListDataListener listener) {
        }

        public void removeListDataListener(ListDataListener listener) {
        }

    }

    static class CheckboxListItem {
        String label;
        boolean check;

        public CheckboxListItem(String label, boolean check) {
            this.label = label;
            this.check = check;
        }
    }

    static class CheckboxListCellRenderer extends JCheckBox implements
            ListCellRenderer {

        private static final long serialVersionUID = 2123678872128933527L;

        public Component getListCellRendererComponent(JList list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            CheckboxListItem item = (CheckboxListItem) value;
            setText(item.label);
            setSelected(item.check);
            return this;
        }

    }

    public void mouseClicked(MouseEvent ev) {
        if (ev.getSource() == classList) {
            int index = classList.locationToIndex(ev.getPoint());
            if (index >= 0 && index < classchecks.length) {
                classchecks[index] = !classchecks[index];
            }
            classList.repaint();
        } else if (ev.getSource() == roleList) {
            int index = roleList.locationToIndex(ev.getPoint());
            if (index >= 0 && index < rolechecks.length) {
                rolechecks[index] = !rolechecks[index];
            }
            roleList.repaint();
        }
    }

    public void mouseEntered(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void mouseExited(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void mousePressed(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void mouseReleased(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }
}
