/*********************************************************************
 * Copyright (c) 2009, 2012 SpringSource, a division of VMware, Inc.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.par;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Bundle</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.virgo.ide.par.Bundle#getSymbolicName <em>Symbolic Name</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.virgo.ide.par.ParPackage#getBundle()
 * @model
 * @generated
 */
public interface Bundle extends EObject {

    /**
     * Returns the value of the '<em><b>Symbolic Name</b></em>' attribute. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Symbolic Name</em>' attribute isn't clear, there really should be more of a
     * description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Symbolic Name</em>' attribute.
     * @see #setSymbolicName(String)
     * @see org.eclipse.virgo.ide.par.ParPackage#getBundle_SymbolicName()
     * @model
     * @generated
     */
    String getSymbolicName();

    /**
     * Sets the value of the '{@link org.eclipse.virgo.ide.par.Bundle#getSymbolicName <em>Symbolic Name</em>}'
     * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Symbolic Name</em>' attribute.
     * @see #getSymbolicName()
     * @generated
     */
    void setSymbolicName(String value);

} // Bundle
