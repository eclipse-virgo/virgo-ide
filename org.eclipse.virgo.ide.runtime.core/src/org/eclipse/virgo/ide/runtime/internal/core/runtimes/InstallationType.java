/*********************************************************************
 * Copyright (c) 2009 - 2012 SpringSource, a division of VMware, Inc.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.runtime.internal.core.runtimes;

/**
 * A particular distribution of Virgo.
 *
 * @author Miles Parker
 *
 */
public enum InstallationType {
    TOMCAT("Virgo Server for Apache Tomcat"), JETTY("Virgo Jetty Server"), KERNEL("Virgo Kernel"), NANO("Virgo Nano");

    private final String name;

    InstallationType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
