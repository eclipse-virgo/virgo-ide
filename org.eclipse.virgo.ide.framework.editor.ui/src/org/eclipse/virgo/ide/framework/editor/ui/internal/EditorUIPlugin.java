/*******************************************************************************
 * Copyright (c) 2009, 2011 SpringSource, a divison of VMware, Inc. and others
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     SAP AG - initial API and implementation
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.virgo.ide.framework.editor.ui.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Kaloyan Raev
 */
public class EditorUIPlugin extends AbstractUIPlugin {
	
	public static final String PLUGIN_ID = "org.eclipse.virgo.ide.framework.editor";

	public EditorUIPlugin() {
		super();
	}

	/**
	 * Constructs new IStatus object from the given String message. 
	 * 
	 * @param message the message to create the IStatus object from. 
	 */
	public static IStatus newErrorStatus(String message) {
		return new Status(IStatus.ERROR, PLUGIN_ID, message);
	}

	/**
	 * Constructs new IStatus object from the given Throwable object. 
	 * 
	 * @param t the Throwable object to create the IStatus message from. 
	 */
	public static IStatus newErrorStatus(Throwable t) {
		return new Status(IStatus.ERROR, PLUGIN_ID, t.getMessage(), t);
	}
	
	/**
	 * Logs the specified status with this plug-in's log.
	 * 
	 * @param status status to log
	 */
	public static void log(IStatus status) {
		Bundle bdl = FrameworkUtil.getBundle(EditorUIPlugin.class);
		ILog log = bdl==null ? null : Platform.getLog(bdl);
		if (log!=null) log.log(status);
	}
	
	/**
	 * Logs the specified throwable with this plug-in's log.
	 * 
	 * @param t throwable to log
	 */
	public static void log(Throwable t) {
		if (t instanceof CoreException) {
			log(((CoreException) t).getStatus());
		} else {
			log(newErrorStatus(t));
		}
	}

}
