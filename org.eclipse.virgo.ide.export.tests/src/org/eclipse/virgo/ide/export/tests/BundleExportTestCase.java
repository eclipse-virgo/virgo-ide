/*********************************************************************
 * Copyright (c) 2009, 2010 SpringSource, a division of VMware, Inc.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.export.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.jarpackager.IJarExportRunnable;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.virgo.ide.export.BundleExportUtils;
import org.eclipse.virgo.ide.tests.util.VirgoIdeTestCase;
import org.eclipse.virgo.ide.tests.util.VirgoIdeTestUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Terry Hon
 * @author Christian Dupuis
 * @author Steffen Pingel
 */
public class BundleExportTestCase extends VirgoIdeTestCase {

    @Test
    public void testExportOperation() throws InvocationTargetException, InterruptedException, IOException, CoreException {
        final IPath jarLocation = Path.fromOSString(VirgoIdeTestUtil.getWorkspaceRoot().getLocation().toFile().getCanonicalPath()).append(
            "bundlor-test-1.0.0.jar");
        final IJavaProject javaProject = JavaCore.create(createPredefinedProject("bundlor-test"));

        final IStatus[] statusArray = new IStatus[1];

        final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().run(true, true, new IRunnableWithProgress() {

            public void run(IProgressMonitor arg0) throws InvocationTargetException, InterruptedException {
                IJarExportRunnable op = BundleExportUtils.createExportOperation(javaProject, jarLocation, shell, new ArrayList<IStatus>());
                op.run(arg0);
                statusArray[0] = op.getStatus();
            }
        });
        IStatus status = statusArray[0];
        Assert.assertTrue("Expects status is OK", status.isOK());

        File file = new File(jarLocation.toOSString());
        FileInputStream fileStream = new FileInputStream(file);
        ZipInputStream stream = new ZipInputStream(fileStream);

        List<String> fileNames = new ArrayList<String>();
        while (stream.available() > 0) {
            ZipEntry entry = stream.getNextEntry();
            if (entry != null) {
                fileNames.add(entry.getName());
            }
        }

        String[] sortedFileNames = fileNames.toArray(new String[fileNames.size()]);
        Arrays.sort(sortedFileNames);

        Assert.assertTrue("Expects 4 entries", sortedFileNames.length == 4);
        Assert.assertEquals("Expects 1st entry to be META-INF/MANIFEST.MF", "META-INF/MANIFEST.MF", sortedFileNames[0]);
        Assert.assertEquals("Expects 2nd entry to be META-INF/spring/module-context.xml", "META-INF/spring/module-context.xml", sortedFileNames[1]);
        Assert.assertEquals("Expects 3rd entry to be com/springsource/Foo.class", "com/springsource/Foo.class", sortedFileNames[2]);
        Assert.assertEquals("Expects 4th entry to be com/springsource/bar/Bar.class", "com/springsource/bar/Bar.class", sortedFileNames[3]);

        fileStream.close();
        stream.close();
    }

    @Override
    protected String getBundleName() {
        return "org.eclipse.virgo.ide.export.tests";
    }

}
