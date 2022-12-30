/*******************************************************************************
 * Copyright (c) 2009 - 2012 SpringSource, a divison of VMware, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     SpringSource, a division of VMware, Inc. - initial API and implementation
 *******************************************************************************/

package org.eclipse.virgo.ide.ui.wizards;

import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellCloses;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;
import org.eclipse.virgo.ide.ui.editors.BundleManifestEditor;
import org.eclipse.virgo.ide.ui.tests.AbstractManifestUiTestCase;

/**
 * @author Leo Dos Santos
 */
public class NewBundleProjectWizardUiTest extends AbstractManifestUiTestCase {

    private static String PROJECT_NAME = "BundleProject";

    public void testProjectCreation() {
        this.bot.menu("File").menu("New").menu("Project...").click();
        SWTBotShell wizard = this.bot.shell("New Project");
        wizard.activate();
        this.bot.tree().expandNode("Virgo").select("Bundle Project");

        SWTBotButton next = this.bot.button("Next >");
        next.click();
        assertFalse(next.isEnabled());

        this.bot.textWithLabel("Project name:").setText(PROJECT_NAME);
        assertTrue(next.isEnabled());
        next.click();

        SWTBotButton finish = this.bot.button("Finish");
        assertTrue(finish.isEnabled());
        finish.click();
        this.bot.waitUntil(shellCloses(wizard), 15000);

        SWTBotEditor editor = this.bot.editorById(BundleManifestEditor.ID_EDITOR);
        // Occasional failure. Caused by race condition?
        assertEquals(PROJECT_NAME, editor.getTitle());
    }

}
