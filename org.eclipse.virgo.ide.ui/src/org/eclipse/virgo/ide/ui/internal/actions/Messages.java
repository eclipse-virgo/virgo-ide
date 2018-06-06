/*********************************************************************
 * Copyright (c) 2015 GianMaria Romanato
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.ui.internal.actions;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    private static final String BUNDLE_NAME = "org.eclipse.virgo.ide.ui.internal.actions.messages"; //$NON-NLS-1$

    public static String ConvertPDE2VirgoProject_message;

    public static String ConvertPDE2VirgoProject_title;

    public static String ConvertPlugInProject_message;

    public static String ConvertPlugInProject_title;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
