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

import cz.cvut.kbss.owldiff.syntax.Syntax;
import cz.cvut.kbss.owldiff.view.DiffVisualization;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: marek
 * Date: 10/3/12
 * Time: 10:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class OntologyNodeModel extends NodeModel<OWLOntology> {

    final List<OWLAnnotation> commonAnnotations = new ArrayList<OWLAnnotation>();
    final List<OWLAnnotation> differentAnnotations = new ArrayList<OWLAnnotation>();
    final List<OWLImportsDeclaration> commonImportsDeclaration = new ArrayList<OWLImportsDeclaration>();
    final List<OWLImportsDeclaration> differentImportsDeclaration = new ArrayList<OWLImportsDeclaration>();

    public OntologyNodeModel(OWLOntology name, NodeModel<?> parent) {
        super(name, parent);
    }

    public void addAnnotation(OWLAnnotation a,
                              Collection<? extends OWLAnnotation> diff) {
        if (diff.contains(a)) {
            this.differentAnnotations
                    .add(-Collections.binarySearch(
                            this.differentAnnotations, a) - 1, a);
        } else {
            this.commonAnnotations
                    .add(-Collections.binarySearch(this.commonAnnotations,
                            a) - 1, a);
        }
    }

    public void addImportsDeclaration(OWLImportsDeclaration a,
                                      Collection<? extends OWLImportsDeclaration> diff) {
        if (diff.contains(a)) {
            this.differentImportsDeclaration.add(-Collections.binarySearch(
                    this.differentImportsDeclaration, a) - 1, a);
        } else {
            this.commonImportsDeclaration.add(-Collections.binarySearch(
                    this.commonImportsDeclaration, a) - 1, a);
        }
    }

    public void accept(NodeModelVisitor v) {
        v.visit(this);
    }

}
