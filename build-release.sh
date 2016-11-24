#!/bin/sh

set -e

echo "Building $(git describe --tags)..."
./gradlew --daemon clean assembleRelease

/opt/android-sdk/build-tools/24.0.3/zipalign -f -v -p 4 build/outputs/apk/hanze-schedule-release-unsigned.apk build/outputs/apk/hanze-schedule-release.apk
/opt/android-sdk/build-tools/24.0.3/apksigner sign --ks hanze-schedule-release-key.jks build/outputs/apk/hanze-schedule-release.apk

cp build/outputs/apk/hanze-schedule-release.apk .

echo "done"
echo
git describe --tags
git log $(git describe --tags --abbrev=0)..HEAD --format="- %s"
