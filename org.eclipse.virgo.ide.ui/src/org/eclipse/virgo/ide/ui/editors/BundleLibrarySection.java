/*******************************************************************************
 * Copyright (c) 2009, 2012 SpringSource, a divison of VMware, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     SpringSource, a division of VMware, Inc. - initial API and implementation
 *******************************************************************************/

package org.eclipse.virgo.ide.ui.editors;

import java.util.ArrayList;
import java.util.HashSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.pde.core.IBaseModel;
import org.eclipse.pde.core.IModel;
import org.eclipse.pde.core.IModelChangedEvent;
import org.eclipse.pde.core.IModelChangedListener;
import org.eclipse.pde.core.build.IBuild;
import org.eclipse.pde.core.build.IBuildEntry;
import org.eclipse.pde.core.build.IBuildModel;
import org.eclipse.pde.core.plugin.IPluginBase;
import org.eclipse.pde.core.plugin.IPluginElement;
import org.eclipse.pde.core.plugin.IPluginLibrary;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.internal.build.IBuildPropertiesConstants;
import org.eclipse.pde.internal.core.ClasspathUtilCore;
import org.eclipse.pde.internal.core.plugin.PluginLibrary;
import org.eclipse.pde.internal.ui.PDEPlugin;
import org.eclipse.pde.internal.ui.PDEUIMessages;
import org.eclipse.pde.internal.ui.editor.FormLayoutFactory;
import org.eclipse.pde.internal.ui.editor.PDEFormPage;
import org.eclipse.pde.internal.ui.editor.TableSection;
import org.eclipse.pde.internal.ui.editor.build.BuildInputContext;
import org.eclipse.pde.internal.ui.editor.build.BuildSourcePage;
import org.eclipse.pde.internal.ui.editor.build.JARFileFilter;
import org.eclipse.pde.internal.ui.editor.context.InputContextManager;
import org.eclipse.pde.internal.ui.editor.plugin.BundleInputContext;
import org.eclipse.pde.internal.ui.editor.plugin.JarSelectionValidator;
import org.eclipse.pde.internal.ui.editor.plugin.NewRuntimeLibraryDialog;
import org.eclipse.pde.internal.ui.parts.EditableTablePart;
import org.eclipse.pde.internal.ui.parts.TablePart;
import org.eclipse.pde.internal.ui.util.SWTUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceComparator;

/**
 * Adapted from PDE's <code>LibrarySection</code>.
 *
 * @author Christian Dupuis
 */
@SuppressWarnings("unchecked")
public class BundleLibrarySection extends TableSection implements IModelChangedListener, IBuildPropertiesConstants {

    private static final int NEW_INDEX = 0;

    private static final int ADD_INDEX = 1;

    private static final int REMOVE_INDEX = 2;

    private static final int UP_INDEX = 3;

    private static final int DOWN_INDEX = 4;

    private Action fRenameAction;

    private Action fRemoveAction;

    private Action fNewAction;

    private TableViewer fLibraryTable;

    class LibraryFilter extends JARFileFilter {

        public LibraryFilter(HashSet set) {
            super(set);
        }

        @Override
        public boolean select(Viewer viewer, Object parent, Object element) {
            if (element instanceof IFolder) {
                return isPathValid(((IFolder) element).getProjectRelativePath());
            }
            if (element instanceof IFile) {
                return isFileValid(((IFile) element).getProjectRelativePath());
            }
            return false;
        }
    }

    class LibrarySelectionValidator extends JarSelectionValidator {

        public LibrarySelectionValidator(Class[] acceptedTypes, boolean allowMultipleSelection) {
            super(acceptedTypes, allowMultipleSelection);
        }

        @Override
        public boolean isValid(Object element) {
            return element instanceof IFolder ? true : super.isValid(element);
        }
    }

    class TableContentProvider implements IStructuredContentProvider {

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

        public Object[] getElements(Object parent) {
            return getModel().getPluginBase().getLibraries();
        }
    }

    public BundleLibrarySection(PDEFormPage page, Composite parent) {
        super(page, parent, Section.DESCRIPTION,
            new String[] { PDEUIMessages.NewManifestEditor_LibrarySection_new, PDEUIMessages.NewManifestEditor_LibrarySection_add,
                PDEUIMessages.NewManifestEditor_LibrarySection_remove, PDEUIMessages.ManifestEditor_LibrarySection_up,
                PDEUIMessages.ManifestEditor_LibrarySection_down });
    }

    private String getSectionDescription() {
        return "Specify the libraries and folders that constitute the bundle classpath. "
            + "If unspecified, the classes and resources are assumed to be at the root of the bundle.";
    }

    protected boolean isBundle() {
        return getBundleContext() != null;
    }

    private BundleInputContext getBundleContext() {
        InputContextManager manager = getPage().getPDEEditor().getContextManager();
        return (BundleInputContext) manager.findContext(BundleInputContext.CONTEXT_ID);
    }

    @Override
    public void createClient(Section section, FormToolkit toolkit) {
        section.setText(PDEUIMessages.ManifestEditor_LibrarySection_title);
        section.setDescription(getSectionDescription());

        Composite container = createClientContainer(section, 2, toolkit);
        EditableTablePart tablePart = getTablePart();
        tablePart.setEditable(isEditable());

        createViewerPartControl(container, SWT.MULTI, 2, toolkit);
        this.fLibraryTable = tablePart.getTableViewer();
        this.fLibraryTable.setContentProvider(new TableContentProvider());
        this.fLibraryTable.setLabelProvider(PDEPlugin.getDefault().getLabelProvider());
        toolkit.paintBordersFor(container);

        makeActions();
        updateButtons();
        section.setLayout(FormLayoutFactory.createClearGridLayout(false, 1));
        section.setLayoutData(new GridData(GridData.FILL_BOTH));
        section.setClient(container);

        IPluginModelBase model = getModel();
        this.fLibraryTable.setInput(model.getPluginBase());
        model.addModelChangedListener(this);
    }

    private void updateButtons() {
        Table table = this.fLibraryTable.getTable();
        boolean hasSelection = table.getSelection().length > 0;
        boolean singleSelection = table.getSelection().length == 1;
        int count = table.getItemCount();
        int index = table.getSelectionIndex();
        boolean canMoveUp = singleSelection && index > 0;
        boolean canMoveDown = singleSelection && index < count - 1;

        TablePart tablePart = getTablePart();
        tablePart.setButtonEnabled(ADD_INDEX, isEditable());
        tablePart.setButtonEnabled(NEW_INDEX, isEditable());
        tablePart.setButtonEnabled(REMOVE_INDEX, isEditable() && hasSelection);
        tablePart.setButtonEnabled(UP_INDEX, isEditable() && canMoveUp);
        tablePart.setButtonEnabled(DOWN_INDEX, isEditable() && canMoveDown);
    }

    private void makeActions() {
        this.fNewAction = new Action(PDEUIMessages.ManifestEditor_LibrarySection_newLibrary) {

            @Override
            public void run() {
                handleNew();
            }
        };
        this.fNewAction.setEnabled(isEditable());

        this.fRenameAction = new Action(PDEUIMessages.EditableTablePart_renameAction) {

            @Override
            public void run() {
                getRenameAction().run();
            }
        };
        this.fRenameAction.setEnabled(isEditable());

        this.fRemoveAction = new Action(PDEUIMessages.NewManifestEditor_LibrarySection_remove) {

            @Override
            public void run() {
                handleRemove();
            }
        };
        this.fRemoveAction.setEnabled(isEditable());
    }

    @Override
    protected void selectionChanged(IStructuredSelection selection) {
        getPage().getPDEEditor().setSelection(selection);
        if (getPage().getModel().isEditable()) {
            updateButtons();
        }
    }

    @Override
    protected void buttonSelected(int index) {
        switch (index) {
            case NEW_INDEX:
                handleNew();
                break;
            case ADD_INDEX:
                handleAdd();
                break;
            case REMOVE_INDEX:
                handleRemove();
                break;
            case UP_INDEX:
                handleUp();
                break;
            case DOWN_INDEX:
                handleDown();
                break;
        }
    }

    @Override
    public void dispose() {
        IPluginModelBase model = getModel();
        if (model != null) {
            model.removeModelChangedListener(this);
        }
        super.dispose();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.pde.internal.ui.editor.PDESection#doGlobalAction(java.lang .String)
     */
    @Override
    public boolean doGlobalAction(String actionId) {

        if (!isEditable()) {
            return false;
        }

        if (actionId.equals(ActionFactory.DELETE.getId())) {
            handleRemove();
            return true;
        }
        if (actionId.equals(ActionFactory.CUT.getId())) {
            // delete here and let the editor transfer
            // the selection to the clipboard
            handleRemove();
            return false;
        }
        if (actionId.equals(ActionFactory.PASTE.getId())) {
            doPaste();
            return true;
        }
        return false;
    }

    @Override
    public boolean setFormInput(Object object) {
        if (object instanceof IPluginLibrary) {
            this.fLibraryTable.setSelection(new StructuredSelection(object), true);
            return true;
        }
        return false;
    }

    @Override
    protected void fillContextMenu(IMenuManager manager) {
        manager.add(this.fNewAction);
        if (!this.fLibraryTable.getSelection().isEmpty()) {
            manager.add(new Separator());
            manager.add(this.fRenameAction);
            manager.add(this.fRemoveAction);
        }
        // Copy, cut, and paste operations not supported for plug-ins that do
        // not have a MANIFEST.MF (not a Bundle)
        getPage().getPDEEditor().getContributor().contextMenuAboutToShow(manager, isBundle());
    }

    private void handleRemove() {
        Object[] selection = ((IStructuredSelection) this.fLibraryTable.getSelection()).toArray();
        int index = this.fLibraryTable.getTable().getSelectionIndex();
        int[] indices = this.fLibraryTable.getTable().getSelectionIndices();
        for (int element : indices) {
            if (element < index) {
                index = element;
            }
        }

        String[] remove = new String[selection.length];
        for (int i = 0; i < selection.length; i++) {
            if (selection[i] != null && selection[i] instanceof IPluginLibrary) {
                IPluginLibrary ep = (IPluginLibrary) selection[i];
                IPluginBase plugin = ep.getPluginBase();
                try {
                    plugin.remove(ep);
                } catch (CoreException e) {
                    PDEPlugin.logException(e);
                }
                remove[i] = ep.getName();
            }
        }
        updateBuildProperties(remove, new String[remove.length], true);
        updateJavaClasspathLibs(remove, new String[remove.length]);

        int itemCount = this.fLibraryTable.getTable().getItemCount();
        if (itemCount > 0) {
            if (index >= itemCount) {
                index = itemCount - 1;
            }
            this.fLibraryTable.getTable().setSelection(index);
            this.fLibraryTable.getTable().setFocus();
        }
        updateButtons();
    }

    private void handleDown() {
        Table table = getTablePart().getTableViewer().getTable();
        int index = table.getSelectionIndex();
        if (index != table.getItemCount() - 1) {
            swap(index, index + 1);
        }
    }

    private void handleUp() {
        int index = getTablePart().getTableViewer().getTable().getSelectionIndex();
        if (index >= 1) {
            swap(index, index - 1);
        }
    }

    public void swap(int index1, int index2) {
        Table table = getTablePart().getTableViewer().getTable();
        IPluginLibrary l1 = (IPluginLibrary) table.getItem(index1).getData();
        IPluginLibrary l2 = (IPluginLibrary) table.getItem(index2).getData();

        try {
            IPluginModelBase model = getModel();
            IPluginBase pluginBase = model.getPluginBase();
            pluginBase.swap(l1, l2);
            refresh();
            table.setSelection(index2);
            table.setFocus();
            updateButtons();
        } catch (CoreException e) {
            PDEPlugin.logException(e);
        }
    }

    private void handleNew() {
        IPluginModelBase model = getModel();
        NewRuntimeLibraryDialog dialog = new NewRuntimeLibraryDialog(getPage().getSite().getShell(), model.getPluginBase().getLibraries());
        dialog.create();
        dialog.getShell().setText(PDEUIMessages.ManifestEditor_LibrarySection_newLibraryEntry);
        SWTUtil.setDialogSize(dialog, 250, 175);

        if (dialog.open() == Window.OK) {
            String libName = dialog.getLibraryName();
            if (libName == null || libName.length() == 0) {
                return;
            }
            try {
                IPluginLibrary library = model.getPluginFactory().createLibrary();
                library.setName(libName);
                library.setExported(true);
                model.getPluginBase().add(library);
                checkSourceRootEntry();
                updateBuildProperties(new String[] { null }, new String[] { library.getName() }, true);
                this.fLibraryTable.setSelection(new StructuredSelection(library));
                this.fLibraryTable.getTable().setFocus();
            } catch (CoreException e) {
                PDEPlugin.logException(e);
            }
        }
    }

    private void checkSourceRootEntry() {
        IPluginModelBase pluginModel = getModel();
        IPluginLibrary[] libraries = pluginModel.getPluginBase().getLibraries();
        for (IPluginLibrary element : libraries) {
            if (element.getName().equals(".")) {
                return;
            }
        }

        IPluginLibrary library = pluginModel.getPluginFactory().createLibrary();
        try {
            library.setName("."); //$NON-NLS-1$
            pluginModel.getPluginBase().add(library);
        } catch (CoreException e) {
        }
    }

    private IBuildModel getBuildModel() {
        IFormPage page = getPage().getEditor().findPage(BuildInputContext.CONTEXT_ID);
        IBaseModel model = null;
        if (page instanceof BuildSourcePage) {
            model = ((BuildSourcePage) page).getInputContext().getModel();
        }

        if (model != null && model instanceof IBuildModel) {
            return (IBuildModel) model;
        }
        return null;
    }

    private void configureSourceBuildEntry(IBuildModel bmodel, String oldPath, String newPath) throws CoreException {
        IBuild build = bmodel.getBuild();
        IBuildEntry entry = build.getEntry(PROPERTY_SOURCE_PREFIX + (oldPath != null ? oldPath : newPath));
        try {
            if (newPath != null) {
                if (entry == null) {
                    IProject project = ((IModel) getPage().getModel()).getUnderlyingResource().getProject();
                    IJavaProject jproject = JavaCore.create(project);
                    ArrayList tokens = new ArrayList();
                    IClasspathEntry[] entries = jproject.getRawClasspath();
                    for (IClasspathEntry element : entries) {
                        if (element.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
                            tokens.add(element.getPath().removeFirstSegments(1).addTrailingSeparator().toString());
                        }
                    }
                    if (tokens.size() == 0) {
                        return;
                    }

                    entry = bmodel.getFactory().createEntry(PROPERTY_SOURCE_PREFIX + newPath);
                    for (int i = 0; i < tokens.size(); i++) {
                        entry.addToken((String) tokens.get(i));
                    }
                    build.add(entry);
                } else {
                    entry.setName(PROPERTY_SOURCE_PREFIX + newPath);
                }
            } else if (entry != null && newPath == null) {
                build.remove(entry);
            }
        } catch (JavaModelException e) {
        }
    }

    private void handleAdd() {
        ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getPage().getSite().getShell(), new WorkbenchLabelProvider(),
            new WorkbenchContentProvider());

        Class[] acceptedClasses = new Class[] { IFile.class };
        dialog.setValidator(new LibrarySelectionValidator(acceptedClasses, true));
        dialog.setTitle(PDEUIMessages.BuildEditor_ClasspathSection_jarsTitle);
        dialog.setMessage(PDEUIMessages.ClasspathSection_jarsMessage);
        IPluginLibrary[] libraries = getModel().getPluginBase().getLibraries();
        HashSet set = new HashSet();
        for (IPluginLibrary element : libraries) {
            set.add(new Path(ClasspathUtilCore.expandLibraryName(element.getName())));
        }

        dialog.addFilter(new LibraryFilter(set));
        IProject project = ((IModel) getPage().getModel()).getUnderlyingResource().getProject();
        dialog.setInput(project);
        dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));

        if (dialog.open() == Window.OK) {
            Object[] elements = dialog.getResult();
            String[] filePaths = new String[elements.length];
            IPluginModelBase model = getModel();
            ArrayList list = new ArrayList();
            for (int i = 0; i < elements.length; i++) {
                IResource elem = (IResource) elements[i];
                IPath path = elem.getProjectRelativePath();
                if (elem instanceof IFolder) {
                    path = path.addTrailingSeparator();
                }
                filePaths[i] = path.toString();
                IPluginLibrary library = model.getPluginFactory().createLibrary();
                try {
                    library.setName(filePaths[i]);
                    library.setExported(true);
                    model.getPluginBase().add(library);
                    list.add(library);
                } catch (CoreException e) {
                    PDEPlugin.logException(e);
                }
            }
            checkSourceRootEntry();
            this.fLibraryTable.setSelection(new StructuredSelection(list.toArray()));
            this.fLibraryTable.getTable().setFocus();
        }
    }

    private void updateBuildProperties(final String[] oldPaths, final String[] newPaths, boolean modifySourceEntry) {
        IBuildModel bmodel = getBuildModel();
        if (bmodel == null) {
            return;
        }

        IBuild build = bmodel.getBuild();

        IBuildEntry entry = build.getEntry(PROPERTY_BIN_INCLUDES);
        if (entry == null) {
            entry = bmodel.getFactory().createEntry(PROPERTY_BIN_INCLUDES);
        }

        try {
            // adding new entries
            if (oldPaths[0] == null) {
                for (String element : newPaths) {
                    if (element != null) {
                        entry.addToken(element);
                        if (modifySourceEntry) {
                            configureSourceBuildEntry(bmodel, null, element);
                        }
                    }
                }
                // removing entries
            } else if (newPaths[0] == null) {
                for (String element : oldPaths) {
                    if (element != null) {
                        entry.removeToken(element);
                        if (modifySourceEntry) {
                            configureSourceBuildEntry(bmodel, element, null);
                        }
                    }
                }
                if (entry.getTokens().length == 0) {
                    build.remove(entry);
                }
                // rename entries
            } else {
                for (int i = 0; i < oldPaths.length; i++) {
                    if (newPaths[i] != null && oldPaths[i] != null) {
                        entry.renameToken(oldPaths[i], newPaths[i]);
                        if (modifySourceEntry) {
                            configureSourceBuildEntry(bmodel, oldPaths[i], newPaths[i]);
                        }
                    }
                }
            }
        } catch (CoreException e) {
        }
    }

    private void updateJavaClasspathLibs(String[] oldPaths, String[] newPaths) {
        IProject project = ((IModel) getPage().getModel()).getUnderlyingResource().getProject();
        IJavaProject jproject = JavaCore.create(project);
        try {
            IClasspathEntry[] entries = jproject.getRawClasspath();
            ArrayList toBeAdded = new ArrayList();
            int index = -1;
            entryLoop: for (int i = 0; i < entries.length; i++) {
                if (entries[i].getEntryKind() == IClasspathEntry.CPE_LIBRARY) {
                    if (index == -1) {
                        index = i;
                    }
                    // do not add the old paths (handling deletion/renaming)
                    IPath path = entries[i].getPath().removeFirstSegments(1).removeTrailingSeparator();
                    for (int j = 0; j < oldPaths.length; j++) {
                        if (oldPaths[j] != null && path.equals(new Path(oldPaths[j]).removeTrailingSeparator())) {
                            continue entryLoop;
                        }
                    }
                } else if (entries[i].getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
                    if (index == -1) {
                        index = i;
                    }
                }
                toBeAdded.add(entries[i]);
            }
            if (index == -1) {
                index = entries.length;
            }

            // add paths
            for (String element : newPaths) {
                if (element == null) {
                    continue;
                }
                IClasspathEntry entry = JavaCore.newLibraryEntry(project.getFullPath().append(element), null, null, true);
                if (!toBeAdded.contains(entry)) {
                    toBeAdded.add(index++, entry);
                }
            }

            if (toBeAdded.size() == entries.length) {
                return;
            }

            IClasspathEntry[] updated = (IClasspathEntry[]) toBeAdded.toArray(new IClasspathEntry[toBeAdded.size()]);
            jproject.setRawClasspath(updated, null);
        } catch (JavaModelException e) {
        }
    }

    @Override
    public void refresh() {
        if (this.fLibraryTable.getControl().isDisposed()) {
            return;
        }
        this.fLibraryTable.setSelection(null);
        this.fLibraryTable.refresh();
        super.refresh();
    }

    @Override
    public void modelChanged(IModelChangedEvent event) {
        if (event.getChangeType() == IModelChangedEvent.WORLD_CHANGED) {
            markStale();
            return;
        }
        Object changeObject = event.getChangedObjects()[0];
        if (changeObject instanceof IPluginLibrary) {
            if (event.getChangeType() == IModelChangedEvent.INSERT) {
                this.fLibraryTable.add(changeObject);
            } else if (event.getChangeType() == IModelChangedEvent.REMOVE) {
                this.fLibraryTable.remove(changeObject);
            } else {
                this.fLibraryTable.update(changeObject, null);
            }
        } else if (changeObject.equals(this.fLibraryTable.getInput())) {
            markStale();
        } else if (changeObject instanceof IPluginElement && ((IPluginElement) changeObject).getParent() instanceof IPluginLibrary) {
            this.fLibraryTable.update(((IPluginElement) changeObject).getParent(), null);
        }
    }

    @Override
    public void setFocus() {
        this.fLibraryTable.getTable().setFocus();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.pde.internal.ui.editor.StructuredViewerSection#doPaste(java .lang.Object, java.lang.Object[])
     */
    @Override
    protected void doPaste(Object targetObject, Object[] sourceObjects) {
        // Get the model
        IPluginModelBase model = getModel();
        IPluginBase plugin = model.getPluginBase();
        try {
            // Paste all source objects
            for (Object sourceObject : sourceObjects) {
                if (sourceObject instanceof PluginLibrary) {
                    // Plugin library object
                    PluginLibrary library = (PluginLibrary) sourceObject;
                    // Adjust all the source object transient field values to
                    // acceptable values
                    library.reconnect(model, plugin);
                    // Add the library to the plug-in
                    plugin.add(library);
                }
            }
        } catch (CoreException e) {
            PDEPlugin.logException(e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.pde.internal.ui.editor.StructuredViewerSection#canPaste(java .lang.Object, java.lang.Object[])
     */
    @Override
    protected boolean canPaste(Object targetObject, Object[] sourceObjects) {
        HashSet librarySet = null;
        // Only source objects that are plugin libraries that have not already
        // been specified can be pasted
        for (Object element : sourceObjects) {
            // Only plugin libraries are allowed
            if (element instanceof IPluginLibrary == false) {
                return false;
            }
            // We have a plugin library
            // Get the current libraries already specified and store them in
            // a set to assist with searching
            if (librarySet == null) {
                librarySet = createPluginLibrarySet();
            }
            // No duplicate libraries are allowed
            IPluginLibrary library = (IPluginLibrary) element;
            if (librarySet.contains(new Path(ClasspathUtilCore.expandLibraryName(library.getName())))) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return
     */
    private HashSet createPluginLibrarySet() {
        // Get the current libraries and add them to a set for easy searching
        IPluginLibrary[] libraries = getModel().getPluginBase().getLibraries();
        HashSet librarySet = new HashSet();
        for (IPluginLibrary element : libraries) {
            librarySet.add(new Path(ClasspathUtilCore.expandLibraryName(element.getName())));
        }
        return librarySet;
    }

    @Override
    protected void entryModified(Object entry, String value) {
        try {
            IPluginModelBase model = getModel();
            IProject project = model.getUnderlyingResource().getProject();
            IPluginLibrary library = (IPluginLibrary) entry;
            model.getPluginBase().remove(library);
            String[] oldValue = { library.getName() };
            String[] newValue = { value };
            library.setName(value);
            boolean memberExists = project.findMember(value) != null;
            updateBuildProperties(oldValue, newValue, !memberExists);
            updateJavaClasspathLibs(oldValue, memberExists ? newValue : new String[] { null });
            model.getPluginBase().add(library);
        } catch (CoreException e) {
            PDEPlugin.logException(e);
        }
    }

    /**
     * @return
     */
    private IPluginModelBase getModel() {
        return (IPluginModelBase) getPage().getModel();
    }
}