/*********************************************************************
 * Copyright (c) 2009, 2012 SpringSource, a division of VMware, Inc.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.ui.editors;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.virgo.ide.facet.core.FacetUtils;

/**
 * @author Christian Dupuis
 */
public class ProjectSelectionDialog extends ElementListSelectionDialog {

    public ProjectSelectionDialog(Shell parent) {
        super(parent, WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider());
        setTitle("Bundle Selection");
        setMessage("Select a Bundle:");
        setElements(FacetUtils.getBundleProjects());
        setMultipleSelection(true);
    }

    public IProject[] getSelectedProjects() {
        Object[] result = getResult();
        ArrayList<IProject> projects = new ArrayList<IProject>();
        for (Object project : result) {
            projects.add((IProject) project);
        }
        return projects.toArray(new IProject[0]);
    }

}
