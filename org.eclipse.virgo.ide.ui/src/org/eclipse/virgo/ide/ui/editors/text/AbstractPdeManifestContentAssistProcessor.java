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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.environments.IExecutionEnvironment;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ContentAssistEvent;
import org.eclipse.jface.text.contentassist.ICompletionListener;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.eclipse.osgi.service.resolver.ExportPackageDescription;
import org.eclipse.osgi.util.ManifestElement;
import org.eclipse.pde.core.IBaseModel;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.ModelEntry;
import org.eclipse.pde.core.plugin.PluginRegistry;
import org.eclipse.pde.internal.core.ICoreConstants;
import org.eclipse.pde.internal.core.ibundle.IBundleModel;
import org.eclipse.pde.internal.core.util.HeaderMap;
import org.eclipse.pde.internal.core.util.PDEJavaHelper;
import org.eclipse.pde.internal.ui.PDEPluginImages;
import org.eclipse.pde.internal.ui.editor.PDEFormEditor;
import org.eclipse.pde.internal.ui.editor.PDESourcePage;
import org.eclipse.pde.internal.ui.editor.contentassist.TypePackageCompletionProcessor;
import org.eclipse.pde.internal.ui.editor.plugin.ManifestEditor;
import org.eclipse.pde.internal.ui.util.ImageOverlayIcon;
import org.eclipse.pde.internal.ui.util.PDEJavaHelperUI;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;

/**
 * @author Christian Dupuis
 */
public class AbstractPdeManifestContentAssistProcessor extends TypePackageCompletionProcessor implements ICompletionListener {

    protected PDESourcePage fSourcePage;

    private IJavaProject fJP;

    // if we order the headers alphabetically in the array, there is no need to
    // sort and we can save time
    protected static String[] fHeader = { Constants.BUNDLE_ACTIVATOR, Constants.BUNDLE_ACTIVATIONPOLICY, Constants.BUNDLE_CATEGORY,
        Constants.BUNDLE_CLASSPATH, Constants.BUNDLE_CONTACTADDRESS, Constants.BUNDLE_COPYRIGHT, Constants.BUNDLE_DESCRIPTION,
        Constants.BUNDLE_DOCURL, Constants.BUNDLE_LOCALIZATION, Constants.BUNDLE_MANIFESTVERSION, Constants.BUNDLE_NAME, Constants.BUNDLE_NATIVECODE,
        Constants.BUNDLE_REQUIREDEXECUTIONENVIRONMENT, Constants.BUNDLE_SYMBOLICNAME, Constants.BUNDLE_UPDATELOCATION, Constants.BUNDLE_VENDOR,
        Constants.BUNDLE_VERSION, Constants.DYNAMICIMPORT_PACKAGE, ICoreConstants.ECLIPSE_BUDDY_POLICY, ICoreConstants.ECLIPSE_GENERIC_CAPABILITY,
        ICoreConstants.ECLIPSE_GENERIC_REQUIRED, ICoreConstants.ECLIPSE_LAZYSTART, Constants.EXPORT_PACKAGE, ICoreConstants.PLATFORM_FILTER,
        ICoreConstants.ECLIPSE_REGISTER_BUDDY, ICoreConstants.EXPORT_SERVICE, Constants.IMPORT_PACKAGE, ICoreConstants.IMPORT_SERVICE,
        Constants.REQUIRE_BUNDLE, Constants.FRAGMENT_HOST };

    private static final String BAUMAN = "Brian Bauman"; //$NON-NLS-1$

    private static final String ANISZCZYK = "Chris Aniszczyk"; //$NON-NLS-1$

    private static final String LASOCKI_BICZYSKO = "Janek Lasocki-Biczysko"; //$NON-NLS-1$

    private static final String PAWLOWSKI = "Mike Pawlowski"; //$NON-NLS-1$

    private static final String MELHEM = "Wassim Melhem"; //$NON-NLS-1$

    private static final String[] fNames = { BAUMAN, ANISZCZYK, LASOCKI_BICZYSKO, PAWLOWSKI, MELHEM };

    protected static final short F_TYPE_HEADER = 0, // header proposal
        F_TYPE_PKG = 1, // package proposal
        F_TYPE_BUNDLE = 2, // bundle proposal
        F_TYPE_CLASS = 3, // class proposal
        F_TYPE_DIRECTIVE = 4, // directive proposal
        F_TYPE_ATTRIBUTE = 5, // attribute proposal
        F_TYPE_VALUE = 6, // value of attribute or directive proposal
        F_TYPE_EXEC_ENV = 7, // value of execution env., added since we
        // use a unique icon for exec envs.
        F_TYPE_LIB = 8, F_TOTAL_TYPES = 9;

    protected final Image[] fImages = new Image[F_TOTAL_TYPES];

    private static final String[] fExecEnvs;

    static {
        IExecutionEnvironment[] envs = JavaRuntime.getExecutionEnvironmentsManager().getExecutionEnvironments();
        fExecEnvs = new String[envs.length];
        for (int i = 0; i < envs.length; i++) {
            fExecEnvs[i] = envs[i].getId();
        }
        Arrays.sort(fExecEnvs, new Comparator() {

            public int compare(Object o1, Object o2) {
                return ((String) o1).compareToIgnoreCase((String) o2);
            }
        });
    }

    protected Map fHeaders;

    public AbstractPdeManifestContentAssistProcessor(PDESourcePage sourcePage) {
        this.fSourcePage = sourcePage;
    }

    @Override
    public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
        IDocument doc = this.fSourcePage.getDocumentProvider().getDocument(this.fSourcePage.getInputContext().getInput());
        if (this.fHeaders == null) {
            parseDocument(doc);
        }
        try {
            int lineNum = doc.getLineOfOffset(offset);
            int lineStart = doc.getLineOffset(lineNum);
            return computeCompletionProposals(doc, lineStart, offset);
        } catch (BadLocationException e) {
        }
        return null;
    }

    protected final void parseDocument(IDocument doc) {
        this.fHeaders = new HeaderMap();
        int numLines = doc.getNumberOfLines();
        int offset = 0;
        for (int i = 0; i < numLines; i++) {
            try {
                IRegion line = doc.getLineInformation(i);
                String value = doc.get(offset, line.getOffset() + line.getLength() - offset);
                if (value.indexOf(':') != value.lastIndexOf(':') || i == numLines - 1) {
                    value = doc.get(offset, line.getOffset() - offset - 1).trim();
                    int index = value.indexOf(':');
                    String header = index == -1 ? value : value.substring(0, index);
                    try {
                        if (value.endsWith(",")) {
                            value = value.substring(0, value.length() - 1);
                        }
                        ManifestElement[] elems = ManifestElement.parseHeader(header, value.substring(index + 1));
                        if (shouldStoreSet(header)) {
                            HashSet set = new HashSet(4 / 3 * elems.length + 1);
                            for (ManifestElement element : elems) {
                                set.add(element.getValue());
                            }
                            this.fHeaders.put(header, set);
                        } else {
                            this.fHeaders.put(header, elems);
                        }
                    } catch (BundleException e) {
                    }
                    offset = line.getOffset();
                }
            } catch (BadLocationException e) {
            }
        }
    }

    protected boolean shouldStoreSet(String header) {
        return header.equalsIgnoreCase(Constants.IMPORT_PACKAGE) || header.equalsIgnoreCase(Constants.EXPORT_PACKAGE)
            || header.equalsIgnoreCase(Constants.REQUIRE_BUNDLE) || header.equalsIgnoreCase(Constants.BUNDLE_REQUIREDEXECUTIONENVIRONMENT);
    }

    protected ICompletionProposal[] computeCompletionProposals(IDocument doc, int startOffset, int offset) {
        try {
            if (!isHeader(doc, startOffset, offset)) {
                return computeValue(doc, startOffset, offset);
            }
            return computeHeader(doc.get(startOffset, offset - startOffset), startOffset, offset);
        } catch (BadLocationException e) {
        }
        return new ICompletionProposal[0];
    }

    protected final boolean isHeader(IDocument doc, int startOffset, int offset) throws BadLocationException {
        String value = doc.get(startOffset, offset - startOffset);
        if (value.indexOf(':') != -1) {
            return false;
        }
        for (--startOffset; startOffset >= 0; --startOffset) {
            char ch = doc.getChar(startOffset);
            if (!Character.isWhitespace(ch)) {
                return ch != ',' && ch != ':' && ch != ';';
            }
        }
        return true;
    }

    protected ICompletionProposal[] computeHeader(String currentValue, int startOffset, int offset) {
        ArrayList completions = new ArrayList();
        IBaseModel model = this.fSourcePage.getInputContext().getModel();
        if (model instanceof IBundleModel && !((IBundleModel) model).isFragmentModel()) {
        }
        for (String element : fHeader) {
            if (element.regionMatches(true, 0, currentValue, 0, currentValue.length()) && this.fHeaders.get(element) == null) {
                BundleTypeCompletionProposal proposal = new BundleTypeCompletionProposal(element + ": ", getImage(F_TYPE_HEADER), //$NON-NLS-1$
                    element, startOffset, currentValue.length());
                proposal.setAdditionalProposalInfo(getJavaDoc(element));
                completions.add(proposal);
            }
        }
        return (ICompletionProposal[]) completions.toArray(new ICompletionProposal[completions.size()]);
    }

    protected ICompletionProposal[] computeValue(IDocument doc, int startOffset, int offset) throws BadLocationException {
        String value = doc.get(startOffset, offset - startOffset);
        int lineNum = doc.getLineOfOffset(startOffset) - 1;
        int index;
        while ((index = value.indexOf(':')) == -1 || value.length() - 1 != index && value.charAt(index + 1) == '=') {
            int startLine = doc.getLineOffset(lineNum);
            value = doc.get(startLine, offset - startLine);
            lineNum--;
        }

        int length = value.length();
        if (value.regionMatches(true, 0, Constants.IMPORT_PACKAGE, 0, Math.min(length, Constants.IMPORT_PACKAGE.length()))) {
            return handleImportPackageCompletion(value.substring(Constants.IMPORT_PACKAGE.length() + 1), offset);
        }
        if (value.regionMatches(true, 0, Constants.FRAGMENT_HOST, 0, Math.min(length, Constants.FRAGMENT_HOST.length()))) {
            return handleFragmentHostCompletion(value.substring(Constants.FRAGMENT_HOST.length() + 1), offset);
        }
        if (value.regionMatches(true, 0, Constants.REQUIRE_BUNDLE, 0, Math.min(length, Constants.REQUIRE_BUNDLE.length()))) {
            return handleRequireBundleCompletion(value.substring(Constants.REQUIRE_BUNDLE.length() + 1), offset);
        }
        if (value.regionMatches(true, 0, Constants.EXPORT_PACKAGE, 0, Math.min(length, Constants.EXPORT_PACKAGE.length()))) {
            return handleExportPackageCompletion(value.substring(Constants.EXPORT_PACKAGE.length() + 1), offset);
        }
        if (value.regionMatches(true, 0, Constants.BUNDLE_ACTIVATOR, 0, Math.min(length, Constants.BUNDLE_ACTIVATOR.length()))) {
            return handleBundleActivatorCompletion(removeLeadingSpaces(value.substring(Constants.BUNDLE_ACTIVATOR.length() + 1)), offset);
        }
        if (value.regionMatches(true, 0, Constants.BUNDLE_SYMBOLICNAME, 0, Math.min(length, Constants.BUNDLE_SYMBOLICNAME.length()))) {
            return handleBundleSymbolicNameCompletion(value.substring(Constants.BUNDLE_SYMBOLICNAME.length() + 1), offset);
        }
        if (value.regionMatches(true, 0, Constants.BUNDLE_REQUIREDEXECUTIONENVIRONMENT, 0,
            Math.min(length, Constants.BUNDLE_REQUIREDEXECUTIONENVIRONMENT.length()))) {
            return handleRequiredExecEnv(value.substring(Constants.BUNDLE_REQUIREDEXECUTIONENVIRONMENT.length() + 1), offset);
        }
        if (value.regionMatches(true, 0, ICoreConstants.ECLIPSE_LAZYSTART, 0, Math.min(length, ICoreConstants.ECLIPSE_LAZYSTART.length()))) {
            return handleTrueFalseValue(value.substring(ICoreConstants.ECLIPSE_LAZYSTART.length() + 1), offset);
        }
        if (value.regionMatches(true, 0, Constants.BUNDLE_NAME, 0, Math.min(length, Constants.BUNDLE_NAME.length()))) {
            return handleBundleNameCompletion(value.substring(Constants.BUNDLE_NAME.length() + 1), offset);
        }
        if (value.regionMatches(true, 0, Constants.BUNDLE_ACTIVATIONPOLICY, 0, Math.min(length, Constants.BUNDLE_ACTIVATIONPOLICY.length()))) {
            return handleBundleActivationPolicyCompletion(value.substring(Constants.BUNDLE_ACTIVATIONPOLICY.length() + 1), offset);
        }
        if (value.regionMatches(true, 0, ICoreConstants.ECLIPSE_BUDDY_POLICY, 0, Math.min(length, ICoreConstants.ECLIPSE_BUDDY_POLICY.length()))) {
            return handleBuddyPolicyCompletion(value.substring(ICoreConstants.ECLIPSE_BUDDY_POLICY.length() + 1), offset);
        }
        return new ICompletionProposal[0];
    }

    /*
     * Easter Egg
     */
    protected ICompletionProposal[] handleBundleNameCompletion(String currentValue, int offset) {
        currentValue = removeLeadingSpaces(currentValue);
        int length = currentValue.length();

        // only show when there is no bundle name
        if (length == 0) {
            return new ICompletionProposal[] { new BundleTypeCompletionProposal(BAUMAN, null, BAUMAN, offset - length, length),
                new BundleTypeCompletionProposal(ANISZCZYK, null, ANISZCZYK, offset - length, length),
                new BundleTypeCompletionProposal(LASOCKI_BICZYSKO, null, LASOCKI_BICZYSKO, offset - length, length),
                new BundleTypeCompletionProposal(PAWLOWSKI, null, PAWLOWSKI, offset - length, length),
                new BundleTypeCompletionProposal(MELHEM, null, MELHEM, offset - length, length) };
        }

        // only show when we are trying to complete a name
        for (String element : fNames) {
            StringTokenizer tokenizer = new StringTokenizer(currentValue, " "); //$NON-NLS-1$
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                if (element.regionMatches(true, 0, token, 0, token.length())) {
                    return new ICompletionProposal[] {
                        new BundleTypeCompletionProposal(element, null, element, offset - token.length(), token.length()) };
                }
            }
        }
        return new ICompletionProposal[0];
    }

    protected ICompletionProposal[] handleImportPackageCompletion(String currentValue, int offset) {
        int comma = currentValue.lastIndexOf(',');
        int semicolon = currentValue.lastIndexOf(';');
        String value = comma != -1 ? currentValue.substring(comma + 1) : currentValue;
        if (comma > semicolon || comma == semicolon) {
            HashSet set = (HashSet) this.fHeaders.get(Constants.IMPORT_PACKAGE);
            if (set == null) {
                set = parseHeaderForValues(currentValue, offset);
            }
            HashSet importedBundles = (HashSet) this.fHeaders.get(Constants.REQUIRE_BUNDLE);
            if (importedBundles == null) {
                importedBundles = new HashSet(0);
            }
            value = removeLeadingSpaces(value);
            int length = value.length();
            set.remove(value);
            ArrayList completions = new ArrayList();
            IPluginModelBase[] bases = PluginRegistry.getActiveModels();

            for (IPluginModelBase element : bases) { // Remove any packages
                // already imported
                // through
                // Require-Bundle
                BundleDescription desc = element.getBundleDescription();
                if (desc == null || importedBundles.contains(desc.getSymbolicName())) {
                    continue;
                }
                ExportPackageDescription[] expPkgs = desc.getExportPackages();
                for (ExportPackageDescription element2 : expPkgs) {
                    String pkgName = element2.getName();
                    if (pkgName.regionMatches(true, 0, value, 0, length) && !set.contains(pkgName)) {
                        completions.add(new BundleTypeCompletionProposal(pkgName, getImage(F_TYPE_PKG), pkgName, offset - length, length));
                        set.add(pkgName);
                    }
                }
            }
            ICompletionProposal[] proposals = (ICompletionProposal[]) completions.toArray(new ICompletionProposal[completions.size()]);
            sortCompletions(proposals);
            return proposals;
        }
        int equals = currentValue.lastIndexOf('=');
        if (equals == -1 || semicolon > equals) {
            String[] validAtts = new String[] { Constants.RESOLUTION_DIRECTIVE, Constants.VERSION_ATTRIBUTE };
            Integer[] validTypes = new Integer[] { new Integer(F_TYPE_DIRECTIVE), new Integer(F_TYPE_ATTRIBUTE) };
            return handleAttrsAndDirectives(value, initializeNewList(validAtts), initializeNewList(validTypes), offset);
        }
        String attributeValue = removeLeadingSpaces(currentValue.substring(semicolon + 1));
        if (Constants.RESOLUTION_DIRECTIVE.regionMatches(true, 0, attributeValue, 0, Constants.RESOLUTION_DIRECTIVE.length())) {
            return matchValueCompletion(currentValue.substring(equals + 1),
                new String[] { Constants.RESOLUTION_MANDATORY, Constants.RESOLUTION_OPTIONAL }, new int[] { F_TYPE_VALUE, F_TYPE_VALUE }, offset,
                "RESOLUTION_"); //$NON-NLS-1$
        }
        if (Constants.VERSION_ATTRIBUTE.regionMatches(true, 0, attributeValue, 0, Constants.VERSION_ATTRIBUTE.length())) {
            value = removeLeadingSpaces(currentValue.substring(equals + 1));
            if (value.length() == 0) {
                return new ICompletionProposal[] { new BundleTypeCompletionProposal("\"\"", getImage(F_TYPE_VALUE), "\"\"", offset, 0) }; //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
        return new ICompletionProposal[0];
    }

    protected ICompletionProposal[] handleXFriendsCompletion(String value, final int offset) {
        ManifestElement[] elems = (ManifestElement[]) this.fHeaders.get(Constants.BUNDLE_SYMBOLICNAME);
        HashSet set = new HashSet();
        if (elems != null && elems.length > 0) {
            set.add(elems[0].getValue());
        }
        value = removeLeadingSpaces(value);
        if (value.length() == 0) {
            return new ICompletionProposal[] { new BundleTypeCompletionProposal("\"\"", getImage(F_TYPE_VALUE), "\"\"", offset, 0) }; //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (value.charAt(0) == '"') {
            value = value.substring(1);
        }
        int index = value.lastIndexOf(',');
        StringTokenizer tokenizer = new StringTokenizer(value, ","); //$NON-NLS-1$
        while (tokenizer.hasMoreTokens()) {
            set.add(tokenizer.nextToken());
        }
        return handleBundleCompletions(value.substring(index == -1 ? 0 : index + 1), set, F_TYPE_VALUE, offset, true);
    }

    protected ICompletionProposal[] handleFragmentHostCompletion(String currentValue, int offset) {
        int index = currentValue.lastIndexOf(';');
        if (index == -1) {
            HashMap completions = new HashMap();
            IPluginModelBase base = PluginRegistry.findModel(((ManifestEditor) this.fSourcePage.getEditor()).getCommonProject());
            BundleDescription desc = base.getBundleDescription();
            String currentId = desc != null ? desc.getSymbolicName() : null;

            String pluginStart = removeLeadingSpaces(currentValue);
            int length = pluginStart.length();
            IPluginModelBase[] bases = PluginRegistry.getActiveModels();
            for (IPluginModelBase element : bases) {
                desc = element.getBundleDescription();
                if (desc != null && desc.getHost() == null) {
                    String pluginID = element.getBundleDescription().getSymbolicName();
                    if (!completions.containsKey(pluginID) && pluginID.regionMatches(true, 0, pluginStart, 0, length)
                        && !pluginID.equals(currentId)) {
                        completions.put(pluginID,
                            new BundleTypeCompletionProposal(pluginID, getImage(F_TYPE_BUNDLE), pluginID, offset - length, length));
                    }
                }
            }
            return (ICompletionProposal[]) completions.values().toArray(new ICompletionProposal[completions.size()]);
        }
        int equals = currentValue.lastIndexOf('=');
        if (equals == -1 || index > equals) {
            return matchValueCompletion(removeLeadingSpaces(currentValue.substring(index + 1)), new String[] { Constants.BUNDLE_VERSION_ATTRIBUTE },
                new int[] { F_TYPE_ATTRIBUTE }, offset);
        }
        String attributeValue = removeLeadingSpaces(currentValue.substring(index + 1));
        if (Constants.BUNDLE_VERSION_ATTRIBUTE.regionMatches(true, 0, attributeValue, 0, Constants.BUNDLE_VERSION_ATTRIBUTE.length())) {
            return getBundleVersionCompletions(currentValue.substring(0, index).trim(), removeLeadingSpaces(currentValue.substring(equals + 1)),
                offset);
        }
        return new ICompletionProposal[0];
    }

    protected ICompletionProposal[] handleRequireBundleCompletion(String currentValue, int offset) {
        int comma = currentValue.lastIndexOf(',');
        int semicolon = currentValue.lastIndexOf(';');
        String value = comma != -1 ? currentValue.substring(comma + 1) : currentValue;
        if (comma > semicolon || comma == semicolon) {
            HashSet set = (HashSet) this.fHeaders.get(Constants.REQUIRE_BUNDLE);
            if (set == null) {
                set = parseHeaderForValues(currentValue, offset);
            }
            return handleBundleCompletions(value, set, F_TYPE_BUNDLE, offset, false);
        }
        int equals = currentValue.lastIndexOf('=');
        if (equals == -1 || semicolon > equals) {
            String[] validAttrs = new String[] { Constants.BUNDLE_VERSION_ATTRIBUTE, Constants.RESOLUTION_DIRECTIVE, Constants.VISIBILITY_DIRECTIVE };
            Integer[] validTypes = new Integer[] { new Integer(F_TYPE_ATTRIBUTE), new Integer(F_TYPE_DIRECTIVE), new Integer(F_TYPE_DIRECTIVE) };
            return handleAttrsAndDirectives(value, initializeNewList(validAttrs), initializeNewList(validTypes), offset);
        }
        String attributeValue = removeLeadingSpaces(currentValue.substring(semicolon + 1));
        if (Constants.VISIBILITY_DIRECTIVE.regionMatches(true, 0, attributeValue, 0, Constants.VISIBILITY_DIRECTIVE.length())) {
            return matchValueCompletion(currentValue.substring(equals + 1),
                new String[] { Constants.VISIBILITY_PRIVATE, Constants.VISIBILITY_REEXPORT }, new int[] { F_TYPE_VALUE, F_TYPE_VALUE }, offset,
                "VISIBILITY_"); //$NON-NLS-1$
        }
        if (Constants.RESOLUTION_DIRECTIVE.regionMatches(true, 0, attributeValue, 0, Constants.RESOLUTION_DIRECTIVE.length())) {
            return matchValueCompletion(currentValue.substring(equals + 1),
                new String[] { Constants.RESOLUTION_MANDATORY, Constants.RESOLUTION_OPTIONAL }, new int[] { F_TYPE_VALUE, F_TYPE_VALUE }, offset,
                "RESOLUTION_"); //$NON-NLS-1$
        }
        if (Constants.BUNDLE_VERSION_ATTRIBUTE.regionMatches(true, 0, attributeValue, 0, Constants.RESOLUTION_DIRECTIVE.length())) {
            String pluginId = removeLeadingSpaces(currentValue.substring(comma == -1 ? 0 : comma + 1, semicolon));
            return getBundleVersionCompletions(pluginId, removeLeadingSpaces(currentValue.substring(equals + 1)), offset);
        }
        return new ICompletionProposal[0];
    }

    protected ICompletionProposal[] getBundleVersionCompletions(String pluginID, String existingValue, int offset) {
        ModelEntry entry = PluginRegistry.findEntry(pluginID);
        if (entry != null) {
            IPluginModelBase[] hosts = entry.getActiveModels();
            ArrayList proposals = new ArrayList(hosts.length);
            for (IPluginModelBase element : hosts) {
                String proposalValue = getVersionProposal(element);
                if (proposalValue.regionMatches(0, existingValue, 0, existingValue.length())) {
                    proposals.add(new BundleTypeCompletionProposal(proposalValue.substring(existingValue.length()), getImage(F_TYPE_VALUE),
                        proposalValue, offset, 0));
                }
            }
            return (ICompletionProposal[]) proposals.toArray(new ICompletionProposal[proposals.size()]);
        } else if (existingValue.length() == 0) {
            return new ICompletionProposal[] { new BundleTypeCompletionProposal("\"\"", getImage(F_TYPE_VALUE), "\"\"", offset, 0) }; //$NON-NLS-1$ //$NON-NLS-2$
        }
        return new ICompletionProposal[0];
    }

    protected String getVersionProposal(IPluginModelBase base) {
        StringBuffer buffer = new StringBuffer("\""); //$NON-NLS-1$
        BundleDescription desc = base.getBundleDescription();
        if (desc != null) {
            Version version = desc.getVersion();
            buffer.append(version.getMajor());
            buffer.append('.');
            buffer.append(version.getMinor());
            buffer.append('.');
            buffer.append(version.getMicro());
        } else {
            char[] chars = base.getPluginBase().getVersion().toCharArray();
            int periodCount = 0;
            for (char element : chars) {
                if (element == '.') {
                    if (periodCount == 2) {
                        break;
                    }
                    ++periodCount;
                }
                buffer.append(element);
            }
        }
        return buffer.append('\"').toString();
    }

    protected ICompletionProposal[] handleBundleCompletions(String value, Collection doNotInclude, int type, int offset, boolean includeFragments) {
        value = removeLeadingSpaces(value);
        int length = value.length();
        doNotInclude.remove(value);
        ArrayList completions = new ArrayList();
        IPluginModelBase[] bases = PluginRegistry.getActiveModels();
        for (IPluginModelBase element : bases) {
            BundleDescription desc = element.getBundleDescription();
            if (desc != null) {
                if (!includeFragments && desc.getHost() != null) {
                    continue;
                }
                String bundleId = desc.getSymbolicName();
                if (bundleId.regionMatches(true, 0, value, 0, value.length()) && !doNotInclude.contains(bundleId)) {
                    completions.add(new BundleTypeCompletionProposal(bundleId, getImage(type), bundleId, offset - length, length));
                }
            }
        }
        return (ICompletionProposal[]) completions.toArray(new ICompletionProposal[completions.size()]);
    }

    protected ICompletionProposal[] handleExportPackageCompletion(String currentValue, int offset) {
        int comma = currentValue.lastIndexOf(',');
        int semicolon = currentValue.lastIndexOf(';');
        ArrayList list = new ArrayList();
        if (!insideQuotes(currentValue) && comma > semicolon || comma == semicolon) {
            String value = comma != -1 ? currentValue.substring(comma + 1) : currentValue;
            HashSet set = (HashSet) this.fHeaders.get(Constants.EXPORT_PACKAGE);
            if (set == null) {
                set = parseHeaderForValues(currentValue, offset);
            }
            value = removeLeadingSpaces(value);
            int length = value.length();
            IProject proj = ((PDEFormEditor) this.fSourcePage.getEditor()).getCommonProject();
            if (proj != null) {
                IJavaProject jp = JavaCore.create(proj);
                IPackageFragment[] frags = PDEJavaHelper.getPackageFragments(jp, set, false);
                for (IPackageFragment element : frags) {
                    String name = element.getElementName();
                    if (name.regionMatches(true, 0, value, 0, length)) {
                        list.add(new BundleTypeCompletionProposal(name, getImage(F_TYPE_PKG), name, offset - length, length));
                    }
                }
            }
        } else {
            String value = currentValue;
            if (comma > 0) {
                do {
                    String prefix = currentValue.substring(0, comma);
                    if (!insideQuotes(prefix)) {
                        value = currentValue.substring(comma + 1);
                        break;
                    }
                    comma = currentValue.lastIndexOf(',', comma - 1);
                } while (comma > 0);
            }
            int equals = currentValue.lastIndexOf('=');
            if (equals == -1 || semicolon > equals) {
                String[] validAttrs = new String[] { Constants.VERSION_ATTRIBUTE, ICoreConstants.INTERNAL_DIRECTIVE,
                    ICoreConstants.FRIENDS_DIRECTIVE };
                Integer[] validTypes = new Integer[] { new Integer(F_TYPE_ATTRIBUTE), new Integer(F_TYPE_DIRECTIVE), new Integer(F_TYPE_DIRECTIVE) };
                return handleAttrsAndDirectives(value, initializeNewList(validAttrs), initializeNewList(validTypes), offset);
            }
            String attributeValue = removeLeadingSpaces(currentValue.substring(semicolon + 1));
            if (ICoreConstants.FRIENDS_DIRECTIVE.regionMatches(true, 0, attributeValue, 0, ICoreConstants.FRIENDS_DIRECTIVE.length())) {
                return handleXFriendsCompletion(currentValue.substring(equals + 1), offset);
            }
            if (ICoreConstants.INTERNAL_DIRECTIVE.regionMatches(true, 0, attributeValue, 0, ICoreConstants.INTERNAL_DIRECTIVE.length())) {
                return handleTrueFalseValue(currentValue.substring(equals + 1), offset);
            }
            if (Constants.VERSION_ATTRIBUTE.regionMatches(true, 0, attributeValue, 0, Constants.VERSION_ATTRIBUTE.length())) {
                value = removeLeadingSpaces(currentValue.substring(equals + 1));
                if (value.length() == 0) {
                    return new ICompletionProposal[] { new BundleTypeCompletionProposal("\"\"", getImage(F_TYPE_VALUE), "\"\"", offset, 0) }; //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
        }
        return (ICompletionProposal[]) list.toArray(new ICompletionProposal[list.size()]);
    }

    protected ICompletionProposal[] handleBundleActivatorCompletion(final String currentValue, final int offset) {
        ArrayList completions = new ArrayList();
        IProject project = ((PDEFormEditor) this.fSourcePage.getEditor()).getCommonProject();
        int startOffset = offset - currentValue.length();
        generateTypePackageProposals(currentValue, project, completions, startOffset, IJavaSearchConstants.CLASS);
        ICompletionProposal[] proposals = (ICompletionProposal[]) completions.toArray(new ICompletionProposal[completions.size()]);
        sortCompletions(proposals);
        return proposals;
    }

    protected ICompletionProposal[] handleBundleSymbolicNameCompletion(String currentValue, int offset) {
        int semicolon = currentValue.indexOf(';');
        if (semicolon != -1) {
            int equals = currentValue.indexOf('=');
            if (equals == -1) {
                String attribute = currentValue.substring(semicolon + 1);
                attribute = removeLeadingSpaces(attribute);
                Object o = this.fHeaders.get(Constants.BUNDLE_MANIFESTVERSION);
                int type = o == null || o.toString().equals("1") ? F_TYPE_ATTRIBUTE : F_TYPE_DIRECTIVE;//$NON-NLS-1$
                if (Constants.SINGLETON_DIRECTIVE.regionMatches(true, 0, attribute, 0, attribute.length())) {
                    int length = attribute.length();
                    BundleTypeCompletionProposal proposal = new BundleTypeCompletionProposal(Constants.SINGLETON_DIRECTIVE + ":=", //$NON-NLS-1$
                        getImage(type), Constants.SINGLETON_DIRECTIVE, offset - length, length);
                    proposal.setAdditionalProposalInfo(getJavaDoc("SINGLETON_DIRECTIVE")); //$NON-NLS-1$
                    return new ICompletionProposal[] { proposal };
                }
            } else if (equals > semicolon) {
                return handleTrueFalseValue(currentValue.substring(equals + 1), offset);
            }
        }
        return new ICompletionProposal[0];
    }

    protected ICompletionProposal[] handleBundleActivationPolicyCompletion(final String currentValue, final int offset) {
        int comma = currentValue.lastIndexOf(',');
        int semicolon = currentValue.lastIndexOf(';');
        if (!insideQuotes(currentValue) && comma > semicolon || comma == semicolon) {
            String value = removeLeadingSpaces(currentValue);
            String lazyValue = "lazy"; //$NON-NLS-1$
            int length = value.length();
            if (lazyValue.regionMatches(0, value, 0, length)) {
                return new ICompletionProposal[] { new BundleTypeCompletionProposal(lazyValue, null, lazyValue, offset - length, length) };
            }
        }
        return new ICompletionProposal[0];
    }

    protected ICompletionProposal[] handleBuddyPolicyCompletion(String currentValue, int offset) {
        String value = removeLeadingSpaces(currentValue);
        // values from bug 178517 comment #7
        ArrayList validValues = initializeNewList(new String[] { "dependent", "global", "registered", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            "app", "ext", "boot", "parent" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        ArrayList types = initializeNewList(new Object[] { new Integer(F_TYPE_VALUE), new Integer(F_TYPE_VALUE), new Integer(F_TYPE_VALUE),
            new Integer(F_TYPE_VALUE), new Integer(F_TYPE_VALUE), new Integer(F_TYPE_VALUE), new Integer(F_TYPE_VALUE) });
        return handleAttrsAndDirectives(value, validValues, types, offset);
    }

    protected ICompletionProposal[] handleRequiredExecEnv(String currentValue, int offset) {
        int comma = currentValue.lastIndexOf(',');
        if (comma != -1) {
            currentValue = currentValue.substring(comma + 1);
        }
        currentValue = removeLeadingSpaces(currentValue);
        ArrayList completions = new ArrayList();
        HashSet set = (HashSet) this.fHeaders.get(Constants.BUNDLE_REQUIREDEXECUTIONENVIRONMENT);
        if (set == null) {
            set = new HashSet(0);
        }
        int length = currentValue.length();
        for (int i = 0; i < fExecEnvs.length; i++) {
            if (fExecEnvs[i].regionMatches(true, 0, currentValue, 0, length) && !set.contains(fExecEnvs[i])) {
                completions.add(new BundleTypeCompletionProposal(fExecEnvs[i], getImage(F_TYPE_EXEC_ENV), fExecEnvs[i], offset - length, length));
            }
        }
        return (ICompletionProposal[]) completions.toArray(new ICompletionProposal[completions.size()]);
    }

    protected ICompletionProposal[] handleTrueFalseValue(String currentValue, int offset) {
        currentValue = removeLeadingSpaces(currentValue);
        int length = currentValue.length();
        if (length == 0) {
            return new ICompletionProposal[] { new BundleTypeCompletionProposal("true", getImage(F_TYPE_VALUE), "true", offset, 0), //$NON-NLS-1$ //$NON-NLS-2$
                new BundleTypeCompletionProposal("false", getImage(F_TYPE_VALUE), "false", offset, 0) //$NON-NLS-1$ //$NON-NLS-2$
            };
        } else if (length < 5 && "true".regionMatches(true, 0, currentValue, 0, length)) {
            return new ICompletionProposal[] { new BundleTypeCompletionProposal("true", getImage(F_TYPE_VALUE), "true", offset - length, length) //$NON-NLS-1$ //$NON-NLS-2$
            };
        } else if (length < 6 && "false".regionMatches(true, 0, currentValue, 0, length)) {
            return new ICompletionProposal[] { new BundleTypeCompletionProposal("false", getImage(F_TYPE_VALUE), "false", offset - length, length) //$NON-NLS-1$ //$NON-NLS-2$
            };
        }
        return new ICompletionProposal[0];
    }

    protected ICompletionProposal[] matchValueCompletion(String value, String[] attrs, int[] types, int offset) {
        return matchValueCompletion(value, attrs, types, offset, ""); //$NON-NLS-1$
    }

    protected ICompletionProposal[] matchValueCompletion(String value, String[] attrs, int[] types, int offset, String prefixCostant) {
        ArrayList list = new ArrayList();
        int length = value.length();
        BundleTypeCompletionProposal proposal = null;
        for (int i = 0; i < attrs.length; i++) {
            if (attrs[i].regionMatches(true, 0, value, 0, length)) {
                if (types[i] == F_TYPE_ATTRIBUTE) {
                    proposal = new BundleTypeCompletionProposal(attrs[i] + "=", getImage(F_TYPE_ATTRIBUTE), attrs[i], offset - length, length); //$NON-NLS-1$
                    proposal.setAdditionalProposalInfo(getJavaDoc(attrs[i] + "_ATTRIBUTE")); //$NON-NLS-1$
                } else if (types[i] == F_TYPE_DIRECTIVE) {
                    proposal = new BundleTypeCompletionProposal(attrs[i] + ":=", getImage(F_TYPE_DIRECTIVE), attrs[i], offset - length, length); //$NON-NLS-1$
                    proposal.setAdditionalProposalInfo(getJavaDoc(attrs[i] + "_DIRECTIVE")); //$NON-NLS-1$
                } else {
                    proposal = new BundleTypeCompletionProposal(attrs[i], getImage(types[i]), attrs[i], offset - length, length);
                    proposal.setAdditionalProposalInfo(getJavaDoc(prefixCostant + attrs[i]));
                }
                list.add(proposal);
            }
        }
        return (ICompletionProposal[]) list.toArray(new ICompletionProposal[list.size()]);
    }

    protected ICompletionProposal[] handleAttrsAndDirectives(String value, ArrayList attrs, ArrayList types, int offset) {
        String fullValue = findFullLine(value, offset, false);
        int semicolon = value.lastIndexOf(';');
        value = removeLeadingSpaces(value.substring(semicolon + 1));
        StringTokenizer tokenizer = new StringTokenizer(fullValue, ";"); //$NON-NLS-1$
        tokenizer.nextToken();
        while (tokenizer.hasMoreTokens()) {
            String tokenValue = removeLeadingSpaces(tokenizer.nextToken());
            int index = tokenValue.indexOf('=');
            if (index == -1) {
                continue;
            }
            if (tokenValue.charAt(index - 1) == ':') {
                --index;
            }
            tokenValue = tokenValue.substring(0, index);
            int indexOfObject = attrs.indexOf(tokenValue);
            if (indexOfObject >= 0) {
                attrs.remove(indexOfObject);
                types.remove(indexOfObject);
            }
        }
        return matchValueCompletion(value, (String[]) attrs.toArray(new String[attrs.size()]), toIntArray(types), offset);
    }

    protected HashSet parseHeaderForValues(String currentValue, int offset) {
        HashSet set = new HashSet();
        String fullValue = findFullLine(currentValue, offset, true);
        StringTokenizer tokenizer = new StringTokenizer(fullValue, ","); //$NON-NLS-1$
        while (tokenizer.hasMoreTokens()) {
            String pkgValue = tokenizer.nextToken();
            int index = pkgValue.indexOf(';');
            set.add(index == -1 ? pkgValue.trim() : pkgValue.substring(0, index).trim());
        }
        return set;
    }

    protected String findFullLine(String value, int offset, boolean entireHeader) {
        IDocument doc = this.fSourcePage.getDocumentProvider().getDocument(this.fSourcePage.getInputContext().getInput());
        try {
            int line = doc.getLineOfOffset(offset);
            String newValue = ""; //$NON-NLS-1$
            int startOfLine = 0;
            int colon = -1;
            do {
                startOfLine = doc.getLineOffset(line);
                newValue = doc.get(offset, doc.getLineLength(line) - offset + startOfLine);
                ++line;
                colon = newValue.lastIndexOf(':');
            } while ((colon == -1 || newValue.length() > colon && newValue.charAt(colon + 1) == '=') && (entireHeader || newValue.indexOf(',') == -1)
                && !(doc.getNumberOfLines() == line));
            int firstColon = newValue.indexOf(':');
            if (colon > 0 && newValue.charAt(colon + 1) != '=') {
                newValue = doc.get(offset, startOfLine - 1 - offset);
            } else if (firstColon > 0 && newValue.charAt(firstColon + 1) == ' ') {
                newValue = doc.get(offset, startOfLine - 1 - offset);
            } else {
                // break on the comma to find our element, but only break on a
                // comma that is not enclosed in parenthesis
                int comma = newValue.indexOf(',');
                int parenthesis = newValue.indexOf('"');
                if (!(parenthesis < comma && newValue.indexOf('"', parenthesis + 1) > comma)) {
                    newValue = comma != -1 ? newValue.substring(0, comma) : newValue;
                }
            }
            return value.concat(newValue);
        } catch (BadLocationException e) {
        }
        return ""; //$NON-NLS-1$
    }

    protected int[] toIntArray(ArrayList list) {
        int[] result = new int[list.size()];
        int i = -1;
        while (++i < result.length) {
            Object o = list.get(i);
            if (!(o instanceof Integer)) {
                return new int[0];
            }
            result[i] = ((Integer) o).intValue();
        }
        return result;
    }

    // if you use java.util.Arrays.asList(), we get an UnsupportedOperation
    // later in the code
    protected final ArrayList initializeNewList(Object[] values) {
        ArrayList list = new ArrayList(values.length);
        for (Object element : values) {
            list.add(element);
        }
        return list;
    }

    protected boolean insideQuotes(String value) {
        char[] chars = value.toCharArray();
        int numOfQuotes = 0;
        for (char element : chars) {
            if (element == '\"') {
                ++numOfQuotes;
            }
        }
        int j = numOfQuotes % 2;
        return j == 1;
    }

    public void assistSessionEnded(ContentAssistEvent event) {
        this.fHeaders = null;
    }

    public void assistSessionStarted(ContentAssistEvent event) {
    }

    public void selectionChanged(ICompletionProposal proposal, boolean smartToggle) {
    }

    public Image getImage(int type) {
        if (type >= 0 && type < F_TOTAL_TYPES) {
            if (this.fImages[type] == null) {
                switch (type) {
                    case F_TYPE_HEADER:
                        return this.fImages[type] = PDEPluginImages.DESC_BUILD_VAR_OBJ.createImage();
                    case F_TYPE_PKG:
                        return PDEPluginImages.get(PDEPluginImages.OBJ_DESC_PACKAGE);
                    case F_TYPE_BUNDLE:
                        return this.fImages[type] = PDEPluginImages.DESC_PLUGIN_OBJ.createImage();
                    case F_TYPE_CLASS:
                        return PDEPluginImages.get(PDEPluginImages.OBJ_DESC_GENERATE_CLASS);
                    case F_TYPE_ATTRIBUTE:
                        return this.fImages[type] = PDEPluginImages.DESC_ATT_URI_OBJ.createImage();
                    case F_TYPE_DIRECTIVE:
                        this.fImages[F_TYPE_ATTRIBUTE] = PDEPluginImages.DESC_ATT_URI_OBJ.createImage();
                        ImageOverlayIcon icon = new ImageOverlayIcon(this.fImages[F_TYPE_ATTRIBUTE],
                            new ImageDescriptor[][] { new ImageDescriptor[] { PDEPluginImages.DESC_DOC_CO }, null, null, null });
                        return this.fImages[type] = icon.createImage();
                    case F_TYPE_EXEC_ENV:
                        return this.fImages[type] = PDEPluginImages.DESC_JAVA_LIB_OBJ.createImage();
                    case F_TYPE_VALUE:
                        return null;
                }
            } else {
                return this.fImages[type];
            }
        }
        return null;
    }

    protected void dispose() {
        for (int i = 0; i < this.fImages.length; i++) {
            if (this.fImages[i] != null && !this.fImages[i].isDisposed()) {
                this.fImages[i].dispose();
            }
        }
    }

    protected String getJavaDoc(String constant) {
        if (this.fJP == null) {
            IProject project = ((PDEFormEditor) this.fSourcePage.getEditor()).getCommonProject();
            this.fJP = JavaCore.create(project);
        }
        return PDEJavaHelperUI.getOSGIConstantJavaDoc(constant, this.fJP);
    }

}
