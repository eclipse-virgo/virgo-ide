/*********************************************************************
 * Copyright (c) 2009, 2012 SpringSource, a division of VMware, Inc.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.runtime.core.provisioning;

import org.eclipse.virgo.ide.runtime.core.artefacts.ArtefactRepositoryManager;
import org.eclipse.virgo.kernel.repository.BundleRepository;
import org.eclipse.wst.server.core.IRuntime;

/**
 * Implementation of this interface get notified if a {@link BundleRepository} stored in the
 * {@link ArtefactRepositoryManager} is changed.
 *
 * @author Christian Dupuis
 * @since 1.0.1
 * @see ArtefactRepositoryManager#addBundleRepositoryChangeListener(IBundleRepositoryChangeListener)
 * @see ArtefactRepositoryManager#removeBundleRepositoryChangeListener(IBundleRepositoryChangeListener)
 */
public interface IBundleRepositoryChangeListener {

    /**
     * Notifies changes to the {@link BundleRepository} of the supplied {@link IRuntime}.
     *
     * @param runtime the runtime instance which {@link BundleRepository} has changed
     */
    void bundleRepositoryChanged(IRuntime runtime);

}
