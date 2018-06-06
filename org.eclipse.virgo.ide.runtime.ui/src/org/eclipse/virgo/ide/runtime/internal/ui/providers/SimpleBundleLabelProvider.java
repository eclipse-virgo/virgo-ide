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

import org.eclipse.virgo.ide.runtime.core.artefacts.ILocalArtefact;
import org.eclipse.virgo.ide.runtime.internal.ui.projects.IServerProjectArtefact;

/**
 * @author Miles Parker
 */
public class SimpleBundleLabelProvider extends RuntimeLabelProvider {

    @Override
    public String getText(Object element) {
        if (element instanceof IServerProjectArtefact) {

            ILocalArtefact artefact = ((IServerProjectArtefact) element).getArtefact();

            StringBuilder sb = new StringBuilder();
            sb.append(artefact.getSymbolicName()).append(' ').append(artefact.getVersion());
            return sb.toString();
        }
        return super.getText(element);
    }
}
