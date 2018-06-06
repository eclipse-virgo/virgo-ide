/*********************************************************************
 * Copyright (c) 2009, 2012 SpringSource, a division of VMware, Inc.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.manifest.core.editor.model;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.pde.internal.core.ibundle.IBundleModelFactory;
import org.eclipse.pde.internal.core.text.bundle.BundleModel;

/**
 * @author Leo Dos Santos
 */
public class SpringBundleModel extends BundleModel {

    private IBundleModelFactory fFactory;

    public SpringBundleModel(String string, boolean isReconciling) {
        this(new Document(string), isReconciling);
    }

    public SpringBundleModel(IDocument document, boolean isReconciling) {
        super(document, isReconciling);
    }

    @Override
    public IBundleModelFactory getFactory() {
        if (this.fFactory == null) {
            this.fFactory = new SpringBundleModelFactory(this);
        }
        return this.fFactory;
    }

}
