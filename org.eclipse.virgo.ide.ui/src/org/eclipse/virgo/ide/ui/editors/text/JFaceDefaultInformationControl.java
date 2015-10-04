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

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.internal.text.html.HTMLTextPresenter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.DefaultInformationControl.IInformationPresenter;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.util.Geometry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Drawable;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Default implementation of {@link org.eclipse.jface.text.IInformationControl}.
 * <p>
 * Displays textual information in a {@link org.eclipse.swt.custom.StyledText}widget. Before displaying, the information
 * set to this information control is processed by an <code>IInformationPresenter</code>.
 *
 * @author Christian Dupuis
 * @since 2.0
 */
public class JFaceDefaultInformationControl extends JFaceAbstractInformationControl implements DisposeListener {

    /**
     * An information presenter determines the style presentation of information displayed in the default information
     * control. The interface can be implemented by clients.
     *
     * @since 3.2
     */
    public interface IInformationPresenterExtension {

        /**
         * Updates the given presentation of the given information and thereby may manipulate the information to be
         * displayed. The manipulation could be the extraction of textual encoded style information etc. Returns the
         * manipulated information.
         * <p>
         * Replaces
         * {@link DefaultInformationControl.IInformationPresenter#updatePresentation(Display, String, TextPresentation, int, int)}
         * Implementations should use the font of the given <code>drawable</code> to calculate the size of the text to
         * be presented.
         * </p>
         *
         * @param drawable the drawable of the information control
         * @param hoverInfo the information to be presented
         * @param presentation the presentation to be updated
         * @param maxWidth the maximal width in pixels
         * @param maxHeight the maximal height in pixels
         * @return the manipulated information
         */
        String updatePresentation(Drawable drawable, String hoverInfo, TextPresentation presentation, int maxWidth, int maxHeight);
    }

    /**
     * Inner border thickness in pixels.
     *
     * @since 3.1
     */
    private static final int INNER_BORDER = 1;

    /** The control's text widget */
    private StyledText fText;

    /** The information presenter, or <code>null</code> if none. */
    private final IInformationPresenter fPresenter;

    /** A cached text presentation */
    private final TextPresentation fPresentation = new TextPresentation();

    /**
     * Additional styles to use for the text control.
     *
     * @since 3.4, previously called <code>fTextStyle</code>
     */
    private final int fAdditionalTextStyles;

    /**
     * Creates a default information control with the given shell as parent. An information presenter that can handle
     * simple HTML is used to process the information to be displayed.
     *
     * @param parent the parent shell
     * @param isResizeable <code>true</code> if the control should be resizable
     * @since 3.4
     */
    public JFaceDefaultInformationControl(Shell parent, boolean isResizeable) {
        super(parent, isResizeable);
        this.fAdditionalTextStyles = isResizeable ? SWT.V_SCROLL | SWT.H_SCROLL : SWT.NONE;
        this.fPresenter = new HTMLTextPresenter(!isResizeable);
        create();
    }

    /**
     * Creates a default information control with the given shell as parent. An information presenter that can handle
     * simple HTML is used to process the information to be displayed.
     *
     * @param parent the parent shell
     * @param statusFieldText the text to be used in the status field or <code>null</code> to hide the status field
     * @since 3.4
     */
    public JFaceDefaultInformationControl(Shell parent, String statusFieldText) {
        this(parent, statusFieldText, new HTMLTextPresenter(true));
    }

    /**
     * Creates a default information control with the given shell as parent. The given information presenter is used to
     * process the information to be displayed.
     *
     * @param parent the parent shell
     * @param statusFieldText the text to be used in the status field or <code>null</code> to hide the status field
     * @param presenter the presenter to be used, or <code>null</code> if no presenter should be used
     * @since 3.4
     */
    public JFaceDefaultInformationControl(Shell parent, String statusFieldText, IInformationPresenter presenter) {
        super(parent, statusFieldText);
        this.fAdditionalTextStyles = SWT.NONE;
        this.fPresenter = presenter;
        create();
    }

    /**
     * Creates a resizable default information control with the given shell as parent. An information presenter that can
     * handle simple HTML is used to process the information to be displayed.
     *
     * @param parent the parent shell
     * @param toolBarManager the manager or <code>null</code> if toolbar is not desired
     * @since 3.4
     */
    public JFaceDefaultInformationControl(Shell parent, ToolBarManager toolBarManager) {
        this(parent, toolBarManager, new HTMLTextPresenter(false));
    }

    /**
     * Creates a resizable default information control with the given shell as parent. The given information presenter
     * is used to process the information to be displayed.
     *
     * @param parent the parent shell
     * @param toolBarManager the manager or <code>null</code> if toolbar is not desired
     * @param presenter the presenter to be used, or <code>null</code> if no presenter should be used
     * @since 3.4
     */
    public JFaceDefaultInformationControl(Shell parent, ToolBarManager toolBarManager, IInformationPresenter presenter) {
        super(parent, toolBarManager);
        this.fAdditionalTextStyles = SWT.V_SCROLL | SWT.H_SCROLL;
        this.fPresenter = presenter;
        create();
    }

    /**
     * Creates a default information control with the given shell as parent. No information presenter is used to process
     * the information to be displayed.
     *
     * @param parent the parent shell
     */
    public JFaceDefaultInformationControl(Shell parent) {
        this(parent, (String) null, null);
    }

    /**
     * Creates a default information control with the given shell as parent. The given information presenter is used to
     * process the information to be displayed.
     *
     * @param parent the parent shell
     * @param presenter the presenter to be used
     */
    public JFaceDefaultInformationControl(Shell parent, IInformationPresenter presenter) {
        this(parent, (String) null, presenter);
    }

    /**
     * Creates a default information control with the given shell as parent. The given information presenter is used to
     * process the information to be displayed. The given styles are applied to the created styled text widget.
     *
     * @param parent the parent shell
     * @param shellStyle the additional styles for the shell
     * @param style the additional styles for the styled text widget
     * @param presenter the presenter to be used
     * @deprecated As of 3.4, replaced by simpler constructors
     */
    @Deprecated
    public JFaceDefaultInformationControl(Shell parent, int shellStyle, int style, IInformationPresenter presenter) {
        this(parent, shellStyle, style, presenter, null);
    }

    /**
     * Creates a default information control with the given shell as parent. The given information presenter is used to
     * process the information to be displayed. The given styles are applied to the created styled text widget.
     *
     * @param parentShell the parent shell
     * @param shellStyle the additional styles for the shell
     * @param style the additional styles for the styled text widget
     * @param presenter the presenter to be used
     * @param statusFieldText the text to be used in the status field or <code>null</code> to hide the status field
     * @since 3.0
     * @deprecated As of 3.4, replaced by simpler constructors
     */
    @Deprecated
    public JFaceDefaultInformationControl(Shell parentShell, int shellStyle, final int style, IInformationPresenter presenter,
        String statusFieldText) {
        super(parentShell, SWT.NO_FOCUS | SWT.ON_TOP | shellStyle, statusFieldText, null);
        this.fAdditionalTextStyles = style;
        this.fPresenter = presenter;
        create();
    }

    /**
     * Creates a default information control with the given shell as parent. The given information presenter is used to
     * process the information to be displayed.
     *
     * @param parent the parent shell
     * @param textStyles the additional styles for the styled text widget
     * @param presenter the presenter to be used
     * @deprecated As of 3.4, replaced by
     *             {@link #DefaultInformationControl(Shell, DefaultInformationControl.IInformationPresenter)}
     */
    @Deprecated
    public JFaceDefaultInformationControl(Shell parent, int textStyles, IInformationPresenter presenter) {
        this(parent, textStyles, presenter, null);
    }

    /**
     * Creates a default information control with the given shell as parent. The given information presenter is used to
     * process the information to be displayed.
     *
     * @param parent the parent shell
     * @param textStyles the additional styles for the styled text widget
     * @param presenter the presenter to be used
     * @param statusFieldText the text to be used in the status field or <code>null</code> to hide the status field
     * @since 3.0
     * @deprecated As of 3.4, replaced by
     *             {@link #DefaultInformationControl(Shell, String, DefaultInformationControl.IInformationPresenter)}
     */
    @Deprecated
    public JFaceDefaultInformationControl(Shell parent, int textStyles, IInformationPresenter presenter, String statusFieldText) {
        super(parent, statusFieldText);
        this.fAdditionalTextStyles = textStyles;
        this.fPresenter = presenter;
        create();
    }

    /*
     * @see org.eclipse.jface.text.AbstractInformationControl#createContent(org.eclipse .swt.widgets.Composite)
     */
    @Override
    protected void createContent(Composite parent) {
        this.fText = new StyledText(parent, SWT.MULTI | SWT.READ_ONLY | this.fAdditionalTextStyles);
        this.fText.setForeground(parent.getForeground());
        this.fText.setBackground(parent.getBackground());
        this.fText.setFont(JFaceResources.getDialogFont());
        FillLayout layout = (FillLayout) parent.getLayout();
        if (this.fText.getWordWrap()) {
            // indent does not work for wrapping StyledText, see
            // https://bugs.eclipse.org/bugs/show_bug.cgi?id=56342 and
            // https://bugs.eclipse.org/bugs/show_bug.cgi?id=115432
            layout.marginHeight = INNER_BORDER;
            layout.marginWidth = INNER_BORDER;
        } else {
            this.fText.setIndent(INNER_BORDER);
        }
    }

    /*
     * @see IInformationControl#setInformation(String)
     */
    @Override
    public void setInformation(String content) {
        if (this.fPresenter == null) {
            this.fText.setText(content);
        } else {
            this.fPresentation.clear();

            int maxWidth = -1;
            int maxHeight = -1;
            Point constraints = getSizeConstraints();
            if (constraints != null) {
                maxWidth = constraints.x;
                maxHeight = constraints.y;
                if (this.fText.getWordWrap()) {
                    maxWidth -= INNER_BORDER * 2;
                    maxHeight -= INNER_BORDER * 2;
                } else {
                    maxWidth -= INNER_BORDER; // indent
                }
                Rectangle trim = computeTrim();
                maxWidth -= trim.width;
                maxHeight -= trim.height;
                maxWidth -= this.fText.getCaret().getSize().x; // StyledText adds a
                // border at the end
                // of the line for
                // the caret.
            }
            if (isResizable()) {
                maxHeight = Integer.MAX_VALUE;
            }

            if (this.fPresenter instanceof IInformationPresenterExtension) {
                content = ((IInformationPresenterExtension) this.fPresenter).updatePresentation(this.fText, content, this.fPresentation, maxWidth,
                    maxHeight);
            } else {
                content = this.fPresenter.updatePresentation(getShell().getDisplay(), content, this.fPresentation, maxWidth, maxHeight);
            }

            if (content != null) {
                this.fText.setText(content);
                TextPresentation.applyTextPresentation(this.fPresentation, this.fText);
            } else {
                this.fText.setText(""); //$NON-NLS-1$
            }
        }
    }

    /*
     * @see IInformationControl#setVisible(boolean)
     */
    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            if (this.fText.getWordWrap()) {
                Point currentSize = getShell().getSize();
                getShell().pack(true);
                Point newSize = getShell().getSize();
                if (newSize.x > currentSize.x || newSize.y > currentSize.y) {
                    setSize(currentSize.x, currentSize.y); // restore previous
                    // size
                }
            }
        }

        super.setVisible(visible);
    }

    /*
     * @see IInformationControl#computeSizeHint()
     */
    @Override
    public Point computeSizeHint() {
        // see: https://bugs.eclipse.org/bugs/show_bug.cgi?id=117602
        int widthHint = SWT.DEFAULT;
        Point constraints = getSizeConstraints();
        if (constraints != null && this.fText.getWordWrap()) {
            widthHint = constraints.x;
        }

        return getShell().computeSize(widthHint, SWT.DEFAULT, true);
    }

    /*
     * @see org.eclipse.jface.text.AbstractInformationControl#computeTrim()
     */
    @Override
    public Rectangle computeTrim() {
        return Geometry.add(super.computeTrim(), this.fText.computeTrim(0, 0, 0, 0));
    }

    /*
     * @see IInformationControl#setForegroundColor(Color)
     */
    @Override
    public void setForegroundColor(Color foreground) {
        super.setForegroundColor(foreground);
        this.fText.setForeground(foreground);
    }

    /*
     * @see IInformationControl#setBackgroundColor(Color)
     */
    @Override
    public void setBackgroundColor(Color background) {
        super.setBackgroundColor(background);
        this.fText.setBackground(background);
    }

    /*
     * @see IInformationControlExtension#hasContents()
     */
    public boolean hasContents() {
        return this.fText.getCharCount() > 0;
    }

    /**
     * @see org.eclipse.swt.events.DisposeListener#widgetDisposed(org.eclipse.swt.events.DisposeEvent)
     * @since 3.0
     * @deprecated As of 3.2, no longer used and called
     */
    @Deprecated
    public void widgetDisposed(DisposeEvent event) {
    }

    /*
     * @seeorg.eclipse.jface.text.IInformationControlExtension5# getInformationPresenterControlCreator()
     *
     * @since 3.4
     */
    @Override
    public IInformationControlCreator getInformationPresenterControlCreator() {
        return new IInformationControlCreator() {

            /*
             * @seeorg.eclipse.jface.text.IInformationControlCreator#
             * createInformationControl(org.eclipse.swt.widgets.Shell)
             */
            public IInformationControl createInformationControl(Shell parent) {
                return new JFaceDefaultInformationControl(parent, (ToolBarManager) null, JFaceDefaultInformationControl.this.fPresenter);
            }
        };
    }

}
