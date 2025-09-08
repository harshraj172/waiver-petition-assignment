#!/bin/bash

# Set classpath
CP="build/classes/java/main:build/classes/java/test"
CP="$CP:$(find ~/.gradle/caches/modules-2/files-2.1/junit/junit/4.13.2 -name "*.jar" | head -1)"
CP="$CP:$(find ~/.gradle/caches/modules-2/files-2.1/org.hamcrest/hamcrest-core/1.3 -name "*.jar" | head -1)"

# Download PIT if not present
if [ ! -f pitest-command-line.jar ]; then
    wget https://repo1.maven.org/maven2/org/pitest/pitest-command-line/1.15.0/pitest-command-line-1.15.0-all.jar -O pitest-command-line.jar
fi

# Run PIT directly
java -cp pitest-command-line.jar:$CP \
    org.pitest.mutationtest.commandline.MutationCoverageReport \
    --reportDir build/reports/pitest \
    --targetClasses "expression.*,intervals.*" \
    --targetTests "*Test*" \
    --sourceDirs src \
    --threads 4 \
    --mutators DEFAULTS \
    --outputFormats HTML
