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

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.server.core.IServer;

/**
 *
 * @author Miles Parker
 *
 */
public class ServerFile {

    protected final IFile file;

    private final IServer server;

    public ServerFile(IServer server, IFile file) {
        this.server = server;
        this.file = file;
    }

    public IFile getFile() {
        return this.file;
    }

    public IServer getServer() {
        return this.server;
    }
}