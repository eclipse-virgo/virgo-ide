/*********************************************************************
 * Copyright (c) 2009, 2011 SpringSource, a division of VMware, Inc. and others and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.management.remote;

import java.io.Serializable;

import org.eclipse.libra.framework.editor.core.model.IPackageExport;

/**
 * @author Christian Dupuis
 * @author Kaloyan Raev
 */
public class PackageExport implements IPackageExport, Serializable {

    private static final long serialVersionUID = -4798012781542524159L;

    private final String name;

    private final String version;

    public PackageExport(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return this.name;
    }

    public String getVersion() {
        return this.version;
    }

}