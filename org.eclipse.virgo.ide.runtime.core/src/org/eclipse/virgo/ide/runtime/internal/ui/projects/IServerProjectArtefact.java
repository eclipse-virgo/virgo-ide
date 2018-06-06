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

import org.eclipse.virgo.ide.runtime.core.artefacts.ILocalArtefact;

/**
 *
 * @author Miles Parker
 *
 */
public interface IServerProjectArtefact {

    IServerProjectContainer getContainer();

    public ILocalArtefact getArtefact();
}
