/*******************************************************************************
 * Copyright (c) 2011 SAP AG
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package org.eclipse.virgo.ide.framework.editor.core;

import org.eclipse.core.runtime.CoreException;

/**
 * The methods of this interface are called by the <i>Server Console</i> editor
 * part to execute commands in the shell of the OSGi framework.
 * 
 * <p>
 * The editor parts get a reference to this interface by trying to adapt the
 * {@code IServer} instance to this interface, i.e. by calling something like:
 * {@code getServer().loadAdapter(IOSGiFrameworkConsole.class, null)}. .
 * </p>
 * 
 * <p>
 * Server runtimes that integrate the <i>Server Console</i> editor part must
 * implement this interface in order to feed the editor with the required
 * capabilities for this runtime. The easiest way to make this implementation
 * available to the editor parts is to implement this interface in either the
 * {@code ServerDelegate} or the {@code ServerBehaviorDelegate} classes.
 * </p>
 * 
 * @noextend This interface is not intended to be extended by clients.
 * 
 * @author Kaloyan Raev
 */
public interface IOSGiFrameworkConsole {

	/**
	 * Executes a command in the shell of the OSGi framework.
	 * 
	 * @param command
	 *            the command line to be executed.
	 * @throws CoreException
	 *             if the operation cannot be executed successfully. The
	 *             {@code IStatus} of the exception is presented to the user in
	 *             a appropriate way.
	 */
	public String executeCommand(String command) throws CoreException;

}
