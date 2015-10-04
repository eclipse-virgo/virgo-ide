/*******************************************************************************
 * Copyright (c) 2009, 2012 SpringSource, a divison of VMware, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     SpringSource, a division of VMware, Inc. - initial API and implementation
 *******************************************************************************/

package org.eclipse.virgo.ide.ui.internal.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.virgo.ide.facet.core.FacetCorePlugin;
import org.eclipse.virgo.ide.facet.core.FacetUtils;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

/**
 * Menu action to add or remove the SpringSource AP bundle project nature/facet from a given project.
 *
 * @author Christian Dupuis
 * @since 1.0.0
 */
public class ConvertToBundleProject implements IObjectActionDelegate {

    private final List<IProject> selected = new ArrayList<IProject>();

    public void run(IAction action) {
        Iterator<IProject> iter = this.selected.iterator();
        while (iter.hasNext()) {
            IProject project = iter.next();
            if (FacetUtils.isBundleProject(project)) {
                removeFacetsFromProject(project);
            } else {
                addFacetsToProject(project);
            }
        }
    }

    public void selectionChanged(IAction action, ISelection selection) {
        this.selected.clear();
        if (selection instanceof IStructuredSelection) {
            boolean enabled = true;
            Iterator<?> iter = ((IStructuredSelection) selection).iterator();
            while (iter.hasNext()) {
                Object obj = iter.next();
                if (obj instanceof IJavaProject) {
                    obj = ((IJavaProject) obj).getProject();
                }
                if (obj instanceof IProject) {
                    IProject project = (IProject) obj;
                    if (!project.isOpen()) {
                        enabled = false;
                        break;
                    } else {
                        this.selected.add(project);
                    }
                } else {
                    enabled = false;
                    break;
                }
            }
            action.setEnabled(enabled);
        }
    }

    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
    }

    private void addFacetsToProject(final IProject project) {
        IWorkspaceRunnable oper = new IWorkspaceRunnable() {

            public void run(IProgressMonitor monitor) throws CoreException {
                IFacetedProject fProject = ProjectFacetsManager.create(project, true, monitor);
                fProject.installProjectFacet(ProjectFacetsManager.getProjectFacet(FacetCorePlugin.BUNDLE_FACET_ID).getDefaultVersion(), null,
                    monitor);
            }
        };

        try {
            ResourcesPlugin.getWorkspace().run(oper, new NullProgressMonitor());
        } catch (CoreException e) {
        }
    }

    private void removeFacetsFromProject(final IProject project) {
        IWorkspaceRunnable oper = new IWorkspaceRunnable() {

            public void run(IProgressMonitor monitor) throws CoreException {
                IFacetedProject fProject = ProjectFacetsManager.create(project, true, monitor);
                fProject.uninstallProjectFacet(ProjectFacetsManager.getProjectFacet(FacetCorePlugin.BUNDLE_FACET_ID).getDefaultVersion(), null,
                    monitor);
            }
        };

        try {
            ResourcesPlugin.getWorkspace().run(oper, new NullProgressMonitor());
        } catch (CoreException e) {
        }
    }
}
