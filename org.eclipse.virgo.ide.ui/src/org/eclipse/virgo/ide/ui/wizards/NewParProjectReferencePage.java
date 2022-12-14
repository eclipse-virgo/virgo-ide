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

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.ui.dialogs.WizardNewProjectReferencePage;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.virgo.ide.facet.core.FacetUtils;

/**
 * @author Christian Dupuis
 */
public class NewParProjectReferencePage extends WizardNewProjectReferencePage {

    private static String NEW_PROJECT_REFERENCE_TITLE = Messages.NewParProjectReferencePage_title;

    private static String NEW_PROJECT_REFERENCE_DESCRIPTION = Messages.NewParProjectReferencePage_desc;

    public NewParProjectReferencePage(String pageName) {
        super(pageName);
        setTitle(NEW_PROJECT_REFERENCE_TITLE);
        setDescription(NEW_PROJECT_REFERENCE_DESCRIPTION);
    }

    @Override
    protected IStructuredContentProvider getContentProvider() {
        return new WorkbenchContentProvider() {

            @Override
            public Object[] getChildren(Object element) {
                if (!(element instanceof IWorkspace)) {
                    return new Object[0];
                }

                return FacetUtils.getBundleProjects();
            }
        };
    }
}
