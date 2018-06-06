
package org.eclipse.virgo.ide.runtime.internal.ui;

/*********************************************************************
 * Copyright (c) 2015 GianMaria Romanato
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/
import org.eclipse.osgi.util.NLS;

public class PDEUIMessages extends NLS {

    private static final String BUNDLE_NAME = "org.eclipse.virgo.ide.runtime.internal.ui.pdeuimessages"; //$NON-NLS-1$

    public static String PDETargetPlatformWizardFragment_desc;

    public static String PDETargetPlatformWizardFragment_title;

    public static String PDETargetPlatformWizardFragment_update_dialog_error;

    public static String PDETargetPlatformWizardFragment_update_dialog_message;

    public static String PDETargetPlatformWizardFragment_update_dialog_title;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, PDEUIMessages.class);
    }

    private PDEUIMessages() {
    }
}
