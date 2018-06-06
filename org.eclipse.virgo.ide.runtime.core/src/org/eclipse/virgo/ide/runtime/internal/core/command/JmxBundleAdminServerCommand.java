/*********************************************************************
 * Copyright (c) 2009, 2011 SpringSource, a division of VMware, Inc. and others and others.
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
import java.util.Map;
import java.util.concurrent.TimeoutException;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.eclipse.libra.framework.editor.core.model.IBundle;
import org.eclipse.virgo.ide.runtime.core.IServerBehaviour;

/**
 * @author Christian Dupuis
 * @author Kaloyan Raev
 */
public class JmxBundleAdminServerCommand extends AbstractJmxServerCommand implements IServerCommand<Map<Long, IBundle>> {

    public JmxBundleAdminServerCommand(IServerBehaviour serverBehaviour) {
        super(serverBehaviour);
    }

    @SuppressWarnings("unchecked")
    public Map<Long, IBundle> execute() throws IOException, TimeoutException {

        return (Map<Long, IBundle>) execute(new JmxServerCommandTemplate() {

            public Object invokeOperation(MBeanServerConnection connection) throws Exception {
                ObjectName name = ObjectName.getInstance("org.eclipse.virgo.kernel:type=BundleAdmin");

                // Verify that the BundleAdmin exists and runs
                checkBundleAdminAndInstall(JmxBundleAdminServerCommand.this.serverBehaviour, connection, name);

                return connection.invoke(name, "retrieveBundles", null, null);
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