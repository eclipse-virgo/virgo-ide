/*********************************************************************
 * Copyright (c) 2015 GianMaria Romanato
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.runtime.internal.ui;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;

/**
 * A {@link WizardFragment} that adds an extra page for setting up the PDE target platform.
 */
public class PDETargetPlatformWizardFragment extends WizardFragment {

    private PDETargetPlatformComposite targetPlatformComposite;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasComposite() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Composite createComposite(Composite parent, final IWizardHandle handle) {
        handle.setTitle(PDEUIMessages.PDETargetPlatformWizardFragment_title);
        handle.setDescription(PDEUIMessages.PDETargetPlatformWizardFragment_desc);

        IRunnableContext ctx = new IRunnableContext() {

            public void run(boolean fork, boolean cancelable, IRunnableWithProgress runnable) throws InvocationTargetException, InterruptedException {
                handle.run(fork, cancelable, runnable);
            }
        };
        targetPlatformComposite = new PDETargetPlatformComposite(parent, ctx, (IRuntimeWorkingCopy) getTaskModel().getObject(TaskModel.TASK_RUNTIME));
        return targetPlatformComposite;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isComplete() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enter() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performFinish(IProgressMonitor monitor) throws CoreException {
        if (targetPlatformComposite != null) {
            targetPlatformComposite.performFinish(monitor);
        }
    }
}
