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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.WorkbenchException;
import org.semanticweb.kaon2.api.DefaultOntologyResolver;
import org.semanticweb.kaon2.api.KAON2Exception;
import org.semanticweb.kaon2.api.Ontology;

import com.ontoprise.api.formatting.OntoBrokerOntologyFileFormat;
import com.ontoprise.datamodel.compiler.SerializationConstants;
import com.ontoprise.ontostudio.datamodel.DatamodelPlugin;
import com.ontoprise.ontostudio.datamodel.api.IOntologyContainer;
import com.ontoprise.ontostudio.owl.gui.navigator.ontology.OntologyTreeElement;

import cz.cvut.kbss.owldiff.neonplugin.views.OWLDiffView;

public class RunOWLDiffAction implements IObjectActionDelegate {

	private class OntologyInfo {
		String id;
		String projectName;
	}

	private static Log log = LogFactory.getLog(RunOWLDiffAction.class);

	private static int i = 1;

	private IStructuredSelection s;
	private URI u1 = null;
	private URI u2 = null;
	private String id1 = null;
	private String id2 = null;
	private List<OntologyInfo> modifiedOntologies = new ArrayList<OntologyInfo>();

	private void analyzeOntologyURIs() {
		final Iterator ii = s.iterator();
		final Object first = ii.next();
		final Object second = ii.next();

		OntologyTreeElement firstE = (OntologyTreeElement) first;
		OntologyTreeElement secondE = (OntologyTreeElement) second;

		try {
			final String projectName1 = firstE.getProjectName();
			final String projectName2 = secondE.getProjectName();
			final DatamodelPlugin dm = DatamodelPlugin.getDefault();
			u1 = new File(dm.getPhysicalOntologyUri(projectName1, firstE
					.getModuleId())).toURI();
			u2 = new File(dm.getPhysicalOntologyUri(projectName2, secondE
					.getModuleId())).toURI();
			id1 = firstE.getId();
			id2 = secondE.getId();

			List<String> mod = Arrays.asList(DatamodelPlugin.getDefault()
					.getContainer(projectName1).getModifiedOntologies());

			if (mod.contains(id1)) {
				OntologyInfo oi = new OntologyInfo();
				oi.id = id1;
				oi.projectName = projectName1;
				modifiedOntologies.add(oi);
			}

			if (!projectName1.equals(projectName2)) {
				mod = Arrays.asList(DatamodelPlugin.getDefault().getContainer(
						projectName2).getModifiedOntologies());
			}

			if (mod.contains(id2)) {
				OntologyInfo oi = new OntologyInfo();
				oi.id = id2;
				oi.projectName = projectName2;
				modifiedOntologies.add(oi);
			}

			log.debug("Modified ontologies:" + modifiedOntologies.toString());
		} catch (CoreException e) {
			log.error(e, e);
		}
	}

	private void runAction() {
		final IWorkbench workbench = Activator.getDefault().getWorkbench();
		final IWorkbenchWindow workbenchWindow = workbench
				.getActiveWorkbenchWindow();
		final IWorkbenchPage page = workbenchWindow.getActivePage();

		try {
			workbench.showPerspective(
					OWLDiffPerspectiveFactory.OWLDIFFPERSPECTIVE_ID,
					workbenchWindow);
			OWLDiffView owlDiffView = (OWLDiffView) page.showView(
					OWLDiffView.ID, OWLDiffView.SECONDARY_ID + i,
					IWorkbenchPage.VIEW_ACTIVATE);
			owlDiffView.getDiffView().openFiles(u1, u2);
			owlDiffView.setPartName("OWLDiffView" + i);
			i++;
		} catch (PartInitException e) {
			log.error(e, e);
		} catch (WorkbenchException e) {
			log.error(e, e);
		}
	}

	private boolean ontologiesSaved() {
		int result = SWT.YES;
		if (!modifiedOntologies.isEmpty()) {
			MessageBox mb = new MessageBox(Display.getCurrent()
					.getActiveShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
			StringBuffer message = new StringBuffer("The following ontolog")
					.append((modifiedOntologies.size() == 1) ? "y" : "ies")
					.append(" will be saved before OWLDiff processing:\n\n");
			for (OntologyInfo oi : modifiedOntologies) {
				message.append(oi.id).append("\n");
			}
			message = message.append("\nProceed?");
			mb.setMessage(message.toString());
			result = mb.open();
			if (result == SWT.YES) {
				saveModifiedOntologies();
			}
		}
		return (result == SWT.YES);
	}

	private void saveModifiedOntologies() {
		for (OntologyInfo oi : modifiedOntologies) {
			IOntologyContainer container = DatamodelPlugin.getDefault()
					.getContainer(oi.projectName);
			Ontology onto;
			try {
				onto = container.getConnection().getOntology(oi.id);
				String physicalURI = onto.getPhysicalURI();
				if (container.getConnection().getOntologyResolver() instanceof DefaultOntologyResolver) {
					physicalURI = ((DefaultOntologyResolver) container
							.getConnection().getOntologyResolver())
							.getReplacement(onto.getOntologyURI());
				}
				String format = DatamodelPlugin.getDefault()
						.getProjectDefaultOntologyFileFormats(oi.projectName)
						.get(0);
				if (onto.getPhysicalURI().equals(physicalURI)) {
					String format2 = onto.getOntologyFormatting()
							.getFormatName();
					if (!format2.equals(OntoBrokerOntologyFileFormat.F_LOGIC)) {
						format = format2;
					}
				}
				onto.saveOntology(format, physicalURI,
						SerializationConstants.ENCODING_UTF);
				container.setDirty(oi.id, false);
			} catch (KAON2Exception e1) {
				log.error(e1, e1);
			} catch (IOException e) {
				log.error(e, e);
			} catch (InterruptedException e) {
				log.error(e, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		log.debug("setActivePart: " + action + ", " + targetPart);
	}

	/**
	 * {@inheritDoc}
	 */
	public void run(IAction action) {
		modifiedOntologies.clear();
		analyzeOntologyURIs();
		if (ontologiesSaved()) {
			runAction();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		log.debug("action: " + action + ", selection=" + selection);
		this.s = (IStructuredSelection) selection;
	}
}
