/*********************************************************************
 * Copyright (c) 2009, 2012 SpringSource, a division of VMware, Inc.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.kernel.osgi.provisioning.tools;

import org.eclipse.virgo.medic.eventlog.EventLogger;
import org.eclipse.virgo.medic.eventlog.Level;
import org.eclipse.virgo.medic.eventlog.LogEvent;

/**
 * NoOp {@link EventLogger} implementation
 *
 * @author Christian Dupuis
 * @since 2.1.1
 */
public class NoOpEventLogger implements EventLogger {

    public void log(LogEvent arg0, Object... arg1) {
    }

    public void log(String arg0, Level arg1, Object... arg2) {
    }

    public void log(LogEvent arg0, Throwable arg1, Object... arg2) {
    }

    public void log(String arg0, Level arg1, Throwable arg2, Object... arg3) {
    }

}
