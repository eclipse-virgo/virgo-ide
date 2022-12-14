/*********************************************************************
 * Copyright (c) 2009, 2012 SpringSource, a division of VMware, Inc.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.manifest.core.editor.model;

import org.eclipse.osgi.util.ManifestElement;
import org.eclipse.pde.internal.core.ibundle.IBundle;
import org.eclipse.pde.internal.core.text.bundle.CompositeManifestHeader;
import org.eclipse.pde.internal.core.text.bundle.PDEManifestElement;
import org.osgi.framework.Constants;

/**
 * @author Leo Dos Santos
 */
public class ImportBundleHeader extends CompositeManifestHeader {

    private static final long serialVersionUID = 1L;

    public ImportBundleHeader(String name, String value, IBundle bundle, String lineDelimiter) {
        super(name, value, bundle, lineDelimiter);
    }

    public void addBundle(String id) {
        addBundle(id, null);
    }

    public void addBundle(String id, String version) {
        ImportBundleObject element = new ImportBundleObject(this, id);

        if (version != null && version.trim().length() > 0) {
            element.setAttribute(Constants.VERSION_ATTRIBUTE, version.trim());
        }

        addManifestElement(element);
    }

    public void removeBundle(String id) {
        removeManifestElement(id);
    }

    public void removeBundle(ImportBundleObject bundle) {
        removeManifestElement(bundle);
    }

    @Override
    protected PDEManifestElement createElement(ManifestElement element) {
        return new ImportBundleObject(this, element);
    }

    public ImportBundleObject[] getImportedBundles() {
        PDEManifestElement[] elements = getElements();
        ImportBundleObject[] result = new ImportBundleObject[elements.length];
        System.arraycopy(elements, 0, result, 0, elements.length);
        return result;
    }
}
