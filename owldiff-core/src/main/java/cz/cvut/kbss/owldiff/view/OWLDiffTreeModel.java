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

import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import cz.cvut.kbss.owldiff.view.nodes.*;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividualAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import cz.cvut.kbss.owldiff.diff.OWLDiffConfiguration;

public class OWLDiffTreeModel implements TreeModel {

    private static final Logger LOG = Logger.getLogger(OWLDiffTreeModel.class.getName());

    private final List<TreeModelListener> listenerList = new ArrayList<TreeModelListener>();

    private final Collection<OWLAxiom> inferred;
    private final Collection<OWLAxiom> rest;

    private final Collection<OWLClass> diffR;
    private final Collection<OWLClass> diffL;

    private final OWLOntology ontology;
    private OWLReasoner reasoner;

    private OntologyNodeModel rootNode;

    private boolean showCommon;

    public OWLDiffTreeModel(final DiffVisualization view,
                            final Collection<OWLAxiom> incomparable, final OWLOntology ontology) {
        this.ontology = ontology;
        this.inferred = new HashSet<OWLAxiom>();
        this.rest = new HashSet<OWLAxiom>(incomparable);
        this.diffL = new HashSet<OWLClass>();
        this.diffR = new HashSet<OWLClass>();
        this.setView(view);
    }

    public void setCEXDiff(final Set<OWLClass> diffL, final Set<OWLClass> diffR) {
        this.diffR.clear();
        this.diffL.clear();

        this.diffL.addAll(diffL);
        this.diffR.addAll(diffR);
        updateCEX(rootNode);
        fireTreeNodesChanged();
    }

    private void updateInferredStatus(NodeModel current) {
        if (current instanceof AxiomNodeModel) {
            AxiomNodeModel anm = ((AxiomNodeModel) current);
            anm.setInferred(inferred.contains(anm.getData()));
        }

        for (int i = 0; i < current.getCount(true); i++) {
            updateInferredStatus(current.getChild(i, true));
        }
    }

    public void setInferred(final Collection<OWLAxiom> inferred) {
        this.inferred.clear();
        this.inferred.addAll(inferred);
        updateInferredStatus(rootNode);
        fireTreeNodesChanged();
    }

    private void updateCEX(NodeModel current) {
        if (current instanceof ClassNodeModel) {
            ClassNodeModel anm = ((ClassNodeModel) current);
            anm.setCEX(diffL.contains(anm.getData()), diffR.contains(anm.getData()));
        }

        for (int i = 0; i < current.getCount(true); i++) {
            updateCEX(current.getChild(i, true));
        }
    }

    private void addAnnotationsAndDeclarations(final NodeModel<?> m,
                                               final OWLEntity e) {
        for (final OWLAnnotationAssertionAxiom ann : ontology
                .getAnnotationAssertionAxioms(e.getIRI())) {
            m.addAxiom(ann, this.rest, this.inferred);
        }
        for (final OWLDeclarationAxiom ann : ontology.getDeclarationAxioms(e)) {
            m.addAxiom(ann, this.rest, this.inferred);
        }
    }

    protected void fireTreeStructureChanged() {
        final TreeModelEvent e = new TreeModelEvent(this,
                new Object[]{rootNode});
        for (final TreeModelListener l : listenerList) {
            l.treeStructureChanged(e);
        }
    }

    protected void fireTreeNodesChanged() {
        final TreeModelEvent e = new TreeModelEvent(this,
                new Object[]{rootNode});
        for (final TreeModelListener l : listenerList) {
            l.treeNodesChanged(e);
        }
    }

    private void insertAllSubNodes(NodeModel<?> parent, OWLClass parentClass,
                                   OWLReasoner reasoner, OWLOntology ontology, boolean direct) {
        for (Node<OWLClass> nodeClass : reasoner.getSubClasses(parentClass, direct)) {
            if (!nodeClass.contains(OWLManager.getOWLDataFactory().getOWLNothing())) {
                for (OWLClass newClass : nodeClass) {
                    final ClassNodeModel newClassNodeModel = new ClassNodeModel(newClass, parent, diffL.contains(newClass), diffR.contains(newClass));
                    newClassNodeModel.addAxiom(ontology.getAxioms(newClass), this.rest, this.inferred);
                    addAnnotationsAndDeclarations(newClassNodeModel, newClass);
                    insertAllSubNodes(newClassNodeModel, newClass, reasoner, ontology, direct);
                    parent.addNodeModel(newClassNodeModel);
                }
            }
        }
    }

    private void insertAllSubNodes(NodeModel<?> parent,
                                   OWLObjectPropertyExpression ax, OWLReasoner h, OWLOntology ontology, boolean direct) {
        for (OWLObjectPropertyExpression oc : h.getSubObjectProperties(ax, direct).getFlattened()) {
            PropertyNodeModel m2 = new PropertyNodeModel(oc, parent);
            m2.addAxiom(ontology.getAxioms(oc), this.rest, this.inferred);
            if (oc.isAnonymous()) {
                LOG.config(String.format(OWLDiffConfiguration.getCoreTranslations().getString("core.msg.annotations-declarations-for-object-property-expressions-ommited.omitted"), oc));
                continue;
            } else {
                addAnnotationsAndDeclarations(m2, oc.asOWLObjectProperty());
            }
            insertAllSubNodes(m2, oc, h, ontology, direct);
            parent.addNodeModel(m2);
        }
    }

    private void insertAllSubNodes(NodeModel<?> parent, OWLDataProperty ax,
                                   OWLReasoner h, OWLOntology ontology, boolean direct) {
        for (OWLDataProperty oc : h.getSubDataProperties(ax, direct)
                .getFlattened()) {
            final PropertyNodeModel m2 = new PropertyNodeModel(oc, parent);
            m2.addAxiom(ontology.getAxioms(oc), this.rest, this.inferred);
            addAnnotationsAndDeclarations(m2, oc);
            insertAllSubNodes(m2, oc, h, ontology, direct);
            parent.addNodeModel(m2);
        }
    }

    // private Collection<OWLDataProperty> getDataTops(final OWLReasoner r,
    // final OWLOntology o) {
    // final Set<OWLDataProperty> dps = new HashSet<OWLDataProperty>();
    // for (final OWLDataProperty dp : o.getDataPropertiesInSignature()) {
    // if (r.getSuperDataProperties(dp, false).isEmpty()) {
    // dps.add(dp);
    // }
    // }
    //
    // return dps;
    // }

    // private Collection<OWLObjectProperty> getObjectTops(final OWLReasoner r,
    // final OWLOntology o) {
    // final Set<OWLObjectProperty> dps = new HashSet<OWLObjectProperty>();
    // for (final OWLObjectProperty dp : o.getObjectPropertiesInSignature()) {
    // if (r.getSuperObjectProperties(dp, false).isEmpty()) {
    // dps.add(dp);
    // }
    // }
    //
    // return dps;
    // }

    public void setView(final DiffVisualization view) {
        this.rootNode = new OntologyNodeModel(ontology, null);

        final NodeModel<String> nodeIndividual;
        final NodeModel<String> nodeDataProperty;
        final NodeModel<String> nodeObjectProperty;
        final NodeModel<String> nodeClass;

        if (DiffVisualization.LIST_VIEW.equals(view)) {
            nodeClass = new CategoryNodeModel(OWLDiffConfiguration.getCoreTranslations().getString("core.tree.tbox-axioms"), rootNode);
            nodeIndividual = new CategoryNodeModel(OWLDiffConfiguration.getCoreTranslations().getString("core.tree.abox-axioms"), rootNode);
            nodeDataProperty = new CategoryNodeModel(OWLDiffConfiguration.getCoreTranslations().getString("core.tree.rbox-axioms"), rootNode);
            final NodeModel<String> other = new CategoryNodeModel(OWLDiffConfiguration.getCoreTranslations().getString("core.tree.other-axioms"), rootNode);

            for (OWLAxiom o : ontology.getAxioms()) {
                if ((o instanceof OWLClassAxiom)) {
                    nodeClass.addAxiom(o, this.rest, this.inferred);
                } else if (o instanceof OWLDataPropertyAxiom) {
                    nodeDataProperty.addAxiom(o, this.rest, this.inferred);
                } else if (o instanceof OWLObjectPropertyAxiom) {
                    nodeDataProperty.addAxiom(o, this.rest, this.inferred);
                } else if (o instanceof OWLIndividualAxiom) {
                    nodeIndividual.addAxiom(o, this.rest, this.inferred);
                } else
                    other.addAxiom(o, this.rest, this.inferred);
            }

            rootNode.addNodeModel(nodeClass);
            rootNode.addNodeModel(nodeIndividual);
            rootNode.addNodeModel(nodeDataProperty);
            rootNode.addNodeModel(other);
        } else {
            nodeClass = new CategoryNodeModel(OWLDiffConfiguration.getCoreTranslations().getString("core.tree.classes"), rootNode);
            nodeIndividual = new CategoryNodeModel(OWLDiffConfiguration.getCoreTranslations().getString("core.tree.individuals"), rootNode);
            nodeDataProperty = new CategoryNodeModel(OWLDiffConfiguration.getCoreTranslations().getString("core.tree.data-properties"), rootNode);
            nodeObjectProperty = new CategoryNodeModel(OWLDiffConfiguration.getCoreTranslations().getString("core.tree.object-properties"), rootNode);

            switch (view) {
                case SIMPLE_FRAME_VIEW:
                    for (OWLClass ax : ontology.getClassesInSignature()) {
                        final NodeModel<OWLClass> m = new ClassNodeModel(ax, nodeClass, diffL.contains(ax), diffR.contains(ax));
                        m.addAxiom(ontology.getAxioms(ax), this.rest, this.inferred);
                        addAnnotationsAndDeclarations(m, ax);
                        nodeClass.addNodeModel(m);
                    }

                    for (OWLDataProperty ax : ontology.getDataPropertiesInSignature()) {
                        final NodeModel m = new PropertyNodeModel(ax, nodeDataProperty);
                        m.addAxiom(ontology.getAxioms(ax), this.rest, this.inferred);
                        addAnnotationsAndDeclarations(m, ax);
                        nodeDataProperty.addNodeModel(m);
                    }

                    for (OWLObjectProperty ax : ontology.getObjectPropertiesInSignature()) {
                        final NodeModel m = new PropertyNodeModel(ax, nodeObjectProperty);
                        m.addAxiom(ontology.getAxioms(ax), this.rest, this.inferred);
                        addAnnotationsAndDeclarations(m, ax);
                        nodeObjectProperty.addNodeModel(m);
                    }

                    break;
                case CLASSIFIED_FRAME_VIEW:
                    reasoner = OWLDiffConfiguration.getOWLReasoner(ontology);
                    LOG.info(MessageFormat.format(OWLDiffConfiguration.getCoreTranslations().getString("core.msg.using-reasoner"), reasoner));

                    if (!reasoner.isConsistent()) {
                        LOG.info(OWLDiffConfiguration.getCoreTranslations().getString("core.msg.ontology-inconsistent"));
                        setView(DiffVisualization.SIMPLE_FRAME_VIEW);
                        return;
                    }

                    // TODO
                    // if (!reasoner.isClassified()) {
                    // long j = System.currentTimeMillis();
                    // reasoner.classify();
                    // if (LOG.isLoggable(Level.INFO)) {
                    // LOG.info("Building hierarchy took "
                    // + (System.currentTimeMillis() - j));
                    // }
                    // }

                    final OWLDataFactory f = OWLManager.getOWLDataFactory();

                    final Node<OWLClass> unsats = reasoner.getEquivalentClasses(f
                            .getOWLNothing());

                    if (unsats.getSize() > 0) {
                        final ClassNodeModel nm = new ClassNodeModel(f.getOWLNothing(), nodeClass, diffL.contains(f.getOWLNothing()), diffR.contains(f.getOWLNothing()));
                        nodeClass.addNodeModel(nm);

                        for (final OWLClass c : unsats) {
                            final ClassNodeModel nm2 = new ClassNodeModel(c, nodeClass, diffL.contains(c), diffR.contains(c));
                            nm2.addAxiom(ontology.getAxioms(c), this.rest, this.inferred);
                            addAnnotationsAndDeclarations(nm2, c);
                            nm.addNodeModel(nm2);
                        }
                    }

                    insertAllSubNodes(nodeClass, f.getOWLThing(), reasoner,
                            ontology, true);

                    insertAllSubNodes(nodeObjectProperty,
                            f.getOWLTopObjectProperty(), reasoner, ontology, true);

                    insertAllSubNodes(nodeDataProperty, f.getOWLTopDataProperty(),
                            reasoner, ontology, true);
            }

            for (final OWLNamedIndividual ax : ontology.getIndividualsInSignature()) {
                final IndividualNodeModel m = new IndividualNodeModel(ax, nodeIndividual);
                m.addAxiom(ontology.getAxioms(ax), rest, this.inferred);
                addAnnotationsAndDeclarations(m, ax);
                nodeIndividual.addNodeModel(m);
            }

            for (OWLAxiom ax : ontology.getAxioms()) {
                if (!nodeClass.containsAxiom(ax)
                        && !nodeDataProperty.containsAxiom(ax)
                        && !nodeObjectProperty.containsAxiom(ax)
                        && !nodeIndividual.containsAxiom(ax)) {
                    rootNode.addAxiom(ax, this.rest, this.inferred);
                }
            }

            rootNode.addNodeModel(nodeClass);
            rootNode.addNodeModel(nodeIndividual);
            rootNode.addNodeModel(nodeDataProperty);
            rootNode.addNodeModel(nodeObjectProperty);
        }

        fireTreeStructureChanged();
    }

    public void setShowCommon(boolean showCommon) {
        if (showCommon != this.showCommon) {
            this.showCommon = showCommon;
            fireTreeStructureChanged();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addTreeModelListener(TreeModelListener l) {
        if (l != null && !listenerList.contains(l)) {
            listenerList.add(l);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object getChild(Object parent, int index) {
        return ((NodeModel<?>) parent).getChild(index, showCommon);
    }

    /**
     * {@inheritDoc}
     */
    public int getChildCount(Object parent) {
        return ((NodeModel<?>) parent).getCount(showCommon);
    }

    /**
     * {@inheritDoc}
     */
    public int getIndexOfChild(Object parent, Object child) {
        return ((NodeModel<?>) parent).getIndexOf(child, showCommon);
    }

    /**
     * {@inheritDoc}
     */
    public Object getRoot() {
        return rootNode;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isLeaf(Object o) {
        return ((NodeModel) o).isLeaf(showCommon);
    }

    /**
     * {@inheritDoc}
     */
    public void removeTreeModelListener(TreeModelListener l) {
        if (l != null && listenerList.contains(l)) {
            listenerList.remove(l);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void valueForPathChanged(TreePath arg0, Object arg1) {
        if (LOG.isLoggable(Level.INFO)) {
            // TODO
            LOG.info(OWLDiffConfiguration.getCoreTranslations().getString("core.msg.treemodel-not-editable"));
        }
    }
}
