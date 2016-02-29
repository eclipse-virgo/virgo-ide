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

package org.eclipse.virgo.ide.runtime.internal.ui;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.virgo.ide.runtime.internal.ui.projects.ServerProjectManager;
import org.eclipse.wst.server.core.ServerCore;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 *
 * @author Christian Dupuis
 */
public class ServerUiPlugin extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.eclipse.virgo.ide.runtime.ui";

    private static final String RESOURCE_NAME = "org.eclipse.virgo.ide.runtime.internal.ui.messages";

    public static final String REPOSITORY_PAGE_ID = "org.eclipse.virgo.ide.server.ui.configuration.editor.repository";

    public static final String PREF_DOWNLOAD_MESSAGE_KEY = PLUGIN_ID + "..download.message";

    // The shared instance
    private static ServerUiPlugin plugin;

    /** Resource bundle */
    private ResourceBundle resourceBundle;

    // Naturally, this isn't public in WST..sigh.
    public static final String WST_SERVER_VIEW_ID = "org.eclipse.wst.server.ui.ServersView";

    public static final String WST_EDITOR_ID = "org.eclipse.wst.server.ui.internal.editor.ServerEditor";

    public static final String ARTEFACTS_BROWSER_VIEW_ID = "org.eclipse.virgo.ide.runtime.ui.ArtefactsBrowserView";

    public static final String ARTEFACTS_DETAIL_VIEW_ID = "org.eclipse.virgo.ide.runtime.ui.ArtefactsDetailView";

    public static final String RUNTIME_OUTLINE_VIEW_ID = "org.eclipse.virgo.ide.runtime.ui.OutlineView";

    public static final String PROPERTIES_VIEW_ID = "org.eclipse.virgo.ide.runtime.ui.PropertiesView";

    public static final String PROPERTIES_CONTENT_ID = "org.eclipse.virgo.ide.runtime.ui.properties";

    public static final String FLATTENED_PROPERTIES_CONTENT_ID = "org.eclipse.virgo.ide.runtime.ui.flattenedProperties";

    public static final String LOG_VIEW_ID = "org.eclipse.virgo.ide.runtime.ui.LogView";

    public static final String LOG_CONTENT_ID = "org.eclipse.virgo.ide.runtime.ui.logs";

    public static final String RUNTIME_ARTEFACTS_CONTENT_ID = "org.eclipse.virgo.ide.runtime.ui.runtimeArtefacts";

    public static final String RUNTIME_FLATTENED_ARTEFACTS_CONTENT_ID = "org.eclipse.virgo.ide.runtime.ui.flattenedRuntimeArtefacts";

    /**
     * The constructor
     */
    public ServerUiPlugin() {
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        try {
            this.resourceBundle = ResourceBundle.getBundle(RESOURCE_NAME);
        } catch (MissingResourceException e) {
            this.resourceBundle = null;
        }
        plugin.getPreferenceStore().setDefault(PREF_DOWNLOAD_MESSAGE_KEY, false);

        ServerCore.addRuntimeLifecycleListener(RuntimeListener.getDefault());

        ServerProjectManager.getInstance().updateProjects();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        ServerCore.removeRuntimeLifecycleListener(RuntimeListener.getDefault());
        super.stop(context);
    }

    @Override
    protected void initializeImageRegistry(ImageRegistry reg) {
        ServerUiImages.initializeImageRegistry(reg);
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static ServerUiPlugin getDefault() {
        return plugin;
    }

    public static void log(IStatus status) {
        getDefault().getLog().log(status);
    }

    /**
     * Writes the message to the plug-in's log
     *
     * @param message the text to write to the log
     */
    public static void log(String message, Throwable exception) {
        IStatus status = createErrorStatus(message, exception);
        getDefault().getLog().log(status);
    }

    public static void log(Throwable exception) {
        getDefault().getLog().log(createErrorStatus("Internal Error", exception));
    }

    /**
     * Returns a new <code>IStatus</code> for this plug-in
     */
    public static IStatus createErrorStatus(String message, Throwable exception) {
        if (message == null) {
            message = "";
        }
        return new Status(IStatus.ERROR, PLUGIN_ID, 0, message, exception);
    }

    /**
     * Returns the string from the plugin's resource bundle, or 'key' if not found.
     */
    public static String getResourceString(String key) {
        String bundleString;
        ResourceBundle bundle = getDefault().getResourceBundle();
        if (bundle != null) {
            try {
                bundleString = bundle.getString(key);
            } catch (MissingResourceException e) {
                log(e);
                bundleString = "!" + key + "!";
            }
        } else {
            bundleString = "!" + key + "!";
        }
        return bundleString;
    }

    /**
     * Returns the plugin's resource bundle,
     */
    public final ResourceBundle getResourceBundle() {
        return this.resourceBundle;
    }

}
