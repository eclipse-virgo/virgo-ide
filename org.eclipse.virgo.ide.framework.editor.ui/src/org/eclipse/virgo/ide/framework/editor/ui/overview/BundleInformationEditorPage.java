/*******************************************************************************
 * Copyright (c) 2009, 2011 SpringSource, a divison of VMware, Inc. and others
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     SpringSource, a division of VMware, Inc. - initial API and implementation
 *     SAP AG - enhancements
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.virgo.ide.framework.editor.ui.overview;

import java.util.Map;

import org.eclipse.virgo.ide.framework.editor.core.model.IBundle;
import org.eclipse.virgo.ide.framework.editor.ui.internal.AbstractBundleEditorPage;
import org.eclipse.virgo.ide.framework.editor.ui.internal.overview.BundleInformationMasterDetailsBlock;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.wst.server.ui.ServerUICore;
import org.eclipse.wst.server.ui.internal.editor.ServerEditorPartInput;
import org.eclipse.wst.server.ui.internal.editor.ServerResourceCommandManager;


/**
 * @author Christian Dupuis
 * @author Steffen Pingel
 * @author Kaloyan Raev
 */
@SuppressWarnings("restriction")
public class BundleInformationEditorPage extends AbstractBundleEditorPage {

	private BundleInformationMasterDetailsBlock masterDetailsBlock;

	private ServerResourceCommandManager commandManager;

	@Override
	protected void createBundleContent(Composite parent) {
		if (mform == null) {
			mform = new ManagedForm(parent);
		}

		FormToolkit toolkit = getFormToolkit(parent.getDisplay());

		sform = mform.getForm();
		sform.getForm().setSeparatorVisible(true);
		sform.getForm().setText("Bundle Information");
		sform.setExpandHorizontal(true);
		sform.setExpandVertical(true);
		sform.setImage(ServerUICore.getLabelProvider().getImage(getServer()));
		toolkit.decorateFormHeading(sform.getForm());

		masterDetailsBlock = new BundleInformationMasterDetailsBlock(this, commandManager.getServerEditor(),
				getServer().getOriginal());
		masterDetailsBlock.createContent(mform);

	}

	@Override
	protected void enablePage() {
		super.enablePage();
		setInfoStatus("Click the 'Refresh' button for fetching data from server.");
		masterDetailsBlock.refresh();
	}
	
	@Override
	protected void disablePage() {
		super.disablePage();
		masterDetailsBlock.clear();
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) {
		super.init(site, input);
		commandManager = ((ServerEditorPartInput) input).getServerCommandManager();
	}

	public void showOverviewForBundle(final IBundle bundle) {
		masterDetailsBlock.setSelectedBundle(bundle);
	}

	public void refresh(Map<Long, IBundle> bundles) {
		masterDetailsBlock.refresh(bundles);
	}

}
