/*********************************************************************
 * Copyright (c) 2007, 2009 2010 SpringSource
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.tests.util;

import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.junit.After;

import junit.framework.TestCase;

/**
 * Derived from AbstractBeansCoreTestCase
 *
 * @author Christian Dupuis
 * @author Leo Dos Santos
 * @author Terry Hon
 */
public abstract class VirgoIdeTestCase extends TestCase {

    protected IProject createPredefinedProject(final String projectName) throws CoreException, IOException {
        return VirgoIdeTestUtil.createPredefinedProject(projectName, getBundleName());
    }

    protected IResource createPredefinedProjectAndGetResource(String projectName, String resourcePath) throws CoreException, IOException {
        IProject project = createPredefinedProject(projectName);
        // XXX do a second full build to ensure markers are up-to-date
        project.build(IncrementalProjectBuilder.FULL_BUILD, null);

        IResource resource = project.findMember(resourcePath);
        VirgoIdeTestUtil.waitForResource(resource);
        return resource;
    }

    protected abstract String getBundleName();

    protected String getSourceWorkspacePath() {
        return VirgoIdeTestUtil.getSourceWorkspacePath(getBundleName());
    }

    @After
    @Override
    public void tearDown() throws Exception {
        VirgoIdeTestUtil.cleanUpProjects();
        super.tearDown();
    }

}
