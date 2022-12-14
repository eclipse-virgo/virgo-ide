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

import org.eclipse.virgo.ide.runtime.internal.ui.editor.RepositoryBrowserEditorPage;
import org.eclipse.wst.server.core.IServer;

/**
 * Common content provider for repository installation nodes.
 *
 * @author Miles Parker
 */
public class RepositoryPageContentProvider extends FlattenedArtefactsContentProvider {

    @Override
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof RepositoryBrowserEditorPage) {
            IServer server = ((RepositoryBrowserEditorPage) inputElement).getServer().getOriginal();
            return super.getElements(server);
        }
        return new Object[0];
    }

    /**
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     */
    @Override
    public boolean hasChildren(Object element) {
        return element instanceof RepositoryBrowserEditorPage;
    }
}