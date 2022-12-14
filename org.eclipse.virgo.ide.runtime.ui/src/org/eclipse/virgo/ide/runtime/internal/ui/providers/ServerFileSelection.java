/*********************************************************************
 * Copyright (c) 2009, 2012 SpringSource, a division of VMware, Inc.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.runtime.internal.ui.providers;

import org.apache.commons.lang.ObjectUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.wst.server.core.IServer;

/**
 *
 * @author Miles Parker
 *
 */
public class ServerFileSelection extends ServerFile {

    private final String line;

    private final int offset;

    private final int length;

    private final int item;

    public ServerFileSelection(IServer server, IFile file, String line, int start, int end, int item) {
        super(server, file);
        this.line = line;
        this.offset = start;
        this.length = end;
        this.item = item;
    }

    public String getLine() {
        return this.line;
    }

    public int getOffset() {
        return this.offset;
    }

    public int getLength() {
        return this.length;
    }

    public int getItem() {
        return this.item;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return this.file.hashCode() + this.offset + 27103 * this.length;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object arg) {
        if (arg instanceof ServerFileSelection) {
            ServerFileSelection other = (ServerFileSelection) arg;
            return ObjectUtils.equals(this.file, other.file) && this.item == other.item;
        }
        return false;
    }
}
