/*********************************************************************
 * Copyright (c) 2009, 2012 SpringSource, a division of VMware, Inc.
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
 * {@link AbstractOperation} to modify the tail log file settings.
 *
 * @author Christian Dupuis
 * @since 1.0.1
 */
public class ModifyTailLogFilesCommand extends AbstractOperation {

    private final IServerWorkingCopy workingCopy;

    private final boolean oldValue;

    private final boolean newValue;

    public ModifyTailLogFilesCommand(IServerWorkingCopy workingCopy, boolean newValue) {
        super("Modify Tail Log Files");
        this.workingCopy = workingCopy;
        this.oldValue = workingCopy.shouldTailTraceFiles();
        this.newValue = newValue;
    }

    @Override
    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        this.workingCopy.shouldTailTraceFiles(this.newValue);
        return Status.OK_STATUS;
    }

    @Override
    public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        this.workingCopy.shouldTailTraceFiles(this.newValue);
        return Status.OK_STATUS;
    }

    @Override
    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        this.workingCopy.shouldTailTraceFiles(this.oldValue);
        return Status.OK_STATUS;
    }

}
