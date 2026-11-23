#!/usr/bin/env sh
# Runs standalone Detekt CLI + Compose Rules (io.nlopez.compose.rules:detekt)
# with SHA-256 verification and SARIF report export.
set -eu

DETEKT_VERSION="1.23.8"
DETEKT_SHA256="2ce2ff952e150baf28a29cda70a363b0340b3e81a55f43e51ec5edffc3d066c1"
COMPOSE_RULES_VERSION="0.4.28"
COMPOSE_RULES_SHA256="b8c732880e9d26fac90f44733ecd9dab099e2abb1f597b76ceab29e676d4ddd6"

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
REPO_ROOT=$(CDPATH= cd -- "$SCRIPT_DIR/.." && pwd)

CACHE_DIR="${DETEKT_CACHE_DIR:-$HOME/.cache/droidquest-detekt}"
REPORT_DIR="${DETEKT_REPORT_DIR:-$REPO_ROOT/build/reports/detekt}"
DETEKT_JAR="$CACHE_DIR/detekt-cli-${DETEKT_VERSION}-all.jar"
COMPOSE_RULES_JAR="$CACHE_DIR/detekt-compose-${COMPOSE_RULES_VERSION}-all.jar"

mkdir -p "$CACHE_DIR" "$REPORT_DIR"

verify_sha256() {
    expected="$1"
    file="$2"
    if command -v sha256sum >/dev/null 2>&1; then
        echo "$expected  $file" | sha256sum --status -c -
    elif command -v shasum >/dev/null 2>&1; then
        echo "$expected  $file" | shasum -a 256 --status -c -
    else
        echo "ERROR: Neither sha256sum nor shasum is available." >&2
        exit 1
    fi
}

download_verified() {
    url="$1"
    dest="$2"
    expected_sha="$3"

    if [ -f "$dest" ] && verify_sha256 "$expected_sha" "$dest" 2>/dev/null; then
        return 0
    fi

    echo "Downloading $(basename "$dest")..."
    tmp_file="${dest}.tmp.$$"
    curl -fsSL -o "$tmp_file" "$url"
    if ! verify_sha256 "$expected_sha" "$tmp_file"; then
        rm -f "$tmp_file"
        echo "ERROR: SHA-256 mismatch for $url" >&2
        exit 1
    fi
    mv "$tmp_file" "$dest"
}

download_verified \
    "https://github.com/detekt/detekt/releases/download/v${DETEKT_VERSION}/detekt-cli-${DETEKT_VERSION}-all.jar" \
    "$DETEKT_JAR" \
    "$DETEKT_SHA256"

download_verified \
    "https://github.com/mrmans0n/compose-rules/releases/download/v${COMPOSE_RULES_VERSION}/detekt-compose-${COMPOSE_RULES_VERSION}-all.jar" \
    "$COMPOSE_RULES_JAR" \
    "$COMPOSE_RULES_SHA256"

JAVA_BIN="${JAVA_BIN:-java}"
if ! command -v "$JAVA_BIN" >/dev/null 2>&1; then
    if [ -n "${JAVA_HOME:-}" ] && [ -x "$JAVA_HOME/bin/java" ]; then
        JAVA_BIN="$JAVA_HOME/bin/java"
    else
        echo "ERROR: Java runtime not found. Install JDK 17+ or set JAVA_HOME." >&2
        exit 1
    fi
fi

cd "$REPO_ROOT"
exec "$JAVA_BIN" -jar "$DETEKT_JAR" \
    --build-upon-default-config \
    --config "$REPO_ROOT/config/detekt/detekt.yml" \
    --plugins "$COMPOSE_RULES_JAR" \
    --input "androidApp,iosApp,shared,webApp" \
    --excludes "**/build/**" \
    --base-path "$REPO_ROOT" \
    --report "sarif:$REPORT_DIR/detekt.sarif" \
    --report "md:$REPORT_DIR/detekt.md" \
    "$@"
