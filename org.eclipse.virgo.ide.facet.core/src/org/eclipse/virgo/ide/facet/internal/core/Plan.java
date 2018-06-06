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

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.Version;

/**
 * Represents a plan in the workspace and includes the list of referred plans. Note that this class does not intend to
 * represent the full content of a plan file, just the minimum needed by the Tooling for deploying plans to the Virgo
 * Runtime.
 */
public final class Plan extends PlanReference {

    /**
     * Gets this plan as a {@link PlanReference}. Useful for comparing this plan with a reference from another plan to
     * identify whether such other bundle refers to this plan.
     * 
     * @return
     */
    public PlanReference asRefence() {
        return new PlanReference(getName(), getVersion());
    }

    private final List<Artifact> nestedArtifacts;

    /* default */ Plan(String name, Version version, List<Artifact> nestedArtifacts) {
        super(name, version);
        this.nestedArtifacts = nestedArtifacts;
    }

    /**
     * Returns the list of nested artifacts.
     * @return
     */
    public List<Artifact> getNestedArtifacts() {
        return nestedArtifacts;
    }

    /**
     * A view over nested artifacts that returns plan references.
     * @return
     */
    public List<PlanReference> getNestedPlans() {
        List<PlanReference> refs = new ArrayList<PlanReference>();
        for (Artifact a : nestedArtifacts) {
            if (a instanceof PlanReference) {
                refs.add((PlanReference) a);
            }
        }
        return refs;
    }
}
