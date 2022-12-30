#!/bin/sh
ECLIPSE_EXE=/Applications/Eclipse.app/Contents/MacOS/eclipse

SCRIPT_DIR=$( cd -- "$( dirname -- "$0" )" &> /dev/null && pwd )

$ECLIPSE_EXE -nosplash -application org.eclipse.equinox.p2.publisher.FeaturesAndBundlesPublisher \
    -metadataRepository file:/$SCRIPT_DIR/repo \
    -artifactRepository file:/$SCRIPT_DIR/repo \
    -source $SCRIPT_DIR/source/ -publishArtifacts
