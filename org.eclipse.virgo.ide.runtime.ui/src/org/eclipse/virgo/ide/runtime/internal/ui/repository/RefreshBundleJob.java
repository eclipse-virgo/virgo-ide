/*********************************************************************
 * Copyright (c) 2009 - 2013 SpringSource, a division of VMware, Inc.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.runtime.internal.ui.repository;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.virgo.ide.runtime.core.ServerCorePlugin;
import org.eclipse.virgo.ide.runtime.core.provisioning.RepositoryUtils;
import org.eclipse.virgo.ide.runtime.internal.ui.editor.Messages;
import org.eclipse.wst.server.core.IRuntime;

/**
 * @author Miles Parker
 * @author Leo Dos Santos
 */
public class RefreshBundleJob implements IRunnableWithProgress {

    private final IRuntime runtime;

    RefreshBundleJob(IRuntime runtime) {
        this.runtime = runtime;
    }

    public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        monitor.subTask(Messages.RepositoryBrowserEditorPage_RefreshingBundlesMessage);
        if (RepositoryUtils.doesRuntimeSupportRepositories(this.runtime)) {
            ServerCorePlugin.getArtefactRepositoryManager().refreshBundleRepository(this.runtime);
        }
        monitor.done();
    }

    public static void execute(Shell shell, IRuntime runtime) {
        RefreshBundleJob job = new RefreshBundleJob(runtime);
        try {
            IRunnableContext context = new ProgressMonitorDialog(shell);
            context.run(true, true, job);
        } catch (InvocationTargetException e1) {
            StatusManager.getManager().handle(
                new Status(IStatus.ERROR, ServerCorePlugin.PLUGIN_ID, "�problem occurred while updating repository", e1));
        } catch (InterruptedException e) {
        }
    }
}