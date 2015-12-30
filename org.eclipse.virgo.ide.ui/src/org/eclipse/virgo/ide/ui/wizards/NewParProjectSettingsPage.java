/*******************************************************************************
 * Copyright (c) 2009, 2012 SpringSource, a divison of VMware, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     SpringSource, a division of VMware, Inc. - initial API and implementation
 *******************************************************************************/

package org.eclipse.virgo.ide.ui.wizards;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

/**
 * @author Christian Dupuis
 * @author Leo Dos Santos
 */
public class NewParProjectSettingsPage extends WizardNewProjectCreationPage {

    private static String NEW_PROJECT_SETTINGS_TITLE = Messages.NewParProjectSettingsPage_TITLE;

    private static String NEW_PROJECT_SETTINGS_DESCRIPTION = Messages.NewParProjectSettingsPage_DESCRIPTION;

    private final IStructuredSelection selection;

    public NewParProjectSettingsPage(String pageName, IStructuredSelection selection) {
        super(pageName);
        this.selection = selection;
        setTitle(NEW_PROJECT_SETTINGS_TITLE);
        setDescription(NEW_PROJECT_SETTINGS_DESCRIPTION);
    }

    @Override
    public void createControl(Composite parent) {
        super.createControl(parent);
        createWorkingSetGroup((Composite) getControl(), this.selection, new String[] { "org.eclipse.jdt.ui.JavaWorkingSetPage" }); //$NON-NLS-1$
        Dialog.applyDialogFont(getControl());
    }

}
