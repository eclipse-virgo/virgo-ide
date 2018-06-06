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

import org.eclipse.wst.sse.core.internal.document.IDocumentCharsetDetector;
import org.eclipse.wst.sse.core.internal.document.IDocumentLoader;
import org.eclipse.wst.sse.core.internal.ltk.modelhandler.AbstractModelHandler;
import org.eclipse.wst.sse.core.internal.ltk.modelhandler.IModelHandler;
import org.eclipse.wst.sse.core.internal.provisional.IModelLoader;
import org.eclipse.wst.xml.core.internal.encoding.XMLDocumentCharsetDetector;
import org.eclipse.wst.xml.core.internal.encoding.XMLDocumentLoader;
import org.eclipse.wst.xml.core.internal.modelhandler.XMLModelLoader;

/**
 * @author Christian Dupuis
 * @since 2.3.1
 */
public class PlanModelHandler extends AbstractModelHandler implements IModelHandler {

    private static String modelHandlerID = "org.eclipse.virgo.ide.ui.handler.planConfig";

    private static String associatedContentTypeID = "org.eclipse.virgo.ide.facet.core.planContentType";

    public PlanModelHandler() {
        setId(modelHandlerID);
        setAssociatedContentTypeId(associatedContentTypeID);
    }

    @Override
    public IDocumentCharsetDetector getEncodingDetector() {
        return new XMLDocumentCharsetDetector();
    }

    public IDocumentLoader getDocumentLoader() {
        return new XMLDocumentLoader();
    }

    public IModelLoader getModelLoader() {
        return new XMLModelLoader();
    }
}
