#!/bin/bash
# Build script for Echoes of the Void
# Requires: JDK 17+

set -e

echo "========================================="
echo "  Echoes of the Void — Build Script"
echo "========================================="
echo ""

# Check Java
if ! command -v java &> /dev/null; then
    echo "ERROR: Java is not installed or not in PATH."
    echo "Please install JDK 17+ and try again."
    echo "  Ubuntu/Debian: sudo apt install openjdk-17-jdk"
    echo "  macOS:         brew install openjdk@17"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "ERROR: Java $JAVA_VERSION detected, but Java 17+ is required."
    exit 1
fi
echo "✓ Java $JAVA_VERSION detected"

# Generate Gradle wrapper if not present
if [ ! -f "gradle/wrapper/gradle-wrapper.jar" ]; then
    echo "→ Generating Gradle wrapper..."
    if command -v gradle &> /dev/null; then
        gradle wrapper --gradle-version 8.3
        echo "✓ Gradle wrapper generated"
    else
        echo "ERROR: gradle-wrapper.jar not found and 'gradle' command not available."
        echo "Please either:"
        echo "  1. Install Gradle and run: gradle wrapper --gradle-version 8.3"
        echo "  2. Download gradle-wrapper.jar manually into gradle/wrapper/"
        exit 1
    fi
fi

# Build
echo "→ Building mod..."
./gradlew build

echo ""
echo "========================================="
echo "  BUILD COMPLETE!"
echo "========================================="
echo ""
echo "JAR file: build/libs/echoes-of-the-void-1.0.0.jar"
echo ""
echo "To install:"
echo "  1. Install Fabric Loader 1.20.1"
echo "  2. Install Fabric API mod"
echo "  3. Copy the JAR to .minecraft/mods/"
echo ""
echo "The darkness awaits."
