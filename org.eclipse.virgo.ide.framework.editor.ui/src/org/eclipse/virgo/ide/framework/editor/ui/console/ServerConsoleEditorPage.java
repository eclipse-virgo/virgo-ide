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
package org.eclipse.virgo.ide.framework.editor.ui.console;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.virgo.ide.framework.editor.core.IOSGiFrameworkConsole;
import org.eclipse.virgo.ide.framework.editor.ui.internal.AbstractBundleEditorPage;
import org.eclipse.virgo.ide.framework.editor.ui.internal.EditorUIPlugin;
import org.eclipse.virgo.ide.framework.editor.ui.internal.SearchTextHistory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.commands.ICommandImageService;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.wst.server.ui.ServerUICore;


/**
 * @author Christian Dupuis
 * @author Kaloyan Raev
 */
@SuppressWarnings("restriction")
public class ServerConsoleEditorPage extends AbstractBundleEditorPage {

	Text commandText;
	
	StyledText manifestText;

	private IToolBarManager toolBarManager;

	private Action backAction;

	private Action forwardAction;

	private Action refreshAction;

	final SearchTextHistory history = new SearchTextHistory();

	@Override
	protected void createBundleContent(Composite parent) {

		mform = new ManagedForm(parent);
		setManagedForm(mform);
		sform = mform.getForm();
		FormToolkit toolkit = mform.getToolkit();
		sform.setText("Server Console");
		sform.setImage(ServerUICore.getLabelProvider().getImage(getServer()));
		sform.setExpandHorizontal(true);
		sform.setExpandVertical(true);
		toolkit.decorateFormHeading(sform.getForm());

		Composite body = sform.getBody();
		GridLayout layout = new GridLayout(1, false);
		layout.marginLeft = 6;
		layout.marginTop = 6;
		layout.marginRight = 6;
		body.setLayout(layout);

		Section manifestSection = toolkit.createSection(sform.getBody(), ExpandableComposite.TITLE_BAR
				| Section.DESCRIPTION);
		manifestSection.setText("Commands");
		manifestSection.setDescription("Execute commands on server.");
		layout = new GridLayout();
		manifestSection.setLayout(layout);
		manifestSection.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite manifestComposite = toolkit.createComposite(manifestSection);
		layout = new GridLayout();
		layout.marginLeft = 6;
		layout.marginTop = 6;
		layout.numColumns = 3;
		manifestComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		manifestComposite.setLayout(layout);
		manifestSection.setClient(manifestComposite);

		Label commandLabel = toolkit.createLabel(manifestComposite, "Command:");
		commandLabel.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		GridDataFactory.fillDefaults().grab(true, false).span(3, 1).applyTo(commandLabel);
		commandText = toolkit.createText(manifestComposite, "", SWT.CANCEL | SWT.SEARCH);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(commandText);

		commandText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.character == SWT.CR || e.character == SWT.LF) {
					history.add(commandText.getText());
					String cmdLine = commandText.getText();
					executeCommand(cmdLine);
				}
				else if (e.keyCode == SWT.ARROW_UP) {
					String command = history.back();
					commandText.setText(command);
					commandText.setSelection(command.length());
					e.doit = false;
				}
				else if (e.keyCode == SWT.ARROW_DOWN) {
					String command = history.forward();
					commandText.setText(command);
					commandText.setSelection(command.length());
					e.doit = false;
				}
			}
		});

		Button commandButton = toolkit.createButton(manifestComposite, "Execute", SWT.PUSH);
		commandButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				history.add(commandText.getText());
				String cmdLine = commandText.getText();
				executeCommand(cmdLine);
			}
		});
		Button clearButton = toolkit.createButton(manifestComposite, "Clear", SWT.PUSH);
		clearButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				manifestText.setText("");
			}
		});

		manifestText = new StyledText(manifestComposite, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		manifestText.setWordWrap(false);
		manifestText.setFont(JFaceResources.getTextFont());
		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 3;
		manifestText.setLayoutData(data);

		Label helpLabel = toolkit.createLabel(manifestComposite, "Type 'help' to get a list of supported commands.");
		GridDataFactory.fillDefaults().grab(true, false).span(3, 1).applyTo(helpLabel);

		toolBarManager = sform.getToolBarManager();

		backAction = new Action("Back") {
			@Override
			public void run() {
				commandText.setText(history.back());
				String cmdLine = commandText.getText();
				executeCommand(cmdLine);
			}
		};
		
		final ICommandImageService service = getSite().getWorkbenchWindow().getService(ICommandImageService.class);
		final ImageDescriptor imgDescBack = service.getImageDescriptor(IWorkbenchCommandConstants.NAVIGATE_BACK);
		backAction.setImageDescriptor(imgDescBack);
		backAction.setHoverImageDescriptor(imgDescBack);
		backAction.setDisabledImageDescriptor(imgDescBack);
		backAction.setEnabled(false);
		toolBarManager.add(backAction);

		forwardAction = new Action("Forward") {
			@Override
			public void run() {
				commandText.setText(history.forward());
				String cmdLine = commandText.getText();
				executeCommand(cmdLine);
			}
		};
		ImageDescriptor imgDescFwd = service.getImageDescriptor(IWorkbenchCommandConstants.NAVIGATE_FORWARD);
		forwardAction.setImageDescriptor(imgDescFwd);
		forwardAction.setHoverImageDescriptor(imgDescFwd);
		forwardAction.setDisabledImageDescriptor(imgDescFwd);
		forwardAction.setEnabled(false);
		toolBarManager.add(forwardAction);

		refreshAction = new Action("Refresh from server", service.getImageDescriptor(IWorkbenchCommandConstants.FILE_REFRESH)) {

			@Override
			public void run() {
				String cmdLine = history.current();
				if (cmdLine != null) {
					executeCommand(cmdLine);
				}
			}

		};
		toolBarManager.add(refreshAction);
		sform.updateToolBar();
	}
	
	void executeCommand(String cmdLine) {
		clearStatus();
		
		IOSGiFrameworkConsole console = (IOSGiFrameworkConsole) getServer().getOriginal()
				.loadAdapter(IOSGiFrameworkConsole.class, null);
		
		if (console == null) {
			IStatus status = EditorUIPlugin.newErrorStatus("Console editor part is not integrated with the runtime.");
			EditorUIPlugin.log(status);
			setStatus(status);
			return ;
		}
		
		try {
			String result = console.executeCommand(cmdLine);
			manifestText.append("osgi> " + cmdLine + "\n");
			manifestText.append(result + "\n");
			forwardAction.setEnabled(history.canForward());
			backAction.setEnabled(history.canBack());
			toolBarManager.update(true);
			manifestText.setTopIndex(manifestText.getLineCount() - 1);
		} catch (CoreException e) {
			EditorUIPlugin.log(e);
			setStatus(EditorUIPlugin.newErrorStatus("Failed to execute command. See Error Log for details."));
		}
		
		commandText.setText("");
	}

}
