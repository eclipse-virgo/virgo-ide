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

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.ui.ISharedImages;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.actions.ShowInPackageViewAction;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.service.resolver.ExportPackageDescription;
import org.eclipse.pde.core.IBaseModel;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.internal.core.text.bundle.ExportPackageObject;
import org.eclipse.pde.internal.core.text.bundle.ImportPackageHeader;
import org.eclipse.pde.internal.core.text.bundle.ImportPackageObject;
import org.eclipse.pde.internal.core.text.bundle.PackageObject;
import org.eclipse.pde.internal.core.util.PDEJavaHelper;
import org.eclipse.pde.internal.ui.PDEPlugin;
import org.eclipse.pde.internal.ui.PDEPluginImages;
import org.eclipse.pde.internal.ui.PDEUIMessages;
import org.eclipse.pde.internal.ui.editor.PDEFormPage;
import org.eclipse.pde.internal.ui.util.SWTUtil;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.virgo.ide.bundlerepository.domain.OsgiVersion;
import org.eclipse.virgo.ide.bundlerepository.domain.PackageExport;
import org.eclipse.virgo.ide.manifest.core.IHeaderConstants;
import org.eclipse.virgo.ide.runtime.core.artefacts.Artefact;
import org.eclipse.virgo.ide.runtime.core.artefacts.ArtefactRepository;
import org.eclipse.virgo.ide.runtime.core.artefacts.BundleArtefact;
import org.eclipse.virgo.ide.runtime.core.artefacts.IArtefact;
import org.eclipse.virgo.ide.runtime.core.artefacts.IArtefactTyped;
import org.eclipse.virgo.ide.runtime.core.artefacts.LocalBundleArtefact;
import org.eclipse.virgo.ide.runtime.core.provisioning.RepositoryUtils;
import org.eclipse.virgo.ide.ui.ServerIdeUiPlugin;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;

/**
 * @author Christian Dupuis
 */
public class BundleImportPackageSection extends AbstractImportSection {

    private static final String DESCRIPTION = "Specify packages on which this bundle depends without explicitly identifying their originating bundle.";

    private Action fGoToAction;

    private static final int ADD_INDEX = 0;

    private static final int REMOVE_INDEX = 1;

    private static final int PROPERTIES_INDEX = 2;

    public BundleImportPackageSection(PDEFormPage page, Composite parent) {
        super(page, parent, Section.DESCRIPTION, new String[] { PDEUIMessages.ImportPackageSection_add, PDEUIMessages.ImportPackageSection_remove,
            PDEUIMessages.ImportPackageSection_properties });
        getSection().setText("Import Package");
        getSection().setDescription(DESCRIPTION);
        getTablePart().setEditable(false);
    }

    protected void setElementsLocal(ImportListSelectionDialog dialog) {
        IProject project = ((BundleManifestEditor) this.getPage().getEditor()).getCommonProject();
        Set<PackageExport> packages = RepositoryUtils.getImportPackageProposals(project, "");
        ImportPackageHeader header = (ImportPackageHeader) getBundle().getManifestHeader(Constants.IMPORT_PACKAGE);
        Set<PackageExport> filteredElements = new HashSet<PackageExport>();

        if (header != null) {
            ImportPackageObject[] filter = header.getPackages();
            for (PackageExport proposal : packages) {
                for (ImportPackageObject imported : filter) {
                    if (proposal.getName().equalsIgnoreCase(imported.getName())) {
                        filteredElements.add(proposal);
                    }
                }
            }
            packages.removeAll(filteredElements);
        }
        dialog.setElements(packages.toArray());
    }

    protected void setElementsRemote(ImportListSelectionDialog dialog) {
        Iterable<IArtefact> bundles = null;
        ArtefactRepository bundleRepository = RepositoryUtils.searchForArtifacts("", true, false);
        bundles = bundleRepository.getBundles();

        Set<PackageExport> allPackageExports = new HashSet<PackageExport>();
        for (IArtefactTyped currBundleArtefact : bundles) {
            allPackageExports.addAll(((BundleArtefact) currBundleArtefact).getExports());
        }

        dialog.setElements(allPackageExports.toArray());
    }

    @Override
    protected ITableLabelProvider getLabelProvider() {
        return new ImportPackageLabelProvider();
    }

    class ImportPackageLabelProvider extends AbstractSectionViewerLabelProvider {

        @Override
        public Image getColumnImage(Object element, int columnIndex) {
            return JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_PACKAGE);
        }

        @Override
        public String getColumnText(Object element, int columnIndex) {
            ImportPackageObject importPackageObject = (ImportPackageObject) element;
            String label = importPackageObject.getName();
            if (null != importPackageObject.getVersion()) {
                label += " " + importPackageObject.getVersion();
            }
            return label;
        }
    }

    @Override
    protected void fillContextMenu(IMenuManager manager) {
        final ISelection selection = this.fViewer.getSelection();
        boolean singleSelection = selection instanceof IStructuredSelection && ((IStructuredSelection) selection).size() == 1;
        if (singleSelection) {
            manager.add(this.fGoToAction);
        }
        super.fillContextMenu(manager);
    }

    @Override
    protected void handleRemove() {
        Object[] removed = ((IStructuredSelection) this.fViewer.getSelection()).toArray();
        ImportPackageHeader importPackageHeader = (ImportPackageHeader) getBundle().getManifestHeader(Constants.IMPORT_PACKAGE);
        for (Object element : removed) {
            importPackageHeader.removePackage((PackageObject) element);
        }
    }

    @Override
    protected void handleOpenProperties() {
        Object[] selected = ((IStructuredSelection) this.fViewer.getSelection()).toArray();
        ImportPackageObject first = (ImportPackageObject) selected[0];
        BundleDependencyPropertiesDialog dialog = new BundleDependencyPropertiesDialog(isEditable(), false, false, first.isOptional(),
            first.getVersion(), true, true);
        dialog.create();
        SWTUtil.setDialogSize(dialog, 400, -1);
        if (selected.length == 1) {
            dialog.setTitle(((ImportPackageObject) selected[0]).getName());
        } else {
            dialog.setTitle(PDEUIMessages.ExportPackageSection_props);
        }
        if (dialog.open() == Window.OK && isEditable()) {
            String newVersion = dialog.getVersion();
            boolean newOptional = dialog.isOptional();
            for (Object element : selected) {
                ImportPackageObject object = (ImportPackageObject) element;
                if (!newVersion.equals(object.getVersion())) {
                    object.setVersion(newVersion);
                }
                if (!newOptional == object.isOptional()) {
                    object.setOptional(newOptional);
                }
            }
        }
    }

    @Override
    protected void handleDoubleClick(IStructuredSelection selection) {
        handleGoToPackage(selection);
    }

    @Override
    protected void makeActions() {
        super.makeActions();
        this.fGoToAction = new Action(PDEUIMessages.ImportPackageSection_goToPackage) {

            @Override
            public void run() {
                handleGoToPackage(BundleImportPackageSection.this.fViewer.getSelection());
            }
        };
    }

    private IPackageFragment getPackageFragment(ISelection sel) {
        if (sel instanceof IStructuredSelection) {
            IStructuredSelection selection = (IStructuredSelection) sel;
            if (selection.size() != 1) {
                return null;
            }

            IBaseModel model = getPage().getModel();
            if (!(model instanceof IPluginModelBase)) {
                return null;
            }

            return PDEJavaHelper.getPackageFragment(((PackageObject) selection.getFirstElement()).getName(),
                ((IPluginModelBase) model).getPluginBase().getId(), getPage().getPDEEditor().getCommonProject());
        }
        return null;
    }

    private void handleGoToPackage(ISelection selection) {
        IPackageFragment frag = getPackageFragment(selection);
        if (frag != null) {
            try {
                IViewPart part = PDEPlugin.getActivePage().showView(JavaUI.ID_PACKAGES);
                ShowInPackageViewAction action = new ShowInPackageViewAction(part.getSite());
                action.run(frag);
            } catch (PartInitException e) {
            }
        }
    }

    @Override
    protected int getAddIndex() {
        return ADD_INDEX;
    }

    @Override
    protected int getRemoveIndex() {
        return REMOVE_INDEX;
    }

    @Override
    protected int getPropertiesIndex() {
        return PROPERTIES_INDEX;
    }

    class BundleImportPackageDialogLabelProvider extends LabelProvider {

        @Override
        public Image getImage(Object element) {
            return JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_PACKAGE);
        }

        @Override
        public String getText(Object element) {
            PackageExport packageExport = (PackageExport) element;
            String label = packageExport.getName();
            if (null != packageExport.getVersion()) {
                label += " " + packageExport.getVersion();
            }
            return label;
        }
    }

    @Override
    protected IContentProvider getContentProvider() {
        return new ImportPackageContentProvider();
    }

    @Override
    protected String getHeaderConstant() {
        return Constants.IMPORT_PACKAGE;
    }

    class ImportItemWrapper {

        Object fUnderlying;

        public ImportItemWrapper(Object underlying) {
            this.fUnderlying = underlying;
        }

        @Override
        public String toString() {
            return getName();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ImportItemWrapper) {
                ImportItemWrapper item = (ImportItemWrapper) obj;
                return getName().equals(item.getName());
            }
            return false;
        }

        public String getName() {
            if (this.fUnderlying instanceof ExportPackageDescription) {
                return ((ExportPackageDescription) this.fUnderlying).getName();
            }
            if (this.fUnderlying instanceof IPackageFragment) {
                return ((IPackageFragment) this.fUnderlying).getElementName();
            }
            if (this.fUnderlying instanceof ExportPackageObject) {
                return ((ExportPackageObject) this.fUnderlying).getName();
            }
            return null;
        }

        public Version getVersion() {
            if (this.fUnderlying instanceof ExportPackageDescription) {
                return ((ExportPackageDescription) this.fUnderlying).getVersion();
            }
            if (this.fUnderlying instanceof ExportPackageObject) {
                String version = ((ExportPackageObject) this.fUnderlying).getVersion();
                if (version != null) {
                    return new Version(version);
                }
            }
            return null;
        }

        boolean hasVersion() {
            return hasEPD() && ((ExportPackageDescription) this.fUnderlying).getVersion() != null;
        }

        boolean hasEPD() {
            return this.fUnderlying instanceof ExportPackageDescription;
        }
    }

    class ImportPackageContentProvider implements IStructuredContentProvider {

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

        public Object[] getElements(Object parent) {
            ImportPackageHeader header = (ImportPackageHeader) getBundle().getManifestHeader(Constants.IMPORT_PACKAGE);
            if (header == null) {
                return new Object[0];
            } else {
                return header.getPackages();
            }
        }
    }

    @Override
    protected void handleAdd() {

        final ImportListSelectionDialog dialog = new ImportListSelectionDialog(PDEPlugin.getActiveWorkbenchShell(),
            new PackageImportDialogLabelProvider());

        Runnable runnable = new Runnable() {

            public void run() {
                setElements(dialog);
                dialog.setMultipleSelection(true);
                dialog.setTitle("Package Selection");
                dialog.setMessage("Select a Package:");
                dialog.create();
                SWTUtil.setDialogSize(dialog, 400, 500);
            }
        };

        BusyIndicator.showWhile(Display.getCurrent(), runnable);
        if (dialog.open() == Window.OK) {

            Object[] selected = dialog.getResult();

            addLocalPackages(selected);
        }

    }

    private void addLocalPackages(Object[] selected) {
        ImportPackageHeader importPackageHeader = (ImportPackageHeader) getBundle().getManifestHeader(IHeaderConstants.IMPORT_PACKAGE);
        for (Object currSelectedElement : selected) {
            PackageExport currPackage = (PackageExport) currSelectedElement;
            if (null == importPackageHeader) {
                getBundle().setHeader(IHeaderConstants.IMPORT_PACKAGE, "");
                importPackageHeader = (ImportPackageHeader) getBundle().getManifestHeader(IHeaderConstants.IMPORT_PACKAGE);
            }

            String versionString = null;
            OsgiVersion osgiVers = currPackage.getVersion();
            if (osgiVers.getMajor() != 0 || osgiVers.getMinor() != 0 || osgiVers.getService() != 0
                || osgiVers.getQualifier() != null && !osgiVers.getQualifier().trim().equals("")) {
                versionString = "[" + currPackage.getVersion().toString() + "," + currPackage.getVersion().toString() + "]";
            }

            ImportPackageObject pack = importPackageHeader.addPackage(currPackage.getName());
            pack.setVersion(versionString);
        }
    }

    private void setElements(ImportListSelectionDialog dialog) {
        IProject project = ((BundleManifestEditor) this.getPage().getEditor()).getCommonProject();
        IArtefact[] bundles = null;
        Collection<Artefact> bundleList = RepositoryUtils.getImportBundleProposals(project, "");
        Collection<PackageExport> packageList = new TreeSet<>(new Comparator<PackageExport>() {

            @Override
            public int compare(PackageExport o1, PackageExport o2) {
                int result = o1.getName().compareTo(o2.getName());
                if (result == 0) {
                    return o1.getVersion().compareTo(o2.getVersion());
                }
                return result;
            }
        });
        for (Artefact bundle : bundleList) {
            packageList.addAll(((LocalBundleArtefact) bundle).getExports());
        }

        dialog.setElements(packageList.toArray(new PackageExport[packageList.size()]));
    }

    class PackageImportDialogLabelProvider extends LabelProvider {

        @Override
        public Image getImage(Object element) {
            return ServerIdeUiPlugin.getPDEImage(PDEPluginImages.DESC_PACKAGE_OBJ);
        }

        @Override
        public String getText(Object element) {
            PackageExport aPackage = (PackageExport) element;
            String label = aPackage.getName();
            if (null != aPackage.getVersion()) {
                label += " " + aPackage.getVersion(); //$NON-NLS-1$
            }
            return label;
        }
    }

}
