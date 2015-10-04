/// *******************************************************************************
// * Copyright (c) 2009, 2012 SpringSource, a divison of VMware, Inc.
// * All rights reserved. This program and the accompanying materials
// * are made available under the terms of the Eclipse Public License v1.0
// * which accompanies this distribution, and is available at
// * http://www.eclipse.org/legal/epl-v10.html
// *
// * Contributors:
// * SpringSource, a division of VMware, Inc. - initial API and implementation
// *******************************************************************************/
// package org.eclipse.virgo.ide.manifest.internal.core.validation.rules;
//
//
//
/// **
// * @author Christian Dupuis
// * @since 1.0.0
// */
/// **
// * TODO CD add comments
// */
// @SuppressWarnings("restriction")
// public class BundleSymbolicNameRule extends AbstractBundleManifestHeaderRule {
//
// private static final String[] FRAGMENT_ATTACHMENT_DIRECTIVE_ALLOWED_VALUES = new String[] {
// Constants.FRAGMENT_ATTACHMENT_ALWAYS, Constants.FRAGMENT_ATTACHMENT_NEVER,
// Constants.FRAGMENT_ATTACHMENT_RESOLVETIME };
//
// @Override
// protected String[] getHeaderName() {
// return new String[] { Constants.BUNDLE_SYMBOLICNAME };
// }
//
// @Override
// protected boolean isRequiredHeader(IResource resource) {
// return !FacetUtils.hasProjectFacet(resource, FacetCorePlugin.WEB_FACET_ID);
// }
//
// @Override
// protected void validateHeader(BundleManifestHeader header, BundleManifestValidationContext context) {
//
// BundleManifestHeaderElement[] elements = header.getBundleManifestHeaderElements();
// String id = elements.length > 0 ? elements[0].getManifestElement().getValue() : null;
// if (id == null || id.length() == 0) {
// context.error("NO_SYMBOLIC_NAME", BundleManifestCoreMessages.BundleErrorReporter_NoSymbolicName, header
// .getLineNumber() + 1);
// }
//
// if (StringUtils.hasText(id) && !IdUtil.isValidCompositeID3_0(id)) {
// context.error("ILLAGEL_SYMBOLIC_NAME", BundleManifestCoreMessages.BundleErrorReporter_InvalidSymbolicName,
// BundleManifestUtils.getLineNumber(context.getBundleManifest().getDocument(), header, id));
// }
//
// if (elements.length > 0) {
// // validate singleton directive
// context.validateBooleanAttributeValue(header, elements[0].getManifestElement(),
// Constants.SINGLETON_DIRECTIVE);
// context.validateBooleanDirectiveValue(header, elements[0].getManifestElement(),
// Constants.SINGLETON_DIRECTIVE);
//
// // validate fragment-attachment directive
// context.validateAttributeValue(header, elements[0].getManifestElement(),
// Constants.FRAGMENT_ATTACHMENT_DIRECTIVE, FRAGMENT_ATTACHMENT_DIRECTIVE_ALLOWED_VALUES);
// context.validateDirectiveValue(header, elements[0].getManifestElement(),
// Constants.FRAGMENT_ATTACHMENT_DIRECTIVE, FRAGMENT_ATTACHMENT_DIRECTIVE_ALLOWED_VALUES);
// }
// }
//
// @Override
// protected String getMissingRequiredHeaderErrorId() {
// return ManifestValidationRuleConstants.MISSING_BUNDLE_SYMBOLIC_NAME;
// }
//
// }
