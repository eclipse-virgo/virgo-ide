/*********************************************************************
 * Copyright (c) 2009, 2012 SpringSource, a division of VMware, Inc.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.runtime.internal.core.command;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.TimeoutException;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.eclipse.virgo.ide.runtime.core.IServerBehaviour;

/**
 * @author Christian Dupuis
 */
public class JmxBundleAdminExecuteCommand extends AbstractJmxServerCommand implements IServerCommand<String> {

    private final String cmdLine;

    public JmxBundleAdminExecuteCommand(IServerBehaviour serverBehaviour, String cmdLine) {
        super(serverBehaviour);
        this.cmdLine = cmdLine;
    }

    public String execute() throws IOException, TimeoutException {

        return (String) execute(new JmxServerCommandTemplate() {

            public Object invokeOperation(MBeanServerConnection connection) throws Exception {
                ObjectName name = ObjectName.getInstance("org.eclipse.virgo.kernel:type=BundleAdmin");

                // Verify that the BundleAdmin exists and runs
                checkBundleAdminAndInstall(JmxBundleAdminExecuteCommand.this.serverBehaviour, connection, name);

                return connection.invoke(name, "execute", new Object[] { JmxBundleAdminExecuteCommand.this.cmdLine },
                    new String[] { String.class.getName() });
            }

        });
    }

    private static void checkBundleAdminAndInstall(IServerBehaviour behaviour, MBeanServerConnection connection, ObjectName name)
        throws IOException, TimeoutException, URISyntaxException {
        try {
            // Check if BundleAdmin MBean is registered
            connection.getObjectInstance(name);
        } catch (InstanceNotFoundException e) {
            // Install the BundleAdmin bundle
            behaviour.getVersionHandler().getServerDeployCommand(behaviour).execute();
        }
    }

}