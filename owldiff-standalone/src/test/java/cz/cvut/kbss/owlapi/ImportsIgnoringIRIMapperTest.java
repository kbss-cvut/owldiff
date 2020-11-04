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

package cz.cvut.kbss.owlapi;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;

public class ImportsIgnoringIRIMapperTest {

    ImportsIgnoringIRIMapper mapper;

    @Before
    public void setUp() throws Exception {
        mapper = new ImportsIgnoringIRIMapper("http://test");
    }

    @Test
    public void testGetDocumentIRICorrect() throws Exception {
        if (mapper.getDocumentIRI(IRI.create("http://x")).getStart().startsWith("http://")) {
            Assert.fail();
        }
    }

    @Test
    public void testGetDocumentIRIWrong() throws Exception {
        if (mapper.getDocumentIRI(IRI.create("file://x")).getStart().startsWith("http://")) {
            Assert.fail();
        }
    }

}
