/*********************************************************************
 * Copyright (c) 2012 SpringSource, a division of VMware, Inc.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.manifest.internal.core.model;

/**
 * @author Leo Dos Santos
 */
public class AbstractManifestElement {

    private AbstractManifestElement parent;

    private String name;

    public AbstractManifestElement(AbstractManifestElement parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    public AbstractManifestElement[] getChildren() {
        return new AbstractManifestElement[0];
    }

    public String getName() {
        return this.name;
    }

    public AbstractManifestElement getParent() {
        return this.parent;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParent(AbstractManifestElement parent) {
        this.parent = parent;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (this.name == null ? 0 : this.name.hashCode());
        result = prime * result + (this.parent == null ? 0 : this.parent.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AbstractManifestElement other = (AbstractManifestElement) obj;
        if (this.name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!this.name.equals(other.name)) {
            return false;
        }
        if (this.parent == null) {
            if (other.parent != null) {
                return false;
            }
        } else if (!this.parent.equals(other.parent)) {
            return false;
        }
        return true;
    }

}
