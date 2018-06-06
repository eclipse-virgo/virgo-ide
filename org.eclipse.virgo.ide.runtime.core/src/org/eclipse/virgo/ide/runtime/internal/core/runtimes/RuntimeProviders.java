/*********************************************************************
 * Copyright (c) 2009 - 2012 SpringSource, a division of VMware, Inc.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.runtime.internal.core.runtimes;

import org.eclipse.virgo.ide.runtime.core.IServerRuntimeProvider;
import org.eclipse.wst.server.core.IRuntime;

/**
 * Utility that loads {@link IServerRuntimeProvider}s based on given version identifiers.
 *
 * @author Christian Dupuis
 * @author Leo Dos Santos
 * @author Miles Parker
 * @since 1.0.0
 */
public class RuntimeProviders {

    public static final VirgoRuntimeProvider[] ALL_HANDLERS = new VirgoRuntimeProvider[] { Virgo21_30Provider.INSTANCE, Virgo35Provider.INSTANCE,
        Virgo37Provider.INSTANCE };

    public static IServerRuntimeProvider getRuntimeProvider(IRuntime runtime) {
        if (runtime != null) {
            for (VirgoRuntimeProvider version : RuntimeProviders.ALL_HANDLERS) {
                if (version.isHandlerFor(runtime)) {
                    return version;
                }
            }
        }
        return InvalidRuntimeProvider.INSTANCE;
    }
}
