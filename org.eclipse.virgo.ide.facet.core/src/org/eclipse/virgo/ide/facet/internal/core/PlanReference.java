/*********************************************************************
 * Copyright (c) 2016 GianMaria Romanato
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.facet.internal.core;

import org.osgi.framework.Version;

/**
 * Represents a reference to a nested plan. Provides a proper implementation of {@link #equals(Object)} and
 * {@link #hashCode()} and can be used in collections for structural equality.
 */
public class PlanReference extends Artifact {

    public PlanReference(String name, Version version) {
        super(name, version);
    }

}