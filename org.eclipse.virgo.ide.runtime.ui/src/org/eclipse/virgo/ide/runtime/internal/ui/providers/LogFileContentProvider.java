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

import org.eclipse.virgo.ide.runtime.core.IServerRuntimeProvider;
import org.eclipse.virgo.ide.runtime.internal.core.runtimes.RuntimeProviders;
import org.eclipse.virgo.ide.runtime.internal.ui.projects.ServerProject;
import org.eclipse.wst.server.core.IServer;

/**
 *
 * @author Miles Parker
 *
 */
public class LogFileContentProvider extends ServerFileContentProvider {

    public static final String[] LOG_INCLUDE_EXTS = new String[] { "log" };

    /**
     * @see org.eclipse.virgo.ide.runtime.internal.ui.providers.ServerFileContentProvider#getServerDirectories(org.eclipse.wst.server.core.IServer)
     */
    @Override
    public String[] getServerDirectories(IServer server) {
        IServerRuntimeProvider provider = RuntimeProviders.getRuntimeProvider(server.getRuntime());
        return provider.getServerLogDirectories();
    }

    /**
     * @see org.eclipse.virgo.ide.runtime.internal.ui.providers.ServerFileContentProvider#getBaseDirectory()
     */
    @Override
    public String getBaseDirectory() {
        return ServerProject.LOG_WORKSPACE_DIR;
    }

    /**
     * @see org.eclipse.virgo.ide.runtime.internal.ui.providers.ServerFileContentProvider#getIncludeExtensions()
     */
    @Override
    public String[] getIncludeExtensions() {
        return LOG_INCLUDE_EXTS;
    }

    /**
     * @see org.eclipse.virgo.ide.runtime.internal.ui.providers.ServerFileContentProvider#getExcludeExtensions()
     */
    @Override
    public String[] getExcludeExtensions() {
        return null;
    }

    /**
     * @see org.eclipse.virgo.ide.runtime.internal.ui.providers.ServerFileContentProvider#isIncludeNoExtension()
     */
    @Override
    public boolean isIncludeNoExtension() {
        return false;
    }
}
