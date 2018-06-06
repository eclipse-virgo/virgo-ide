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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Christian Dupuis
 */
public class ServiceReference implements Serializable {

    public enum Type {
        IN_USE, REGISTERED
    }

    private static final long serialVersionUID = -4896924600246187914L;

    private final Long bundleId;

    private final String[] clazzes;

    private final Map<String, String> properties = new HashMap<String, String>();

    private final Set<Long> usingBundles = new HashSet<Long>();

    private final Type type;

    public ServiceReference(Type type, Long bundleId, String[] clazzes) {
        this.bundleId = bundleId;
        this.clazzes = clazzes;
        this.type = type;
    }

    public Map<String, String> getProperties() {
        return this.properties;
    }

    public String[] getClazzes() {
        return this.clazzes;
    }

    public Set<Long> getUsingBundleIds() {
        return this.usingBundles;
    }

    public void addProperty(String key, String value) {
        this.properties.put(key, value);
    }

    public void addUsingBundle(Long id) {
        this.usingBundles.add(id);
    }

    public Long getBundleId() {
        return this.bundleId;
    }

    /**
     * @return the type
     */
    public Type getType() {
        return this.type;
    }
}
