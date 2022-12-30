/*******************************************************************************
 * Copyright (c) 2011 SAP AG
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     SAP AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.virgo.ide.framework.editor.core.model;

import java.util.Map;
import java.util.Set;

/**
 * This interface represents an OSGi service that is either registered or in use
 * by an OSGi bundle represented by an {@code IBundle} object.
 * 
 * @see IBundle
 * 
 * @author Kaloyan Raev
 */
public interface IServiceReference {

	/**
	 * The type of the reference between the bundle and the service.
	 */
	public enum Type {

		/**
		 * Means that the service is in use by the bundle.
		 */
		IN_USE,

		/**
		 * Means that the service is registered by the bundle.
		 */
		REGISTERED
	}

	/**
	 * Returns the type of the reference between this service and the
	 * corresponding bundle.
	 * 
	 * @return An element of {@link Type#IN_USE} or {@link Type#REGISTERED}.
	 *         This method never returns {@code null}.
	 * 
	 * @see Type
	 */
	public Type getType();

	/**
	 * Returns the unique identifier of the bundle that registered this service.
	 * 
	 * @return A {@code Long} object representing the bundle id. This method
	 *         never returns {@code null}.
	 */
	public Long getBundleId();

	/**
	 * Returns the class names under which this service can be located.
	 * 
	 * @return An array of String objects representing the different class
	 *         names. This method never returns {@code null}.
	 */
	public String[] getClazzes();

	/**
	 * Returns the unique identifiers of the bundles that are using this
	 * service.
	 * 
	 * @return A {@code Set} object containing the bundle ids represented as
	 *         {@code Long} objects. This method never returns {@code null}.
	 */
	public Set<Long> getUsingBundleIds();

	/**
	 * Returns the properties that this service is registered with.
	 * 
	 * @return A {@code Map} object containing the keys and values of all
	 *         properties. This method never returns {@code null}.
	 */
	public Map<String, String> getProperties();

}
