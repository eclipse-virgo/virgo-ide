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

import org.eclipse.wst.server.core.IServer;

/**
 * Represents a node (may or may not be expressed directly in tree) to represent all libraries (e.g. not on a
 * per-repository basis) within a given server installation.
 *
 * @author Miles Parker
 *
 */
public class LibrariesNode {

    private final IServer server;

    public LibrariesNode(IServer server) {
        super();
        this.server = server;
    }

    public IServer getServer() {
        return this.server;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object other) {
        return other instanceof LibrariesNode && ((LibrariesNode) other).server.equals(this.server);
    }
}