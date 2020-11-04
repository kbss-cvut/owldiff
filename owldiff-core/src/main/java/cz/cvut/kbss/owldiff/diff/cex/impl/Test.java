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
package cz.cvut.kbss.owldiff.diff.cex.impl;

import java.net.URI;

import javax.swing.JFrame;
import javax.swing.JTextArea;

public class Test {

    public static final String NL = "\n";

    public static void main(String[] args) {

        // String[] files = {"G:/java/swutils/examples/NoimplyExample.owl",
        // "G:/java/swutils/examples/NoimplyExample.owl"};
        // String[] files = {"G:/java/swutils/examples/CEXexample.owl",
        // "G:/java/swutils/examples/CEXexample2.owl"};
        // String[] files = {"examples/e1_1.owl", "examples/e1_2.owl"};
        // String[] files = { "examples/univ-bench.owl",
        // "examples/univ-bench_2.owl" };
        String[] files = {"examples/simple-cex/simple-cex-original.owl",
                "examples/simple-cex/simple-cex-update.owl"};
        // cz.cvut.sw.utils.diff.DiffFrame.main(files);
        Diff diff = new Diff(null);
        // Result result = diff.diff(files[0], files[1]);
        try {
            URI currentDir = URI.create("file:"
                    + System.getProperty("user.dir") + "/");

            URI oldF = currentDir.resolve(URI.create(files[0]).normalize());
            URI newF = currentDir.resolve(URI.create(files[1]).normalize());

            diff.diff(oldF, newF);
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

        final JFrame frame = new JFrame("CEX");
        final JTextArea ta = new JTextArea();
        final StringBuffer sb;

        frame.getContentPane().add(ta);
        frame.setSize(600, 400);
        frame.setLocation(300, 200);
        sb = new StringBuffer();

        sb.append("DiffR:" + NL);
        sb.append(Diff.classesToString(diff.getDiffR()));
        sb.append(NL + "DiffL:" + NL);
        sb.append(Diff.classesToString(diff.getDiffL()));

        ta.setText(sb.toString());
        frame.setVisible(true);
    }

}
