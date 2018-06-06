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
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageTwo;
import org.eclipse.jface.wizard.IWizardPage;

/**
 * @author Christian Dupuis
 * @author Leo Dos Santos
 */
public class NewBundleProjectCreationPage extends NewJavaProjectWizardPageTwo {

    public NewBundleProjectCreationPage(NewJavaProjectWizardPageOne mainPage) {
        super(mainPage);
    }

    @Override
    public IWizardPage getPreviousPage() {
        NewBundleProjectWizard wizard = (NewBundleProjectWizard) getWizard();
        if (wizard.getPropertiesPage() instanceof NullPropertiesPage) {
            return wizard.getInformationPage();
        } else {
            return wizard.getPropertiesPage();
        }
    }

    @Override
    public IWizardPage getNextPage() {
        return null;
    }

}
