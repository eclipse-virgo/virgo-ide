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

import java.util.Map;

/**
 * @author Christian Dupuis
 */
public interface BundleAdmin {

    Map<Long, Bundle> retrieveBundles();

    String execute(String cmdLine);

}
