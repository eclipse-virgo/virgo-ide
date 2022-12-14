/*********************************************************************
 * Copyright (c) 2013 Eclipse Foundation
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.runtime.internal.core.actions;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.virgo.ide.runtime.core.IServerWorkingCopy;

/**
 * @author Leo Dos Santos
 */
public class ModifyMaxPermSizeCommand extends AbstractOperation {

    private final IServerWorkingCopy workingCopy;

    private final String oldPermSizeValue;

    private final String newPermSizeValue;

    public ModifyMaxPermSizeCommand(IServerWorkingCopy workingCopy, String maxPermSizeValue) {
        super("Modify -XX:MaxPermSize value");
        this.workingCopy = workingCopy;
        this.oldPermSizeValue = workingCopy.getMaxPermSize();
        this.newPermSizeValue = maxPermSizeValue;
    }

    @Override
    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        this.workingCopy.setMaxPermSize(this.newPermSizeValue);
        return Status.OK_STATUS;
    }

    @Override
    public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        return execute(monitor, info);
    }

    @Override
    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        this.workingCopy.setMaxPermSize(this.oldPermSizeValue);
        return Status.OK_STATUS;
    }

}
