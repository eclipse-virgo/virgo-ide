/*********************************************************************
 * Copyright (c) 2009, 2012 SpringSource, a division of VMware, Inc.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.runtime.core;

import java.util.List;

/**
 * Marker interface to be implemented by configuration providers for the dm server integration.
 *
 * @author Christian Dupuis
 * @since 1.0.0
 */
public interface IServerConfiguration {

    List<String> getArtefactOrder();

    void setArtefactOrder(List<String> artefacts);

    void addArtefact(String artefact);

    void removeArtefact(String artefact);

}
