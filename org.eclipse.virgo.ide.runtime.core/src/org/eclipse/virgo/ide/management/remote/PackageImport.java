/*******************************************************************************
 * Copyright (c) 2009, 2011 SpringSource, a divison of VMware, Inc. and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     SpringSource, a division of VMware, Inc. - initial API and implementation
 *     SAP AG - moving to Eclipse Libra project and enhancements
 *******************************************************************************/

package org.eclipse.virgo.ide.management.remote;

import java.io.Serializable;

import org.eclipse.libra.framework.editor.core.model.IPackageImport;
import org.eclipse.virgo.util.common.ObjectUtils;

/**
 * @author Christian Dupuis
 * @author Kaloyan Raev
 */
public class PackageImport implements IPackageImport, Serializable {

    private static final long serialVersionUID = 8376491037926415151L;

    private final String name;

    private final String version;

    private final String supplierId;

    public PackageImport(String name, String version, String supplierId) {
        this.name = name;
        this.version = version;
        this.supplierId = supplierId;
    }

    public String getName() {
        return this.name;
    }

    public String getVersion() {
        return this.version;
    }

    public String getSupplierId() {
        return this.supplierId;
    }

    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode = 31 * hashCode + this.name.hashCode();
        hashCode = 31 * hashCode + this.version.hashCode();
        hashCode = 31 * hashCode + this.supplierId.hashCode();
        return hashCode;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PackageImport)) {
            return false;
        }
        PackageImport that = (PackageImport) other;
        if (!ObjectUtils.nullSafeEquals(this.name, that.name)) {
            return false;
        }
        if (!ObjectUtils.nullSafeEquals(this.version, that.version)) {
            return false;
        }
        if (!ObjectUtils.nullSafeEquals(this.supplierId, that.supplierId)) {
            return false;
        }
        return true;
    }

}
