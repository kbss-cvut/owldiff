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
import org.semanticweb.owlapi.model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: marek
 * Date: 10/3/12
 * Time: 10:30 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class NodeModel<T extends Comparable> implements Comparable<NodeModel<?>> {

    private static final Logger LOG = Logger.getLogger(NodeModel.class.getName());

    protected final T name;
    private final NodeModel<?> parent;

    // Nested Frames
    private final List<NodeModel<?>> differentNodeModels;
    private final List<NodeModel<?>> commonNodeModels;

    private final List<AxiomNodeModel> differentAxioms;
    private final List<AxiomNodeModel> commonAxioms;

    public NodeModel(T name, NodeModel<?> parent) {
        this.name = name;

        this.parent = parent;

        this.commonNodeModels = new ArrayList<NodeModel<?>>();
        this.differentNodeModels = new ArrayList<NodeModel<?>>();

        this.commonAxioms = new ArrayList<AxiomNodeModel>();
        this.differentAxioms = new ArrayList<AxiomNodeModel>();
    }

    public void addNodeModel(NodeModel<?> n) {
        if (n.containsDifferent() || n.containsInferred()) {
            this.differentNodeModels.add(n);
            if (parent != null) {
                parent.putToDifferentInParent(this);
            }
        } else {
            this.commonNodeModels
                    .add(-Collections
                            .binarySearch(this.commonNodeModels, n) - 1, n);
        }
    }

    // Due to hierarchy, it is necessary to check all parents to see if they
    // are
    // correctly marked as containing different axioms
    private void putToDifferentInParent(NodeModel<?> m) {
        if (commonNodeModels.contains(m)) {
            commonNodeModels.remove(m);
            this.differentNodeModels
                    .add(-Collections.binarySearch(
                            this.differentNodeModels, m) - 1, m);
        }
        if (parent != null) {
            parent.putToDifferentInParent(this);
        }
    }

    public void addAxiom(Collection<? extends OWLAxiom> c,
                         Collection<? extends OWLAxiom> diff, Collection<OWLAxiom> inferred) {
        for (OWLAxiom a : c) {
            addAxiom(a, diff, inferred);
        }
    }

    public void addAxiom(OWLAxiom c, Collection<? extends OWLAxiom> diff, Collection<OWLAxiom> inferred) {
        if (diff.contains(c)) {
            AxiomNodeModel n = new AxiomNodeModel(c, this, inferred.contains(c), false);
            int idx = getIndexToAddToSorted(n, this.differentAxioms);
            if (idx >= 0) {
                this.differentAxioms.add(idx, n);
            }
        } else {
            AxiomNodeModel n = new AxiomNodeModel(c, this, false, true);
            int idx = getIndexToAddToSorted(n, this.commonAxioms);
            if (idx >= 0) {
                this.commonAxioms.add(idx, n);
            }
        }
    }

    private int getIndexToAddToSorted(AxiomNodeModel c, List<AxiomNodeModel> list) {
        int idx = Collections.binarySearch(list, c);
        if (idx >= 0) { // a similar axiom (probably not equal), e.g. has different annotations
            if (list.contains(c)) {
                LOG.log(Level.WARNING, OWLDiffConfiguration.getCoreTranslations().getString("core.nodemodel.adding-existing-axiom"), new Object[]{c, list});
                return -1;
            }
            return idx; // let's put it before the similar axiom
        }
        return -idx - 1;
    }

    public Object getObject() {
        return this.name;
    }

    public T getData() {
        return name;
    }

    public int getCount(boolean showCommon) {
        if (showCommon) {
            return this.commonAxioms.size() + this.differentAxioms.size()
                    + this.commonNodeModels.size()
                    + this.differentNodeModels.size();
        } else {
            return this.differentAxioms.size()
                    + this.differentNodeModels.size();
        }
    }

    public int getIndexOf(Object o, boolean showCommon) {
        int index = -1;

        if (differentAxioms.contains(o)) {
            return differentAxioms.indexOf(o);
        }

        index += differentAxioms.size();
        if (showCommon) {
            if (commonAxioms.contains(o)) {
                return index + commonAxioms.indexOf(o);
            }
            index += commonAxioms.size();
        }

        if (differentNodeModels.contains(o)) {
            return index + differentNodeModels.indexOf(o);
        }
        index += differentNodeModels.size();

        return index + commonNodeModels.indexOf(o);
    }

    public boolean containsInferred() {
        //if (!Collections.disjoint(inferred, differentAxioms)) {
        for (AxiomNodeModel anm : differentAxioms) {
            if (anm.isInferred()) {
                return true;
            }
        }

        if (!differentNodeModels.isEmpty()) {
            for (NodeModel<?> node : differentNodeModels) {
                if (node.containsInferred()) {
                    return true;
                }
            }
        }
        if (!commonNodeModels.isEmpty()) {

            for (NodeModel<?> node : commonNodeModels) {
                if (node.containsInferred()) {
                    return true;
                }
            }

        }
        return false;
    }

    public boolean containsDifferent() {
        //if (!Collections.disjoint(rest, differentAxioms)) {
        for (AxiomNodeModel anm : differentAxioms) {
            if (!anm.isCommon()) {
                return true;
            }
        }
        if (!differentNodeModels.isEmpty()) {
            for (NodeModel<?> node : differentNodeModels) {
                if (node.containsDifferent()) {
                    return true;
                }
            }
        }
        if (!commonNodeModels.isEmpty()) {
            for (NodeModel<?> node : commonNodeModels) {
                if (node.containsDifferent()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean containsAxiom(OWLAxiom ax) {
        /*if (this.commonAxioms.contains(ax) || this.differentAxioms.contains(ax)) {
            return true;
        }*/
        for (AxiomNodeModel anm : commonAxioms) {
            if (anm.getObject().equals(ax)) {
                return true;
            }
        }
        for (AxiomNodeModel anm : differentAxioms) {
            if (anm.getObject().equals(ax)) {
                return true;
            }
        }

        if (!commonNodeModels.isEmpty()) {
            for (NodeModel<?> node : commonNodeModels) {
                if (node.containsAxiom(ax)) {
                    return true;
                }
            }
        }
        if (!differentNodeModels.isEmpty()) {
            for (NodeModel<?> node : differentNodeModels) {
                if (node.containsAxiom(ax)) {
                    return true;
                }
            }
        }
        return false;
    }

    public NodeModel<?> getChild(int index, boolean showCommon) {
        if (index < differentAxioms.size()) {
            return differentAxioms.get(index);
        }
        index = index - differentAxioms.size();

        if (showCommon) {
            if (index < commonAxioms.size()) {
                return commonAxioms.get(index);
            }
            index = index - commonAxioms.size();
        }

        if (index < this.differentNodeModels.size()) {
            return differentNodeModels.get(index);
        }
        index = index - differentNodeModels.size();

        // only if showCommon is set
        return commonNodeModels.get(index);
    }

    public int compareTo(NodeModel<?> o) {
        return name.compareTo(o.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        NodeModel<?> other = (NodeModel<?>) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    public String toString() {
        return name.toString();
    }

    public boolean isLeaf(boolean showCommon) {
        return false;
    }

    public abstract void accept(NodeModelVisitor v);

    public void writeProperty(WriteCallback listener, final Syntax syntax, final ExplanationManager explMgr) {
        listener.notify(null);
    }

    public interface WriteCallback {
        void notify(String returnValue);
    }
}

