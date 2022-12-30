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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.ZipFile;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.virgo.ide.framework.editor.core.model.IBundle;
import org.eclipse.virgo.ide.framework.editor.core.model.IPackageExport;
import org.eclipse.virgo.ide.framework.editor.core.model.IPackageImport;
import org.eclipse.virgo.ide.framework.editor.core.model.IServiceReference;
import org.eclipse.virgo.ide.framework.editor.ui.internal.EditorUIPlugin;
import org.eclipse.virgo.ide.framework.editor.ui.internal.overview.BundleInformationDetailsPart.ServicesContentProvider.ServicesHolder;
import org.eclipse.pde.internal.ui.PDEPlugin;
import org.eclipse.pde.internal.ui.PDEPluginImages;
import org.eclipse.pde.internal.ui.editor.JarEntryEditorInput;
import org.eclipse.pde.internal.ui.editor.JarEntryFile;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.osgi.framework.Constants;

/**
 * @author Christian Dupuis
 * @author Kaloyan Raev
 */
@SuppressWarnings("restriction")
public class BundleInformationDetailsPart extends AbstractFormPart implements IDetailsPage {

	class PackageExportContentProvider implements ITreeContentProvider {

		private IBundle cpBundle;

		public void dispose() {
			// empty
		}

		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof IPackageExport) {
				Set<IBundle> returnBundles = new HashSet<IBundle>();
				String name = ((IPackageExport) parentElement).getName();
				String version = ((IPackageExport) parentElement).getVersion();
				String id = cpBundle.getId();

				for (IBundle loopBundle : BundleInformationDetailsPart.this.bundles.values()) {
					for (IPackageImport pi : loopBundle.getPackageImports()) {
						if (pi.getSupplierId().equals(id) && pi.getName().equals(name)
								&& pi.getVersion().equals(version)) {
							returnBundles.add(loopBundle);
						}
					}
				}
				return returnBundles.toArray(new IBundle[returnBundles.size()]);
			}
			return new Object[0];
		}

		public Object[] getElements(Object inputElement) {
			if (cpBundle.getPackageExports().size() > 0) {
				return cpBundle.getPackageExports().toArray();
			}
			return new Object[] { "<no exported packages>" };
		}

		public Object getParent(Object element) {
			return null;
		}

		public boolean hasChildren(Object element) {
			return getChildren(element).length > 0;
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			cpBundle = (IBundle) newInput;
		}
	}

	class PackageImportContentProvider implements ITreeContentProvider {

		private IBundle cpBundle;

		public void dispose() {
			// nothing
		}

		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof IPackageImport) {
 				String supplierId = ((IPackageImport) parentElement).getSupplierId();
				IBundle value = bundles.get(Long.valueOf(supplierId));
				if (value != null) {
					return new Object[] { value };
				}
			}
			return new Object[0];
		}

		public Object[] getElements(Object inputElement) {
			if (cpBundle.getPackageImports().size() > 0) {
				return cpBundle.getPackageImports().toArray();
			}
			return new Object[] { "<no imported packages>" };
		}

		public Object getParent(Object element) {
			return null;
		}

		public boolean hasChildren(Object element) {
			return getChildren(element).length > 0;
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			cpBundle = (IBundle) newInput;
		}
	}

	static class PackageLabelProvider extends LabelProvider {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
		 */
		@Override
		public Image getImage(Object element) {
			if (element instanceof IPackageImport) {
				return PDEPluginImages.get(PDEPluginImages.OBJ_DESC_PACKAGE);
			}
			else if (element instanceof IPackageExport) {
				return PDEPluginImages.get(PDEPluginImages.OBJ_DESC_PACKAGE);
			}
			else if (element instanceof IBundle) {
				return PDEPluginImages.get(PDEPluginImages.OBJ_DESC_BUNDLE);
			}
			return super.getImage(element);
		}

		@Override
		public String getText(Object element) {
			if (element instanceof IPackageImport) {
				return ((IPackageImport) element).getName() + " (" + ((IPackageImport) element).getVersion() + ")";
			}
			else if (element instanceof IPackageExport) {
				return ((IPackageExport) element).getName() + " (" + ((IPackageExport) element).getVersion() + ")";
			}
			else if (element instanceof IBundle) {
				return ((IBundle) element).getSymbolicName() + " (" + ((IBundle) element).getVersion() + ")";
			}
			return super.getText(element);
		}
	}

	static class ServicePropertyComparator extends ViewerComparator {

		@SuppressWarnings("unchecked")
		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			Map.Entry<String, String> p1 = (Entry<String, String>) e1;
			Map.Entry<String, String> p2 = (Entry<String, String>) e2;
			return p1.getKey().compareTo(p2.getKey());
		}
	}

	static class ServicePropertyContentProvider implements IStructuredContentProvider {

		private IServiceReference ref;

		public void dispose() {
			// nothing
		}

		public Object[] getElements(Object inputElement) {
			if (ref != null) {
				return ref.getProperties().entrySet().toArray();
			}
			return new Object[0];
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			if (newInput instanceof IServiceReference) {
				ref = (IServiceReference) newInput;
			}
			else {
				ref = null;
			}
		}

	}

	static class ServicePropertyLabelProvider extends LabelProvider implements ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@SuppressWarnings("unchecked")
		public String getColumnText(Object element, int columnIndex) {
			Map.Entry<String, String> entry = (Entry<String, String>) element;
			if (columnIndex == 0) {
				return entry.getKey();
			}
			else if (columnIndex == 1) {
				return entry.getValue();
			}
			return null;
		}
	}

	class ServicesContentProvider implements ITreeContentProvider {

		class ServicesHolder {

			private final String label;

			private final Set<IServiceReference> refs;

			public ServicesHolder(Set<IServiceReference> refs, String label) {
				this.refs = refs;
				this.label = label;
			}

			/**
			 * @return the label
			 */
			public String getLabel() {
				return label;
			}

			public Set<IServiceReference> getRefs() {
				return refs;
			}
		}

		private IBundle cpBundle;

		public void dispose() {
			// nothing
		}

		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof ServicesHolder) {
				return ((ServicesHolder) parentElement).getRefs().toArray();
			}
			else if (parentElement instanceof IServiceReference) {
				Set<IBundle> bs = new HashSet<IBundle>();
				IServiceReference ref = (IServiceReference) parentElement;
				if (ref.getType() == IServiceReference.Type.IN_USE) {
					bs.add(bundles.get(ref.getBundleId()));
				}
				else if (ref.getType() == IServiceReference.Type.REGISTERED) {
					for (Long id : ref.getUsingBundleIds()) {
						bs.add(bundles.get(id));
					}
				}
				return bs.toArray();
			}
			return new Object[0];
		}

		public Object[] getElements(Object inputElement) {
			Set<ServicesHolder> serviceHolder = new HashSet<ServicesHolder>();
			if (cpBundle.getRegisteredServices().size() > 0) {
				serviceHolder.add(new ServicesHolder(cpBundle.getRegisteredServices(), "Registered Services"));
			}
			if (cpBundle.getServicesInUse().size() > 0) {
				serviceHolder.add(new ServicesHolder(cpBundle.getServicesInUse(), "Services in Use"));
			}
			if (serviceHolder.size() > 0) {
				return serviceHolder.toArray();
			}
			return new Object[] { "<no registered or used services>" };
		}

		public Object getParent(Object element) {
			return null;
		}

		public boolean hasChildren(Object element) {
			return getChildren(element).length > 0;
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			cpBundle = (IBundle) newInput;
		}
	}

	static class ServicesLabelProvider extends LabelProvider {

		@Override
		public Image getImage(Object element) {
			if (element instanceof IServiceReference) {
				return PDEPlugin.getDefault().getLabelProvider().get(PDEPluginImages.DESC_EXTENSIONS_OBJ);
			}
			else if (element instanceof ServicesHolder) {
				return PDEPlugin.getDefault().getLabelProvider().get(PDEPluginImages.DESC_EXTENSIONS_OBJ);
			}
			else if (element instanceof IBundle) {
				return PDEPluginImages.get(PDEPluginImages.OBJ_DESC_BUNDLE);
			}
			return super.getImage(element);
		}

		@Override
		public String getText(Object element) {
			if (element instanceof IServiceReference) {
				String id = ((IServiceReference) element).getProperties().get(Constants.SERVICE_ID);
				if (id.length() > 0) {
					return ((IServiceReference) element).getClazzes()[0] + " (" + id + ")";
				}
				return ((IServiceReference) element).getClazzes()[0];
			}
			else if (element instanceof IBundle) {
				return ((IBundle) element).getSymbolicName() + " (" + ((IBundle) element).getVersion() + ")";
			}
			else if (element instanceof ServicesHolder) {
				return ((ServicesHolder) element).getLabel();
			}
			return super.getText(element);
		}
	}

	IBundle bundle;

	Map<Long, IBundle> bundles;

	private Text bundleSymbolicNameText;

	private FilteredTree exportsTable;

	TreeViewer exportsTableViewer;

	private Text idText;

	private FilteredTree importsTable;

	TreeViewer importsTableViewer;

	private Text locationText;

	private Text manifestText;

	final BundleInformationMasterDetailsBlock masterDetailsBlock;

	private Text providerText;

	private Table servicePropertiesTable;

	TableViewer servicePropertiesTableViewer;

	private FilteredTree servicesTable;

	TreeViewer servicesTableViewer;

	private Text stateText;

	private Text versionText;

	public BundleInformationDetailsPart(BundleInformationMasterDetailsBlock bundleInformationMasterDetailsBlock) {
		this.masterDetailsBlock = bundleInformationMasterDetailsBlock;
	}

	public void createContents(Composite parent) {
		FormToolkit toolkit = getManagedForm().getToolkit();
		GridLayout layout = new GridLayout();
		layout.marginTop = -5;
		layout.marginLeft = 6;
		parent.setLayout(layout);

		GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		Section detailsSection = toolkit.createSection(parent, ExpandableComposite.TWISTIE
				| ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR | Section.DESCRIPTION
				| ExpandableComposite.FOCUS_TITLE);
		detailsSection.setText("Bundle Details");
		detailsSection.setDescription("Details about the selected bundle.");
		detailsSection.setLayoutData(data);
		createSectionToolbar(detailsSection, toolkit, new Action("Show dependency graph for bundle",
				PDEPluginImages.DESC_COMGROUP_OBJ) {
			@Override
			public void run() {
				masterDetailsBlock.openDependencyPage(bundle.getSymbolicName(), bundle.getVersion());
			}
		});

		Composite detailsComposite = toolkit.createComposite(detailsSection);
		layout = new GridLayout();
		layout.numColumns = 4;
		detailsComposite.setLayout(layout);

		data = new GridData(GridData.FILL_BOTH);
		data.minimumHeight = 100;
		data.heightHint = 100;
		detailsComposite.setLayoutData(data);

		toolkit.paintBordersFor(detailsComposite);
		detailsSection.setClient(detailsComposite);

		Label idLabel = toolkit.createLabel(detailsComposite, "Id:");
		idLabel.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		idText = toolkit.createText(detailsComposite, "");
		idText.setEditable(false);
		idText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));

		Label stateLabel = toolkit.createLabel(detailsComposite, "State:");
		stateLabel.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		stateText = toolkit.createText(detailsComposite, "", SWT.NONE);
		stateText.setEditable(false);
		stateText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));

		Label bundleSymbolicNameLabel = toolkit.createLabel(detailsComposite, "Symbolic-Name:");
		bundleSymbolicNameLabel.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		bundleSymbolicNameText = toolkit.createText(detailsComposite, "", SWT.NONE);
		bundleSymbolicNameText.setEditable(false);
		bundleSymbolicNameText
				.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
		GridDataFactory.fillDefaults().grab(true, false).span(3, 1).applyTo(bundleSymbolicNameText);

		Label versionLabel = toolkit.createLabel(detailsComposite, "Version:");
		versionLabel.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		versionText = toolkit.createText(detailsComposite, "");
		versionText.setEditable(false);
		versionText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));

		Label providerLabel = toolkit.createLabel(detailsComposite, "Provider:");
		providerLabel.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		providerText = toolkit.createText(detailsComposite, "", SWT.NONE);
		providerText.setEditable(false);
		providerText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));

		Label locationLabel = toolkit.createLabel(detailsComposite, "Location:");
		locationLabel.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		locationText = toolkit.createText(detailsComposite, "", SWT.NONE);
		locationText.setEditable(false);
		locationText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
		GridDataFactory.fillDefaults().grab(true, false).span(3, 1).applyTo(locationText);

		Link openDependenciesText = new Link(detailsComposite, SWT.NONE);
		openDependenciesText.setText("<a href=\"open\">Show dependency graph for bundle</a>");
		openDependenciesText.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				masterDetailsBlock.openDependencyPage(bundle.getSymbolicName(), bundle.getVersion());
			}
		});
		GridDataFactory.fillDefaults().grab(true, false).span(4, 1).applyTo(openDependenciesText);

		Section manifestSection = toolkit.createSection(parent, ExpandableComposite.TWISTIE
				| ExpandableComposite.TITLE_BAR | Section.DESCRIPTION | ExpandableComposite.FOCUS_TITLE);
		manifestSection.setText("Manifest");
		manifestSection.setDescription("Displays the bundle manifest.");
		manifestSection.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));

		Composite manifestComposite = toolkit.createComposite(manifestSection);
		layout = new GridLayout();
		layout.numColumns = 1;
		manifestComposite.setLayout(layout);
		manifestComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		toolkit.paintBordersFor(manifestComposite);
		manifestSection.setClient(manifestComposite);
		createSectionToolbar(manifestSection, toolkit, new Action("Open MANIFEST.MF in editor",
				PDEPluginImages.DESC_TOC_LEAFTOPIC_OBJ) {
			@Override
			public void run() {
				openBundleEditor(bundle);
			}
		});

		manifestText = toolkit.createText(manifestComposite, "", SWT.MULTI | SWT.WRAP);
		manifestText.setEditable(false);
		manifestText.setFont(JFaceResources.getTextFont());
		GC gc = new GC(manifestText);
		FontMetrics fm = gc.getFontMetrics();
		int height = 10 * fm.getHeight();
		gc.dispose();

		data = new GridData(GridData.FILL_HORIZONTAL);
		data.heightHint = manifestText.computeSize(SWT.DEFAULT, height).y;
		manifestText.setLayoutData(data);

		Link openManifestText = new Link(manifestComposite, SWT.NONE);
		openManifestText.setText("<a href=\"open\">Open MANIFEST.MF in editor</a>");
		openManifestText.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				openBundleEditor(bundle);
			}
		});

		Section importsSection = toolkit.createSection(parent, ExpandableComposite.TWISTIE
				| ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR | Section.DESCRIPTION
				| ExpandableComposite.FOCUS_TITLE);
		importsSection.setText("Package Imports");
		importsSection.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
		importsSection.setDescription("Information about visible packages and consumers of those packages.");

		Composite importsComposite = toolkit.createComposite(importsSection);
		layout = new GridLayout();
		layout.numColumns = 1;
		importsComposite.setLayout(layout);
		importsComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		toolkit.paintBordersFor(importsComposite);
		importsSection.setClient(importsComposite);
		ImageDescriptor collapseAllImageDescriptor = PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_COLLAPSEALL);
		createSectionToolbar(importsSection, toolkit, new Action("Collapse All", collapseAllImageDescriptor) {
			@Override
			public void run() {
				importsTableViewer.collapseAll();
			}
		});

		importsTable = new FilteredTree(importsComposite, SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER,
				new PatternFilter());
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.heightHint = 200;
		importsTable.getViewer().getControl().setLayoutData(data);

		importsTableViewer = importsTable.getViewer();
		importsTableViewer.setContentProvider(new PackageImportContentProvider());
		importsTableViewer.setLabelProvider(new PackageLabelProvider());
		importsTableViewer.setAutoExpandLevel(2);

		Section exportsSection = toolkit.createSection(parent, ExpandableComposite.TWISTIE
				| ExpandableComposite.TITLE_BAR | Section.DESCRIPTION | ExpandableComposite.FOCUS_TITLE);
		exportsSection.setText("Package Exports");
		exportsSection.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
		exportsSection.setDescription("Information about exported packages and bundles that use these packages.");

		Composite exportsComposite = toolkit.createComposite(exportsSection);
		layout = new GridLayout();
		layout.numColumns = 1;
		exportsComposite.setLayout(layout);
		exportsComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		toolkit.paintBordersFor(exportsComposite);
		exportsSection.setClient(exportsComposite);
		createSectionToolbar(exportsSection, toolkit, new Action("Collapse All", collapseAllImageDescriptor) {
			@Override
			public void run() {
				exportsTableViewer.collapseAll();
			}
		});

		exportsTable = new FilteredTree(exportsComposite, SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER,
				new PatternFilter());
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.heightHint = 200;
		exportsTable.getViewer().getControl().setLayoutData(data);

		exportsTableViewer = exportsTable.getViewer();
		exportsTableViewer.setContentProvider(new PackageExportContentProvider());
		exportsTableViewer.setLabelProvider(new PackageLabelProvider());
		exportsTableViewer.setAutoExpandLevel(2);

		Section servicesSection = toolkit.createSection(parent, ExpandableComposite.TWISTIE
				| ExpandableComposite.TITLE_BAR | Section.DESCRIPTION | ExpandableComposite.FOCUS_TITLE);
		servicesSection.setText("Services");
		servicesSection.setDescription("Details about registered and in-use services.");
		servicesSection.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));

		Composite servicesComposite = toolkit.createComposite(servicesSection);
		layout = new GridLayout();
		layout.numColumns = 1;
		servicesComposite.setLayout(layout);
		servicesComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		toolkit.paintBordersFor(servicesComposite);
		servicesSection.setClient(servicesComposite);
		createSectionToolbar(servicesSection, toolkit, new Action("Collapse All", collapseAllImageDescriptor) {
			@Override
			public void run() {
				servicesTableViewer.collapseAll();
			}
		});

		servicesTable = new FilteredTree(servicesComposite, SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER,
				new PatternFilter());
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.heightHint = 200;
		servicesTable.getViewer().getControl().setLayoutData(data);

		servicesTableViewer = servicesTable.getViewer();
		servicesTableViewer.setContentProvider(new ServicesContentProvider());
		servicesTableViewer.setLabelProvider(new ServicesLabelProvider());
		servicesTableViewer.setAutoExpandLevel(2);

		servicesTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				servicePropertiesTableViewer.setInput(((IStructuredSelection) event.getSelection()).getFirstElement());
			}
		});

		Label servicePropertiesLabel = toolkit.createLabel(servicesComposite, "Service Properties:");
		servicePropertiesLabel.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));

		servicePropertiesTable = toolkit.createTable(servicesComposite, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.heightHint = 100;
		servicePropertiesTable.setLayoutData(data);
		servicePropertiesTable.setLinesVisible(true);
		TableColumn keyColumn = new TableColumn(servicePropertiesTable, SWT.LEFT);
		keyColumn.setText("Key");
		keyColumn.setWidth(150);
		TableColumn valueColumn = new TableColumn(servicePropertiesTable, SWT.LEFT);
		valueColumn.setText("Value");
		valueColumn.setWidth(450);
		servicePropertiesTable.setHeaderVisible(true);

		servicePropertiesTableViewer = new TableViewer(servicePropertiesTable);
		servicePropertiesTableViewer.setContentProvider(new ServicePropertyContentProvider());
		servicePropertiesTableViewer.setLabelProvider(new ServicePropertyLabelProvider());
		servicePropertiesTableViewer.setComparator(new ServicePropertyComparator());

	}

	public void refresh(Map<Long, IBundle> newBundles) {
		this.bundles = newBundles;
	}

	public void selectionChanged(IFormPart part, ISelection selection) {
		bundle = (IBundle) ((IStructuredSelection) selection).getFirstElement();

		idText.setText(bundle.getId());
		stateText.setText(bundle.getState());
		bundleSymbolicNameText.setText(bundle.getSymbolicName());
		versionText.setText(bundle.getVersion());
		String vendor = bundle.getHeaders().get("Bundle-Vendor");
		providerText.setText((vendor == null) ? "" : vendor);
		locationText.setText(bundle.getLocation());

		importsTableViewer.setInput(bundle);
		importsTableViewer.setAutoExpandLevel(2);

		exportsTableViewer.setInput(bundle);
		exportsTableViewer.setAutoExpandLevel(2);

		StringBuilder builder = new StringBuilder();
		for (Map.Entry<String, String> header : bundle.getHeaders().entrySet()) {
			builder.append(header.getKey()).append(": ").append(header.getValue()).append(Text.DELIMITER);
		}
		manifestText.setText(builder.toString());

		servicesTableViewer.setInput(bundle);
		servicesTableViewer.setAutoExpandLevel(2);

	}

	/**
	 * @param toolkit  
	 */
	private static void createSectionToolbar(Section section, FormToolkit toolkit, Action... actions) {
		ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
		ToolBar toolbar = toolBarManager.createControl(section);
		final Cursor handCursor = new Cursor(Display.getCurrent(), SWT.CURSOR_HAND);
		toolbar.setCursor(handCursor);
		// Cursor needs to be explicitly disposed
		toolbar.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				if (! handCursor.isDisposed()) {
					handCursor.dispose();
				}
			}
		});

		for (Action action : actions) {
			toolBarManager.add(action);
		}

		toolBarManager.update(true);

		section.setTextClient(toolbar);
	}
	

	
	void openBundleEditor(IBundle openBundle) {
		try {
			String fileName = "META-INF/MANIFEST.MF";
			IEditorInput input = getEditorInput(getBundleRoot(openBundle), fileName);
			IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(fileName, getContentType());
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(); 
			page.openEditor(input, desc.getId());
		} catch (Exception e) {
			EditorUIPlugin.log(e);
		}
	}
	
	private static File getBundleRoot(IBundle bundle) throws URISyntaxException {
		String location = bundle.getLocation();
		
		// if location URI starts with "reference:" then remove it
		if (location.startsWith("reference:")) {
			location = location.substring("reference:".length());
		}
		
		File bundleRoot;
		if (location.startsWith("file:/")) {
			// this is a file URI
			location = sanitizeLocation(location);

			bundleRoot = new File(new URI(location));
		} else if (location.startsWith("file:")) {
			// this is an invalid file URI, probably it's a standard file path
			location = sanitizeLocation(location);
			location = location.substring("file:".length());
			bundleRoot = new File(location);
		} else {
			// not a file URI or a standard file path - very high chance to throw exception
			bundleRoot = new File(new URI(location));
		}
		return bundleRoot;
	}

	private static String sanitizeLocation(String location) {
		int offsetOfAnchor = location.indexOf('#');
			if (offsetOfAnchor > 0) {
			location = location.substring(0, offsetOfAnchor);
			if (location.toLowerCase().endsWith(".jar/")) {
				location = location.substring(0, offsetOfAnchor-1);
			}
		}
		return location;
	}
	
	private static IEditorInput getEditorInput(File bundleRoot, String filename) {
		IEditorInput input = null;
		if (bundleRoot.isFile()) {
			try {
				ZipFile zipFile = new ZipFile(bundleRoot);
				if (zipFile.getEntry(filename) != null) {
					input = new JarEntryEditorInput(new JarEntryFile(zipFile, filename));
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			File file = new File(bundleRoot, filename);
			if (file.exists()) {
				IFileStore store;
				try {
					store = EFS.getStore(file.toURI());
					input = new FileStoreEditorInput(store);
				}
				catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}
		return input;
	}
	
	private static IContentType getContentType() {
		// first check if an add-on (like Virgo Tooling) has registered a specialized content type
		IContentType type = Platform.getContentTypeManager().findContentTypeFor("META-INF/MANIFEST.MF");
		if (type == null) {
			// then check if PDE is available
			type = Platform.getContentTypeManager().getContentType("org.eclipse.pde.bundleManifest");
		}
		if (type == null) {
			// in worst case use the default plain text content type
			type = Platform.getContentTypeManager().getContentType(IContentTypeManager.CT_TEXT);
		}
		return type;
	}
	
}
