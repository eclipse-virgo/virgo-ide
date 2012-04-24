/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.virgo.ide.eclipse.wizards;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.internal.corext.util.Messages;
import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.preferences.CompliancePreferencePage;
import org.eclipse.jdt.internal.ui.preferences.NewJavaProjectPreferencePage;
import org.eclipse.jdt.internal.ui.preferences.PropertyAndPreferencePage;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.BuildPathSupport;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.ComboDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.SelectionButtonDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringButtonDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringDialogField;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstall2;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.VMStandin;
import org.eclipse.jdt.launching.environments.IExecutionEnvironment;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageOne;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.util.Policy;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;

/**
 * SpringSource Tool Suite Team - This class was copied from Eclipse 3.4 for use in 3.3-based distributions. Necessary
 * changes included porting all the NewJavaProjectWizard messages from the NewWizardMessages utility class to
 * NewJavaProjectWizardConstants. All working set functionality has been commented out in order to retain compatibility
 * between 3.3 and 3.4
 * 
 * @deprecated As of release 2.0.0, STS only supports Eclipse 3.4 and above. Use {@link NewJavaProjectWizardPageOne}
 *             instead. ----------------------------------------------------------------------------- The first page of
 *             the New Java Project wizard. This page is typically used in combination with
 *             {@link NewJavaProjectWizardPageTwoCOPY}. Clients can extend this page to modify the UI: Add, remove or
 *             reorder sections.
 *             <p>
 *             Clients may instantiate or subclass.
 *             </p>
 *             <p>
 *             <strong>EXPERIMENTAL</strong> This class or interface has been added as part of a work in progress. This
 *             API is under review and may still change when finalized. Please send your comments to bug 160985.
 *             </p>
 * @since 3.4
 */
@SuppressWarnings("restriction")
@Deprecated
public class NewJavaProjectWizardPageOneCOPY extends WizardPage {

	/**
	 * Show a warning when the project location contains files.
	 */
	private final class DetectGroup extends Observable implements Observer, SelectionListener {

		private Link fHintText;

		private Label fIcon;

		private boolean fDetect;

		public DetectGroup() {
			fDetect = false;
		}

		private boolean computeDetectState() {
			if (fLocationGroup.isWorkspaceRadioSelected()) {
				String name = fNameGroup.getName();
				if (name.length() == 0 || JavaPlugin.getWorkspace().getRoot().findMember(name) != null) {
					return false;
				} else {
					final File directory = fLocationGroup.getLocation().append(name).toFile();
					return directory.isDirectory();
				}
			} else {
				final File directory = fLocationGroup.getLocation().toFile();
				return directory.isDirectory();
			}
		}

		public Control createControl(Composite parent) {

			Composite composite = new Composite(parent, SWT.NONE);
			composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
			GridLayout layout = new GridLayout(2, false);
			layout.horizontalSpacing = 10;
			composite.setLayout(layout);

			fIcon = new Label(composite, SWT.LEFT);
			fIcon.setImage(Dialog.getImage(Dialog.DLG_IMG_MESSAGE_WARNING));
			GridData gridData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
			fIcon.setLayoutData(gridData);

			fHintText = new Link(composite, SWT.WRAP);
			fHintText.setFont(composite.getFont());
			fHintText.addSelectionListener(this);
			gridData = new GridData(GridData.FILL, SWT.FILL, true, true);
			gridData.widthHint = convertWidthInCharsToPixels(50);
			gridData.heightHint = convertHeightInCharsToPixels(3);
			fHintText.setLayoutData(gridData);

			handlePossibleJVMChange();
			return composite;
		}

		public void handlePossibleJVMChange() {

			if (JavaRuntime.getDefaultVMInstall() == null) {
				fHintText.setText(NewJavaProjectWizardConstants.NewJavaProjectWizardPageOne_NoJREFound_link);
				fHintText.setVisible(true);
				fIcon.setImage(Dialog.getImage(Dialog.DLG_IMG_MESSAGE_WARNING));
				fIcon.setVisible(true);
				return;
			}

			String selectedCompliance = fJREGroup.getSelectedCompilerCompliance();
			if (selectedCompliance != null) {
				String defaultCompliance = JavaCore.getOption(JavaCore.COMPILER_COMPLIANCE);
				if (selectedCompliance.equals(defaultCompliance)) {
					fHintText.setVisible(false);
					fIcon.setVisible(false);
				} else {
					fHintText.setText(Messages.format(
							NewJavaProjectWizardConstants.NewJavaProjectWizardPageOne_DetectGroup_differendWorkspaceCC_message,
							new String[] { defaultCompliance, selectedCompliance }));
					fHintText.setVisible(true);
					fIcon.setImage(Dialog.getImage(Dialog.DLG_IMG_MESSAGE_INFO));
					fIcon.setVisible(true);
				}
				return;
			}

			selectedCompliance = JavaCore.getOption(JavaCore.COMPILER_COMPLIANCE);
			IVMInstall selectedJVM = fJREGroup.getSelectedJVM();
			if (selectedJVM == null) {
				selectedJVM = JavaRuntime.getDefaultVMInstall();
			}
			String jvmCompliance = JavaCore.VERSION_1_4;
			if (selectedJVM instanceof IVMInstall2) {
				jvmCompliance = JavaModelUtil.getCompilerCompliance((IVMInstall2) selectedJVM, JavaCore.VERSION_1_4);
			}
			if (!selectedCompliance.equals(jvmCompliance)
					&& (JavaModelUtil.is50OrHigher(selectedCompliance) || JavaModelUtil.is50OrHigher(jvmCompliance))) {
				if (selectedCompliance.equals(JavaCore.VERSION_1_5)) {
					selectedCompliance = "5.0"; //$NON-NLS-1$
				} else if (selectedCompliance.equals(JavaCore.VERSION_1_6)) {
					selectedCompliance = "6.0"; //$NON-NLS-1$
				}

				fHintText.setText(Messages.format(
						NewJavaProjectWizardConstants.NewJavaProjectWizardPageOne_DetectGroup_jre_message,
						new String[] { selectedCompliance, jvmCompliance }));
				fHintText.setVisible(true);
				fIcon.setImage(Dialog.getImage(Dialog.DLG_IMG_MESSAGE_WARNING));
				fIcon.setVisible(true);
			} else {
				fHintText.setVisible(false);
				fIcon.setVisible(false);
			}
		}

		public boolean mustDetect() {
			return fDetect;
		}

		public void update(Observable o, Object arg) {
			if (o instanceof LocationGroup) {
				boolean oldDetectState = fDetect;
				fDetect = computeDetectState();

				if (oldDetectState != fDetect) {
					setChanged();
					notifyObservers();

					if (fDetect) {
						fHintText.setVisible(true);
						fHintText.setText(NewJavaProjectWizardConstants.NewJavaProjectWizardPageOne_DetectGroup_message);
						fIcon.setImage(Dialog.getImage(Dialog.DLG_IMG_MESSAGE_INFO));
						fIcon.setVisible(true);
					} else {
						handlePossibleJVMChange();
					}
				}
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org
		 * .eclipse.swt.events.SelectionEvent)
		 */
		@SuppressWarnings("unchecked")
		public void widgetDefaultSelected(SelectionEvent e) {
			String jreID = BuildPathSupport.JRE_PREF_PAGE_ID;
			String complianceId = CompliancePreferencePage.PREF_ID;
			Map data = new HashMap();
			data.put(PropertyAndPreferencePage.DATA_NO_LINK, Boolean.TRUE);
			String id = "JRE".equals(e.text) ? jreID : complianceId; //$NON-NLS-1$
			PreferencesUtil.createPreferenceDialogOn(getShell(), id, new String[] { jreID, complianceId }, data).open();

			fJREGroup.handlePossibleJVMChange();
			handlePossibleJVMChange();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse
		 * .swt.events.SelectionEvent)
		 */
		public void widgetSelected(SelectionEvent e) {
			widgetDefaultSelected(e);
		}
	}

	private final class JREGroup implements Observer, SelectionListener, IDialogFieldListener {

		private static final String LAST_SELECTED_EE_SETTINGS_KEY = JavaUI.ID_PLUGIN
				+ ".last.selected.execution.enviroment"; //$NON-NLS-1$

		private static final String LAST_SELECTED_JRE_SETTINGS_KEY = JavaUI.ID_PLUGIN + ".last.selected.project.jre"; //$NON-NLS-1$

		private static final String LAST_SELECTED_JRE_KIND = JavaUI.ID_PLUGIN + ".last.selected.jre.kind"; //$NON-NLS-1$

		private static final int DEFAULT_JRE = 0;

		private static final int PROJECT_JRE = 1;

		private static final int EE_JRE = 2;

		private final SelectionButtonDialogField fUseDefaultJRE, fUseProjectJRE, fUseEEJRE;

		private final ComboDialogField fJRECombo;

		private final ComboDialogField fEECombo;

		private Group fGroup;

		private Link fPreferenceLink;

		private IVMInstall[] fInstalledJVMs;

		private String[] fJRECompliance;

		private IExecutionEnvironment[] fInstalledEEs;

		private String[] fEECompliance;

		public JREGroup() {
			fUseDefaultJRE = new SelectionButtonDialogField(SWT.RADIO);
			fUseDefaultJRE.setLabelText(getDefaultJVMLabel());

			fUseProjectJRE = new SelectionButtonDialogField(SWT.RADIO);
			fUseProjectJRE.setLabelText(NewJavaProjectWizardConstants.NewJavaProjectWizardPageOne_JREGroup_specific_compliance);

			fJRECombo = new ComboDialogField(SWT.READ_ONLY);
			fillInstalledJREs(fJRECombo);
			fJRECombo.setDialogFieldListener(this);

			fUseEEJRE = new SelectionButtonDialogField(SWT.RADIO);
			fUseEEJRE.setLabelText(NewJavaProjectWizardConstants.NewJavaProjectWizardPageOne_JREGroup_specific_EE);

			fEECombo = new ComboDialogField(SWT.READ_ONLY);
			fillExecutionEnvironments(fEECombo);
			fEECombo.setDialogFieldListener(this);

			switch (getLastSelectedJREKind()) {
			case DEFAULT_JRE:
				fUseDefaultJRE.setSelection(true);
				break;
			case PROJECT_JRE:
				fUseProjectJRE.setSelection(true);
				break;
			case EE_JRE:
				fUseEEJRE.setSelection(true);
				break;
			}

			fJRECombo.setEnabled(fUseProjectJRE.isSelected());
			fEECombo.setEnabled(fUseEEJRE.isSelected());

			fUseDefaultJRE.setDialogFieldListener(this);
			fUseProjectJRE.setDialogFieldListener(this);
			fUseEEJRE.setDialogFieldListener(this);
		}

		public Control createControl(Composite composite) {
			fGroup = new Group(composite, SWT.NONE);
			fGroup.setFont(composite.getFont());
			fGroup.setLayout(initGridLayout(new GridLayout(2, false), true));
			fGroup.setText(NewJavaProjectWizardConstants.NewJavaProjectWizardPageOne_JREGroup_title);

			fUseDefaultJRE.doFillIntoGrid(fGroup, 1);

			fPreferenceLink = new Link(fGroup, SWT.NONE);
			fPreferenceLink.setFont(fGroup.getFont());
			fPreferenceLink.setText(NewJavaProjectWizardConstants.NewJavaProjectWizardPageOne_JREGroup_link_description);
			fPreferenceLink.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
			fPreferenceLink.addSelectionListener(this);

			Composite nonDefaultJREComposite = new Composite(fGroup, SWT.NONE);
			nonDefaultJREComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			GridLayout layout = new GridLayout(2, false);
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			nonDefaultJREComposite.setLayout(layout);

			fUseProjectJRE.doFillIntoGrid(nonDefaultJREComposite, 1);

			Combo comboControl = fJRECombo.getComboControl(nonDefaultJREComposite);
			comboControl.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
			comboControl.setVisibleItemCount(30);

			fUseEEJRE.doFillIntoGrid(nonDefaultJREComposite, 1);

			Combo eeComboControl = fEECombo.getComboControl(nonDefaultJREComposite);
			eeComboControl.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
			eeComboControl.setVisibleItemCount(30);

			updateEnableState();
			return fGroup;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener
		 * #dialogFieldChanged(org.eclipse.jdt.internal.ui.wizards.dialogfields.
		 * DialogField)
		 */
		public void dialogFieldChanged(DialogField field) {
			updateEnableState();
			fDetectGroup.handlePossibleJVMChange();
			if (field == fJRECombo) {
				if (fUseProjectJRE.isSelected()) {
					storeSelectionValue(fJRECombo, LAST_SELECTED_JRE_SETTINGS_KEY);
				}
			} else if (field == fEECombo) {
				if (fUseEEJRE.isSelected()) {
					storeSelectionValue(fEECombo, LAST_SELECTED_EE_SETTINGS_KEY);
				}
			} else if (field == fUseDefaultJRE) {
				if (fUseDefaultJRE.isSelected()) {
					JavaPlugin.getDefault().getDialogSettings().put(LAST_SELECTED_JRE_KIND, DEFAULT_JRE);
					fUseProjectJRE.setSelection(false);
					fUseEEJRE.setSelection(false);
				}
			} else if (field == fUseProjectJRE) {
				if (fUseProjectJRE.isSelected()) {
					JavaPlugin.getDefault().getDialogSettings().put(LAST_SELECTED_JRE_KIND, PROJECT_JRE);
					fUseDefaultJRE.setSelection(false);
					fUseEEJRE.setSelection(false);
				}
			} else if (field == fUseEEJRE) {
				if (fUseEEJRE.isSelected()) {
					JavaPlugin.getDefault().getDialogSettings().put(LAST_SELECTED_JRE_KIND, EE_JRE);
					fUseDefaultJRE.setSelection(false);
					fUseProjectJRE.setSelection(false);
				}
			}
		}

		@SuppressWarnings("unchecked")
		private void fillExecutionEnvironments(ComboDialogField comboField) {
			String selectedItem = getLastSelectedEE();
			int selectionIndex = -1;
			if (fUseEEJRE.isSelected()) {
				selectionIndex = comboField.getSelectionIndex();
				if (selectionIndex != -1) {// paranoia
					selectedItem = comboField.getItems()[selectionIndex];
				}
			}

			fInstalledEEs = JavaRuntime.getExecutionEnvironmentsManager().getExecutionEnvironments();
			Arrays.sort(fInstalledEEs, new Comparator() {
				public int compare(Object arg0, Object arg1) {
					return Policy.getComparator().compare(((IExecutionEnvironment) arg0).getId(),
							((IExecutionEnvironment) arg1).getId());
				}
			});
			selectionIndex = -1;// find new index
			String[] eeLabels = new String[fInstalledEEs.length];
			fEECompliance = new String[fInstalledEEs.length];
			for (int i = 0; i < fInstalledEEs.length; i++) {
				eeLabels[i] = fInstalledEEs[i].getId();
				if (selectedItem != null && eeLabels[i].equals(selectedItem)) {
					selectionIndex = i;
				}
				fEECompliance[i] = JavaModelUtil.getExecutionEnvironmentCompliance(fInstalledEEs[i]);
			}
			comboField.setItems(eeLabels);
			if (selectionIndex == -1) {
				comboField.selectItem(getDefaultEEName());
			} else {
				comboField.selectItem(selectedItem);
			}
		}

		@SuppressWarnings("unchecked")
		private void fillInstalledJREs(ComboDialogField comboField) {
			String selectedItem = getLastSelectedJRE();
			int selectionIndex = -1;
			if (fUseProjectJRE.isSelected()) {
				selectionIndex = comboField.getSelectionIndex();
				if (selectionIndex != -1) {// paranoia
					selectedItem = comboField.getItems()[selectionIndex];
				}
			}

			fInstalledJVMs = getWorkspaceJREs();
			Arrays.sort(fInstalledJVMs, new Comparator() {

				public int compare(Object arg0, Object arg1) {
					IVMInstall i0 = (IVMInstall) arg0;
					IVMInstall i1 = (IVMInstall) arg1;
					if (i1 instanceof IVMInstall2 && i0 instanceof IVMInstall2) {
						String cc0 = JavaModelUtil.getCompilerCompliance((IVMInstall2) i0, JavaCore.VERSION_1_4);
						String cc1 = JavaModelUtil.getCompilerCompliance((IVMInstall2) i1, JavaCore.VERSION_1_4);
						int result = cc1.compareTo(cc0);
						if (result != 0) {
							return result;
						}
					}
					return Policy.getComparator().compare(i0.getName(), i1.getName());
				}

			});
			selectionIndex = -1;// find new index
			String[] jreLabels = new String[fInstalledJVMs.length];
			fJRECompliance = new String[fInstalledJVMs.length];
			for (int i = 0; i < fInstalledJVMs.length; i++) {
				jreLabels[i] = fInstalledJVMs[i].getName();
				if (selectedItem != null && jreLabels[i].equals(selectedItem)) {
					selectionIndex = i;
				}
				if (fInstalledJVMs[i] instanceof IVMInstall2) {
					fJRECompliance[i] = JavaModelUtil.getCompilerCompliance((IVMInstall2) fInstalledJVMs[i],
							JavaCore.VERSION_1_4);
				} else {
					fJRECompliance[i] = JavaCore.VERSION_1_4;
				}
			}
			comboField.setItems(jreLabels);
			if (selectionIndex == -1) {
				comboField.selectItem(getDefaultJVMName());
			} else {
				comboField.selectItem(selectedItem);
			}
		}

		private String getDefaultEEName() {
			IVMInstall defaultVM = JavaRuntime.getDefaultVMInstall();

			IExecutionEnvironment[] environments = JavaRuntime.getExecutionEnvironmentsManager()
					.getExecutionEnvironments();
			if (defaultVM != null) {
				for (IExecutionEnvironment environment : environments) {
					IVMInstall eeDefaultVM = environment.getDefaultVM();
					if (eeDefaultVM != null && defaultVM.getId().equals(eeDefaultVM.getId())) {
						return environment.getId();
					}
				}
			}

			String defaultCC;
			if (defaultVM instanceof IVMInstall2) {
				defaultCC = JavaModelUtil.getCompilerCompliance((IVMInstall2) defaultVM, JavaCore.VERSION_1_4);
			} else {
				defaultCC = JavaCore.VERSION_1_4;
			}

			for (IExecutionEnvironment environment : environments) {
				String eeCompliance = JavaModelUtil.getExecutionEnvironmentCompliance(environment);
				if (defaultCC.endsWith(eeCompliance)) {
					return environment.getId();
				}
			}

			return "J2SE-1.5"; //$NON-NLS-1$
		}

		private String getDefaultJVMLabel() {
			return Messages.format(
					NewJavaProjectWizardConstants.NewJavaProjectWizardPageOne_JREGroup_default_compliance,
					getDefaultJVMName());
		}

		private String getDefaultJVMName() {
			IVMInstall install = JavaRuntime.getDefaultVMInstall();
			if (install != null) {
				return install.getName();
			} else {
				return NewJavaProjectWizardConstants.NewJavaProjectWizardPageOne_UnknownDefaultJRE_name;
			}
		}

		public IPath getJREContainerPath() {
			if (fUseProjectJRE.isSelected()) {
				int index = fJRECombo.getSelectionIndex();
				if (index >= 0 && index < fInstalledJVMs.length) { // paranoia
					return JavaRuntime.newJREContainerPath(fInstalledJVMs[index]);
				}
			} else if (fUseEEJRE.isSelected()) {
				int index = fEECombo.getSelectionIndex();
				if (index >= 0 && index < fInstalledEEs.length) { // paranoia
					return JavaRuntime.newJREContainerPath(fInstalledEEs[index]);
				}
			}
			return null;
		}

		private String getLastSelectedEE() {
			IDialogSettings settings = JavaPlugin.getDefault().getDialogSettings();
			return settings.get(LAST_SELECTED_EE_SETTINGS_KEY);
		}

		private String getLastSelectedJRE() {
			IDialogSettings settings = JavaPlugin.getDefault().getDialogSettings();
			return settings.get(LAST_SELECTED_JRE_SETTINGS_KEY);
		}

		private int getLastSelectedJREKind() {
			IDialogSettings settings = JavaPlugin.getDefault().getDialogSettings();
			if (settings.get(LAST_SELECTED_JRE_KIND) == null) {
				return DEFAULT_JRE;
			}

			return settings.getInt(LAST_SELECTED_JRE_KIND);
		}

		public String getSelectedCompilerCompliance() {
			if (fUseProjectJRE.isSelected()) {
				int index = fJRECombo.getSelectionIndex();
				if (index >= 0 && index < fJRECompliance.length) { // paranoia
					return fJRECompliance[index];
				}
			} else if (fUseEEJRE.isSelected()) {
				int index = fEECombo.getSelectionIndex();
				if (index >= 0 && index < fEECompliance.length) { // paranoia
					return fEECompliance[index];
				}
			}
			return null;
		}

		public IVMInstall getSelectedJVM() {
			if (fUseProjectJRE.isSelected()) {
				int index = fJRECombo.getSelectionIndex();
				if (index >= 0 && index < fInstalledJVMs.length) { // paranoia
					return fInstalledJVMs[index];
				}
			} else if (fUseEEJRE.isSelected()) {

			}
			return null;
		}

		@SuppressWarnings("unchecked")
		private IVMInstall[] getWorkspaceJREs() {
			List standins = new ArrayList();
			IVMInstallType[] types = JavaRuntime.getVMInstallTypes();
			for (IVMInstallType type : types) {
				IVMInstall[] installs = type.getVMInstalls();
				for (IVMInstall install : installs) {
					standins.add(new VMStandin(install));
				}
			}
			return ((IVMInstall[]) standins.toArray(new IVMInstall[standins.size()]));
		}

		public void handlePossibleJVMChange() {
			fUseDefaultJRE.setLabelText(getDefaultJVMLabel());
			fillInstalledJREs(fJRECombo);
			fillExecutionEnvironments(fEECombo);
		}

		private void storeSelectionValue(ComboDialogField combo, String preferenceKey) {
			int index = combo.getSelectionIndex();
			if (index == -1) {
				return;
			}

			String item = combo.getItems()[index];
			JavaPlugin.getDefault().getDialogSettings().put(preferenceKey, item);
		}

		public void update(Observable o, Object arg) {
			updateEnableState();
		}

		private void updateEnableState() {
			final boolean detect = fDetectGroup.mustDetect();
			fUseDefaultJRE.setEnabled(!detect);
			fUseProjectJRE.setEnabled(!detect);
			fUseEEJRE.setEnabled(!detect);
			fJRECombo.setEnabled(!detect && fUseProjectJRE.isSelected());
			fEECombo.setEnabled(!detect && fUseEEJRE.isSelected());
			if (fPreferenceLink != null) {
				fPreferenceLink.setEnabled(!detect);
			}
			if (fGroup != null) {
				fGroup.setEnabled(!detect);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org
		 * .eclipse.swt.events.SelectionEvent)
		 */
		@SuppressWarnings("unchecked")
		public void widgetDefaultSelected(SelectionEvent e) {
			String jreID = BuildPathSupport.JRE_PREF_PAGE_ID;
			String complianceId = CompliancePreferencePage.PREF_ID;
			Map data = new HashMap();
			data.put(PropertyAndPreferencePage.DATA_NO_LINK, Boolean.TRUE);
			PreferencesUtil.createPreferenceDialogOn(getShell(), jreID, new String[] { jreID, complianceId }, data)
					.open();

			handlePossibleJVMChange();
			fDetectGroup.handlePossibleJVMChange();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse
		 * .swt.events.SelectionEvent)
		 */
		public void widgetSelected(SelectionEvent e) {
			widgetDefaultSelected(e);
		}
	}

	/**
	 * Request a project layout.
	 */
	private final class LayoutGroup implements Observer, SelectionListener {

		private final SelectionButtonDialogField fStdRadio, fSrcBinRadio;

		private Group fGroup;

		private Link fPreferenceLink;

		public LayoutGroup() {
			fStdRadio = new SelectionButtonDialogField(SWT.RADIO);
			fStdRadio.setLabelText(NewJavaProjectWizardConstants.NewJavaProjectWizardPageOne_LayoutGroup_option_oneFolder);

			fSrcBinRadio = new SelectionButtonDialogField(SWT.RADIO);
			fSrcBinRadio.setLabelText(NewJavaProjectWizardConstants.NewJavaProjectWizardPageOne_LayoutGroup_option_separateFolders);

			boolean useSrcBin = PreferenceConstants.getPreferenceStore().getBoolean(
					PreferenceConstants.SRCBIN_FOLDERS_IN_NEWPROJ);
			fSrcBinRadio.setSelection(useSrcBin);
			fStdRadio.setSelection(!useSrcBin);
		}

		public Control createContent(Composite composite) {
			fGroup = new Group(composite, SWT.NONE);
			fGroup.setFont(composite.getFont());
			fGroup.setLayout(initGridLayout(new GridLayout(3, false), true));
			fGroup.setText(NewJavaProjectWizardConstants.NewJavaProjectWizardPageOne_LayoutGroup_title);

			fStdRadio.doFillIntoGrid(fGroup, 3);
			LayoutUtil.setHorizontalGrabbing(fStdRadio.getSelectionButton(null));

			fSrcBinRadio.doFillIntoGrid(fGroup, 2);

			fPreferenceLink = new Link(fGroup, SWT.NONE);
			fPreferenceLink.setText(NewJavaProjectWizardConstants.NewJavaProjectWizardPageOne_LayoutGroup_link_description);
			fPreferenceLink.setLayoutData(new GridData(GridData.END, GridData.END, false, false));
			fPreferenceLink.addSelectionListener(this);

			updateEnableState();
			return fGroup;
		}

		/**
		 * Return <code>true</code> if the user specified to create 'source' and 'bin' folders.
		 * 
		 * @return returns <code>true</code> if the user specified to create 'source' and 'bin' folders.
		 */
		public boolean isSrcBin() {
			return fSrcBinRadio.isSelected();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observer#update(java.util.Observable,
		 * java.lang.Object)
		 */
		public void update(Observable o, Object arg) {
			updateEnableState();
		}

		private void updateEnableState() {
			final boolean detect = fDetectGroup.mustDetect();
			fStdRadio.setEnabled(!detect);
			fSrcBinRadio.setEnabled(!detect);
			if (fPreferenceLink != null) {
				fPreferenceLink.setEnabled(!detect);
			}
			if (fGroup != null) {
				fGroup.setEnabled(!detect);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org
		 * .eclipse.swt.events.SelectionEvent)
		 */
		public void widgetDefaultSelected(SelectionEvent e) {
			String id = NewJavaProjectPreferencePage.ID;
			PreferencesUtil.createPreferenceDialogOn(getShell(), id, new String[] { id }, null).open();
			fDetectGroup.handlePossibleJVMChange();
			fJREGroup.handlePossibleJVMChange();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse
		 * .swt.events.SelectionEvent)
		 */
		public void widgetSelected(SelectionEvent e) {
			widgetDefaultSelected(e);
		}
	}

	/**
	 * Request a location. Fires an event whenever the checkbox or the location field is changed, regardless of whether
	 * the change originates from the user or has been invoked programmatically.
	 */
	private final class LocationGroup extends Observable implements Observer, IStringButtonAdapter,
			IDialogFieldListener {

		private static final String DIALOGSTORE_LAST_EXTERNAL_LOC = JavaUI.ID_PLUGIN + ".last.external.project"; //$NON-NLS-1$

		protected final SelectionButtonDialogField fWorkspaceRadio;

		protected final SelectionButtonDialogField fExternalRadio;

		protected final StringButtonDialogField fLocation;

		private String fPreviousExternalLocation;

		public LocationGroup() {
			fWorkspaceRadio = new SelectionButtonDialogField(SWT.RADIO);
			fWorkspaceRadio.setDialogFieldListener(this);
			fWorkspaceRadio.setLabelText(NewJavaProjectWizardConstants.NewJavaProjectWizardPageOne_LocationGroup_workspace_desc);

			fExternalRadio = new SelectionButtonDialogField(SWT.RADIO);
			fExternalRadio.setLabelText(NewJavaProjectWizardConstants.NewJavaProjectWizardPageOne_LocationGroup_external_desc);

			fLocation = new StringButtonDialogField(this);
			fLocation.setDialogFieldListener(this);
			fLocation.setLabelText(NewJavaProjectWizardConstants.NewJavaProjectWizardPageOne_LocationGroup_locationLabel_desc);
			fLocation.setButtonLabel(NewJavaProjectWizardConstants.NewJavaProjectWizardPageOne_LocationGroup_browseButton_desc);

			fExternalRadio.attachDialogField(fLocation);

			fWorkspaceRadio.setSelection(true);
			fExternalRadio.setSelection(false);

			fPreviousExternalLocation = ""; //$NON-NLS-1$
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jdt.internal.ui.wizards.dialogfields.IStringButtonAdapter
		 * #
		 * changeControlPressed(org.eclipse.jdt.internal.ui.wizards.dialogfields
		 * .DialogField)
		 */
		public void changeControlPressed(DialogField field) {
			final DirectoryDialog dialog = new DirectoryDialog(getShell());
			dialog.setMessage(NewJavaProjectWizardConstants.NewJavaProjectWizardPageOne_directory_message);
			String directoryName = fLocation.getText().trim();
			if (directoryName.length() == 0) {
				String prevLocation = JavaPlugin.getDefault().getDialogSettings().get(DIALOGSTORE_LAST_EXTERNAL_LOC);
				if (prevLocation != null) {
					directoryName = prevLocation;
				}
			}

			if (directoryName.length() > 0) {
				final File path = new File(directoryName);
				if (path.exists()) {
					dialog.setFilterPath(directoryName);
				}
			}
			final String selectedDirectory = dialog.open();
			if (selectedDirectory != null) {
				fLocation.setText(selectedDirectory);
				JavaPlugin.getDefault().getDialogSettings().put(DIALOGSTORE_LAST_EXTERNAL_LOC, selectedDirectory);
			}
		}

		public Control createControl(Composite composite) {
			final int numColumns = 3;

			final Group group = new Group(composite, SWT.NONE);
			group.setLayout(initGridLayout(new GridLayout(numColumns, false), true));
			group.setText(NewJavaProjectWizardConstants.NewJavaProjectWizardPageOne_LocationGroup_title);

			fWorkspaceRadio.doFillIntoGrid(group, numColumns);
			fExternalRadio.doFillIntoGrid(group, numColumns);
			fLocation.doFillIntoGrid(group, numColumns);
			LayoutUtil.setHorizontalGrabbing(fLocation.getTextControl(null));

			return group;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener
		 * #dialogFieldChanged(org.eclipse.jdt.internal.ui.wizards.dialogfields.
		 * DialogField)
		 */
		public void dialogFieldChanged(DialogField field) {
			if (field == fWorkspaceRadio) {
				final boolean checked = fWorkspaceRadio.isSelected();
				if (checked) {
					fPreviousExternalLocation = fLocation.getText();
					fLocation.setText(getDefaultPath(fNameGroup.getName()));
				} else {
					fLocation.setText(fPreviousExternalLocation);
				}
			}
			fireEvent();
		}

		protected void fireEvent() {
			setChanged();
			notifyObservers();
		}

		protected String getDefaultPath(String name) {
			final IPath path = Platform.getLocation().append(name);
			return path.toOSString();
		}

		public IPath getLocation() {
			if (isWorkspaceRadioSelected()) {
				return Platform.getLocation();
			}
			return Path.fromOSString(fLocation.getText().trim());
		}

		/**
		 * Returns <code>true</code> if the location is in the workspace
		 * 
		 * @return <code>true</code> if the location is in the workspace
		 */
		public boolean isLocationInWorkspace() {
			final String location = fLocationGroup.getLocation().toOSString();
			IPath projectPath = Path.fromOSString(location);
			return Platform.getLocation().isPrefixOf(projectPath);
		}

		public boolean isWorkspaceRadioSelected() {
			return fWorkspaceRadio.isSelected();
		}

		public void setLocation(IPath path) {
			fWorkspaceRadio.setSelection(path == null);
			if (path != null) {
				fLocation.setText(path.toOSString());
			} else {
				fLocation.setText(getDefaultPath(fNameGroup.getName()));
			}
			fireEvent();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observer#update(java.util.Observable,
		 * java.lang.Object)
		 */
		public void update(Observable o, Object arg) {
			if (isWorkspaceRadioSelected()) {
				fLocation.setText(getDefaultPath(fNameGroup.getName()));
			}
			fireEvent();
		}
	}

	/**
	 * Request a project name. Fires an event whenever the text field is changed, regardless of its content.
	 */
	private final class NameGroup extends Observable implements IDialogFieldListener {

		protected final StringDialogField fNameField;

		public NameGroup() {
			// text field for project name
			fNameField = new StringDialogField();
			fNameField.setLabelText(NewJavaProjectWizardConstants.NewJavaProjectWizardPageOne_NameGroup_label_text);
			fNameField.setDialogFieldListener(this);
		}

		public Control createControl(Composite composite) {
			Composite nameComposite = new Composite(composite, SWT.NONE);
			nameComposite.setFont(composite.getFont());
			nameComposite.setLayout(initGridLayout(new GridLayout(2, false), false));

			fNameField.doFillIntoGrid(nameComposite, 2);
			LayoutUtil.setHorizontalGrabbing(fNameField.getTextControl(null));

			return nameComposite;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener
		 * #dialogFieldChanged(org.eclipse.jdt.internal.ui.wizards.dialogfields.
		 * DialogField)
		 */
		public void dialogFieldChanged(DialogField field) {
			fireEvent();
		}

		protected void fireEvent() {
			setChanged();
			notifyObservers();
		}

		public String getName() {
			return fNameField.getText().trim();
		}

		public void postSetFocus() {
			fNameField.postSetFocusOnDialogField(getShell().getDisplay());
		}

		public void setName(String name) {
			fNameField.setText(name);
		}
	}

	/**
	 * Validate this page and show appropriate warnings and error NewWizardMessages.
	 */
	private final class Validator implements Observer {

		private boolean canCreate(File file) {
			while (!file.exists()) {
				file = file.getParentFile();
				if (file == null) {
					return false;
				}
			}

			return file.canWrite();
		}

		public void update(Observable o, Object arg) {

			final IWorkspace workspace = JavaPlugin.getWorkspace();

			final String name = fNameGroup.getName();

			// check whether the project name field is empty
			if (name.length() == 0) {
				setErrorMessage(null);
				setMessage(NewJavaProjectWizardConstants.NewJavaProjectWizardPageOne_Message_enterProjectName);
				setPageComplete(false);
				return;
			}

			// check whether the project name is valid
			final IStatus nameStatus = workspace.validateName(name, IResource.PROJECT);
			if (!nameStatus.isOK()) {
				setErrorMessage(nameStatus.getMessage());
				setPageComplete(false);
				return;
			}

			// check whether project already exists
			final IProject handle = workspace.getRoot().getProject(name);
			if (handle.exists()) {
				setErrorMessage(NewJavaProjectWizardConstants.NewJavaProjectWizardPageOne_Message_projectAlreadyExists);
				setPageComplete(false);
				return;
			}

			IPath projectLocation = ResourcesPlugin.getWorkspace().getRoot().getLocation().append(name);
			if (projectLocation.toFile().exists()) {
				try {
					// correct casing
					String canonicalPath = projectLocation.toFile().getCanonicalPath();
					projectLocation = new Path(canonicalPath);
				} catch (IOException e) {
					JavaPlugin.log(e);
				}

				String existingName = projectLocation.lastSegment();
				if (!existingName.equals(fNameGroup.getName())) {
					setErrorMessage(Messages.format(
							NewJavaProjectWizardConstants.NewJavaProjectWizardPageOne_Message_invalidProjectNameForWorkspaceRoot,
							existingName));
					setPageComplete(false);
					return;
				}

			}

			final String location = fLocationGroup.getLocation().toOSString();

			// check whether location is empty
			if (location.length() == 0) {
				setErrorMessage(null);
				setMessage(NewJavaProjectWizardConstants.NewJavaProjectWizardPageOne_Message_enterLocation);
				setPageComplete(false);
				return;
			}

			// check whether the location is a syntactically correct path
			if (!Path.EMPTY.isValidPath(location)) {
				setErrorMessage(NewJavaProjectWizardConstants.NewJavaProjectWizardPageOne_Message_invalidDirectory);
				setPageComplete(false);
				return;
			}

			IPath projectPath = Path.fromOSString(location);

			if (fLocationGroup.isWorkspaceRadioSelected()) {
				projectPath = projectPath.append(fNameGroup.getName());
			}

			if (projectPath.toFile().exists()) {// create from existing source
				if (Platform.getLocation().isPrefixOf(projectPath)) { // create
					// from
					// existing
					// source
					// in
					// workspace
					if (!Platform.getLocation().equals(projectPath.removeLastSegments(1))) {
						setErrorMessage(NewJavaProjectWizardConstants.NewJavaProjectWizardPageOne_Message_notOnWorkspaceRoot);
						setPageComplete(false);
						return;
					}

					if (!projectPath.toFile().exists()) {
						setErrorMessage(NewJavaProjectWizardConstants.NewJavaProjectWizardPageOne_Message_notExisingProjectOnWorkspaceRoot);
						setPageComplete(false);
						return;
					}
				}
			} else if (!fLocationGroup.isWorkspaceRadioSelected()) {// create at
				// non
				// existing
				// external
				// location
				if (!canCreate(projectPath.toFile())) {
					setErrorMessage(NewJavaProjectWizardConstants.NewJavaProjectWizardPageOne_Message_cannotCreateAtExternalLocation);
					setPageComplete(false);
					return;
				}

				// If we do not place the contents in the workspace validate the
				// location.
				final IStatus locationStatus = workspace.validateProjectLocation(handle, projectPath);
				if (!locationStatus.isOK()) {
					setErrorMessage(locationStatus.getMessage());
					setPageComplete(false);
					return;
				}
			}

			setPageComplete(true);

			setErrorMessage(null);
			setMessage(null);
		}
	}

	// private final class WorkingSetGroup {
	//
	// private final WorkingSetConfigurationBlock fWorkingSetBlock;
	//
	// public WorkingSetGroup() {
	// String[] workingSetIds = new String[] { JavaWorkingSetUpdater.ID,
	// "org.eclipse.ui.resourceWorkingSetPage" }; //$NON-NLS-1$
	// // String label = "Add project to working sets";
	// fWorkingSetBlock = new WorkingSetConfigurationBlock(workingSetIds,
	// JavaPlugin.getDefault()
	// .getDialogSettings());
	// //
	// fWorkingSetBlock.setDialogMessage(NewWizardMessages.NewJavaProjectWizardPageOne_WorkingSetSelection_message);
	// }
	//
	// public Control createControl(Composite composite) {
	// Group workingSetGroup = new Group(composite, SWT.NONE);
	// workingSetGroup.setFont(composite.getFont());
	// workingSetGroup.setText(NewJavaProjectWizardConstants.NewJavaProjectWizardPageOne_WorkingSets_group);
	// workingSetGroup.setLayout(new GridLayout(1, false));
	//
	// fWorkingSetBlock.createContent(workingSetGroup);
	//
	// return workingSetGroup;
	// }
	//
	// public IWorkingSet[] getSelectedWorkingSets() {
	// return fWorkingSetBlock.getSelectedWorkingSets();
	// }
	//
	// public void setWorkingSets(IWorkingSet[] workingSets) {
	// fWorkingSetBlock.setWorkingSets(workingSets);
	// }
	// }

	private static final String PAGE_NAME = "NewJavaProjectWizardPageOne"; //$NON-NLS-1$

	// private static final IWorkingSet[] EMPTY_WORKING_SET_ARRAY = new
	// IWorkingSet[0];
	//
	// private static boolean isValidWorkingSet(IWorkingSet workingSet) {
	// String id = workingSet.getId();
	// if (!JavaWorkingSetUpdater.ID.equals(id) &&
	// !"org.eclipse.ui.resourceWorkingSetPage".equals(id)) {
	// return false;
	// }
	//
	// if (workingSet.isAggregateWorkingSet()) {
	// return false;
	// }
	//
	// return true;
	// }

	private final NameGroup fNameGroup;

	private final LocationGroup fLocationGroup;

	private final LayoutGroup fLayoutGroup;

	private final JREGroup fJREGroup;

	private final DetectGroup fDetectGroup;

	private final Validator fValidator;

	// private final WorkingSetGroup fWorkingSetGroup;

	/**
	 * Creates a new {@link NewJavaProjectWizardPageOneCOPY}.
	 */
	public NewJavaProjectWizardPageOneCOPY() {
		super(PAGE_NAME);
		setPageComplete(false);
		setTitle(NewJavaProjectWizardConstants.NewJavaProjectWizardPageOne_page_title);
		setDescription(NewJavaProjectWizardConstants.NewJavaProjectWizardPageOne_page_description);

		fNameGroup = new NameGroup();
		fLocationGroup = new LocationGroup();
		fJREGroup = new JREGroup();
		fLayoutGroup = new LayoutGroup();
		// fWorkingSetGroup = new WorkingSetGroup();
		fDetectGroup = new DetectGroup();

		// establish connections
		fNameGroup.addObserver(fLocationGroup);
		fDetectGroup.addObserver(fLayoutGroup);
		fDetectGroup.addObserver(fJREGroup);
		fLocationGroup.addObserver(fDetectGroup);

		// initialize all elements
		fNameGroup.notifyObservers();

		// create and connect validator
		fValidator = new Validator();
		fNameGroup.addObserver(fValidator);
		fLocationGroup.addObserver(fValidator);

		// initialize defaults
		setProjectName(""); //$NON-NLS-1$
		setProjectLocationURI(null);
		// setWorkingSets(new IWorkingSet[0]);

		initializeDefaultVM();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);

		final Composite composite = new Composite(parent, SWT.NULL);
		composite.setFont(parent.getFont());
		composite.setLayout(initGridLayout(new GridLayout(1, false), true));
		composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		// create UI elements
		Control nameControl = createNameControl(composite);
		nameControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Control locationControl = createLocationControl(composite);
		locationControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Control jreControl = createJRESelectionControl(composite);
		jreControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Control layoutControl = createProjectLayoutControl(composite);
		layoutControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Control workingSetControl = createWorkingSetControl(composite);
		// workingSetControl.setLayoutData(new
		// GridData(GridData.FILL_HORIZONTAL));

		Control infoControl = createInfoControl(composite);
		infoControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		setControl(composite);
	}

	/**
	 * Creates the controls for the info section.
	 * 
	 * @param composite
	 *            the parent composite
	 * @return the created control
	 */
	protected Control createInfoControl(Composite composite) {
		return fDetectGroup.createControl(composite);
	}

	/**
	 * Creates the controls for the JRE selection
	 * 
	 * @param composite
	 *            the parent composite
	 * @return the created control
	 */
	protected Control createJRESelectionControl(Composite composite) {
		return fJREGroup.createControl(composite);
	}

	/**
	 * Creates the controls for the location field.
	 * 
	 * @param composite
	 *            the parent composite
	 * @return the created control
	 */
	protected Control createLocationControl(Composite composite) {
		return fLocationGroup.createControl(composite);
	}

	/**
	 * Creates the controls for the name field.
	 * 
	 * @param composite
	 *            the parent composite
	 * @return the created control
	 */
	protected Control createNameControl(Composite composite) {
		return fNameGroup.createControl(composite);
	}

	/**
	 * Creates the controls for the project layout selection.
	 * 
	 * @param composite
	 *            the parent composite
	 * @return the created control
	 */
	protected Control createProjectLayoutControl(Composite composite) {
		return fLayoutGroup.createContent(composite);
	}

	// /**
	// * Creates the controls for the working set selection.
	// *
	// * @param composite the parent composite
	// * @return the created control
	// */
	// protected Control createWorkingSetControl(Composite composite) {
	// return fWorkingSetGroup.createControl(composite);
	// }

	/**
	 * Returns the compiler compliance to be used for the project, or <code>null</code> to use the workspace compiler
	 * compliance.
	 * 
	 * @return compiler compliance to be used for the project or <code>null</code>
	 */
	public String getCompilerCompliance() {
		return fJREGroup.getSelectedCompilerCompliance();
	}

	/**
	 * Returns the default class path entries to be added on new projects. By default this is the JRE container as
	 * selected by the user.
	 * 
	 * @return returns the default class path entries
	 */
	public IClasspathEntry[] getDefaultClasspathEntries() {
		IClasspathEntry[] defaultJRELibrary = PreferenceConstants.getDefaultJRELibrary();
		String compliance = getCompilerCompliance();
		IPath jreContainerPath = new Path(JavaRuntime.JRE_CONTAINER);
		if (compliance == null || defaultJRELibrary.length > 1
				|| !jreContainerPath.isPrefixOf(defaultJRELibrary[0].getPath())) {
			// use default
			return defaultJRELibrary;
		}
		IPath newPath = fJREGroup.getJREContainerPath();
		if (newPath != null) {
			return new IClasspathEntry[] { JavaCore.newContainerEntry(newPath) };
		}
		return defaultJRELibrary;
	}

	/**
	 * Returns the source class path entries to be added on new projects. The underlying resource may not exist.
	 * 
	 * @return returns the default class path entries
	 */
	public IPath getOutputLocation() {
		IPath outputLocationPath = new Path(getProjectName()).makeAbsolute();
		if (fLayoutGroup.isSrcBin()) {
			IPath binPath = new Path(PreferenceConstants.getPreferenceStore().getString(
					PreferenceConstants.SRCBIN_BINNAME));
			if (binPath.segmentCount() > 0) {
				outputLocationPath = outputLocationPath.append(binPath);
			}
		}
		return outputLocationPath;
	}

	/**
	 * Returns the current project location path as entered by the user, or <code>null</code> if the project should be
	 * created in the workspace.
	 * 
	 * @return the project location path or its anticipated initial value.
	 */
	public URI getProjectLocationURI() {
		if (fLocationGroup.isLocationInWorkspace()) {
			return null;
		}
		return URIUtil.toURI(fLocationGroup.getLocation());
	}

	/**
	 * Gets a project name for the new project.
	 * 
	 * @return the new project resource handle
	 */
	public String getProjectName() {
		return fNameGroup.getName();
	}

	// @SuppressWarnings("unchecked")
	// private IWorkingSet[] getSelectedWorkingSet(IStructuredSelection
	// selection) {
	// if (!(selection instanceof ITreeSelection)) {
	// return EMPTY_WORKING_SET_ARRAY;
	// }
	//
	// ITreeSelection treeSelection = (ITreeSelection) selection;
	// if (treeSelection.isEmpty()) {
	// return EMPTY_WORKING_SET_ARRAY;
	// }
	//
	// List elements = treeSelection.toList();
	// if (elements.size() == 1) {
	// Object element = elements.get(0);
	// TreePath[] paths = treeSelection.getPathsFor(element);
	// if (paths.length != 1) {
	// return EMPTY_WORKING_SET_ARRAY;
	// }
	//
	// TreePath path = paths[0];
	// if (path.getSegmentCount() == 0) {
	// return EMPTY_WORKING_SET_ARRAY;
	// }
	//
	// Object candidate = path.getSegment(0);
	// if (!(candidate instanceof IWorkingSet)) {
	// return EMPTY_WORKING_SET_ARRAY;
	// }
	//
	// IWorkingSet workingSetCandidate = (IWorkingSet) candidate;
	// if (isValidWorkingSet(workingSetCandidate)) {
	// return new IWorkingSet[] { workingSetCandidate };
	// }
	//
	// return EMPTY_WORKING_SET_ARRAY;
	// }
	//
	// ArrayList result = new ArrayList();
	// for (Iterator iterator = elements.iterator(); iterator.hasNext();) {
	// Object element = iterator.next();
	// if (element instanceof IWorkingSet && isValidWorkingSet((IWorkingSet)
	// element)) {
	// result.add(element);
	// }
	// }
	// return (IWorkingSet[]) result.toArray(new IWorkingSet[result.size()]);
	// }

	// private IWorkingSet[] getSelectedWorkingSet(IStructuredSelection
	// selection, IWorkbenchPart activePart) {
	// IWorkingSet[] selected = getSelectedWorkingSet(selection);
	// if (selected != null && selected.length > 0) {
	// for (int i = 0; i < selected.length; i++) {
	// if (!isValidWorkingSet(selected[i])) {
	// return EMPTY_WORKING_SET_ARRAY;
	// }
	// }
	// return selected;
	// }
	//
	// if (!(activePart instanceof PackageExplorerPart)) {
	// return EMPTY_WORKING_SET_ARRAY;
	// }
	//
	// PackageExplorerPart explorerPart = (PackageExplorerPart) activePart;
	// if (explorerPart.getRootMode() ==
	// NewJavaProjectWizardConstants.PROJECTS_AS_ROOTS) {
	// // Get active filter
	// IWorkingSet filterWorkingSet = explorerPart.getFilterWorkingSet();
	// if (filterWorkingSet == null) {
	// return EMPTY_WORKING_SET_ARRAY;
	// }
	//
	// if (!isValidWorkingSet(filterWorkingSet)) {
	// return EMPTY_WORKING_SET_ARRAY;
	// }
	//
	// return new IWorkingSet[] { filterWorkingSet };
	// }
	// else {
	// // If we have been gone into a working set return the working set
	// Object input = explorerPart.getViewPartInput();
	// if (!(input instanceof IWorkingSet)) {
	// return EMPTY_WORKING_SET_ARRAY;
	// }
	//
	// IWorkingSet workingSet = (IWorkingSet) input;
	// if (!isValidWorkingSet(workingSet)) {
	// return EMPTY_WORKING_SET_ARRAY;
	// }
	//
	// return new IWorkingSet[] { workingSet };
	// }
	// }

	/**
	 * Returns the source class path entries to be added on new projects. The underlying resources may not exist. All
	 * entries that are returned must be of kind {@link IClasspathEntry#CPE_SOURCE}.
	 * 
	 * @return returns the source class path entries for the new project
	 */
	public IClasspathEntry[] getSourceClasspathEntries() {
		IPath sourceFolderPath = new Path(getProjectName()).makeAbsolute();

		if (fLayoutGroup.isSrcBin()) {
			IPath srcPath = new Path(PreferenceConstants.getPreferenceStore().getString(
					PreferenceConstants.SRCBIN_SRCNAME));
			if (srcPath.segmentCount() > 0) {
				sourceFolderPath = sourceFolderPath.append(srcPath);
			}
		}
		return new IClasspathEntry[] { JavaCore.newSourceEntry(sourceFolderPath) };
	}

	// /**
	// * Returns the working sets to which the new project should be added.
	// *
	// * @return the selected working sets to which the new project should be
	// * added
	// */
	// public IWorkingSet[] getWorkingSets() {
	// return fWorkingSetGroup.getSelectedWorkingSets();
	// }

	/**
	 * The wizard owning this page can call this method to initialize the fields from the current selection and active
	 * part.
	 * 
	 * @param selection
	 *            used to initialize the fields
	 * @param activePart
	 *            the (typically active) part to initialize the fields or <code>null</code>
	 */
	public void init(IStructuredSelection selection, IWorkbenchPart activePart) {
		// setWorkingSets(getSelectedWorkingSet(selection, activePart));
	}

	private GridLayout initGridLayout(GridLayout layout, boolean margins) {
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		if (margins) {
			layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
			layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		} else {
			layout.marginWidth = 0;
			layout.marginHeight = 0;
		}
		return layout;
	}

	private void initializeDefaultVM() {
		JavaRuntime.getDefaultVMInstall();
	}

	@Override
	protected void setControl(Control newControl) {
		Dialog.applyDialogFont(newControl);

		PlatformUI.getWorkbench().getHelpSystem().setHelp(newControl, IJavaHelpContextIds.NEW_JAVAPROJECT_WIZARD_PAGE);

		super.setControl(newControl);
	}

	/**
	 * Sets the project location of the new project or <code>null</code> if the project should be created in the
	 * workspace
	 * 
	 * @param uri
	 *            the new project location
	 */
	public void setProjectLocationURI(URI uri) {
		IPath path = uri != null ? URIUtil.toPath(uri) : null;
		fLocationGroup.setLocation(path);
	}

	/**
	 * Sets the name of the new project
	 * 
	 * @param name
	 *            the new name
	 */
	public void setProjectName(String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		fNameGroup.setName(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.DialogPage#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			fNameGroup.postSetFocus();
		}
	}

	// /**
	// * Sets the working sets to which the new project should be added.
	// *
	// * @param workingSets the initial selected working sets
	// */
	// public void setWorkingSets(IWorkingSet[] workingSets) {
	// if (workingSets == null) {
	// throw new IllegalArgumentException();
	// }
	// fWorkingSetGroup.setWorkingSets(workingSets);
	// }

}
