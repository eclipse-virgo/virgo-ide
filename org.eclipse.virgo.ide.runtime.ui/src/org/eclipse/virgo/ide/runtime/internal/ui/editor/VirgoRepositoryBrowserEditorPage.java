/*********************************************************************
 * Copyright (c) 2010 SpringSource, a division of VMware, Inc.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.runtime.internal.ui.editor;

import org.eclipse.swt.graphics.Image;
import org.eclipse.virgo.ide.runtime.internal.ui.ServerUiImages;
import org.eclipse.wst.server.ui.editor.ServerEditorPart;

/**
 * {@link ServerEditorPart} that allows to browse the local and remote bundle repository.
 *
 * @author Terry Hon
 * @author Christian Dupuis
 * @since 1.0.0
 */
public class VirgoRepositoryBrowserEditorPage extends RepositoryBrowserEditorPage {

    @Override
    protected Image getFormImage() {
        return ServerUiImages.getImage(ServerUiImages.IMG_OBJ_VIRGO);
    }

    @Override
    protected String getServerName() {
        return Messages.VirgoServerName;
    }

}
