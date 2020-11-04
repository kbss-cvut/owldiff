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

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchActionConstants;

import cz.cvut.kbss.owldiff.view.OWLDiffAction;

public class ODToolBar {
	private ODActions odActions;
	private IViewPart part;

	public ODToolBar(final ODActions odActions, final IViewPart part) {
		this.odActions = odActions;
		this.part = part;
		createToolBar();
	}

	private void createToolBar() {
		IToolBarManager mgr = part.getViewSite().getActionBars()
				.getToolBarManager();

		mgr.add(odActions.getAction(OWLDiffAction.showCommon));
		mgr.add(new Separator());
		mgr.add(odActions.getAction(OWLDiffAction.showAxiomList));
		mgr.add(odActions.getAction(OWLDiffAction.showAssertedFrames));
		mgr.add(odActions.getAction(OWLDiffAction.showClassifiedFrames));
		mgr.add(new Separator());
		mgr.add(odActions.getAction(OWLDiffAction.manchester));
		mgr.add(odActions.getAction(OWLDiffAction.descriptionLogic));
		mgr.add(new Separator());
		mgr.add(odActions.getAction(OWLDiffAction.showExplanations));
		mgr.add(odActions.getAction(OWLDiffAction.useCEX));
		mgr.add(new Separator());
		mgr.add(odActions.getAction(OWLDiffAction.selectAllOriginal));
		mgr.add(odActions.getAction(OWLDiffAction.selectAllUpdate));
		mgr.add(odActions.getAction(OWLDiffAction.deselectAllOriginal));
		mgr.add(odActions.getAction(OWLDiffAction.deselectAllUpdate));
		mgr.add(new Separator());
		mgr.add(odActions.getAction(OWLDiffAction.merge));
		mgr.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
	}
}
