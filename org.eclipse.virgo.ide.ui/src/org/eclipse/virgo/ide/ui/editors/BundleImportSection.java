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

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.pde.internal.ui.PDEPlugin;
import org.eclipse.pde.internal.ui.PDEPluginImages;
import org.eclipse.pde.internal.ui.PDEUIMessages;
import org.eclipse.pde.internal.ui.editor.PDEFormPage;
import org.eclipse.pde.internal.ui.util.SWTUtil;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.virgo.ide.bundlerepository.domain.OsgiVersion;
import org.eclipse.virgo.ide.manifest.core.IHeaderConstants;
import org.eclipse.virgo.ide.manifest.core.editor.model.ImportBundleHeader;
import org.eclipse.virgo.ide.manifest.core.editor.model.ImportBundleObject;
import org.eclipse.virgo.ide.runtime.core.artefacts.Artefact;
import org.eclipse.virgo.ide.runtime.core.artefacts.BundleArtefact;
import org.eclipse.virgo.ide.runtime.core.artefacts.IArtefact;
import org.eclipse.virgo.ide.runtime.core.provisioning.RepositoryUtils;
import org.eclipse.virgo.ide.ui.ServerIdeUiPlugin;

/**
 * @author Christian Dupuis
 * @author Leo Dos Santos
 */
public class BundleImportSection extends AbstractImportSection {

    private static final String DESCRIPTION = "Specify the list of bundles required for the operation of this bundle.";

    private static final int ADD_INDEX = 0;

    private static final int REMOVE_INDEX = 1;

    private static final int PROPERTIES_INDEX = 2;

    public BundleImportSection(PDEFormPage page, Composite parent) {
        super(page, parent, Section.DESCRIPTION, new String[] { PDEUIMessages.ImportPackageSection_add, PDEUIMessages.ImportPackageSection_remove,
            PDEUIMessages.ImportPackageSection_properties });

        getSection().setText("Import Bundle");
        getSection().setDescription(DESCRIPTION);
        getTablePart().setEditable(false);
    }

    class ImportBundleContentProvider implements IStructuredContentProvider {

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

        public Object[] getElements(Object parent) {
            ImportBundleHeader header = (ImportBundleHeader) getBundle().getManifestHeader(IHeaderConstants.IMPORT_BUNDLE);
            if (header == null) {
                return new Object[0];
            } else {
                return header.getImportedBundles();
            }
        }
    }

    @Override
    protected IContentProvider getContentProvider() {
        return new ImportBundleContentProvider();
    }

    @Override
    protected ITableLabelProvider getLabelProvider() {
        return new BundleImportLabelProvider();
    }

    private void setElements(ImportListSelectionDialog dialog) {
        IProject project = ((BundleManifestEditor) this.getPage().getEditor()).getCommonProject();
        IArtefact[] bundles = null;
        Collection<Artefact> bundleList = RepositoryUtils.getImportBundleProposals(project, "");
        bundles = bundleList.toArray(new IArtefact[] {});
        dialog.setElements(bundles);
    }

    @Override
    protected void handleAdd() {
        final ImportListSelectionDialog dialog = new ImportListSelectionDialog(PDEPlugin.getActiveWorkbenchShell(),
            new BundleImportDialogLabelProvider());

        Runnable runnable = new Runnable() {

            public void run() {
                setElements(dialog);
                dialog.setMultipleSelection(true);
                dialog.setTitle("Bundle Selection");
                dialog.setMessage("Select a Bundle:");
                dialog.create();
                SWTUtil.setDialogSize(dialog, 400, 500);
            }
        };

        BusyIndicator.showWhile(Display.getCurrent(), runnable);
        if (dialog.open() == Window.OK) {

            Object[] selected = dialog.getResult();

            addLocalBundles(selected);
        }

    }

    private void addLocalBundles(Object[] selected) {
        ImportBundleHeader importBundleHeader = (ImportBundleHeader) getBundle().getManifestHeader(IHeaderConstants.IMPORT_BUNDLE);
        for (Object currSelectedElement : selected) {
            BundleArtefact currBundle = (BundleArtefact) currSelectedElement;
            if (null == importBundleHeader) {
                getBundle().setHeader(IHeaderConstants.IMPORT_BUNDLE, "");
                importBundleHeader = (ImportBundleHeader) getBundle().getManifestHeader(IHeaderConstants.IMPORT_BUNDLE);
            }

            String versionString = null;
            OsgiVersion osgiVers = currBundle.getVersion();
            if (osgiVers.getMajor() != 0 || osgiVers.getMinor() != 0 || osgiVers.getService() != 0
                || osgiVers.getQualifier() != null && !osgiVers.getQualifier().trim().equals("")) {
                versionString = "[" + currBundle.getVersion().toString() + "," + currBundle.getVersion().toString() + "]";
            }
            importBundleHeader.addBundle(currBundle.getSymbolicName(), versionString);
        }
    }

    @Override
    protected void handleRemove() {
        Object[] removed = ((IStructuredSelection) this.fViewer.getSelection()).toArray();
        for (Object element : removed) {
            ImportBundleHeader header = (ImportBundleHeader) getBundle().getManifestHeader(IHeaderConstants.IMPORT_BUNDLE);
            header.removeBundle((ImportBundleObject) element);
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

    class BundleImportDialogLabelProvider extends LabelProvider {

        @Override
        public Image getImage(Object element) {
            return ServerIdeUiPlugin.getPDEImage(PDEPluginImages.DESC_BUNDLE_OBJ);
        }

        @Override
        public String getText(Object element) {
            BundleArtefact bundleArtifact = (BundleArtefact) element;
            String label = bundleArtifact.getSymbolicName();
            if (null != bundleArtifact.getVersion()) {
                label += " " + bundleArtifact.getVersion();
            }
            return label;
        }
    }

    class BundleImportLabelProvider extends AbstractSectionViewerLabelProvider {

        @Override
        public Image getColumnImage(Object element, int columnIndex) {
            return ServerIdeUiPlugin.getPDEImage(PDEPluginImages.DESC_BUNDLE_OBJ);
        }

        @Override
        public String getColumnText(Object element, int columnIndex) {
            ImportBundleObject importBundleObject = (ImportBundleObject) element;
            String label = importBundleObject.getValue();
            if (null != importBundleObject.getVersion()) {
                label += " " + importBundleObject.getVersion();
            }
            return label;
        }
    }

    @Override
    protected String getHeaderConstant() {
        return IHeaderConstants.IMPORT_BUNDLE;
    }

    @Override
    protected boolean shouldEnableProperties(Object[] selected) {
        if (selected.length == 0) {
            return false;
        }
        if (selected.length == 1) {
            return true;
        }

        String version = ((ImportBundleObject) selected[0]).getVersion();
        boolean optional = ((ImportBundleObject) selected[0]).isOptional();
        for (int i = 1; i < selected.length; i++) {
            ImportBundleObject object = (ImportBundleObject) selected[i];
            if (version == null) {
                if (object.getVersion() != null || !(optional == object.isOptional())) {
                    return false;
                }
            } else if (!version.equals(object.getVersion()) || !(optional == object.isOptional())) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void handleOpenProperties() {
        Object[] selected = ((IStructuredSelection) this.fViewer.getSelection()).toArray();
        ImportBundleObject first = (ImportBundleObject) selected[0];
        BundleDependencyPropertiesDialog dialog = new BundleDependencyPropertiesDialog(isEditable(), false, false, first.isOptional(),
            first.getVersion(), true, true);
        dialog.create();
        SWTUtil.setDialogSize(dialog, 400, -1);
        if (selected.length == 1) {
            dialog.setTitle(((ImportBundleObject) selected[0]).getValue());
        } else {
            dialog.setTitle("Properties");
        }
        if (dialog.open() == Window.OK && isEditable()) {
            String newVersion = dialog.getVersion();
            boolean newOptional = dialog.isOptional();
            for (Object element : selected) {
                ImportBundleObject object = (ImportBundleObject) element;
                if (!newVersion.equals(object.getVersion())) {
                    object.setVersion(newVersion);
                }
                if (!newOptional == object.isOptional()) {
                    object.setOptional(newOptional);
                }
            }
        }
    }

}
