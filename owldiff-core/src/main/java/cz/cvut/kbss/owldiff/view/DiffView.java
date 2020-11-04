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

import cz.cvut.kbss.owldiff.ExplanationManager;
import cz.cvut.kbss.owldiff.OWLDiffException;
import cz.cvut.kbss.owldiff.diff.OWLDiffConfiguration;
import cz.cvut.kbss.owldiff.diff.cex.CEXDiff;
import cz.cvut.kbss.owldiff.diff.cex.CEXDiffOutput;
import cz.cvut.kbss.owldiff.diff.entailments.EntailmentsExplanationsDiff;
import cz.cvut.kbss.owldiff.diff.entailments.EntailmentsExplanationsDiffOutput;
import cz.cvut.kbss.owldiff.diff.owlapi.BBOWLAPIExplanationManager;
import cz.cvut.kbss.owldiff.diff.syntactic.SyntacticDiff;
import cz.cvut.kbss.owldiff.diff.syntactic.SyntacticDiffOutput;
import cz.cvut.kbss.owldiff.ontology.OntologyHandler;
import cz.cvut.kbss.owldiff.syntax.SyntaxEnum;
import org.semanticweb.owlapi.model.*;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.io.File;
import java.net.URI;
import java.util.Set;

public class DiffView extends JPanel {

    private static final long serialVersionUID = -1511915926159556871L;

    private OntologyHandler handler;

    private CEXDiffOutput cexDiffOutput;
    private SyntacticDiffOutput syntacticDiffOutput;
    private EntailmentsExplanationsDiffOutput entailmentsExplanationsDiffOutput;

    // VIEW
    private OntologyView originalView;
    private OntologyView updateView;

    // internal visualization state
    protected DiffVisualization visualization = DiffVisualization.SIMPLE_FRAME_VIEW;
    protected final Framework fr;

    private OntologyView.OntologyViewListener listener;

    public enum DiffEnum {
        SYNTACTIC, ENTAILMENT, CEX;
    }

    public DiffView(final Framework f) {
        this.fr = f;
        createView();
        createControllers();
    }

    private void createView() {
        this.setLayout(new BorderLayout());
        final JPanel pnlTop = new JPanel();
        pnlTop.setLayout(new BorderLayout());
        final JPanel pnlButtons = new JPanel();
        pnlButtons.setLayout(new BorderLayout());
        pnlTop.add(pnlButtons, BorderLayout.CENTER);

        this.add(pnlTop, BorderLayout.NORTH);
        final JSplitPane jSplitPane = new JSplitPane();
        jSplitPane.setOneTouchExpandable(true);
        jSplitPane.setDividerSize(10);

        originalView = new OntologyView(fr, true, new Color(200, 255, 200), Color.GREEN);
        jSplitPane.setLeftComponent(originalView);

        updateView = new OntologyView(fr, false, new Color(255, 200, 200), Color.RED);
        jSplitPane.setRightComponent(updateView);

        jSplitPane.setResizeWeight(0.5D);
        this.add(jSplitPane, BorderLayout.CENTER);
    }

    private OntologyView getOpposite(final OntologyView view) {
        return (view == originalView) ? updateView : originalView;
    }

    private void createControllers() {
        listener = new OntologyView.OntologyViewListener() {
            private OntologyView last;

            public void scrollChanged(OntologyView view, int value) {
                getOpposite(view).scrollTo(value);
            }

            public void selectionChanged(OntologyView view, TreePath path) {
                if (last == null || last == view) {         // Not to propagate the event back to the source.
                    last = view;
                    getOpposite(view).selectItem(path);
                } else {
                    last = null;
                }
            }

            public void treeExpandedOrCollapsed(OntologyView view, boolean expanded, TreePath path) {
                getOpposite(view).expandOrCollapse(expanded, path);
            }

            public void mergeSelectionChanged(OntologyView view) {
                updateDependencies();
            }
        };

        originalView.addOntologyViewListener(listener);
        updateView.addOntologyViewListener(listener);
    }


    public void updateDependencies() {
        runInSwingThread(new Runnable() {
            public void run() {
                boolean mergePossible = (!originalView.getSelectedForMerge().isEmpty() || !updateView.getSelectedForMerge().isEmpty());

                fr.getActions().getAction(OWLDiffAction.merge).setEnabled(mergePossible);
                fr.getActions().getAction(OWLDiffAction.mergeToFile).setEnabled(mergePossible);
                fr.getActions().getAction(OWLDiffAction.useCEX).setEnabled(syntacticDiffOutput != null && cexDiffOutput == null && OWLDiffConfiguration.isReasonerAvailable());
                fr.getActions().getAction(OWLDiffAction.showExplanations).setEnabled(syntacticDiffOutput != null && !(entailmentsExplanationsDiffOutput != null) && OWLDiffConfiguration.isReasonerAvailable());
                fr.getActions().getAction(OWLDiffAction.descriptionLogic).setEnabled(syntacticDiffOutput != null);
                fr.getActions().getAction(OWLDiffAction.manchester).setEnabled(syntacticDiffOutput != null);
                fr.getActions().getAction(OWLDiffAction.showAxiomList).setEnabled(syntacticDiffOutput != null);
                fr.getActions().getAction(OWLDiffAction.showAssertedFrames).setEnabled(syntacticDiffOutput != null);
                fr.getActions().getAction(OWLDiffAction.showClassifiedFrames).setEnabled(syntacticDiffOutput != null && OWLDiffConfiguration.isReasonerAvailable());
                fr.getActions().getAction(OWLDiffAction.showCommon).setEnabled(syntacticDiffOutput != null);
//  TODO              fr.getActions().getAction(OWLDiffAction.considerAnnotations).setEnabled(syntacticDiffOutput != null);
                fr.getActions().getAction(OWLDiffAction.selectAllOriginal).setEnabled(syntacticDiffOutput != null && !syntacticDiffOutput.getInOriginal().isEmpty());
                fr.getActions().getAction(OWLDiffAction.selectAllUpdate).setEnabled(syntacticDiffOutput != null && !syntacticDiffOutput.getInUpdate().isEmpty());
                fr.getActions().getAction(OWLDiffAction.deselectAllOriginal).setEnabled(!originalView.getSelectedForMerge().isEmpty());
                fr.getActions().getAction(OWLDiffAction.deselectAllUpdate).setEnabled(!updateView.getSelectedForMerge().isEmpty());
            }
        });
    }

    private ExplanationManager getExplanationManager(final OWLOntology o) {
//        return new BBPelletExplanationManager(o, OWLDiffConfiguration.getOWLReasonerFactory()) ;
        return new BBOWLAPIExplanationManager(o);
    }

    private void preventView(DiffVisualization vis) {
        if (visualization == vis) {
            runInSwingThread(new Runnable() {
                public void run() {
                    setVisualization(DiffVisualization.SIMPLE_FRAME_VIEW);
                }
            });
        }
    }

    private void runInSwingThread(final Runnable r) {
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            SwingUtilities.invokeLater(r);
        }
    }

    public void diffOntologies(final OWLOntology original, final OWLOntology update) {
        diffOntologies(original, update, null);
    }

    public void diffOntologies(final OWLOntology original, final OWLOntology update, SyntacticDiffOutput syntacticDiffOutput) {
        this.handler = new OntologyHandler() {

            public OWLOntology getOriginalOntology() {
                return original;
            }

            public OWLOntology getUpdateOntology() {
                return update;
            }
        };

        this.syntacticDiffOutput = syntacticDiffOutput;
        runDiff(DiffEnum.SYNTACTIC);
    }

    public void runDiff(final DiffEnum de) {
        new Thread() {

            public void run() {
                fr.setProgressVisible(true);
                fr.setProgress(0);

                long time = System.currentTimeMillis();

                fr.showMsg(String.format(OWLDiffConfiguration.getCoreTranslations().getString("core.msg.executing-diff"), de));

                try {
                    switch (de) {
                        case SYNTACTIC:
                            if (syntacticDiffOutput == null) {
                                syntacticDiffOutput = new SyntacticDiff(handler, fr).diff();
                            }
                            entailmentsExplanationsDiffOutput = null;
                            cexDiffOutput = null;
                            preventView(DiffVisualization.CLASSIFIED_FRAME_VIEW);
                            originalView.setDiffOutput(syntacticDiffOutput.getInOriginal(), handler.getOriginalOntology());
                            updateView.setDiffOutput(syntacticDiffOutput.getInUpdate(), handler.getUpdateOntology());
                            setCommon(false);
                            break;
                        case ENTAILMENT:
                            if (syntacticDiffOutput == null) {
                                syntacticDiffOutput = new SyntacticDiff(handler, fr).diff();
                            }
                            if (entailmentsExplanationsDiffOutput == null) {
                                entailmentsExplanationsDiffOutput = new EntailmentsExplanationsDiff(handler, fr, syntacticDiffOutput).diff();
                            }
                            setCommon(false);
                            originalView.addExplanations(entailmentsExplanationsDiffOutput.getInferred(), getExplanationManager(handler.getUpdateOntology()));
                            updateView.addExplanations(entailmentsExplanationsDiffOutput.getPossiblyRemove(), getExplanationManager(handler.getOriginalOntology()));
                            break;
                        case CEX:
                            if (cexDiffOutput == null) {
                                cexDiffOutput = new CEXDiff(handler, fr).diff();
                            }
                            /*if (!cexDiffOutput.isOntologyEL()) {
                                fr.showError(null, "Ontology is not acyclic EL");
                                break;
                            }*/
                            preventView(DiffVisualization.LIST_VIEW);
                            setCommon(true);
                            originalView.addCEX(cexDiffOutput.getOriginalDiffR(), cexDiffOutput.getOriginalDiffL());
                            updateView.addCEX(cexDiffOutput.getUpdateDiffR(), cexDiffOutput.getUpdateDiffL());
                            break;
                    }
                } catch (final OWLDiffException odex) {
                    String title = OWLDiffConfiguration.getCoreTranslations().getString("core.owldiffex." + odex.getReason().name());
                    if (title == null) {
                        title = OWLDiffConfiguration.getCoreTranslations().getString("core.owldiffex.default");
                    }
                    fr.showError(odex, title);
                } catch (final Exception ex) {
                    fr.showError(ex, OWLDiffConfiguration.getCoreTranslations().getString("core.diff.performfailed"));
                }

                fr.showMsg(String.format(OWLDiffConfiguration.getCoreTranslations().getString("core.msg.computation-took"), System.currentTimeMillis() - time));
                updateDependencies();
                fr.setProgressVisible(false);
            }

            private void setCommon(boolean show) {
                ((AbstractToggleAction) fr.getActions().getAction(OWLDiffAction.showCommon)).setState(show);
            }
        }.start();
    }

    public void saveMergeResult(final boolean mergeToFile) {
        new Thread() {
            public void run() {
                final OWLOntologyManager m = handler.getUpdateOntology().getOWLOntologyManager();
                URI newFile = m.getOntologyDocumentIRI(handler.getUpdateOntology()).toURI();

                final MergeAcceptDialog ma = new MergeAcceptDialog(originalView.getSyntax().getSyntax(), null, true, originalView.getSelectedForMerge(), updateView.getSelectedForMerge(), new File(newFile), mergeToFile);
                if (ma.returnValue) {
                    if (handler.getUpdateOntology() == null) {
                        return;
                    }

                    final OWLOntology o = handler.getUpdateOntology();

                    changeOntology(o, originalView.getSelectedForMerge(), updateView.getSelectedForMerge());
                    if ((OWLDiffConfiguration.isReasonerAvailable() && !OWLDiffConfiguration.getOWLReasoner(o).isConsistent()) &&
                            JOptionPane.showConfirmDialog(DiffView.this, OWLDiffConfiguration.getCoreTranslations().getString("core.merge-dialog.save-inconsistent-ontology")) != JOptionPane.OK_OPTION) {
                        changeOntology(o, updateView.getSelectedForMerge(), originalView.getSelectedForMerge());
                        return;
                    }

                    try {
                        if (mergeToFile) {
                            newFile = ma.getSelectedFile().toURI();
                            m.setOntologyDocumentIRI(o, IRI.create(newFile));
                            fr.showMsg(String.format(OWLDiffConfiguration.getCoreTranslations().getString("core.msg.saving"), newFile));
                            m.saveOntology(o);
                            fr.showMsg(String.format(OWLDiffConfiguration.getCoreTranslations().getString("core.msg.merged-ontology-saveds"), newFile));
                        }
                    } catch (Exception ex) {
                        fr.showError(ex, OWLDiffConfiguration.getCoreTranslations().getString("core.error.failed-to-save-merged-ontology"));
                        return;
                    }
                    syntacticDiffOutput = null;
                    runDiff(DiffEnum.SYNTACTIC);
                }
            }
        }.start();
    }

    private void changeOntology(final OWLOntology o, final Set<OWLAxiom> toAdd, final Set<OWLAxiom> toDelete) {
        try {
            o.getOWLOntologyManager().addAxioms(o, toAdd);
        } catch (Exception ex) {
            fr.showError(ex, String.format(OWLDiffConfiguration.getCoreTranslations().getString("core.error.failed-to-add-axiom"), toAdd));
            return;
        }
        try {
            o.getOWLOntologyManager().removeAxioms(o, toDelete);
        } catch (Exception ex) {
            fr.showError(ex, String.format(OWLDiffConfiguration.getCoreTranslations().getString("core.error.failed-to-remove-axiom"), toDelete));
            return;
        }
    }

    public void selectAllOriginalAxioms() {
        this.originalView.addSelected(syntacticDiffOutput.getInOriginal());
        updateDependencies();
    }

    public void selectAllUpdateAxioms() {
        this.updateView.addSelected(syntacticDiffOutput.getInUpdate());
        updateDependencies();
    }

    public void deselectAllOriginalAxioms() {
        this.originalView.removeSelected(syntacticDiffOutput.getInOriginal());
        updateDependencies();
    }

    public void deselectAllUpdateAxioms() {
        this.updateView.removeSelected(syntacticDiffOutput.getInUpdate());
        updateDependencies();
    }

    public void setShowCommonAxioms(boolean show) {
        this.originalView.setShowCommonAxioms(show);
        this.updateView.setShowCommonAxioms(show);
    }

    public void setConsiderAnnotations(boolean show) {
        // TODO use for ticket #8
    }

    public void setSyntax(SyntaxEnum syntax) {
        this.originalView.syntaxChanged(syntax);
        this.updateView.syntaxChanged(syntax);
    }

    public void setVisualization(DiffVisualization v) {
        if (visualization == v) {
            return;
        }

        this.visualization = v;
        new Thread() {
            public void run() {
                fr.setProgressVisible(true);
                fr.setProgress(0);
                fr.setProgressMax(2);
                fr.showMsg(OWLDiffConfiguration.getCoreTranslations().getString("core.msg.building-visualization"));
                originalView.setView(visualization);
                fr.setProgress(1);
                updateView.setView(visualization);
                fr.setProgress(2);
                fr.showMsg(OWLDiffConfiguration.getCoreTranslations().getString("core.msg.view-created"));
                fr.setProgressVisible(false);
            }
        }.start();
    }
}