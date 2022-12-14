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

import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.virgo.ide.runtime.core.IServerWorkingCopy;

/**
 * {@link AbstractOperation} to modify the artefact deployment order.
 *
 * @author Christian Dupuis
 * @since 1.0.2
 */
public class ModifyArtefactOrderCommand extends AbstractOperation {

    private final IServerWorkingCopy workingCopy;

    private final List<String> oldOrder;

    private final List<String> newOrder;

    public ModifyArtefactOrderCommand(IServerWorkingCopy workingCopy, List<String> newOrder) {
        super("Modify Artefact deployment order");
        this.workingCopy = workingCopy;
        this.oldOrder = workingCopy.getArtefactOrder();
        this.newOrder = newOrder;
    }

    @Override
    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        this.workingCopy.setArtefactOrder(this.newOrder);
        return Status.OK_STATUS;
    }

    @Override
    public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        this.workingCopy.setArtefactOrder(this.newOrder);
        return Status.OK_STATUS;
    }

    @Override
    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        this.workingCopy.setArtefactOrder(this.oldOrder);
        return Status.OK_STATUS;
    }

}
