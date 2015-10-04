/*******************************************************************************
 * Copyright (c) 2009, 2012 SpringSource, a divison of VMware, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     SpringSource, a division of VMware, Inc. - initial API and implementation
 *******************************************************************************/

package org.eclipse.virgo.ide.runtime.internal.ui.filters;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.virgo.ide.runtime.core.artefacts.LocalLibraryArtefact;
import org.eclipse.virgo.ide.runtime.internal.ui.projects.ProjectFileContainer;
import org.eclipse.virgo.ide.runtime.internal.ui.projects.ProjectFileReference;

/**
 *
 * @author Miles Parker
 *
 */
public class LibraryArtefactFilter extends ViewerFilter {

    private static final String VIRGO_LIB_EXT = "libd";

    /**
     * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object,
     *      java.lang.Object)
     */
    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        return !(element instanceof ProjectFileContainer) && !(element instanceof ProjectFileReference) && !(element instanceof LocalLibraryArtefact)
            && !(element instanceof IFile && ((IFile) element).getFileExtension().equals(VIRGO_LIB_EXT));
    }

}
