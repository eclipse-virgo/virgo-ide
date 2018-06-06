/*********************************************************************
 * Copyright (c) 2009, 2012 SpringSource, a division of VMware, Inc.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.runtime.core.artefacts;

/**
 *
 * @author Miles Parker
 *
 */
public interface IArtefactTyped {

    /**
     * What type of artefact is this?
     */
    public abstract ArtefactType getArtefactType();

}