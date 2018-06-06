/*********************************************************************
 * Copyright (c) 2009, 2012 SpringSource, a division of VMware, Inc.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.ui.editors;

import org.eclipse.core.resources.IProject;

/**
 * @author Christian Dupuis
 */
public class ParUtils {

    public static String getSymbolicName(IProject project) {
        // For now we are going to use project names instead of
        // Bundle-SymbolicName cause these *must*
        // be unique within the eclipse workspace
        return project.getName();
    }

}
