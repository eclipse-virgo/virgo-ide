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

package org.eclipse.virgo.ide.ui.editors.text;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.pde.internal.ui.PDEPlugin;
import org.eclipse.pde.internal.ui.editor.text.IColorManager;
import org.eclipse.pde.internal.ui.editor.text.IPDEColorConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * @author Christian Dupuis
 */
public class BundleColorManager implements IColorManager, IPDEColorConstants, ISpringColorConstants {

    private static BundleColorManager fColorManager;

    private final Map fColorTable = new HashMap(5);

    private static int counter = 0;

    public BundleColorManager() {
        initialize();
    }

    public static IColorManager getDefault() {
        if (fColorManager == null) {
            fColorManager = new BundleColorManager();
        }

        counter += 1;
        return fColorManager;
    }

    public static void initializeDefaults(IPreferenceStore store) {
        PreferenceConverter.setDefault(store, P_DEFAULT, DEFAULT);
        PreferenceConverter.setDefault(store, P_PROC_INSTR, PROC_INSTR);
        PreferenceConverter.setDefault(store, P_STRING, STRING);
        PreferenceConverter.setDefault(store, P_TAG, TAG);
        PreferenceConverter.setDefault(store, P_XML_COMMENT, XML_COMMENT);
        PreferenceConverter.setDefault(store, P_HEADER_KEY, HEADER_KEY);
        PreferenceConverter.setDefault(store, P_HEADER_OSGI, HEADER_OSGI);
        store.setDefault(P_HEADER_OSGI + IPDEColorConstants.P_BOLD_SUFFIX, true);
        PreferenceConverter.setDefault(store, P_HEADER_VALUE, HEADER_VALUE);
        PreferenceConverter.setDefault(store, P_HEADER_ATTRIBUTES, HEADER_ATTRIBUTES);
        store.setDefault(P_HEADER_ATTRIBUTES + IPDEColorConstants.P_ITALIC_SUFFIX, true);
        PreferenceConverter.setDefault(store, P_HEADER_ASSIGNMENT, HEADER_ASSIGNMENT);
        PreferenceConverter.setDefault(store, P_HEADER_SPRING, HEADER_SPRING);
        store.setDefault(P_HEADER_SPRING + IPDEColorConstants.P_BOLD_SUFFIX, true);
    }

    private void initialize() {
        IPreferenceStore pstore = PDEPlugin.getDefault().getPreferenceStore();
        putColor(pstore, P_DEFAULT);
        putColor(pstore, P_PROC_INSTR);
        putColor(pstore, P_STRING);
        putColor(pstore, P_TAG);
        putColor(pstore, P_XML_COMMENT);
        putColor(pstore, P_HEADER_KEY);
        putColor(pstore, P_HEADER_OSGI);
        putColor(pstore, P_HEADER_VALUE);
        putColor(pstore, P_HEADER_ATTRIBUTES);
        putColor(pstore, P_HEADER_ASSIGNMENT);
        putColor(pstore, P_HEADER_SPRING);
        pstore = JavaPlugin.getDefault().getCombinedPreferenceStore();
        for (String element : IColorManager.PROPERTIES_COLORS) {
            putColor(pstore, element);
        }
    }

    public void disposeColors(boolean resetSingleton) {
        Iterator e = this.fColorTable.values().iterator();
        while (e.hasNext()) {
            ((Color) e.next()).dispose();
        }
        if (resetSingleton) {
            fColorManager = null;
        }

    }

    public void dispose() {
        counter--;
        if (counter == 0) {
            disposeColors(true);
        }
    }

    private void putColor(IPreferenceStore pstore, String property) {
        putColor(property, PreferenceConverter.getColor(pstore, property));
    }

    private void putColor(String property, RGB setting) {
        Color oldColor = (Color) this.fColorTable.get(property);
        if (oldColor != null) {
            if (oldColor.getRGB().equals(setting)) {
                return;
            }
            oldColor.dispose();
        }
        this.fColorTable.put(property, new Color(Display.getCurrent(), setting));
    }

    public Color getColor(String key) {
        Color color = (Color) this.fColorTable.get(key);
        if (color == null) {
            color = Display.getCurrent().getSystemColor(SWT.COLOR_LIST_FOREGROUND);
        }
        return color;
    }

    public void handlePropertyChangeEvent(PropertyChangeEvent event) {
        Object color = event.getNewValue();
        if (color instanceof RGB) {
            putColor(event.getProperty(), (RGB) color);
        } else {
            putColor(event.getProperty(), StringConverter.asRGB(color.toString()));
        }
    }

}
