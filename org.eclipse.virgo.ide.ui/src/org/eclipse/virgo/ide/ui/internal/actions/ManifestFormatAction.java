/*********************************************************************
 * Copyright (c) 2009, 2012 SpringSource, a division of VMware, Inc.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.ui.internal.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.pde.internal.ui.PDEPlugin;
import org.eclipse.pde.internal.ui.editor.actions.FormatAction;
import org.eclipse.ui.PlatformUI;

/**
 * @author Christian Dupuis
 * @author Leo Dos Santos
 */
public class ManifestFormatAction extends FormatAction {

    @Override
    public void run() {
        if (this.fTextEditor == null || this.fTextEditor.getEditorInput() == null) {
            return;
        }

        try {
            PlatformUI.getWorkbench().getProgressService().busyCursorWhile(
                new ManifestFormatOperation(new Object[] { this.fTextEditor.getEditorInput() }));
        } catch (InvocationTargetException e) {
            PDEPlugin.log(e);
        } catch (InterruptedException e) {
            PDEPlugin.log(e);
        }
    }

}
