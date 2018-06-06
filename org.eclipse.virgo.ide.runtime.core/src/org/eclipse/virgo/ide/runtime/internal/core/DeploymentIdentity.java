/*********************************************************************
 * Copyright (c) 2009, 2012 SpringSource, a division of VMware, Inc.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.runtime.internal.core;

/**
 * Simple data holder for a deployed par or bundle.
 *
 * @author Christian Dupuis
 * @since 1.0.0
 */
public class DeploymentIdentity {

    private final String symbolicName;

    private final String version;

    public DeploymentIdentity(String symbolicName, String version) {
        this.symbolicName = symbolicName;
        this.version = version;
    }

    public DeploymentIdentity(String output) {
        int ix = output.lastIndexOf('#');
        this.symbolicName = output.substring(0, ix);
        this.version = output.substring(ix + 1);
    }

    public String getSymbolicName() {
        return this.symbolicName;
    }

    public String getVersion() {
        return this.version;
    }

}
