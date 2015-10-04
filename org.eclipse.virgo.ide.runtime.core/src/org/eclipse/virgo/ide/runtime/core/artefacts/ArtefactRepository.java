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

package org.eclipse.virgo.ide.runtime.core.artefacts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.server.core.IServer;

/**
 * @author Christian Dupuis
 * @author Miles Parker
 * @since 2.2.7
 */
public class ArtefactRepository {

    ArtefactSet bundles;

    ArtefactSet libraries;

    ArtefactSet allArtefacts;

    List<ArtefactSet> allSets;

    IServer server;

    public ArtefactRepository() {
        this.bundles = createArtefactSet(ArtefactType.BUNDLE);
        this.libraries = createArtefactSet(ArtefactType.LIBRARY);
        this.allArtefacts = new ArtefactSet(this, ArtefactType.COMBINED);
        this.allSets = new ArrayList<ArtefactSet>();
        this.allSets.add(this.bundles);
        this.allSets.add(this.libraries);
    }

    protected ArtefactSet createArtefactSet(ArtefactType type) {
        return new ArtefactSet(this, type) {

            @Override
            public boolean add(IArtefact artefact) {
                return super.add(artefact) && ArtefactRepository.this.allArtefacts.add(artefact);
            }
        };
    }

    public Iterable<IArtefact> getBundles() {
        return this.bundles.getArtefacts();
    }

    public ArtefactSet getArtefactSet(ArtefactType artefactType) {
        if (artefactType == ArtefactType.BUNDLE) {
            return this.bundles;
        } else if (artefactType == ArtefactType.LIBRARY) {
            return this.libraries;
        }
        throw new RuntimeException("Internal error, bad artifact type: " + artefactType);
    }

    /**
     * Returns the appropriate set for the artefact. This set may or may not actually contain the supplied artefact.
     */
    public ArtefactSet getMatchingArtefactSet(IArtefactTyped artefact) {
        return getArtefactSet(artefact.getArtefactType());
    }

    /**
     * Adds the artefact to the appropriate and common set.
     */
    public void add(IArtefact artefact) {
        getMatchingArtefactSet(artefact).add(artefact);
        artefact.setRepository(this);
    }

    public ArtefactSet getLibrarySet() {
        return this.libraries;
    }

    public ArtefactSet getBundleSet() {
        return this.bundles;
    }

    public void addBundle(BundleArtefact bundle) {
        // Add to all handled through set
        this.bundles.add(bundle);
        bundle.setRepository(this);
    }

    public Iterable<IArtefact> getLibraries() {
        // Add to all handled through set
        return this.libraries.getArtefacts();
    }

    public void addLibrary(LibraryArtefact library) {
        this.libraries.add(library);
        library.setRepository(this);
    }

    public ArtefactSet getAllArtefacts() {
        return this.allArtefacts;
    }

    public List<ArtefactSet> getAllSets() {
        return this.allSets;
    }

    public boolean contains(IArtefact artefact) {
        for (IArtefact repositoryArtefact : getMatchingArtefactSet(artefact).getArtefacts()) {
            if (artefact.isMatch(repositoryArtefact)) {
                return true;
            }
        }
        return false;
    }

    public void setServer(IServer server) {
        this.server = server;
    }

    public IServer getServer() {
        return this.server;
    }
}
