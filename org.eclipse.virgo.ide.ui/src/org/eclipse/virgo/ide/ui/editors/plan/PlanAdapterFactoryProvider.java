/*********************************************************************
 * Copyright (c) 2010 - 2012 SpringSource, a division of VMware, Inc.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.ui.editors.plan;

import org.eclipse.wst.sse.core.internal.ltk.modelhandler.IDocumentTypeHandler;
import org.eclipse.wst.xml.ui.internal.registry.AdapterFactoryProviderForXML;

/**
 * @author Christian Dupuis
 * @since 2.3.1
 */
public class PlanAdapterFactoryProvider extends AdapterFactoryProviderForXML {

    @Override
    public boolean isFor(IDocumentTypeHandler contentTypeDescription) {
        return contentTypeDescription instanceof PlanModelHandler;
    }

}
