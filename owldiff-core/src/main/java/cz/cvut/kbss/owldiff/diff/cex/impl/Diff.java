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

import java.io.File;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import cz.cvut.kbss.owldiff.OWLDiffException;
import cz.cvut.kbss.owldiff.syntax.DLSyntax;
import cz.cvut.kbss.owldiff.view.ProgressListener;
import cz.cvut.kbss.owldiff.view.SigmaFrame;

public class Diff {

    private static final Logger LOG = Logger.getLogger(Diff.class.getName());

    private static final int PROGRESS_MAX = 5;

    private OWLOntologyManager managerO, managerU;

    //private OWLReasonerFactory f;

    private OWLDataFactory factoryO, factoryU;

    private Set<OWLClass> diffL, diffR;

    private ProgressListener progressListener;

    public Diff(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    public void diff(final URI o, final URI u) throws OWLDiffException {
        diff(o, u, false);
    }

    /**
     * Convenience method when parsing from scratch.
     */
    public void diff(final URI o, final URI u, boolean askForSigma) throws OWLDiffException {

        try {
            OWLOntologyManager originalM = org.semanticweb.owlapi.apibinding.OWLManager.createOWLOntologyManager();
            OWLOntology original = originalM.loadOntology(IRI.create(new File(o).toURI()));

            OWLOntologyManager updateM = org.semanticweb.owlapi.apibinding.OWLManager.createOWLOntologyManager();
            OWLOntology update = updateM.loadOntology(IRI.create(new File(u).toURI()));

            if (LOG.isLoggable(Level.CONFIG)) {
                LOG.config("Original: " + axiomsToString(original.getAxioms()));
            }

            if (LOG.isLoggable(Level.CONFIG)) {
                LOG.config("Update: " + axiomsToString(update.getAxioms()));
            }

            diff(original, update, askForSigma);
        } catch (OWLOntologyCreationException e1) {
            LOG.log(Level.SEVERE, e1.getMessage(), e1);
        }

    }

    public void diff(OWLOntology original, OWLOntology update) throws OWLDiffException {
        diff(original, update, false);
    }

    public void diff(OWLOntology original, OWLOntology update, boolean askForSigma) throws OWLDiffException {

        if (progressListener != null) {
            progressListener.setProgress(0);
            progressListener.setProgressMax(PROGRESS_MAX);
        }

        // NORMALIZATION
        Normalizator origNorm = new Normalizator(original, progressListener);
        original = origNorm.normalizeTerminology();
        if (LOG.isLoggable(Level.CONFIG)) {
            LOG.config("Original normalized: " + axiomsToString(original.getAxioms()));
        }

        Normalizator updateNorm = new Normalizator(update, progressListener);
        update = updateNorm.normalizeTerminology();
        if (LOG.isLoggable(Level.CONFIG)) {
            LOG.config("Update normalized: " + axiomsToString(update.getAxioms()));
        }

        if ((original == null) || (update == null)) {
            LOG.severe("Failed to normalize an input ontology");
            return;
        }

        new AcyclicChecker(original).check();
        new AcyclicChecker(update).check();

        // CEX DIFF
        Sig origSig = new Sig(original);
        Sig updateSig = new Sig(update);
        Sig sigma = createFullSigma(origSig, updateSig);

        if (LOG.isLoggable(Level.CONFIG)) {
            LOG.config("Full sigma: " + sigma.toString());
        }
        if (askForSigma) {
            SigmaFrame sf = new SigmaFrame(sigma);
            sf.setVisible(true);
        }
        if (LOG.isLoggable(Level.INFO)) {
            LOG.info("Selected sigma: " + sigma.toString());
        }

        managerO = OWLManager.createOWLOntologyManager();
        factoryO = managerO.getOWLDataFactory();
        Noimply nio = new Noimply(managerO, original, origSig, factoryO, sigma, false);
        nio.nnoImply();
        managerU = OWLManager.createOWLOntologyManager();
        factoryU = managerU.getOWLDataFactory();
        Noimply niu = new Noimply(managerU, update, updateSig, factoryU, sigma, true);
        niu.nnoImply();

        if (progressListener != null) {
            progressListener.setProgress(1);
        }

        if (LOG.isLoggable(Level.CONFIG)) {
            for (OWLClass a : origSig.getSig()) {
                String name = a.getIRI().getFragment();
                Set<OWLClass> n = nio.noImply(a);
                Set<OWLAxiom> nn = nio.nnoImply(a);
                Set<OWLClass> pre = nio.pre(a);
                LOG.config("orig: " + name + ": noImply: " + classesToString(n));
                LOG.config("orig: " + name + ": NoImply: " + axiomsToString(nn));
                LOG.config("orig: " + name + ": pre: " + classesToString(pre));
            }
            LOG.config("Xi orig: " + classesToString(nio.getXi()));
            LOG.info("NoImply original: " + axiomsToString(nio.nnoImply()));

            for (OWLClass a : updateSig.getSig()) {
                String name = a.getIRI().getFragment();
                Set<OWLClass> n = niu.noImply(a);
                Set<OWLAxiom> nn = niu.nnoImply(a);
                Set<OWLClass> pre = niu.pre(a);
                LOG.config("update: " + name + ": noImply: " + classesToString(n));
                LOG.config("update: " + name + ": NoImply: " + axiomsToString(nn));
                LOG.config("update: " + name + ": pre: " + classesToString(pre));
            }
            LOG.config("Xi update: " + classesToString(niu.getXi()));
            LOG.info("NoImply update: " + axiomsToString(niu.nnoImply()));
        }

        // Result result = new Result();

        DiffR dR = new DiffR(original, update, origSig, updateSig, sigma, nio, niu, managerU);
        dR.mark();
        if (progressListener != null) {
            progressListener.setProgress(2);
        }
        // result.diffR = dR.diffR();
        diffR = dR.diffR();
        if (progressListener != null) {
            progressListener.setProgress(3);
        }
        if (LOG.isLoggable(Level.INFO)) {
            LOG.info("DiffR: " + classesToString(diffR));
        }

        DiffL dL = new DiffL(original, update, origSig, updateSig, sigma, nio, niu);
        dL.generateOs();
        if (progressListener != null) {
            progressListener.setProgress(4);
        }
        if (LOG.isLoggable(Level.CONFIG)) {
            StringBuffer sb = new StringBuffer();
            sb.append("O: ");
            for (DiffL.OWLClassPair p : dL.getO()) {
                sb.append(p + ",");
            }
            LOG.config(sb.toString());
        }
        // result.diffL = dL.diffL();
        diffL = dL.diffL();
        if (progressListener != null) {
            progressListener.setProgress(5);
        }
        if (LOG.isLoggable(Level.INFO)) {
            LOG.info("DiffL: " + classesToString(diffL));
        }

        // final ExplanationManager<OWLAxiom> origEM = getExplanationManager(original);
        // Reasoner origR = new Reasoner(originalM);
        // origR.loadOntology(original);

        // return result;
    }

    /*
      * public class Result { public Set<OWLClass> diffL; public Set<OWLClass>
      * diffR; }
      */

    private Sig createFullSigma(Sig origSig, Sig updateSig) {
        Set<OWLClass> sigo = origSig.getSig();
        Set<OWLClass> sigu = updateSig.getSig();
        Set<OWLClass> sigmaSet = new HashSet<OWLClass>();
        for (OWLClass c : sigo) {
            if (sigu.remove(c)) {
                sigmaSet.add(c);
            }
        }

        Set<OWLObjectProperty> roleso = origSig.getRoles();
        Set<OWLObjectProperty> rolesu = updateSig.getRoles();
        Set<OWLObjectProperty> roleSet = new HashSet<OWLObjectProperty>();
        for (OWLObjectProperty p : roleso) {
            if (rolesu.remove(p)) {
                roleSet.add(p);
            }
        }

        return new Sig(sigmaSet, roleSet);
    }

    public Set<OWLClass> getDiffL() {
        return diffL;
    }

    public Set<OWLClass> getDiffR() {
        return diffR;
    }

    static String classesToString(Set<OWLClass> set) {
        StringBuffer sb = new StringBuffer();
        if (set == null) {
            return sb.toString();
        }
        for (OWLClass c : set) {
            sb.append(c.getIRI().getFragment() + ",");
        }
        return sb.toString();
    }

    private static String axiomsToString(Set<OWLAxiom> set) {
        StringBuffer sb = new StringBuffer();
        if (set == null) {
            return sb.toString();
        }

        for (final OWLAxiom a : set) {
            sb.append(new DLSyntax().writeAxiom(a, false, null, false));
            sb.append(", ");
        }

        return sb.toString();
    }
}
