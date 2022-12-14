/*********************************************************************
 * Copyright (c) 2009 - 2012 SpringSource, a division of VMware, Inc.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.module.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.virgo.ide.facet.core.FacetCorePlugin;
import org.eclipse.virgo.ide.facet.core.FacetUtils;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.model.ModuleDelegate;
import org.eclipse.wst.server.core.util.ProjectModuleFactoryDelegate;

/**
 * {@link ProjectModuleFactoryDelegate} extension that knows how to handle par and bundle projects.
 *
 * @author Christian Dupuis
 * @since 1.0.0
 */
public class ServerModuleFactoryDelegate extends ProjectModuleFactoryDelegate {

    public static final String MODULE_FACTORY_ID = "org.eclipse.virgo.server.modulefactory";

    /**
     * {@inheritDoc}
     */
    @Override
    public ModuleDelegate getModuleDelegate(IModule module) {
        return new ServerModuleDelegate(module.getProject());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IModule[] createModules(final IProject project) {
        final Set<IModule> modules = new HashSet<IModule>();
        if (FacetUtils.isBundleProject(project)) {

            // Add module for bundle deployment
            modules.add(createModule(project.getName(), project.getName(), FacetCorePlugin.BUNDLE_FACET_ID, "1.0", project));

            // Add module for par deployment
            for (IProject parProject : FacetUtils.getParProjects(project)) {
                modules.add(
                    createModule(parProject.getName() + "$" + project.getName(), project.getName(), FacetCorePlugin.BUNDLE_FACET_ID, "1.0", project));
            }

        } else if (FacetUtils.isParProject(project)) {
            modules.add(createModule(project.getName(), project.getName(), FacetCorePlugin.PAR_FACET_ID, "1.0", project));
        }

        // Every project can also be a plan project
        if (FacetUtils.isPlanProject(project)) {
            Collection<IFile> files = FacetUtils.getPlansInPlanProject(project);
            for (IFile resource : files) {
                modules.add(createModule(resource.getFullPath().toString(),
                    resource.getProject().getName() + "/" + resource.getProjectRelativePath().toString(), FacetCorePlugin.PLAN_FACET_ID, "2.0",
                    project));
            }
        }
        return modules.toArray(new IModule[modules.size()]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IPath[] getListenerPaths() {
        final List<IPath> paths = new ArrayList<IPath>();
        for (IModule module : getModules()) {
            if (FacetUtils.isPlanProject(module.getProject())) {
                try {
                    module.getProject().accept(new IResourceVisitor() {

                        public boolean visit(IResource resource) throws CoreException {
                            if (resource instanceof IFile && resource.getName().endsWith(".plan")) {
                                paths.add(resource.getProjectRelativePath());
                            }
                            return true;
                        }
                    });
                } catch (CoreException e) {
                }

            }
        }
        paths.add(new Path(".settings/org.eclipse.wst.common.project.facet.core.xml"));
        paths.add(new Path(".project"));
        paths.add(new Path(".settings"));
        paths.add(new Path(".classpath"));
        return paths.toArray(new IPath[paths.size()]);
    }

}
