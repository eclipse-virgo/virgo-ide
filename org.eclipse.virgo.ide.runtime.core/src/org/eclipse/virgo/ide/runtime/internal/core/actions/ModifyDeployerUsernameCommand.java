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
 * {@link AbstractOperation} to modify the jmx deployer username.
 *
 * @author Christian Dupuis
 * @since 1.0.1
 */
public class ModifyDeployerUsernameCommand extends AbstractOperation {

    private final IServerWorkingCopy workingCopy;

    private final String oldUsername;

    private final String newUsername;

    public ModifyDeployerUsernameCommand(IServerWorkingCopy workingCopy, String username) {
        super("Modify JMX server port");
        this.workingCopy = workingCopy;
        this.oldUsername = workingCopy.getMBeanServerUsername();
        this.newUsername = username;
    }

    @Override
    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        this.workingCopy.setMBeanServerUsername(this.newUsername);
        return Status.OK_STATUS;
    }

    @Override
    public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        this.workingCopy.setMBeanServerUsername(this.newUsername);
        return Status.OK_STATUS;
    }

    @Override
    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        this.workingCopy.setMBeanServerUsername(this.oldUsername);
        return Status.OK_STATUS;
    }

}
