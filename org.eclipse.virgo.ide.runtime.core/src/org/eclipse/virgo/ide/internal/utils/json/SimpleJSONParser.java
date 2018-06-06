/*********************************************************************
 * Copyright (c) 2009, 2012 SpringSource, a division of VMware, Inc.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.internal.utils.json;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A common interface for very basic JSON parsing. But you shouldn't ever need more than this, JSON just isn't that
 * complicated!
 *
 * @author Miles Parker
 *
 */
public interface SimpleJSONParser {

    /**
     * Implement to process the passed object.
     */
    void parse(JSONObject object) throws JSONException;
}