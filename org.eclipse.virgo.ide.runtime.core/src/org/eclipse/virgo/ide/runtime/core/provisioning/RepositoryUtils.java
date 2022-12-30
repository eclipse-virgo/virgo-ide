/*********************************************************************
 * Copyright (c) 2009 - 2013 SpringSource, a division of VMware, Inc.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.virgo.ide.runtime.core.provisioning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.text.StringMatcher;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.osgi.service.resolver.VersionRange;
import org.eclipse.virgo.ide.bundlerepository.domain.OsgiVersion;
import org.eclipse.virgo.ide.bundlerepository.domain.PackageExport;
import org.eclipse.virgo.ide.bundlerepository.domain.PackageMember;
import org.eclipse.virgo.ide.facet.core.FacetUtils;
import org.eclipse.virgo.ide.manifest.core.BundleManifestCorePlugin;
import org.eclipse.virgo.ide.runtime.core.IServerRuntimeProvider;
import org.eclipse.virgo.ide.runtime.core.ServerCorePlugin;
import org.eclipse.virgo.ide.runtime.core.ServerUtils;
import org.eclipse.virgo.ide.runtime.core.artefacts.Artefact;
import org.eclipse.virgo.ide.runtime.core.artefacts.ArtefactRepository;
import org.eclipse.virgo.ide.runtime.core.artefacts.BundleArtefact;
import org.eclipse.virgo.ide.runtime.core.artefacts.IArtefact;
import org.eclipse.virgo.ide.runtime.core.artefacts.IArtefactTyped;
import org.eclipse.virgo.ide.runtime.core.artefacts.LibraryArtefact;
import org.eclipse.virgo.ide.runtime.core.artefacts.LocalBundleArtefact;
import org.eclipse.virgo.ide.runtime.core.artefacts.LocalLibraryArtefact;
import org.eclipse.virgo.ide.runtime.internal.core.ServerRuntimeUtils;
import org.eclipse.virgo.ide.runtime.internal.core.VirgoServerRuntime;
import org.eclipse.virgo.ide.runtime.internal.core.runtimes.InstallationType;
import org.eclipse.virgo.ide.runtime.internal.core.runtimes.RuntimeProviders;
import org.eclipse.virgo.ide.runtime.internal.core.runtimes.VirgoRuntimeProvider;
import org.eclipse.virgo.kernel.repository.BundleRepository;
import org.eclipse.virgo.repository.Repository;
import org.eclipse.virgo.util.osgi.manifest.BundleManifest;
import org.eclipse.virgo.util.osgi.manifest.ExportPackage;
import org.eclipse.virgo.util.osgi.manifest.ExportedPackage;
import org.eclipse.wst.server.core.IRuntime;
import org.osgi.framework.Version;

/**
 * Utility class that is able to create {@link Repository} instances from either the remote enterprise bundle repository
 * or from a local installed Virgo instance.
 *
 * @author Christian Dupuis
 * @author Leo Dos Santos
 * @since 1.0.0
 */
public class RepositoryUtils {

    public static boolean doesRuntimeSupportRepositories(IRuntime runtime) {
        IServerRuntimeProvider provider = RuntimeProviders.getRuntimeProvider(runtime);
        if (provider instanceof VirgoRuntimeProvider) {
            VirgoRuntimeProvider virgoProvider = (VirgoRuntimeProvider) provider;
            InstallationType install = virgoProvider.getInstallationType(runtime);
            if (InstallationType.NANO.equals(install)) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a BundleDefinition for the given bundle symbolic name and version
     */
    public static org.eclipse.virgo.kernel.repository.BundleDefinition getBundleDefinition(final String bundle, final String version,
        IProject project) {
        final List<org.eclipse.virgo.kernel.repository.BundleDefinition> bundles = new ArrayList<org.eclipse.virgo.kernel.repository.BundleDefinition>();

        ServerRuntimeUtils.execute(project, new ServerRuntimeUtils.ServerRuntimeCallback() {

            public boolean doWithRuntime(VirgoServerRuntime runtime) {
                try {
                    BundleRepository bundleRepository = ServerCorePlugin.getArtefactRepositoryManager().getBundleRepository(runtime.getRuntime());
                    org.eclipse.virgo.kernel.repository.BundleDefinition bundleDefinition = bundleRepository.findBySymbolicName(bundle,
                        new org.eclipse.virgo.util.osgi.manifest.VersionRange(version));
                    if (bundleDefinition != null) {
                        bundles.add(bundleDefinition);
                        return false;
                    }
                } catch (Exception e) {

                }
                return true;
            }
        });

        if (bundles.size() > 0) {
            return bundles.get(0);
        }
        return null;
    }

    /**
     * Returns {@link BundleArtefact}s which bundle symbolic name matches the given <code>value</code>.
     */
    public static Set<Artefact> getImportBundleProposals(final IProject project, final String value) {
        final Set<Artefact> packages = new TreeSet<Artefact>(new Comparator<Artefact>() {

            public int compare(Artefact o1, Artefact o2) {
                if (o1.getName() != null && o1.getName().equals(o2.getName())) {
                    return o1.getVersion().compareTo(o2.getVersion());
                } else if (o1.getName() != null && o2.getName() != null) {
                    return o1.getName().compareTo(o2.getName());
                } else if (o1.getSymbolicName() != null && o1.getSymbolicName().equals(o2.getSymbolicName())) {
                    return o1.getVersion().compareTo(o2.getVersion());
                } else if (o1.getSymbolicName() != null) {
                    return o1.getSymbolicName().compareTo(o2.getSymbolicName());
                }
                return 0;
            }
        });

        ServerRuntimeUtils.execute(project, new ServerRuntimeUtils.ServerRuntimeCallback() {

            public boolean doWithRuntime(VirgoServerRuntime runtime) {
                packages.addAll(getImportBundleProposalsForRuntime(runtime, project, value));
                return true;
            }
        });

        return packages;

    }

    /**
     * Returns {@link LibraryArtefact}s which library symbolic name matches the given <code>value</code>.
     */
    public static Set<Artefact> getImportLibraryProposals(final IProject project, final String value) {
        final Set<Artefact> packages = new TreeSet<Artefact>(new Comparator<Artefact>() {

            public int compare(Artefact o1, Artefact o2) {
                if (o1.getName() != null && o1.getName().equals(o2.getName())) {
                    return o1.getVersion().compareTo(o2.getVersion());
                } else if (o1.getName() != null) {
                    return o1.getName().compareTo(o2.getName());
                }
                return 0;
            }
        });

        ServerRuntimeUtils.execute(project, new ServerRuntimeUtils.ServerRuntimeCallback() {

            public boolean doWithRuntime(VirgoServerRuntime runtime) {
                packages.addAll(getImportLibraryProposalsForRuntime(runtime, project, value));
                return true;
            }
        });

        return packages;

    }

    /**
     * Returns {@link org.eclipse.virgo.ide.bundlerepository.domain.PackageExport}s which name matches the given
     * <code>value</code>.
     */
    public static Set<org.eclipse.virgo.ide.bundlerepository.domain.PackageExport> getImportPackageProposals(final IProject project,
        final String value) {
        final Set<org.eclipse.virgo.ide.bundlerepository.domain.PackageExport> packages = new TreeSet<org.eclipse.virgo.ide.bundlerepository.domain.PackageExport>(
            new Comparator<org.eclipse.virgo.ide.bundlerepository.domain.PackageExport>() {

                public int compare(org.eclipse.virgo.ide.bundlerepository.domain.PackageExport o1,
                    org.eclipse.virgo.ide.bundlerepository.domain.PackageExport o2) {
                    if (o1.getName().equals(o2.getName())) {
                        return o1.getVersion().compareTo(o2.getVersion());
                    }
                    return o1.getName().compareTo(o2.getName());
                }
            });

        ServerRuntimeUtils.execute(project, new ServerRuntimeUtils.ServerRuntimeCallback() {

            public boolean doWithRuntime(VirgoServerRuntime runtime) {
                packages.addAll(getImportPackageProposalsForRuntime(runtime, project, value));
                return true;
            }
        });

        return packages;

    }

    /**
     * Creates a {@link Repository} inventory from the given runtime.
     */
    public static ArtefactRepository getRepositoryContents(IRuntime runtime) {
        ArtefactRepository artifacts = new ArtefactRepository();
        if (doesRuntimeSupportRepositories(runtime)) {
            BundleRepository bundleRepository = ServerCorePlugin.getArtefactRepositoryManager().getBundleRepository(runtime);

            for (org.eclipse.virgo.kernel.repository.BundleDefinition bundleDefinition : bundleRepository.getBundles()) {
                if (bundleDefinition.getManifest() != null && bundleDefinition.getManifest().getBundleSymbolicName() != null
                    && bundleDefinition.getManifest().getBundleSymbolicName().getSymbolicName() != null) {
                    BundleManifest manifest = bundleDefinition.getManifest();
                    boolean sourcefileExists = ServerUtils.getSourceFile(bundleDefinition.getLocation()) != null
                        && ServerUtils.getSourceFile(bundleDefinition.getLocation()).exists();
                    OsgiVersion version = null;
                    if (manifest.getBundleVersion() != null) {
                        version = new OsgiVersion(manifest.getBundleVersion());
                    }
                    artifacts.addBundle(new LocalBundleArtefact(manifest.getBundleName(), manifest.getBundleSymbolicName().getSymbolicName(), version,
                        sourcefileExists, bundleDefinition.getLocation()));
                }

            }
            for (org.eclipse.virgo.kernel.repository.LibraryDefinition libraryDefinition : bundleRepository.getLibraries()) {
                if (libraryDefinition.getSymbolicName() != null) {
                    artifacts.addLibrary(new LocalLibraryArtefact(libraryDefinition.getName(), libraryDefinition.getSymbolicName(),
                        new OsgiVersion(libraryDefinition.getVersion()), libraryDefinition.getLocation()));
                }
            }
        }
        return artifacts;
    }

    /**
     * Returns proposals for version content assist requests. 2.5.4 -> 2.5.4 [2.5.4,2.6.0) [2.5.4,2.5.4] [2.5.4,3.0.0)
     */
    public static List<String> getVersionProposals(Set<String> versionStrings) {
        // First order the version so that the highest appears first
        List<Version> versions = new ArrayList<Version>();
        for (String versionString : versionStrings) {
            versions.add(Version.parseVersion(versionString));
        }
        Collections.sort(versions);
        Collections.reverse(versions);

        // Now add version ranges to the version
        List<String> versionRanges = new ArrayList<String>();
        for (Version version : versions) {
            // 2.5.4 - removed as we don't want to make people use the
            // completion
            // versionRanges.add(version.toString());

            // [2.5.4,2.6.0)
            versionRanges.add(new VersionRange(version, true, new Version(version.getMajor(), version.getMinor() + 1, 0, null), false).toString());

            // [2.5.4,2.5.4]
            versionRanges.add(new VersionRange(version, true, version, true).toString());

            // [2.5.4,3.0.0)
            versionRanges.add(new VersionRange(version, true, new Version(version.getMajor() + 1, 0, 0, null), false).toString());

        }

        return versionRanges;
    }

    /**
     * Searches the remote bundle repository for matches of a given search string.
     */
    public static ArtefactRepository searchForArtifacts(final String search) {
        return searchForArtifacts(search, true, true);
    }

    /**
     * Searches the remote bundle repository for matches of a given search string.
     */
    public static ArtefactRepository searchForArtifacts(final String search, boolean includeBundles, boolean includesLibraries) {
        StringMatcher matcher = new StringMatcher("*" + search + "*", true, false); //$NON-NLS-1$//$NON-NLS-2$

        ArtefactRepository repository = new ArtefactRepository();
        if (includeBundles) {
            for (IArtefactTyped artefact : ServerCorePlugin.getArtefactRepositoryManager().getArtefactRepository().getBundles()) {
                BundleArtefact bundle = (BundleArtefact) artefact;
                // check symbolic name and name
                if (matcher.match(bundle.getSymbolicName()) || matcher.match(bundle.getName())) {
                    repository.addBundle(bundle);
                }
                // check export packages
                else {
                    for (org.eclipse.virgo.ide.bundlerepository.domain.PackageExport pe : bundle.getExports()) {
                        if (matcher.match(pe.getName())) {
                            repository.addBundle(bundle);
                            break;
                        }
                        for (PackageMember pm : pe.getExports()) {
                            if (matcher.match(pm.getName())) {
                                repository.addBundle(bundle);
                                break;
                            }
                        }
                    }
                }
            }
        }
        if (includesLibraries) {
            for (IArtefact library : ServerCorePlugin.getArtefactRepositoryManager().getArtefactRepository().getLibraries()) {
                // check symbolic name and name
                if (matcher.match(library.getSymbolicName()) || matcher.match(library.getName())) {
                    repository.addLibrary((LibraryArtefact) library);
                }
            }
        }
        return repository;
    }

    private static void addImportBundleProposalsFromManifest(Set<BundleArtefact> bundles, BundleManifest manifest, String value) {
        if (manifest != null && manifest.getBundleSymbolicName() != null && manifest.getBundleSymbolicName().getSymbolicName() != null
            && manifest.getBundleSymbolicName().getSymbolicName().startsWith(value)) {
            OsgiVersion version = null;
            if (manifest.getBundleVersion() != null) {
                version = new OsgiVersion(manifest.getBundleVersion());
            }
            LocalBundleArtefact bundleArtefact = new LocalBundleArtefact(manifest.getBundleName(), manifest.getBundleSymbolicName().getSymbolicName(),
                version, false, null);
            ExportPackage exports = manifest.getExportPackage();
            for (ExportedPackage export : exports.getExportedPackages()) {
                bundleArtefact.getExports().add(new PackageExport(bundleArtefact, export.getPackageName(), new OsgiVersion(export.getVersion())));
            }

            bundles.add(bundleArtefact);
        }
    }

    private static void addImportPackageProposalsFromManifest(String value, Set<org.eclipse.virgo.ide.bundlerepository.domain.PackageExport> packages,
        BundleManifest manifest) {
        if (manifest != null && manifest.getExportPackage() != null) {
            for (ExportedPackage export : manifest.getExportPackage().getExportedPackages()) {
                Object version = export.getAttributes().get("version");
                String packageName = export.getPackageName();
                if (packageName.startsWith(value)) {
                    OsgiVersion v = null;
                    if (version != null) {
                        v = new OsgiVersion(version.toString());
                    } else {
                        v = new OsgiVersion(Version.emptyVersion);
                    }
                    packages.add(new org.eclipse.virgo.ide.bundlerepository.domain.PackageExport(null, packageName, v));
                }
            }
        }
    }

    private static String getCategory(IArtefact artefact) {
        if (artefact.getOrganisationName() != null
            && (artefact.getOrganisationName().startsWith("com.springsource") || artefact.getOrganisationName().startsWith("org.springframework"))) {
            return "/release";
        } else {
            return "/external";
        }
    }

    private static Set<BundleArtefact> getImportBundleProposalsForRuntime(VirgoServerRuntime runtime, IProject project, String value) {
        if (value == null) {
            value = "";
        }
        Set<BundleArtefact> bundles = new HashSet<BundleArtefact>();

        BundleRepository bundleRepository = ServerCorePlugin.getArtefactRepositoryManager().getBundleRepository(runtime.getRuntime());

        for (org.eclipse.virgo.kernel.repository.BundleDefinition definition : bundleRepository.getBundles()) {
            if (definition.getManifest() != null) {
                BundleManifest manifest = definition.getManifest();
                addImportBundleProposalsFromManifest(bundles, manifest, value);
            }
        }

        // Add workspace bundles
        for (IProject p : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
            if (FacetUtils.isBundleProject(p) && !p.equals(project)) {
                BundleManifest manifest = BundleManifestCorePlugin.getBundleManifestManager().getBundleManifest(JavaCore.create(p));
                addImportBundleProposalsFromManifest(bundles, manifest, value);
            }
        }

        return bundles;
    }

    private static Set<LibraryArtefact> getImportLibraryProposalsForRuntime(VirgoServerRuntime runtime, IProject project, String value) {
        if (value == null) {
            value = "";
        }
        Set<LibraryArtefact> libraries = new HashSet<LibraryArtefact>();

        BundleRepository bundleRepository = ServerCorePlugin.getArtefactRepositoryManager().getBundleRepository(runtime.getRuntime());

        for (org.eclipse.virgo.kernel.repository.LibraryDefinition definition : bundleRepository.getLibraries()) {
            if (definition.getSymbolicName() != null && definition.getSymbolicName().startsWith(value)) {
                libraries.add(new LibraryArtefact(definition.getName(), definition.getSymbolicName(), new OsgiVersion(definition.getVersion()),
                    definition.getSymbolicName(), definition.getSymbolicName()));
            }
        }
        return libraries;
    }

    private static Set<org.eclipse.virgo.ide.bundlerepository.domain.PackageExport> getImportPackageProposalsForRuntime(VirgoServerRuntime runtime,
        IProject project, String value) {
        if (value == null) {
            value = "";
        }
        Set<org.eclipse.virgo.ide.bundlerepository.domain.PackageExport> packages = new HashSet<org.eclipse.virgo.ide.bundlerepository.domain.PackageExport>();

        BundleRepository bundleRepository = ServerCorePlugin.getArtefactRepositoryManager().getBundleRepository(runtime.getRuntime());

        for (org.eclipse.virgo.kernel.repository.BundleDefinition definition : bundleRepository.getBundles()) {
            if (definition.getManifest() != null) {
                BundleManifest manifest = definition.getManifest();
                addImportPackageProposalsFromManifest(value, packages, manifest);
            }
        }

        // Add workspace bundles
        for (IProject p : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
            if (FacetUtils.isBundleProject(p) && !p.equals(project)) {
                BundleManifest manifest = BundleManifestCorePlugin.getBundleManifestManager().getBundleManifest(JavaCore.create(p));
                addImportPackageProposalsFromManifest(value, packages, manifest);
            }
        }

        return packages;
    }

}
