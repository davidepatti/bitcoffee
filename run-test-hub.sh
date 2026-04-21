#!/usr/bin/env sh
set -eu

PROJECT_ROOT=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
BUILD_DIR="$PROJECT_ROOT/out/testhub"

if ! command -v javac >/dev/null 2>&1; then
    echo "Error: javac not found in PATH." >&2
    exit 1
fi

if ! command -v java >/dev/null 2>&1; then
    echo "Error: java not found in PATH." >&2
    exit 1
fi

mkdir -p "$BUILD_DIR"

if [ -t 1 ] && [ -z "${NO_COLOR:-}" ] && [ -z "${BITCOFFEE_FORCE_COLOR:-}" ]; then
    case "${TERM:-}" in
        ""|dumb)
            ;;
        *)
            export BITCOFFEE_FORCE_COLOR=1
            ;;
    esac
fi

echo "Compiling Java sources..."
javac -d "$BUILD_DIR" $(find "$PROJECT_ROOT/src" -name '*.java' | sort)

echo "Launching Test Hub..."
exec java -cp "$BUILD_DIR" Tests.TestHub
