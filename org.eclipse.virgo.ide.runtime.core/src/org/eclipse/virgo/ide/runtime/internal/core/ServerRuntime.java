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
package org.eclipse.virgo.ide.runtime.internal.core;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.virgo.ide.runtime.core.IServerRuntime;
import org.eclipse.virgo.ide.runtime.core.IServerRuntimeWorkingCopy;
import org.eclipse.virgo.ide.runtime.core.IServerVersionHandler;
import org.eclipse.virgo.ide.runtime.core.ServerCorePlugin;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.model.RuntimeDelegate;


/**
 * Default dm server runtime implementation.
 * @author Christian Dupuis
 * @since 1.0.0
 */
public class ServerRuntime extends RuntimeDelegate implements IServerRuntime,
		IServerRuntimeWorkingCopy {
	
	public List<IRuntimeClasspathEntry> getRuntimeClasspath() {
		return getVersionHandler().getRuntimeClasspath(getRuntime().getLocation());
	}
	
	public String getRuntimeClass() {
		return getVersionHandler().getRuntimeClass();
	}

	public IServerVersionHandler getVersionHandler() {
		return ServerVersionAdapter.getVersionHandler(getRuntime().getRuntimeType()
				.getId());
	}

	public IVMInstall getVMInstall() {
		if (getVMInstallTypeId() == null) {
			return JavaRuntime.getDefaultVMInstall();
		}
		try {
			IVMInstall[] vmInstalls = JavaRuntime.getVMInstallType(getVMInstallTypeId())
					.getVMInstalls();
			String id = getVMInstallId();
			for (IVMInstall vmInstall : vmInstalls) {
				if (id.equals(vmInstall.getId())) {
					return vmInstall;
				}
			}
		}
		catch (Exception e) {
		}
		return null;
	}

	public boolean isUsingDefaultJRE() {
		return getVMInstallTypeId() == null;
	}

	@Override
	public void setDefaults(IProgressMonitor monitor) {
		IRuntimeType type = getRuntimeWorkingCopy().getRuntimeType();
		getRuntimeWorkingCopy().setLocation(
				new Path(ServerCorePlugin.getPreference("location" + type.getId())));
	}

	public void setVMInstall(IVMInstall vmInstall) {
		if (vmInstall == null) {
			setVMInstall(null, null);
		}
		else {
			setVMInstall(vmInstall.getVMInstallType().getId(), vmInstall.getId());
		}
	}

	@Override
	public IStatus validate() {
		IStatus status = super.validate();
		if (!status.isOK()) {
			return status;
		}

		status = verifyLocation();
		if (!status.isOK()) {
			return status;
		}

		return Status.OK_STATUS;
	}

	public IStatus verifyLocation() {
		return getVersionHandler().verifyInstallation(getRuntime().getLocation());
	}

	protected String getVMInstallId() {
		return getAttribute(PROPERTY_VM_INSTALL_ID, (String) null);
	}

	protected String getVMInstallTypeId() {
		return getAttribute(PROPERTY_VM_INSTALL_TYPE_ID, (String) null);
	}

	protected void setVMInstall(String typeId, String id) {
		if (typeId == null) {
			setAttribute(PROPERTY_VM_INSTALL_TYPE_ID, (String) null);
		}
		else {
			setAttribute(PROPERTY_VM_INSTALL_TYPE_ID, typeId);
		}

		if (id == null) {
			setAttribute(PROPERTY_VM_INSTALL_ID, (String) null);
		}
		else {
			setAttribute(PROPERTY_VM_INSTALL_ID, id);
		}
	}
	
	public String getUserLevelBundleRepositoryPath() {
		return getVersionHandler().getUserLevelBundleRepositoryPath(getRuntime());
	}

	public String getUserLevelLibraryRepositoryPath() {
		return getVersionHandler().getUserLevelLibraryRepositoryPath(getRuntime());
	}
	
	public String getProfilePath() {
		return getVersionHandler().getProfilePath(getRuntime());
	}
	
	public String getConfigPath() {
		return getVersionHandler().getConfigPath(getRuntime());
	}

}
