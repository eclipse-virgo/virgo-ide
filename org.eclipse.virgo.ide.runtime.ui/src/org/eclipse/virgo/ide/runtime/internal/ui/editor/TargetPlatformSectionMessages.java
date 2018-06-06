/*********************************************************************
 * Copyright (c) 2015 GianMaria Romanato
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.runtime.internal.ui.editor;

import org.eclipse.osgi.util.NLS;

public class TargetPlatformSectionMessages extends NLS {

    private static final String BUNDLE_NAME = "org.eclipse.virgo.ide.runtime.internal.ui.editor.tpsectionmessages"; //$NON-NLS-1$

    public static String TargetPlatformSection_description;

    public static String TargetPlatformSection_form_text;

    public static String TargetPlatformSection_InternalError;

    public static String TargetPlatformSection_not_configured_message;

    public static String TargetPlatformSection_not_configured_title;

    public static String TargetPlatformSection_title;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, TargetPlatformSectionMessages.class);
    }

    private TargetPlatformSectionMessages() {
    }
}
