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
import org.semanticweb.owlapi.model.OWLClass;

/**
 * Created with IntelliJ IDEA.
 * User: marek
 * Date: 10/3/12
 * Time: 4:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClassNodeModel extends NodeModel<OWLClass> {
    private boolean inDiffR;
    private boolean inDiffL;

    public ClassNodeModel(OWLClass name, NodeModel<?> parent, boolean inDiffL, boolean inDiffR) {
        super(name, parent);
        setCEX(inDiffL, inDiffR);
    }

    public void setCEX(boolean inDiffL, boolean inDiffR) {
        this.inDiffR = inDiffR;
        this.inDiffL = inDiffL;
    }

    public void accept(NodeModelVisitor v) {
        v.visit(this);
    }

    public void writeProperty(final WriteCallback callback, final Syntax syntax, final ExplanationManager explMgr) {
        if (inDiffR && inDiffL) {
            callback.notify(OWLDiffConfiguration.getCoreTranslations().getString("core.nodemodel.cex-both"));
        } else if (inDiffR) {
            callback.notify(OWLDiffConfiguration.getCoreTranslations().getString("core.nodemodel.cex-extra"));
        } else if (inDiffL) {
            callback.notify(OWLDiffConfiguration.getCoreTranslations().getString("core.nodemodel.cex-lack"));
        } else {
            callback.notify(null);
        }
    }

    public boolean isInBothLandR() {
        return inDiffR && inDiffL;
    }

    public boolean isJustInL() {
        return inDiffL;
    }

    public boolean isJustR() {
        return inDiffR;
    }

}
