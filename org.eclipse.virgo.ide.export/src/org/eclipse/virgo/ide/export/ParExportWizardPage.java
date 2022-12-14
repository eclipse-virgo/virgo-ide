/*********************************************************************
 * Copyright (c) 2009, 2012 SpringSource, a division of VMware, Inc.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.export;

import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.virgo.ide.facet.core.FacetCorePlugin;
import org.eclipse.virgo.util.osgi.manifest.BundleManifest;
import org.eclipse.virgo.util.osgi.manifest.BundleManifestFactory;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

/**
 * Export wizard page for exporting par project
 *
 * @author Christian Dupuis
 * @author Terry Hon
 * @author Leo Dos Santos
 */
public class ParExportWizardPage extends AbstractProjectExportWizardPage {

    protected ParExportWizardPage(IStructuredSelection selection) {
        super("parExportWizardPage", selection);

        setTitle("JAR File Specification");
        setDescription("Define which PAR project should be exported into the JAR.");
    }

    @Override
    protected ViewerFilter getTreeViewerFilter() {
        return new ViewerFilter() {

            @Override
            public boolean select(Viewer viewer, Object parentElement, Object element) {
                if (element instanceof IPackageFragmentRoot) {
                    IPackageFragmentRoot root = (IPackageFragmentRoot) element;
                    return !root.isArchive() && !root.isExternal();
                } else if (element instanceof IProject) {
                    IProject project = (IProject) element;
                    try {
                        IFacetedProject facetedProject = ProjectFacetsManager.create(project);
                        if (facetedProject == null) {
                            return false;
                        }
                        return facetedProject.hasProjectFacet(ProjectFacetsManager.getProjectFacet(FacetCorePlugin.PAR_FACET_ID).getDefaultVersion());
                    } catch (CoreException e) {
                        return false;
                    }
                }
                return false;
            }
        };
    }

    @Override
    protected String getExtension() {
        return ".par";
    }

    @Override
    protected String getDestinationLabel() {
        return "PAR file:";
    }

    @Override
    protected String getSymbolicName(BundleManifest bundleManifest) {
        return bundleManifest.toDictionary().get("Application-SymbolicName");
    }

    @Override
    protected String getVersion(BundleManifest bundleManifest) {
        return bundleManifest.toDictionary().get("Application-Version");
    }

    @Override
    protected BundleManifest getBundleManifest(IProject project) {
        Path path = new Path("META-INF/MANIFEST.MF");
        IFile manifestFile = (IFile) project.findMember(path);
        if (manifestFile != null) {
            try {
                return BundleManifestFactory.createBundleManifest(new InputStreamReader(manifestFile.getContents(true)));
            } catch (IOException e) {
            } catch (CoreException e) {
            }
        }
        return BundleManifestFactory.createBundleManifest();
    }

}
