/*********************************************************************
 * Copyright (c) 2009, 2012 SpringSource, a division of VMware, Inc.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.runtime.core.artefacts;

/**
 * Types of artefacts that might exist within a repository.
 *
 * @author Miles Parker
 *
 */
public enum ArtefactType {
    BUNDLE("Bundle", "Bundles", 2), LIBRARY("Library", "Libraries", 3), COMBINED("Library Or Bundle", "Libraries and Bundles", 1);

    private final String label;

    private final int ordering;

    private final String pluralLabel;

    ArtefactType(String label, String pluralLabel, int ordering) {
        this.label = label;
        this.pluralLabel = pluralLabel;
        this.ordering = ordering;
    }

    public String getLabel() {
        return this.label;
    }

    public String getPluralLabel() {
        return this.pluralLabel;
    }

    public int getOrdering() {
        return this.ordering;
    }
}
