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

package cz.cvut.kbss.owldiff.diff;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.*;

import java.io.IOException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OWLDiffConfiguration {
    private static final Logger LOG = Logger.getLogger(OWLDiffConfiguration.class
            .getName());

    private static String VERSION;

    private static ReasonerProvider p;

    private static ResourceBundle resourceBundle;

    static {

        String reasonerFactory = System.getProperty("reasonerFactory");
        if (reasonerFactory != null) {
            try {
                final Class<?> c = Class.forName(reasonerFactory);
                setReasonerProvider(new ReasonerProvider() {
                    final OWLReasonerFactory f = (OWLReasonerFactory) c.newInstance();

                    public OWLReasoner getOWLReasoner(OWLOntology o) {
                        return f.createReasoner(o, new SimpleConfiguration(
                                new NullReasonerProgressMonitor(), FreshEntityPolicy.ALLOW, -1,
                                IndividualNodeSetPolicy.BY_SAME_AS));
                    }
                });
                LOG.info("Reasoner factory successfully set");
            } catch (Exception e) {
                LOG.log(Level.WARNING, "Reasoner factory not found.");
//            p = new ReasonerProvider() {
//                public OWLReasoner getOWLReasoner(OWLOntology o) {
//                    return new StructuralReasoner(o, new SimpleConfiguration(), BufferingMode.BUFFERING);
//                }
//            };
            }
        } else {
            LOG.log(Level.WARNING, "Reasoner factory not found.");
        }
        try {
            Properties p = new Properties();
            p.load(OWLDiffConfiguration.class.getResource("/owldiff.properties").openStream());
            VERSION = p.getProperty("version");
            resourceBundle = ResourceBundle.getBundle("core-translation");
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error during loading properties", e);
        }
    }

    public interface ReasonerProvider {
        OWLReasoner getOWLReasoner(final OWLOntology o);
    }

    public static void setReasonerProvider(final ReasonerProvider rp) {
        p = rp;
    }

    public static boolean isReasonerAvailable() {
        return p != null;
    }

    public static OWLReasoner getOWLReasoner(final OWLOntology o) {
        return p.getOWLReasoner(o);
    }

    public static String getVersion() {
        return VERSION;
    }

    public static ResourceBundle getCoreTranslations() {
        return resourceBundle;
    }
}