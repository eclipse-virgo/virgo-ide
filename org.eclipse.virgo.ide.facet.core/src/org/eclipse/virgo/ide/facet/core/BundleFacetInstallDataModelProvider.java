/*********************************************************************
 * Copyright (c) 2009, 2011 SpringSource, a division of VMware, Inc.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.facet.core;

import java.util.Set;

import org.eclipse.jst.common.project.facet.JavaProjectFacetCreationDataModelProvider;

/**
 * @author Christian Dupuis
 * @author Martin Lippert
 * @since 1.1.3
 */
public class BundleFacetInstallDataModelProvider extends JavaProjectFacetCreationDataModelProvider {

    public static final String ENABLE_SERVER_CLASSPATH_CONTAINER = FacetCorePlugin.PLUGIN_ID + ".ENABLE_BUNDLE_CLASSPATH_CONTAINER";

    public static final String ENABLE_WEB_BUNDLE = FacetCorePlugin.PLUGIN_ID + ".ENABLE_WEB_BUNDLE";

    @Override
    @SuppressWarnings("unchecked")
    public Set getPropertyNames() {
        Set propertyNames = super.getPropertyNames();
        propertyNames.add(ENABLE_SERVER_CLASSPATH_CONTAINER);
        propertyNames.add(ENABLE_WEB_BUNDLE);
        return propertyNames;
    }

    @Override
    public Object getDefaultProperty(String propertyName) {
        if (ENABLE_SERVER_CLASSPATH_CONTAINER.equals(propertyName)) {
            return Boolean.TRUE;
        }
        if (ENABLE_WEB_BUNDLE.equals(propertyName)) {
            return Boolean.FALSE;
        }
        return super.getDefaultProperty(propertyName);
    }

}
