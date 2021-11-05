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

import java.io.IOException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.FreshEntityPolicy;
import org.semanticweb.owlapi.reasoner.IndividualNodeSetPolicy;
import org.semanticweb.owlapi.reasoner.NullReasonerProgressMonitor;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

public class OWLDiffConfiguration {
    private static final Logger LOG = Logger.getLogger(OWLDiffConfiguration.class
        .getName());

    private static String VERSION;

    private static String REASONER_FACTORY;

    private static ReasonerProvider reasonerProvider;

    private static ResourceBundle resourceBundle;

    static {
        try {
            final Properties p = new Properties();
            p.load(OWLDiffConfiguration.class.getResource("/owldiff.properties").openStream());
            VERSION = p.getProperty("version");
            REASONER_FACTORY = p.getProperty("reasonerFactory", StructuralReasonerFactory.class.getName());
            resourceBundle = ResourceBundle.getBundle("core-translation");
            initReasonerFactory();
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error during loading properties", e);
        }
    }

    private static void initReasonerFactory() {
        try {
            final Class<?> c = Class.forName(REASONER_FACTORY);
            setReasonerProvider(new ReasonerProvider() {
                final OWLReasonerFactory f =
                    (OWLReasonerFactory) c.getDeclaredConstructor().newInstance();
                public OWLReasoner getOWLReasoner(OWLOntology o) {
                    return f.createReasoner(o, new SimpleConfiguration(
                        new NullReasonerProgressMonitor(), FreshEntityPolicy.ALLOW, -1,
                        IndividualNodeSetPolicy.BY_SAME_AS));
                }
            });
            LOG.info("Reasoner factory successfully set to " + REASONER_FACTORY);
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Reasoner factory not found.", e);
        }
    }


    public interface ReasonerProvider {
        OWLReasoner getOWLReasoner(final OWLOntology o);
    }

    public static void setReasonerProvider(final ReasonerProvider rp) {
        reasonerProvider = rp;
    }

    public static boolean isReasonerAvailable() {
        return reasonerProvider != null;
    }

    public static OWLReasoner getOWLReasoner(final OWLOntology o) {
        return reasonerProvider.getOWLReasoner(o);
    }

    public static String getVersion() {
        return VERSION;
    }

    public static ResourceBundle getCoreTranslations() {
        return resourceBundle;
    }
}