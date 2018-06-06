/*********************************************************************
 * Copyright (c) 2009, 2012 SpringSource, a division of VMware, Inc.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.facet.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * Uninstall delegate to remove the class path container.
 *
 * @author Christian Dupuis
 * @author Miles Parker
 * @since 1.0.0
 */
public class BundleFacetUninstallDelegate implements IDelegate {

    public void execute(IProject project, IProjectFacetVersion fv, Object config, IProgressMonitor monitor) throws CoreException {
        IJavaProject jproj = JavaCore.create(project);
        removeFromClasspath(jproj, JavaCore.newContainerEntry(FacetCorePlugin.CLASSPATH_CONTAINER_PATH), monitor);
        IProjectDescription desc = project.getDescription();
        List<String> natures = new ArrayList<String>(Arrays.asList(desc.getNatureIds()));
        natures.remove(FacetCorePlugin.BUNDLE_NATURE_ID);
        desc.setNatureIds(natures.toArray(new String[] {}));
        project.setDescription(desc, monitor);
    }

    protected void removeFromClasspath(IJavaProject javaProject, IClasspathEntry entry, IProgressMonitor monitor) throws CoreException {
        Set<IClasspathEntry> entries = new LinkedHashSet<IClasspathEntry>();
        for (IClasspathEntry existingEntry : javaProject.getRawClasspath()) {
            if (!existingEntry.equals(entry)) {
                entries.add(existingEntry);
            }
        }
        javaProject.setRawClasspath(entries.toArray(new IClasspathEntry[entries.size()]), monitor);
    }
}
