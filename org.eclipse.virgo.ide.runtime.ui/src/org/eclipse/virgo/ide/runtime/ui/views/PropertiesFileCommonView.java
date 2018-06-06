/*********************************************************************
 * Copyright (c) 2009, 2012 SpringSource, a division of VMware, Inc.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.runtime.ui.views;

import org.eclipse.virgo.ide.runtime.internal.ui.ServerUiPlugin;
import org.eclipse.virgo.ide.runtime.internal.ui.projects.ServerProject;

/**
 *
 * @see org.eclipse.pde.internal.ui.views.dependencies.DependenciesView
 * @author Miles Parker
 *
 */
@SuppressWarnings("restriction")
public class PropertiesFileCommonView extends ServerFileCommonView {

    /**
     * @see org.eclipse.virgo.ide.runtime.ui.views.CommonView#getTreeContentId()
     */
    @Override
    protected String getTreeContentId() {
        return ServerUiPlugin.PROPERTIES_CONTENT_ID;
    }

    /**
     * @see org.eclipse.virgo.ide.runtime.ui.views.CommonView#getListContentId()
     */
    @Override
    protected String getListContentId() {
        return ServerUiPlugin.FLATTENED_PROPERTIES_CONTENT_ID;
    }

    /**
     * @see org.eclipse.virgo.ide.runtime.ui.views.CommonView#getViewId()
     */
    @Override
    protected String getViewId() {
        return ServerUiPlugin.PROPERTIES_VIEW_ID;
    }

    /**
     * @see org.eclipse.virgo.ide.runtime.ui.views.ServerFileCommonView#getManagedDirs[]()
     */
    @Override
    public String[] getManagedDirs() {
        return new String[] { ServerProject.PROPERTIES_DIR };

    }

}
