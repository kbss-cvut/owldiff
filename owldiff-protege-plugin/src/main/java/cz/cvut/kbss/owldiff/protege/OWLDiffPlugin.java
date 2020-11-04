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
package cz.cvut.kbss.owldiff.protege;

import cz.cvut.kbss.owldiff.diff.OWLDiffConfiguration;
import cz.cvut.kbss.owldiff.protege.actions.*;
import cz.cvut.kbss.owldiff.view.*;
import org.protege.editor.core.ui.util.JOptionPaneEx;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.inference.ProtegeOWLReasonerInfo;
import org.protege.editor.owl.ui.view.AbstractActiveOntologyViewComponent;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.IRIDocumentSource;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OWLDiffPlugin extends AbstractActiveOntologyViewComponent implements ProgressListener, Framework {

    private static final Logger LOG = Logger.getLogger(OWLDiffPlugin.class.getName());
    private static final long serialVersionUID = -1511915926159556871L;
    // VIEW
    private JProgressBar progressBar;
    private JLabel lblStatusBar;
    private JFileChooser fc;
    private DiffView diffView;

    private SwingActions actions;

    private static String svnUserName;
    private static String svnPassword;
    private OWLModelManagerListener modelListener;
    private OWLOntologyChangeListener ontologyChangeListener;

    private static final ResourceBundle resourceBundle;

    static {
        resourceBundle = ResourceBundle.getBundle("plugin");
    }

    private void setFile(final URI newFile) {
        new Thread() {
            public void run() {
                showMsg(resourceBundle.getString("protegeplugin.msg.parsing"));
                setProgressVisible(true);
                setProgressMax(3);
                setProgress(0);

                try {
                    OWLOntology originalO = getOWLModelManager().getActiveOntology();
                    showMsg(resourceBundle.getString("protegeplugin.msg.original-parsed"));
                    setProgress(1);

                    OWLOntologyManager updateM = OWLManager.createOWLOntologyManager();
                    OWLOntologyLoaderConfiguration config = new OWLOntologyLoaderConfiguration();
                    config.setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT);

                    OWLOntology updateO = updateM.loadOntologyFromOntologyDocument(new IRIDocumentSource(IRI.create(newFile)), config);
                    showMsg(resourceBundle.getString("protegeplugin.msg.update-parsed"));
                    setProgress(2);

                    diffView.diffOntologies(originalO, updateO);
                    setProgress(3);
                } catch (Exception e) {
                    showError(e, resourceBundle.getString("protegeplugin.error.loading"));
                } finally {
                    setProgressVisible(false);
                }
            }
        }.start();
    }

    public void showError(Exception e, final String msg) {
        if (LOG.isLoggable(Level.SEVERE)) {
            LOG.log(Level.SEVERE, msg, e);
        }

        final StringBuilder s = new StringBuilder();
        s.append(msg);
        if (e != null) {
            s.append(": ");
            s.append(e.getMessage());
        }
        JOptionPane.showMessageDialog(this, s.toString(), resourceBundle.getString("protegeplugin.msg.owldiff-error"), JOptionPane.ERROR_MESSAGE);
        showInStatusBar(s.toString());
    }

    public void showMsg(final String msg) {
        if (LOG.isLoggable(Level.INFO)) {
            LOG.info(msg);
        }

        showInStatusBar(msg);
    }

    public SwingActions getActions() {
        return actions;
    }

    public void quit() {
        OWLDiffPlugin.this.disposeOntologyView();
    }

    private void showInStatusBar(final String msg) {
        scheduleForEDT(new Runnable() {
            public void run() {
                lblStatusBar.setText(msg);
            }
        });
    }

    public void setProgressVisible(final boolean flag) {
        scheduleForEDT(new Runnable() {
            public void run() {
                progressBar.setVisible(flag);
                setCursor(new Cursor(flag ? Cursor.WAIT_CURSOR : Cursor.DEFAULT_CURSOR));
                setEnabled(!flag);
            }
        });
    }

    public void setProgress(final int p) {
        if (progressBar != null) {
            scheduleForEDT(new Runnable() {
                public void run() {
                    progressBar.setValue(p);
                }
            });
        }
    }

    public void setProgressMax(final int max) {
        if (progressBar != null) {
            scheduleForEDT(new Runnable() {
                public void run() {
                    progressBar.setValue(0);
                    progressBar.setMaximum(max);
                }
            });
        }
    }

    private static boolean getCredForSVNLogin() {
        final JLabel nLabel = new JLabel(resourceBundle.getString("protegeplugin.svn.name"), JLabel.RIGHT);
        final JTextField nF = new JTextField("");
        nF.setColumns(20);
        nF.setText(svnUserName);
        final JLabel pLabel = new JLabel(resourceBundle.getString("protegeplugin.svn.password"), JLabel.RIGHT);
        final JTextField pF = new JPasswordField("");
        pF.setColumns(20);
        pF.setText(svnPassword);
        final JPanel mPanel = new JPanel(false);
        mPanel.setLayout(new BoxLayout(mPanel, BoxLayout.X_AXIS));
        mPanel.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        final JPanel namePanel = new JPanel(false);
        namePanel.setLayout(new GridLayout(0, 1));
        namePanel.add(nLabel);
        namePanel.add(pLabel);
        final JPanel fieldPanel = new JPanel(false);
        fieldPanel.setLayout(new GridLayout(0, 1));
        fieldPanel.add(nF);
        fieldPanel.add(pF);
        mPanel.add(namePanel, BorderLayout.CENTER);
        mPanel.add(fieldPanel, BorderLayout.AFTER_LAST_LINE);

        if (JOptionPaneEx.showConfirmDialog(null, resourceBundle.getString("protegeplugin.svn.insertcredentials"), mPanel, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null) != JOptionPane.OK_OPTION) {
            return false;
        }
        svnUserName = nF.getText();
        svnPassword = pF.getText();

        return true;
    }

    enum SVNDiff {
        BASE, HEAD, REVISION;
    }

    private void compareToSVN(final SVNDiff svnDiff) {
        final Thread r = new Thread() {
            @Override
            public void run() {
                synchronized (SVNManager.getInstance()) {
                    try {
                        CompareToFileAction.INSTANCE.setEnabled(false);
                        CompareToSVNBaseAction.INSTANCE.setEnabled(false);
                        CompareToSVNHeadAction.INSTANCE.setEnabled(false);
                        CompareToSVNRevisionAction.INSTANCE.setEnabled(false);
                        CompareToOntologyAction.INSTANCE.setEnabled(false);

                        showMsg(resourceBundle.getString("protegeplugin.svn.checking"));

                        final URI orig = getOWLModelManager().getOntologyPhysicalURI(getOWLModelManager().getActiveOntology());

                        if (!orig.getScheme().startsWith("file")) {
                            showError(new Exception(), resourceBundle.getString("protegeplugin.svn.file-svn-only"));
                            return;
                        }

                        boolean connectOK = true;
                        while ((!SVNManager.getInstance().connect(svnUserName, svnPassword, new File(orig)))) {
                            if (!getCredForSVNLogin()) {
                                connectOK = false;
                                break;
                            }
                        }
                        // // TODO remove from EDT

                        if (connectOK) {
                            switch (svnDiff) {
                                case BASE:
                                    setFile(SVNManager.getInstance().getBaseFile().toURI());
                                    break;
                                case HEAD:
                                    setFile(SVNManager.getInstance().getBaseFile().toURI());
                                    break;
                                case REVISION:
                                    final JComboBox revList = new JComboBox(SVNManager.getInstance().getRevisionNumbers().toArray());
                                    scheduleForEDT(new Runnable() {
                                        public void run() {
                                            showMsg(resourceBundle.getString("protegeplugin.svn.waiting-for-revision"));
                                        }
                                    });
                                    if (JOptionPaneEx.showConfirmDialog(null, resourceBundle.getString("protegeplugin.svn.select-revision"), revList, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null) == JOptionPane.OK_OPTION) {
                                        setFile(SVNManager.getInstance().getFileForRevision(Long.valueOf(revList.getSelectedItem().toString())).toURI());
                                    }
                                    break;
                                default:
                                    throw new RuntimeException(resourceBundle.getString("protegeplugin.svn.unknown-svn-diff"));
                            }
                        } else {
                            scheduleForEDT(new Runnable() {
                                public void run() {
                                    showError(new Exception(), resourceBundle.getString("protegeplugin.svn.operation-failed"));
                                }
                            });
                        }
                    } finally {
                        CompareToFileAction.INSTANCE.setEnabled(true);
                        CompareToSVNBaseAction.INSTANCE.setEnabled(true);
                        CompareToSVNHeadAction.INSTANCE.setEnabled(true);
                        CompareToSVNRevisionAction.INSTANCE.setEnabled(true);
                        CompareToOntologyAction.INSTANCE.setEnabled(true);
                    }
                }
            }
        };
        r.start();
    }

    private Icon getIcon(String name) {
        return new ImageIcon(OWLDiffPlugin.class.getResource("images/" + name));
    }

    public void compareTo(OWLOntology o) {
        diffView.diffOntologies(getOWLModelManager().getActiveOntology(), o);
    }

    public void compareToFile() {
        if (fc.showOpenDialog(OWLDiffPlugin.this) == JFileChooser.APPROVE_OPTION) {
            setFile(fc.getSelectedFile().toURI());
        }
    }

    public void compareToSVNBase() {
        compareToSVN(SVNDiff.BASE);
    }

    public void compareToSVNHead() {
        compareToSVN(SVNDiff.HEAD);
    }

    public void compareToSVNRevision() {
        compareToSVN(SVNDiff.REVISION);
    }

    private JComponent getToolBar() {
        final JToolBar toolbar = new JToolBar(JToolBar.HORIZONTAL);

//        addToToolBar(toolbar, OWLDiffAction.considerAnnotations, "considerAnnotations.png");
        addToToolBar(toolbar, OWLDiffAction.showCommon, "showCommon.png");
        toolbar.addSeparator();
        actions.addRadioButtonGroup(toolbar, new Icon[]{getIcon("showAxiomList.png"), getIcon("showAssertedFrames.png"), getIcon("showClassifiedFrames.png")}, OWLDiffAction.showAxiomList, OWLDiffAction.showAxiomList, OWLDiffAction.showAssertedFrames, OWLDiffAction.showClassifiedFrames);
        toolbar.addSeparator();
        actions.addRadioButtonGroup(toolbar, new Icon[]{getIcon("manchester.png"), getIcon("descriptionLogic.png")}, OWLDiffAction.manchester, OWLDiffAction.manchester, OWLDiffAction.descriptionLogic);
        toolbar.addSeparator();
        addToToolBar(toolbar, OWLDiffAction.showExplanations, "showExplanations.png");
        addToToolBar(toolbar, OWLDiffAction.useCEX, "useCEX.png");
        toolbar.addSeparator();
        addToToolBar(toolbar, OWLDiffAction.selectAllOriginal, "selectAllOriginal.png");
        addToToolBar(toolbar, OWLDiffAction.selectAllUpdate, "selectAllUpdate.png");
        addToToolBar(toolbar, OWLDiffAction.deselectAllOriginal, "deselectAllOriginal.png");
        addToToolBar(toolbar, OWLDiffAction.deselectAllUpdate, "deselectAllUpdate.png");
        toolbar.addSeparator();
        addToToolBar(toolbar, OWLDiffAction.merge, "merge.png");
        addToToolBar(toolbar, OWLDiffAction.mergeToFile, "mergeToFile.png");

        return toolbar;
    }

    private void addToToolBar(JToolBar toolbar, OWLDiffAction action, final String icon) {
        actions.addAction(toolbar, action, getIcon(icon));
    }

    private JComponent getStatusBar() {
        final JPanel bottomPanel = new JPanel(new BorderLayout(6, 6));

        lblStatusBar = new JLabel();
        bottomPanel.add(lblStatusBar, BorderLayout.CENTER);

        progressBar = new JProgressBar();
        progressBar.setBorderPainted(false);
        progressBar.setStringPainted(true);
        bottomPanel.add(progressBar, BorderLayout.EAST);
        bottomPanel.setPreferredSize(new Dimension(bottomPanel.getPreferredSize().width, progressBar.getPreferredSize().height));
        progressBar.setVisible(false);

        return bottomPanel;
    }

    @Override
    public void initialiseOntologyView() throws Exception {
        fc = new JFileChooser(getOWLEditorKit().getModelManager().getOntologyCatalogManager().getActiveCatalogFolder());
        fc.setDialogTitle(resourceBundle.getString("protegeplugin.svn.open-ontology-file"));

        modelListener = new OWLModelManagerListener() {
            public void handleChange(OWLModelManagerChangeEvent event) {
                switch (event.getType()) {
                    case ACTIVE_ONTOLOGY_CHANGED:
                        //TODO
                        break;
                    case REASONER_CHANGED:
                    case ONTOLOGY_CLASSIFIED:
                        OWLDiffConfiguration.setReasonerProvider(new OWLDiffConfiguration.ReasonerProvider() {
                            public OWLReasoner getOWLReasoner(OWLOntology o) {
                                //if (o.equals(getOWLModelManager().getActiveOntology())) {
                                if (o == getOWLModelManager().getActiveOntology()) {
                                    return getOWLModelManager().getReasoner();
                                } else {
                                    ProtegeOWLReasonerInfo i = OWLDiffPlugin.this.getOWLModelManager().getOWLReasonerManager().getCurrentReasonerFactory();
                                    if (i != null) {
                                        return i.getReasonerFactory().createReasoner(o);
                                    } else {
                                        return null;
                                    }
                                }
                            }
                        });

                        diffView.updateDependencies();
                        break;
                }
            }
        };
        getOWLModelManager().addListener(modelListener);

        ontologyChangeListener = new OWLOntologyChangeListener() {
            public void ontologiesChanged(List<? extends OWLOntologyChange> arg0) throws OWLException {
                // TODO
            }
        };
        getOWLModelManager().addOntologyChangeListener(ontologyChangeListener);

        OWLDiffConfiguration.setReasonerProvider(new OWLDiffConfiguration.ReasonerProvider() {
            public OWLReasoner getOWLReasoner(OWLOntology o) {
                //return OWLDiffPlugin.this.getOWLModelManager().getReasoner();
                //if (o.equals(getOWLModelManager().getActiveOntology())) {
                // equals on OWLOntologies does not work well, it is probably not implemented, thus works on hashCode,that maybe derived from ontologyIRI (we have 2 different ontos with the same ID)
                // could be compared by equals on axiom sets, but == comparison is quicker, and stricter :)
                if (o == getOWLModelManager().getActiveOntology()) {
                    return getOWLModelManager().getReasoner();
                } else {
                    ProtegeOWLReasonerInfo i = OWLDiffPlugin.this.getOWLModelManager().getOWLReasonerManager().getCurrentReasonerFactory();
                    if (i != null) {
                        return i.getReasonerFactory().createReasoner(o);
                    } else {
                        return null;
                    }
                }
            }
        });

        diffView = new DiffView(this);

        this.actions = new SwingActions(diffView, this);
        this.setLayout(new BorderLayout(6, 6));
        this.add(getToolBar(), BorderLayout.NORTH);
        this.add(diffView, BorderLayout.CENTER);
        this.add(getStatusBar(), BorderLayout.SOUTH);

        diffView.updateDependencies();

        showMsg(String.format(resourceBundle.getString("protegeplugin.msg.owldiff-started"), OWLDiffConfiguration.getVersion()));
    }

    private static void scheduleForEDT(final Runnable r) {
        if (SwingUtilities.isEventDispatchThread()) r.run();
        else SwingUtilities.invokeLater(r);
    }

    @Override
    protected void updateView(OWLOntology activeOntology) {
    }

    @Override
    public void disposeOntologyView() {
        getOWLModelManager().removeListener(modelListener);
        getOWLModelManager().removeOntologyChangeListener(ontologyChangeListener);
    }
}
