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

import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.virgo.ide.eclipse.editors.AbstractParXmlEditorPage;
import org.eclipse.virgo.ide.par.Par;
import org.eclipse.virgo.ide.ui.ServerIdeUiPlugin;

/**
 * @author Christian Dupuis
 */
public class ParXmlEditorPage extends AbstractParXmlEditorPage {

    public static String ID_EDITOR = "org.eclipse.virgo.ide.ui.editor.par.dependencies";

    public ParXmlEditorPage(ParManifestEditor editor, String id, String title) {
        super(editor, id, title);
    }

    @Override
    protected IFormPart getFormPart(Composite parent, String[] labels) {
        return new ParDependenciesSection(this, parent, labels);
    }

    @Override
    protected void createFormContent(IManagedForm managedForm) {
        super.createFormContent(managedForm);
        ScrolledForm form = managedForm.getForm();
        form.setImage(ServerIdeUiPlugin.getImage("full/obj16/par_obj.gif"));
        form.setText("PAR Editor");
    }

    protected AdapterFactoryEditingDomain getModel() {
        return ((ParManifestEditor) getEditor()).getEditingDomain();
    }

    public Par getPar() {
        return ((ParManifestEditor) getEditor()).getPar();
    }

}
