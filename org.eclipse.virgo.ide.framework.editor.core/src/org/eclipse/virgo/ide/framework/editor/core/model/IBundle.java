/*******************************************************************************
 * Copyright (c) 2011 SAP AG
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package org.eclipse.virgo.ide.framework.editor.core.model;

import java.util.Map;
import java.util.Set;

import org.eclipse.virgo.ide.framework.editor.core.IOSGiFrameworkAdmin;
import org.osgi.framework.Bundle;

/**
 * This interface represents a bundle and its state in an OSGi framework. It is
 * used by the <i>Bundle Information</i> and <i>Bundle Dependency Graph</i>
 * editor parts to display the state of the represented bundle.
 * 
 * <p>
 * Objects implementing this interface are returned as a map by the
 * {@link IOSGiFrameworkAdmin#getBundles()} method that is called by the editor
 * parts. These objects are provided by the server runtime that implements the
 * {@link IOSGiFrameworkAdmin} interface.
 * </p>
 * 
 * @noextend This interface is not intended to be extended by clients.
 * 
 * @see IOSGiFrameworkAdmin#getBundles()
 * 
 * @author Kaloyan Raev
 */
public interface IBundle {

	/**
	 * Returns the unique identifier of this bundle.
	 * 
	 * @return The String representation of the id number.
	 * 
	 * @see Bundle#getBundleId()
	 */
	public String getId();

	/**
	 * Returns the symbolic name of this bundle.
	 * 
	 * @return The symbolic name of this bundle or {@code null} if this bundle
	 *         does not have a symbolic name.
	 * 
	 * @see Bundle#getSymbolicName()
	 */
	public String getSymbolicName();

	/**
	 * Returns the version of this bundle.
	 * 
	 * @return The String representation of the version number.
	 * 
	 * @see Bundle#getVersion()
	 */
	public String getVersion();

	/**
	 * Returns the current state of this bundle.
	 * 
	 * @return The String representation of the state.
	 * 
	 * @see Bundle#getVersion()
	 */
	public String getState();

	/**
	 * Returns the location identifier of this bundle.
	 * 
	 * @return The String representation of the location identifier.
	 * 
	 * @see Bundle#getLocation()
	 */
	public String getLocation();

	/**
	 * Returns the Manifest headers and values of this bundle.
	 * 
	 * @return A {@code Map} object containing the Manifest headers and values.
	 *         This method never returns {@code null}.
	 * 
	 * @see Bundle#getHeaders()
	 */
	public Map<String, String> getHeaders();

	/**
	 * Returns all packages exported by this bundle.
	 * 
	 * @return A {@code Set} object containing all exported packages represented
	 *         as {@code IPackageExport} objects. This method never returns
	 *         {@code null}.
	 * 
	 * @see IPackageExport
	 */
	public Set<IPackageExport> getPackageExports();

	/**
	 * Returns the packages imported by this bundle.
	 * 
	 * @return A {@code Set} object containing all imported packages represented
	 *         as {@code IPackageImport} objects. This method never returns
	 *         {@code null}.
	 * 
	 * @see IPackageImport
	 */
	public Set<IPackageImport> getPackageImports();

	/**
	 * Returns all services registered by this bundle.
	 * 
	 * @return A {@code Set} object containing all registered services
	 *         represented as {@code IServiceReference} objects. This method
	 *         never returns {@code null}.
	 * 
	 * @see Bundle#getRegisteredServices()
	 * @see IServiceReference
	 */
	public Set<IServiceReference> getRegisteredServices();

	/**
	 * Returns all services this bundle is using.
	 * 
	 * @return A {@code Set} object containing all services in use represented
	 *         as {@code IServiceReference} objects. This method never returns
	 *         {@code null}.
	 * 
	 * @see Bundle#getServicesInUse()
	 * @see IServiceReference
	 */
	public Set<IServiceReference> getServicesInUse();

}
