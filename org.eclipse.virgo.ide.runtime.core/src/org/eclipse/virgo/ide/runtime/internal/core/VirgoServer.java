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

import org.eclipse.virgo.ide.runtime.core.IServer;
import org.eclipse.virgo.ide.runtime.core.IServerWorkingCopy;

/**
 * Default Virgo server implementation.
 *
 * @author Terry Hon
 * @author Christian Dupuis
 * @since 1.0.0
 */
public class VirgoServer extends Server implements IServer, IServerWorkingCopy {

    @Override
    public VirgoServerRuntime getRuntime() {
        if (getServer().getRuntime() == null) {
            return null;
        }

        return (VirgoServerRuntime) getServer().getRuntime().loadAdapter(VirgoServerRuntime.class, null);
    }

    @Override
    protected String getServerName() {
        return "Virgo Server";
    }

}
