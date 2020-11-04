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

import cz.cvut.kbss.owldiff.syntax.Syntax;
import cz.cvut.kbss.owldiff.view.nodes.*;
import org.semanticweb.owlapi.model.*;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import java.awt.*;

public class MyTreeCellRenderer extends DefaultTreeCellRenderer implements NodeModelVisitor {

    private static final long serialVersionUID = -8026145488722024237L;
    private OntologyView ontologyView;

    private Color myBackgroundSelectionColor;
    private Color myTextSelectionColor;

    private String title;
    private String tooltip;
    private Color color;
    private Color backgroundNonSelColor, borderSelColor;

    private Syntax syntax;
    private TreePath tp;

    public MyTreeCellRenderer(OntologyView ontologyView, final Color myBackgroundSelectionColor, final Color myTextSelectionColor) {
        this.ontologyView = ontologyView;
        this.myTextSelectionColor = myTextSelectionColor;
        this.myBackgroundSelectionColor = myBackgroundSelectionColor;
    }

    private void setBoth(Color c) {
        setTextSelectionColor(c);
        setTextNonSelectionColor(c);
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        setBackgroundNonSelectionColor(null);
        setTextNonSelectionColor(null);
        setTextSelectionColor(null);
        setBorderSelectionColor(null);

        if (value instanceof NodeModel) {
            NodeModel nm = (NodeModel) value;
            title = null;
            tooltip = null;
            color = null;
            backgroundNonSelColor = null;
            borderSelColor = null;

            tp = tree.getPathForRow(row);
            syntax = ontologyView.getSyntax().getSyntax();

            nm.accept(this);

            if (tooltip != null) {
                setToolTipText("<html>" + tooltip + "</html>");
            }

            if (color != null) {
                setBoth(color);
            }

            if (backgroundNonSelColor != null) {
                setBackgroundNonSelectionColor(backgroundNonSelColor);
            }
            if (borderSelColor != null) {
                setBorderSelectionColor(borderSelColor);
            }
        }
        super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        setIcon(null);
        if (title != null) {
            setText(title);
        }

        return this;
    }

    public void visit(AxiomNodeModel anm) {
        final OWLAxiom ax = anm.getData();
        final StringBuilder b = new StringBuilder();
        b.append("<html>");

        Object parent = null;

        if (!DiffVisualization.LIST_VIEW.equals(ontologyView.view)) {
            if (tp != null) {
                parent = ((NodeModel<?>) (tp.getPathComponent(tp.getPathCount() - 2))).getObject();
            }
        }

        b.append(syntax.writeAxiom(ax, false, parent, true));
        b.append("</html>");

        title = b.toString();

        tooltip = syntax.writeAxiom(ax, true, null, true);

        if (anm.isCommon()) {
            color = Color.BLUE;
        } else if (anm.isInferred()) {
            color = Color.RED;
        } else {
            color = Color.GREEN;
        }

        if (ontologyView.selectedAxiomsToMerge.contains(ax)) {
            backgroundNonSelColor = myBackgroundSelectionColor;
            borderSelColor = myTextSelectionColor;
        }
    }

    public void visit(ClassNodeModel cnm) {
        title = getTitle(cnm.getData());
        tooltip = getIriTooltip(cnm);
        color = getColor(cnm);

        Color col = null;
        OWLClass c = cnm.getData();
        if (cnm.isInBothLandR()) {
            col = Color.GRAY;
        } else if (cnm.isJustR()) {
            col = Color.ORANGE;
        } else if (cnm.isJustInL()) {
            col = Color.YELLOW;
        }
        backgroundNonSelColor = col;
        borderSelColor = col;
    }

    public void visit(PropertyNodeModel pnm) {
        title = getTitle(pnm.getData());
        tooltip = getIriTooltip(pnm);
        color = getColor(pnm);
    }

    public void visit(CategoryNodeModel cnm) {
        title = getTitle(cnm.getData());
        tooltip = getIriTooltip(cnm);
        color = getColor(cnm);
    }

    public void visit(AnnotationNodeModel anm) {
        final StringBuilder b = new StringBuilder();
        b.append("<html>");
        b.append(syntax.write(anm.getData(), false, true));
        b.append("</html>");
        title = b.toString();
    }

    public void visit(OntologyNodeModel onm) {
        title = onm.getData().getOntologyID().toString();
    }

    public void visit(IndividualNodeModel inm) {
        title = getTitle(inm.getData());
        tooltip = getIriTooltip(inm);
        color = getColor(inm);
    }

    private String getTitle(Object o) {
        if (o instanceof OWLNamedObject) {
            IRI u = ((OWLNamedObject) o).getIRI();
            if (u.getFragment() == null) {
                return u.toString();
            } else {
                return u.getFragment();
            }
        } else {
            return o.toString();
        }
    }

    private String getIriTooltip(NodeModel nm) {
        if (nm.getObject() instanceof OWLEntity) {
            tooltip = ((OWLEntity) nm.getObject()).getIRI().toString();
        }
        return null;
    }

    private Color getColor(NodeModel nm) {
        if (!(nm instanceof OntologyNodeModel)) { // not root
            if (nm.containsInferred()) {
                return Color.RED;
            } else if (nm.containsDifferent()) {
                return Color.GREEN;
            } else {
                return Color.BLUE;
            }
        }
        return null;
    }

}
