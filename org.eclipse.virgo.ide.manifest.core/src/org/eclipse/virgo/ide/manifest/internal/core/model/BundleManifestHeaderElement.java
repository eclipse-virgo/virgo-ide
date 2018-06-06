/*********************************************************************
 * Copyright (c) 2009 - 2012 SpringSource, a division of VMware, Inc.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.manifest.internal.core.model;

import org.eclipse.osgi.util.ManifestElement;

/**
 * @author Christian Dupuis
 * @since 1.0.0
 */
/**
 * TODO CD add comments
 */
public class BundleManifestHeaderElement extends AbstractManifestElement {

    private static final int BUNDLE_MANIFEST_HEADER_ELEMENT_TYPE = 2;

    private final ManifestElement manifestElement;

    public BundleManifestHeaderElement(BundleManifestHeader parent, ManifestElement manifestElement) {
        super(parent, manifestElement.toString());
        this.manifestElement = manifestElement;
    }

    public int getElementType() {
        return BUNDLE_MANIFEST_HEADER_ELEMENT_TYPE;
    }

    public ManifestElement getManifestElement() {
        return this.manifestElement;
    }

    @Override
    public String toString() {
        return new StringBuilder().append(this.manifestElement.getValue()).toString();
    }

}
