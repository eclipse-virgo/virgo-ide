This bundle is deployed into the running Virgo Server 
by the Virgo Tools and acts as a JMX Bridge that allows
the Virgo Tools to provide several functionalities
in the Virgo Server Editor (list of services and packages,
dependency graphs, shell access).

It used to be built and included as a Jar inside the 
runtime.core bundle, which would later deploy it,
see IServerRuntimeProvider.getConnectorBundleUri()
There were 2 Jars, the Jar resulting from this bundle
and a legacy Jar for Virgo Server <= 3.0, both embedded
as a resource in runtime.core.

As of 2022, I am dropping support for this functionality
for Virgo Server <=3.0. The user can still use the Virgo
Web Console to obtain the same information.

Now, the legacy Jar is gone, and instead of embedding this 
bundle as Jar into runtime.core, runtime.core will locate it 
at runtime inside Eclipse and deploy it into Virgo. 
For this to work the binary Jar must be there.
The Jar will always exist when the binary Virgo Tools are installed 
in Eclipse but won't be there if the committer of the Virgo Tools is
running a runtime workbench. In such case the Virgo Tools will
find out and will open a pop-up to tell the committer to
perform a mvn build of this bundle (so that the Jar is produced
by maven) before testing any related functionality.

Important attention points:
- Several imports in MANIFEST.MF are optional as they are only needed at runtime
  in Virgo, not in Eclipse
- the execution environment of this bundle and the Java level
  must remain the same as Virgo Server or lower, otherwise
  the Jar won't be resolved in Virgo at runtime.
- Virgo still ships with a very dated version of Felix Gogo Shell
  and Eclipse 2022 and later includes a newer version of the same.
  One of the methods that the JMX bridge uses to execute shell commands
  in Virgo has a different signature in the newer version 
  (different return type) and the invocation will fail if this bundle
  is built against the newer Gogo Shell. For this reason it is important
  to ensure that the MANIFEST.MF depends on the right version, the same
  that is included in Virgo Server. Also note that the Gogo Shell class
  is located in a Java package that is exported with directive "status=provisional"
  Originally the MANIFEST.MF was importing the package with the same
  directive, otherwise wiring would not work, but I found out that
  in Tycho 3.0 this is not supported and at compile time bundle resolution
  fails and compiler bails out. So I changed from importing the package
  to importing the Gogo bundle, to avoid the resolution issue.
  
  