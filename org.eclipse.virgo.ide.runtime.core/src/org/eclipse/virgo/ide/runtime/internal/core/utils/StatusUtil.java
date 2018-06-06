/*********************************************************************
 * Copyright (c) 2012 SpringSource, a division of VMware, Inc.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.runtime.internal.core.utils;

import java.io.IOException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.virgo.ide.runtime.core.ServerCorePlugin;

/**
 *
 * @author mparker
 *
 */
public class StatusUtil {

    public static void error(String message, Exception e) {
        error(message, e, StatusManager.LOG);
    }

    /**
     * @param message the message to be reported
     * @param e the cause
     * @param style a bitmask of style bits as enumerated on {@link StatusManager}
     */
    public static void error(String message, Exception e, int style) {
        StatusManager.getManager().handle(new Status(IStatus.ERROR, ServerCorePlugin.PLUGIN_ID, "An IO Exception occurred.", e), style);
    }

    public static void error(Exception e) {
        if (e instanceof IOException) {
            error("An IO Exception occurred.", e);
        }
        error("An unexpected exception occurred.", e);
    }
}
