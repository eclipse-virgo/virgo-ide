/*********************************************************************
 * Copyright (c) 2015 GianMaria Romanato
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.pde.core.internal;

/**
 * Constant values.
 *
 */
public final class Constants {

    /**
     * Bundle symbolic name.
     */
    public static final String PLUGIN_ID = "org.eclipse.virgo.ide.pde.core";//$NON-NLS-1$

    /**
     * Nature-id.
     */
    public static final String NATURE_ID = PLUGIN_ID + ".nature"; //$NON-NLS-1$

    /** Builder-id. */
    public static final String BUILDER_ID = PLUGIN_ID + ".builder"; //$NON-NLS-1$

    /* debug key */
    /* package */ static final String DEBUG_KEY = PLUGIN_ID + "/debug"; //$NON-NLS-1$

    // path constants
    /* package */ static final String META_INF = "META-INF"; //$NON-NLS-1$
    /* package */ static final String OSGI_INF = "OSGI-INF"; //$NON-NLS-1$
    /* package */ static final String MANIFEST_MF = "MANIFEST.MF"; //$NON-NLS-1$
    /* package */ static final String WebContent = "WebContent"; //$NON-NLS-1$

    private Constants() {
    }

}
