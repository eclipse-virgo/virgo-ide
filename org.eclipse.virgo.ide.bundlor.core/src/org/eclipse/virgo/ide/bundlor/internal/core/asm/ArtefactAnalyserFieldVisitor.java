/*********************************************************************
 * Copyright (c) 2010 SpringSource, a division of VMware, Inc.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.bundlor.internal.core.asm;

import org.eclipse.virgo.bundlor.support.partialmanifest.PartialManifest;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * ASM {@link FieldVisitor} for scanning class files.
 *
 * @author Christian Dupuis
 * @author Glyn Normington
 */
final class ArtefactAnalyserFieldVisitor extends FieldVisitor {

    /**
     * That <code>PartialManifest</code> being updated.
     */
    private final PartialManifest partialManifest;

    /**
     * The type that is being scanned.
     */
    private final Type type;

    /**
     * Creates a new <code>ArtefactAnalyserClassVisitor</code> to scan the supplied {@link PartialManifest}.
     *
     * @param partialManifest the <code>PartialManifest</code> to scan.
     */
    ArtefactAnalyserFieldVisitor(PartialManifest partialManifest, Type type) {
        super(Opcodes.ASM9);
        this.partialManifest = partialManifest;
        this.type = type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        Type t = Type.getType(desc);
        VisitorUtils.recordReferencedTypes(this.partialManifest, t);
        VisitorUtils.recordUses(this.partialManifest, this.type, t);
        return null;
    }

}
