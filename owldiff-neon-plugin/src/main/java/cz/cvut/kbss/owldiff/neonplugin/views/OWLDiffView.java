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
package cz.cvut.kbss.owldiff.neonplugin.views;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JComponent;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

import swingintegration.example.EmbeddedSwingComposite;
import cz.cvut.kbss.owldiff.view.DiffView;
import cz.cvut.kbss.owldiff.view.OWLDiffAction;
import cz.cvut.kbss.owldiff.neonplugin.Activator;

/**
 * OWLDiff NeON Plugin view
 */

public class OWLDiffView extends ViewPart {

	public static final String ID = "cz.cvut.kbss.owldiff.neonplugin.view";
	public static final String SECONDARY_ID = "cz.cvut.kbss.owldiff.neonplugin.view2";

	/**
	 * {@inheritDoc}
	 */
	public void setPartName(String name) {
		super.setPartName(name);
	}

	private DiffView df;

	private EmbeddedSwingComposite c;

	private ODActions odActions;

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 * 
	 * {@inheritDoc}
	 */
	public void createPartControl(Composite parent) {
		df = new DiffView(new cz.cvut.kbss.owldiff.view.Framework() {

			private double ratio = 1;

			public void showError(final Exception e, final String o) {
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						getStatusLineManager().setErrorMessage(o);

						StringWriter sw = new StringWriter();
						PrintWriter pw = new PrintWriter(sw);
						e.printStackTrace(pw);

						final ErrorDialog mb = new ErrorDialog(Display
								.getCurrent().getActiveShell(), "Error", o,
								new Status(IStatus.ERROR, Activator.PLUGIN_ID,
										e.getMessage(), new Exception(sw
												.toString().trim())),
								IStatus.ERROR);
						mb.open();
					}
				});
			}

			public void showMsg(final String o) {
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						getStatusLineManager().setMessage(o);
					}
				});
			}

			public void setProgress(final int p) {
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						getStatusLineManager().getProgressMonitor().worked(
								(int) (ratio * p));
					}
				});

			}

			public void setProgressMax(int max) {
				ratio = ((double) max) / 100;
			}

			public void setProgressVisible(final boolean show) {
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						if (show) {
							getStatusLineManager().getProgressMonitor()
									.beginTask("OWLDiff action", 100);
						} else {
							getStatusLineManager().getProgressMonitor().done();
						}
					}
				});
			}

			public void executeAction(final OWLDiffAction action) {
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						OWLDiffView.this.showBusy(true);
						odActions.getAction(action).run();
						OWLDiffView.this.showBusy(false);
					}
				});
			}

			public void quit() {
				OWLDiffView.this.dispose();
			}

			public void setEnabled(final OWLDiffAction action,
					final boolean enabled) {
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						odActions.getAction(action).setEnabled(enabled);
					}
				});
			}
		});
		odActions = new ODActions(getDiffView());
		new ODViewMenu(odActions, this);
		new ODToolBar(odActions, this);

		c = new EmbeddedSwingComposite(parent, SWT.EMBEDDED) {

			@Override
			protected JComponent createSwingComponent() {
				return df;
			}

		};
		c.populate();
	}

	private IStatusLineManager getStatusLineManager() {
		return getViewSite().getActionBars().getStatusLineManager();
	}

	/**
	 * {@inheritDoc}
	 */
	public void setFocus() {
		c.setFocus();
	}

	public DiffView getDiffView() {
		return df;
	}
}
