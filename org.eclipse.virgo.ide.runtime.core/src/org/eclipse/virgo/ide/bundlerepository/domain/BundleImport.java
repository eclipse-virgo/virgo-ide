/*******************************************************************************
 * Copyright (c) 2007, 2012 SpringSource
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     SpringSource - initial API and implementation
 *******************************************************************************/

package org.eclipse.virgo.ide.bundlerepository.domain;

import org.eclipse.virgo.ide.runtime.core.artefacts.ArtefactRepositoryManager;
import org.eclipse.virgo.ide.runtime.core.artefacts.BundleArtefact;
import org.eclipse.virgo.ide.runtime.core.artefacts.LibraryArtefact;

/**
 * Represents an bundle import in a library definition.
 *
 * @author acolyer
 */
public class BundleImport {

    private byte[] symbolicName; // matching bundle symbolic name

    private VersionRange versionRange; // acceptable range for matching versions

    private LibraryArtefact library; // the library declaring this import

    protected BundleImport() {
    } // for persistence use only

    public BundleImport(LibraryArtefact lib, String symbolicName, VersionRange versionRange) {
        this.library = lib;
        this.symbolicName = ArtefactRepositoryManager.convert(symbolicName);
        this.versionRange = versionRange;
    }

    public String getSymbolicName() {
        return this.symbolicName != null ? new String(this.symbolicName) : null;
    }

    public VersionRange getVersionRange() {
        return this.versionRange;
    }

    public LibraryArtefact getLibrary() {
        return this.library;
    }

    public boolean isSatisfiedBy(BundleArtefact bundle) {
        return this.symbolicName.equals(bundle.getSymbolicName()) && this.versionRange.contains(bundle.getVersion());
    }

    @Override
    public String toString() {
        return String.format("Import-Bundle: %s;version=\"%s\"", this.symbolicName, this.versionRange);
    }

}
