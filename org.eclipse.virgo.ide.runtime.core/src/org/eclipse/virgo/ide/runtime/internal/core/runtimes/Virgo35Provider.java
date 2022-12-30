/*********************************************************************
 * Copyright (c) 2012 - 2013 Eclipse Foundation
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.runtime.internal.core.runtimes;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.virgo.ide.manifest.core.dependencies.IDependencyLocator;
import org.eclipse.virgo.ide.manifest.core.dependencies.IDependencyLocator.JavaVersion;
import org.eclipse.virgo.ide.runtime.core.IServerBehaviour;
import org.eclipse.virgo.ide.runtime.core.IServerRuntimeProvider;
import org.eclipse.virgo.ide.runtime.core.ServerUtils;
import org.eclipse.virgo.ide.runtime.internal.core.DeploymentIdentity;
import org.eclipse.virgo.ide.runtime.internal.core.command.IServerCommand;
import org.eclipse.virgo.ide.runtime.internal.core.command.JmxServerDeployCommand;
import org.eclipse.virgo.kernel.osgi.provisioning.tools.DependencyLocatorVirgo;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IRuntime;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * {@link IServerRuntimeProvider} for Virgo Server 3.5.x and 3.6.x.
 *
 * @author Borislav Kapukaranov
 * @author Miles Parker
 * @author Leo Dos Santos
 */
public class Virgo35Provider extends VirgoRuntimeProvider {

    // Assumes Stateless
    public static final VirgoRuntimeProvider INSTANCE = new Virgo35Provider();

    protected static final String GEMINI_CONNECTOR_BUNDLE_NAME = "org.eclipse.virgo.ide.management.remote"; //$NON-NLS-1$

    protected static final String GEMINI_CONNECTOR_BUNDLE_NAME_WILDCARD = "org.eclipse.virgo.ide.management.remote*.jar"; //$NON-NLS-1$

    protected Virgo35Provider() {
    }

    @Override
    public boolean isHandlerFor(IRuntime runtime) {
        IPath configPath = runtime.getLocation().append(getConfigurationDir());
        File configDir = configPath.toFile();
        if (!configDir.exists()) {
            return false;
        }
        return new File(configDir, getServerProfileName()).exists();
    }

    /**
     * {@inheritDoc}
     */
    public String getRuntimeClass() {
        return "org.eclipse.equinox.launcher.Main";
    }

    @Override
    public String getConfigurationDir() {
        return "configuration";
    }

    /**
     * @see org.eclipse.virgo.ide.runtime.internal.core.runtimes.VirgoRuntimeProvider#getProfileDir()
     */
    @Override
    String getProfileDir() {
        return getConfigurationDir();
    }

    /**
     * @see org.eclipse.virgo.ide.runtime.core.IServerRuntimeProvider#getRuntimeClasspath(org.eclipse.core.runtime.IPath)
     */
    @Override
    public List<IRuntimeClasspathEntry> getRuntimeClasspath(IPath installPath) {
        List<IRuntimeClasspathEntry> cp = super.getRuntimeClasspath(installPath);

        IPath pluginsPath = installPath.append("plugins");
        if (pluginsPath.toFile().exists()) {
            File pluginsFolder = pluginsPath.toFile();
            for (File library : pluginsFolder.listFiles(new FileFilter() {

                public boolean accept(File pathname) {
                    return pathname.isFile() && pathname.toString().endsWith(".jar") && pathname.toString().contains("org.eclipse.osgi_")
                        && pathname.toString().contains("org.eclipse.equinox.console.supportability_");
                }
            })) {
                IPath path = pluginsPath.append(library.getName());
                cp.add(JavaRuntime.newArchiveRuntimeClasspathEntry(path));
            }
        }

        return cp;
    }

    /**
     * {@inheritDoc}
     */
    public String[] getRuntimeProgramArguments(IServerBehaviour behaviour) {
        List<String> list = new ArrayList<String>();
        list.add("-noExit");
        return list.toArray(new String[list.size()]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getRuntimeVMArguments(IServerBehaviour behaviour, IPath installPath, IPath configPath, IPath deployPath) {
        String[] commonArguments = super.getRuntimeVMArguments(behaviour, installPath, configPath, deployPath);
        String serverHome = ServerUtils.getServer(behaviour).getRuntimeBaseDirectory().toOSString();
        List<String> list = new ArrayList<String>();
        list.addAll(Arrays.asList(commonArguments));

        list.add("-Dorg.eclipse.virgo.kernel.config=" + serverHome + "/" + getConfigurationDir() + "," + serverHome + "/stage"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        list.add("-Dosgi.java.profile=file:" + serverHome + "/" + getConfigurationDir() + "/" + getServerProfileName()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        list.add("-Declipse.ignoreApp=true"); //$NON-NLS-1$
        list.add("-Dosgi.install.area=" + serverHome); //$NON-NLS-1$
        list.add("-Dosgi.configuration.area=" + serverHome + "/work"); //$NON-NLS-1$ //$NON-NLS-2$
        list.add("-Dssh.server.keystore=" + serverHome + "/" + getConfigurationDir() + "/hostkey.ser"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
        list.add("-Djava.endorsed.dirs=\"" + serverHome + "/lib/endorsed\""); //$NON-NLS-1$//$NON-NLS-2$

        if (isWindows10()) {
            // Work around for Equinox 3.9 not recognizing Windows 10 as win32, which results in failure of resolution
            // of native libraries see https://bugs.eclipse.org/bugs/show_bug.cgi?id=486353
            list.add("-Dos.name=\"Windows 8\""); //$NON-NLS-1$
        }

        String fwClassPath = createFWClassPath(serverHome);

        list.add("-Dosgi.frameworkClassPath=" + fwClassPath); //$NON-NLS-1$

        return list.toArray(new String[list.size()]);
    }

    private boolean isWindows10() {
        return System.getProperty("os.name", "").toLowerCase().contains("windows") && System.getProperty("os.version", "").startsWith("1"); //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
    }

    private String createFWClassPath(String serverHome) {
        StringBuilder fwClassPath = new StringBuilder();
        File libDir = new File(serverHome + "/lib");
        if (libDir.exists()) {
            for (File file : libDir.listFiles()) {
                if (file.getName().endsWith(".jar")) {
                    fwClassPath.append("file:" + file.getAbsolutePath() + ",");
                }
            }
        }
        File plugins = new File(serverHome + "/plugins");
        if (plugins.exists()) {
            for (File file : plugins.listFiles()) {
                if (file.getName().contains("org.eclipse.osgi_")) {
                    fwClassPath.append("file:" + file.getAbsolutePath() + ",");
                }
                if (file.getName().contains("org.eclipse.equinox.console.supportability_")) {
                    fwClassPath.append("file:" + file.getAbsolutePath() + ",");
                }
            }
        }
        fwClassPath.deleteCharAt(fwClassPath.length() - 1);
        return fwClassPath.toString();
    }

    @Override
    public String getSupportedVersions() {
        return "3.5+"; //$NON-NLS-1$
    }

    /**
     * @see org.eclipse.virgo.ide.runtime.core.IServerRuntimeProvider#createDependencyLocator(org.eclipse.wst.server.core.IRuntime,
     *      java.lang.String, java.lang.String[], java.lang.String,
     *      org.eclipse.virgo.ide.manifest.core.dependencies.IDependencyLocator.JavaVersion)
     */
    public IDependencyLocator createDependencyLocator(IRuntime runtime, String serverHomePath, String[] additionalSearchPaths,
        String indexDirectoryPath, JavaVersion javaVersion) throws IOException {
        return new DependencyLocatorVirgo(serverHomePath, additionalSearchPaths, indexDirectoryPath, javaVersion);
    }

    /**
     * @see org.eclipse.virgo.ide.runtime.core.IServerRuntimeProvider#getConnectorBundleUri()
     */
    public URI getConnectorBundleUri() {
        Bundle bundle = Arrays.asList(FrameworkUtil.getBundle(getClass()).getBundleContext().getBundles()).stream().filter(
            b -> b.getSymbolicName().equals(GEMINI_CONNECTOR_BUNDLE_NAME)).collect(Collectors.toUnmodifiableList()).iterator().next();
        if (bundle == null) {
            return null;
        }
        Optional<File> file = org.eclipse.core.runtime.FileLocator.getBundleFileLocation(bundle);
        if (file.isPresent()) {
            URI u = checkIfDevelopmentMode(bundle, file);
            if (u == null) {
                // IDE Tools properly deployed as binary in Eclipse
                u = file.get().toURI();
            }
            return u;
        }
        return null;
    }

    /*
     * If Eclipse is running in dev mode, the IDE Tools bundles are likely being loaded from an Eclipse Workspace. In
     * this case they are PDE projects and not Jars, so we try to deploy the binary that may exist in the Maven target
     * folder if the developer has previously performed a maven build. If this is not the case we show a pop-up to
     * inform the committer. The end user will never see this because this code is executed if and only if Eclipse is in
     * dev mode AND the plug-in is a directory, not a Jar.
     */
    private URI checkIfDevelopmentMode(Bundle bundle, Optional<File> file) {
        if (Platform.inDevelopmentMode() && file.get().isDirectory()) {
            // Bundle is a source plugin within the Eclipse workspace and Virgo Tools are
            // running in a runtime workbench. In this case we assume developer performed
            // "mvn package" and binary exists in target folder
            Enumeration<URL> entries = bundle.findEntries("/", GEMINI_CONNECTOR_BUNDLE_NAME_WILDCARD, true); //$NON-NLS-1$
            if (entries != null && entries.hasMoreElements()) {
                try {
                    URL u = FileLocator.resolve(entries.nextElement());
                    return u.toURI();
                } catch (IOException e) {
                } catch (URISyntaxException e) {
                }
            } else {
                new UIJob(PlatformUI.getWorkbench().getDisplay(), "committer-warning") { //$NON-NLS-1$

                    @Override
                    public IStatus runInUIThread(IProgressMonitor monitor) {
                        MessageDialog.openInformation(null, "Warning", //$NON-NLS-1$
                            "In dev mode you need to run 'mvn clean package' in the *.management.remote project to ensure the target binary is produced before trying this functionality"); //$NON-NLS-1$
                        return Status.OK_STATUS;
                    }
                }.schedule();
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IServerCommand<DeploymentIdentity> getServerDeployCommand(IServerBehaviour IServerBehaviour, IModule module) {
        return new JmxServerDeployCommand(IServerBehaviour, module, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IServerCommand<DeploymentIdentity> getServerRedeployCommand(IServerBehaviour IServerBehaviour, IModule module) {
        return new JmxServerDeployCommand(IServerBehaviour, module, false);
    }
}
