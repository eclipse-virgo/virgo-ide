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

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

/**
 * Represents the configuration of the SpringSourceApplicationPlatform osgi profile
 *
 * @author acolyer
 *
 */
public class SpringSourceApplicationPlatform {

    private static final String AP_PROPS = "org.eclipse.virgo.ide.bundlerepository.domain.equinox.APProfile";

    private static final OsgiVersion UNVERSIONED = new OsgiVersion("0.0.0");

    private final ResourceBundle apProps;

    private final List<String> exportedSystemPackages = new ArrayList<String>();

    public SpringSourceApplicationPlatform() {
        this.apProps = ResourceBundle.getBundle(AP_PROPS);
        String systemPackages = this.apProps.getString("org.osgi.framework.system.packages");
        parseSystemPackages(systemPackages);
    }

    public boolean isSatisfiedViaSystemBundle(PackageImport imp) {
        return this.exportedSystemPackages.contains(imp.getName()) && imp.getImportRange().contains(UNVERSIONED);
    }

    private void parseSystemPackages(String systemPackagesPropertyValue) {
        StringTokenizer tokenizer = new StringTokenizer(systemPackagesPropertyValue, ",");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken().trim();
            this.exportedSystemPackages.add(token);
        }
    }

}
