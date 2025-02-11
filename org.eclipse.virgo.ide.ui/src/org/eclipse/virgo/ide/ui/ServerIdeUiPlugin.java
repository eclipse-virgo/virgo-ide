/*********************************************************************
 * Copyright (c) 2009, 2012 SpringSource, a division of VMware, Inc.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.ui;

import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.pde.internal.ui.PDEPlugin;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.virgo.ide.ui.editors.text.BundleColorManager;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 *
 * @author Christian Dupuis
 */
public class ServerIdeUiPlugin extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.eclipse.virgo.ide.ui";

    // The shared instance
    private static ServerIdeUiPlugin plugin;

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static ServerIdeUiPlugin getDefault() {
        return plugin;
    }

    private FormColors formColors;

    /**
     * The constructor
     */
    public ServerIdeUiPlugin() {
    }

    public void log(Exception e) {
        getLog().log(new Status(IStatus.ERROR, ServerIdeUiPlugin.PLUGIN_ID, "Unexpected error", e));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext )
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        BundleColorManager.initializeDefaults(PDEPlugin.getDefault().getPreferenceStore());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext )
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        for (Image i : cache.values()) {
            if (!i.isDisposed()) {
                i.dispose();
            }
        }
        super.stop(context);
    }

    private final static ConcurrentHashMap<ImageDescriptor, Image> cache = new ConcurrentHashMap<>(89);

    public static Image getPDEImage(ImageDescriptor descriptor) {
        return cache.computeIfAbsent(descriptor, (f) -> f.createImage(true));
    }

    public static Image getImage(String path) {
        ImageRegistry imageRegistry = getDefault().getImageRegistry();
        Image image = imageRegistry.get(path);
        if (image == null) {
            ImageDescriptor imageDescriptor = getImageDescriptor(path);
            if (imageDescriptor == null) {
                imageDescriptor = ImageDescriptor.getMissingImageDescriptor();
            }
            image = imageDescriptor.createImage(true);
            imageRegistry.put(path, image);
        }
        return image;
    }

    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, "icons/" + path);
    }

    public FormColors getFormColors(Display display) {
        if (this.formColors == null) {
            this.formColors = new FormColors(display);
            this.formColors.markShared();
        }
        return this.formColors;
    }

}
