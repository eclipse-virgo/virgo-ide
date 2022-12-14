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
public class PackageImport implements Serializable {

    private static final long serialVersionUID = 8376491037926415151L;

    private final String name;

    private final String version;

    private final String supplierId;

    public PackageImport(String name, String version, String supplierId) {
        this.name = name;
        this.version = version;
        this.supplierId = supplierId;
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

    /**
     * @return the supplierId
     */
    public String getSupplierId() {
        return this.supplierId;
    }

}
