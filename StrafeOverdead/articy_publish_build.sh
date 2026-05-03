#!/bin/bash

# Configuration
RUNTIME_PATH="/Users/mihaistoica/REPOSITORIES/Articy-Java-Runtime"
GAME_PATH="/Users/mihaistoica/REPOSITORIES/Strafe-Overdead/StrafeOverdead"

echo "--- Step 1: Publishing Articy Java Runtime to Maven Local ---"
cd "$RUNTIME_PATH" || exit
./gradlew clean publishToMavenLocal

if [ $? -eq 0 ]; then
    echo -e "\n--- Step 2: Refreshing dependencies in Strafe Overdead ---"
    cd "$GAME_PATH" || exit
    # We refresh dependencies and build the desktop module which is the primary dev target
    ./gradlew desktop:build --refresh-dependencies
else
    echo -e "\nERROR: Failed to publish Articy Java Runtime. Aborting."
    exit 1
fi

echo -e "\nDone! Articy Runtime is now up to date in the game project."
