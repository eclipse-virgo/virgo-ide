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
import java.net.URI;

import org.eclipse.virgo.ide.bundlerepository.domain.OsgiVersion;

/**
 * An extension to {@link BundleArtefact} to take some more information of local bundles.
 *
 * @author Christian Dupuis
 * @since 1.0.0
 */
public class LocalBundleArtefact extends BundleArtefact implements ILocalArtefact {

    private final boolean hasDownloadedSources;

    private final File file;

    public LocalBundleArtefact(String name, String symbolicName, OsgiVersion version, boolean hasDownloadedSources, URI fileURI) {
        super(name, symbolicName, version, symbolicName, symbolicName);
        this.hasDownloadedSources = hasDownloadedSources;
        this.file = fileURI != null ? new File(fileURI) : null;
    }

    public boolean isSourceDownloaded() {
        return this.hasDownloadedSources;
    }

    @Override
    public String getSignature() {
        String signature = super.getSignature();
        if (this.hasDownloadedSources) {
            signature += "+Source";
        }
        return signature;
    }

    public File getFile() {
        return this.file;
    }

}