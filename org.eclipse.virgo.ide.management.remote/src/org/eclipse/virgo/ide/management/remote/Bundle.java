/*******************************************************************************
 * Copyright (c) 2007, 2012 SpringSource, a divison of VMware, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     SpringSource, a divison of VMware, Inc. - initial API and implementation
 *******************************************************************************/

package org.eclipse.virgo.ide.management.remote;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Christian Dupuis
 */
public class Bundle implements Serializable {

    private static final long serialVersionUID = 228698327431610457L;

    private final Set<PackageExport> exports = new HashSet<PackageExport>();

    private final String id;

    private final Set<PackageImport> imports = new HashSet<PackageImport>();

    private final String state;

    private final String symbolicName;

    private final String version;

    private final String location;

    private final Map<String, String> headers = new HashMap<String, String>();

    private final Set<ServiceReference> registeredServices = new HashSet<ServiceReference>();

    private final Set<ServiceReference> servicesInUse = new HashSet<ServiceReference>();

    public Bundle(String id, String symbolicName, String version, String state, String location) {
        this.symbolicName = symbolicName;
        this.version = version;
        this.id = id;
        this.state = state;
        this.location = location;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public Set<PackageExport> getPackageExports() {
        return this.exports;
    }

    public String getId() {
        return this.id;
    }

    public Set<PackageImport> getPackageImports() {
        return this.imports;
    }

    public String getState() {
        return this.state;
    }

    public String getSymbolicName() {
        return this.symbolicName;
    }

    public String getVersion() {
        return this.version;
    }

    public void addPackageExport(PackageExport pe) {
        this.exports.add(pe);
    }

    public void addPackageImport(PackageImport pi) {
        this.imports.add(pi);
    }

    public void addRegisteredService(ServiceReference pi) {
        this.registeredServices.add(pi);
    }

    public void addUsingService(ServiceReference pi) {
        this.servicesInUse.add(pi);
    }

    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public String getLocation() {
        return this.location;
    }

    public Set<ServiceReference> getRegisteredServices() {
        return this.registeredServices;
    }

    public Set<ServiceReference> getServicesInUse() {
        return this.servicesInUse;
    }
}
