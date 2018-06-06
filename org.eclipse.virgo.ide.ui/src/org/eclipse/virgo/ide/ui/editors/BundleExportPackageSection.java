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

import org.eclipse.pde.internal.ui.editor.PDEFormPage;
import org.eclipse.pde.internal.ui.editor.plugin.ExportPackageSection;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Christian Dupuis
 */
public class BundleExportPackageSection extends ExportPackageSection {

    private static final String DESCRIPTION = "Enumerate all the packages that this bundle exposes to clients. All other packages will be hidden from clients at all times.";

    public BundleExportPackageSection(PDEFormPage page, Composite parent) {
        super(page, parent);
        getSection().setDescription(DESCRIPTION);
    }

}
