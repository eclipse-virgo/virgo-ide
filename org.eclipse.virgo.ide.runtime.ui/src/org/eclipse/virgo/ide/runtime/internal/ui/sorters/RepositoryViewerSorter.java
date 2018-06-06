/*********************************************************************
 * Copyright (c) 2009, 2012 SpringSource, a division of VMware, Inc.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.runtime.internal.ui.sorters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.virgo.ide.runtime.core.artefacts.ArtefactSet;
import org.eclipse.virgo.ide.runtime.core.artefacts.IArtefactTyped;
import org.eclipse.virgo.ide.runtime.core.artefacts.ILocalEntity;
import org.eclipse.virgo.ide.runtime.core.artefacts.LocalArtefactSet;

/**
 * @author Christian Dupuis
 * @author Miles Parker
 */
public class RepositoryViewerSorter extends ArtefactSignatureSorter {

    @Override
    public int category(Object element) {
        int category = 0;
        if (element instanceof IArtefactTyped) {
            IArtefactTyped typed = (IArtefactTyped) element;
            category = typed.getArtefactType().getOrdering();
        }
        if (element instanceof ArtefactSet) {
            category += 10;
        }
        if (category > 0) {
            return category;
        }
        return super.category(element);
    }

    @Override
    public int compare(Viewer viewer, Object e1, Object e2) {
        if (e1 instanceof LocalArtefactSet && e2 instanceof LocalArtefactSet) {
            return ((ILocalEntity) e1).getFile().compareTo(((ILocalEntity) e2).getFile());
        }
        return super.compare(viewer, e1, e2);
    }

}
