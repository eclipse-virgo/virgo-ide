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

import java.util.Map;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Christian Dupuis
 */
public abstract class AbstractPropertiesPage extends WizardPage {

    public AbstractPropertiesPage(String name) {
        super(name);
        setTitle(Messages.AbstractPropertiesPage_title);
        setDescription(Messages.AbstractPropertiesPage_description + Messages.AbstractPropertiesPage_description2);
    }

    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout());

        createPropertiesGroup(container);
        setControl(container);
    }

    protected abstract void createPropertiesGroup(Composite container);

    public abstract String getModuleType();

    public abstract Map<String, String> getProperties();

    @Override
    public IWizardPage getNextPage() {
        NewBundleProjectWizard wizard = (NewBundleProjectWizard) getWizard();
        return wizard.getFinalPage();
    }

    @Override
    public IWizardPage getPreviousPage() {
        NewBundleProjectWizard wizard = (NewBundleProjectWizard) getWizard();
        return wizard.getInformationPage();
    }

}
