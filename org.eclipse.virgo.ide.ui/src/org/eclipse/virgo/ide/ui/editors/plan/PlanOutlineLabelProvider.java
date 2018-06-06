/*********************************************************************
 * Copyright (c) 2010 SpringSource, a division of VMware, Inc.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.ui.editors.plan;

import org.eclipse.wst.xml.ui.internal.contentoutline.JFaceNodeLabelProvider;
import org.w3c.dom.Node;

/**
 * @author Christian Dupuis
 * @since 2.3.1
 */
public class PlanOutlineLabelProvider extends JFaceNodeLabelProvider {

    @Override
    public String getText(Object o) {

        Node node = (Node) o;
        String shortNodeName = node.getLocalName();
        String text = shortNodeName;

        return text;
    }
}
