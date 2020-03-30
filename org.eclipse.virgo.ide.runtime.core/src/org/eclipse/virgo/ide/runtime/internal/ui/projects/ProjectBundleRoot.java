/*********************************************************************
 * Copyright (c) 2009, 2012 SpringSource, a division of VMware, Inc.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.runtime.internal.ui.projects;

import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.internal.core.ExternalPackageFragmentRoot;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.virgo.ide.runtime.core.artefacts.ILocalArtefact;

public class ProjectBundleRoot extends ExternalPackageFragmentRoot implements IServerProjectArtefact {

    private final ILocalArtefact artefact;

    private final ProjectBundleContainer container;

    // Override to allow constructor access for jar package
    public ProjectBundleRoot(ProjectBundleContainer container, ILocalArtefact artefact) {
        super(new Path(artefact.getFile().getAbsolutePath()), (JavaProject) container.getJavaProject());
        this.container = container;
        this.artefact = artefact;
    }

    public IServerProjectContainer getContainer() {
        return this.container;
    }

    public ILocalArtefact getArtefact() {
        return this.artefact;
    }
}