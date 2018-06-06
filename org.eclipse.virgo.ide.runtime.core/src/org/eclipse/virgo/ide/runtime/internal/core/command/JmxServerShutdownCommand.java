/*********************************************************************
 * Copyright (c) 2009 - 2013 SpringSource, a division of VMware, Inc.
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

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.eclipse.virgo.ide.runtime.core.IServerBehaviour;

/**
 * {@link IServerCommand} to shutdown a dm Server.
 *
 * @author Christian Dupuis
 * @since 1.0.1
 */
public class JmxServerShutdownCommand extends AbstractJmxServerCommand implements IServerCommand<Void> {

    /**
     * Creates a new {@link JmxServerShutdownCommand}.
     */
    public JmxServerShutdownCommand(IServerBehaviour serverBehaviour) {
        super(serverBehaviour);
    }

    /**
     * {@inheritDoc}
     */
    public Void execute() throws IOException, TimeoutException {

        JmxServerCommandTemplate template = new JmxServerCommandTemplate() {

            public Object invokeOperation(MBeanServerConnection connection) throws Exception {
                ObjectName name = ObjectName.getInstance(JmxServerShutdownCommand.this.serverBehaviour.getVersionHandler().getShutdownMBeanName());
                return connection.invoke(name, "shutdown", null, null);
            }

        };

        execute(template);
        return null;
    }

}
