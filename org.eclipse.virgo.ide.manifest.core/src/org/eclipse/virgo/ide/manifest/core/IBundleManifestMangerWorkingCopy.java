/*********************************************************************
 * Copyright (c) 2009, 2012 SpringSource, a division of VMware, Inc.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.manifest.core;

import java.util.Set;

import org.eclipse.jdt.core.IJavaProject;

/**
 * Extension interface for {@link IBundleManifestManager} that allows write access for certain managed resources.
 *
 * @author Christian Dupuis
 * @since 1.0.0
 */
public interface IBundleManifestMangerWorkingCopy extends IBundleManifestManager {

    /**
     * Sets the resolved package imports for the given <code>javaProject</code>.
     */
    void updateResolvedPackageImports(IJavaProject javaProject, Set<String> resolvedPackageImports);

}
