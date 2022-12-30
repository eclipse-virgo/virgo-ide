/*******************************************************************************
 * Copyright (c) 2009, 2011 SpringSource, a divison of VMware, Inc. and others
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     SpringSource, a division of VMware, Inc. - initial API and implementation
 *     SAP AG - enhancements
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.virgo.ide.framework.editor.ui.internal.dependencies;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.virgo.ide.framework.editor.core.model.IBundle;
import org.eclipse.virgo.ide.framework.editor.core.model.IServiceReference;

/**
 * @author Christian Dupuis
 * @author Kaloyan Raev
 */
public class ServiceReferenceBundleDependency extends BundleDependency {

	private final Set<IServiceReference> serviceReference = new HashSet<IServiceReference>();

	public ServiceReferenceBundleDependency(IBundle exportingBundle, IBundle importingBundle) {
		super(exportingBundle, importingBundle);
	}

	public void addServiceReferece(IServiceReference reference) {
		serviceReference.add(reference);
	}

	@Override
	public int hashCode() {
		int hashCode = 17;
		hashCode = 31 * hashCode + serviceReference.hashCode();
		return 31 * hashCode + super.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ServiceReferenceBundleDependency)) {
			return false;
		}
		ServiceReferenceBundleDependency that = (ServiceReferenceBundleDependency) other;
		if (!this.serviceReference.equals(that.serviceReference)) { // null safe guaranteed by the final modifier
			return false;
		}
		return super.equals(other);
	}

}
