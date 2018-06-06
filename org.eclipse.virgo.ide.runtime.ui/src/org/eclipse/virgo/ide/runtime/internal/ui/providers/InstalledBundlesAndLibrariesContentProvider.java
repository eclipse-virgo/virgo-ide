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

import org.eclipse.virgo.ide.runtime.internal.ui.projects.IServerProjectContainer;
import org.eclipse.wst.server.core.IServer;

/**
 * Common content provider for repository installation nodes.
 *
 * @author Miles Parker
 */
public class InstalledBundlesAndLibrariesContentProvider extends RuntimeContainersContentProvider {

    @Override
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof IServer) {
            return new Object[] { new LibrariesNode((IServer) inputElement) };
        }
        if (inputElement instanceof LibrariesNode) {
            return super.getElements(((LibrariesNode) inputElement).getServer());
        }
        return super.getElements(inputElement);
    }

    @Override
    public Object getParent(Object element) {
        if (element instanceof LibrariesNode) {
            return ((LibrariesNode) element).getServer();
        }
        if (element instanceof IServerProjectContainer) {
            return new LibrariesNode(((IServerProjectContainer) element).getServer());
        }
        return super.getParent(element);
    }
}