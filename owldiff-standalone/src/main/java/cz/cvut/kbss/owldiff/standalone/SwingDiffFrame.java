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
package cz.cvut.kbss.owldiff.standalone;

import cz.cvut.kbss.owlapi.ImportsIgnoringIRIMapper;
import cz.cvut.kbss.owldiff.diff.OWLDiffConfiguration;
import cz.cvut.kbss.owldiff.diff.syntactic.SyntacticDiffOutput;
import cz.cvut.kbss.owldiff.view.DiffView;
import cz.cvut.kbss.owldiff.view.Framework;
import cz.cvut.kbss.owldiff.view.OWLDiffAction;
import cz.cvut.kbss.owldiff.view.SwingActions;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.IRIDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URI;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SwingDiffFrame extends JFrame implements Framework {

    private static final long serialVersionUID = -1511915926159556871L;
    private static final Logger LOG = Logger.getLogger(SwingDiffFrame.class
            .getName());

    private static final String diffViewCardId = "diff";
    private final CardLayout cl = new CardLayout();
    private final JPanel cards = new JPanel(cl);
    private final JProgressBar progressBar = new JProgressBar();
    private final JLabel lblStatusBar = new JLabel();
    private final DiffView diffView;
    private final StandaloneActions actions;

    private static final ResourceBundle resourceBundle;

    static {
        resourceBundle = ResourceBundle.getBundle("standalone-translation");
    }

    public SwingDiffFrame() {
        this.setSize(400, 300);
        diffView = new DiffView(this);
        actions = new StandaloneActions(diffView, this, resourceBundle);
        this.setJMenuBar(createJMenuBar());
        this.setContentPane(create());
        this.setTitle(String.format("OWLDiff (%s)", OWLDiffConfiguration.getVersion()));
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        try {
            this.setIconImage(ImageIO
                    .read(getClass().getResource("/icon.gif")));
        } catch (Exception ex) {
            LOG.warning(resourceBundle.getString("message.icon-not-found"));
        }

        final Dimension dim = this.getToolkit().getScreenSize();
        final Rectangle abounds = this.getBounds();
        this.setLocation((dim.width - abounds.width) / 2,
                (dim.height - abounds.height) / 2);
    }

    public void setOntologies(final OWLOntology originalO, final OWLOntology updateO, SyntacticDiffOutput output) {
        diffView.diffOntologies(originalO, updateO, output);
    }

    public void setOntologies(final URI u1, final URI u2) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                cl.show(cards, diffViewCardId);
            }
        });

        showMsg(resourceBundle.getString("message.parsing"));
        setProgressVisible(true);
        setProgressMax(4);
        setProgress(0);

        try {
            final OWLOntologyManager originalM = OWLManager
                    .createOWLOntologyManager();
            originalM.addIRIMapper(new ImportsIgnoringIRIMapper(u1.toString()));
            final OWLOntology originalO = originalM.loadOntologyFromOntologyDocument(new IRIDocumentSource(IRI.create(u1)));
            showMsg(String.format(resourceBundle.getString("message.original-parsed"), originalO.getOntologyID(), u1, originalO.getAxiomCount()));

            setProgress(1);

            final OWLOntologyManager updateM = OWLManager.createOWLOntologyManager();
            updateM.addIRIMapper(new ImportsIgnoringIRIMapper(u2.toString()));
            OWLOntology updateO = updateM.loadOntologyFromOntologyDocument(new IRIDocumentSource(IRI.create(u2)));
            showMsg(String.format(resourceBundle.getString("message.update-parsed"), updateO.getOntologyID(), u2, updateO.getAxiomCount()));

            setProgress(2);
            setOntologies(originalO, updateO, null);
        } catch (OWLException e) {
            showError(e, resourceBundle.getString("message.error-during-loading"));
        } finally {
            setProgress(4);
            setProgressVisible(false);
        }
    }

    private JPanel createIntroPanel() {
        final JPanel pnlIntro = new JPanel();

        pnlIntro.setLayout(new GridBagLayout());
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        final JLabel lbl = new JLabel(resourceBundle.getString("message.welcome"));
        gbc.gridx = 0;
        gbc.gridy = 0;
        pnlIntro.add(lbl, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        final JButton btnOpen = new JButton(actions
                .getAction(OWLDiffStandaloneAction.open));
        pnlIntro.add(btnOpen, gbc);

        return pnlIntro;
    }

    private JPanel create() {
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        cards.add(createIntroPanel(), "intro");
        cards.add(diffView, diffViewCardId);
        mainPanel.add(cards, BorderLayout.CENTER);

        final JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.add(lblStatusBar, BorderLayout.CENTER);
        progressBar.setBorderPainted(false);
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);
        bottomPanel.add(progressBar, BorderLayout.EAST);
        bottomPanel.setPreferredSize(new Dimension(bottomPanel
                .getPreferredSize().width,
                progressBar.getPreferredSize().height));

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private JMenuBar createJMenuBar() {
        final JMenuBar menuBar = new JMenuBar();
        JMenu menu;

        menu = new JMenu(resourceBundle.getString("menu.file"));
        menuBar.add(menu);
        actions.addAction(menu, OWLDiffStandaloneAction.open);
        menu.addSeparator();
        actions.addAction(menu, OWLDiffAction.exit);

        menu = new JMenu(resourceBundle.getString("menu.diff"));
        menuBar.add(menu);
        actions.addAction(menu, OWLDiffAction.showExplanations);
        actions.addAction(menu, OWLDiffAction.useCEX);
        menu.addSeparator();
        actions.addAction(menu, OWLDiffAction.selectAllOriginal);
        actions.addAction(menu, OWLDiffAction.selectAllUpdate);
        actions.addAction(menu, OWLDiffAction.deselectAllOriginal);
        actions.addAction(menu, OWLDiffAction.deselectAllUpdate);
        actions.addAction(menu, OWLDiffAction.mergeToFile);

        menu = new JMenu(resourceBundle.getString("menu.view"));
        menuBar.add(menu);
//        actions.addAction(menu, OWLDiffAction.considerAnnotations);
        actions.addAction(menu, OWLDiffAction.showCommon);
        menu.addSeparator();
        actions.addRadioButtonGroup(menu, OWLDiffAction.showAssertedFrames, OWLDiffAction.showAxiomList, OWLDiffAction.showAssertedFrames, OWLDiffAction.showClassifiedFrames);
        menu.addSeparator();
        actions.addRadioButtonGroup(menu, OWLDiffAction.manchester, OWLDiffAction.manchester, OWLDiffAction.descriptionLogic);
        diffView.updateDependencies();

        return menuBar;
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

        JOptionPane.showMessageDialog(this, s.toString(), resourceBundle.getString("message.owldiff-error"), JOptionPane.ERROR_MESSAGE);
        showInStatusBar(s.toString());
    }

    public void showMsg(final String msg) {
        if (LOG.isLoggable(Level.INFO)) {
            LOG.info(msg);
        }

        showInStatusBar(msg);
    }

    private void showInStatusBar(final String msg) {
        if (SwingUtilities.isEventDispatchThread()) {
            lblStatusBar.setText(msg);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    lblStatusBar.setText(msg);
                }
            });
        }
    }

    public void setProgressVisible(final boolean flag) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                progressBar.setVisible(flag);
                setCursor(new Cursor(flag ? Cursor.WAIT_CURSOR
                        : Cursor.DEFAULT_CURSOR));
                setEnabled(!flag);
            }
        });
    }

    public void setProgress(final int p) {
        if (progressBar != null) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    progressBar.setValue(p);
                }
            });
        }
    }

    public void setProgressMax(final int max) {
        if (progressBar != null) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    progressBar.setValue(0);
                    progressBar.setMaximum(max);
                }
            });
        }
    }

    public SwingActions getActions() {
        return actions;
    }

    public void quit() {
        LOG.info(resourceBundle.getString("message.closing-owldiff"));
        System.exit(0);
    }

    public static void main(String[] args) {
        final SwingDiffFrame f = new SwingDiffFrame();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                f.setVisible(true);
            }
        });

        if (LOG.isLoggable(Level.INFO)) {
            LOG.info(MessageFormat.format(resourceBundle.getString("messages.running-diff"), Arrays.asList(args)));
        }

        if (args.length == 2) {
            URI oldF;
            URI newF;
            if (new File(args[0]).exists()) {
                oldF = new File(args[0]).getAbsoluteFile().toURI();
            } else {
                LOG
                        .warning(String.format(resourceBundle.getString("message.file-does-not-exist"), args[0]));
                oldF = URI.create(args[0]);
            }

            if (new File(args[1]).exists()) {
                newF = new File(args[1]).getAbsoluteFile().toURI();
            } else {
                LOG
                        .warning(String.format(resourceBundle.getString("message.file-does-not-exist"), args[1]));
                newF = URI.create(args[1]);
            }

            if (oldF.isAbsolute() && newF.isAbsolute()) {
                f.setOntologies(oldF, newF);
            } else {
                f.showError(null, String.format(resourceBundle.getString("message.uris-not-absolute"), oldF, newF));
            }
        }
    }
}