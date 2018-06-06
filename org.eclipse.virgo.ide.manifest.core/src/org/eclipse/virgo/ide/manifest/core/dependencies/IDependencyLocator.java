/*********************************************************************
 * Copyright (c) 2009, 2012 SpringSource, a division of VMware, Inc.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.manifest.core.dependencies;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.virgo.kernel.osgi.provisioning.tools.DependencyLocationException;
import org.eclipse.virgo.kernel.repository.BundleDefinition;
import org.eclipse.virgo.kernel.repository.LibraryDefinition;
import org.eclipse.virgo.util.osgi.manifest.BundleManifest;

/**
 * Implementors of this interface can calculate dependencies for a given {@link BundleManifest}.
 *
 * @author Christian Dupuis
 * @since 2.0.0
 */
public interface IDependencyLocator {

    enum JavaVersion {
        Java5, Java6;
    }

    Map<File, List<String>> locateDependencies(BundleManifest manifest) throws DependencyLocationException;

    Set<? extends BundleDefinition> getBundles();

    Set<? extends LibraryDefinition> getLibraries();

    void shutdown();
}
