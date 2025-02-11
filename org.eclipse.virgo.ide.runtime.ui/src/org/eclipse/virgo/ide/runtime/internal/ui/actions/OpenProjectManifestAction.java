/*********************************************************************
 * Copyright (c) 2009, 2012 SpringSource, a division of VMware, Inc.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.runtime.internal.ui.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.virgo.ide.facet.core.FacetCorePlugin;
import org.eclipse.virgo.ide.facet.core.FacetUtils;
import org.eclipse.virgo.ide.manifest.core.BundleManifestCorePlugin;
import org.eclipse.virgo.ide.manifest.core.BundleManifestUtils;
import org.eclipse.virgo.ide.pde.core.internal.Helper;
import org.eclipse.virgo.ide.runtime.core.ServerCorePlugin;
import org.eclipse.wst.common.project.facet.core.FacetedProjectFramework;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.ui.internal.view.servers.ModuleServer;

/**
 * Action implementation that opens a MANIFEST.MF or par.xml file of the selected module.
 *
 * @author Christian Dupuis
 * @since 1.0.0
 */
@SuppressWarnings("restriction")
public class OpenProjectManifestAction implements IObjectActionDelegate {

    private IModule selectedModule;

    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        // nothing to do here
    }

    public void run(IAction action) {
        IProject project = this.selectedModule.getProject();
        if (Helper.forcePDEEditor(project) != null) {
            openResource(Helper.forcePDEEditor(project));
        } else if (FacetUtils.isBundleProject(project)) {
            openResource(BundleManifestUtils.locateManifest(JavaCore.create(project), false));
        } else if (FacetUtils.isParProject(project)) {
            openResource(project.findMember(BundleManifestCorePlugin.MANIFEST_FILE_LOCATION));
        } else {
            try {
                if (FacetUtils.hasNature(project, JavaCore.NATURE_ID)
                    && FacetedProjectFramework.hasProjectFacet(project, FacetCorePlugin.WEB_FACET_ID)) {
                    openResource(BundleManifestUtils.locateManifest(JavaCore.create(project), false));
                }
            } catch (CoreException e) {
                StatusManager.getManager().handle(
                    new Status(IStatus.ERROR, ServerCorePlugin.PLUGIN_ID, "Problem occurred while openeing project manifest.", e));
            }
        }
    }

    private void openResource(IResource resource) {
        if (resource instanceof IFile) {
            try {
                IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), (IFile) resource);
            } catch (PartInitException e) {
                StatusManager.getManager().handle(
                    new Status(IStatus.ERROR, ServerCorePlugin.PLUGIN_ID, "Problem occurred while opening project manifest.", e));
            }
        }
    }

    public void selectionChanged(IAction action, ISelection selection) {
        this.selectedModule = null;
        if (!selection.isEmpty()) {
            if (selection instanceof IStructuredSelection) {
                Object obj = ((IStructuredSelection) selection).getFirstElement();
                if (obj instanceof ModuleServer) {
                    ModuleServer ms = (ModuleServer) obj;
                    this.selectedModule = ms.module[ms.module.length - 1];
                }
            }
        }
    }

}
