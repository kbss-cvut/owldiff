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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;

import cz.cvut.kbss.owldiff.view.DiffView;
import cz.cvut.kbss.owldiff.view.DiffVisualization;
import cz.cvut.kbss.owldiff.view.OWLDiffAction;
import cz.cvut.kbss.owldiff.syntax.SyntaxEnum;

/**
 * For given instance of OwlDiffView, the instance of ODActions keeps all
 * actions in a hashmap.
 */
public class ODActions {
	private final Map<OWLDiffAction, Action> actionMap = new HashMap<OWLDiffAction, Action>();

	/**
	 * Method creates an instance of the action and put it to the actionMap
	 * hashmap.
	 * 
	 * @param actionEnum -
	 *            identifies the particular action
	 * @see OWLDiffAction
	 * @param relativeIconName -
	 *            name of the icon representing the particular action in the GUI
	 * @param actionRunnable -
	 *            its run() method will be carried out when the action is
	 *            invoked
	 * @throws IOException
	 */
	private void setupAction(final Action a, final OWLDiffAction actionEnum,
			final String relativeIconName, final int styleConstant) {
		ImageDescriptor imageDescr = null;

		// KeyStroke keyStroke = (KeyStroke) actionEnum.getKeyStroke();
		// a.setAccelerator(keyStroke.getKeyCode() | SWT.CTRL);//
		// keyStroke.getModifiers());

		imageDescr = ImageDescriptor.createFromURL(ODActions.class
				.getClassLoader().getResource(relativeIconName));

		a.setImageDescriptor(imageDescr);
		a.setEnabled(false);

		actionMap.put(actionEnum, a);
	}

	public ODActions(final DiffView owlDiffView) {
		Action a = null;

		// Execution of initial actions

		// useCEX
		a = new Action(OWLDiffAction.useCEX.getLabel(), IAction.AS_UNSPECIFIED) {
			public void run() {
				owlDiffView.runCEXDiff();
			}
		};
		setupAction(a, OWLDiffAction.useCEX, "useCEX.png",
				IAction.AS_UNSPECIFIED);

		// showExplanations
		a = new Action(OWLDiffAction.showExplanations.getLabel(), IAction.AS_UNSPECIFIED) {
			public void run() {
				owlDiffView.setShowEntailments(true);
			}
		};
		setupAction(a, OWLDiffAction.showExplanations, "showExplanations.png",
				IAction.AS_UNSPECIFIED);

		// selectAllOriginal
		a = new Action(OWLDiffAction.selectAllOriginal.getLabel(), IAction.AS_UNSPECIFIED) {
			public void run() {
				owlDiffView.selectAllOriginalAxioms();
			}
		};
		setupAction(a, OWLDiffAction.selectAllOriginal,
				"selectAllOriginal.png", IAction.AS_UNSPECIFIED);

		// selectAllUpdate
		a = new Action(OWLDiffAction.selectAllUpdate.getLabel(), IAction.AS_UNSPECIFIED) {
			public void run() {
				owlDiffView.selectAllUpdateAxioms();
			}
		};
		setupAction(a, OWLDiffAction.selectAllUpdate, "selectAllUpdate.png",
				IAction.AS_UNSPECIFIED);

		// deselectAllOriginal
		a = new Action(OWLDiffAction.deselectAllOriginal.getLabel(), IAction.AS_UNSPECIFIED) {
			public void run() {
				owlDiffView.deselectAllOriginalAxioms();
			}
		};
		setupAction(a, OWLDiffAction.deselectAllOriginal,
				"deselectAllOriginal.png", IAction.AS_UNSPECIFIED);

		// deselectAllUpdate
		a = new Action(OWLDiffAction.deselectAllUpdate.getLabel(), IAction.AS_UNSPECIFIED) {
			public void run() {
				owlDiffView.deselectAllUpdateAxioms();

			}
		};
		setupAction(a, OWLDiffAction.deselectAllUpdate,
				"deselectAllUpdate.png", IAction.AS_UNSPECIFIED);

		// merge
		a = new Action(OWLDiffAction.merge.getLabel(), IAction.AS_UNSPECIFIED) {
			public void run() {
				owlDiffView.saveMergeResult();
			}
		};
		setupAction(a, OWLDiffAction.merge, "merge.png",
				IAction.AS_UNSPECIFIED);

		// showCommon
		a = new Action(OWLDiffAction.showCommon.getLabel(), IAction.AS_CHECK_BOX) {
			public void run() {
				owlDiffView.setShowCommonAxioms(!owlDiffView
						.isShowCommonAxioms());
			}
		};
		setupAction(a, OWLDiffAction.showCommon, "showCommon.png",
				IAction.AS_CHECK_BOX);
		a.setChecked(owlDiffView.isShowCommonAxioms());

		final ActionGroup grpVisualization = new ActionGroup();

		// showAxiomList
		a = new Action(OWLDiffAction.showAxiomList.getLabel(), IAction.AS_RADIO_BUTTON) {
			public void run() {
				grpVisualization.setSelectedAction(this);
				owlDiffView.setView(DiffVisualization.LIST_VIEW);
			}
		};

		setupAction(a, OWLDiffAction.showAxiomList, "showAxiomList.png",
				IAction.AS_RADIO_BUTTON);
		grpVisualization.addAction(a);

		// showAssertedFrames
		a = new Action(OWLDiffAction.showAssertedFrames.getLabel(), IAction.AS_RADIO_BUTTON) {
			public void run() {
				grpVisualization.setSelectedAction(this);
				owlDiffView.setView(DiffVisualization.SIMPLE_FRAME_VIEW);
			}
		};
		setupAction(a, OWLDiffAction.showAssertedFrames,
				"showAssertedFrames.png", IAction.AS_RADIO_BUTTON);
		grpVisualization.addAction(a);

		// showClassifiedFrames
		a = new Action(OWLDiffAction.showClassifiedFrames.getLabel(), IAction.AS_RADIO_BUTTON) {
			public void run() {
				grpVisualization.setSelectedAction(this);
				owlDiffView.setView(DiffVisualization.CLASSIFIED_FRAME_VIEW);
			}
		};
		setupAction(a, OWLDiffAction.showClassifiedFrames,
				"showClassifiedFrames.png", IAction.AS_RADIO_BUTTON);
		grpVisualization.addAction(a);

		switch (owlDiffView.getView()) {
		case LIST_VIEW:
			grpVisualization.setSelectedAction(actionMap
					.get(OWLDiffAction.showAxiomList));
			break;
		case SIMPLE_FRAME_VIEW:
			grpVisualization.setSelectedAction(actionMap
					.get(OWLDiffAction.showAssertedFrames));
			break;
		case CLASSIFIED_FRAME_VIEW:
			grpVisualization.setSelectedAction(actionMap
					.get(OWLDiffAction.showClassifiedFrames));
			break;
		default:
			throw new IllegalArgumentException(
					"Unexpected DiffVisualization type : "
							+ owlDiffView.getView());
		}

		final ActionGroup grpSyntax = new ActionGroup();

		// manchester
		a = new Action(OWLDiffAction.manchester.getLabel(), IAction.AS_RADIO_BUTTON) {
			public void run() {
				owlDiffView.setSyntax(SyntaxEnum.MANCHESTER);
				grpSyntax.setSelectedAction(this);
			}
		};
		setupAction(a, OWLDiffAction.manchester, "manchester.png",
				IAction.AS_RADIO_BUTTON);
		grpSyntax.addAction(a);

		// descriptionLogic
		a = new Action(OWLDiffAction.descriptionLogic.getLabel(), IAction.AS_RADIO_BUTTON) {
			public void run() {
				owlDiffView.setSyntax(SyntaxEnum.DL);
				grpSyntax.setSelectedAction(this);
			}
		};
		setupAction(a, OWLDiffAction.descriptionLogic, "descriptionLogic.png",
				IAction.AS_RADIO_BUTTON);
		grpSyntax.addAction(a);

		switch (owlDiffView.getSyntax()) {
		case DL:
			grpSyntax.setSelectedAction(actionMap
					.get(OWLDiffAction.descriptionLogic));
			break;
		case MANCHESTER:
			grpSyntax.setSelectedAction(actionMap
					.get(OWLDiffAction.manchester));
			break;
		default:
			throw new IllegalArgumentException("Unexpected Syntax : "
					+ owlDiffView.getSyntax());
		}
	}

	Action getAction(OWLDiffAction a) {
		return actionMap.get(a);
	}

	private class ActionGroup {

		private final Collection<IAction> actions = new ArrayList<IAction>();

		public void addAction(IAction a) {
			actions.add(a);

			if (actions.size() == 1) {
				setSelectedAction(a);
			}
		}

		public void setSelectedAction(final IAction a) {
			if (!actions.contains(a)) {
				throw new IllegalArgumentException(
						"Selected action must be chosen from the initial action set : "
								+ actions);
			}
			for (final IAction ac : actions) {
				ac.setChecked(ac.equals(a));
			}
		}
	}
}
