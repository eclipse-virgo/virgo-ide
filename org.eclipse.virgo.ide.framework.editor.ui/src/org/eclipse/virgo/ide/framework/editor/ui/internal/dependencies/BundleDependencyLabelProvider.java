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
package org.eclipse.virgo.ide.framework.editor.ui.internal.dependencies;

import org.eclipse.draw2d.IFigure;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.virgo.ide.framework.editor.core.model.IBundle;
import org.eclipse.pde.internal.ui.PDEPluginImages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.zest.core.viewers.IConnectionStyleProvider;
import org.eclipse.zest.core.viewers.IEntityStyleProvider;
import org.eclipse.zest.core.widgets.ZestStyles;

/**
 * @author Christian Dupuis
 * @author Kaloyan Raev
 */
public class BundleDependencyLabelProvider implements ILabelProvider, IEntityStyleProvider, IConnectionStyleProvider,
		ISelectionChangedListener {

	private final Color blackColor = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);

	private final Color colorBorder = new Color(null, 0, 0, 0);

	private final Color colorRel = new Color(null, 150, 150, 255);

	private final Color colorRelated = new Color(null, 236, 255, 122);

	private final Color colorRelResolved = new Color(null, 255, 100, 100);

	private final Color colorTestBackground = new Color(null, 255, 255, 255);

	private final Color colorTestHighlight = new Color(null, 182, 217, 0);

	private final BundleDependencyContentProvider contentProvider;

	private final Color grayColor = Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);

	/**
	 * @param formToolkit  
	 */
	public BundleDependencyLabelProvider(BundleDependencyContentProvider contentProvider, FormToolkit formToolkit) {
		this.contentProvider = contentProvider;
	}

	public void addListener(ILabelProviderListener listener) {
		// nothing
	}

	public void dispose() {
		colorTestBackground.dispose();
		colorTestHighlight.dispose();
		colorRel.dispose();
		colorRelResolved.dispose();
		colorBorder.dispose();
		colorRelated.dispose();
	}

	public boolean fisheyeNode(Object entity) {
		return false;
	}

	public Color getBackgroundColour(Object entity) {
		if (entity instanceof IBundle) {
			if (contentProvider.isSelected((IBundle) entity)) {
				return colorRelated;
			}
		}
		return colorTestBackground;
	}

	public Color getBorderColor(Object entity) {
		return colorBorder;
	}

	public Color getBorderHighlightColor(Object entity) {
		return colorBorder;
	}

	public int getBorderWidth(Object entity) {
		return 1;
	}

	public Color getColor(Object rel) {
		if (rel instanceof BundleDependency) {
			if (contentProvider.isSelected((BundleDependency) rel)) {
				return blackColor;
			}
			return grayColor;
		}
		return colorRel;
	}

	public int getConnectionStyle(Object rel) {
		return ZestStyles.CONNECTIONS_DASH;
	}

	public Color getForegroundColour(Object entity) {
		return colorBorder;
	}

	public Color getHighlightColor(Object rel) {
		if (rel instanceof BundleDependency) {
			return blackColor;
		}
		return colorTestHighlight;
	}

	public Image getImage(Object element) {
		if (element instanceof IBundle) {
			return PDEPluginImages.get(PDEPluginImages.OBJ_DESC_BUNDLE);
		}
		return null;
	}

	public int getLineWidth(Object rel) {
		return 1;
	}

	public Color getNodeHighlightColor(Object entity) {
		return colorTestHighlight;
	}

	public String getText(Object element) {
		if (element instanceof IBundle) {
			return ((IBundle) element).getSymbolicName() + " (" + ((IBundle) element).getVersion() + ")";
		}
		return null;
	}

	public IFigure getTooltip(Object entity) {
		return null;
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
		// nothing
	}

	public void selectionChanged(SelectionChangedEvent event) {
		// nothing
	}

}
