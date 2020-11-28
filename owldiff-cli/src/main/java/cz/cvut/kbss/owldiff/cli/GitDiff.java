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

package cz.cvut.kbss.owldiff.cli;

import com.github.rvesse.airline.SingleCommand;
import java.util.logging.LogManager;
import org.semanticweb.owlapi.io.OWLRendererException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class GitDiff {

    static {
        LogManager.getLogManager().reset();
    }

    public static void main(String[] args) throws OWLOntologyCreationException,
        OWLRendererException {
        SingleCommand<Diff> parser = SingleCommand.singleCommand(Diff.class);
        Diff cmd = parser.parse(args);
        cmd.run();
    }
}
