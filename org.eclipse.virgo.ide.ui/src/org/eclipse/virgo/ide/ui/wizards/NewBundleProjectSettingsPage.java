/*********************************************************************
 * Copyright (c) 2009, 2012 SpringSource, a division of VMware, Inc.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.ui.wizards;

import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageOne;

/**
 * @author Christian Dupuis
 * @author Leo Dos Santos
 */
public class NewBundleProjectSettingsPage extends NewJavaProjectWizardPageOne {

    private static String NEW_PROJECT_SETTINGS_TITLE = Messages.NewBundleProjectSettingsPage_TITLE;

    private static String NEW_PROJECT_SETTINGS_DESCRIPTION = Messages.NewBundleProjectSettingsPage_DESCRIPTION;

    public NewBundleProjectSettingsPage() {
        super();
        setTitle(NEW_PROJECT_SETTINGS_TITLE);
        setDescription(NEW_PROJECT_SETTINGS_DESCRIPTION);
    }

}
