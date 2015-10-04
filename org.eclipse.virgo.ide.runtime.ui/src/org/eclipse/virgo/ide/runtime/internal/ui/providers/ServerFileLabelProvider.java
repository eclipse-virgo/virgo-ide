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

package org.eclipse.virgo.ide.runtime.internal.ui.providers;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.debug.internal.ui.DebugPluginImages;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.virgo.ide.runtime.internal.ui.ServerUiImages;

/**
 *
 * @author Miles Parker
 *
 */
public class ServerFileLabelProvider extends LabelProvider implements ILightweightLabelDecorator {

    private static final String REMOVE_REGEXP = "org\\.eclipse\\.virgo\\.|\\.properties";

    WorkbenchLabelProvider delegate = new WorkbenchLabelProvider();

    /**
     * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
     */
    @Override
    public String getText(Object element) {
        if (element instanceof IFile) {
            IFile file = (IFile) element;
            return file.getName().replaceAll(REMOVE_REGEXP, "");
        }
        if (element instanceof File) {
            File file = (File) element;
            return file.getName().replaceAll(REMOVE_REGEXP, "");
        }
        if (element instanceof ServerFileSelection) {
            IFile workspaceFile = ((ServerFile) element).getFile();
            String line = ((ServerFileSelection) element).getLine();
            if (workspaceFile.getFileExtension() != null && workspaceFile.getFileExtension().equals("properties")) {
                workspaceFile.getName();
                if (workspaceFile.getLocation().toPortableString().contains("repository")) {
                    line += " [User]";
                }
            }
            return line;
        }
        if (element instanceof ServerFile) {
            IFile workspaceFile = ((ServerFile) element).getFile();
            String serverName = workspaceFile.getLocation().lastSegment();
            if (workspaceFile.getFileExtension() != null && workspaceFile.getFileExtension().equals("properties")) {
                if (workspaceFile.getLocation().toPortableString().contains("repository")) {
                    serverName += " [User]";
                }
            }
            return serverName.replaceAll(REMOVE_REGEXP, "");
        }
        return this.delegate.getText(element);
    }

    /**
     * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
     */
    @Override
    public Image getImage(Object element) {
        if (element instanceof ServerFileSelection) {
            return DebugPluginImages.getImage(IDebugUIConstants.IMG_OBJS_VARIABLE);
        }
        if (element instanceof ServerFile) {
            return getImage(((ServerFile) element).getFile());
        }
        return this.delegate.getImage(element);
    }

    public void decorate(Object element, IDecoration decoration) {
        if (element instanceof ServerFile) {
            ServerFile serverFile = (ServerFile) element;
            String suffix = " [" + serverFile.getServer() + "] - " + serverFile.getFile().getLocation();
            if (element instanceof ServerFileSelection) {
                suffix += " #" + ((ServerFileSelection) element).getItem();
            }
            decoration.addSuffix(suffix);
            if (!(element instanceof ServerFileSelection)) {
                decoration.addOverlay(ServerUiImages.DESC_OBJ_VIRGO_OVER, IDecoration.TOP_LEFT);
            }
        }
    }
}
