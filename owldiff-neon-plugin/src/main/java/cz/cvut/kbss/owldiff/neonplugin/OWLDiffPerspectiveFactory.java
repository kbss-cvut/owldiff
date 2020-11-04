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
package cz.cvut.kbss.owldiff.neonplugin;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import cz.cvut.kbss.owldiff.neonplugin.views.OWLDiffView;

public class OWLDiffPerspectiveFactory implements IPerspectiveFactory {
	public static final String OWLDIFFPERSPECTIVE_ID = "cz.cvut.kbss.owldiff.neonplugin.perspective";

	private static final String OWLDIFFAREA_ID = "cz.cvut.kbss.owldiff.owldiffarea";
	private static final String NAVIGATORAREA_ID = "cz.cvut.kbss.owldiff.navigatorarea";
	private static final String CONSOLEAREA_ID = "cz.cvut.kbss.owldiff.consolearea";
	private static final String ONTONAVIGATOR_ID = "com.ontoprise.ontostudio.views.navigator";

	/**
	 * {@inheritDoc}
	 */
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(false);

		final IFolderLayout navigatorArea = layout.createFolder(
				NAVIGATORAREA_ID, IPageLayout.LEFT, 0.25f, layout
						.getEditorArea());

		final IFolderLayout owlDiffViewArea = layout
				.createFolder(OWLDIFFAREA_ID, IPageLayout.RIGHT, 0.2f, layout
						.getEditorArea());
		owlDiffViewArea.addPlaceholder(OWLDiffView.ID + ":"
				+ OWLDiffView.SECONDARY_ID + "*");

		final IFolderLayout consoleArea = layout.createFolder(CONSOLEAREA_ID,
				IPageLayout.BOTTOM, 0.8f, OWLDIFFAREA_ID);
		
		navigatorArea.addView(ONTONAVIGATOR_ID);
		consoleArea.addView(IPageLayout.ID_PROGRESS_VIEW);

	}
}
