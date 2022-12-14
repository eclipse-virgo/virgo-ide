/*********************************************************************
 * Copyright (c) 2009, 2012 SpringSource, a division of VMware, Inc.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.runtime.internal.ui.providers;

import org.eclipse.virgo.ide.runtime.core.artefacts.ArtefactSet;
import org.eclipse.virgo.ide.runtime.internal.ui.projects.IServerProjectArtefact;
import org.eclipse.virgo.ide.runtime.internal.ui.projects.IServerProjectContainer;
import org.eclipse.virgo.ide.runtime.internal.ui.projects.ServerProject;
import org.eclipse.virgo.ide.runtime.internal.ui.projects.ServerProjectManager;
import org.eclipse.wst.server.core.IServer;

public class RuntimeContainersContentProvider extends GenericTreeProvider {

    @Override
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof IServer) {
            IServer server = (IServer) inputElement;
            if (ServerProject.isVirgo(server)) {
                ServerProject project = ServerProjectManager.getInstance().getProject(server);
                if (project != null) {
                    return project.getArtefactSets().toArray(new Object[0]);
                }
            }
        }
        if (inputElement instanceof IServerProjectContainer) {
            return ((IServerProjectContainer) inputElement).getMembers();
        }
        if (inputElement instanceof ArtefactSet) {
            ArtefactSet artefactSet = (ArtefactSet) inputElement;
            IServer server = artefactSet.getRepository().getServer();
            ServerProject project = ServerProjectManager.getInstance().getProject(server);
            if (project != null) {
                IServerProjectContainer container = project.getContainer(artefactSet);
                if (container != null) {
                    return container.getMembers();
                }
                return artefactSet.toArray();
            }
        }
        return super.getElements(inputElement);
    }

    @Override
    public Object getParent(Object element) {
        if (element instanceof IServerProjectContainer) {
            return ((IServerProjectContainer) element).getServer();
        }
        if (element instanceof IServerProjectArtefact) {
            return ((IServerProjectArtefact) element).getContainer();
        }
        return null;
    }

    public IServer getServer(Object element) {
        if (element instanceof IServer) {
            return (IServer) element;
        }
        if (element instanceof IServerProjectContainer) {
            return ((IServerProjectContainer) element).getServer();
        }
        if (element instanceof IServerProjectArtefact) {
            return ((IServerProjectArtefact) element).getContainer().getServer();
        }
        return null;
    }
}
