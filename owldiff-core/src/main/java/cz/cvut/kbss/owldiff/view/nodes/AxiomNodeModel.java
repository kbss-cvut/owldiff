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

package cz.cvut.kbss.owldiff.view.nodes;

import cz.cvut.kbss.owldiff.ExplanationManager;
import cz.cvut.kbss.owldiff.diff.OWLDiffConfiguration;
import cz.cvut.kbss.owldiff.syntax.Syntax;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: marek
 * Date: 10/3/12
 * Time: 10:37 AM
 * To change this template use File | Settings | File Templates.
 */
public class AxiomNodeModel extends NodeModel<OWLAxiom> {

    protected List<AnnotationNodeModel> annotations;
    protected Set<OWLAxiom> explanations;

    protected boolean inferred;
    protected boolean common;

    public AxiomNodeModel(OWLAxiom ax, NodeModel<?> parent, boolean isInferred, boolean isCommon) {
        super(ax, parent);
        this.inferred = isInferred;
        this.common = isCommon;
        annotations = new ArrayList<AnnotationNodeModel>();
        for (OWLAnnotation a : ax.getAnnotations()) {
            annotations.add(new AnnotationNodeModel(a, this));
        }
    }

    public int getCount(boolean showCommon) {
        return annotations.size();
    }

    public int getIndexOf(Object o, boolean showCommon) {
        return annotations.indexOf(o);
    }

    public AnnotationNodeModel getChild(int index, boolean showCommon) {
        return annotations.get(index);
    }

    @Override
    public boolean isLeaf(boolean showCommon) {
        return getCount(showCommon) == 0;
    }

    @Override
    public void writeProperty(final WriteCallback callback, final Syntax syntax, final ExplanationManager explMgr) {
        final OWLAxiom ax = name;
        if (inferred) {
            if (explanations == null) {
                new Thread() {
                    public void run() {
                        explMgr.setAxiom(ax);
                        explanations = explMgr.getNextExplanation();
                        callback.notify(writeExplanation(explanations, syntax));
                    }
                }.start();
            } else {
                callback.notify(writeExplanation(explanations, syntax));
            }
        } else {
            callback.notify(OWLDiffConfiguration.getCoreTranslations().getString("core.axiomnodemodel.connection-other-ontology"));
        }
    }

    private String writeExplanation(final Set<OWLAxiom> axioms, Syntax syntax) {
        final StringBuffer b = new StringBuffer();
        b.append("<html><i>");
        b.append(OWLDiffConfiguration.getCoreTranslations().getString("core.axiomnodemodel.inferred"));
        b.append("</i><br/>");
        b.append("<ul>");

        for (final OWLAxiom a : axioms) {
            b.append("<li>");
            b.append(syntax.writeAxiom(a, false, null, true));
            b.append("</li>");
        }

        b.append("</ul></html>");
        return b.toString();
    }

    public void setInferred(boolean inferred) {
        this.inferred = inferred;
    }

    public boolean isInferred() {
        return inferred;
    }

    public boolean isCommon() {
        return common;
    }

    public void accept(NodeModelVisitor v) {
        v.visit(this);
    }
}
