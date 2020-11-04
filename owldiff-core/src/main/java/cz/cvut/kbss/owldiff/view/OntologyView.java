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

import cz.cvut.kbss.owldiff.ExplanationManager;
import cz.cvut.kbss.owldiff.diff.OWLDiffConfiguration;
import cz.cvut.kbss.owldiff.syntax.SyntaxEnum;
import cz.cvut.kbss.owldiff.view.nodes.AxiomNodeModel;
import cz.cvut.kbss.owldiff.view.nodes.NodeModel;
import org.semanticweb.owlapi.model.*;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;

public class OntologyView extends JPanel {

    private static final long serialVersionUID = -1511915926159556871L;

    protected Set<OWLAxiom> selectedAxiomsToMerge = new HashSet<OWLAxiom>();

    // VIEW
    private JLabel lblTitle;
    private JTree tre;
    private JScrollPane scp;
    private JTextPane jtp;
    private Color myTextSelectionColor, myBackgroundSelectionColor;

    protected OWLDiffTreeModel tm;

    // internal visualization state
    protected DiffVisualization view = DiffVisualization.SIMPLE_FRAME_VIEW;
    protected SyntaxEnum syntax = SyntaxEnum.MANCHESTER;
    //    protected boolean synchronizing = false;
    protected final Framework fr;

    private ExplanationManager mgr;
    //protected final Map<OWLAxiom, Set<OWLAxiom>> explanations = new HashMap<OWLAxiom, Set<OWLAxiom>>();

    private boolean original;

    private Collection<OntologyViewListener> listeners = new HashSet<OntologyViewListener>();

    public OntologyView(final Framework f, final boolean original, final Color backGroundSelectionColor, final Color textSelectionColor) {
        this.fr = f;

        this.myTextSelectionColor = textSelectionColor;
        this.myBackgroundSelectionColor = backGroundSelectionColor;

        this.original = original;

        createView();

        createControllers();
    }

    public interface OntologyViewListener {
        public void scrollChanged(OntologyView view, int value);

        public void selectionChanged(OntologyView view, final TreePath treePath);

        public void treeExpandedOrCollapsed(OntologyView view, final boolean expanded, final TreePath path);

        public void mergeSelectionChanged(OntologyView view);
    }

    public void addOntologyViewListener(OntologyViewListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeOntologyViewListener(OntologyViewListener listener) {
        if (listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }

    private void createControllers() {
        tre.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent arg0) {
                writeAxiomProperty(arg0.getPath());
                fireSelectionChange(arg0);
            }
        });

        tre.addTreeExpansionListener(new TreeExpansionListener() {

            public void treeExpanded(TreeExpansionEvent event) {
                fireExpandedOrCollapsed(true, event);
            }

            public void treeCollapsed(TreeExpansionEvent event) {
                fireExpandedOrCollapsed(false, event);
            }
        });

        tre.addMouseListener(new MouseAdapter() {
            /**
             * {@inheritDoc}
             */
            public void mousePressed(MouseEvent ev) {
                if (ev.getClickCount() == 2) {
                    TreePath path = tre.getPathForLocation(ev.getX(), ev.getY());
                    if (path != null) {
                        Object o = path.getLastPathComponent();
                        if (o instanceof AxiomNodeModel) {
                            AxiomNodeModel anm = (AxiomNodeModel) o;
                            if (!anm.isCommon()) {
                                OWLAxiom ax = anm.getData();
                                if (selectedAxiomsToMerge.contains(ax)) {
                                    selectedAxiomsToMerge.remove(ax);
                                } else {
                                    selectedAxiomsToMerge.add(ax);
                                }
                                fireMergeSelectionChanged(ev);
                            }
                        }
                    }
                }
            }
        });
    }

    private void createView() {
        lblTitle = new JLabel(getTypeLabel());
        tre = createTree();
        scp = createScrollPane(tre);
        jtp = createInfo();

        this.setLayout(new BorderLayout());
        this.add(createPane(scp, jtp), BorderLayout.CENTER);
        this.add(lblTitle, BorderLayout.NORTH);
    }

    private JTextPane createInfo() {
        JTextPane pane = new JTextPane();
        pane.setEditable(false);
        pane.setEditorKit(new HTMLEditorKit());
        pane.setVisible(false);
        return pane;
    }

    private JScrollPane createScrollPane(final JComponent c) {
        JScrollPane scp = new JScrollPane(c);
        scp.getVerticalScrollBar()
                .addAdjustmentListener(new AdjustmentListener() {
                    public void adjustmentValueChanged(AdjustmentEvent e) {
                        fireScrollChange(e);
                    }
                });
        return scp;
    }

    private void fireScrollChange(AdjustmentEvent e) {
        for (OntologyViewListener l : listeners) {
            try {
                l.scrollChanged(this, e.getValue());
            } catch (Exception ex) {
                fr.showError(ex, OWLDiffConfiguration.getCoreTranslations().getString("core.error.invalid-listener"));
            }
        }
    }

    private void fireSelectionChange(TreeSelectionEvent e) {
        for (OntologyViewListener l : listeners) {
            try {
                l.selectionChanged(this, e.getPath());
            } catch (Exception ex) {
                fr.showError(ex, OWLDiffConfiguration.getCoreTranslations().getString("core.error.invalid-listener"));
            }
        }
    }

    private void fireExpandedOrCollapsed(boolean expanded, TreeExpansionEvent e) {
        for (OntologyViewListener l : listeners) {
            try {
                l.treeExpandedOrCollapsed(this, expanded, e.getPath());
            } catch (Exception ex) {
                fr.showError(ex, OWLDiffConfiguration.getCoreTranslations().getString("core.error.invalid-listener"));
            }
        }
    }

    private void fireMergeSelectionChanged(MouseEvent e) {
        for (OntologyViewListener l : listeners) {
            try {
                l.mergeSelectionChanged(this);
            } catch (Exception ex) {
                fr.showError(ex, OWLDiffConfiguration.getCoreTranslations().getString("core.error.invalid-listener"));
            }
        }
    }

    private JPanel createPane(final JComponent center, final JComponent south) {
        final JPanel pane = new JPanel();
        pane.setLayout(new BorderLayout());
        pane.add(center, BorderLayout.CENTER);
        pane.add(south, BorderLayout.SOUTH);
        return pane;
    }

    private TreePath getTreePath(JTree tree, TreePath path) {
        Object[] o = new Object[path.getPathCount()];
        o[0] = tree.getModel().getRoot();

        for (int i = 0; i < path.getPathCount() - 1; i++) {

            boolean found = false;

            for (int j = 0; j < tree.getModel().getChildCount(o[i]); j++) {
                if (tree.getModel().getChild(o[i], j).equals(
                        path.getPathComponent(i + 1))) {
                    o[i + 1] = tree.getModel().getChild(o[i], j);
                    found = true;
                    break;
                }
            }
            if (!found) {
                return null;
            }
        }
        return new TreePath(o);
    }

    private void writeAxiomProperty(TreePath path) {
        if (path == null) {
            jtp.setVisible(false);
            return;
        }

        ((NodeModel) path.getLastPathComponent()).writeProperty(new NodeModel.WriteCallback() {
            public void notify(final String returnValue) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        if (returnValue == null) {
                            jtp.setVisible(false);
                            return;
                        }
                        jtp.setVisible(true);
                        jtp.setText(returnValue);
                    }
                });
            }
        }, syntax.getSyntax(), mgr);
    }

    private JTree createTree() {
        final JTree tree = new JTree((TreeModel) null);
        tree.setCellRenderer(new MyTreeCellRenderer(this, myBackgroundSelectionColor, myTextSelectionColor));
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        ToolTipManager.sharedInstance().registerComponent(tree);
        return tree;
    }

    public void setShowCommonAxioms(boolean show) {
        tm.setShowCommon(show);
    }

    public Set<OWLAxiom> getSelectedForMerge() {
        return selectedAxiomsToMerge;
    }

    public void syntaxChanged(SyntaxEnum syntax) {
        this.syntax = syntax;
        tm.fireTreeNodesChanged();
    }

    public void addSelected(final Collection<OWLAxiom> axioms) {
        selectedAxiomsToMerge.addAll(axioms);
        tm.fireTreeNodesChanged();
    }

    public void removeSelected(final Collection<OWLAxiom> axioms) {
        selectedAxiomsToMerge.removeAll(axioms);
        tm.fireTreeNodesChanged();
    }

    public void scrollTo(int value) {
        scp.getVerticalScrollBar().setValue(value);
    }

    public void selectItem(TreePath path) {
        if (path != null) {
            path = getTreePath(tre, path);
            tre.setSelectionPath(path);
        } else {
            tre.clearSelection();
        }

        writeAxiomProperty(path);
    }

    public void setView(DiffVisualization view) {
        tm.setView(view);
    }

    String getTypeLabel() {
        if (original)
            return OWLDiffConfiguration.getCoreTranslations().getString("core.original");
        else
            return OWLDiffConfiguration.getCoreTranslations().getString("core.update");
    }

    public void setDiffOutput(Set<OWLAxiom> axioms, OWLOntology o) {
        selectedAxiomsToMerge.clear();

        tm = new OWLDiffTreeModel(view, axioms, o);
        tre.setModel(tm);

        lblTitle.setText(String.format("%s: %s", getTypeLabel(), o.getOWLOntologyManager().getOntologyDocumentIRI(o)));

        //selectItem(null);
    }

    public void addExplanations(final Set<OWLAxiom> inferred, final ExplanationManager mgr) {
        tm.setInferred(inferred);
        this.mgr = mgr;
    }

    public void addCEX(final Set<OWLClass> diffR, final Set<OWLClass> diffL) {
        tm.setCEXDiff(diffL, diffR);
    }

    public void expandOrCollapse(boolean expand, TreePath path) {
        path = getTreePath(tre, path);
        if (path != null) {
            if (expand) {
                tre.expandPath(path);
            } else {
                tre.collapsePath(path);
            }
        }
    }

    public SyntaxEnum getSyntax() {
        return syntax;
    }
}
