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

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.virgo.ide.framework.editor.core.model.IBundle;

/**
 * The methods of this interface are called by the <i>Bundle Information</i> and
 * <i>Bundle Dependency Graph</i> editor parts to retrieve and manage the state
 * of the bundles in the OSGi framework.
 * 
 * <p>
 * The editor parts get a reference to this interface by trying to adapt the
 * {@code IServer} instance to this interface, i.e. by calling something like:
 * {@code getServer().loadAdapter(IOSGiFrameworkAdmin.class, null)}.
 * </p>
 * 
 * <p>
 * Server runtimes that integrate the <i>Bundle Information</i> and <i>Bundle
 * Dependency Graph</i> editor parts must implement this interface in order to
 * feed the editor with the required capabilities for this runtime. The easiest
 * way to make this implementation available to the editor parts is to implement
 * this interface in either the {@code ServerDelegate} or the
 * {@code ServerBehaviorDelegate} classes.
 * </p>
 * 
 * @noextend This interface is not intended to be extended by clients.
 * 
 * @author Kaloyan Raev
 */
public interface IOSGiFrameworkAdmin {

	/**
	 * Retrieves the bundle state of the OSGi framework.
	 * 
	 * @param monitor
	 *            for tracking the progress of retrieving bundle information.
	 * 
	 * @return a map of the bundle state: the key is the bundle id and the value
	 *         is an {@code IBundle} object that provides information about the
	 *         bundle state.
	 * @throws CoreException
	 *             if the operation cannot be executed successfully. The
	 *             {@code IStatus} of the exception is presented to the user in
	 *             a appropriate way.
	 * 
	 * @see IBundle
	 */
	public Map<Long, IBundle> getBundles(IProgressMonitor monitor) throws CoreException;

	/**
	 * Starts a bundle in the OSGi framework.
	 * 
	 * @param bundleId
	 *            the id of the bundle to be started.
	 * @throws CoreException
	 *             if the operation cannot be executed successfully. The
	 *             {@code IStatus} of the exception is presented to the user in
	 *             a appropriate way.
	 */
	public void startBundle(long bundleId) throws CoreException;

	/**
	 * Stops a bundle in the OSGi framework.
	 * 
	 * @param bundleId
	 *            the id of the bundle to be stopped.
	 * @throws CoreException
	 *             if the operation cannot be executed successfully. The
	 *             {@code IStatus} of the exception is presented to the user in
	 *             a appropriate way.
	 */
	public void stopBundle(long bundleId) throws CoreException;

	/**
	 * Refreshes a bundle in the OSGi framework.
	 * 
	 * @param bundleId
	 *            the id of the bundle to be refreshed.
	 * @throws CoreException
	 *             if the operation cannot be executed successfully. The
	 *             {@code IStatus} of the exception is presented to the user in
	 *             a appropriate way.
	 */
	public void refreshBundle(long bundleId) throws CoreException;

	/**
	 * Updates a bundle in the OSGi framework.
	 * 
	 * @param bundleId
	 *            the id of the bundle to be updated.
	 * @throws CoreException
	 *             if the operation cannot be executed successfully. The
	 *             {@code IStatus} of the exception is presented to the user in
	 *             a appropriate way.
	 */
	public void updateBundle(long bundleId) throws CoreException;

}
