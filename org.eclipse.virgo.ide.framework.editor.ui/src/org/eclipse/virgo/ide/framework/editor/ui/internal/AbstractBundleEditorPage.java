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
package org.eclipse.virgo.ide.framework.editor.ui.internal;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerListener;
import org.eclipse.wst.server.core.ServerEvent;
import org.eclipse.wst.server.ui.editor.ServerEditorPart;
import org.eclipse.wst.server.ui.internal.ImageResource;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.view.servers.StartAction;
import org.eclipse.wst.server.ui.internal.view.servers.StopAction;

/**
 * @author Christian Dupuis
 * @author Kaloyan Raev
 */
@SuppressWarnings("restriction")
public abstract class AbstractBundleEditorPage extends ServerEditorPart {

	class PageEnablementServerListener implements IServerListener {

		public void serverChanged(final ServerEvent event) {
			if ((event.getKind() & ServerEvent.SERVER_CHANGE) != 0 && (event.getKind() & ServerEvent.STATE_CHANGE) != 0) {
				getSite().getShell().getDisplay().asyncExec(new Runnable() {

					public void run() {
						if (event.getState() == IServer.STATE_STARTED) {
							enablePage();
						}
						else {
							disablePage();
						}
						sform.getToolBarManager().update(true);
					}
				});
			}
		}
	}

	class StartServerAction extends Action {

		private final String launchMode;

		public StartServerAction(String launchMode) {
			this.launchMode = launchMode;
			if (ILaunchManager.RUN_MODE.equals(launchMode)) {
				setToolTipText(Messages.actionStartToolTip);
				setText(Messages.actionStart);
				setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ELCL_START));
				setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CLCL_START));
				setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DLCL_START));
			}
			else if (ILaunchManager.DEBUG_MODE.equals(launchMode)) {
				setToolTipText(Messages.actionDebugToolTip);
				setText(Messages.actionDebug);
				setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ELCL_START_DEBUG));
				setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CLCL_START_DEBUG));
				setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DLCL_START_DEBUG));
			}
		}

		@Override
		public void run() {
			if (getServer().getOriginal().canStart(launchMode).isOK()) {
				StartAction.start(getServer().getOriginal(), launchMode, getSite().getShell());
			}
		}

	}

	class StopServerAction extends Action {

		public StopServerAction() {
			setToolTipText(Messages.actionStopToolTip);
			setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ELCL_STOP));
			setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CLCL_STOP));
			setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DLCL_STOP));
		}

		@Override
		public void run() {
			if (getServer().getOriginal().canStop().isOK()) {
				StopAction.stop(getServer().getOriginal(), getSite().getShell());
			}
		}

	}

	private StartServerAction debugAction;

	private StartServerAction runAction;

	private IServerListener serverListener;

	private StopServerAction stopAction;

	protected ManagedForm mform;

	protected ScrolledForm sform;

	@Override
	public final void createPartControl(Composite parent) {

		createBundleContent(parent);

		IToolBarManager toolBarManager = sform.getToolBarManager();

		if (toolBarManager.getItems().length > 0) {
			toolBarManager.add(new Separator());
		}

		debugAction = new StartServerAction(ILaunchManager.DEBUG_MODE);
		toolBarManager.add(debugAction);
		runAction = new StartServerAction(ILaunchManager.RUN_MODE);
		toolBarManager.add(runAction);
		stopAction = new StopServerAction();
		toolBarManager.add(stopAction);
		toolBarManager.update(true);

		if (server.getOriginal().getServerState() != IServer.STATE_STARTED) {
			disablePage();
		}
		else {
			enablePage();
		}
	}

	@Override
	public void dispose() {
		getServer().getOriginal().removeServerListener(serverListener);
		super.dispose();
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) {
		super.init(site, input);
		serverListener = new PageEnablementServerListener();
		getServer().getOriginal().addServerListener(serverListener);
	}

	@Override
	public void setFocus() {
		// nothing
	}

	protected abstract void createBundleContent(Composite parent);

	protected void disablePage() {
		setInfoStatus("Server '" + getServer().getName() + "' is not running.");
		setEnabled(sform.getForm().getBody(), false);
		runAction.setEnabled(true);
		debugAction.setEnabled(true);
		stopAction.setEnabled(false);
	}

	protected void enablePage() {
		clearStatus();
		setEnabled(sform.getForm().getBody(), true);
		runAction.setEnabled(false);
		debugAction.setEnabled(false);
		stopAction.setEnabled(true);
	}

	protected void setEnabled(Control control, boolean enabled) {
		control.setEnabled(enabled);
		if (control instanceof Composite) {
			for (Control childControl : ((Composite) control).getChildren()) {
				setEnabled(childControl, enabled);
			}
		}
	}
	
	/**
	 * Sets the status message of the editor. 
	 * 
	 * @param status status message to set. 
	 */
	public void setStatus(IStatus status) {
		sform.getForm().setMessage(status.getMessage(), getMessageType(status));
	}
	
	/**
	 * Sets the status message of the editor with the provide info message. 
	 * 
	 * @param message info message to set. 
	 */
	public void setInfoStatus(String message) {
		sform.getForm().setMessage(message, IMessageProvider.INFORMATION);
	}
	
	/**
	 * Clears the status message of the editor.
	 */
	public void clearStatus() {
		sform.getForm().setMessage(null);
	}
	
	/**
	 * Converts an IStatus message type to Form message type. 
	 */
	private static int getMessageType(IStatus status) {
		switch (status.getSeverity()) {
		case IStatus.ERROR:
			return IMessageProvider.ERROR;
		case IStatus.WARNING:
			return IMessageProvider.WARNING;
		case IStatus.INFO:
			return IMessageProvider.INFORMATION;
		default:
			return IMessageProvider.NONE;
		}
	}

}
