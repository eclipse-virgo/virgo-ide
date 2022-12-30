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
package org.eclipse.virgo.ide.framework.editor.ui.internal.overview;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.virgo.ide.framework.editor.core.model.IBundle;
import org.eclipse.virgo.ide.framework.editor.ui.dependencies.BundleDependencyEditorPage;
import org.eclipse.virgo.ide.framework.editor.ui.overview.BundleInformationEditorPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IDetailsPageProvider;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Christian Dupuis
 * @author Steffen Pingel
 * @author Kaloyan Raev
 */
public class BundleInformationMasterDetailsBlock extends MasterDetailsBlock {

	BundleInformationDetailsPart bundleDetailsPart;

	private BundleInformationMasterPart bundleMasterPart;

	private final MultiPageEditorPart serverEditor;

	private final BundleInformationEditorPage editorPage;

	private final IServer server;

	public BundleInformationMasterDetailsBlock(BundleInformationEditorPage bundleInformationEditorPage,
			MultiPageEditorPart serverEditor, IServer server) {
		this.editorPage = bundleInformationEditorPage;
		this.serverEditor = serverEditor;
		this.server = server;
	}

	@Override
	public void createContent(IManagedForm managedForm) {
		final ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		form.getBody().setLayout(layout);
		sashForm = new MDSashForm(form.getBody(), SWT.NULL);
		sashForm.setData("form", managedForm); //$NON-NLS-1$
		toolkit.adapt(sashForm, false, false);
		sashForm.setMenu(form.getBody().getMenu());
		sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		createMasterPart(managedForm, sashForm);
		createDetailsPart(managedForm, sashForm);
		hookResizeListener();
		createToolBarActions(managedForm);
		form.updateToolBar();

		layout = new GridLayout(1, true);
		layout.marginTop = 6;
		layout.marginLeft = 6;
		managedForm.getForm().getBody().setLayout(layout);
	}

	private void createDetailsPart(final IManagedForm mform, Composite parent) {
		super.detailsPart = new DetailsPart(mform, mform.getToolkit().createPageBook(parent, SWT.V_SCROLL));
		mform.addPart(super.detailsPart);
		registerPages(super.detailsPart);
	}

	@Override
	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {
		bundleMasterPart = new BundleInformationMasterPart(parent, managedForm.getToolkit(), ExpandableComposite.TWISTIE
				| ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR | Section.DESCRIPTION
				| ExpandableComposite.FOCUS_TITLE, this);
		managedForm.addPart(bundleMasterPart);
		bundleMasterPart.createContents();
	}

	@Override
	protected void createToolBarActions(IManagedForm managedForm) {
		// nothing
	}

	@Override
	protected void registerPages(DetailsPart targetDetailsPart) {
		this.bundleDetailsPart = new BundleInformationDetailsPart(this);
		targetDetailsPart.setPageProvider(new IDetailsPageProvider() {
			
			public Object getPageKey(Object object) {
				if (object instanceof IBundle) {
					return IBundle.class;
				}
				return object.getClass();
			}
			
			public IDetailsPage getPage(Object key) {
				if (key.equals(IBundle.class)) {
					return BundleInformationMasterDetailsBlock.this.bundleDetailsPart;
				}
				return null;
			}
		});
	}

	/**
	 * @param bundles
	 */
	public void refresh(Map<Long, IBundle> bundles) {
		if (bundleMasterPart.refresh(bundles)) {
			bundleDetailsPart.refresh(bundles);
			
			BundleDependencyEditorPage depPage = getDependencyPage();
			if (depPage != null) {
				depPage.refresh(bundles);
			}
		}
	}

	private void hookResizeListener() {
		Listener listener = ((MDSashForm) sashForm).listener;
		Control[] children = sashForm.getChildren();
		for (Control element : children) {
			if (element instanceof Sash) {
				continue;
			}
			element.addListener(SWT.Resize, listener);
		}
	}

	void onSashPaint(Event e) {
		Sash sash = (Sash) e.widget;
		IManagedForm form = (IManagedForm) sash.getParent().getData("form"); //$NON-NLS-1$
		FormColors colors = form.getToolkit().getColors();
		boolean vertical = (sash.getStyle() & SWT.VERTICAL) != 0;
		GC gc = e.gc;
		Boolean hover = (Boolean) sash.getData("hover"); //$NON-NLS-1$
		gc.setBackground(colors.getColor(IFormColors.TB_BG));
		gc.setForeground(colors.getColor(IFormColors.TB_BORDER));
		Point size = sash.getSize();
		if (vertical) {
			if (hover != null) {
				gc.fillRectangle(0, 0, size.x, size.y);
				// else
				// gc.drawLine(1, 0, 1, size.y-1);
			}
		}
		else {
			if (hover != null) {
				gc.fillRectangle(0, 0, size.x, size.y);
				// else
				// gc.drawLine(0, 1, size.x-1, 1);
			}
		}
	}

	class MDSashForm extends SashForm {

		List<Sash> sashes = new ArrayList<Sash>();

		Listener listener = new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.MouseEnter:
					e.widget.setData("hover", Boolean.TRUE); //$NON-NLS-1$
					((Control) e.widget).redraw();
					break;
				case SWT.MouseExit:
					e.widget.setData("hover", null); //$NON-NLS-1$
					((Control) e.widget).redraw();
					break;
				case SWT.Paint:
					onSashPaint(e);
					break;
				case SWT.Resize:
					hookSashListeners();
					break;
				default:
					// only above events need to be taken care of
					break;
				}
			}
		};

		public MDSashForm(Composite parent, int style) {
			super(parent, style);
		}

		@Override
		public void layout(boolean changed) {
			super.layout(changed);
			hookSashListeners();
		}

		@Override
		public void layout(Control[] children) {
			super.layout(children);
			hookSashListeners();
		}

		void hookSashListeners() {
			purgeSashes();
			Control[] children = getChildren();
			for (Control element : children) {
				if (element instanceof Sash) {
					Sash sash = (Sash) element;
					if (sashes.contains(sash)) {
						continue;
					}
					sash.addListener(SWT.Paint, listener);
					sash.addListener(SWT.MouseEnter, listener);
					sash.addListener(SWT.MouseExit, listener);
					sashes.add(sash);
				}
			}
		}

		private void purgeSashes() {
			for (Iterator<Sash> iter = sashes.iterator(); iter.hasNext();) {
				Sash sash = iter.next();
				if (sash.isDisposed()) {
					iter.remove();
				}
			}
		}
	}

	public void clear() {
		bundleMasterPart.clear();
	}
	
	private BundleDependencyEditorPage getDependencyPage() {
		IEditorPart[] parts = serverEditor.findEditors(editorPage.getEditorInput());
		for (IEditorPart part : parts) {
			if (part instanceof BundleDependencyEditorPage) {
				return (BundleDependencyEditorPage) part;
			}
		}
		return null;
	}

	public void openDependencyPage(String bundle, String version) {
		BundleDependencyEditorPage depPage = getDependencyPage();
		if (depPage != null) {
			serverEditor.setActiveEditor(depPage);
			depPage.showDependenciesForBundle(bundle, version);
		}
	}

	/**
	 * @return the server
	 */
	public IServer getServer() {
		return server;
	}

	public void setSelectedBundle(IBundle bundle) {
		bundleMasterPart.setSelectedBundle(bundle);
	}

	public void refresh() {
		bundleMasterPart.updateButtonState();
	}
	
	public BundleInformationEditorPage getEditorPage() {
		return this.editorPage;
	}
	
}
