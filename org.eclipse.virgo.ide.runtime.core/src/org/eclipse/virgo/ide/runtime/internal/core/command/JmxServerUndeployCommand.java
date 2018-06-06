/*********************************************************************
 * Copyright (c) 2010 - 2013 SpringSource, a division of VMware, Inc.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.runtime.internal.core.command;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import javax.management.openmbean.CompositeData;

import org.eclipse.virgo.ide.runtime.core.IServerBehaviour;
import org.eclipse.virgo.ide.runtime.internal.core.DeploymentIdentity;
import org.eclipse.wst.server.core.IModule;

/**
 * {@link IServerCommand} to undeploy artifacts on Virgo
 *
 * @author Christian Dupuis
 * @author Leo Dos Santos
 * @since 2.3.1
 */
public class JmxServerUndeployCommand extends AbstractJmxServerDeployerCommand<CompositeData>implements IServerCommand<Void> {

    /** {@link DeploymentIdentity} of the deployed PAR or bundle */
    private final DeploymentIdentity identity;

    /**
     * Creates a new {@link JmxServerUndeployCommand}.
     */
    public JmxServerUndeployCommand(IServerBehaviour serverBehaviour, IModule module) {
        super(serverBehaviour, module);
        this.identity = serverBehaviour.getDeploymentIdentities().remove(module.getId());
    }

    public Void execute() throws IOException, TimeoutException {
        doExecute();
        return null;
    }

    @Override
    protected Object[] getOperationArguments() {
        return new Object[] { this.identity.getSymbolicName(), this.identity.getVersion() };
    }

    @Override
    protected String getOperationName() {
        return "undeploy"; //$NON-NLS-1$
    }

}
