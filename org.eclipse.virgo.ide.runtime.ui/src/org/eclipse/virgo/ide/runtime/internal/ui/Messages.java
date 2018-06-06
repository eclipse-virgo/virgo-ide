/*********************************************************************
 * Copyright (c) 2015 GianMaria Romanato
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.runtime.internal.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    private static final String BUNDLE_NAME = "org.eclipse.virgo.ide.runtime.internal.ui.messages"; //$NON-NLS-1$

    public static String PDETargetPlatformComposite_add;

    public static String PDETargetPlatformComposite_add_dialog_message;

    public static String PDETargetPlatformComposite_add_dialog_title;

    public static String PDETargetPlatformComposite_edit;

    public static String PDETargetPlatformComposite_edit_dialog_message;

    public static String PDETargetPlatformComposite_edit_dialog_title;

    public static String PDETargetPlatformComposite_enable_checkbox;

    public static String PDETargetPlatformComposite_error_message;

    public static String PDETargetPlatformComposite_note;

    public static String PDETargetPlatformComposite_remove;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
