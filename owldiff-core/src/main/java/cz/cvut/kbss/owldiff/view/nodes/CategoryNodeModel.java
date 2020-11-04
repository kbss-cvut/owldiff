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

/**
 * Created with IntelliJ IDEA.
 * User: marek
 * Date: 10/3/12
 * Time: 4:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class CategoryNodeModel extends NodeModel<String> {

    public CategoryNodeModel(String name, NodeModel<?> parent) {
        super(name, parent);
    }

    public void accept(NodeModelVisitor v) {
        v.visit(this);
    }

}
