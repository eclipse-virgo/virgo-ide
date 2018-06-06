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

import org.eclipse.virgo.ide.bundlerepository.domain.OsgiVersion;

/**
 * Represents an artefact in the repository :- bundle or library
 *
 * @author adriancolyer
 * @author Miles Parker
 *
 */
public interface IArtefact extends IArtefactTyped {

    /**
     * The human-readable name of the artefact
     */
    String getName();

    /**
     * The symbolic name of the artefact
     */
    String getSymbolicName();

    /**
     * The version of the artefact
     */
    OsgiVersion getVersion();

    /**
     * The organisation name as used in ivy.xml (groupId name for maven)
     */
    String getOrganisationName();

    /**
     * The module name as used in ivy.xml (artefactId for maven)
     */
    String getModuleName();

    /**
     * Is source code available for this artefact?
     */
    boolean isSourceAvailable();

    /**
     * Set if source code is available
     */
    void setSourceAvailable(boolean sourceAvailable);

    /**
     * Get the relative URL path for downloading this artefact from S3
     */
    String getRelativeUrlPath();

    /**
     * Get the relative URL path for displaying the license file for this artefact
     */
    String getRelativeLicenseUrlPath();

    /**
     * Returns a string that identifies this artefact as completely matching a given requirement. At minimum, this
     * should use name, version and artefact type.
     */
    String getSignature();

    /**
     * Does the artefact match the supplied artefact? Matching artefacts are expected to share the same signature.
     */
    boolean isMatch(IArtefact artefact);

    void setSet(ArtefactSet set);

    ArtefactSet getSet();

    void setRepository(ArtefactRepository repository);

    ArtefactRepository getRepository();
}