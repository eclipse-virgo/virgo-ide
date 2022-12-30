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

/**
 * This interface represents a package exported by an OSGi bundle represented by
 * an {@code IBundle} object.
 * 
 * @see IBundle
 * 
 * @author Kaloyan Raev
 */
public interface IPackageExport {

	/**
	 * Returns the name of the exported package.
	 * 
	 * @return The fully qualified name of the package.
	 */
	public String getName();

	/**
	 * Returns the version of the exported package.
	 * 
	 * @return The String representation of the version.
	 */
	public String getVersion();

}
