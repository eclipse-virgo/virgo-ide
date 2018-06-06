/*********************************************************************
 * Copyright (c) 2009, 2012 SpringSource, a division of VMware, Inc.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.ui.editors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.virgo.ide.runtime.core.artefacts.Artefact;

/**
 * @author Christian Dupuis
 */
public class ManifestEditorUtils {

    /**
     * Returns a collection of downloadable artifacts where only the latest versions are included.
     */
    public static void removeOldVersions(Set<Artefact> bundles) {
        Map<String, Artefact> symbolicNameToHighestVersionMap = new HashMap<String, Artefact>(bundles.size());

        Set<Artefact> oldArtifacts = new HashSet<Artefact>();

        for (Artefact currArtifactDefinition : bundles) {
            Artefact mappedArtifact = symbolicNameToHighestVersionMap.get(currArtifactDefinition.getSymbolicName());
            if (mappedArtifact == null) {
                symbolicNameToHighestVersionMap.put(currArtifactDefinition.getSymbolicName(), currArtifactDefinition);
            } else {
                if (currArtifactDefinition.getVersion().compareTo(mappedArtifact.getVersion()) <= 0) {
                    oldArtifacts.add(currArtifactDefinition);
                } else {
                    oldArtifacts.add(mappedArtifact);
                    symbolicNameToHighestVersionMap.put(currArtifactDefinition.getSymbolicName(), currArtifactDefinition);
                }
            }
        }

        bundles.removeAll(oldArtifacts);
    }

    /**
     * Returns true if the array contains a marker where the severity is ERROR.
     */
    public static boolean hasErrorSeverityMarker(IMarker[] markers) throws CoreException {

        for (IMarker currMarker : markers) {
            Integer severity = (Integer) currMarker.getAttribute("severity");
            if (severity != null && severity.intValue() == 2) {
                return true;
            }
        }
        return false;
    }

}
