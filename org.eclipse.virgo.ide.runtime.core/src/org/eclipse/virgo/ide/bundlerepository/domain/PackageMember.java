/*********************************************************************
 * Copyright (c) 2007, 2012 SpringSource
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.bundlerepository.domain;

import org.eclipse.virgo.ide.runtime.core.artefacts.ArtefactRepositoryManager;

/**
 * An exported member of an exported package (class or resource)
 *
 */
public class PackageMember {

    private byte[] name; // fully-qualified name of the exported member

    private PackageExport exportingPackage; // the package exporting this member

    private PackageMemberType memberType; // the member type (class or resource)

    /* for persistence only */
    protected PackageMember() {
    }

    /**
     * Construct a new package member
     *
     * @param name the fully-qualified member name
     * @param memberType the member type (class or resource)
     * @param pkg the exporting package
     */
    public PackageMember(String name, PackageMemberType memberType, PackageExport pkg) {
        this.name = ArtefactRepositoryManager.convert(name);
        this.exportingPackage = pkg;
        this.memberType = memberType;
    }

    /**
     * The fully-qualified name of this member
     */
    public String getName() {
        return this.name != null ? new String(this.name) : null;
    }

    /**
     * The type of member (class or resource)
     */
    public PackageMemberType getMemberType() {
        return this.memberType;
    }

    /**
     * The package exporting this member
     *
     * @return
     */
    public PackageExport getExportingPackage() {
        return this.exportingPackage;
    }

}