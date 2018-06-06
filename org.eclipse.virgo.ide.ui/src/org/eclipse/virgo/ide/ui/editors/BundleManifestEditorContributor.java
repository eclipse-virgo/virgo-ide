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

/**
 * @author Christian Dupuis
 */
public class BundleManifestEditorContributor extends AbstractPdeFormTextEditorContributor {

    public BundleManifestEditorContributor() {
        super("&Plugin"); //$NON-NLS-1$
    }

    @Override
    public boolean supportsContentAssist() {
        return true;
    }

    @Override
    public boolean supportsFormatAction() {
        return true;
    }

    @Override
    public boolean supportsCorrectionAssist() {
        return true;
    }

    @Override
    public boolean supportsHyperlinking() {
        return true;
    }
}
