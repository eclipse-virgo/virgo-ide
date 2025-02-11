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
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * ASM {@link MethodVisitor} to scan method bodies for imports.
 *
 * @author Christian Dupuis
 * @author Rob Harrop
 */
final class ArtefactAnalyserMethodVisitor extends MethodVisitor {

    /**
     * The <code>PartialManifest</code> being updated.
     */
    private final PartialManifest partialManifest;

    /**
     * The type that is being scanned.
     */
    private final Type type;

    /**
     * Creates a new <code>ArtefactAnalyserMethodVisitor</code> for the supplied {@link PartialManifest}.
     *
     * @param partialManifest the <code>PartialManifest</code>.
     */
    public ArtefactAnalyserMethodVisitor(PartialManifest partialManifest, Type type) {
        super(Opcodes.ASM9);
        this.partialManifest = partialManifest;
        this.type = type;
    }

    /**
     * @inheritDoc
     */
    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        Type t = Type.getType(desc);
        VisitorUtils.recordReferencedTypes(this.partialManifest, t);
        VisitorUtils.recordUses(this.partialManifest, this.type, t);
        return null;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        VisitorUtils.recordReferencedTypes(this.partialManifest, Type.getType(desc));
        VisitorUtils.recordReferencedTypes(this.partialManifest, Type.getObjectType(owner));
    }

    /**
     * @inheritDoc
     */
    @Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        Type t = Type.getType(desc);
        VisitorUtils.recordReferencedTypes(this.partialManifest, t);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc) {
        Type t = Type.getObjectType(owner);
        VisitorUtils.recordReferencedTypes(this.partialManifest, t);
        VisitorUtils.recordReferencedTypes(this.partialManifest, Type.getReturnType(desc));
        VisitorUtils.recordReferencedTypes(this.partialManifest, Type.getArgumentTypes(desc));
    }

    /**
     * @inheritDoc
     */
    @Override
    public void visitMultiANewArrayInsn(String desc, int dims) {
        Type t = Type.getType(desc);
        VisitorUtils.recordReferencedTypes(this.partialManifest, t);
    }

    /**
     * @inheritDoc
     */
    @Override
    public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
        Type t = Type.getType(desc);
        VisitorUtils.recordReferencedTypes(this.partialManifest, t);
        VisitorUtils.recordUses(this.partialManifest, this.type, t);
        return null;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        if (type != null) {
            Type t = Type.getObjectType(type);
            VisitorUtils.recordReferencedTypes(this.partialManifest, t);
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public void visitTypeInsn(int opcode, String type) {
        Type t = Type.getObjectType(type);
        VisitorUtils.recordReferencedTypes(this.partialManifest, t);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitLdcInsn(Object cst) {
        if (cst instanceof Type) {
            VisitorUtils.recordReferencedTypes(this.partialManifest, (Type) cst);
        }
    }

}
