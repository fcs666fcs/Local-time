#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")" && pwd)"
GRADLE_VERSION="9.4.1"
CACHE="${HOME}/.gradle/localtime-wrapper/${GRADLE_VERSION}"
DIST="$CACHE/gradle-${GRADLE_VERSION}/bin/gradle"
if [ ! -x "$DIST" ]; then
  mkdir -p "$CACHE"
  tmp="$CACHE/gradle.zip"
  curl -fsSL "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" -o "$tmp"
  unzip -q "$tmp" -d "$CACHE"
  rm -f "$tmp"
fi
exec "$DIST" "$@"
