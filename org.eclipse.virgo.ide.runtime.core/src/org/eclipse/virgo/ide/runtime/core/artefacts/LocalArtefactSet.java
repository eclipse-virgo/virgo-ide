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

import java.io.File;

import org.eclipse.virgo.ide.runtime.core.ServerUtils;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Miles Parker
 */
public class LocalArtefactSet extends ArtefactSet implements ILocalEntity {

    final File location;

    public LocalArtefactSet(ArtefactRepository artefactRepository, ArtefactType artefactType, File location) {
        super(artefactRepository, artefactType);
        this.location = location;
    }

    public File getFile() {
        return this.location;
    }

    public String getRelativePath() {
        String fileName = getFile().toString();
        IServer server = getRepository().getServer();
        if (server != null && server.getRuntime() != null) {
            String home = ServerUtils.getServerHome(server.getRuntime());
            if (fileName.startsWith(home)) {
                fileName = fileName.substring(home.length() + 1);
            }
        }
        return fileName;
    }

    public String getShortLabel() {
        return getRelativePath() + " [" + getArtefactType().getPluralLabel() + "]";
    }

    /**
     * @see org.eclipse.virgo.ide.runtime.core.artefacts.ArtefactSet#toString()
     */
    @Override
    public String toString() {
        return this.location + " [" + super.toString() + "]";
    }
}
