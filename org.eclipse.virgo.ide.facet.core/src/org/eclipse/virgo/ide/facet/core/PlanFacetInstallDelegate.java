/*********************************************************************
 * Copyright (c) 2010 SpringSource, a division of VMware, Inc.
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
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * Empty install delegate for the plan facet.
 *
 * @author Christian Dupuis
 * @since 2.3.1
 */
public class PlanFacetInstallDelegate implements IDelegate {

    public void execute(IProject project, IProjectFacetVersion fv, Object config, IProgressMonitor monitor) throws CoreException {
        IProjectDescription desc = project.getDescription();
        List<String> natures = new ArrayList<String>(Arrays.asList(desc.getNatureIds()));
        natures.add(FacetCorePlugin.PLAN_NATURE_ID);
        desc.setNatureIds(natures.toArray(new String[] {}));
        project.setDescription(desc, monitor);

    }
}
