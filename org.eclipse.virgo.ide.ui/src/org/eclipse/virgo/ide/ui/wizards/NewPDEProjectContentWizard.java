/*********************************************************************
 * Copyright (c) 2015 GianMaria Romanato
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.ui.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.IPluginReference;
import org.eclipse.pde.ui.IFieldData;
import org.eclipse.pde.ui.IPluginContentWizard;

/**
 * A content wizard that adds a page for collecting the context root of a WAB. The bundle is assumed to be a WAB if the
 * contect root is not empty.
 * <p />
 */
public class NewPDEProjectContentWizard extends Wizard implements IPluginContentWizard {

    private NewPDEProjectWABPage wabPage;

    /**
     * {@inheritDoc}
     */
    public void init(IFieldData data) {
    }

    /**
     * {@inheritDoc}
     */
    public IPluginReference[] getDependencies(String schemaVersion) {
        return new IPluginReference[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPages() {
        wabPage = new NewPDEProjectWABPage();
        addPage(wabPage);
    }

    /**
     * {@inheritDoc}
     */
    public boolean performFinish(IProject project, IPluginModelBase model, IProgressMonitor monitor) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean performFinish() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public String[] getNewFiles() {
        return new String[0];
    }

}
