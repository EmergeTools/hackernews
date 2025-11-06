#!/bin/bash
set -e

VERSION="2.58.0"
REPO="https://github.com/getsentry/sentry-cli"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CLI_DIR="$SCRIPT_DIR/../.sentry"
CLI_PATH="$CLI_DIR/sentry-cli"

# Create directory if it doesn't exist
mkdir -p "$CLI_DIR"

# Detect platform
case "$(uname -s)" in
    Darwin*)
        PLATFORM="Darwin-universal"
        ;;
    Linux*)
        case "$(uname -m)" in
            x86_64)
                PLATFORM="Linux-x86_64"
                ;;
            aarch64)
                PLATFORM="Linux-aarch64"
                ;;
            i686)
                PLATFORM="Linux-i686"
                ;;
            *)
                echo "Unsupported Linux architecture: $(uname -m)"
                exit 1
                ;;
        esac
        ;;
    MINGW*|MSYS*|CYGWIN*)
        PLATFORM="Windows-x86_64.exe"
        CLI_PATH="$CLI_PATH.exe"
        ;;
    *)
        echo "Unsupported operating system: $(uname -s)"
        exit 1
        ;;
esac

# Check if already downloaded
if [ -f "$CLI_PATH" ]; then
    CURRENT_VERSION=$("$CLI_PATH" --version 2>/dev/null | grep -oE '[0-9]+\.[0-9]+\.[0-9]+' | head -1 || echo "unknown")
    if [ "$CURRENT_VERSION" = "$VERSION" ]; then
        echo "sentry-cli $VERSION already downloaded at $CLI_PATH"
        exit 0
    else
        echo "Existing version ($CURRENT_VERSION) differs from target ($VERSION), re-downloading..."
        rm -f "$CLI_PATH"
    fi
fi

# Download sentry-cli
DOWNLOAD_URL="$REPO/releases/download/$VERSION/sentry-cli-$PLATFORM"
echo "Downloading sentry-cli $VERSION from $DOWNLOAD_URL"
curl -L -o "$CLI_PATH" "$DOWNLOAD_URL"

# Make executable
chmod +x "$CLI_PATH"

# Verify
DOWNLOADED_VERSION=$("$CLI_PATH" --version 2>/dev/null | grep -oE '[0-9]+\.[0-9]+\.[0-9]+' | head -1)
if [ "$DOWNLOADED_VERSION" = "$VERSION" ]; then
    echo "Successfully downloaded sentry-cli $VERSION to $CLI_PATH"
else
    echo "ERROR: Downloaded version ($DOWNLOADED_VERSION) does not match expected version ($VERSION)"
    exit 1
fi
