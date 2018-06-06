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

import org.apache.commons.lang.ObjectUtils;

/**
 *
 * @author Miles Parker
 *
 */
public class LocalArtefactRepository extends ArtefactRepository implements ILocalEntity {

    private final File file;

    public LocalArtefactRepository(File file) {
        this.file = file;
        this.bundles = createArtefactSet(ArtefactType.BUNDLE, file);
        this.libraries = createArtefactSet(ArtefactType.LIBRARY, file);
        this.allArtefacts = new LocalArtefactSet(this, ArtefactType.COMBINED, file);
    }

    protected ArtefactSet createArtefactSet(ArtefactType type, File file) {
        return new LocalArtefactSet(this, type, file) {

            @Override
            public boolean add(IArtefact artefact) {
                return super.add(artefact) && LocalArtefactRepository.this.allArtefacts.add(artefact);
            }
        };
    }

    /**
     * @see org.eclipse.virgo.ide.runtime.core.artefacts.ILocalEntity#getFile()
     */
    public File getFile() {
        return this.file;
    }

    /**
     * Assumes one and only one repository at each file.
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof LocalArtefactRepository) {
            LocalArtefactRepository otherRepos = (LocalArtefactRepository) other;
            return ObjectUtils.equals(this.file, otherRepos.file);
        }
        return false;
    }
}
