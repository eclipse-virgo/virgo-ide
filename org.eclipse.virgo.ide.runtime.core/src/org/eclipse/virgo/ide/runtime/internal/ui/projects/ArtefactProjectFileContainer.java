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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.virgo.ide.runtime.core.artefacts.IArtefact;
import org.eclipse.virgo.ide.runtime.core.artefacts.ILocalArtefact;
import org.eclipse.virgo.ide.runtime.core.artefacts.LocalArtefactSet;

/**
 * @author Miles Parker
 */
public class ArtefactProjectFileContainer extends ProjectFileContainer {

    private final LocalArtefactSet artefactSet;

    public ArtefactProjectFileContainer(ServerProject serverProject, LocalArtefactSet artefactSet) {
        super(serverProject);
        this.artefactSet = artefactSet;
    }

    /**
     * @see org.eclipse.virgo.ide.runtime.internal.ui.projects.ProjectFileContainer#createFileReferences()
     */
    @Override
    protected ProjectFileReference[] createFileReferences() {
        List<ProjectFileReference> references = new ArrayList<ProjectFileReference>();
        createFolder(getRootFolder());
        for (IArtefact artefact : this.artefactSet.getArtefacts()) {
            if (artefact instanceof ILocalArtefact) {
                ProjectFileReference fileReference = new ProjectFileReference(this, (ILocalArtefact) artefact);
                references.add(fileReference);
            }
        }
        return references.toArray(new ProjectFileReference[0]);
    }

    /**
     * @see org.eclipse.virgo.ide.runtime.internal.ui.projects.IServerProjectContainer#getArtefactSet()
     */
    public LocalArtefactSet getArtefactSet() {
        return this.artefactSet;
    }

    /**
     * @see org.eclipse.virgo.ide.runtime.internal.ui.projects.ProjectFileContainer#getRootFolder()
     */
    @Override
    protected IFolder getRootFolder() {
        return getServerProject().getWorkspaceProject().getFolder(getArtefactSet().getRelativePath());
    }
}