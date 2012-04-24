/*******************************************************************************
 * Copyright (c) 2009 - 2012 SpringSource, a divison of VMware, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     SpringSource, a division of VMware, Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.virgo.ide.runtime.internal.core.runtimes;

import org.eclipse.virgo.ide.runtime.core.IServerRuntimeProvider;
import org.eclipse.wst.server.core.IRuntime;

/**
 * Utility that loads {@link IServerRuntimeProvider}s based on given version
 * identifiers.
 * 
 * @author Christian Dupuis
 * @author Leo Dos Santos
 * @author Miles Parker
 * @since 1.0.0
 */
public class RuntimeProviders {

	public static final VirgoRuntimeProvider[] ALL_HANDLERS = new VirgoRuntimeProvider[] { Virgo21_30Provider.INSTANCE,
		Virgo35Provider.INSTANCE };

	public static IServerRuntimeProvider getVersionHandler(String id) {
		for (VirgoRuntimeProvider handler : ALL_HANDLERS) {
			if (handler.getID().equals(id)) {
				return handler;
			}
		}
		return null;
	}

	public static String getRuntimeID(IServerRuntimeProvider handler) {
		if (handler instanceof VirgoRuntimeProvider) {
			return ((VirgoRuntimeProvider) handler).getID();
		}
		return null;
	}

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
