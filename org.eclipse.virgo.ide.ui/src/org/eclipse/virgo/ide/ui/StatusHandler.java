/*********************************************************************
 * Copyright (c) 2009, 2012 SpringSource, a division of VMware, Inc.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.ui;

import org.eclipse.core.runtime.IStatus;

/**
 * @author Christian Dupuis
 */
public class StatusHandler {

    public static void log(IStatus status) {
        ServerIdeUiPlugin.getDefault().getLog().log(status);
    }

}
