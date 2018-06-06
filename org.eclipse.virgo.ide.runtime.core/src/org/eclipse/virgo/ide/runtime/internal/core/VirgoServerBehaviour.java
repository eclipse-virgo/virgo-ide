/*********************************************************************
 * Copyright (c) 2010 SpringSource, a division of VMware, Inc.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.runtime.internal.core;

import org.eclipse.virgo.ide.runtime.core.IServerBehaviour;

/**
 * Default Virgo server behavior.
 *
 * @author Terry Hon
 * @author Christian Dupuis
 * @since 1.0.0
 */
public class VirgoServerBehaviour extends ServerBehaviour implements IServerBehaviour {

    @Override
    public String toString() {
        return "Virgo Server";
    }

}
