/*********************************************************************
 * Copyright (c) 2007, 2012 SpringSource, a division of VMware, Inc.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.management.remote;

import java.io.Serializable;

/**
 * @author Christian Dupuis
 */
public class PackageExport implements Serializable {

    private static final long serialVersionUID = -4798012781542524159L;

    private final String name;

    private final String version;

    public PackageExport(String name, String version) {
        this.name = name;
        this.version = version;
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return this.version;
    }

}