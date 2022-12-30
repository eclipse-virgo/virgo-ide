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

import org.eclipse.virgo.ide.framework.editor.core.model.IBundle;

/**
 * @author Christian Dupuis
 * @author Kaloyan Raev
 */
public abstract class BundleDependency {

	private final IBundle exportingBundle;

	private final IBundle importingBundle;

	public BundleDependency(IBundle exportingBundle, IBundle importingBundle) {
		this.exportingBundle = exportingBundle;
		this.importingBundle = importingBundle;
	}

	public IBundle getExportingBundle() {
		return exportingBundle;
	}

	public IBundle getImportingBundle() {
		return importingBundle;
	}

	@Override
	public int hashCode() {
		int hashCode = 17;
		if (exportingBundle != null) {
			hashCode = 31 * hashCode + exportingBundle.hashCode();
		}
		if (importingBundle != null) {
			hashCode = 31 * hashCode + importingBundle.hashCode();
		}
		return hashCode;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof BundleDependency)) {
			return false;
		}
		BundleDependency that = (BundleDependency) other;
		if (this.exportingBundle != that.exportingBundle) {
			return false;
		}
		if (this.exportingBundle != null && !this.exportingBundle.equals(that.exportingBundle)) {
			return false;
		}
		if (this.importingBundle != that.importingBundle) {
			return false;
		}
		if (this.importingBundle != null && !this.importingBundle.equals(that.importingBundle)) {
			return false;
		}
		return true;
	}

}
