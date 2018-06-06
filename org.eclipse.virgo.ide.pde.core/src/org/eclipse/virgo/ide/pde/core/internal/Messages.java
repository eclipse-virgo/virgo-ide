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

import org.eclipse.osgi.util.NLS;

/**
 * Localization support.
 */
public class Messages extends NLS {

    public static String Builder_copy_nativecode;

    public static String Builder_IncrementalBuildMessage;

    public static String Builder_FullBuildMessage;

    public static String Builder_copy_libraries;

    public static String Builder_CopyContent;

    public static String Helper_BinFolderError;

    public static String Helper_ManifestParsingError;

    static {
        // initialize resource bundle
        NLS.initializeMessages(Messages.class.getName(), Messages.class);
    }

    private Messages() {
    }
}
